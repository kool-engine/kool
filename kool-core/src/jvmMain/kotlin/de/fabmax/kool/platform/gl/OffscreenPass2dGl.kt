package de.fabmax.kool.platform.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D
import org.lwjgl.opengl.GL43.glCopyImageSubData

class OffscreenPass2dGl(val parentPass: OffscreenRenderPass2d) : OffscreenPass2dImpl {
    private val fbos = mutableListOf<Int>()
    private val rbos = mutableListOf<Int>()

    private val drawMipLevels = parentPass.drawMipLevels
    private val renderMipLevels: Int = if (drawMipLevels) { parentPass.mipLevels } else { 1 }

    private var isCreated = false
    private val glColorTexs = IntArray(parentPass.colorAttachments.size)
    private var glDepthTex = 0

    override fun draw(ctx: KoolContext) {
        ctx as Lwjgl3Context

        if (!isCreated) {
            create(ctx)
        }

        if (isCreated) {
            val glBackend = ctx.renderBackend as GlRenderBackend
            val pass = parentPass
            for (mipLevel in 0 until renderMipLevels) {
                pass.onSetupMipLevel?.invoke(mipLevel, ctx)
                for (i in pass.views.indices) {
                    pass.views[i].viewport.set(0, 0, pass.getMipWidth(mipLevel), pass.getMipHeight(mipLevel))
                }
                glBindFramebuffer(GL_FRAMEBUFFER, fbos[mipLevel])
                glBackend.queueRenderer.renderViews(pass)
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
        for (mipLevel in 0 until parentPass.mipLevels) {
            for (i in parentPass.copyTargetsColor.indices) {
                val copyTarget = parentPass.copyTargetsColor[i]
                var width = copyTarget.loadedTexture?.width ?: 0
                var height = copyTarget.loadedTexture?.height ?: 0
                if (width != parentPass.width || height != parentPass.height) {
                    copyTarget.loadedTexture?.dispose()
                    copyTarget.createCopyTexColor(ctx)
                    width = copyTarget.loadedTexture!!.width
                    height = copyTarget.loadedTexture!!.height
                }
                width = width shr mipLevel
                height = height shr mipLevel
                val target = copyTarget.loadedTexture as LoadedTextureGl

                if (parentPass.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
                    glCopyImageSubData(glColorTexs[0], GL_TEXTURE_2D, mipLevel, 0, 0, 0,
                            target.texture, GL_TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                } else {
                    glCopyImageSubData(rbos[mipLevel], GL_RENDERBUFFER, 0, 0, 0, 0,
                            target.texture, GL_TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
                }
            }
        }
    }

    override fun dispose(ctx: KoolContext) {
        fbos.forEach { glDeleteFramebuffers(it) }
        rbos.forEach { glDeleteRenderbuffers(it) }
        fbos.clear()
        rbos.clear()

        parentPass.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        parentPass.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        for (i in glColorTexs.indices) { glColorTexs[i] = 0 }
        glDepthTex = 0
        isCreated = false
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: KoolContext) {
        if (parentPass.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createColorTexs(ctx)
        }
        if (parentPass.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
            createDepthTex(ctx)
        }

        for (i in 0 until renderMipLevels) {
            val mipWidth = parentPass.getMipWidth(i)
            val mipHeight = parentPass.getMipHeight(i)
            val fbo = glGenFramebuffers()
            glBindFramebuffer(GL_FRAMEBUFFER, fbo)

            if (parentPass.colorRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
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

            if (parentPass.depthRenderTarget == OffscreenRenderPass.RenderTarget.TEXTURE) {
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

    private fun createColorTexs(ctx: KoolContext) {
        for (i in parentPass.colorTextures.indices) {
            val cfg = parentPass.colorAttachments[i]

            if (cfg.providedTexture != null) {
                glColorTexs[i] = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

            } else {
                val format = cfg.colorFormat
                val intFormat = format.glInternalFormat
                val width = parentPass.width
                val height = parentPass.height
                val mipLevels = parentPass.mipLevels

                val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, format.pxSize)
                val tex = LoadedTextureGl(ctx as Lwjgl3Context, GL_TEXTURE_2D, glGenTextures(), estSize)
                tex.setSize(width, height, 1)
                tex.applySamplerProps(parentPass.colorTextures[i].props)
                glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)

                glColorTexs[i] = tex.texture
                parentPass.colorTextures[i].loadedTexture = tex
                parentPass.colorTextures[i].loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun createDepthTex(ctx: KoolContext) {
        val cfg = parentPass.depthAttachment!!

        if (cfg.providedTexture != null) {
            glDepthTex = (cfg.providedTexture.loadedTexture as LoadedTextureGl).texture

        } else {
            val intFormat = GL_DEPTH_COMPONENT32F
            val width = parentPass.width
            val height = parentPass.height
            val mipLevels = parentPass.mipLevels
            val depthCfg = parentPass.depthAttachment

            val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, 4)
            val tex = LoadedTextureGl(ctx as Lwjgl3Context, GL_TEXTURE_2D, glGenTextures(), estSize)
            tex.setSize(width, height, 1)
            tex.applySamplerProps(parentPass.depthTexture!!.props)
            if (depthCfg.depthCompareOp != DepthCompareOp.DISABLED) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, depthCfg.depthCompareOp.glOp)
            }
            glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)

            glDepthTex = tex.texture
            parentPass.depthTexture.loadedTexture = tex
            parentPass.depthTexture.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture2d.createCopyTexColor(ctx: Lwjgl3Context) {
        val intFormat = props.format.glInternalFormat
        val width = parentPass.width
        val height = parentPass.height
        val mipLevels = parentPass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, props.format.pxSize)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        glTexStorage2D(GL_TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}