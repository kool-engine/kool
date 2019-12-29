package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

abstract class ModeledShader(protected val model: ShaderModel) : Shader() {
    override fun generateCode(pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        return ctx.shaderGenerator.generateShader(model, pipeline, ctx)
    }

    class StaticColor(model: ShaderModel, val uStaticColor: Uniform4f) : ModeledShader(model)

    class VertexColor(model: ShaderModel) : ModeledShader(model)

    class TextureColor(model: ShaderModel, private val texName: String) : ModeledShader(model) {
        lateinit var textureSampler: TextureSampler

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            textureSampler = pipeline.descriptorSetLayouts[0].getTextureSampler(texName)
        }
    }

    companion object {
        fun staticColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> StaticColor = { mesh, buildCtx, ctx ->
            val staticColorNode = PushConstantNode4f("uStaticColor")
            val model = ShaderModel().apply {
                val mvp = UniformBufferPremultipliedMvp()
                val attribPos = AttributeNode(Attribute.POSITIONS)
                val plainPos = PlainVertexPosNode()

                plainPos.inPosition = attribPos.output
                plainPos.inMvp = mvp.output

                vertexStage.nodes += mvp
                vertexStage.nodes += attribPos
                vertexStage.nodes += plainPos

                fragmentStage.nodes += staticColorNode
                fragmentStage.nodes += UnlitMaterialNode(staticColorNode.output)
            }
            model.setup(mesh, buildCtx, ctx)
            StaticColor(model, staticColorNode.uniform)
        }

        fun vertexColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel().apply {
                val mvp = UniformBufferPremultipliedMvp()
                val attribPos = AttributeNode(Attribute.POSITIONS)
                val attribColor = AttributeNode(Attribute.COLORS)
                val ifColor = StageInterfaceNode("colorIf")
                val plainPos = PlainVertexPosNode()

                ifColor.input = attribColor.output
                plainPos.inMvp = mvp.output
                plainPos.inPosition = attribPos.output

                vertexStage.nodes += attribPos
                vertexStage.nodes += mvp
                vertexStage.nodes += attribColor
                vertexStage.nodes += ifColor.vertexNode
                vertexStage.nodes += plainPos

                fragmentStage.nodes += ifColor.fragmentNode
                fragmentStage.nodes += UnlitMaterialNode(ifColor.output)
            }
            model.setup(mesh, buildCtx, ctx)
            VertexColor(model)
        }

        fun textureColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> TextureColor = { mesh, buildCtx, ctx ->
            val texName = "tex"
            val model = ShaderModel().apply {
                val mvp = UniformBufferPremultipliedMvp()
                val attribPos = AttributeNode(Attribute.POSITIONS)
                val attribTexCoords = AttributeNode(Attribute.TEXTURE_COORDS)
                val ifTexCoords = StageInterfaceNode("texCoordsIf")
                val plainPos = PlainVertexPosNode()

                ifTexCoords.input = attribTexCoords.output
                plainPos.inMvp = mvp.output
                plainPos.inPosition = attribPos.output

                vertexStage.nodes += attribPos
                vertexStage.nodes += mvp
                vertexStage.nodes += attribTexCoords
                vertexStage.nodes += ifTexCoords.vertexNode
                vertexStage.nodes += plainPos

                val tex = TextureNode(texName)
                val texSampler = TextureSamplerNode(tex)
                texSampler.inTexCoord = ifTexCoords.output

                fragmentStage.nodes += ifTexCoords.fragmentNode
                fragmentStage.nodes += tex
                fragmentStage.nodes += texSampler
                fragmentStage.nodes += UnlitMaterialNode(texSampler.outColor)
            }
            model.setup(mesh, buildCtx, ctx)
            TextureColor(model, texName)
        }
    }
}