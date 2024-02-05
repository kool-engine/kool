package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.ComputePipeline
import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.pipeline.PipelineBackend
import de.fabmax.kool.util.BaseReleasable

class WgpuComputePipeline(
    val computePipeline: ComputePipeline,
    private val computeShaderModule: GPUShaderModule,
    private val backend: RenderBackendWebGpu,
): BaseReleasable(), PipelineBackend {

    override fun removeUser(user: Any) {
        TODO("WgpuComputePipeline.removeUser")
    }

    fun bind(task: ComputeRenderPass.Task): Boolean {
        TODO("WgpuComputePipeline.bind()")
    }
}