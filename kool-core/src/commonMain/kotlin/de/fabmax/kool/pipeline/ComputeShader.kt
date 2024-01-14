package de.fabmax.kool.pipeline

/**
 * Base class for compute shaders.
 */
abstract class ComputeShader(name: String) : ShaderBase<ComputePipeline>(name) {
    fun getOrCreatePipeline(computePass: ComputeRenderPass): ComputePipeline {
        return createdPipeline ?: createPipeline(computePass).also { pipelineCreated(it) }
    }

    protected abstract fun createPipeline(computePass: ComputeRenderPass): ComputePipeline
}