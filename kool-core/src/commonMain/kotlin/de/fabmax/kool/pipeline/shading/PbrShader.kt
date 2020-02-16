package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.util.Color

class PbrShader(cfg: PbrConfig = PbrConfig(), model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {

    // Simple material props
    private var uRoughness: PushConstantNode1f? = null
    private var uMetallic: PushConstantNode1f? = null
    private var uAlbedo: PushConstantNodeColor? = null

    var metallic = 0f
        set(value) {
            field = value
            uMetallic?.uniform?.value = value
        }

    var roughness = 0.5f
        set(value) {
            field = value
            uRoughness?.uniform?.value = value
        }

    var albedo: Color = Color.WHITE
        set(value) {
            field = value
            uAlbedo?.uniform?.value?.set(value)
        }

    // Material maps
    private var albedoSampler: TextureSampler? = null
    private var normalSampler: TextureSampler? = null
    private var metallicSampler: TextureSampler? = null
    private var roughnessSampler: TextureSampler? = null
    private var ambientOcclusionSampler: TextureSampler? = null
    private var displacementSampler: TextureSampler? = null
    private var uDispStrength: PushConstantNode1f? = null

    var albedoMap: Texture? = null
        set(value) {
            field = value
            albedoSampler?.texture = value
        }
    var normalMap: Texture? = null
        set(value) {
            field = value
            normalSampler?.texture = value
        }
    var metallicMap: Texture? = null
        set(value) {
            field = value
            metallicSampler?.texture = value
        }
    var roughnessMap: Texture? = null
        set(value) {
            field = value
            roughnessSampler?.texture = value
        }
    var ambientOcclusionMap: Texture? = null
        set(value) {
            field = value
            ambientOcclusionSampler?.texture = value
        }
    var displacementMap: Texture? = null
        set(value) {
            field = value
            displacementSampler?.texture = value
        }
    var displacementStrength = 0.1f
        set(value) {
            field = value
            uDispStrength?.uniform?.value = value
        }

    // Simple lighting props
    private var uAmbient: PushConstantNodeColor? = null

    var ambient = Color(0.03f, 0.03f, 0.03f, 1f)
        set(value) {
            field = value
            uAmbient?.uniform?.value?.set(value)
        }

    // Image based lighting maps
    private var irradianceMapSampler: CubeMapSampler? = null
    private var reflectionMapSampler: CubeMapSampler? = null
    private var brdfLutSampler: TextureSampler? = null

    var irradianceMap: CubeMapTexture? = null
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: CubeMapTexture? = null
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture? = null
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
        uAlbedo = model.findNode("uAlbedo")
        uAlbedo?.uniform?.value?.set(albedo)
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
        uDispStrength = model.findNode("uDispStrength")
        uDispStrength?.let { it.uniform.value = displacementStrength }
    }

    companion object {
        fun defaultPbrModel(cfg: PbrConfig) = ShaderModel("defaultPbrModel()").apply {
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

                ifTexCoords = if (cfg.requiresTexCoords()) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }

                val worldPos = if (cfg.isDisplacementMapped) {
                    val dispTex = textureNode("tDisplacement")
                    val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, attrPositions().output, attrNormals().output).apply {
                        inStrength = pushConstantNode1f("uDispStrength").output
                    }
                    dispNd.outPosition
                } else {
                    attrPositions().output
                }
                val pos = transformNode(worldPos, mvp.outModelMat, 1f).output
                ifFragPos = stageInterfaceNode("ifFragPos", pos)

                ifColors = if (cfg.albedoSource == AlbedoSource.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTangents = if (cfg.isNormalMapped) {
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

                if (cfg.isImageBasedLighting) {
                    val irrMap = cubeMapNode("irradianceMap")
                    irrSampler = cubeMapSamplerNode(irrMap, ifNormals.output, false)
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

                    inAlbedo = when (cfg.albedoSource) {
                        AlbedoSource.VERTEX_ALBEDO -> ifColors!!.output
                        AlbedoSource.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                        AlbedoSource.TEXTURE_ALBEDO -> {
                            val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output, false)
                            val albedoLin = gammaNode(albedoSampler.outColor)
                            albedoLin.outColor
                        }
                    }
                    inNormal = if (cfg.isNormalMapped && ifTangents != null) {
                        val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                        bumpNormal.outNormal
                    } else {
                        ifNormals.output
                    }
                    inMetallic = if (cfg.isMetallicMapped) {
                        textureSamplerNode(textureNode("tMetallic"), ifTexCoords!!.output, false).outColor
                    } else {
                        pushConstantNode1f("uMetallic").output
                    }
                    inRoughness = if (cfg.isRoughnessMapped) {
                        textureSamplerNode(textureNode("tRoughness"), ifTexCoords!!.output, false).outColor
                    } else {
                        pushConstantNode1f("uRoughness").output
                    }
                    if (cfg.isAmbientOcclusionMapped) {
                        inAmbientOccl = textureSamplerNode(textureNode("tAmbOccl"), ifTexCoords!!.output, false).outColor
                    }
                }
                val hdrToLdr = hdrToLdrNode(mat.outColor)
                colorOutput = hdrToLdr.outColor
            }
        }
    }

    enum class AlbedoSource {
        STATIC_ALBEDO,
        TEXTURE_ALBEDO,
        VERTEX_ALBEDO
    }

    class PbrConfig {
        var albedoSource = AlbedoSource.VERTEX_ALBEDO
        var isNormalMapped = false
        var isRoughnessMapped = false
        var isMetallicMapped = false
        var isAmbientOcclusionMapped = false
        var isDisplacementMapped = false

        var isImageBasedLighting = false

        fun requiresTexCoords(): Boolean {
            return albedoSource == AlbedoSource.TEXTURE_ALBEDO ||
                    isNormalMapped ||
                    isRoughnessMapped ||
                    isMetallicMapped ||
                    isAmbientOcclusionMapped ||
                    isDisplacementMapped
        }

//        var albedoMap: Texture? = null
//        var normalMap: Texture? = null
//        var roughnessMap: Texture? = null
//        var metallicMap: Texture? = null
//        var ambientOcclusionMap: Texture? = null
//        var displacementMap: Texture? = null
//
//        var irradianceMap: CubeMapTexture? = null
//        var reflectionMap: CubeMapTexture? = null
//        var brdfLut: Texture? = null
//
//        val isIbl: Boolean
//            get() = irradianceMap != null && reflectionMap != null && brdfLut != null
    }
}