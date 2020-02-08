package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

abstract class ModeledShader(protected val model: ShaderModel) : Shader(), PipelineFactory {

    val onSetup = mutableListOf<((builder: Pipeline.Builder) -> Unit)>()

    protected open fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext): ModeledShader {
        model.setup(mesh, buildCtx, ctx)
        return this
    }

    override fun generateCode(pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        return ctx.shaderGenerator.generateShader(model, pipeline, ctx)
    }

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.shaderLoader = this::setup
        onSetup.forEach { it(builder) }
        return builder.create(mesh, ctx)
    }

    //class StaticColor(model: ShaderModel, val uStaticColor: Uniform4f) : ModeledShader(model)

    class VertexColor(model: ShaderModel = vertexColorModel()) : ModeledShader(model)

    class TextureColor(private val texName: String = "colorTex", model: ShaderModel = textureColorModel(texName)) : ModeledShader(model) {
        lateinit var textureSampler: TextureSampler

        override fun onPipelineCreated(pipeline: Pipeline) {
            textureSampler = model.findNode<TextureNode>(texName)!!.sampler
            super.onPipelineCreated(pipeline)
        }
    }

    class CubeMapColor(private val texName: String = "cubeMap", model: ShaderModel = cubeMapColorModel(texName)) : ModeledShader(model) {
        lateinit var cubeMapSampler: CubeMapSampler

        override fun onPipelineCreated(pipeline: Pipeline) {
            cubeMapSampler = model.findNode<CubeMapNode>(texName)!!.sampler
            super.onPipelineCreated(pipeline)
        }
    }

    companion object {
        private fun vertexColorModel(): ShaderModel = ShaderModel("ModeledShader.vertexColor()").apply {
            val ifColors: StageInterfaceNode

            vertexStage {
                ifColors = stageInterfaceNode("ifColors", attrColors().output)
                positionOutput = simpleVertexPositionNode().outPosition
            }
            fragmentStage {
                colorOutput = unlitMaterialNode(ifColors.output).outColor
            }
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

        private fun textureColorModel(texName: String) = ShaderModel("ModeledShader.textureColor()").apply {
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

        private fun cubeMapColorModel(texName: String) = ShaderModel("ModeledShader.cubeMapColor()").apply {
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
    }
}