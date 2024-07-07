package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.releaseWith

abstract class WgpuRenderPass<T: RenderPass>(
    val depthFormat: GPUTextureFormat?,
    val numSamples: Int,
    val backend: RenderBackendWebGpu
) : BaseReleasable() {

    private var beginTimestamp: WgpuTimestamps.QuerySlot? = null
    private var endTimestamp: WgpuTimestamps.QuerySlot? = null

    private val passEncoderState = RenderPassEncoderState(this)

    protected val device: GPUDevice
        get() = backend.device

    abstract val colorTargetFormats: List<GPUTextureFormat>

    protected fun render(renderPass: T, encoder: GPUCommandEncoder) {
        var timestampWrites: GPURenderPassTimestampWrites? = null
        if (renderPass.isProfileTimes) {
            createTimestampQueries()
            val begin = beginTimestamp
            val end = endTimestamp
            if (begin != null && end != null && begin.isReady && end.isReady) {
                renderPass.tGpu = (end.latestResult - begin.latestResult) / 1e6
                timestampWrites = GPURenderPassTimestampWrites(backend.timestampQuery.querySet, begin.index, end.index)
            }
        }

        for (mipLevel in 0 until renderPass.numRenderMipLevels) {
            renderPass.setupMipLevel(mipLevel)

            when (renderPass.viewRenderMode) {
                RenderPass.ViewRenderMode.SINGLE_RENDER_PASS -> {
                    passEncoderState.setup(encoder, renderPass)
                    passEncoderState.begin(0, mipLevel, timestampWrites)
                    for (viewIndex in renderPass.views.indices) {
                        renderView(viewIndex, mipLevel, passEncoderState)
                    }
                    passEncoderState.end()
                }

                RenderPass.ViewRenderMode.MULTI_RENDER_PASS -> {
                    for (viewIndex in renderPass.views.indices) {
                        passEncoderState.setup(encoder, renderPass)
                        passEncoderState.begin(viewIndex, mipLevel)
                        renderView(viewIndex, mipLevel, passEncoderState)
                        passEncoderState.end()
                    }
                }
            }
        }

        if (renderPass.mipMode == RenderPass.MipMode.Generate) {
            generateMipLevels(encoder)
        }

        var anySingleShots = false
        for (i in renderPass.frameCopies.indices) {
            copy(renderPass.frameCopies[i], encoder)
            anySingleShots = anySingleShots || renderPass.frameCopies[i].isSingleShot
        }
        if (anySingleShots) {
            renderPass.frameCopies.removeAll { it.isSingleShot }
        }
        renderPass.afterDraw()
    }

    private fun createTimestampQueries() {
        if (beginTimestamp == null) {
            beginTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
        if (endTimestamp == null) {
            endTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
    }

    protected abstract fun generateMipLevels(encoder: GPUCommandEncoder)

    protected abstract fun copy(frameCopy: FrameCopy, encoder: GPUCommandEncoder)

    private fun renderView(viewIndex: Int, mipLevel: Int, passEncoderState: RenderPassEncoderState<*>) {
        val view = passEncoderState.renderPass.views[viewIndex]

        passEncoderState.renderPass.setupView(viewIndex)

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
                    passEncoderState.end()
                    copy(frameCopy, passEncoderState.encoder)
                    passEncoderState.begin(viewIndex, mipLevel, forceLoad = true)
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
            passEncoderState.end()
            copy(it, passEncoderState.encoder)
            passEncoderState.begin(viewIndex, mipLevel, forceLoad = true)
            anySingleShots = anySingleShots || it.isSingleShot
        }
        if (anySingleShots) {
            view.frameCopies.removeAll { it.isSingleShot }
        }
    }

    abstract fun getRenderAttachments(renderPass: T, viewIndex: Int, mipLevel: Int, forceLoad: Boolean): RenderAttachments

    data class RenderAttachments(
        val colorAttachments: Array<GPURenderPassColorAttachment>,
        val depthAttachment: GPURenderPassDepthStencilAttachment?
    )
}
