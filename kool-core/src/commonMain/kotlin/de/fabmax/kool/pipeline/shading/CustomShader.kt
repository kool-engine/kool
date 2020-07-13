package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Shader
import de.fabmax.kool.pipeline.ShaderCode

class CustomShader(val shaderCode: ShaderCode) : Shader() {
    override fun generateCode(pipeline: Pipeline, ctx: KoolContext) = shaderCode

    override fun onPipelineCreated(pipeline: Pipeline) { }
}