package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.OffscreenPassCube
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Color
import org.lwjgl.vulkan.KHRDynamicRendering.vkCmdEndRenderingKHR
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VK12.*
import org.lwjgl.vulkan.VkRenderingAttachmentInfo
import org.lwjgl.vulkan.VkRenderingInfo

abstract class RenderPassVk(
    val hasDepth: Boolean,
    val numSamples: Int,
    val backend: RenderBackendVk
) : BaseReleasable() {

    abstract val colorTargetFormats: List<Int>
    val numColorAttachments: Int get() = colorTargetFormats.size

    val hasColor: Boolean get() = colorTargetFormats.isNotEmpty()

    protected val device: Device get() = backend.device

    private val timeQuery = Timer(backend.timestampQueryPool) { }

    protected fun render(renderPass: RenderPass, passEncoderState: PassEncoderState) {
        if (renderPass.isProfileTimes) {
            if (timeQuery.isComplete) {
                renderPass.tGpu = timeQuery.latestResult
            }
            timeQuery.begin(passEncoderState.commandBuffer)
        }

        when (val mode = renderPass.mipMode) {
            is RenderPass.MipMode.Render -> {
                val numLevels = mode.getRenderMipLevels(renderPass.size)
                if (mode.renderOrder == RenderPass.MipMapRenderOrder.HigherResolutionFirst) {
                    for (mipLevel in 0 until numLevels) {
                        renderPass.renderMipLevel(mipLevel, passEncoderState)
                    }
                } else {
                    for (mipLevel in (numLevels-1) downTo 0) {
                        renderPass.renderMipLevel(mipLevel, passEncoderState)
                    }
                }
            }
            else -> renderPass.renderMipLevel(0, passEncoderState)
        }

        if (renderPass.mipMode == RenderPass.MipMode.Generate) {
            passEncoderState.ensureRenderPassInactive()
            generateMipLevels(passEncoderState)
        }

        var anySingleShots = false
        for (i in renderPass.frameCopies.indices) {
            passEncoderState.ensureRenderPassInactive()
            copy(renderPass.frameCopies[i], passEncoderState)
            anySingleShots = anySingleShots || renderPass.frameCopies[i].isSingleShot
        }
        if (anySingleShots) {
            renderPass.frameCopies.removeAll { it.isSingleShot }
        }
        renderPass.afterPass()

        if (renderPass.isProfileTimes) {
            timeQuery.end(passEncoderState.commandBuffer)
        }
    }

    private fun RenderPass.renderMipLevel(mipLevel: Int, passEncoderState: PassEncoderState) {
        setupMipLevel(mipLevel)

        if (this is OffscreenPassCube) {
            for (layer in views.indices) {
                passEncoderState.beginRenderPass(this, this@RenderPassVk, mipLevel, layer)
                renderView(views[layer], passEncoderState)
            }
        } else {
            passEncoderState.beginRenderPass(this, this@RenderPassVk, mipLevel)
            for (viewIndex in views.indices) {
                renderView(views[viewIndex], passEncoderState)
            }
        }
    }

    private fun renderView(view: RenderPass.View, passEncoderState: PassEncoderState) = with(passEncoderState.stack) {
        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
        view.setupView()

        val renderWidth = (view.viewport.width shr mipLevel).coerceAtLeast(1)
        val renderHeight = (view.viewport.height shr mipLevel).coerceAtLeast(1)

        val viewport = callocVkViewportN(1) {
            x(view.viewport.x.toFloat())
            y(renderHeight + view.viewport.y.toFloat())
            width(renderWidth.toFloat())
            height(-renderHeight.toFloat())
            minDepth(0f)
            maxDepth(1f)
        }
        vkCmdSetViewport(passEncoderState.commandBuffer, 0, viewport)

        val scissor = callocVkRect2DN(1) {
            offset { it.set(view.viewport.x, view.viewport.y) }
            extent { it.set(renderWidth, renderHeight) }
        }
        vkCmdSetScissor(passEncoderState.commandBuffer, 0, scissor)

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
                    copy(frameCopy, passEncoderState)
                    passEncoderState.beginRenderPass(rp, this@RenderPassVk, mipLevel, layer, forceLoad = true)
                    anySingleShots = anySingleShots || frameCopy.isSingleShot
                    nextFrameCopy = view.frameCopies.getOrNull(nextFrameCopyI++)
                }
            }

            val isCmdValid = cmd.isActive && cmd.geometry.numIndices > 0
            val bindSuccessful = backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)
            if (isCmdValid && bindSuccessful) {
                val insts = cmd.instances
                if (insts == null) {
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
                    vkCmdDrawIndexed(passEncoderState.commandBuffer, cmd.geometry.numIndices, 1, 0, 0, 0)
                } else if (insts.numInstances > 0) {
                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
                    vkCmdDrawIndexed(passEncoderState.commandBuffer, cmd.geometry.numIndices, insts.numInstances, 0, 0, 0)
                }
            }
        }

        nextFrameCopy?.let {
            val rp = passEncoderState.renderPass
            passEncoderState.ensureRenderPassInactive()
            copy(it, passEncoderState)
            passEncoderState.beginRenderPass(rp, this@RenderPassVk, mipLevel, layer, forceLoad = true)
            anySingleShots = anySingleShots || it.isSingleShot
        }
        if (anySingleShots) {
            view.frameCopies.removeAll { it.isSingleShot }
        }
    }

    abstract fun beginRenderPass(passEncoderState: PassEncoderState, forceLoad: Boolean)

    open fun endRenderPass(passEncoderState: PassEncoderState) {
        vkCmdEndRenderingKHR(passEncoderState.commandBuffer)
    }

    protected abstract fun generateMipLevels(passEncoderState: PassEncoderState)

    protected abstract fun copy(frameCopy: FrameCopy, passEncoderState: PassEncoderState)

    fun setupRenderingInfo(
        width: Int,
        height: Int,

        colorImageViews: List<VkImageView> = emptyList(),
        clearColors: List<Color> = emptyList(),
        colorLoadOp: Int = VK_ATTACHMENT_LOAD_OP_DONT_CARE,
        colorStoreOp: Int = VK_ATTACHMENT_STORE_OP_DONT_CARE,
        resolveColorViews: List<VkImageView> = emptyList(),

        depthImageView: VkImageView? = null,
        isReverseDepth: Boolean = false,
        depthLoadOp: Int = colorLoadOp,
        depthStoreOp: Int = colorStoreOp,
        resolveDepthView: VkImageView? = null
    ): VkRenderingInfo {
        val colorInfo = if (colorImageViews.isEmpty()) null else colorAttachmentInfo
        colorAttachmentInfo.limit(colorImageViews.size)
        for (i in colorImageViews.indices) {
            colorAttachmentInfo[i].apply {
                imageView(colorImageViews[i].handle)
                imageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                loadOp(colorLoadOp)
                storeOp(colorStoreOp)
                if (colorLoadOp == VK_ATTACHMENT_LOAD_OP_CLEAR) {
                    clearValue { it.setColor(clearColors.getOrNull(i) ?: Color.BLACK) }
                }
                val resolveView = resolveColorViews.getOrNull(i)
                if (resolveView != null) {
                    resolveMode(VK_RESOLVE_MODE_AVERAGE_BIT)
                    resolveImageView(resolveView.handle)
                    resolveImageLayout(VK_IMAGE_LAYOUT_COLOR_ATTACHMENT_OPTIMAL)
                } else {
                    resolveMode(VK_RESOLVE_MODE_NONE)
                    resolveImageView(0L)
                    resolveImageLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                }
            }
        }

        val depthInfo = depthImageView?.let {
            depthAttachmentInfo.apply {
                imageView(depthImageView.handle)
                imageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                resolveMode(VK_RESOLVE_MODE_NONE)
                loadOp(depthLoadOp)
                storeOp(depthStoreOp)
                if (depthLoadOp == VK_ATTACHMENT_LOAD_OP_CLEAR) {
                    clearValue { cv -> cv.depthStencil { it.depth(if (isReverseDepth) 0f else 1f) } }
                }
                if (resolveDepthView != null) {
                    resolveMode(VK_RESOLVE_MODE_SAMPLE_ZERO_BIT)
                    resolveImageView(resolveDepthView.handle)
                    resolveImageLayout(VK_IMAGE_LAYOUT_DEPTH_STENCIL_ATTACHMENT_OPTIMAL)
                } else {
                    resolveMode(VK_RESOLVE_MODE_NONE)
                    resolveImageView(0L)
                    resolveImageLayout(VK_IMAGE_LAYOUT_UNDEFINED)
                }
            }
        }
        return renderingInfo.apply {
            renderArea { ra ->
                ra.offset { it.set(0, 0) }
                ra.extent { it.set(width, height) }
            }
            layerCount(1)
            pColorAttachments(colorInfo)
            pDepthAttachment(depthInfo)
        }
    }

    companion object {
        private val colorAttachmentInfo = VkRenderingAttachmentInfo.calloc(16).also {
            repeat(16) { i -> it[i].`sType$Default`() }
        }
        private val depthAttachmentInfo = VkRenderingAttachmentInfo.calloc().apply { `sType$Default`() }
        private val renderingInfo = VkRenderingInfo.calloc().apply { `sType$Default`() }
    }
}