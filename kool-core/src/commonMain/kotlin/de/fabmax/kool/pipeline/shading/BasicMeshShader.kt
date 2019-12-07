package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.BaseAlbedo
import de.fabmax.kool.pipeline.shadermodel.BaseMaterial
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.shading.Attribute

abstract class BasicMeshShader(buildCtx: Pipeline.BuildContext) : Shader() {
    init {
        buildCtx.descriptorLayout.apply { +UniformBuffer.uboMvp() }
    }

    protected fun bindAttributes(mesh: Mesh, buildCtx: Pipeline.BuildContext, attribNames: List<String>) {
        val attribs = mutableListOf<VertexLayoutDescription.Attribute>()
        val verts = mesh.meshData.vertexList
        attribNames.forEachIndexed { iAttrib, name ->
            val attrib = mesh.meshData.vertexAttributes.find { it.name == name }
                    ?: throw NoSuchElementException("Mesh does not include required vertex attribute: $name")
            val off = verts.attributeOffsets[attrib] ?: throw NoSuchElementException()

            // fixme: unify / replace old de.fabmax.kool.shading.AttributeType by new one (which is not OpenGL specific)
            val attribType = when (attrib.type) {
                de.fabmax.kool.shading.AttributeType.FLOAT -> AttributeType.FLOAT
                de.fabmax.kool.shading.AttributeType.VEC_2F -> AttributeType.VEC_2F
                de.fabmax.kool.shading.AttributeType.VEC_3F -> AttributeType.VEC_3F
                de.fabmax.kool.shading.AttributeType.VEC_4F -> AttributeType.VEC_4F
                de.fabmax.kool.shading.AttributeType.COLOR_4F -> AttributeType.COLOR_4F
                // fixme: support int types, maybe as a 2nd binding?
                else -> throw IllegalStateException("Attribute is not a float type")
            }
            attribs += VertexLayoutDescription.Attribute(0, iAttrib, off * 4, attribType, attrib.name)
        }

        buildCtx.vertexLayout.bindings += VertexLayoutDescription.Binding(0, InputRate.VERTEX, attribs, verts.strideBytesF)
    }

    class VertexColor(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) : BasicMeshShader(buildCtx) {
        override val shaderCode: ShaderCode

        init {
            bindAttributes(mesh, buildCtx, listOf(Attribute.POSITIONS.name, Attribute.COLORS.name))

            val model = ShaderModel().apply {
                baseMaterial = BaseMaterial.PHONG
                baseAlbedo = BaseAlbedo.VERTEX
            }
            shaderCode = ctx.shaderGenerator.generateShader(model, ctx)
        }

        companion object {
            val loader: (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
                VertexColor(mesh, buildCtx, ctx)
            }
        }
    }

    class TextureColor(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) : BasicMeshShader(buildCtx) {
        override val shaderCode: ShaderCode

        lateinit var textureSampler: TextureSampler

        init {
            bindAttributes(mesh, buildCtx, listOf(Attribute.POSITIONS.name, Attribute.TEXTURE_COORDS.name))
            buildCtx.descriptorLayout.apply {
                +TextureSampler.Builder().apply {
                    name = "tex"
                    stages += Stage.FRAGMENT_SHADER
                }
            }
            val model = ShaderModel().apply {
                baseMaterial = BaseMaterial.PHONG
                baseAlbedo = BaseAlbedo.TEXTURE
            }
            shaderCode = ctx.shaderGenerator.generateShader(model, ctx)
        }

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            textureSampler = pipeline.descriptorLayout.getTextureSampler("tex")
        }

        companion object {
            val loader: (Mesh, Pipeline.BuildContext, KoolContext) -> TextureColor = { mesh, buildCtx, ctx ->
                TextureColor(mesh, buildCtx, ctx)
            }
        }
    }

    class MaskedColor(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) : BasicMeshShader(buildCtx) {
        override val shaderCode: ShaderCode

        lateinit var textureSampler: TextureSampler

        init {
            bindAttributes(mesh, buildCtx, listOf(Attribute.POSITIONS.name, Attribute.COLORS.name, Attribute.TEXTURE_COORDS.name))
            buildCtx.descriptorLayout.apply {
                +TextureSampler.Builder().apply {
                    name = "tex"
                    stages += Stage.FRAGMENT_SHADER
                }
            }
            val model = ShaderModel().apply {
                baseMaterial = BaseMaterial.PHONG
                baseAlbedo = BaseAlbedo.MASKED
            }
            shaderCode = ctx.shaderGenerator.generateShader(model, ctx)
        }

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            textureSampler = pipeline.descriptorLayout.getTextureSampler("tex")
        }

        companion object {
            val loader: (Mesh, Pipeline.BuildContext, KoolContext) -> MaskedColor = { mesh, buildCtx, ctx ->
                MaskedColor(mesh, buildCtx, ctx)
            }
        }
    }
}