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
import org.khronos.webgl.WebGLRenderingContext.Companion.CLAMP_TO_EDGE
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.LESS
import org.khronos.webgl.WebGLRenderingContext.Companion.LINEAR
import org.khronos.webgl.WebGLRenderingContext.Companion.NEAREST
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MAG_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_MIN_FILTER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_S
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_WRAP_T
import org.khronos.webgl.WebGLTexture

actual class OffscreenPass2dMrtImpl actual constructor(val offscreenPass: OffscreenRenderPass2dMrt) {
    actual val textures = List(offscreenPass.nAttachments) { Texture(loader = null) }
    actual val depthTexture = Texture(loader = null)

    private var isCreated = false

    private var fbo: WebGLFramebuffer? = null
    private val glColorTexs = Array<WebGLTexture?>(offscreenPass.nAttachments) { null }
    private var glDepthTex: WebGLTexture? = null

    actual fun dispose(ctx: KoolContext) {
        ctx as JsContext
        ctx.gl.deleteFramebuffer(fbo)
        fbo = null
        textures.forEach { it.dispose() }
        depthTexture.dispose()
        for (i in glColorTexs.indices) { glColorTexs[i] = null }
        glDepthTex = null
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

        ctx.gl.bindFramebuffer(FRAMEBUFFER, fbo)
        ctx.queueRenderer.renderQueue(offscreenPass.drawQueue)
        ctx.gl.bindFramebuffer(FRAMEBUFFER, null)
    }

    private fun create(ctx: JsContext) {
        createColorTex(ctx)
        createDepthTex(ctx)

        val gl = ctx.gl
        fbo = gl.createFramebuffer()
        gl.bindFramebuffer(FRAMEBUFFER, fbo)
        for (i in glColorTexs.indices) {
            gl.framebufferTexture2D(FRAMEBUFFER, COLOR_ATTACHMENT0 + i, TEXTURE_2D, glColorTexs[i], 0)
        }
        gl.framebufferTexture2D(FRAMEBUFFER, DEPTH_ATTACHMENT, TEXTURE_2D, glDepthTex, 0)

        val attachments = IntArray(glColorTexs.size) { COLOR_ATTACHMENT0 + it }
        gl.drawBuffers(attachments)

        isCreated = true
    }

    private fun createColorTex(ctx: JsContext) {
        val gl = ctx.gl
        for (i in glColorTexs.indices) {
            val colorFormat = offscreenPass.texFormats[i]
            val intFormat = colorFormat.glInternalFormat
            val width = offscreenPass.texWidth
            val height = offscreenPass.texHeight

            glColorTexs[i] = gl.createTexture()
            gl.bindTexture(TEXTURE_2D, glColorTexs[i])
            gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, NEAREST)
            gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, NEAREST)

            val estSize = Texture.estimatedTexSize(width, height, colorFormat.pxSize, 1, offscreenPass.mipLevels)
            textures[i].loadedTexture = LoadedTextureWebGl(ctx, glColorTexs[i], estSize)
            textures[i].loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun createDepthTex(ctx: JsContext) {
        val gl = ctx.gl
        val intFormat = DEPTH_COMPONENT24
        val width = offscreenPass.texWidth
        val height = offscreenPass.texHeight

        glDepthTex = gl.createTexture()
        gl.bindTexture(TEXTURE_2D, glDepthTex)
        gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_S, CLAMP_TO_EDGE)
        gl.texParameteri(TEXTURE_2D, TEXTURE_WRAP_T, CLAMP_TO_EDGE)
        gl.texParameteri(TEXTURE_2D, TEXTURE_MIN_FILTER, LINEAR)
        gl.texParameteri(TEXTURE_2D, TEXTURE_MAG_FILTER, LINEAR)
        gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_MODE, COMPARE_REF_TO_TEXTURE)
        gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_FUNC, LESS)

        val estSize = Texture.estimatedTexSize(width, height, 4, 1, offscreenPass.mipLevels)
        depthTexture.loadedTexture = LoadedTextureWebGl(ctx, glDepthTex, estSize)
        depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}