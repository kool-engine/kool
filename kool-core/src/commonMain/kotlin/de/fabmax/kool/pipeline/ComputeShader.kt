package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Mesh

/**
 * Base class for compute shaders.
 */
abstract class ComputeShader : ShaderBase() {

    open fun onComputePipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        TODO()
    }

    open fun onComputePipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        TODO()
    }
}