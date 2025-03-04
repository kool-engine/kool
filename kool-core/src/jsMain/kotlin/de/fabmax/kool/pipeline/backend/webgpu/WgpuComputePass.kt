package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.ComputePassImpl
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.releaseWith
import kotlin.time.Duration.Companion.nanoseconds

class WgpuComputePass(val parentPass: ComputePass, val backend: RenderBackendWebGpu) :
    BaseReleasable(),
    ComputePassImpl
{
    private val computePassEncoderState = ComputePassEncoderState()

    private var beginTimestamp: WgpuTimestamps.QuerySlot? = null
    private var endTimestamp: WgpuTimestamps.QuerySlot? = null

    fun dispatch(encoder: GPUCommandEncoder) {
        val maxNumGroups = backend.features.maxComputeWorkGroupsPerDimension
        val maxWorkGroupSz = backend.features.maxComputeWorkGroupSize
        val maxInvocations = backend.features.maxComputeInvocationsPerWorkgroup

        var timestampWrites: GPUComputePassTimestampWrites? = null
        if (parentPass.isProfileTimes) {
            createTimestampQueries()
            val begin = beginTimestamp
            val end = endTimestamp
            if (begin != null && end != null && begin.isReady && end.isReady) {
                parentPass.tGpu = (end.latestResult - begin.latestResult).nanoseconds
                timestampWrites = backend.timestampQuery.getQuerySet()?.let { GPUComputePassTimestampWrites(it, begin.index, end.index) }
            }
        }
        val desc = GPUComputePassDescriptor(parentPass.name, timestampWrites)

        val tasks = parentPass.tasks
        computePassEncoderState.setup(encoder, encoder.beginComputePass(desc), parentPass)
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
                    if (backend.pipelineManager.bindComputePipeline(task, computePassEncoderState)) {
                        computePassEncoderState.passEncoder.dispatchWorkgroups(task.numGroups.x, task.numGroups.y, task.numGroups.z)
                        task.afterDispatch()
                    }
                }
            }
        }
        computePassEncoderState.end()
    }

    private fun createTimestampQueries() {
        if (beginTimestamp == null) {
            beginTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
        if (endTimestamp == null) {
            endTimestamp = backend.timestampQuery.createQuery()?.also { it.releaseWith(this) }
        }
    }
}