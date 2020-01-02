package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.ShaderCode
import de.fabmax.kool.pipeline.TextureSampler
import de.fabmax.kool.pipeline.Uniform4f
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
            val model = ShaderModel("ModeledShader.staticColor()").apply {
                vertexStage {
                    simpleVertexPositionNode()
                }
                fragmentStage {
                    staticColorNode = pushConstantNode4f("uStaticColor")
                    val preMultColor = premultiplyColorNode(staticColorNode.output)
                    unlitMaterialNode(preMultColor.outColor)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            StaticColor(model, staticColorNode.uniform)
        }

        fun vertexColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel("ModeledShader.vertexColor()").apply {
                val ifColors: StageInterfaceNode

                vertexStage {
                    val preMultColor = premultiplyColorNode(attrColors().output)
                    ifColors = stageInterfaceNode("ifColors", preMultColor.outColor)
                    simpleVertexPositionNode()
                }
                fragmentStage {
                    unlitMaterialNode(ifColors.output)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            VertexColor(model)
        }

        fun vertexColorPhong(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel("ModeledShader.vertexColorPhong()").apply {
                val ifColors: StageInterfaceNode

                val ifNormals: StageInterfaceNode
                val ifFragPos: StageInterfaceNode
                val mvp: UniformBufferMvp

                vertexStage {
                    mvp = mvpNode()
                    val preMultColor = premultiplyColorNode(attrColors().output)
                    ifColors = stageInterfaceNode("ifColors", preMultColor.outColor)
                    val nrm = transformNode(attrNormals().output, mvp.outModelMat, 0f)
                    ifNormals = stageInterfaceNode("ifNormals", nrm.output)
                    val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f)
                    ifFragPos = stageInterfaceNode("ifFragPos", worldPos.output)

                    vertexPositionNode(attrPositions().output, mvp.outMvpMat)
                }
                fragmentStage {
                    val mvpFrag = mvp.addToStage(fragmentStage)
                    val lightNode = defaultLightNode()
                    phongMaterialNode(ifColors.output, ifNormals.output, ifFragPos.output, mvpFrag.outCamPos, lightNode)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            VertexColor(model)
        }

        fun vertexColorPbr(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel("ModeledShader.vertexColorPbr()").apply {
                val ifColors: StageInterfaceNode

                val ifNormals: StageInterfaceNode
                val ifFragPos: StageInterfaceNode
                val mvp: UniformBufferMvp

                vertexStage {
                    mvp = mvpNode()
                    val preMultColor = premultiplyColorNode(attrColors().output)
                    ifColors = stageInterfaceNode("ifColors", preMultColor.outColor)
                    val nrm = transformNode(attrNormals().output, mvp.outModelMat, 0f)
                    ifNormals = stageInterfaceNode("ifNormals", nrm.output)
                    val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f)
                    ifFragPos = stageInterfaceNode("ifFragPos", worldPos.output)

                    vertexPositionNode(attrPositions().output, mvp.outMvpMat)
                }
                fragmentStage {
                    val mvpFrag = mvp.addToStage(fragmentStage)
                    val lightNode = defaultLightNode()
                    pbrMaterialNode(ifColors.output, ifNormals.output, ifFragPos.output, mvpFrag.outCamPos, lightNode)
                }
            }
            model.setup(mesh, buildCtx, ctx)
            VertexColor(model)
        }

        fun textureColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> TextureColor = { mesh, buildCtx, ctx ->
            val texName = "tex"
            val model = ShaderModel("ModeledShader.textureColor()").apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
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