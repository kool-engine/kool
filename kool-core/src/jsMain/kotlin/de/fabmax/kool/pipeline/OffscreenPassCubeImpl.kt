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
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLTexture

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenRenderPassCube) {
    actual val texture = CubeMapTexture(
            "offscreen_cube_tex",
            TextureProps(
                    addressModeU = AddressMode.CLAMP_TO_EDGE,
                    addressModeV = AddressMode.CLAMP_TO_EDGE,
                    addressModeW = AddressMode.CLAMP_TO_EDGE,
                    minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR,
                    mipMapping = offscreenPass.mipLevels > 1, maxAnisotropy = 1),
            loader = null)

    private val fbos = mutableListOf<WebGLFramebuffer?>()
    private val rbos = mutableListOf<WebGLRenderbuffer?>()

    private var isCreated = false
    private var offscreenTex: WebGLTexture? = null

    private fun create(ctx: JsContext) {
        val gl = ctx.gl

        createColorTex(ctx)

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

        val mipLevel = offscreenPass.targetMipLevel
        val fboIdx = if (mipLevel < 0) 0 else mipLevel

        offscreenPass.setMipViewport(mipLevel)
        ctx.gl.bindFramebuffer(WebGLRenderingContext.FRAMEBUFFER, fbos[fboIdx])

        for (i in 0 until 6) {
            val view = VIEWS[i]
            val queue = offscreenPass.drawQueues[view.index]
            ctx.gl.framebufferTexture2D(WebGLRenderingContext.FRAMEBUFFER, WebGLRenderingContext.COLOR_ATTACHMENT0, WebGLRenderingContext.TEXTURE_CUBE_MAP_POSITIVE_X + i, offscreenTex, fboIdx)
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

    fun createColorTex(ctx: JsContext) {
        val intFormat = offscreenPass.colorFormat.glInternalFormat
        val width = offscreenPass.texWidth
        val height = offscreenPass.texHeight

        val estSize = Texture.estimatedTexSize(width, height, offscreenPass.colorFormat.pxSize, 6, offscreenPass.mipLevels)
        val tex = LoadedTextureWebGl(ctx, TEXTURE_CUBE_MAP, ctx.gl.createTexture(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(texture.props)
        ctx.gl.texStorage2D(TEXTURE_CUBE_MAP, offscreenPass.mipLevels, intFormat, width, height)

        offscreenTex = tex.texture
        texture.loadedTexture = tex
        texture.loadingState = Texture.LoadingState.LOADED
    }
}