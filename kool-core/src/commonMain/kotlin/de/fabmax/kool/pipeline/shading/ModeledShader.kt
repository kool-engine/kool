package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

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

        var irradianceMapSampler: CubeMapSampler? = null
            private set
        var reflectionMapSampler: CubeMapSampler? = null
            private set
        var brdfLutSampler: TextureSampler? = null
            private set

        override fun onPipelineCreated(pipeline: Pipeline) {
            super.onPipelineCreated(pipeline)
            irradianceMapSampler = model.findNode<CubeMapNode>("irradianceMap")?.sampler
            irradianceMapSampler?.let { it.texture = cfg.irradianceMap }
            reflectionMapSampler = model.findNode<CubeMapNode>("reflectionMap")?.sampler
            reflectionMapSampler?.let { it.texture = cfg.reflectionMap }
            brdfLutSampler = model.findNode<TextureNode>("brdfLut")?.sampler
            brdfLutSampler?.let { it.texture = cfg.brdfLut }
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

                    positionOutput = vertexPositionNode(attrPositions().output, mvp.outMvpMat).outPosition
                }
                fragmentStage {
                    val mvpFrag = mvp.addToStage(fragmentStage)
                    val lightNode = defaultLightNode()
                    val irrMap = cubeMapNode("irradianceMap")
                    val reflMap = cubeMapNode("reflectionMap")
                    val brdfLut = textureNode("brdfLut")
                    val irrSampler = cubeMapSamplerNode(irrMap, ifNormals.output)
                    val mat = pbrIblMaterialNode(reflMap, brdfLut, ifColors.output, ifNormals.output, ifFragPos.output, mvpFrag.outCamPos, lightNode).apply {
                        inIrradiance = irrSampler.outColor
                        inMetallic = pushConstantNode1f(cfg.metallic).output
                        inRoughness = pushConstantNode1f(cfg.roughness).output
                    }
                    val hdrToLdr = hdrToLdrNode(mat.outColor).apply {
                        inExposure = ShaderNodeIoVar(ModelVar1fConst(0.5f))
                        inContrast = ShaderNodeIoVar(ModelVar1fConst(0.9f))
                    }
                    colorOutput = hdrToLdr.outColor
                }
            }
        }

        class PbrConfig {
            val roughness = Uniform1f(0.5f, "uRoughness")
            val metallic = Uniform1f(0.0f, "uMetallic")

            var irradianceMap: CubeMapTexture? = null
            var reflectionMap: CubeMapTexture? = null
            var brdfLut: Texture? = null
            val ambient = UniformColor(Color(0.03f, 0.03f, 0.03f, 1f), "uAmbient")
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