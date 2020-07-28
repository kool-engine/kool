package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D
import org.lwjgl.opengl.GL43.glCopyImageSubData

class OffscreenPass2dGl(val parentPass: OffscreenPass2dImpl) : OffscreenPass2dImpl.BackendImpl {
    private val fbos = mutableListOf<Int>()
    private val rbos = mutableListOf<Int>()

    private val drawMipLevels = parentPass.offscreenPass.config.drawMipLevels
    private val renderMipLevels: Int = if (drawMipLevels) { parentPass.offscreenPass.config.mipLevels } else { 1 }

    private var isCreated = false
    private val glColorTexs = IntArray(parentPass.offscreenPass.config.nColorAttachments)
    private var glDepthTex = 0

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            val glBackend = ctx.renderBackend as GlRenderBackend
            for (mipLevel in 0 until renderMipLevels) {
                parentPass.offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
                parentPass.offscreenPass.applyMipViewport(mipLevel)
                glBindFramebuffer(GL_FRAMEBUFFER, fbos[mipLevel])
                glBackend.queueRenderer.renderQueue(parentPass.offscreenPass.drawQueue)
            }
            glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE)

            if (!drawMipLevels) {
                for (i in glColorTexs.indices) {
                    glBindTexture(GL_TEXTURE_2D, glColorTexs[i])
                    glGenerateMipmap(GL_TEXTURE_2D)
                }
            }
            copyToTextures(ctx)
        }
    }

    private fun copyToTextures(ctx: Lwjgl3Context) {
        for (mipLevel in 0 until parentPass.offscreenPass.config.mipLevels) {
            for (i in parentPass.offscreenPass.copyTargetsColor.indices) {
                val copyTarget = parentPass.offscreenPass.copyTargetsColor[i]
                var width = copyTarget.loadedTexture?.width ?: 0
                var height = copyTarget.loadedTexture?.height ?: 0
                if (width != parentPass.offscreenPass.width || height != parentPass.offscreenPass.height) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                    width = copyTarget.loadedTexture!!.width
                    height = copyTarget.loadedTexture!!.height
                }
                width = width shr mipLevel
                height = height shr mipLevel
                val target = copyTarget.loadedTexture as LoadedTextureGl

                if (parentPass.offscreenPass.config.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                    glCopyImageSubData(glColorTexs[0], GL_TEXTURE_2D, mipLevel, 0, 0, 0,
                            target.texture, GL_TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                } else {
                    glCopyImageSubData(rbos[mipLevel], GL_RENDERBUFFER, 0, 0, 0, 0,
                            target.texture, GL_TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                }
            }
        }
    }

    override fun dispose(ctx: Lwjgl3Context) {
        fbos.forEach { glDeleteFramebuffers(it) }
        rbos.forEach { glDeleteRenderbuffers(it) }
        fbos.clear()
        rbos.clear()

        parentPass.offscreenPass.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        parentPass.offscreenPass.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        for (i in glColorTexs.indices) { glColorTexs[i] = 0 }
        glDepthTex = 0
        isCreated = false
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.config.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createColorTexs(ctx)
        }
        if (parentPass.offscreenPass.config.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createDepthTex(ctx)
        }

        for (i in 0 until renderMipLevels) {
            val mipWidth = parentPass.offscreenPass.getMipWidth(i)
            val mipHeight = parentPass.offscreenPass.getMipHeight(i)
            val fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            if (parentPass.offscreenPass.config.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                glColorTexs.forEachIndexed { iAttachment, tex ->
                    glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0 + iAttachment, GL_TEXTURE_2D, tex, i)
                }
                val attachments = IntArray(glColorTexs.size) { GL_COLOR_ATTACHMENT0 + it }
                glDrawBuffers(attachments)
            } else {
                val rbo = glGenRenderbuffers()
                glBindRenderbuffer(GL_RENDERBUFFER, rbo)
                glRenderbufferStorage(GL_RENDERBUFFER, TexFormat.R.glInternalFormat, mipWidth, mipHeight)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_RENDERBUFFER, rbo)
                rbos += rbo
            }

            if (parentPass.offscreenPass.config.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, glDepthTex, i)
            } else {
                val rbo = glGenRenderbuffers()
                glBindRenderbuffer(GL_RENDERBUFFER, rbo)
                glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipWidth, mipHeight)
                glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo)
                rbos += rbo
            }
            fbos += fbo
        }
        isCreated = true
    }

    private fun createColorTexs(ctx: Lwjgl3Context) {
        for (i in parentPass.offscreenPass.colorTextures.indices) {
            val cfg = parentPass.offscreenPass.config.colorAttachments[i]

            if (cfg.providedTexture != null) {
                glColorTexs[i] = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

            } else {
                val format = cfg.colorFormat
                val intFormat = format.glInternalFormat
                val width = parentPass.offscreenPass.width
                val height = parentPass.offscreenPass.height
                val mipLevels = parentPass.offscreenPass.config.mipLevels

                val estSize = Texture.estimatedTexSize(width, height, format.pxSize, 1, mipLevels)
                val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
                tex.setSize(width, height)
                tex.applySamplerProps(parentPass.offscreenPass.colorTextures[i].props)
                glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)

                glColorTexs[i] = tex.texture
                parentPass.offscreenPass.colorTextures[i].loadedTexture = tex
                parentPass.offscreenPass.colorTextures[i].loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun createDepthTex(ctx: Lwjgl3Context) {
        val cfg = parentPass.offscreenPass.config.depthAttachment!!

        if (cfg.providedTexture != null) {
            glDepthTex = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

        } else {
            val intFormat = GL_DEPTH_COMPONENT24
            val width = parentPass.offscreenPass.width
            val height = parentPass.offscreenPass.height
            val mipLevels = parentPass.offscreenPass.config.mipLevels
            val depthCfg = parentPass.offscreenPass.config.depthAttachment

            val estSize = Texture.estimatedTexSize(width, height, 4, 1, mipLevels)
            val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
            tex.setSize(width, height)
            tex.applySamplerProps(parentPass.offscreenPass.depthTexture!!.props)
            if (depthCfg.depthCompareOp != DepthCompareOp.DISABLED) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, depthCfg.depthCompareOp.glOp)
            }
            glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)

            glDepthTex = tex.texture
            parentPass.offscreenPass.depthTexture.loadedTexture = tex
            parentPass.offscreenPass.depthTexture.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture.createCopyTexColor(ctx: Lwjgl3Context) {
        val intFormat = props.format.glInternalFormat
        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height
        val mipLevels = parentPass.offscreenPass.config.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, props.format.pxSize, 1, mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(props)
        glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}