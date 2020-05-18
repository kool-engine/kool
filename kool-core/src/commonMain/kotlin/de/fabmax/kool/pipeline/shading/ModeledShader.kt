package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

open class ModeledShader(val model: ShaderModel) : Shader(), PipelineFactory {

    val onSetup = mutableListOf<((builder: Pipeline.Builder) -> Unit)>()

    protected open fun setup(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext): ModeledShader {
        model.setup(mesh, buildCtx)
        return this
    }

    override fun generateCode(pipeline: Pipeline, ctx: KoolContext): ShaderCode {
        return ctx.shaderGenerator.generateShader(model, pipeline, ctx)
    }

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.name = model.modelInfo
        builder.shaderLoader = this::setup
        onSetup.forEach { it(builder) }
        return builder.create(mesh, ctx)
    }

    class StaticColor(model: ShaderModel = staticColorModel()) : ModeledShader(model) {
        private var uColor: UniformColor? = null

        var color: Color = Color.GRAY
            set(value) {
                field = value
                uColor?.value?.set(value)
            }

        override fun onPipelineCreated(pipeline: Pipeline) {
            uColor = model.findNode<PushConstantNodeColor>("uStaticColor")?.uniform
            uColor?.value?.set(color)
            super.onPipelineCreated(pipeline)
        }
    }

    class VertexColor(model: ShaderModel = vertexColorModel()) : ModeledShader(model)

    class TextureColor(texture: Texture? = null, private val texName: String = "colorTex", model: ShaderModel = textureColorModel(texName)) : ModeledShader(model) {
        private var textureSampler: TextureSampler? = null

        var texture: Texture? = texture
            set(value) {
                field = value
                textureSampler?.texture = value
            }

        override fun onPipelineCreated(pipeline: Pipeline) {
            textureSampler = model.findNode<TextureNode>(texName)?.sampler
            textureSampler?.let { it.texture = texture }
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
        private fun staticColorModel(): ShaderModel = ShaderModel("ModeledShader.staticColor()").apply {
            vertexStage {
                positionOutput = simpleVertexPositionNode().outPosition
            }
            fragmentStage {
                val color = pushConstantNodeColor("uStaticColor")
                colorOutput(unlitMaterialNode(color.output).outColor)
            }
        }

        private fun vertexColorModel(): ShaderModel = ShaderModel("ModeledShader.vertexColor()").apply {
            val ifColors: StageInterfaceNode
            vertexStage {
                ifColors = stageInterfaceNode("ifColors", attrColors().output)
                positionOutput = simpleVertexPositionNode().outPosition
            }
            fragmentStage {
                colorOutput(unlitMaterialNode(ifColors.output).outColor)
            }
        }

        private fun textureColorModel(texName: String) = ShaderModel("ModeledShader.textureColor()").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = simpleVertexPositionNode().outPosition
            }
            fragmentStage {
                val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
                colorOutput(unlitMaterialNode(sampler.outColor).outColor)
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
                colorOutput(unlitMaterialNode(sampler.outColor).outColor)
            }
        }
    }
}