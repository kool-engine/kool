package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.util.Color

class PbrShader(cfg: PbrConfig = PbrConfig(), model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {

    // Simple material props
    private var uRoughness: PushConstantNode1f? = null
    private var uMetallic: PushConstantNode1f? = null

    var metallic = cfg.metallic
        set(value) {
            field = value
            uMetallic?.uniform?.value = value
        }

    var roughness = cfg.roughness
        set(value) {
            field = value
            uRoughness?.uniform?.value = value
        }

    // Material maps
    private var albedoSampler: TextureSampler? = null
    private var normalSampler: TextureSampler? = null
    private var metallicSampler: TextureSampler? = null
    private var roughnessSampler: TextureSampler? = null
    private var ambientOcclusionSampler: TextureSampler? = null
    private var displacementSampler: TextureSampler? = null

    var albedoMap = cfg.albedoMap
        set(value) {
            field = value
            albedoSampler?.texture = value
        }
    var normalMap = cfg.normalMap
        set(value) {
            field = value
            normalSampler?.texture = value
        }
    var metallicMap = cfg.metallicMap
        set(value) {
            field = value
            metallicSampler?.texture = value
        }
    var roughnessMap = cfg.roughnessMap
        set(value) {
            field = value
            roughnessSampler?.texture = value
        }
    var ambientOcclusionMap = cfg.ambientOcclusionMap
        set(value) {
            field = value
            ambientOcclusionSampler?.texture = value
        }
    var displacementMap = cfg.displacementMap
        set(value) {
            field = value
            displacementSampler?.texture = value
        }

    // Simple lighting props
    private var uAmbient: PushConstantNodeColor? = null

    var ambient = cfg.ambient
        set(value) {
            field = value
            uAmbient?.uniform?.value?.set(value)
        }

    // Image based lighting maps
    private var irradianceMapSampler: CubeMapSampler? = null
    private var reflectionMapSampler: CubeMapSampler? = null
    private var brdfLutSampler: TextureSampler? = null

    var irradianceMap = cfg.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap = cfg.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut = cfg.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    override fun onPipelineCreated(pipeline: Pipeline) {
        super.onPipelineCreated(pipeline)
        uMetallic = model.findNode("uMetallic")
        uMetallic?.let { it.uniform.value = metallic }
        uRoughness = model.findNode("uRoughness")
        uRoughness?.let { it.uniform.value = roughness }
        uAmbient = model.findNode("uAmbient")
        uAmbient?.uniform?.value?.set(ambient)

        irradianceMapSampler = model.findNode<CubeMapNode>("irradianceMap")?.sampler
        irradianceMapSampler?.let { it.texture = irradianceMap }
        reflectionMapSampler = model.findNode<CubeMapNode>("reflectionMap")?.sampler
        reflectionMapSampler?.let { it.texture = reflectionMap }
        brdfLutSampler = model.findNode<TextureNode>("brdfLut")?.sampler
        brdfLutSampler?.let { it.texture = brdfLut }

        albedoSampler = model.findNode<TextureNode>("tAlbedo")?.sampler
        albedoSampler?.let { it.texture = albedoMap }
        normalSampler = model.findNode<TextureNode>("tNormal")?.sampler
        normalSampler?.let { it.texture = normalMap }
        metallicSampler = model.findNode<TextureNode>("tMetallic")?.sampler
        metallicSampler?.let { it.texture = metallicMap }
        roughnessSampler = model.findNode<TextureNode>("tRoughness")?.sampler
        roughnessSampler?.let { it.texture = roughnessMap }
        ambientOcclusionSampler = model.findNode<TextureNode>("tAmbOccl")?.sampler
        ambientOcclusionSampler?.let { it.texture = ambientOcclusionMap }
        displacementSampler = model.findNode<TextureNode>("tDisplacement")?.sampler
        displacementSampler?.let { it.texture = displacementMap }
    }

    companion object {
        private fun defaultPbrModel(cfg: PbrConfig) = ShaderModel("defaultPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val mvp: UniformBufferMvp

            vertexStage {
                mvp = mvpNode()
                val nrm = transformNode(attrNormals().output, mvp.outModelMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.output)

                ifTexCoords = if (cfg.albedoMap != null || cfg.normalMap != null || cfg.roughnessMap != null ||
                        cfg.metallicMap != null || cfg.ambientOcclusionMap != null || cfg.displacementMap != null) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }

                val worldPos = if (cfg.displacementMap != null) {
                    val dispTex = textureNode("tDisplacement")
                    displacementMapNode(dispTex, ifTexCoords!!.input, attrPositions().output, attrNormals().output).outPosition
                } else {
                    attrPositions().output
                }
                val pos = transformNode(worldPos, mvp.outModelMat, 1f).output
                ifFragPos = stageInterfaceNode("ifFragPos", pos)

                ifColors = if (cfg.albedoMap == null) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTangents = if (cfg.normalMap != null) {
                    val tan = transformNode(attrTangents().output, mvp.outModelMat, 0f)
                    stageInterfaceNode("ifTangents", tan.output)
                } else {
                    null
                }

                positionOutput = vertexPositionNode(worldPos, mvp.outMvpMat).outPosition
            }
            fragmentStage {
                val mvpFrag = mvp.addToStage(fragmentStage)
                val lightNode = defaultLightNode()

                val reflMap: CubeMapNode?
                val brdfLut: TextureNode?
                val irrSampler: CubeMapSamplerNode?

                if (cfg.isIbl) {
                    val irrMap = cubeMapNode("irradianceMap")
                    irrSampler = cubeMapSamplerNode(irrMap, ifNormals.output)
                    reflMap = cubeMapNode("reflectionMap")
                    brdfLut = textureNode("brdfLut")
                } else {
                    irrSampler = null
                    reflMap = null
                    brdfLut = null
                }

                val mat = pbrMaterialNode(lightNode, reflMap, brdfLut).apply {
                    inFragPos = ifFragPos.output
                    inCamPos = mvpFrag.outCamPos

                    inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

                    inAlbedo = if (ifColors != null) {
                        ifColors.output
                    } else {
                        val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output)
                        val albedoLin = gammaNode(albedoSampler.outColor)
                        albedoLin.outColor
                    }
                    inNormal = if (cfg.normalMap != null && ifTangents != null) {
                        val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                        bumpNormal.outNormal
                    } else {
                        ifNormals.output
                    }
                    inMetallic = if (cfg.roughnessMap != null) {
                        textureSamplerNode(textureNode("tMetallic"), ifTexCoords!!.output, false).outColor
                    } else {
                        pushConstantNode1f("uMetallic").output
                    }
                    inRoughness = if (cfg.roughnessMap != null) {
                        textureSamplerNode(textureNode("tRoughness"), ifTexCoords!!.output, false).outColor
                    } else {
                        pushConstantNode1f("uRoughness").output
                    }
                    cfg.ambientOcclusionMap?.let {
                        inAmbientOccl = textureSamplerNode(textureNode("tAmbOccl"), ifTexCoords!!.output, false).outColor
                    }
                }
                val hdrToLdr = hdrToLdrNode(mat.outColor)
                colorOutput = hdrToLdr.outColor
            }
        }
    }

    class PbrConfig {
        var roughness = 0.5f
        var metallic = 0.0f
        var ambient = Color(0.03f, 0.03f, 0.03f, 1f)

        var albedoMap: Texture? = null
        var normalMap: Texture? = null
        var roughnessMap: Texture? = null
        var metallicMap: Texture? = null
        var ambientOcclusionMap: Texture? = null
        var displacementMap: Texture? = null

        var irradianceMap: CubeMapTexture? = null
        var reflectionMap: CubeMapTexture? = null
        var brdfLut: Texture? = null

        val isIbl: Boolean
            get() = irradianceMap != null && reflectionMap != null && brdfLut != null
    }
}