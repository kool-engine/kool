package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.COMPARE_REF_TO_TEXTURE
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_COMPARE_FUNC
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_COMPARE_MODE
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.glOp
import de.fabmax.kool.platform.pxSize
import de.fabmax.kool.platform.webgl.LoadedTextureWebGl
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLTexture

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenRenderPass2d) {
    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private val drawMipLevels = offscreenPass.config.drawMipLevels
    private val renderMipLevels: Int = if (drawMipLevels) { offscreenPass.config.mipLevels } else { 1 }

    private var isCreated = false
    private var colorTexs = Array<WebGLTexture?>(offscreenPass.config.nColorAttachments) { null }
    private var depthTex: WebGLTexture? = null

    fun draw(ctx: JsContext) {
        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            for (mipLevel in 0 until renderMipLevels) {
                offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
                offscreenPass.applyMipViewport(mipLevel)
                ctx.gl.bindFramebuffer(FRAMEBUFFER, fbos[mipLevel])
                ctx.queueRenderer.renderQueue(offscreenPass.drawQueue)
            }
            if (!drawMipLevels) {
                for (i in colorTexs.indices) {
                    ctx.gl.bindTexture(TEXTURE_2D, colorTexs[i])
                    ctx.gl.generateMipmap(TEXTURE_2D)
                }
            }
            copyToTextures(ctx)
            ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
        }
    }

    private fun copyToTextures(ctx: JsContext) {
        for (mipLevel in 0 until renderMipLevels) {
            ctx.gl.bindFramebuffer(FRAMEBUFFER, fbos[mipLevel])
            ctx.gl.readBuffer(COLOR_ATTACHMENT0)
            for (i in offscreenPass.copyTargetsColor.indices) {
                val copyTarget = offscreenPass.copyTargetsColor[i]
                var width = copyTarget.loadedTexture?.width ?: 0
                var height = copyTarget.loadedTexture?.height ?: 0
                if (width != offscreenPass.width || height != offscreenPass.height) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                    width = copyTarget.loadedTexture!!.width
                    height = copyTarget.loadedTexture!!.height
                }
                width = width shr mipLevel
                height = height shr mipLevel
                val target = copyTarget.loadedTexture as LoadedTextureWebGl
                ctx.gl.bindTexture(TEXTURE_2D, target.texture)
                ctx.gl.copyTexSubImage2D(TEXTURE_2D, mipLevel, 0, 0, 0, 0, width, height)
            }
        }
        if (!drawMipLevels) {
            for (i in offscreenPass.copyTargetsColor.indices) {
                val copyTarget = offscreenPass.copyTargetsColor[i]
                val target = copyTarget.loadedTexture as LoadedTextureWebGl
                ctx.gl.bindTexture(TEXTURE_2D, target.texture)
                ctx.gl.generateMipmap(TEXTURE_2D)
            }
        }
    }

    actual fun dispose(ctx: KoolContext) {
        ctx as JsContext
        fbos.forEach { ctx.gl.deleteFramebuffer(it) }
        rbos.forEach { ctx.gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        offscreenPass.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        offscreenPass.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        for (i in colorTexs.indices) { colorTexs[i] = null }
        depthTex = null
        isCreated = false
    }

    actual fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx as JsContext)
    }

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        if (offscreenPass.config.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createColorTexs(ctx)
        }
        if (offscreenPass.config.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createDepthTex(ctx)
        }

        for (i in 0 until offscreenPass.config.mipLevels) {
            val mipWidth = offscreenPass.getMipWidth(i)
            val mipHeight = offscreenPass.getMipHeight(i)
            val fbo = gl.createFramebuffer()
            gl.bindFramebuffer(FRAMEBUFFER, fbo)

            if (offscreenPass.config.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                colorTexs.forEachIndexed { iAttachment, tex ->
                    gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0 + iAttachment, TEXTURE_2D, tex, i)
                }
                val attachments = IntArray(colorTexs.size) { COLOR_ATTACHMENT0 + it }
                gl.drawBuffers(attachments)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(RENDERBUFFER, rbo)
                gl.renderbufferStorage(RENDERBUFFER, TexFormat.R.glInternalFormat, mipWidth, mipHeight)
                gl.framebufferRenderbuffer(FRAMEBUFFER, COLOR_ATTACHMENT0, RENDERBUFFER, rbo)
                rbos += rbo
            }

            if (offscreenPass.config.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                gl.framebufferTexture2D(FRAMEBUFFER, DEPTH_ATTACHMENT, TEXTURE_2D, depthTex, i)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(RENDERBUFFER, rbo)
                gl.renderbufferStorage(RENDERBUFFER, DEPTH_COMPONENT24, mipWidth, mipHeight)
                gl.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, rbo)
                rbos += rbo
            }
            fbos += fbo
        }
        isCreated = true
    }

    private fun createColorTexs(ctx: JsContext) {
        for (i in offscreenPass.colorTextures.indices) {
            val cfg = offscreenPass.config.colorAttachments[i]

            if (cfg.providedTexture != null) {
                colorTexs[i] = (cfg.providedTexture.loadedTexture as LoadedTextureWebGl).texture

            } else {
                val format = cfg.colorFormat
                val intFormat = format.glInternalFormat
                val width = offscreenPass.width
                val height = offscreenPass.height
                val mipLevels = offscreenPass.config.mipLevels

                val estSize = Texture.estimatedTexSize(width, height, format.pxSize, 1, mipLevels)
                val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
                tex.setSize(width, height)
                tex.applySamplerProps(offscreenPass.colorTextures[i].props)
                ctx.gl.texStorage2D(TEXTURE_2D, mipLevels, intFormat, width, height)

                colorTexs[i] = tex.texture
                offscreenPass.colorTextures[i].loadedTexture = tex
                offscreenPass.colorTextures[i].loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun createDepthTex(ctx: JsContext) {
        val cfg = offscreenPass.config.depthAttachment!!

        if (cfg.providedTexture != null) {
            depthTex = (cfg.providedTexture.loadedTexture as LoadedTextureWebGl).texture

        } else {
            val intFormat = DEPTH_COMPONENT24
            val width = offscreenPass.width
            val height = offscreenPass.height
            val mipLevels = offscreenPass.config.mipLevels
            val depthCfg = offscreenPass.config.depthAttachment

            val estSize = Texture.estimatedTexSize(width, height, 4, 1, mipLevels)
            val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
            tex.setSize(width, height)
            tex.applySamplerProps(offscreenPass.depthTexture!!.props)
            if (depthCfg.depthCompareOp != DepthCompareOp.DISABLED) {
                ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_MODE, COMPARE_REF_TO_TEXTURE)
                ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_FUNC, depthCfg.depthCompareOp.glOp)
            }
            ctx.gl.texStorage2D(TEXTURE_2D, mipLevels, intFormat, width, height)

            depthTex = tex.texture
            offscreenPass.depthTexture.loadedTexture = tex
            offscreenPass.depthTexture.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture.createCopyTexColor(ctx: JsContext) {
        val intFormat = props.format.glInternalFormat
        val width = offscreenPass.width
        val height = offscreenPass.height
        val mipLevels = offscreenPass.config.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, props.format.pxSize, 1, mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(props)
        ctx.gl.texStorage2D(TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}

