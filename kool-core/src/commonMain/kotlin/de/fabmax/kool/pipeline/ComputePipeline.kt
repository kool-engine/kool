package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec3i

/**
 * Compute pipeline: Compute shader + data layout.
 */
class ComputePipeline(
    name: String,
    bindGroupLayouts: List<BindGroupLayout>,
    val workGroupSize: Vec3i,
    shaderCodeGenerator: (ComputePipeline) -> ComputeShaderCode
) : PipelineBase(name, bindGroupLayouts) {

    override val shaderCode: ComputeShaderCode = shaderCodeGenerator(this)
    val onUpdate = mutableListOf<(ComputeRenderPass) -> Unit>()

    init {
        hash += shaderCode.hash
    }
}