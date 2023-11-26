package de.fabmax.kool.modules.ksl

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

interface KslShaderListener {
    fun onShaderCreated(shader: KslShader, pipeline: Pipeline, updateEvent: RenderPass.UpdateEvent) { }
    fun onUpdate(cmd: DrawCommand) { }
}