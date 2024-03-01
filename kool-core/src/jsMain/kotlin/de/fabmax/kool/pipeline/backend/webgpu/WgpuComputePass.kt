package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE

class WgpuComputePass(val parentPass: ComputeRenderPass, val backend: RenderBackendWebGpu) :
    BaseReleasable(),
    ComputePassImpl
{
    private val computePassEncoderState = ComputePassEncoderState()

    fun dispatch(encoder: GPUCommandEncoder) {
        val tasks = parentPass.tasks

        val maxNumGroups = backend.device.limits.maxComputeWorkgroupsPerDimension
        val maxWorkGroupSzX = backend.device.limits.maxComputeWorkgroupSizeX
        val maxWorkGroupSzY = backend.device.limits.maxComputeWorkgroupSizeY
        val maxWorkGroupSzZ = backend.device.limits.maxComputeWorkgroupSizeZ
        val maxInvocations = backend.device.limits.maxComputeInvocationsPerWorkgroup

        computePassEncoderState.setup(encoder, encoder.beginComputePass())
        for (i in tasks.indices) {
            val task = tasks[i]
            if (task.isEnabled) {
                val pipeline = tasks[i].pipeline

                var isInLimits = true

                val groupSize = pipeline.workGroupSize
                if (task.numGroups.x > maxNumGroups || task.numGroups.y > maxNumGroups || task.numGroups.z > maxNumGroups) {
                    logE { "Maximum compute shader workgroup count exceeded: max count = $maxNumGroups, requested count: (${task.numGroups.x}, ${task.numGroups.y}, ${task.numGroups.z})" }
                    isInLimits = false
                }
                if (groupSize.x > maxWorkGroupSzX || groupSize.y > maxWorkGroupSzY || groupSize.z > maxWorkGroupSzZ) {
                    logE { "Maximum compute shader workgroup size exceeded: max size = ($maxWorkGroupSzX, $maxWorkGroupSzY, $maxWorkGroupSzZ), requested size: $groupSize" }
                    isInLimits = false
                }
                if (groupSize.x * groupSize.y * groupSize.z > maxInvocations) {
                    logE { "Maximum compute shader workgroup invocations exceeded: max invocations = $maxInvocations, " +
                            "requested invocations: ${groupSize.x} x ${groupSize.y} x ${groupSize.z} = ${groupSize.x * groupSize.y * groupSize.z}" }
                    isInLimits = false
                }

                if (isInLimits) {
                    task.beforeDispatch()
                    if (backend.pipelineManager.bindComputePipeline(task, computePassEncoderState)) {
                        computePassEncoderState.passEncoder.dispatchWorkgroups(task.numGroups.x, task.numGroups.y, task.numGroups.z)
                        task.afterDispatch()
                    }
                }
            }
        }
        computePassEncoderState.end()
    }
}