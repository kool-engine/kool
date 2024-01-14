package de.fabmax.kool.pipeline

abstract class PipelineBinding(val bindingName: String, private val shader: ShaderBase<*>) {
    protected var bindGroup = -1
    protected var bindingIndex = -1

    protected val bindGroupData: BindGroupData?
        get() = shader.createdPipeline?.pipelineData

    val isValid: Boolean
        get() = bindGroup >= 0 && bindingIndex >= 0

    open fun setup(pipeline: PipelineBase) {
        bindGroup = -1
        bindingIndex = -1
    }
}
