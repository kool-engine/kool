package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith
import kotlin.time.Duration.Companion.nanoseconds

abstract class WgpuRenderPass(
    val depthFormat: GPUTextureFormat?,
    numSamples: Int,
    val backend: RenderBackendWebGpu
) : BaseReleasable() {

    val numSamples = numSamples.coerceAtMost(4)
    val isMultiSampled: Boolean get() = numSamples > 1

    private var beginTimestamp: WgpuTimestamps.QuerySlot? = null
    private var endTimestamp: WgpuTimestamps.QuerySlot? = null

    protected val device: GPUDevice
        get() = backend.device

    abstract val colorTargetFormats: List<GPUTextureFormat>

    protected fun render(renderPass: RenderPass, passEncoderState: RenderPassEncoderState) {
        var timestampWrites: GPURenderPassTimestampWrites? = null
        if (renderPass.isProfileTimes) {
            createTimestampQueries()
            val begin = beginTimestamp
            val end = endTimestamp
            if (begin != null && end != null && begin.isReady && end.isReady) {
                renderPass.tGpu = (end.latestResult - begin.latestResult).nanoseconds
                timestampWrites = backend.timestampQuery.getQuerySet()?.let { GPURenderPassTimestampWrites(it, begin.index, end.index) }
            }
        }

        when (val mode = renderPass.mipMode) {
            is RenderPass.MipMode.Render -> {
                val numLevels = mode.getRenderMipLevels(renderPass.size)
                if (mode.renderOrder == RenderPass.MipMapRenderOrder.HigherResolutionFirst) {
                    for (mipLevel in 0 until numLevels) {
                        renderPass.renderMipLevel(mipLevel, passEncoderState, timestampWrites)
                    }
                } else {
                    for (mipLevel in (numLevels-1) downTo 0) {
                        renderPass.renderMipLevel(mipLevel, passEncoderState, timestampWrites)
                    }
                }
            }
            else -> renderPass.renderMipLevel(0, passEncoderState, timestampWrites)
        }

        if (renderPass.mipMode == RenderPass.MipMode.Generate) {
            passEncoderState.ensureRenderPassInactive()
            generateMipLevels(passEncoderState.encoder)
        }

        var anySingleShots = false
        for (i in renderPass.frameCopies.indices) {
            passEncoderState.ensureRenderPassInactive()
            copy(renderPass.frameCopies[i], passEncoderState.encoder)
            anySingleShots = anySingleShots || renderPass.frameCopies[i].isSingleShot
        }
        if (anySingleShots) {
            renderPass.frameCopies.removeAll { it.isSingleShot }
        }
    }

    private fun RenderPass.renderMipLevel(mipLevel: Int, passEncoderState: RenderPassEncoderState, timestampWrites: GPURenderPassTimestampWrites?) {
        setupMipLevel(mipLevel)

        if (this is OffscreenPassCube) {
            for (layer in views.indices) {
                passEncoderState.beginRenderPass(this, this@WgpuRenderPass, mipLevel, layer, timestampWrites)
                renderView(layer, passEncoderState)
            }
        } else {
            passEncoderState.beginRenderPass(this, this@WgpuRenderPass, mipLevel, timestampWrites = timestampWrites)
            for (viewIndex in views.indices) {
                renderView(viewIndex, passEncoderState)
            }
        }
    }

    private fun createTimestampQueries() {
        if (beginTimestamp == null) {
            beginTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
        if (endTimestamp == null) {
            endTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
    }

    private fun renderView(viewIndex: Int, passEncoderState: RenderPassEncoderState) {
        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
        val view = passEncoderState.renderPass.views[viewIndex]
        view.setupView()

        val viewport = view.viewport
        val x = (viewport.x shr mipLevel).toFloat()
        val y = (viewport.y shr mipLevel).toFloat()
        val w = (viewport.width shr mipLevel).toFloat()
        val h = (viewport.height shr mipLevel).toFloat()
        passEncoderState.passEncoder.setViewport(x, y, w, h, 0f, 1f)

        // only do copy when last mip-level is rendered
        val isLastMipLevel = mipLevel == view.renderPass.numRenderMipLevels - 1
        var nextFrameCopyI = 0
        var nextFrameCopy = if (isLastMipLevel) view.frameCopies.getOrNull(nextFrameCopyI++) else null
        var anySingleShots = false

        view.drawQueue.forEach { cmd ->
            nextFrameCopy?.let { frameCopy ->
                if (cmd.drawGroupId > frameCopy.drawGroupId) {
                    val rp = passEncoderState.renderPass
                    passEncoderState.ensureRenderPassInactive()
                    copy(frameCopy, passEncoderState.encoder)
                    passEncoderState.beginRenderPass(rp, this, mipLevel, layer, forceLoad = true)
                    anySingleShots = anySingleShots || frameCopy.isSingleShot
                    nextFrameCopy = view.frameCopies.getOrNull(nextFrameCopyI++)
                }
            }

            val insts = cmd.instances
            val isCmdValid = cmd.isActive && cmd.geometry.numIndices > 0 && (insts == null || insts.numInstances > 0)
            val bindSuccessful = isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)
            if (bindSuccessful) {
                if (insts == null) {
                    passEncoderState.passEncoder.drawIndexed(cmd.geometry.numIndices)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                } else {
                    passEncoderState.passEncoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                }
            }
        }

        nextFrameCopy?.let {
            val rp = passEncoderState.renderPass
            passEncoderState.ensureRenderPassInactive()
            copy(it, passEncoderState.encoder)
            passEncoderState.beginRenderPass(rp, this, mipLevel, layer, forceLoad = true)
            anySingleShots = anySingleShots || it.isSingleShot
        }
        if (anySingleShots) {
            view.frameCopies.removeAll { it.isSingleShot }
        }
    }

    abstract fun beginRenderPass(
        passEncoderState: RenderPassEncoderState,
        forceLoad: Boolean,
        timestampWrites: GPURenderPassTimestampWrites?
    ): GPURenderPassEncoder

    open fun endRenderPass(passEncoderState: RenderPassEncoderState) {
        passEncoderState.passEncoder.end()
    }

    protected abstract fun generateMipLevels(encoder: GPUCommandEncoder)

    protected abstract fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder)

    inner class Attachments(
        val colorFormats: List<GPUTextureFormat>,
        val depthFormat: GPUTextureFormat?,
        val layers: Int,
        val isCopySrc: Boolean,
        val parentPass: RenderPass,
    ) : BaseReleasable() {
        val colorImages = colorFormats.map { format ->
            createImage(parentPass.width, parentPass.height, numSamples, format)
        }
        val depthImage = depthFormat?.let { format ->
            createImage(parentPass.width, parentPass.height, numSamples, format)
        }

        val resolveColorImages = if (!isMultiSampled) emptyList() else colorFormats.map { format ->
            createImage(parentPass.width, parentPass.height, 1, format)
        }
        val isResolveDepth = isMultiSampled && parentPass.depthAttachment is RenderPassDepthTextureAttachment<*>
        val resolveDepthImage = if (!isResolveDepth) null else depthFormat?.let { format ->
            createImage(parentPass.width, parentPass.height, 1, format)
        }

        val colorMipViews = colorImages.map { it.createMipViews() }
        val depthMipViews = depthImage?.createMipViews() ?: emptyList()

        val resolveColorMipViews = resolveColorImages.map { it.createMipViews() }
        val resolveDepthMipViews = resolveDepthImage?.createMipViews() ?: emptyList()

        val colorViewsByLayerAndMip = (0..<layers).map { layer ->
            (0..<parentPass.numRenderMipLevels).map { mipLevel ->
                (0..<colorImages.size).map { attachment ->
                    getColorView(attachment, mipLevel, layer)
                }
            }
        }

        val resolveColorViewsByLayerAndMip = (0..<layers).map { layer ->
            (0..<parentPass.numRenderMipLevels).map { mipLevel ->
                (0..<resolveColorImages.size).map { attachment ->
                    getResolveColorView(attachment, mipLevel, layer)
                }
            }
        }

        private fun createImage(width: Int, height: Int, samples: Int, format: GPUTextureFormat): WgpuTextureResource {
            val copySrcUsage = if (isCopySrc) GPUTextureUsage.COPY_SRC else 0
            val usage = GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT or copySrcUsage

            return createImageWithUsage(width, height, samples, format, usage)
        }

        private fun createImageWithUsage(width: Int, height: Int, samples: Int, format: GPUTextureFormat, usage: Int): WgpuTextureResource {
            val descriptor = GPUTextureDescriptor(
                label = parentPass.name,
                size = intArrayOf(width, height, layers),
                format = format,
                usage = usage,
                dimension = GPUTextureDimension.texture2d,
                mipLevelCount = parentPass.numTextureMipLevels,
                sampleCount = samples
            )
            return backend.createTexture(descriptor)
        }

        private fun WgpuTextureResource.createMipViews() = List<List<GPUTextureView>>(parentPass.numRenderMipLevels) { mipLevel ->
            List<GPUTextureView>(layers) { layer ->
                gpuTexture.createView(baseMipLevel = mipLevel, mipLevelCount = 1, baseArrayLayer = layer, arrayLayerCount = 1)
            }
        }

        fun getColorView(attachment: Int, mipLevel: Int, layer: Int = 0): GPUTextureView {
            return colorMipViews[attachment][mipLevel][layer]
        }

        fun getColorViews(mipLevel: Int, layer: Int = 0): List<GPUTextureView> {
            return colorViewsByLayerAndMip[layer][mipLevel]
        }

        fun getDepthView(mipLevel: Int, layer: Int = 0): GPUTextureView? {
            return depthMipViews.getOrNull(mipLevel)?.get(layer)
        }

        fun getResolveColorView(attachment: Int, mipLevel: Int, layer: Int = 0): GPUTextureView {
            return resolveColorMipViews[attachment][mipLevel][layer]
        }

        fun getResolveColorViews(mipLevel: Int, layer: Int = 0): List<GPUTextureView> {
            return resolveColorViewsByLayerAndMip[layer][mipLevel]
        }

        fun getResolveDepthView(mipLevel: Int, layer: Int = 0): GPUTextureView? {
            return resolveDepthMipViews.getOrNull(mipLevel)?.get(layer)
        }

        fun copyColorToTexture(attachment: Int, target: Texture<*>, encoder: GPUCommandEncoder) {
            val src = if (isMultiSampled) resolveColorImages[attachment] else colorImages[attachment]
            copyToTexture(target, src, colorFormats[attachment], encoder)
        }

        fun copyDepthToTexture(target: Texture<*>, encoder: GPUCommandEncoder) {
            val src = if (isMultiSampled) resolveDepthImage else depthImage
            copyToTexture(target, src!!, depthFormat!!, encoder)
        }

        private fun copyToTexture(target: Texture<*>, src: WgpuTextureResource, format: GPUTextureFormat, encoder: GPUCommandEncoder) {
            var copyDst = (target.gpuTexture as WgpuTextureResource?)
            if (copyDst == null || copyDst.width != parentPass.width || copyDst.height != parentPass.height) {
                copyDst?.release()
                copyDst = createImageWithUsage(
                    width = parentPass.width,
                    height = parentPass.height,
                    samples = 1,
                    format = format,
                    usage = GPUTextureUsage.COPY_DST or GPUTextureUsage.TEXTURE_BINDING or GPUTextureUsage.RENDER_ATTACHMENT,
                )
                target.gpuTexture = copyDst
            }
            backend.textureLoader.copyTexture2d(src.gpuTexture, copyDst.gpuTexture, parentPass.numTextureMipLevels, encoder)
        }

        override fun release() {
            super.release()
            colorImages.forEach { it.release() }
            depthImage?.release()
            resolveColorImages.forEach { it.release() }
            resolveDepthImage?.release()
        }
    }

}
