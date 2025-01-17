package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.OffscreenRenderPassCube
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10.*

abstract class RenderPassVk(
    val backend: RenderBackendVk,
    val colorFormats: List<Int>
) : BaseReleasable() {

    abstract val vkRenderPass: VkRenderPass
    abstract val numSamples: Int
    val numColorAttachments: Int get() = colorFormats.size

    val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    val device: Device get() = backend.device

    abstract fun beginRenderPass(passEncoderState: RenderPassEncoderState)

    protected fun render(renderPass: RenderPass, passEncoderState: RenderPassEncoderState) {
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

//        if (renderPass.mipMode == RenderPass.MipMode.Generate) {
//            generateMipLevels(commandBuffer)
//        }

//        var anySingleShots = false
//        for (i in renderPass.frameCopies.indices) {
//            copy(renderPass.frameCopies[i], encoder)
//            anySingleShots = anySingleShots || renderPass.frameCopies[i].isSingleShot
//        }
//        if (anySingleShots) {
//            renderPass.frameCopies.removeAll { it.isSingleShot }
//        }
        renderPass.afterDraw()
    }

    private fun RenderPass.renderMipLevel(mipLevel: Int, passEncoderState: RenderPassEncoderState) {
        setupMipLevel(mipLevel)

        if (this is OffscreenRenderPassCube) {
            for (layer in views.indices) {
                passEncoderState.beginRenderPass(this, this@RenderPassVk, mipLevel, layer)
                renderView(layer, passEncoderState)
            }
        } else {
            passEncoderState.beginRenderPass(this, this@RenderPassVk, mipLevel)
            for (viewIndex in views.indices) {
                renderView(viewIndex, passEncoderState)
            }
        }
    }

    private fun renderView(viewIndex: Int, passEncoderState: RenderPassEncoderState) = with(passEncoderState.stack) {
        val mipLevel = passEncoderState.mipLevel
        val layer = passEncoderState.layer
        val view = passEncoderState.renderPass.views[viewIndex]
        view.setupView()

        val renderWidth = view.viewport.width.coerceAtLeast(1)
        val renderHeight = view.viewport.height.coerceAtLeast(1)

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
                    logE { "TODO: copy view" }
                    //copy(frameCopy, passEncoderState.encoder)
                    passEncoderState.beginRenderPass(rp, this@RenderPassVk, mipLevel, layer, forceLoad = true)
                    anySingleShots = anySingleShots || frameCopy.isSingleShot
                    nextFrameCopy = view.frameCopies.getOrNull(nextFrameCopyI++)
                }
            }

            val isCmdValid = cmd.isActive && cmd.geometry.numIndices > 0
            if (isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)) {
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
            logE { "TODO: copy frame" }
            //passEncoderState.begin(viewIndex, mipLevel, forceLoad = true)
            passEncoderState.beginRenderPass(rp, this@RenderPassVk, mipLevel, layer, forceLoad = true)
            anySingleShots = anySingleShots || it.isSingleShot
        }
        if (anySingleShots) {
            view.frameCopies.removeAll { it.isSingleShot }
        }
    }

    protected abstract fun copy(frameCopy: FrameCopy, encoder: RenderPassEncoderState)

    fun getTexFormat(attachment: Int): TexFormat {
        return when(colorFormats[attachment]) {
            VK_FORMAT_R8_UNORM -> TexFormat.R
            VK_FORMAT_R8G8_UNORM -> TexFormat.RG
            VK_FORMAT_R8G8B8A8_UNORM -> TexFormat.RGBA

            VK_FORMAT_R32_SINT -> TexFormat.R_I32
            VK_FORMAT_R32G32_SINT -> TexFormat.RG_I32
            VK_FORMAT_R32G32B32A32_SINT -> TexFormat.RGBA_I32

            VK_FORMAT_R32_UINT -> TexFormat.R_U32
            VK_FORMAT_R32G32_UINT -> TexFormat.RG_U32
            VK_FORMAT_R32G32B32A32_UINT -> TexFormat.RGBA_U32

            VK_FORMAT_R16_SFLOAT -> TexFormat.R_F16
            VK_FORMAT_R16G16_SFLOAT -> TexFormat.RG_F16
            VK_FORMAT_R16G16B16A16_SFLOAT -> TexFormat.RGBA_F16

            VK_FORMAT_R32_SFLOAT -> TexFormat.R_F32
            VK_FORMAT_R32G32_SFLOAT -> TexFormat.RG_F32
            VK_FORMAT_R32G32B32A32_SFLOAT -> TexFormat.RGBA_F32

            else -> error("Unsupported format: ${colorFormats[attachment]}")
        }
    }
}