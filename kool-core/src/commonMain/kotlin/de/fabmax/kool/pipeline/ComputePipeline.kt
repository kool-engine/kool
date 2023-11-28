package de.fabmax.kool.pipeline

/**
 * Compute pipeline: Compute shader + data layout.
 */
class ComputePipeline private constructor(builder: Builder) : PipelineBase(builder) {

    val shaderCode: ComputeShaderCode
    val onUpdate = mutableListOf<(RenderPass.UpdateEvent) -> Unit>()

    init {
        shaderCode = builder.shaderCodeGenerator(this)
        hash += shaderCode.hash
    }

    class Builder : PipelineBase.Builder() {
        lateinit var shaderCodeGenerator: (ComputePipeline) -> ComputeShaderCode

        override fun create(): ComputePipeline {
            return ComputePipeline(this)
        }
    }
}