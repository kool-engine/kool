package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

abstract class ModeledShader(protected val model: ShaderModel) : Shader() {
    fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext): ModeledShader {
        model.setup(mesh, buildCtx, ctx)
        return this
    }

    override fun generateCode(pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        return ctx.shaderGenerator.generateShader(model, pipeline, ctx)
    }

    class StaticColor(model: ShaderModel, val uStaticColor: Uniform4f) : ModeledShader(model)

    class VertexColor(model: ShaderModel) : ModeledShader(model)

    class TextureColor(model: ShaderModel, private val texName: String) : ModeledShader(model) {
        lateinit var textureSampler: TextureSampler

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            textureSampler = model.findNode<TextureNode>(texName)!!.sampler
        }
    }

    class CubeMapColor(model: ShaderModel, private val texName: String) : ModeledShader(model) {
        lateinit var cubeMapSampler: CubeMapSampler

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            cubeMapSampler = model.findNode<CubeMapNode>(texName)!!.sampler
        }
    }

    companion object {
        fun staticColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> StaticColor = { mesh, buildCtx, ctx ->
            val staticColorNode: PushConstantNode4f
            val model = ShaderModel("ModeledShader.staticColor()").apply {
                vertexStage {
                    positionOutput = simpleVertexPositionNode().outPosition
                }
                fragmentStage {
                    staticColorNode = pushConstantNode4f("uStaticColor")
                    val preMultColor = premultiplyColorNode(staticColorNode.output)
                    colorOutput = unlitMaterialNode(preMultColor.outColor).outColor
                }
            }
            StaticColor(model, staticColorNode.uniform).setup(mesh, buildCtx, ctx) as StaticColor
        }

        fun vertexColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> VertexColor = { mesh, buildCtx, ctx ->
            val model = ShaderModel("ModeledShader.vertexColor()").apply {
                val ifColors: StageInterfaceNode

                vertexStage {
                    val preMultColor = premultiplyColorNode(attrColors().output)
                    ifColors = stageInterfaceNode("ifColors", preMultColor.outColor)
                    positionOutput = simpleVertexPositionNode().outPosition
                }
                fragmentStage {
                    colorOutput = unlitMaterialNode(ifColors.output).outColor
                }
            }
            VertexColor(model).setup(mesh, buildCtx, ctx) as VertexColor
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

                    positionOutput = vertexPositionNode(attrPositions().output, mvp.outMvpMat).outPosition
                }
                fragmentStage {
                    val mvpFrag = mvp.addToStage(fragmentStage)
                    val lightNode = defaultLightNode()
                    colorOutput = phongMaterialNode(ifColors.output, ifNormals.output, ifFragPos.output, mvpFrag.outCamPos, lightNode).outColor
                }
            }
            VertexColor(model).setup(mesh, buildCtx, ctx) as VertexColor
        }

        fun textureColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> TextureColor = { mesh, buildCtx, ctx ->
            val texName = "colorTex"
            val model = ShaderModel("ModeledShader.textureColor()").apply {
                val ifTexCoords: StageInterfaceNode

                vertexStage {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                    positionOutput = simpleVertexPositionNode().outPosition
                }
                fragmentStage {
                    val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
                    colorOutput = unlitMaterialNode(sampler.outColor).outColor
                }
            }
            TextureColor(model, texName).setup(mesh, buildCtx, ctx) as TextureColor
        }

        fun cubeMapColor(): (Mesh, Pipeline.BuildContext, KoolContext) -> CubeMapColor = { mesh, buildCtx, ctx ->
            val texName = "cubeMap"
            val model = ShaderModel("ModeledShader.cubeMapColor()").apply {
                val ifFragPos: StageInterfaceNode

                vertexStage {
                    val mvp = mvpNode()
                    val worldPos = transformNode(attrPositions().output, mvp.outModelMat, 1f)
                    ifFragPos = stageInterfaceNode("ifFragPos", worldPos.output)
                    positionOutput = vertexPositionNode(attrPositions().output, mvp.outMvpMat).outPosition
                }
                fragmentStage {
                    val nrmPos = normalizeNode(ifFragPos.output)
                    val sampler = cubeMapSamplerNode(cubeMapNode(texName), nrmPos.output)
                    colorOutput = unlitMaterialNode(sampler.outColor).outColor
                }
            }
            CubeMapColor(model, texName).setup(mesh, buildCtx, ctx) as CubeMapColor
        }
    }
}