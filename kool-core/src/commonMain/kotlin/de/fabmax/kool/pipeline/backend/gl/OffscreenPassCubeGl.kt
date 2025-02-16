package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.OffscreenPassInfo
import de.fabmax.kool.util.logW

class OffscreenPassCubeGl(
    val parent: OffscreenPassCube,
    backend: RenderBackendGl
) : GlRenderPass(backend), OffscreenPassCubeImpl {

    private val fbos = mutableListOf<GlFramebuffer>()
    private val rbos = mutableListOf<GlRenderbuffer>()
    private var copyFbo: GlFramebuffer? = null

    internal val colorTextures = Array(parent.colorAttachments.size) { gl.NULL_TEXTURE }
    internal var depthTexture = gl.NULL_TEXTURE

    private val resInfo = OffscreenPassInfo(parent)

    init {
        if (parent.numSamples > 1) {
            logW {
                "OffscreenPassCube ${parent.name} requests a sample count of ${parent.numSamples} but multi-sampling " +
                        "is not yet implemented in OpenGL backend. Falling back to single-sample. (Use Vulkan or " +
                        "WebGPU backend if you need multi-sampled offscreen passes)"
            }
        }
        createBuffers()
    }

    override fun setupFramebuffer(mipLevel: Int, layer: Int) {
        gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
        attachColorTextures(mipLevel, layer)
        if (depthTexture != gl.NULL_TEXTURE) {
            attachDepthTexture(mipLevel, layer)
        }
    }

    fun draw() {
        resInfo.sceneName = parent.parentScene?.name ?: "scene:<null>"

        renderViews(parent)

        if (parent.mipMode == RenderPass.MipMode.Generate) {
            for (i in colorTextures.indices) {
                gl.bindTexture(gl.TEXTURE_CUBE_MAP, colorTextures[i])
                gl.generateMipmap(gl.TEXTURE_CUBE_MAP)
            }
        }
    }

    override fun copy(frameCopy: FrameCopy) {
        val width = parent.width
        val height = parent.height
        val mipLevels = parent.numTextureMipLevels
        frameCopy.setupCopyTargets(width, height, mipLevels, gl.TEXTURE_CUBE_MAP)

        var blitMask = 0
        if (frameCopy.isCopyColor) blitMask = gl.COLOR_BUFFER_BIT
        if (frameCopy.isCopyDepth) blitMask = blitMask or gl.DEPTH_BUFFER_BIT

        var mipWidth = width
        var mipHeight = height
        val copyFbo = this.copyFbo ?: gl.createFramebuffer().also { this.copyFbo = it }
        for (mipLevel in 0 until parent.numTextureMipLevels) {
            for (viewIndex in 0 .. 5) {
                val glTarget = gl.TEXTURE_CUBE_MAP_POSITIVE_X + viewIndex
                gl.bindFramebuffer(gl.FRAMEBUFFER, copyFbo)
                for (i in frameCopy.colorCopy.indices) {
                    val loaded = frameCopy.colorCopy[i].gpuTexture as LoadedTextureGl
                    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.COLOR_ATTACHMENT0 + i, glTarget, loaded.glTexture, mipLevel)
                }
                frameCopy.depthCopy?.let {
                    val loaded = it.gpuTexture as LoadedTextureGl
                    gl.framebufferTexture2D(gl.FRAMEBUFFER, gl.DEPTH_ATTACHMENT, glTarget, loaded.glTexture, mipLevel)
                }

                gl.bindFramebuffer(gl.FRAMEBUFFER, fbos[mipLevel])
                attachColorTextures(mipLevel, viewIndex)
                attachDepthTexture(mipLevel, viewIndex)

                gl.bindFramebuffer(gl.READ_FRAMEBUFFER, fbos[mipLevel])
                gl.bindFramebuffer(gl.DRAW_FRAMEBUFFER, copyFbo)
                gl.blitFramebuffer(
                    0, 0, mipWidth, mipHeight,
                    0, 0, mipWidth, mipHeight,
                    blitMask, gl.NEAREST
                )
            }
            mipWidth = mipWidth shr 1
            mipHeight = mipHeight shr 1
        }
    }

    private fun deleteBuffers() {
        copyFbo?.let { gl.deleteFramebuffer(it) }
        fbos.forEach { gl.deleteFramebuffer(it) }
        rbos.forEach { gl.deleteRenderbuffer(it) }
        fbos.clear()
        rbos.clear()

        parent.colorTextures.forEach { tex ->
            tex.gpuTexture?.release()
        }
        parent.depthTexture?.gpuTexture?.release()

        for (i in colorTextures.indices) { colorTextures[i] = gl.NULL_TEXTURE }
        depthTexture = gl.NULL_TEXTURE
    }

    override fun release() {
        super.release()
        deleteBuffers()
        resInfo.deleted()
    }

    override fun applySize(width: Int, height: Int) {
        deleteBuffers()
        createBuffers()
    }

    private fun createBuffers() {
        parent.colorTextures.forEachIndexed { i, tex ->
            colorTextures[i] = createColorAttachmentTexture(parent.width, parent.height, parent.numTextureMipLevels, tex, gl.TEXTURE_CUBE_MAP)
        }
        parent.depthTexture?.let {
            depthTexture = createDepthAttachmentTexture(parent.width, parent.height, parent.numTextureMipLevels, it, gl.TEXTURE_CUBE_MAP)
        }

        for (mipLevel in 0 until parent.numRenderMipLevels) {
            val fbo = gl.createFramebuffer()
            fbos += fbo
            gl.bindFramebuffer(gl.FRAMEBUFFER, fbo)

            attachColorTextures(mipLevel, 0)
            if (parent.depthTexture != null) {
                attachDepthTexture(mipLevel, 0)
            } else if (parent.depthAttachment is OffscreenPass2d.TransientDepthAttachment) {
                rbos += createAndAttachDepthRenderBuffer(parent, mipLevel)
            }

            check(gl.checkFramebufferStatus(gl.FRAMEBUFFER) == gl.FRAMEBUFFER_COMPLETE) {
                "OffscreenRenderPassCubeGl: Framebuffer incomplete: ${parent.name}, level: $mipLevel"
            }
        }
    }

    private fun attachColorTextures(mipLevel: Int, layer: Int) {
        colorTextures.forEachIndexed { iAttachment, tex ->
            gl.framebufferTexture2D(
                target = gl.FRAMEBUFFER,
                attachment = gl.COLOR_ATTACHMENT0 + iAttachment,
                textarget = gl.TEXTURE_CUBE_MAP_POSITIVE_X + layer,
                texture = tex,
                level = mipLevel
            )
        }
        if (colorTextures.size > 1) {
            val attachments = IntArray(colorTextures.size) { gl.COLOR_ATTACHMENT0 + it }
            gl.drawBuffers(attachments)
        }
    }

    private fun attachDepthTexture(mipLevel: Int, layer: Int) = gl.framebufferTexture2D(
        target = gl.FRAMEBUFFER,
        attachment = gl.DEPTH_ATTACHMENT,
        textarget = gl.TEXTURE_CUBE_MAP_POSITIVE_X + layer,
        texture = depthTexture,
        level = mipLevel
    )
}