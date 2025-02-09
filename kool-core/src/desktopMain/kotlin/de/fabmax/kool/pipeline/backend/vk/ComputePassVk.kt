package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import org.lwjgl.vulkan.VK10.vkCmdDispatch

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