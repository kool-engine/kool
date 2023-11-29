package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.scene.Mesh

/**
 * Base class for regular shaders / materials, which can be attached to [Mesh]es in order to render them. Usually,
 * you should use [KslShader] to define your custom shaders, however, it is also possible to subclass this class
 * directly and supply arbitrary shader source code to the [Pipeline.Builder] in [onPipelineSetup]. In that case the
 * supplied shader source code has to match the rendering backend (e.g. GLSL in case an OpenGL backend is used).
 */
abstract class Shader(name: String) : ShaderBase(name) {
    val pipelineConfig = PipelineConfig()

    val onPipelineCreated = mutableListOf<PipelineCreatedListener>()

    private var createdPipeline: Pipeline? = null

    fun getOrCreatePipeline(mesh: Mesh, updateEvent: RenderPass.UpdateEvent): Pipeline {
        // todo: recreating the pipeline might be necessary if the same shader instance is used on multiple objects
        //  however, that isn't really supported anyway...

        var pipeline = createdPipeline
        if (pipeline == null) {
            val pipelineBuilder = Pipeline.Builder()
            pipelineBuilder.vertexLayout.primitiveType = mesh.geometry.primitiveType
            onPipelineSetup(pipelineBuilder, mesh, updateEvent)
            pipeline = pipelineBuilder.create()
            onPipelineCreated(pipeline, mesh, updateEvent)
        }
        return pipeline
    }

    open fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, updateEvent: RenderPass.UpdateEvent) {
        builder.pipelineConfig.set(pipelineConfig)
    }

    open fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, updateEvent: RenderPass.UpdateEvent) {
        pipelineCreated(pipeline)
        onPipelineCreated.forEach { it.onPipelineCreated(pipeline, mesh, updateEvent) }
    }

    fun interface PipelineCreatedListener {
        fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, updateEvent: RenderPass.UpdateEvent)
    }
}
