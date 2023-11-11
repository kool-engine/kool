package de.fabmax.kool.platform.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.logE
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30.*
import org.lwjgl.opengl.GL42.glTexStorage2D
import org.lwjgl.opengl.GL43.glCopyImageSubData

class OffscreenPassCubeGl(val parentPass: OffscreenRenderPassCube) : OffscreenPassCubeImpl {
    private val fbos = mutableListOf<Int>()
    private val rbos = mutableListOf<Int>()

    private var isCreated = false
    private var glColorTex = 0

    override fun draw(ctx: KoolContext) {
        ctx as Lwjgl3Context
        if (!isCreated) {
            create(ctx)
        }

        val glBackend = ctx.backend as GlRenderBackend
        val pass = parentPass
        for (mipLevel in 0 until pass.mipLevels) {
            pass.onSetupMipLevel?.invoke(mipLevel, ctx)
            for (i in pass.views.indices) {
                pass.views[i].viewport.set(0, 0, pass.getMipWidth(mipLevel), pass.getMipHeight(mipLevel))
            }
            glBindFramebuffer(GL_FRAMEBUFFER, fbos[mipLevel])

            for (i in CUBE_VIEWS.indices) {
                val cubeView = CUBE_VIEWS[i]
                val passView = pass.views[cubeView.index]
                glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_CUBE_MAP_POSITIVE_X + i, glColorTex, mipLevel)
                glBackend.queueRenderer.renderView(passView)
            }
            copyToTextures(mipLevel, ctx)
        }

        glBindFramebuffer(GL_FRAMEBUFFER, GL11.GL_NONE)
    }

    private fun copyToTextures(mipLevel: Int, ctx: Lwjgl3Context) {
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
                glCopyImageSubData(glColorTex, GL_TEXTURE_CUBE_MAP, mipLevel, 0, 0, 0,
                        target.texture, GL_TEXTURE_CUBE_MAP, mipLevel, 0, 0, 0, width, height, 6)
            } else {
                logE { "Cubemap color copy from renderbuffer is not supported" }
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

        glColorTex = 0
        isCreated = false
    }

    override fun applySize(width: Int, height: Int, ctx: KoolContext) {
        dispose(ctx)
        create(ctx)
    }

    private fun create(ctx: KoolContext) {
        createColorTex(ctx)

        for (i in 0 until parentPass.mipLevels) {
            val fbo = glGenFramebuffers()
            val rbo = glGenRenderbuffers()

            val mipWidth = parentPass.getMipWidth(i)
            val mipHeight = parentPass.getMipHeight(i)

            glBindFramebuffer(GL_FRAMEBUFFER, fbo)
            glBindRenderbuffer(GL_RENDERBUFFER, rbo)
            glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT24, mipWidth, mipHeight)
            glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo
        }
        isCreated = true
    }

    private fun createColorTex(ctx: KoolContext) {
        val format = parentPass.colorAttachments[0].colorFormat
        val intFormat = format.glInternalFormat
        val width = parentPass.width
        val height = parentPass.height
        val mipLevels = parentPass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, format.pxSize)
        val tex = LoadedTextureGl(ctx as Lwjgl3Context, GL_TEXTURE_CUBE_MAP, glGenTextures(), estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(parentPass.colorTexture!!.props)
        glTexStorage2D(GL_TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)

        glColorTex = tex.texture
        parentPass.colorTexture!!.loadedTexture = tex
        parentPass.colorTexture!!.loadingState = Texture.LoadingState.LOADED
    }

    private fun TextureCube.createCopyTexColor(ctx: Lwjgl3Context) {
        val intFormat = props.format.glInternalFormat
        val width = parentPass.width
        val height = parentPass.height
        val mipLevels = parentPass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, props.format.pxSize)
        val tex = LoadedTextureGl(ctx, GL_TEXTURE_CUBE_MAP, glGenTextures(), estSize)
        tex.setSize(width, height, 1)
        tex.applySamplerProps(props)
        glTexStorage2D(GL_TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    companion object {
        private val CUBE_VIEWS = Array(6) { i ->
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