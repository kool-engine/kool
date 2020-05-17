package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.pxSize
import de.fabmax.kool.platform.webgl.LoadedTextureWebGl
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.WebGLTexture

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenRenderPassCube) {
    actual val texture: CubeMapTexture = OffscreenTextureCube()

    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        texture as OffscreenTextureCube
        texture.create(ctx)

        for (i in 0 until offscreenPass.mipLevels) {
            val fbo = gl.createFramebuffer()
            val rbo = gl.createRenderbuffer()

            gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, fbo)
            gl.bindRenderbuffer(WebGLRenderingContext.RENDERBUFFER, rbo)
            gl.renderbufferStorage(WebGLRenderingContext.RENDERBUFFER, WebGL2RenderingContext.DEPTH_COMPONENT24, offscreenPass.texWidth shr i, offscreenPass.texHeight shr i)
            gl.framebufferRenderbuffer(WebGLRenderingContext.FRAMEBUFFER, WebGLRenderingContext.DEPTH_ATTACHMENT, WebGLRenderingContext.RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo
        }

        isCreated = true
    }

    actual fun dispose(ctx: KoolContext) {
        ctx as JsContext
        fbos.forEach { ctx.gl.deleteFramebuffer(it) }
        rbos.forEach { ctx.gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()
        texture.dispose()
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
        texture as OffscreenTextureCube

        val mipLevel = offscreenPass.targetMipLevel
        val width = offscreenPass.mipWidth(mipLevel)
        val height = offscreenPass.mipHeight(mipLevel)
        val fboIdx = if (mipLevel < 0) 0 else mipLevel

        offscreenPass.viewport = KoolContext.Viewport(0, 0, width, height)
        ctx.gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, fbos[fboIdx])

        for (i in 0 until 6) {
            val view = VIEWS[i]
            val queue = offscreenPass.drawQueues[view.index]
            ctx.gl.framebufferTexture2D(WebGLRenderingContext.FRAMEBUFFER, WebGLRenderingContext.COLOR_ATTACHMENT0, WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_X + i, texture.offscreenTex, fboIdx)
            ctx.queueRenderer.renderQueue(queue)
        }

        ctx.gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, null)
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

    private inner class OffscreenTextureCube : CubeMapTexture(
            TextureProps(addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE),
            loader = null) {

        var offscreenTex: WebGLTexture? = null

        fun create(ctx: JsContext) {
            val gl = ctx.gl

            val intFormat = offscreenPass.colorFormat.glInternalFormat
            val width = offscreenPass.texWidth
            val height = offscreenPass.texHeight

            offscreenTex = gl.createTexture()
            gl.bindTexture(WebGLRenderingContext.TEXTURE_CUBE_MAP, offscreenTex)
            gl.texStorage2D(WebGLRenderingContext.TEXTURE_CUBE_MAP, offscreenPass.mipLevels, intFormat, width, height)

            gl.texParameteri(WebGLRenderingContext.TEXTURE_CUBE_MAP, WebGLRenderingContext.TEXTURE_WRAP_S, WebGLRenderingContext.CLAMP_TO_EDGE)
            gl.texParameteri(WebGLRenderingContext.TEXTURE_CUBE_MAP, WebGLRenderingContext.TEXTURE_WRAP_T, WebGLRenderingContext.CLAMP_TO_EDGE)
            gl.texParameteri(WebGLRenderingContext.TEXTURE_CUBE_MAP, WebGL2RenderingContext.TEXTURE_WRAP_R, WebGLRenderingContext.CLAMP_TO_EDGE)
            gl.texParameteri(WebGLRenderingContext.TEXTURE_CUBE_MAP, WebGLRenderingContext.TEXTURE_MIN_FILTER, WebGLRenderingContext.LINEAR_MIPMAP_LINEAR)
            gl.texParameteri(WebGLRenderingContext.TEXTURE_CUBE_MAP, WebGLRenderingContext.TEXTURE_MAG_FILTER, WebGLRenderingContext.LINEAR)

            val estSize = estimatedTexSize(width, height, offscreenPass.colorFormat.pxSize, 6, offscreenPass.mipLevels)
            loadedTexture = LoadedTextureWebGl(ctx, offscreenTex, estSize)
            loadingState = LoadingState.LOADED
        }
    }
}