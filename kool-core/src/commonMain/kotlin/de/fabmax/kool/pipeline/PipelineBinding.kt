package de.fabmax.kool.pipeline

abstract class PipelineBinding(val bindingName: String) {
    protected var bindGroup = -1
    protected var bindingIndex = -1

    val isValid: Boolean
        get() = bindGroup >= 0 && bindingIndex >= 0

    open fun setup(pipeline: PipelineBase) {
        bindGroup = -1
        bindingIndex = -1
    }
}
