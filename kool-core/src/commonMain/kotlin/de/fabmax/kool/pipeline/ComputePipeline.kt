package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.util.Releasable

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

    internal var pipelineBackend: Releasable? = null

    init {
        hash += shaderCode.hash
    }

    override fun release() {
        super.release()
        pipelineBackend?.release()
    }
}