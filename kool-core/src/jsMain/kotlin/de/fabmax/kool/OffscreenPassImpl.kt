package de.fabmax.kool

import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.RGBA16F
import de.fabmax.kool.gl.WebGL2RenderingContext.Companion.TEXTURE_WRAP_R
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.LoadedTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.JsContext
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderbuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_BUFFER_BIT
import org.khronos.webgl.WebGLRenderingContext.Companion.FLOAT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.RENDERBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.RGBA
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_CUBE_MAP_POSITIVE_X
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MAG_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MIN_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_S
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_T
import org.khronos.webgl.WebGLTexture

actual class OffscreenPass2dImpl actual constructor(val offscreenPass: OffscreenPass2d) {
    actual val texture: Texture
        get() = TODO()
}

actual class OffscreenPassCubeImpl actual constructor(val offscreenPass: OffscreenPassCube) {
    actual val texture: CubeMapTexture = OffscreenTextureCube()

    private var fbo: WebGLFramebuffer? = null
    private var rbo: WebGLRenderbuffer? = null

    private var isSetup = false

    private fun setup(ctx: JsContext) {
        val gl = ctx.gl

        texture as OffscreenTextureCube

        fbo = gl.createFramebuffer()
        rbo = gl.createRenderbuffer()

        gl.bindFramebuffer(FRAMEBUFFER, fbo)

        gl.bindRenderbuffer(RENDERBUFFER, rbo)
        gl.renderbufferStorage(RENDERBUFFER, DEPTH_COMPONENT24, offscreenPass.texWidth, offscreenPass.texHeight)
        gl.framebufferRenderbuffer(FRAMEBUFFER, DEPTH_ATTACHMENT, RENDERBUFFER, rbo)

        texture.create(ctx)
        isSetup = true
    }

    fun draw(ctx: JsContext) {
        if (!isSetup) {
            setup(ctx)
        }
        texture as OffscreenTextureCube

        ctx.gl.bindFramebuffer(FRAMEBUFFER, fbo)

        ctx.gl.viewport(0, 0, offscreenPass.texWidth, offscreenPass.texHeight)
        for (i in 0 until 6) {
            ctx.gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0, TEXTURE_CUBE_MAP_POSITIVE_X + i, texture.offscreenTex, 0)
            ctx.gl.clear(COLOR_BUFFER_BIT or DEPTH_BUFFER_BIT)

            val view = VIEWS[i]
            val queue = offscreenPass.drawQueues[view.index]
            ctx.queueRenderer.renderQueue(queue)
        }

        ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
    }

    companion object {
        private val VIEWS = Array(6) { i ->
            when (i) {
                0 -> OffscreenPassCube.ViewDirection.RIGHT
                1 -> OffscreenPassCube.ViewDirection.LEFT
                2 -> OffscreenPassCube.ViewDirection.UP
                3 -> OffscreenPassCube.ViewDirection.DOWN
                4 -> OffscreenPassCube.ViewDirection.FRONT
                5 -> OffscreenPassCube.ViewDirection.BACK
                else -> throw IllegalStateException()
            }
        }
    }

    private inner class OffscreenTextureCube : CubeMapTexture(loader = null) {
        var offscreenTex: WebGLTexture? = null

        fun create(ctx: JsContext) {
            val gl = ctx.gl

            offscreenTex = gl.createTexture()
            gl.bindTexture(TEXTURE_CUBE_MAP, offscreenTex)
            for (i in 0 until 6) {
                gl.texImage2D(TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, RGBA16F,
                        offscreenPass.texWidth, offscreenPass.texHeight, 0, RGBA, FLOAT, null)
//                gl.texImage2D(TEXTURE_CUBE_MAP_POSITIVE_X + i, 0, RGB,
//                        offscreenPass.texWidth, offscreenPass.texHeight, 0, RGB, UNSIGNED_BYTE, null)
            }
            gl.texParameteri(TEXTURE_CUBE_MAP, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_CUBE_MAP, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_CUBE_MAP, TEXTURE_WRAP_R, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_CUBE_MAP, TEXTURE_MIN_FILTER, LINEAR)
            gl.texParameteri(TEXTURE_CUBE_MAP, TEXTURE_MAG_FILTER, LINEAR)

            loadedTexture = LoadedTexture(offscreenTex)
            loadingState = LoadingState.LOADED
        }
    }
}