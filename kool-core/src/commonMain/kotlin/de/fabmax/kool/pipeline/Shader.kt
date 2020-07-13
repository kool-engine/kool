package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext

abstract class Shader {

    val onCreated = mutableListOf<((Pipeline) -> Unit)>()

    abstract fun generateCode(pipeline: Pipeline, ctx: KoolContext): ShaderCode

    open fun onPipelineCreated(pipeline: Pipeline) {
        onCreated.forEach { it(pipeline) }
    }

}
