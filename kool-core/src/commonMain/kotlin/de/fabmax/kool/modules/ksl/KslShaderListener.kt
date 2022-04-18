package de.fabmax.kool.modules.ksl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

interface KslShaderListener {
    fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) { }
    fun onUpdate(cmd: DrawCommand) { }
}