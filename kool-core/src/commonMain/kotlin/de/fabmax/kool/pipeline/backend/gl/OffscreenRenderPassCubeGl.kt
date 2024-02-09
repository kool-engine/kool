package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.OffscreenPassInfo

class OffscreenRenderPassCubeGl(val parent: OffscreenRenderPassCube, val backend: RenderBackendGl) : OffscreenPassCubeImpl {
    private val gl = backend.gl

    private val fbos = mutableListOf<GlFramebuffer>()
    private val rbos = mutableListOf<GlRenderbuffer>()

    internal var glColorTex = gl.NULL_TEXTURE

    private var isCreated = false

    private val resInfo = OffscreenPassInfo(parent)

    private val frameBufferSetter = QueueRenderer.FrameBufferSetter { viewIndex, mipLevel ->
        if (viewIndex == 0) {
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
        }
        gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_CUBE_MAP_POSITIVE_X + viewIndex, glColorTex, mipLevel)
    }

    fun draw() {
        resInfo.sceneName = parent.parentScene?.name ?: "scene:<null>"
        if (!isCreated) {
            createBuffers()
        }

        val needsCopy = parent.copyTargetsColor.isNotEmpty()

        backend.queueRenderer.renderViews(parent, frameBufferSetter)

        if (needsCopy) {
            if (gl.capabilities.canFastCopyTextures) {
                // use fast texture copy method, requires OpenGL 4.3 or higher
                gl.copyTexturesFast(this)
            } else {
                for (mipLevel in 0 until parent.numTextureMipLevels) {
                    copyToTexturesCompat(mipLevel)
                }
            }
        }
        gl.bindFramebuffer(gl.FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)
    }

    private fun copyToTexturesCompat(mipLevel: Int) {
        gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
        gl.readBuffer(gl.COLOR_ATTACHMENT0)
        for (face in 0..5) {
            gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.TEXTURE_CUBE_MAP_POSITIVE_X + face, glColorTex, mipLevel)
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
        if (parent.colorAttachments is OffscreenRenderPass.ColorAttachmentTextures) {
            createColorTex(parent.colorAttachments)
        }
        //if (parent.depthAttachment is OffscreenRenderPass.TextureDepthAttachment) {
        //    createDepthTexture(parent.depthAttachment)
        //}

        for (i in 0 until parent.numRenderMipLevels) {
            val fbo = gl.createFramebuffer()
            val rbo = gl.createRenderbuffer()

            val mipWidth = parent.width shr i
            val mipHeight = parent.height shr i

            gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)
            gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
            gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT32F, mipWidth, mipHeight)
            gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, rbo)

            fbos += fbo
            rbos += rbo

            check(gl.checkFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE) {
                "OffscreenRenderPassCubeGl: Framebuffer incomplete: ${parent.name}, level: $i"
            }
        }
        isCreated = true
    }

    private fun createColorTex(colorAttachment: OffscreenRenderPass.ColorAttachmentTextures) {
        val parentTex = parent.colorTexture!!
        val format = colorAttachment.attachments[0].textureFormat
        val intFormat = format.glInternalFormat(gl)
        val width = parent.width
        val height = parent.height
        val mipLevels = parent.numTextureMipLevels

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
        val mipLevels = parent.numTextureMipLevels

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