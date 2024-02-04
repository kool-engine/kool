package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith

class WgpuOffscreenRenderPass2d(
    val parentPass: OffscreenRenderPass2d,
    numSamples: Int,
    backend: RenderBackendWebGpu
) : WgpuRenderPass<OffscreenRenderPass2d>(GPUTextureFormat.depth32float, numSamples, backend), OffscreenPass2dImpl {

    override val isReverseDepth: Boolean = false

    override val colorTargetFormats = parentPass.colorTextures.map { it.props.format.wgpu }

    private val colorAttachments = List(parentPass.numColorTextures) {
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

        val mipLevels = if (parentPass.drawMipLevels) parentPass.mipLevels else 1
        render(parentPass, mipLevels, encoder)
        if (!parentPass.drawMipLevels && parentPass.mipLevels > 1) {
            colorAttachments.forEach {
                backend.textureLoader.mipmapGenerator.generateMipLevels(it.descriptor, it.gpuTexture.gpuTexture, encoder)
            }
        }

        if (parentPass.copyTargetsColor.isNotEmpty()) {
            parentPass.copyTargetsColor.forEach { tex ->
                var wgpuTex = (tex.loadedTexture as WgpuTextureResource?)
                if (wgpuTex == null) {
                    val (_, gpuTex) = colorAttachments[0].createTexture(
                        width = parentPass.width,
                        height = parentPass.height,
                        usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                    )
                    wgpuTex = gpuTex
                    tex.loadedTexture = WgpuLoadedTexture(gpuTex)
                }
                tex.loadingState = Texture.LoadingState.LOADED

                var width = parentPass.width
                var height = parentPass.height
                for (i in 0 until parentPass.mipLevels) {
                    encoder.copyTextureToTexture(
                        source = GPUImageCopyTexture(colorAttachments[0].gpuTexture.gpuTexture, mipLevel = i),
                        destination = GPUImageCopyTexture(wgpuTex.gpuTexture, mipLevel = i),
                        intArrayOf(width, height)
                    )
                    width /= 2
                    height /= 2
                }
            }
        }
    }

    override fun getRenderAttachments(renderPass: OffscreenRenderPass2d, viewIndex: Int, mipLevel: Int): RenderAttachments {
        val colors = colorAttachments.mapIndexed { i, colorTex ->
            GPURenderPassColorAttachment(
                view = colorTex.mipViews[mipLevel],
                clearValue = parentPass.views[viewIndex].clearColors[i]?.let { GPUColorDict(it) }
            )
        }.toTypedArray()

        val depth = GPURenderPassDepthStencilAttachment(
            view = depthAttachment.mipViews[mipLevel],
            depthLoadOp = if (parentPass.views[viewIndex].clearDepth) GPULoadOp.clear else GPULoadOp.load,
            depthStoreOp = GPUStoreOp.store,
            depthClearValue = 1f
        )
        return RenderAttachments(colors, depth)
    }

    private inner class RenderAttachment(val texture: Texture2d, val isDepth: Boolean, val name: String) : BaseReleasable() {
        var descriptor: GPUTextureDescriptor
        var gpuTexture: WgpuTextureResource
        val mipViews = mutableListOf<GPUTextureView>()

        init {
            val (desc, tex) = createTexture(parentPass.width, parentPass.height, GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT)
            descriptor = desc
            gpuTexture = tex

            releaseWith(this@WgpuOffscreenRenderPass2d)
            texture.loadedTexture = WgpuLoadedTexture(gpuTexture)
            texture.loadingState = Texture.LoadingState.LOADED
            createViews()
        }

        fun applySize(width: Int, height: Int) {
            val (desc, tex) = createTexture(width, height, GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT or copySrcFlag)
            descriptor = desc
            gpuTexture = tex

            texture.loadedTexture?.release()
            texture.loadedTexture = WgpuLoadedTexture(gpuTexture)
            createViews()
        }

        private fun createViews() {
            mipViews.clear()
            for (i in 0 until parentPass.mipLevels) {
                mipViews += gpuTexture.gpuTexture.createView(baseMipLevel = i, mipLevelCount = 1)
            }
        }

        fun createTexture(
            width: Int,
            height: Int,
            usage: Int,
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