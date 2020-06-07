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
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR_MIPMAP_LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MAG_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MIN_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_S
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_T
import org.khronos.webgl.WebGLTexture

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenRenderPass2d) {
    actual val colorTexture: Texture = offscreenPass.setup.extColorTexture ?: OffscreenTexture()
    actual val depthTexture: Texture = offscreenPass.setup.extDepthTexture ?: OffscreenDepthTexture()

    val isExtColorTexture = offscreenPass.setup.extColorTexture != null
    val isExtDepthTexture = offscreenPass.setup.extDepthTexture != null

    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false
    private var colorTex: WebGLTexture? = null
    private var depthTex: WebGLTexture? = null

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        if (offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if(!isExtColorTexture && colorTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                (colorTexture as OffscreenTexture).create(ctx)
            } else if (isExtColorTexture) {
                val extColorTex = colorTexture.loadedTexture ?: return
                colorTex = (extColorTex as LoadedTextureWebGl).texture
            }
        }
        if (offscreenPass.setup.depthRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if (!isExtDepthTexture && depthTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                (depthTexture as OffscreenDepthTexture).create(ctx)
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

    fun draw(ctx: JsContext) {
        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            val mipLevel = offscreenPass.targetMipLevel
            val fboIdx = if (mipLevel < 0) 0 else mipLevel

            ctx.gl.bindFramebuffer(FRAMEBUFFER, fbos[fboIdx])
            ctx.queueRenderer.renderQueue(offscreenPass.drawQueue)
            ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
        }
    }

    private inner class OffscreenTexture : Texture(loader = null) {
        fun create(ctx: JsContext) {
            val gl = ctx.gl

            val intFormat = offscreenPass.colorFormat.glInternalFormat
            val width = offscreenPass.texWidth
            val height = offscreenPass.texHeight

            colorTex = gl.createTexture()
            gl.bindTexture(TEXTURE_2D, colorTex)
            gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR_MIPMAP_LINEAR)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)

            val estSize = estimatedTexSize(width, height, offscreenPass.colorFormat.pxSize, 1, offscreenPass.mipLevels)
            loadedTexture = LoadedTextureWebGl(ctx, colorTex, estSize)
            loadingState = LoadingState.LOADED
        }
    }

    private inner class OffscreenDepthTexture : Texture(loader = null) {
        fun create(ctx: JsContext) {
            val gl = ctx.gl

            val intFormat = DEPTH_COMPONENT24
            val width = offscreenPass.texWidth
            val height = offscreenPass.texHeight

            depthTex = gl.createTexture()
            gl.bindTexture(TEXTURE_2D, depthTex)
            gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)
            gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_MODE, COMPARE_REF_TO_TEXTURE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_FUNC, LESS)

            val estSize = estimatedTexSize(width, height, 4, 1, offscreenPass.mipLevels)
            loadedTexture = LoadedTextureWebGl(ctx, depthTex, estSize)
            loadingState = LoadingState.LOADED
        }
    }
}

