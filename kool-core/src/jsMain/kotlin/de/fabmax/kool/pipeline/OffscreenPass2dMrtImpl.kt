package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.WebGL2RenderingContext.Companion.DEPTH_COMPONENT24
import de.fabmax.kool.platform.glInternalFormat
import de.fabmax.kool.platform.pxSize
import de.fabmax.kool.platform.webgl.LoadedTextureWebGl
import org.khronos.webgl.WebGLFramebuffer
import org.khronos.webgl.WebGLRenderingContext.Companion.COLOR_ATTACHMENT0
import org.khronos.webgl.WebGLRenderingContext.Companion.DEPTH_ATTACHMENT
import org.khronos.webgl.WebGLRenderingContext.Companion.FRAMEBUFFER
import org.khronos.webgl.WebGLRenderingContext.Companion.TEXTURE_2D
import org.khronos.webgl.WebGLTexture

actual class OffscreenPass2dMrtImpl actual constructor(val offscreenPass: OffscreenRenderPass2dMrt) {
    actual val colorTextures = List(offscreenPass.nAttachments) { Texture(loader = null) }
    actual val depthTexture = Texture(loader = null)

    private var isCreated = false

    private var fbo: WebGLFramebuffer? = null
    private val glColorTexs = Array<WebGLTexture?>(offscreenPass.nAttachments) { null }
    private var glDepthTex: WebGLTexture? = null

    actual fun dispose(ctx: KoolContext) {
        ctx as JsContext
        ctx.gl.deleteFramebuffer(fbo)
        fbo = null
        colorTextures.forEach { it.dispose() }
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
        for (i in glColorTexs.indices) {
            val colorFormat = offscreenPass.texFormats[i]
            val intFormat = colorFormat.glInternalFormat
            val width = offscreenPass.texWidth
            val height = offscreenPass.texHeight
            val samplerProps = TextureProps(
                    addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                    minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                    mipMapping = offscreenPass.mipLevels > 1, maxAnisotropy = 1)

            val estSize = Texture.estimatedTexSize(width, height, colorFormat.pxSize, 1, offscreenPass.mipLevels)
            val tex = LoadedTextureWebGl(ctx, TEXTURE_2D, ctx.gl.createTexture(), estSize)
            tex.setSize(width, height)
            tex.applySamplerProps(samplerProps)
            ctx.gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

            glColorTexs[i] = tex.texture
            colorTextures[i].loadedTexture = tex
            colorTextures[i].loadingState = Texture.LoadingState.LOADED
        }
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
        // do not set compare mode: if compare mode is set depth texture cannot be sampled with regular 2d sampler anymore
        //ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_MODE, COMPARE_REF_TO_TEXTURE)
        //ctx.gl.texParameteri(TEXTURE_2D, TEXTURE_COMPARE_FUNC, LESS)
        ctx.gl.texStorage2D(TEXTURE_2D, offscreenPass.mipLevels, intFormat, width, height)

        glDepthTex = tex.texture
        depthTexture.loadedTexture = tex
        depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}