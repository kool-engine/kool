package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.KHRSynchronization2.vkCmdPipelineBarrier2KHR
import org.lwjgl.vulkan.VK10.vkCmdDispatch
import org.lwjgl.vulkan.VK13.*
import org.lwjgl.vulkan.VkMemoryBarrier2

class ComputePassVk(val parentPass: ComputePass, val backend: RenderBackendVk) :
    BaseReleasable(),
    ComputePassImpl
{
    private val timeQuery = Timer(backend.timestampQueryPool) { }

    fun dispatch(passEncoderState: PassEncoderState) {
        val maxNumGroups = backend.features.maxComputeWorkGroupsPerDimension
        val maxWorkGroupSz = backend.features.maxComputeWorkGroupSize
        val maxInvocations = backend.features.maxComputeInvocationsPerWorkgroup

        if (parentPass.isProfileTimes) {
            if (timeQuery.isComplete) {
                parentPass.tGpu = timeQuery.latestResult
            }
            timeQuery.begin(passEncoderState.commandBuffer)
        }

        val tasks = parentPass.tasks
        passEncoderState.ensureRenderPassInactive()
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isEnabled) {
                val pipeline = tasks[i].pipeline

                var isInLimits = true
                val groupSize = pipeline.workGroupSize
                if (task.numGroups.x > maxNumGroups.x || task.numGroups.y > maxNumGroups.y || task.numGroups.z > maxNumGroups.z) {
                    logE { "Maximum compute shader workgroup count exceeded: max count = $maxNumGroups, requested count: ${task.numGroups}" }
                    isInLimits = false
                }
                if (groupSize.x > maxWorkGroupSz.x || groupSize.y > maxWorkGroupSz.y || groupSize.z > maxWorkGroupSz.z) {
                    logE { "Maximum compute shader workgroup size exceeded: max size = $maxWorkGroupSz, requested size: $groupSize" }
                    isInLimits = false
                }
                if (groupSize.x * groupSize.y * groupSize.z > maxInvocations) {
                    logE { "Maximum compute shader workgroup invocations exceeded: max invocations = $maxInvocations, " +
                            "requested invocations: ${groupSize.x} x ${groupSize.y} x ${groupSize.z} = ${groupSize.x * groupSize.y * groupSize.z}" }
                    isInLimits = false
                }

                if (isInLimits) {
                    task.beforeDispatch()
                    if (backend.pipelineManager.bindComputePipeline(task, passEncoderState)) {
                        vkCmdDispatch(passEncoderState.commandBuffer, task.numGroups.x, task.numGroups.y, task.numGroups.z)

                        with(passEncoderState.stack) {
                            val dependecy = callocVkDependencyInfo {
                                val barrier = VkMemoryBarrier2.calloc(1)
                                barrier.`sType$Default`()
                                barrier.srcAccessMask(VK_ACCESS_2_MEMORY_WRITE_BIT)
                                barrier.srcStageMask(VK_PIPELINE_STAGE_2_COMPUTE_SHADER_BIT)
                                barrier.dstAccessMask(VK_ACCESS_2_MEMORY_READ_BIT)
                                barrier.dstStageMask(
                                    VK_PIPELINE_STAGE_2_COMPUTE_SHADER_BIT or
                                    VK_PIPELINE_STAGE_2_PRE_RASTERIZATION_SHADERS_BIT or
                                    VK_PIPELINE_STAGE_2_ALL_GRAPHICS_BIT or
                                    VK_PIPELINE_STAGE_2_ALL_TRANSFER_BIT
                                )
                                pMemoryBarriers(barrier)
                            }
                            vkCmdPipelineBarrier2KHR(passEncoderState.commandBuffer, dependecy)
                        }

                        task.afterDispatch()
                    }
                }
            }
        }

        if (parentPass.isProfileTimes) {
            timeQuery.end(passEncoderState.commandBuffer)
        }
    }
}