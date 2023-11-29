package de.fabmax.kool.pipeline

import de.fabmax.kool.math.MutableVec3i
import de.fabmax.kool.math.Vec3i

/**
 * Compute pipeline: Compute shader + data layout.
 */
class ComputePipeline private constructor(builder: Builder) : PipelineBase(builder) {

    val shaderCode: ComputeShaderCode
    val workGroupSize: Vec3i = Vec3i(builder.workGroupSize)
    val onUpdate = mutableListOf<(ComputeRenderPass) -> Unit>()

    init {
        shaderCode = builder.shaderCodeGenerator(this)
        hash += shaderCode.hash
    }

    class Builder : PipelineBase.Builder() {
        val workGroupSize = MutableVec3i(1, 1, 1)
        lateinit var shaderCodeGenerator: (ComputePipeline) -> ComputeShaderCode

        override fun create(): ComputePipeline {
            return ComputePipeline(this)
        }
    }
}