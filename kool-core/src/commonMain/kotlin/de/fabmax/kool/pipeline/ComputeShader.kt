package de.fabmax.kool.pipeline

/**
 * Base class for compute shaders.
 */
abstract class ComputeShader : ShaderBase() {
    val onPipelineCreated = mutableListOf<ComputePipelineCreatedListener>()

    abstract fun onPipelineSetup(builder: ComputePipeline.Builder, updateEvent: RenderPass.UpdateEvent)

    open fun onComputePipelineCreated(pipeline: ComputePipeline, updateEvent: RenderPass.UpdateEvent) {
        pipelineCreated(pipeline)
        onPipelineCreated.forEach { it.onPipelineCreated(pipeline, updateEvent) }
    }

    fun interface ComputePipelineCreatedListener {
        fun onPipelineCreated(pipeline: ComputePipeline, updateEvent: RenderPass.UpdateEvent)
    }
}