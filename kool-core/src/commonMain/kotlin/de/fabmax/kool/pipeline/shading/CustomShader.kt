package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode

class CustomShader(override val shaderCode: ShaderCode) : Shader() {
    override fun onPipelineCreated(pipeline: Pipeline) { }
}