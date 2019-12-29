package de.fabmax.kool.pipeline.shadermodel

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Mesh

class ShaderModel {
    val stages = mutableMapOf(
            ShaderStage.VERTEX_SHADER to VertexShaderGraph(),
            ShaderStage.FRAGMENT_SHADER to FragmentShaderGraph()
    )

    val vertexStage: VertexShaderGraph
        get() = stages[ShaderStage.VERTEX_SHADER] as VertexShaderGraph
    val fragmentStage: FragmentShaderGraph
        get() = stages[ShaderStage.FRAGMENT_SHADER] as FragmentShaderGraph

    fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) {
        stages.values.forEach { it.setup() }

        setupAttributes(mesh, buildCtx)
        val descBuilder = DescriptorSetLayout.Builder()
        buildCtx.descriptorSetLayouts += descBuilder
        stages.values.forEach { stage ->
            descBuilder.descriptors += stage.descriptorSet.descriptors
            buildCtx.pushConstantRanges += stage.pushConstants
        }
    }

    private fun setupAttributes(mesh: Mesh, buildCtx: Pipeline.BuildContext) {
        val vertLayoutAttribs = mutableListOf<VertexLayout.Attribute>()
        val verts = mesh.meshData.vertexList

        vertexStage.requiredVertexAttributes.forEachIndexed { iAttrib, attrib ->
            if (!mesh.meshData.vertexAttributes.contains(attrib)) {
                throw NoSuchElementException("Mesh does not include required vertex attribute: ${attrib.name}")
            }
            val off = verts.attributeOffsets[attrib] ?: throw NoSuchElementException()
            vertLayoutAttribs += VertexLayout.Attribute(iAttrib, off, attrib.type, attrib.name)
        }

        if (buildCtx.vertexLayout.bindings.isNotEmpty()) {
            TODO("multiple attribute bindings are not yet implemented: attribute location must be changed")
        }

        buildCtx.vertexLayout.bindings += VertexLayout.Binding(0, InputRate.VERTEX, vertLayoutAttribs, verts.strideBytesF)
    }
}