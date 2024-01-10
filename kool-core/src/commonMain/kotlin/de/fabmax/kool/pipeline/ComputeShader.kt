package de.fabmax.kool.pipeline

/**
 * Base class for compute shaders.
 */
abstract class ComputeShader(name: String) : ShaderBase<ComputePipeline>(name) {
    val onPipelineCreated = mutableListOf<ComputePipelineCreatedListener>()

    fun getOrCreatePipeline(computePass: ComputeRenderPass): ComputePipeline {
        var pipeline = createdPipeline
        if (pipeline == null) {
            val pipelineBuilder = ComputePipeline.Builder()
            onPipelineSetup(pipelineBuilder, computePass)
            pipeline = pipelineBuilder.create()
            onComputePipelineCreated(pipeline, computePass)
        }
        return pipeline
    }

    abstract fun onPipelineSetup(builder: ComputePipeline.Builder, computePass: ComputeRenderPass)

    open fun onComputePipelineCreated(pipeline: ComputePipeline, computePass: ComputeRenderPass) {
        pipelineCreated(pipeline)
        onPipelineCreated.forEach { it.onPipelineCreated(pipeline, computePass) }
    }

    fun interface ComputePipelineCreatedListener {
        fun onPipelineCreated(pipeline: ComputePipeline, computePass: ComputeRenderPass)
    }
}