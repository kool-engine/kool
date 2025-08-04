package de.fabmax.kool.modules.ksl

import de.fabmax.kool.pipeline.ComputePass
import de.fabmax.kool.pipeline.DrawCommand
import de.fabmax.kool.pipeline.ShaderBase

interface KslShaderListener {
    fun onShaderCreated(shader: ShaderBase<*>) { }
    fun onUpdateDrawData(cmd: DrawCommand) { }
    fun onUpdateComputeData(computePass: ComputePass) { }
}