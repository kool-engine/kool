package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.ComputePipeline

class VkComputePipeline(
    val computePipeline: ComputePipeline,
    private val computeShaderModule: VkShaderModule,
    backend: RenderBackendVk,
) : VkPipeline(computePipeline, backend) {

    override fun removeUser(user: Any) {
        TODO("Not yet implemented")
    }
}