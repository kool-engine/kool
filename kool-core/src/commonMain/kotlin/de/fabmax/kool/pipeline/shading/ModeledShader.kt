package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

open class ModeledShader(val model: ShaderModel) : Shader() {

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        model.setup(mesh, builder)
        builder.shaderCodeGenerator = { layout ->
            ctx.shaderGenerator.generateShader(model, layout, ctx)
        }
        super.onPipelineSetup(builder, mesh, ctx)
    }

    class StaticColor(color: Color = Color.GRAY, model: ShaderModel = staticColorModel()) : ModeledShader(model) {
        private var uColor: UniformColor? = null

        var color: Color = color
            set(value) {
                field = value
                uColor?.value?.set(value)
            }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            uColor = model.findNode<PushConstantNodeColor>("uStaticColor")?.uniform
            uColor?.value?.set(color)
            super.onPipelineCreated(pipeline, mesh, ctx)
        }
    }

    class VertexColor(model: ShaderModel = vertexColorModel()) : ModeledShader(model)

    class TextureColor(texture: Texture2d? = null, private val texName: String = "colorTex", model: ShaderModel = textureColorModel(texName, false)) : ModeledShader(model) {
        private var textureSampler: TextureSampler2d? = null

        var texture: Texture2d? = texture
            set(value) {
                field = value
                textureSampler?.texture = value
            }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            textureSampler = model.findNode<Texture2dNode>(texName)?.sampler
            textureSampler?.let { it.texture = texture }
            super.onPipelineCreated(pipeline, mesh, ctx)
        }
    }

    class HdrTextureColor(texture: Texture2d? = null, private val texName: String = "colorTex", model: ShaderModel = textureColorModel(texName, true)) : ModeledShader(model) {
        private var textureSampler: TextureSampler2d? = null

        var texture: Texture2d? = texture
            set(value) {
                field = value
                textureSampler?.texture = value
            }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            textureSampler = model.findNode<Texture2dNode>(texName)?.sampler
            textureSampler?.let { it.texture = texture }
            super.onPipelineCreated(pipeline, mesh, ctx)
        }
    }

    class CubeMapColor(texture: TextureCube? = null, private val texName: String = "cubeMap", model: ShaderModel = cubeMapColorModel(texName)) : ModeledShader(model) {
        private var cubeMapSampler: TextureSamplerCube? = null

        var texture: TextureCube? = texture
            set(value) {
                field = value
                cubeMapSampler?.texture = value
            }

        override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
            cubeMapSampler = model.findNode<TextureCubeNode>(texName)?.sampler
            cubeMapSampler?.let { it.texture = texture }
            super.onPipelineCreated(pipeline, mesh, ctx)
        }
    }

    companion object {
        private fun staticColorModel(): ShaderModel = ShaderModel("ModeledShader.staticColor()").apply {
            vertexStage {
                positionOutput = simpleVertexPositionNode().outVec4
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
                positionOutput = simpleVertexPositionNode().outVec4
            }
            fragmentStage {
                colorOutput(unlitMaterialNode(ifColors.output).outColor)
            }
        }

        private fun textureColorModel(texName: String, isHdr: Boolean = false) = ShaderModel("ModeledShader.textureColor()").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = simpleVertexPositionNode().outVec4
            }
            fragmentStage {
                val sampler = texture2dSamplerNode(texture2dNode(texName), ifTexCoords.output)
                if (isHdr) {
                    colorOutput(unlitMaterialNode(hdrToLdrNode(sampler.outColor).outColor).outColor)
                } else {
                    colorOutput(unlitMaterialNode(sampler.outColor).outColor)
                }
            }
        }

        private fun cubeMapColorModel(texName: String) = ShaderModel("ModeledShader.cubeMapColor()").apply {
            val ifFragPos: StageInterfaceNode

            vertexStage {
                val mvp = mvpNode()
                val worldPos = vec4TransformNode(attrPositions().output, mvp.outModelMat, 1f)
                ifFragPos = stageInterfaceNode("ifFragPos", worldPos.outVec4)
                positionOutput = vec4TransformNode(attrPositions().output, mvp.outMvpMat).outVec4
            }
            fragmentStage {
                val nrmPos = normalizeNode(ifFragPos.output)
                val sampler = textureCubeSamplerNode(textureCubeNode(texName), nrmPos.output)
                colorOutput(unlitMaterialNode(sampler.outColor).outColor)
            }
        }
    }
}