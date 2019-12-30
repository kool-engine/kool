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
            val staticColorNode: PushConstantNode4f
            val model = ShaderModel().apply {
                vertexStage {
                    simpleVertexPositionNode()
                }
                fragmentStage {
                    staticColorNode = pushConstantNode4f("uStaticColor")
                    unlitMaterialNode(staticColorNode.output)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            StaticColor(model, staticColorNode.uniform)
        }

        fun vertexColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel().apply {
                val ifColors: StageInterfaceNode

                vertexStage {
                    ifColors = stageInterfaceNode("ifColors", attributeNode(Attribute.COLORS).output)
                    simpleVertexPositionNode()
                }
                fragmentStage {
                    unlitMaterialNode(ifColors.output)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            VertexColor(model)
        }

        fun textureColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> TextureColor = { mesh, buildCtx, ctx ->
            val texName = "tex"
            val model = ShaderModel().apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attributeNode(Attribute.TEXTURE_COORDS).output)
                    simpleVertexPositionNode()
                }
                fragmentStage {
                    val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
                    unlitMaterialNode(sampler.outColor)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            TextureColor(model, texName)
        }
    }
}