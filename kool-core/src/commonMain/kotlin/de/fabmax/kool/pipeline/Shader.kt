package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.scene.Mesh

abstract class Shader {

    val onPipelineSetup = mutableListOf<(Pipeline.Builder, Mesh, KoolContext) -> Unit>()
    val onPipelineCreated = mutableListOf<(Pipeline, Mesh, KoolContext) -> Unit>()

    open fun createPipeline(mesh: Mesh, ctx: KoolContext): Pipeline {
        val pipelineBuilder = Pipeline.Builder()
        pipelineBuilder.vertexLayout.primitiveType = mesh.geometry.primitiveType
        onPipelineSetup(pipelineBuilder, mesh, ctx)
        val pipeline = pipelineBuilder.create()
        onPipelineCreated(pipeline, mesh, ctx)
        return pipeline
    }

    protected open fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        onPipelineSetup.forEach { it(builder, mesh, ctx) }
    }

    protected open fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        onPipelineCreated.forEach { it(pipeline, mesh, ctx) }
    }

}
