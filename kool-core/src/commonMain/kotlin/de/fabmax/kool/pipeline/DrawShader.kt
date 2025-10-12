package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.Struct

/**
 * Base class for regular shaders / materials, which can be attached to [Mesh]es in order to draw them. Usually,
 * you should use [KslShader] to define your custom shaders, however, it is also possible to subclass this class
 * directly and create a [DrawPipeline] using arbitrary custom shader code in [createPipeline]. In that case the
 * supplied shader source code has to match the rendering backend (e.g. GLSL in case an OpenGL backend is used).
 */
abstract class DrawShader(name: String) : ShaderBase<DrawPipeline>(name) {
    private var meshVertexLayout: Struct? = null
    private var meshInstanceLayout: Struct? = null

    fun getOrCreatePipeline(
        mesh: Mesh,
        ctx: KoolContext,
        meshInstances: MeshInstanceList<*>? = mesh.instances
    ): DrawPipeline {
        val created = createdPipeline
        if (created == null) {
            meshVertexLayout = mesh.geometry.layout
            meshInstanceLayout = meshInstances?.layout

        } else {
            // if shader is used for multiple meshes, these must have identical buffer layouts
            check(meshVertexLayout == mesh.geometry.layout) {
                "Shader pipeline was created for mesh vertex layout $meshVertexLayout but provided " +
                "mesh has vertex layout ${mesh.geometry.layout}"
            }
            check(meshInstanceLayout == null || meshInstanceLayout == meshInstances?.layout) {
                "Shader pipeline was created for mesh instance layout $meshInstanceLayout but provided " +
                "mesh has instance layout ${meshInstances?.layout}"
            }
            check(created.vertexLayout.primitiveType == mesh.geometry.primitiveType) {
                "Shader pipeline was created for mesh primitive type ${created.vertexLayout.primitiveType} but " +
                "provided mesh has primitive type ${mesh.geometry.primitiveType}"
            }
        }
        return created ?: createPipeline(mesh, meshInstances, ctx).also { pipelineCreated(it) }
    }

    protected abstract fun createPipeline(mesh: Mesh, instances: MeshInstanceList<*>?, ctx: KoolContext): DrawPipeline
}
