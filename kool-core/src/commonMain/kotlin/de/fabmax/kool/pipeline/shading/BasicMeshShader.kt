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
        buildCtx.descriptorSetLayout { +UniformBuffer.uboMvp() }
    }

    /**
     * Binds specified mesh attributes (by names) to the shader input. Attributes are bound to the locations
     * corresponding to their list index.
     */
    protected fun bindAttributes(mesh: Mesh, buildCtx: Pipeline.BuildContext, attribNames: List<String>) {
        val attribs = mutableListOf<VertexLayout.Attribute>()
        val verts = mesh.meshData.vertexList

        attribNames.forEachIndexed { iAttrib, name ->
            val attrib = mesh.meshData.vertexAttributes.find { it.name == name }
                    ?: throw NoSuchElementException("Mesh does not include required vertex attribute: $name")
            val off = verts.attributeOffsets[attrib] ?: throw NoSuchElementException()
            attribs += VertexLayout.Attribute(0, iAttrib, off, attrib.type, attrib.name)
        }

        buildCtx.vertexLayout.bindings += VertexLayout.Binding(0, InputRate.VERTEX, attribs, verts.strideBytesF)
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

    class StaticColor(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) : BasicMeshShader(buildCtx) {
        override val shaderCode: ShaderCode

        lateinit var staticColor: Uniform4f

        init {
            bindAttributes(mesh, buildCtx, listOf(Attribute.POSITIONS.name))
            buildCtx.pushConstantRange {
                stages += Stage.FRAGMENT_SHADER
                +{ Uniform4f("staticColor") }
            }
            val model = ShaderModel().apply {
                baseMaterial = BaseMaterial.PHONG
                baseAlbedo = BaseAlbedo.STATIC
            }
            shaderCode = ctx.shaderGenerator.generateShader(model, ctx)
        }

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            staticColor = pipeline.pushConstantRanges[0].pushConstants[0] as Uniform4f
        }

        companion object {
            val loader: (Mesh, Pipeline.BuildContext, KoolContext) -> StaticColor = { mesh, buildCtx, ctx ->
                StaticColor(mesh, buildCtx, ctx)
            }
        }
    }

    class TextureColor(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext) : BasicMeshShader(buildCtx) {
        override val shaderCode: ShaderCode

        lateinit var textureSampler: TextureSampler

        init {
            bindAttributes(mesh, buildCtx, listOf(Attribute.POSITIONS.name, Attribute.TEXTURE_COORDS.name))
            buildCtx.descriptorSetLayout {
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
            textureSampler = pipeline.descriptorSetLayouts[0].getTextureSampler("tex")
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
            buildCtx.descriptorSetLayout {
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
            textureSampler = pipeline.descriptorSetLayouts[0].getTextureSampler("tex")
        }

        companion object {
            val loader: (Mesh, Pipeline.BuildContext, KoolContext) -> MaskedColor = { mesh, buildCtx, ctx ->
                MaskedColor(mesh, buildCtx, ctx)
            }
        }
    }
}