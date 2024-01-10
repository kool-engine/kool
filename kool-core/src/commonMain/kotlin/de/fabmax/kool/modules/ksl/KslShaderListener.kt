package de.fabmax.kool.modules.ksl

import de.fabmax.kool.pipeline.ComputeRenderPass
import de.fabmax.kool.pipeline.ShaderBase
import de.fabmax.kool.pipeline.drawqueue.DrawCommand

interface KslShaderListener {
    fun onShaderCreated(shader: ShaderBase<*>) { }
    fun onUpdate(cmd: DrawCommand) { }
    fun onComputeUpdate(computePass: ComputeRenderPass) { }
}