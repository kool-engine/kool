package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.pipeline.backend.stats.OffscreenPassInfo

class OffscreenRenderPassCubeGl(val parent: OffscreenRenderPassCube, val backend: RenderBackendGl) : OffscreenPassCubeImpl {
    private val gl = backend.gl

    private val fbos = mutableListOf<GlFramebuffer>()
    private val rbos = mutableListOf<GlRenderbuffer>()

    internal var glColorTex = gl.NULL_TEXTURE

    override val isReverseDepth: Boolean
        get() = parent.useReversedDepthIfAvailable && backend.depthRange == DepthRange.ZERO_TO_ONE

    private var isCreated = false

    private val resInfo = OffscreenPassInfo(parent)

    fun draw() {
        resInfo.sceneName = parent.parentScene?.name ?: "scene:<null>"
        if (!isCreated) {
            createBuffers()
        }

        val needsCopy = parent.copyTargetsColor.isNotEmpty()

        val pass = parent
        for (mipLevel in 0 until pass.mipLevels) {
            pass.setupMipLevel(mipLevel)
            for (i in pass.views.indices) {
                pass.views[i].viewport.set(0, 0, pass.getMipWidth(mipLevel), pass.getMipHeight(mipLevel))
            }
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])

            for (i in CUBE_VIEWS.indices) {
                val cubeView = CUBE_VIEWS[i]
                pass.setupView(cubeView.index)
                val passView = pass.views[cubeView.index]
                gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_CUBE_MAP_POSITIVE_X + i, glColorTex, mipLevel)
                backend.queueRenderer.renderView(passView, mipLevel)

                if (needsCopy && !gl.capabilities.canFastCopyTextures) {
                    // use fallback / slightly slower texture copy method
                    copyToTexturesCompat(i, mipLevel)
                }
            }
        }
        gl.bindFramebuffer(gl.FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)

        if (needsCopy && gl.capabilities.canFastCopyTextures) {
            // use fast texture copy method, requires OpenGL 4.3 or higher
            gl.copyTexturesFast(this)
        }
    }

    private fun copyToTexturesCompat(face: Int, mipLevel: Int) {
        gl.readBuffer(gl.COLOR_ATTACHMENT0)
        for (i in parent.copyTargetsColor.indices) {
            val copyTarget = parent.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != parent.width || height != parent.height) {
                copyTarget.loadedTexture?.release()
                copyTarget.createCopyTexColor()
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }
            width = width shr mipLevel
            height = height shr mipLevel
            val target = copyTarget.loadedTexture as LoadedTextureGl
            gl.bindTexture(gl.TEXTURE_CUBE_MAP, target.glTexture)
            gl.copyTexSubImage2D(gl.TEXTURE_CUBE_MAP_POSITIVE_X + face, mipLevel, 0, 0, 0, 0, width, height)
        }
    }

    private fun deleteBuffers() {
        fbos.forEach { gl.deleteFramebuffer(it) }
        rbos.forEach { gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        parent.colorTextures.forEach { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }
        parent.depthTexture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.LOADED) {
                tex.dispose()
            }
        }

        glColorTex = gl.NULL_TEXTURE
        isCreated = false
    }

    override fun release() {
        deleteBuffers()
        resInfo.deleted()
    }

    override fun applySize(width: Int, height: Int) {
        deleteBuffers()
        createBuffers()
    }

    private fun createBuffers() {
        if (parent.colorAttachment is OffscreenRenderPass.TextureColorAttachment) {
            createColorTex(parent.colorAttachment)
        }

        for (i in 0 until parent.mipLevels) {
            val fbo = gl.createFramebuffer()
            val rbo = gl.createRenderbuffer()

            val mipWidth = parent.getMipWidth(i)
            val mipHeight = parent.getMipHeight(i)

            gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)
            gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
            gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT24, mipWidth, mipHeight)
            gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo

            check(gl.checkFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE) {
                "OffscreenRenderPassCubeGl: Framebuffer incomplete: ${parent.name}, level: $i"
            }
        }
        isCreated = true
    }

    private fun createColorTex(colorAttachment: OffscreenRenderPass.TextureColorAttachment) {
        val parentTex = parent.colorTexture!!
        val format = colorAttachment.attachments[0].colorFormat
        val intFormat = format.glInternalFormat(gl)
        val width = parent.width
        val height = parent.height
        val mipLevels = parent.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, format.pxSize).toLong()
        val tex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), backend, parentTex, estSize)
        tex.setSize(width, height, 1)
        tex.bind()
        tex.applySamplerSettings(parentTex.props.defaultSamplerSettings)
        gl.texStorage2D(gl.TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)

        glColorTex = tex.glTexture
        parentTex.loadedTexture = tex
        parentTex.loadingState = Texture.LoadingState.LOADED
    }

    private fun TextureCube.createCopyTexColor() {
        val intFormat = props.format.glInternalFormat(gl)
        val width = parent.width
        val height = parent.height
        val mipLevels = parent.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 6, mipLevels, props.format.pxSize).toLong()
        val tex = LoadedTextureGl(gl.TEXTURE_CUBE_MAP, gl.createTexture(), backend, this, estSize)
        tex.setSize(width, height, 1)
        tex.bind()
        tex.applySamplerSettings(props.defaultSamplerSettings)
        gl.texStorage2D(gl.TEXTURE_CUBE_MAP, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }

    companion object {
        private val CUBE_VIEWS = Array(6) { i ->
            when (i) {
                0 -> OffscreenRenderPassCube.ViewDirection.POS_X
                1 -> OffscreenRenderPassCube.ViewDirection.NEG_X
                2 -> OffscreenRenderPassCube.ViewDirection.POS_Y
                3 -> OffscreenRenderPassCube.ViewDirection.NEG_Y
                4 -> OffscreenRenderPassCube.ViewDirection.POS_Z
                5 -> OffscreenRenderPassCube.ViewDirection.NEG_Z
                else -> throw IllegalStateException()
            }
        }
    }
}