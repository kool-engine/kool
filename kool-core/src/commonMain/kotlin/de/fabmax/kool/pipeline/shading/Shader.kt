package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode

abstract class Shader {

    abstract val shaderCode: ShaderCode

    open fun onPipelineCreated(pipeline: Pipeline) { }

}
