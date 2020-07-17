package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.COMPARE_REF_TO_TEXTURE
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_COMPARE_FUNC
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.TEXTURE_COMPARE_MODE
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.pxSize
import de.fabmax.kool.platform.webgl.LoadedTextureWebGl
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLTexture
import kotlin.math.max

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenRenderPass2d) {
    actual val colorTexture: Texture = offscreenPass.setup.extColorTexture ?: Texture(loader = null)
    actual val depthTexture: Texture = offscreenPass.setup.extDepthTexture ?: Texture(loader = null)

    val isExtColorTexture = offscreenPass.setup.extColorTexture != null
    val isExtDepthTexture = offscreenPass.setup.extDepthTexture != null

    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false
    private var colorTex: WebGLTexture? = null
    private var depthTex: WebGLTexture? = null

    fun draw(ctx: JsContext) {
        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            val mipLevel = max(0, offscreenPass.targetMipLevel)

            ctx.gl.bindFramebuffer(FRAMEBUFFER, fbos[mipLevel])
            ctx.queueRenderer.renderQueue(offscreenPass.drawQueue)
            copyToTextures(mipLevel, ctx)
            ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
        }
    }

    private fun copyToTextures(mipLevel: Int, ctx: JsContext) {
        ctx.gl.readBuffer(COLOR_ATTACHMENT0)

        for (i in offscreenPass.copyTargetsColor.indices) {
            val copyTarget = offscreenPass.copyTargetsColor[i]
            val width = copyTarget.loadedTexture?.width ?: 0
            val height = copyTarget.loadedTexture?.height ?: 0
            if (width != offscreenPass.texWidth || height != offscreenPass.texHeight) {
                copyTarget.loadedTexture?.dispose()
                copyTarget.createCopyTexColor(ctx)
            }
            val target = copyTarget.loadedTexture as LoadedTextureWebGl
            ctx.gl.bindTexture(TEXTURE_2D, target.texture)
            ctx.gl.copyTexSubImage2D(TEXTURE_2D, mipLevel, 0, 0, 0, 0, width, height)
        }
    }

    actual fun dispose(ctx: KoolContext) {
        ctx as JsContext
        fbos.forEach { ctx.gl.deleteFramebuffer(it) }
        rbos.forEach { ctx.gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        if (!isExtColorTexture) {
            colorTexture.dispose()
        }
        if (!isExtDepthTexture) {
            depthTexture.dispose()
        }
        colorTex = null
        depthTex = null
        isCreated = false
    }

    actual fun resize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx as JsContext)
    }

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        if (offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if(!isExtColorTexture && colorTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                createColorTex(ctx)
            } else if (isExtColorTexture) {
                val extColorTex = colorTexture.loadedTexture ?: return
                colorTex = (extColorTex as LoadedTextureWebGl).texture
            }
        }
        if (offscreenPass.setup.depthRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if (!isExtDepthTexture && depthTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                createDepthTex(ctx)
            } else if (isExtDepthTexture) {
                val extDepthTex = depthTexture.loadedTexture ?: return
                depthTex = (extDepthTex as LoadedTextureWebGl).texture
            }
        }

        for (i in 0 until offscreenPass.mipLevels) {
            val fbo = gl.createFramebuffer()
            fbos += fbo
            gl.bindFramebuffer(FRAMEBUFFER, fbo)

            if (offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
                gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_2D, colorTex, i)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(RENDERBUFFER, rbo)
                gl.renderbufferStorage(RENDERBUFFER, offscreenPass.colorFormat.glInternalFormat,
                        offscreenPass.texWidth shr i, offscreenPass.texHeight shr i)
                gl.framebufferRenderbuffer(FRAMEBUFFER, COLOR_ATTACHMENT0, RENDERBUFFER, rbo)
                rbos += rbo
            }

            if (offscreenPass.setup.depthRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
                gl.framebufferTexture2D(FRAMEBUFFER, DEPTH_ATTACHMENT, TEXTURE_2D, depthTex, i)
            } else {
                val rbo = gl.createRenderbuffer()
                gl.bindRenderbuffer(RENDERBUFFER, rbo)
                gl.renderbufferStorage(RENDERBUFFER, DEPTH_COMPONENT24,
                        offscreenPass.texWidth shr i, offscreenPass.texHeight shr i)
                gl.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, rbo)
                rbos += rbo
            }
        }

        isCreated = true
    }

    private fun Texture.createCopyTexColor(ctx: JsContext) {
        val intFormat = props.format.glInternalFormat
        val width = offscreenPass.texWidth
        val height = offscreenPass.texHeight

        val estSize = Texture.estimatedTexSize(width, height, offscreenPass.colorFormat.pxSize, 1, offscreenPass.mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(props)
        ctx.gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    private fun createColorTex(ctx: JsContext) {
        val intFormat = offscreenPass.colorFormat.glInternalFormat
        val width = offscreenPass.texWidth
        val height = offscreenPass.texHeight
        val samplerProps = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR,
                mipMapping = offscreenPass.mipLevels > 1, maxAnisotropy = 1)

        val estSize = Texture.estimatedTexSize(width, height, offscreenPass.colorFormat.pxSize, 1, offscreenPass.mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(samplerProps)
        ctx.gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

        colorTex = tex.texture
        colorTexture.loadedTexture = tex
        colorTexture.loadingState = Texture.LoadingState.LOADED
    }

    private fun createDepthTex(ctx: JsContext) {
        val intFormat = DEPTH_COMPONENT24
        val width = offscreenPass.texWidth
        val height = offscreenPass.texHeight
        val samplerProps = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR,
                mipMapping = offscreenPass.mipLevels > 1, maxAnisotropy = 1)

        val estSize = Texture.estimatedTexSize(width, height, 4, 1, offscreenPass.mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(samplerProps)
        ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_MODE, COMPARE_REF_TO_TEXTURE)
        ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_FUNC, LESS)
        ctx.gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

        depthTex = tex.texture
        depthTexture.loadedTexture = tex
        depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}

