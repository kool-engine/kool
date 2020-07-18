package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D

class OffscreenPass2dMrtGl(val parentPass: OffscreenPass2dMrtImpl) : OffscreenPass2dMrtImpl.BackendImpl {

    private var isCreated = false

    private var fbo = 0
    private val glColorTexs = IntArray(parentPass.offscreenPass.nAttachments)
    private var glDepthTex = 0

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
        }

        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        val glBackend = ctx.renderBackend as GlRenderBackend
        glBackend.queueRenderer.renderQueue(parentPass.offscreenPass.drawQueue)
        glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE)
    }

    override fun dispose(ctx: Lwjgl3Context) {
        if (fbo != 0) {
            glDeleteFramebuffers(fbo)
        }
        fbo = 0
        parentPass.colorTextures.forEach { it.dispose() }
        parentPass.depthTexture.dispose()
        for (i in glColorTexs.indices) { glColorTexs[i] = 0 }
        glDepthTex = 0
        isCreated = false
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: Lwjgl3Context) {
        createColorTex(ctx)
        createDepthTex(ctx)

        fbo = glGenFramebuffers()
        glBindFramebuffer(GL_FRAMEBUFFER, fbo)
        for (i in glColorTexs.indices) {
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + i, GL_TEXTURE_2D, glColorTexs[i], 0)
        }
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glDepthTex, 0)

        val attachments = IntArray(glColorTexs.size) { GL_COLOR_ATTACHMENT0 + it }
        glDrawBuffers(attachments)

        isCreated = true
    }

    private fun createColorTex(ctx: Lwjgl3Context) {
        for (i in glColorTexs.indices) {
            val colorFormat = parentPass.offscreenPass.texFormats[i]
            val intFormat = colorFormat.glInternalFormat
            val width = parentPass.offscreenPass.texWidth
            val height = parentPass.offscreenPass.texHeight
            val samplerProps = TextureProps(
                    addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                    minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                    mipMapping = parentPass.offscreenPass.mipLevels > 1, maxAnisotropy = 1)

            val estSize = Texture.estimatedTexSize(width, height, colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
            val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
            tex.setSize(width, height)
            tex.applySamplerProps(samplerProps)
            glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

            glColorTexs[i] = tex.texture
            parentPass.colorTextures[i].loadedTexture = tex
            parentPass.colorTextures[i].loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun createDepthTex(ctx: Lwjgl3Context) {
        val intFormat = GL_DEPTH_COMPONENT24
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight
        val samplerProps = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.NEAREST, magFilter = FilterMethod.NEAREST,
                mipMapping = parentPass.offscreenPass.mipLevels > 1, maxAnisotropy = 1)

        val estSize = Texture.estimatedTexSize(width, height, 4, 1, parentPass.offscreenPass.mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(samplerProps)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glDepthTex = tex.texture
        parentPass.depthTexture.loadedTexture = tex
        parentPass.depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}