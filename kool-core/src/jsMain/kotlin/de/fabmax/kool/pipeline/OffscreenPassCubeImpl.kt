package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.pxSize
import de.fabmax.kool.platform.webgl.LoadedTextureWebGl
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLTexture

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenRenderPassCube) {
    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false
    private var colorTex: WebGLTexture? = null

    fun draw(ctx: JsContext) {
        if (!isCreated) {
            create(ctx)
        }

        for (mipLevel in 0 until offscreenPass.config.mipLevels) {
            offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
            offscreenPass.applyMipViewport(mipLevel)
            ctx.gl.bindFramebuffer(FRAMEBUFFER, fbos[mipLevel])

            for (i in 0 until 6) {
                val view = VIEWS[i]
                val queue = offscreenPass.drawQueues[view.index]
                ctx.gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_CUBE_MAP_POSITIVE_X + i, colorTex, mipLevel)
                ctx.queueRenderer.renderQueue(queue)
            }
        }
        ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
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

        colorTex = null
        isCreated = false
    }

    actual fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx as JsContext)
    }

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        createColorTex(ctx)

        for (i in 0 until offscreenPass.config.mipLevels) {
            val fbo = gl.createFramebuffer()
            val rbo = gl.createRenderbuffer()

            val mipWidth = offscreenPass.getMipWidth(i)
            val mipHeight = offscreenPass.getMipHeight(i)

            gl.bindFramebuffer(FRAMEBUFFER, fbo)
            gl.bindRenderbuffer(RENDERBUFFER, rbo)
            gl.renderbufferStorage(RENDERBUFFER, DEPTH_COMPONENT24, mipWidth, mipHeight)
            gl.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo
        }

        isCreated = true
    }

    private fun createColorTex(ctx: JsContext) {
        val format = offscreenPass.config.colorAttachments[0].colorFormat
        val intFormat = format.glInternalFormat
        val width = offscreenPass.width
        val height = offscreenPass.height
        val mipLevels = offscreenPass.config.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, format.pxSize, 6, mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_CUBE_MAP, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(offscreenPass.colorTexture!!.props)
        ctx.gl.texStorage2D(TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)

        colorTex = tex.texture
        offscreenPass.colorTexture!!.loadedTexture = tex
        offscreenPass.colorTexture!!.loadingState = Texture.LoadingState.LOADED
    }

    companion object {
        private val VIEWS = Array(6) { i ->
            when (i) {
                0 -> OffscreenRenderPassCube.ViewDirection.RIGHT
                1 -> OffscreenRenderPassCube.ViewDirection.LEFT
                2 -> OffscreenRenderPassCube.ViewDirection.UP
                3 -> OffscreenRenderPassCube.ViewDirection.DOWN
                4 -> OffscreenRenderPassCube.ViewDirection.FRONT
                5 -> OffscreenRenderPassCube.ViewDirection.BACK
                else -> throw IllegalStateException()
            }
        }
    }
}