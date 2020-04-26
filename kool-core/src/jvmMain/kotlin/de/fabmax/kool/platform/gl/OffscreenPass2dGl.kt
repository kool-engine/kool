package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D

class OffscreenPass2dGl(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private var fbos: List<Int> = mutableListOf()

    private var isCreated = false

    private var glColorTex = 0
    private var glDepthTex = 0

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
            isCreated = true
        }

        val mipLevel = parentPass.offscreenPass.targetMipLevel
        val width = parentPass.offscreenPass.mipWidth(mipLevel)
        val height = parentPass.offscreenPass.mipHeight(mipLevel)
        val fboIdx = if (mipLevel < 0) 0 else mipLevel

        glBindFramebuffer(GL_FRAMEBUFFER, fbos[fboIdx])
        val glBackend = ctx.renderBackend as GlRenderBackend
        glBackend.queueRenderer.renderQueue(parentPass.offscreenPass.drawQueue)
        glBindFramebuffer(GL_FRAMEBUFFER, GL11.GL_NONE)
    }

    override fun dispose(ctx: Lwjgl3Context) {
        fbos.forEach { glDeleteFramebuffers(it) }
        parentPass.texture.dispose()
        parentPass.depthTexture.dispose()
    }

    private fun create(ctx: Lwjgl3Context) {
        createColorTex(ctx)
        createDepthTex(ctx)

        for (i in 0 until parentPass.offscreenPass.mipLevels) {
            val fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glColorTex, i)
            glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glDepthTex, i)
            fbos += fbo
        }
    }

    private fun createColorTex(ctx: Lwjgl3Context) {
        val intFormat = parentPass.offscreenPass.colorFormat.glInternalFormat
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight

        glColorTex = GL11.glGenTextures()
        glBindTexture(GL_TEXTURE_2D, glColorTex)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val estSize = Texture.estimatedTexSize(width, height, parentPass.offscreenPass.colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
        parentPass.texture.loadedTexture = LoadedTextureGl(ctx, glColorTex, estSize)
        parentPass.texture.loadingState = Texture.LoadingState.LOADED
    }

    private fun createDepthTex(ctx: Lwjgl3Context) {
        val intFormat = GL_DEPTH_COMPONENT24
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight

        glDepthTex = GL11.glGenTextures()
        glBindTexture(GL_TEXTURE_2D, glDepthTex)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LESS)

        val estSize = Texture.estimatedTexSize(width, height, parentPass.offscreenPass.colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
        parentPass.depthTexture.loadedTexture = LoadedTextureGl(ctx, glDepthTex, estSize)
        parentPass.depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}