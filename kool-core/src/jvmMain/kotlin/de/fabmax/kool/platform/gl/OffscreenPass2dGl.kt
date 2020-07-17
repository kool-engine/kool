package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D
import org.lwjgl.opengl.GL43.glCopyImageSubData
import kotlin.math.max

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
            val mipLevel = max(0, parentPass.offscreenPass.targetMipLevel)

            glBindFramebuffer(GL_FRAMEBUFFER, fbos[mipLevel])
            val glBackend = ctx.renderBackend as GlRenderBackend
            glBackend.queueRenderer.renderQueue(parentPass.offscreenPass.drawQueue)
            copyToTextures(mipLevel, ctx)
            glBindFramebuffer(GL_FRAMEBUFFER, GL_NONE)
        }
    }

    private fun copyToTextures(mipLevel: Int, ctx: Lwjgl3Context) {
        if (parentPass.offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_RENDERBUFFER) {
            GL11.glReadBuffer(GL_COLOR_ATTACHMENT0)
        }

        for (i in parentPass.offscreenPass.copyTargetsColor.indices) {
            val copyTarget = parentPass.offscreenPass.copyTargetsColor[i]
            val width = copyTarget.loadedTexture?.width ?: 0
            val height = copyTarget.loadedTexture?.height ?: 0
            if (width != parentPass.offscreenPass.texWidth || height != parentPass.offscreenPass.texHeight) {
                copyTarget.loadedTexture?.dispose()
                copyTarget.createCopyTexColor(ctx)
            }
            val target = copyTarget.loadedTexture as LoadedTextureGl

            if (parentPass.offscreenPass.setup.colorRenderTarget == OffscreenRenderPass2d.RENDER_TARGET_TEXTURE) {
                glCopyImageSubData(glColorTex, GL_TEXTURE_2D, mipLevel, 0, 0, 0,
                    target.texture, GL_TEXTURE_2D, mipLevel, 0, 0, 0, width, height, 1)
            } else {
                glBindTexture(GL_TEXTURE_2D, target.texture)
                glCopyTexSubImage2D(GL_TEXTURE_2D, mipLevel, 0, 0, 0, 0, width, height)
            }
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

    private fun Texture.createCopyTexColor(ctx: Lwjgl3Context) {
        val intFormat = props.format.glInternalFormat
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight

        val estSize = Texture.estimatedTexSize(width, height, parentPass.offscreenPass.colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(props)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    private fun createColorTex(ctx: Lwjgl3Context) {
        val intFormat = parentPass.offscreenPass.colorFormat.glInternalFormat
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight
        val samplerProps = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR,
                mipMapping = parentPass.offscreenPass.mipLevels > 1, maxAnisotropy = 1)

        val estSize = Texture.estimatedTexSize(width, height, parentPass.offscreenPass.colorFormat.pxSize, 1, parentPass.offscreenPass.mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(samplerProps)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glColorTex = tex.texture
        parentPass.colorTexture.loadedTexture = tex
        parentPass.colorTexture.loadingState = Texture.LoadingState.LOADED
    }

    private fun createDepthTex(ctx: Lwjgl3Context) {
        val intFormat = GL_DEPTH_COMPONENT24
        val width = parentPass.offscreenPass.texWidth
        val height = parentPass.offscreenPass.texHeight
        val samplerProps = TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE, addressModeV = AddressMode.CLAMP_TO_EDGE,
                minFilter = FilterMethod.LINEAR, magFilter = FilterMethod.LINEAR,
                mipMapping = parentPass.offscreenPass.mipLevels > 1, maxAnisotropy = 1)

        val estSize = Texture.estimatedTexSize(width, height, 4, 1, parentPass.offscreenPass.mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_2D, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(samplerProps)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_MODE, GL_COMPARE_REF_TO_TEXTURE)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_COMPARE_FUNC, GL_LESS)
        glTexStorage2D(GL_TEXTURE_2D, parentPass.offscreenPass.mipLevels, intFormat, width, height)

        glDepthTex = tex.texture
        parentPass.depthTexture.loadedTexture = tex
        parentPass.depthTexture.loadingState = Texture.LoadingState.LOADED
    }
}