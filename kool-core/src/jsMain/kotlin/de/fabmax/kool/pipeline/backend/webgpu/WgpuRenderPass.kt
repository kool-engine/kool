package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.OffscreenPassCube
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith
import kotlin.time.Duration.Companion.nanoseconds

abstract class WgpuRenderPass(
    val depthFormat: GPUTextureFormat?,
    val numSamples: Int,
    val backend: RenderBackendWebGpu
) : BaseReleasable() {

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
                timestampWrites = GPURenderPassTimestampWrites(backend.timestampQuery.querySet, begin.index, end.index)
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
        renderPass.afterPass()
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

            val isCmdValid = cmd.isActive && cmd.geometry.numIndices > 0
            if (isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)) {
                val insts = cmd.instances
                if (insts == null) {
                    passEncoderState.passEncoder.drawIndexed(cmd.geometry.numIndices)
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                } else if (insts.numInstances > 0) {
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

    protected abstract fun generateMipLevels(encoder: GPUCommandEncoder)

    protected abstract fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder)
}
