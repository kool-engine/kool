package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.BaseReleasable
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

// fixme: change maxWidth and maxHeight
abstract class RenderPassVk<T: RenderPass>(
    val backend: RenderBackendVk,
    val colorFormats: List<Int>
) : BaseReleasable() {

    //var triFrontDirection = VK_FRONT_FACE_COUNTER_CLOCKWISE

    abstract val vkRenderPass: VkRenderPass

    val nColorAttachments: Int
        get() = colorFormats.size

    val texFormat: TexFormat
        get() = getTexFormat(0)

    val physicalDevice: PhysicalDevice get() = backend.physicalDevice
    val device: Device get() = backend.device

    protected fun render(renderPass: T, commandBuffer: VkCommandBuffer, stack: MemoryStack) {
        stack.apply {

            // todo vkCmdBindPipeline()...

            // is setViewPort + setScissor really needed per bound pipeline?

            val view = renderPass.views.first()


            view.drawQueue

            // todo vkCmdDraw()
        }
    }

    private fun MemoryStack.renderView(view: RenderPass.View, mipLevel: Int, commandBuffer: VkCommandBuffer) {
        view.setupView()

        val viewport = callocVkViewportN(1) {
            x(view.viewport.x.toFloat())
            y(view.viewport.y.toFloat())
            width(view.viewport.width.toFloat())
            height(view.viewport.height.toFloat())
            minDepth(0f)
            maxDepth(1f)
        }
        vkCmdSetViewport(commandBuffer, 0, viewport)

        val scissor = callocVkRect2DN(1) {
            offset { it.set(view.viewport.x, view.viewport.y) }
            extent { it.set(view.viewport.width, view.viewport.height) }
        }
        vkCmdSetScissor(commandBuffer, 0, scissor)

//        // only do copy when last mip-level is rendered
//        val isLastMipLevel = mipLevel == view.renderPass.numRenderMipLevels - 1
//        var nextFrameCopyI = 0
//        var nextFrameCopy = if (isLastMipLevel) view.frameCopies.getOrNull(nextFrameCopyI++) else null
//        var anySingleShots = false

        view.drawQueue.forEach { cmd ->
            // nextFrameCopy?.let { ... }

            val isCmdValid = cmd.isActive && cmd.geometry.numIndices > 0
//            if (isCmdValid && backend.pipelineManager.bindDrawPipeline(cmd, passEncoderState)) {
//                val insts = cmd.instances
//                if (insts == null) {
//                    passEncoderState.passEncoder.drawIndexed(cmd.geometry.numIndices)
//                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives)
//                } else if (insts.numInstances > 0) {
//                    passEncoderState.passEncoder.drawIndexed(cmd.geometry.numIndices, insts.numInstances)
//                    BackendStats.addDrawCommands(1, cmd.geometry.numPrimitives * insts.numInstances)
//                }
//            }
        }

//        nextFrameCopy?.let {
//            passEncoderState.end()
//            copy(it, passEncoderState.encoder)
//            passEncoderState.begin(viewIndex, mipLevel, forceLoad = true)
//            anySingleShots = anySingleShots || it.isSingleShot
//        }
//        if (anySingleShots) {
//            view.frameCopies.removeAll { it.isSingleShot }
//        }
    }

    /*

    private fun renderView(viewIndex: Int, mipLevel: Int, passEncoderState: RenderPassEncoderState<*>) {
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
     */

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