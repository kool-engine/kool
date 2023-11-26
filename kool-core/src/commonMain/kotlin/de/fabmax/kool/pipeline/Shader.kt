package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.scene.Mesh
import kotlin.collections.forEach
import kotlin.collections.mutableListOf
import kotlin.collections.set

/**
 * Base class for regular shaders / materials, which can be attached to [Mesh]es in order to render them. Usually,
 * you should use [KslShader] to define your custom shaders, however, it is also possible to subclass this class
 * directly and supply arbitrary shader source code to the [Pipeline.Builder] in [onPipelineSetup]. In that case the
 * supplied shader source code has to match the rendering backend (e.g. GLSL in case an OpenGL backend is used).
 */
abstract class Shader : ShaderBase() {
    val pipelineConfig = PipelineConfig()

    val onPipelineCreated = mutableListOf<(Pipeline, Mesh, KoolContext) -> Unit>()

    fun createPipeline(mesh: Mesh, ctx: KoolContext): Pipeline {
        val pipelineBuilder = Pipeline.Builder()
        pipelineBuilder.vertexLayout.primitiveType = mesh.geometry.primitiveType
        onPipelineSetup(pipelineBuilder, mesh, ctx)
        val pipeline = pipelineBuilder.create()
        onPipelineCreated(pipeline, mesh, ctx)
        return pipeline
    }

    open fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.pipelineConfig.set(pipelineConfig)
    }

    open fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        // todo: it can happen that onPipelineCreated is called repeatedly
        //  check if that is still the case and if there are negative effects (at least performance is not optimal...)

        pipeline.layout.descriptorSets.forEach { descSet ->
            descSet.descriptors.forEach { desc ->
                when (desc) {
                    is UniformBuffer -> desc.uniforms.forEach { uniforms[it.name] = it }
                    is TextureSampler1d -> texSamplers1d[desc.name] = desc
                    is TextureSampler2d -> texSamplers2d[desc.name] = desc
                    is TextureSampler3d -> texSamplers3d[desc.name] = desc
                    is TextureSamplerCube -> texSamplersCube[desc.name] = desc
                }
            }
        }
        shaderCreated()
        onPipelineCreated.forEach { it(pipeline, mesh, ctx) }
    }
}
