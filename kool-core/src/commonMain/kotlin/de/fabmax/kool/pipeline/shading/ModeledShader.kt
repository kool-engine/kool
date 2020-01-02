package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

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

    class PbrShader(private val cfg: PbrConfig = PbrConfig(), model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {
        var roughness: Float
            get() = cfg.roughness.value
            set(value) { cfg.roughness.value = value }

        var metallic: Float
            get() = cfg.metallic.value
            set(value) { cfg.metallic.value = value }

        var ambient: Color
            get() = cfg.ambient.value
            set(value) { cfg.ambient.value.set(value) }

        fun loadShader(mesh: Mesh, buildCtx: Pipeline.BuildContext, ctx: KoolContext): PbrShader {
            model.setup(mesh, buildCtx, ctx)
            return this
        }

        companion object {
            private fun defaultPbrModel(cfg: PbrConfig) = ShaderModel("defaultPbrModel()").apply {
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
                    val mat = pbrMaterialNode(ifColors.output, ifNormals.output, ifFragPos.output, mvpFrag.outCamPos, lightNode)

                    mat.inAmbient = pushConstantNodeColor(cfg.ambient).output
                    mat.inMetallic = pushConstantNode1f(cfg.metallic).output
                    mat.inRoughness = pushConstantNode1f(cfg.roughness).output
                }
            }
        }

        class PbrConfig {
            val roughness = Uniform1f(0.5f, "uRoughness")
            val metallic = Uniform1f(0.0f, "uMetallic")
            val ambient = UniformColor(Color(0.03f, 0.03f, 0.03f, 1f), "uAmbient")
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