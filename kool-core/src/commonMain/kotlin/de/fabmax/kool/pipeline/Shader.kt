package de.fabmax.kool.pipeline

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.scene.Mesh

/**
 * Base class for regular shaders / materials, which can be attached to [Mesh]es in order to render them. Usually,
 * you should use [KslShader] to define your custom shaders, however, it is also possible to subclass this class
 * directly and create a [Pipeline] using arbitrary custom shader code in [createPipeline]. In that case the
 * supplied shader source code has to match the rendering backend (e.g. GLSL in case an OpenGL backend is used).
 */
abstract class Shader(name: String) : ShaderBase<Pipeline>(name) {
    fun getOrCreatePipeline(mesh: Mesh, updateEvent: RenderPass.UpdateEvent): Pipeline {
        val pipeline = createdPipeline ?: createPipeline(mesh, updateEvent).also { pipelineCreated(it) }
        check(pipeline.vertexLayout.primitiveType == mesh.geometry.primitiveType) {
            "Shader pipeline was created for mesh primitive type ${pipeline.vertexLayout.primitiveType} but provided mesh has primitive type ${mesh.geometry.primitiveType}"
        }
        return pipeline
    }

    protected abstract fun createPipeline(mesh: Mesh, updateEvent: RenderPass.UpdateEvent): Pipeline
}
