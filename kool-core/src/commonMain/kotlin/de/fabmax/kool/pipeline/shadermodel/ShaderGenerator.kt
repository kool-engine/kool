package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode

abstract class ShaderGenerator {

    abstract fun generateShader(model: ShaderModel, pipeline: Pipeline, ctx: KoolContext): ShaderCode

}