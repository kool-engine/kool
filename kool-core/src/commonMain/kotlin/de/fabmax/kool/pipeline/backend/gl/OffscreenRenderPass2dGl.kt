package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.pipeline.backend.stats.OffscreenPassInfo
import de.fabmax.kool.util.logE

class OffscreenRenderPass2dGl(val parent: OffscreenRenderPass2d, val backend: RenderBackendGl) : OffscreenPass2dImpl {
    private val gl = backend.gl

    internal val fbos = mutableListOf<GlFramebuffer>()
    internal val rbos = mutableListOf<GlRenderbuffer>()

    internal val colorTextures = Array(parent.numColorAttachments) { gl.NULL_TEXTURE }
    internal var depthTexture = gl.NULL_TEXTURE

    private val renderMipLevels: Int = if (parent.drawMipLevels) { parent.mipLevels } else { 1 }

    override val isReverseDepth: Boolean
        get() = parent.useReversedDepthIfAvailable && backend.depthRange == DepthRange.ZERO_TO_ONE

    private var isCreated = false

    private val resInfo = OffscreenPassInfo(parent)

    private val frameBufferSetter = QueueRenderer.FrameBufferSetter { viewIndex, mipLevel ->
        if (viewIndex == 0) {
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
        }
    }

    fun draw() {
        resInfo.sceneName = parent.parentScene?.name ?: "scene:<null>"
        if (!isCreated) {
            createBuffers()
        }

        val needsCopy = parent.copyTargetsColor.isNotEmpty()

        backend.queueRenderer.renderViews(parent, frameBufferSetter)

        if (!parent.drawMipLevels && parent.mipLevels > 1) {
            for (i in colorTextures.indices) {
                gl.bindTexture(gl.TEXTURE_2D, colorTextures[i])
                gl.generateMipmap(gl.TEXTURE_2D)
            }
        }

        if (needsCopy) {
            if (gl.capabilities.canFastCopyTextures) {
                // use fast texture copy method, requires OpenGL 4.3 or higher
                gl.copyTexturesFast(this)
            } else {
                for (mipLevel in 0 until parent.mipLevels) {
                    copyToTexturesCompat(mipLevel)
                }
            }
        }
        gl.bindFramebuffer(gl.FRAMEBUFFER, gl.DEFAULT_FRAMEBUFFER)
    }

    private fun copyToTexturesCompat(mipLevel: Int) {
        gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
        gl.readBuffer(gl.COLOR_ATTACHMENT0)
        for (i in parent.copyTargetsColor.indices) {
            val copyTarget = parent.copyTargetsColor[i]
            var width = copyTarget.loadedTexture?.width ?: 0
            var height = copyTarget.loadedTexture?.height ?: 0
            if (width != parent.width || height != parent.height) {
                // recreate target texture if size has changed
                copyTarget.loadedTexture?.release()
                copyTarget.createCopyTexColor(parent, backend)
                width = copyTarget.loadedTexture!!.width
                height = copyTarget.loadedTexture!!.height
            }
            width = width shr mipLevel
            height = height shr mipLevel
            val target = copyTarget.loadedTexture as LoadedTextureGl
            gl.bindTexture(gl.TEXTURE_2D, target.glTexture)
            gl.copyTexSubImage2D(gl.TEXTURE_2D, mipLevel, 0, 0, 0, 0, width, height)
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

        for (i in colorTextures.indices) { colorTextures[i] = gl.NULL_TEXTURE }
        depthTexture = gl.NULL_TEXTURE
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
            createColorTextures(parent.colorAttachment)
        }
        if (parent.depthAttachment is OffscreenRenderPass.TextureDepthAttachment) {
            createDepthTexture(parent.depthAttachment)
        }

        for (mipLevel in 0 until renderMipLevels) {
            val fbo = gl.createFramebuffer()
            fbos += fbo
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)

            when (parent.colorAttachment) {
                is OffscreenRenderPass.RenderBufferColorAttachment -> {
                    rbos += attachColorRenderBuffer(mipLevel, parent.colorAttachment)
                }
                is OffscreenRenderPass.TextureColorAttachment -> {
                    attachColorTextures(mipLevel, colorTextures)
                }
            }

            when (parent.depthAttachment) {
                is OffscreenRenderPass.RenderBufferDepthAttachment -> {
                    rbos += attachDepthRenderBuffer(mipLevel)
                }
                is OffscreenRenderPass.TextureDepthAttachment -> {
                    attachDepthTexture(mipLevel)
                }
            }

            if (gl.checkFramebufferStatus(gl.FRAMEBUFFER) != gl.FRAMEBUFFER_COMPLETE) {
                logE { "OffscreenRenderPass2dGl: Framebuffer incomplete: ${parent.name}, level: $mipLevel" }
            }
        }
        isCreated = true
    }

    private fun attachColorTextures(mipLevel: Int, textures: Array<GlTexture>) {
        val attachments = IntArray(textures.size) { gl.COLOR_ATTACHMENT0 + it }
        textures.forEachIndexed { iAttachment, tex ->
            gl.framebufferTexture2D(
                target = gl.FRAMEBUFFER,
                attachment = attachments[iAttachment],
                textarget = gl.TEXTURE_2D,
                texture = tex,
                level = mipLevel
            )
        }
        gl.drawBuffers(attachments)
    }

    private fun attachColorRenderBuffer(mipLevel: Int, attachment: OffscreenRenderPass.RenderBufferColorAttachment): GlRenderbuffer {
        val rbo = gl.createRenderbuffer()
        gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
        if (attachment.isMultiSampled) {
            gl.renderbufferStorageMultisample(
                target = gl.RENDERBUFFER,
                samples = backend.numSamples,
                internalformat = attachment.colorFormat.glInternalFormat(gl),
                width = parent.width shr mipLevel,
                height = parent.height shr mipLevel
            )
        } else {
            gl.renderbufferStorage(
                target = gl.RENDERBUFFER,
                internalformat = attachment.colorFormat.glInternalFormat(gl),
                width = parent.width shr mipLevel,
                height = parent.height shr mipLevel
            )
        }
        gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0, gl.RENDERBUFFER, rbo)
        return rbo
    }

    private fun attachDepthTexture(mipLevel: Int) {
        gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.TEXTURE_2D, depthTexture, mipLevel)
    }

    private fun attachDepthRenderBuffer(mipLevel: Int): GlRenderbuffer {
        val isMultiSampled = parent.colorAttachment is OffscreenRenderPass.RenderBufferColorAttachment && parent.colorAttachment.isMultiSampled

        val rbo = gl.createRenderbuffer()
        val mipWidth = parent.width shr mipLevel
        val mipHeight = parent.height shr mipLevel
        gl.bindRenderbuffer(gl.RENDERBUFFER, rbo)
        if (isMultiSampled) {
            gl.renderbufferStorageMultisample(gl.RENDERBUFFER, backend.numSamples, gl.DEPTH_COMPONENT32F, mipWidth, mipHeight)
        } else {
            gl.renderbufferStorage(gl.RENDERBUFFER, gl.DEPTH_COMPONENT32F, mipWidth, mipHeight)
        }
        gl.framebufferRenderbuffer(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, gl.RENDERBUFFER, rbo)
        return rbo
    }

    private fun createColorTextures(texAttachment: OffscreenRenderPass.TextureColorAttachment) {
        for (i in parent.colorTextures.indices) {
            val cfg = texAttachment.attachments[i]

            if (cfg.providedTexture != null) {
                colorTextures[i] = (cfg.providedTexture.loadedTexture as LoadedTextureGl).glTexture

            } else {
                val parentTex = parent.colorTextures[i]
                val format = cfg.colorFormat
                val intFormat = format.glInternalFormat(gl)
                val width = parent.width
                val height = parent.height
                val mipLevels = parent.mipLevels

                val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, format.pxSize).toLong()
                val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, parentTex, estSize)
                tex.setSize(width, height, 1)
                tex.bind()
                tex.applySamplerSettings(parentTex.props.defaultSamplerSettings)
                gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)

                colorTextures[i] = tex.glTexture
                parentTex.loadedTexture = tex
                parentTex.loadingState = Texture.LoadingState.LOADED
            }
        }
    }

    private fun createDepthTexture(texAttachment: OffscreenRenderPass.TextureDepthAttachment) {
        val cfg = texAttachment.attachment

        if (cfg.providedTexture != null) {
            depthTexture = (cfg.providedTexture.loadedTexture as LoadedTextureGl).glTexture

        } else {
            val parentTex = parent.depthTexture!!
            val intFormat = gl.DEPTH_COMPONENT32F
            val width = parent.width
            val height = parent.height
            val mipLevels = parent.mipLevels

            val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, 4).toLong()
            val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, parentTex, estSize)
            tex.setSize(width, height, 1)
            tex.bind()
            tex.applySamplerSettings(parentTex.props.defaultSamplerSettings)
            if (cfg.depthCompareOp != DepthCompareOp.DISABLED) {
                gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_COMPARE_MODE, gl.COMPARE_REF_TO_TEXTURE)
                gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_COMPARE_FUNC, cfg.depthCompareOp.glOp(gl))
            }
            gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)

            depthTexture = tex.glTexture
            parentTex.loadedTexture = tex
            parentTex.loadingState = Texture.LoadingState.LOADED
        }
    }

    private fun Texture2d.createCopyTexColor(pass: OffscreenRenderPass2d, backend: RenderBackendGl) {
        val gl = backend.gl
        val intFormat = props.format.glInternalFormat(gl)
        val width = pass.width
        val height = pass.height
        val mipLevels = pass.mipLevels

        val estSize = Texture.estimatedTexSize(width, height, 1, mipLevels, props.format.pxSize).toLong()
        val tex = LoadedTextureGl(gl.TEXTURE_2D, gl.createTexture(), backend, this, estSize)
        tex.setSize(width, height, 1)
        tex.bind()
        tex.applySamplerSettings(props.defaultSamplerSettings)
        gl.texStorage2D(gl.TEXTURE_2D, mipLevels, intFormat, width, height)
        loadedTexture = tex
        loadingState = Texture.LoadingState.LOADED
    }
}