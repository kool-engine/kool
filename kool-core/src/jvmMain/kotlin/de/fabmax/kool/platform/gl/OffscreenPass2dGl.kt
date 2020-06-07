package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D

class OffscreenPass2dGl(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private val fbos = mutableListOf<Int>()
    private val rbos = mutableListOf<Int>()

    private var isCreated = false

    private var glColorTex = 0
    private var glDepthTex = 0

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            val mipLevel = parentPass.offscreenPass.targetMipLevel
            val fboIdx = if (mipLevel < 0) 0 else mipLevel

            glBindFramebuffer(GL_FRAMEBUFFER, fbos[fboIdx])
            val glBackend = ctx.renderBackend as GlRenderBackend
            glBackend.queueRenderer.renderQueue(parentPass.offscreenPass.drawQueue)
            glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE)
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        fbos.forEach { glDeleteFramebuffers(it) }
        rbos.forEach { glDeleteRenderbuffers(it) }
        fbos.clear()
        rbos.clear()

        if (!parentPass.isExtColorTexture) {
            parentPass.colorTexture.dispose()
        }
        if (!parentPass.isExtDepthTexture) {
            parentPass.depthTexture.dispose()
        }
        glColorTex = 0
        glDepthTex = 0
        isCreated = false
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if (!parentPass.isExtColorTexture && parentPass.colorTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                createColorTex(ctx)
            } else if (parentPass.isExtColorTexture) {
                val extColorTex = parentPass.colorTexture.loadedTexture ?: return
                glColorTex = (extColorTex as LoadedTextureGl).texture
            }
        }
        if (parentPass.offscreenPass.setup.depthRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
            if (!parentPass.isExtDepthTexture && parentPass.depthTexture.loadingState == Texture.LoadingState.NOT_LOADED) {
                createDepthTex(ctx)
            } else if (parentPass.isExtDepthTexture) {
                val extDepthTex = parentPass.depthTexture.loadedTexture ?: return
                glDepthTex = (extDepthTex as LoadedTextureGl).texture
            }
        }

        for (i in 0 until parentPass.offscreenPass.mipLevels) {
            val fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            if (parentPass.offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, glColorTex, i)
            } else {
                val rbo = glGenRenderbuffers()
                glBindRenderbuffer(GL_RENDERBUFFER, rbo)
                glRenderbufferStorage(GL_RENDERBUFFER, parentPass.offscreenPass.colorFormat.glInternalFormat,
                        parentPass.offscreenPass.texWidth shr i, parentPass.offscreenPass.texHeight shr i)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rbo)
                rbos += rbo
            }

            if (parentPass.offscreenPass.setup.depthRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glDepthTex, i)
            } else {
                val rbo = glGenRenderbuffers()
                glBindRenderbuffer(GL_RENDERBUFFER, rbo)
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24,
                        parentPass.offscreenPass.texWidth shr i, parentPass.offscreenPass.texHeight shr i)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo)
                rbos += rbo
            }

            fbos += fbo
        }
        isCreated = true
    }

    private fun createColorTex(ctx: Lwjgl3Context) {
        val intFormat = parentPass.offscreenPass.colorFormat.glInternalFormat
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight

        glColorTex = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, glColorTex)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        val estSize = Texture.estimatedTexSize(width, height, parentPass.offscreenPass.colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
        parentPass.colorTexture.loadedTexture = LoadedTextureGl(ctx, glColorTex, estSize)
        parentPass.colorTexture.loadingState = Texture.LoadingState.LOADED
    }

    private fun createDepthTex(ctx: Lwjgl3Context) {
        val intFormat = GL_DEPTH_COMPONENT24
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight

        glDepthTex = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, glDepthTex)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LESS)

        val estSize = Texture.estimatedTexSize(width, height, 4, 1, parentPass.offscreenPass.mipLevels)
        parentPass.depthTexture.loadedTexture = LoadedTextureGl(ctx, glDepthTex, estSize)
        parentPass.depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}