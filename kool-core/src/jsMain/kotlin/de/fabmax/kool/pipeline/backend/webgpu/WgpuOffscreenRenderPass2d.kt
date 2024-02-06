package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith

class WgpuOffscreenRenderPass2d(
    val parentPass: OffscreenRenderPass2d,
    numSamples: Int,
    backend: RenderBackendWebGpu
) : WgpuRenderPass<OffscreenRenderPass2d>(GPUTextureFormat.depth32float, numSamples, backend), OffscreenPass2dImpl {

    override val colorTargetFormats = parentPass.colorTextures.map { it.props.format.wgpu }

    private val colorAttachments = List(parentPass.colorTextures.size) {
        RenderAttachment(parentPass.colorTextures[it], false, "${parentPass.name}.color[$it]")
    }
    private val depthAttachment: RenderAttachment

    private var copySrcFlag = 0
    private val isCopyColor: Boolean
        get() = parentPass.copyTargetsColor.isNotEmpty()

    init {
        val depthTex = when (parentPass.depthAttachment) {
            is OffscreenRenderPass.RenderBufferDepthAttachment -> Texture2d(
                TextureProps(generateMipMaps = false, defaultSamplerSettings = SamplerSettings().clamped()),
                "${parentPass.name}_depth"
            )
            is OffscreenRenderPass.TextureDepthAttachment -> parentPass.depthTexture!!
        }
        depthAttachment = RenderAttachment(depthTex, true,  depthTex.name)
        releaseWith(parentPass)
    }

    override fun applySize(width: Int, height: Int) {
        colorAttachments.forEach { it.applySize(width, height) }
        depthAttachment.applySize(width, height)
    }

    override fun release() { }

    fun draw(encoder: GPUCommandEncoder) {
        if (isCopyColor && copySrcFlag == 0) {
            // recreate color attachment texture with COPY_SRC flag set
            // for now, texture copy is limited two first color target
            copySrcFlag = GPUTextureUsage.COPY_SRC
            colorAttachments[0].applySize(parentPass.width, parentPass.height)
        }

        render(parentPass, encoder)
        if (!parentPass.drawMipLevels && parentPass.mipLevels > 1) {
            colorAttachments.forEach {
                backend.textureLoader.mipmapGenerator.generateMipLevels(it.descriptor, it.gpuTexture.gpuTexture, encoder)
            }
        }

        if (parentPass.copyTargetsColor.isNotEmpty()) {
            parentPass.copyTargetsColor.forEach { target -> colorAttachments[0].copyToTexture(target, encoder) }
        }
    }

    override fun getRenderAttachments(renderPass: OffscreenRenderPass2d, viewIndex: Int, mipLevel: Int): RenderAttachments {
        val colors = colorAttachments.mapIndexed { i, colorTex ->
            GPURenderPassColorAttachment(
                view = colorTex.mipViews[mipLevel],
                clearValue = parentPass.clearColors[i]?.let { GPUColorDict(it) }
            )
        }.toTypedArray()

        val depth = GPURenderPassDepthStencilAttachment(
            view = depthAttachment.mipViews[mipLevel],
            depthLoadOp = if (parentPass.clearDepth) GPULoadOp.clear else GPULoadOp.load,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = if (renderPass.isReverseDepth) 0f else 1f
        )
        return RenderAttachments(colors, depth)
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

            releaseWith(this@WgpuOffscreenRenderPass2d)
            texture.loadedTexture = WgpuLoadedTexture(gpuTexture)
            texture.loadingState = Texture.LoadingState.LOADED
            createViews()
        }

        fun applySize(width: Int, height: Int) {
            val (desc, tex) = createTexture(
                width = width,
                height = height,
                usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT or copySrcFlag
            )
            descriptor = desc
            gpuTexture = tex

            texture.loadedTexture?.release()
            texture.loadedTexture = WgpuLoadedTexture(gpuTexture)
            createViews()
        }

        fun copyToTexture(target: Texture2d, encoder: GPUCommandEncoder) {
            var copyDst = (target.loadedTexture as WgpuLoadedTexture?)
            if (copyDst == null || copyDst.width != parentPass.width || copyDst.height != parentPass.height) {
                copyDst?.release()
                val (_, gpuTex) = createTexture(
                    width = parentPass.width,
                    height = parentPass.height,
                    usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                    texture = target
                )
                copyDst = WgpuLoadedTexture(gpuTex)
                target.loadedTexture = copyDst
                target.loadingState = Texture.LoadingState.LOADED
            }
            backend.textureLoader.copyTexture2d(gpuTexture.gpuTexture, copyDst.texture.gpuTexture, parentPass.mipLevels, encoder)
        }

        private fun createViews() {
            mipViews.clear()
            for (i in 0 until parentPass.mipLevels) {
                mipViews += gpuTexture.gpuTexture.createView(baseMipLevel = i, mipLevelCount = 1)
            }
        }

        private fun createTexture(
            width: Int,
            height: Int,
            usage: Int,
            texture: Texture = this.texture
        ): Pair<GPUTextureDescriptor, WgpuTextureResource> {
            val descriptor = GPUTextureDescriptor(
                label = name,
                size = intArrayOf(width, height),
                format = if (isDepth) GPUTextureFormat.depth32float else texture.props.format.wgpu,
                usage = usage,
                mipLevelCount = parentPass.mipLevels,
                sampleCount = numSamples
            )
            val tex = backend.createTexture(descriptor, texture)
            return descriptor to tex
        }

        override fun release() {
            super.release()
            texture.loadingState = Texture.LoadingState.NOT_LOADED
            texture.loadedTexture = null
        }
    }
}