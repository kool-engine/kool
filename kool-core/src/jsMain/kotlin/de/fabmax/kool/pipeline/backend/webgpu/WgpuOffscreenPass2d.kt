package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.BaseReleasable

class WgpuOffscreenPass2d(
    val parentPass: OffscreenRenderPass2d,
    numSamples: Int,
    backend: RenderBackendWebGpu
) : WgpuRenderPass(GPUTextureFormat.depth32float, numSamples, backend), OffscreenPass2dImpl {

    override val colorTargetFormats = parentPass.colorTextures.map { it.props.format.wgpu }

    private val colorAttachments = List(parentPass.colorTextures.size) {
        RenderAttachment(parentPass.colorTextures[it], false, "${parentPass.name}.color[$it]")
    }
    private val depthAttachment: RenderAttachment?

    private var copySrcFlag = 0

    init {
        val depthTex = when (parentPass.depthAttachment) {
            OffscreenRenderPass.DepthAttachmentRender -> Texture2d(
                TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped()),
                "${parentPass.name}:render-depth"
            )
            else -> parentPass.depthTexture
        }
        depthAttachment = depthTex?.let { RenderAttachment(it, true,  it.name) }
    }

    override fun applySize(width: Int, height: Int) {
        colorAttachments.forEach { it.recreate(width, height) }
        depthAttachment?.recreate(width, height)
    }

    override fun release() {
        super.release()
        colorAttachments.forEach { it.release() }
        depthAttachment?.release()
    }

    fun draw(passEncoderState: RenderPassEncoderState) {
        val isCopySrc = parentPass.frameCopies.isNotEmpty() || parentPass.views.any { it.frameCopies.isNotEmpty() }
        if (isCopySrc && copySrcFlag == 0) {
            // recreate attachment textures with COPY_SRC flag set
            copySrcFlag = GPUTextureUsage.COPY_SRC
            colorAttachments.forEach { it.recreate(parentPass.width, parentPass.height) }
            depthAttachment?.recreate(parentPass.width, parentPass.height)
        }
        render(parentPass, passEncoderState)
    }

    override fun generateMipLevels(encoder: GPUCommandEncoder) {
        colorAttachments.forEach {
            backend.textureLoader.mipmapGenerator.generateMipLevels(it.descriptor, it.gpuTexture.gpuTexture, encoder)
        }
    }

    override fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder) {
        if (frameCopy.isCopyColor) {
            for (i in frameCopy.colorCopy.indices) {
                colorAttachments[i].copyToTexture(frameCopy.colorCopy[i] as Texture2d, encoder)
            }
        }
        if (frameCopy.isCopyDepth) {
            depthAttachment?.copyToTexture(frameCopy.depthCopy2d, encoder)
        }
    }

    override fun beginRenderPass(
        passEncoderState: RenderPassEncoderState,
        forceLoad: Boolean,
        timestampWrites: GPURenderPassTimestampWrites?
    ): GPURenderPassEncoder {
        val renderPass = passEncoderState.renderPass
        val mipLevel = passEncoderState.mipLevel

        val colors = colorAttachments.mapIndexed { i, colorTex ->
            val colorLoadOp = when {
                forceLoad -> GPULoadOp.load
                renderPass.clearColor == null -> GPULoadOp.load
                else -> GPULoadOp.clear
            }
            val clearColor = if (colorLoadOp == GPULoadOp.load) null else parentPass.clearColors[i]?.let { GPUColorDict(it) }

            GPURenderPassColorAttachment(
                view = colorTex.mipViews[mipLevel],
                loadOp = colorLoadOp,
                clearValue = clearColor,
            )
        }.toTypedArray()

        val depthLoadOp = when {
            forceLoad -> GPULoadOp.load
            renderPass.clearDepth -> GPULoadOp.clear
            else -> GPULoadOp.load
        }
        val depth = depthAttachment?.let {
            GPURenderPassDepthStencilAttachment(
                view = it.mipViews[mipLevel],
                depthLoadOp = depthLoadOp,
                depthStoreOp = GPUStoreOp.store,
                depthClearValue = if (renderPass.isReverseDepth) 0f else 1f
            )
        }
        return passEncoderState.encoder.beginRenderPass(colors, depth, timestampWrites, renderPass.name)
    }

    private inner class RenderAttachment(val texture: Texture2d, val isDepth: Boolean, val name: String) : BaseReleasable() {
        var descriptor: GPUTextureDescriptor
        var gpuTexture: WgpuTextureResource
        val mipViews = mutableListOf<GPUTextureView>()

        init {
            val (desc, tex) = createTexture(
                width = parentPass.width,
                height = parentPass.height,
                usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT
            )
            descriptor = desc
            gpuTexture = tex

            texture.gpuTexture = gpuTexture
            texture.loadingState = Texture.LoadingState.LOADED
            createViews()
        }

        fun recreate(width: Int, height: Int) {
            val (desc, tex) = createTexture(
                width = width,
                height = height,
                usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT or copySrcFlag
            )
            descriptor = desc
            gpuTexture = tex

            texture.gpuTexture?.release()
            texture.gpuTexture = gpuTexture
            createViews()
        }

        private fun createViews() {
            mipViews.clear()
            for (i in 0 until parentPass.numRenderMipLevels) {
                mipViews += gpuTexture.gpuTexture.createView(baseMipLevel = i, mipLevelCount = 1)
            }
        }

        private fun createTexture(
            width: Int,
            height: Int,
            usage: Int,
            texture: Texture<*> = this.texture
        ): Pair<GPUTextureDescriptor, WgpuTextureResource> {
            val descriptor = GPUTextureDescriptor(
                label = name,
                size = intArrayOf(width, height),
                format = if (isDepth) GPUTextureFormat.depth32float else texture.props.format.wgpu,
                usage = usage,
                mipLevelCount = parentPass.numTextureMipLevels,
                sampleCount = numSamples
            )
            val tex = backend.createTexture(descriptor, texture)
            return descriptor to tex
        }

        fun copyToTexture(target: Texture2d, encoder: GPUCommandEncoder) {
            var copyDst = (target.gpuTexture as WgpuTextureResource?)
            if (copyDst == null || copyDst.width != parentPass.width || copyDst.height != parentPass.height) {
                copyDst?.release()
                val (_, gpuTex) = createTexture(
                    width = parentPass.width,
                    height = parentPass.height,
                    usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                    texture = target
                )
                copyDst = gpuTex
                target.gpuTexture = copyDst
                target.loadingState = Texture.LoadingState.LOADED
            }
            backend.textureLoader.copyTexture2d(gpuTexture.gpuTexture, copyDst.gpuTexture, parentPass.numTextureMipLevels, encoder)
        }

        override fun release() {
            if (!isReleased) {
                super.release()
                texture.gpuTexture?.release()
                texture.gpuTexture = null
                texture.loadingState = Texture.LoadingState.NOT_LOADED
            }
        }
    }
}