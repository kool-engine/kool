package de.fabmax.kool.platform.gl

import de.fabmax.kool.pipeline.OffscreenPassCubeImpl
import de.fabmax.kool.pipeline.OffscreenRenderPassCube
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.platform.Lwjgl3Context
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D

class OffscreenPassCubeGl(val parentPass: OffscreenPassCubeImpl) : OffscreenPassCubeImpl.BackendImpl {
    private val fbos = mutableListOf<Int>()
    private val rbos = mutableListOf<Int>()

    private var isCreated = false
    private var glColorTex = 0

    override fun draw(ctx: Lwjgl3Context) {
        if (!isCreated) {
            create(ctx)
        }

        val glBackend = ctx.renderBackend as GlRenderBackend
        for (mipLevel in 0 until parentPass.offscreenPass.config.mipLevels) {
            parentPass.offscreenPass.onSetupMipLevel?.invoke(mipLevel, ctx)
            parentPass.offscreenPass.applyMipViewport(mipLevel)
            glBindFramebuffer(GL_FRAMEBUFFER, fbos[mipLevel])

            for (i in 0 until 6) {
                val view = VIEWS[i]
                val queue = parentPass.offscreenPass.drawQueues[view.index]
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, glColorTex, mipLevel)
                glBackend.queueRenderer.renderQueue(queue)
            }
        }

        glBindFramebuffer(GL_FRAMEBUFFER, GL11.GL_NONE)
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

        glColorTex = 0
        isCreated = false
    }

    override fun resize(width: Int, height: Int, ctx: Lwjgl3Context) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: Lwjgl3Context) {
        createColorTex(ctx)

        for (i in 0 until parentPass.offscreenPass.config.mipLevels) {
            val fbo = glGenFramebuffers()
            val rbo = glGenRenderbuffers()

            val mipWidth = parentPass.offscreenPass.getMipWidth(i)
            val mipHeight = parentPass.offscreenPass.getMipHeight(i)

            glBindFramebuffer(GL_FRAMEBUFFER, fbo)
            glBindRenderbuffer(GL_RENDERBUFFER, rbo)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipWidth, mipHeight)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo
        }
        isCreated = true
    }

    private fun createColorTex(ctx: Lwjgl3Context) {
        val format = parentPass.offscreenPass.config.colorAttachments[0].colorFormat
        val intFormat = format.glInternalFormat
        val width = parentPass.offscreenPass.width
        val height = parentPass.offscreenPass.height
        val mipLevels = parentPass.offscreenPass.config.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, format.pxSize, 6, mipLevels)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_CUBE_MAP, glGenTextures(), estSize)
        tex.setSize(width, height)
        tex.applySamplerProps(parentPass.offscreenPass.colorTexture!!.props)
        glTexStorage2D(GL_TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)

        glColorTex = tex.texture
        parentPass.offscreenPass.colorTexture!!.loadedTexture = tex
        parentPass.offscreenPass.colorTexture!!.loadingState = Texture.LoadingState.LOADED
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
}