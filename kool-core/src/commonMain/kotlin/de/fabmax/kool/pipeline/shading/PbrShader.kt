package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.SimpleShadowMap

fun pbrShader(cfgBlock: PbrMaterialConfig.() -> Unit): PbrShader {
    val cfg = PbrMaterialConfig()
    cfg.cfgBlock()
    return PbrShader(cfg)
}

class PbrShader(cfg: PbrMaterialConfig = PbrMaterialConfig(), model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod
    private val isBlending = cfg.alphaMode is AlphaModeBlend
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    // Simple material props
    private var uRoughness: PushConstantNode1f? = null
    private var uMetallic: PushConstantNode1f? = null
    private var uAlbedo: PushConstantNodeColor? = null
    private var uEmissive: PushConstantNodeColor? = null

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
    var albedo: Color = cfg.albedo
        set(value) {
            field = value
            uAlbedo?.uniform?.value?.set(value)
        }
    var emissive: Color = cfg.emissive
        set(value) {
            field = value
            uEmissive?.uniform?.value?.set(value)
        }

    // Material maps
    private var albedoSampler: TextureSampler? = null
    private var emissiveSampler: TextureSampler? = null
    private var normalSampler: TextureSampler? = null
    private var metallicSampler: TextureSampler? = null
    private var roughnessSampler: TextureSampler? = null
    private var occlusionSampler: TextureSampler? = null
    private var displacementSampler: TextureSampler? = null
    private var uDispStrength: PushConstantNode1f? = null

    private val metallicTexName = cfg.metallicTexName
    private val roughnessTexName = cfg.roughnessTexName
    private val occlusionTexName = cfg.occlusionTexName

    var albedoMap: Texture? = cfg.albedoMap
        set(value) {
            field = value
            albedoSampler?.texture = value
        }
    var emissiveMap: Texture? = cfg.emissiveMap
        set(value) {
            field = value
            emissiveSampler?.texture = value
        }
    var normalMap: Texture? = cfg.normalMap
        set(value) {
            field = value
            normalSampler?.texture = value
        }
    var metallicMap: Texture? = cfg.metallicMap
        set(value) {
            field = value
            metallicSampler?.texture = value
        }
    var roughnessMap: Texture? = cfg.roughnessMap
        set(value) {
            field = value
            roughnessSampler?.texture = value
        }
    var occlusionMap: Texture? = cfg.occlusionMap
        set(value) {
            field = value
            occlusionSampler?.texture = value
        }
    var displacementMap: Texture? = cfg.displacementMap
        set(value) {
            field = value
            displacementSampler?.texture = value
        }
    var displacementStrength = cfg.displacementStrength
        set(value) {
            field = value
            uDispStrength?.uniform?.value = value
        }

    // Lighting props
    private var uAmbient: PushConstantNodeColor? = null
    private val depthSamplers = Array<TextureSampler?>(shadowMaps.size) { null }

    var ambient = Color(0.03f, 0.03f, 0.03f, 1f)
        set(value) {
            field = value
            uAmbient?.uniform?.value?.set(value)
        }

    // Image based lighting maps
    private var irradianceMapSampler: CubeMapSampler? = null
    private var reflectionMapSampler: CubeMapSampler? = null
    private var brdfLutSampler: TextureSampler? = null

    var irradianceMap: CubeMapTexture? = cfg.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: CubeMapTexture? = cfg.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture? = cfg.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    // Screen space ambient occlusion map
    private var ssaoSampler: TextureSampler? = null
    var scrSpcAmbientOcclusionMap: Texture? = cfg.scrSpcAmbientOcclusionMap
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.cullMethod = cullMethod
        builder.blendMode = if (isBlending) BlendMode.BLEND_PREMULTIPLIED_ALPHA else BlendMode.DISABLED
        return super.createPipeline(mesh, builder, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline) {
        uMetallic = model.findNode("uMetallic")
        uMetallic?.let { it.uniform.value = metallic }
        uRoughness = model.findNode("uRoughness")
        uRoughness?.let { it.uniform.value = roughness }
        uAlbedo = model.findNode("uAlbedo")
        uAlbedo?.uniform?.value?.set(albedo)
        uEmissive = model.findNode("uEmissive")
        uEmissive?.uniform?.value?.set(emissive)

        uAmbient = model.findNode("uAmbient")
        uAmbient?.uniform?.value?.set(ambient)

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<TextureNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
        }

        irradianceMapSampler = model.findNode<CubeMapNode>("irradianceMap")?.sampler
        irradianceMapSampler?.let { it.texture = irradianceMap }
        reflectionMapSampler = model.findNode<CubeMapNode>("reflectionMap")?.sampler
        reflectionMapSampler?.let { it.texture = reflectionMap }
        brdfLutSampler = model.findNode<TextureNode>("brdfLut")?.sampler
        brdfLutSampler?.let { it.texture = brdfLut }

        ssaoSampler = model.findNode<TextureNode>("ssaoMap")?.sampler
        ssaoSampler?.let { it.texture = scrSpcAmbientOcclusionMap }

        albedoSampler = model.findNode<TextureNode>("tAlbedo")?.sampler
        albedoSampler?.let { it.texture = albedoMap }
        emissiveSampler = model.findNode<TextureNode>("tEmissive")?.sampler
        emissiveSampler?.let { it.texture = emissiveMap }
        normalSampler = model.findNode<TextureNode>("tNormal")?.sampler
        normalSampler?.let { it.texture = normalMap }
        metallicSampler = model.findNode<TextureNode>(metallicTexName)?.sampler
        metallicSampler?.let { it.texture = metallicMap }
        roughnessSampler = model.findNode<TextureNode>(roughnessTexName)?.sampler
        roughnessSampler?.let { it.texture = roughnessMap }
        occlusionSampler = model.findNode<TextureNode>(occlusionTexName)?.sampler
        occlusionSampler?.let { it.texture = occlusionMap }
        displacementSampler = model.findNode<TextureNode>("tDisplacement")?.sampler
        displacementSampler?.let { it.texture = displacementMap }
        uDispStrength = model.findNode("uDispStrength")
        uDispStrength?.let { it.uniform.value = displacementStrength }

        super.onPipelineCreated(pipeline)
    }

    companion object {
        fun defaultPbrModel(cfg: PbrMaterialConfig) = ShaderModel("defaultPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val mvpNode: UniformBufferMvp
            val shadowMapNodes = mutableListOf<ShadowMapNode>()

            vertexStage {
                var modelMat: ShaderNodeIoVar
                var mvpMat: ShaderNodeIoVar

                mvpNode = mvpNode()
                if (cfg.isInstanced) {
                    modelMat = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
                    mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                } else {
                    modelMat = mvpNode.outModelMat
                    mvpMat = mvpNode.outMvpMat
                }
                if (cfg.isSkinned) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output, cfg.maxJoints)
                    modelMat = multiplyNode(modelMat, skinNd.outJointMat).output
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                }

                ifTexCoords = if (cfg.requiresTexCoords()) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }
                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }

                val morphWeights = if (cfg.morphAttributes.isNotEmpty()) {
                    morphWeightsNode(cfg.morphAttributes.size)
                } else {
                    null
                }

                var localPos = attrPositions().output
                cfg.morphAttributes.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { morphAttrib ->
                    val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                    val posDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                    localPos = addNode(localPos, posDisplacement.output).output
                }

                var localNrm = attrNormals().output
                cfg.morphAttributes.filter { it.name.startsWith(Attribute.NORMALS.name) }.forEach { morphAttrib ->
                    val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                    val nrmDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                    localNrm = addNode(localNrm, nrmDisplacement.output).output
                }

                ifTangents = if (cfg.isNormalMapped) {
                    val tanAttr = attrTangents().output
                    var localTan = splitNode(tanAttr, "xyz").output
                    cfg.morphAttributes.filter { it.name.startsWith(Attribute.TANGENTS.name) }.forEach { morphAttrib ->
                        val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                        val tanDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                        localTan = addNode(localTan, tanDisplacement.output).output
                    }
                    val tan = vec3TransformNode(localTan, modelMat, 0f)
                    val tan4 = combineXyzWNode(tan.outVec3, splitNode(tanAttr, "w").output)
                    stageInterfaceNode("ifTangents", tan4.output)
                } else {
                    null
                }

                if (cfg.isDisplacementMapped) {
                    val dispTex = textureNode("tDisplacement")
                    val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, localPos, localNrm).apply {
                        inStrength = pushConstantNode1f("uDispStrength").output
                    }
                    localPos = dispNd.outPosition
                }

                val worldPos = vec3TransformNode(localPos, modelMat, 1f).outVec3
                ifFragPos = stageInterfaceNode("ifFragPos", worldPos)

                val nrm = vec3TransformNode(localNrm, modelMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

                val viewPos = vec4TransformNode(worldPos, mvpNode.outViewMat).outVec4
                cfg.shadowMaps.forEachIndexed { i, map ->
                    when (map) {
                        is CascadedShadowMap -> shadowMapNodes += cascadedShadowMapNode(map, "depthMap_$i", viewPos, worldPos)
                        is SimpleShadowMap -> shadowMapNodes += simpleShadowMapNode(map, "depthMap_$i", worldPos)
                    }
                }
                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }

            fragmentStage {
                var albedo = when (cfg.albedoSource) {
                    Albedo.VERTEX_ALBEDO -> ifColors!!.output
                    Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                    Albedo.TEXTURE_ALBEDO -> {
                        val albedoSampler = textureSamplerNode(textureNode("tAlbedo"), ifTexCoords!!.output)
                        val albedoLin = gammaNode(albedoSampler.outColor)
                        if (cfg.isMultiplyAlbedoMap) {
                            val fac = pushConstantNodeColor("uAlbedo").output
                            multiplyNode(albedoLin.outColor, fac).output
                        } else {
                            albedoLin.outColor
                        }
                    }
                }

                (cfg.alphaMode as? AlphaModeMask)?.let { mask ->
                    discardAlpha(splitNode(albedo, "a").output, constFloat(mask.cutOff))
                }
                if (cfg.alphaMode !is AlphaModeBlend) {
                    albedo = combineXyzWNode(albedo, constFloat(1f)).output
                }

                val mvpFrag = mvpNode.addToStage(fragmentStageGraph)
                val lightNode = multiLightNode(cfg.maxLights)
                shadowMapNodes.forEach {
                    lightNode.inShaodwFacs[it.lightIndex] = it.outShadowFac
                }

                val reflMap: CubeMapNode?
                val brdfLut: TextureNode?
                val irrSampler: CubeMapSamplerNode?

                if (cfg.isImageBasedLighting) {
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
                    lightBacksides = cfg.lightBacksides
                    inFragPos = ifFragPos.output
                    inCamPos = mvpFrag.outCamPos

                    inIrradiance = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output

                    inAlbedo = albedo
                    inNormal = if (cfg.isNormalMapped && ifTangents != null) {
                        val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                        bumpNormal.inStrength = constFloat(cfg.normalStrength)
                        bumpNormal.outNormal
                    } else {
                        ifNormals.output
                    }
                    inNormal = flipBacksideNormalNode(inNormal).outNormal

                    if (cfg.isEmissiveMapped) {
                        val emissive = textureSamplerNode(textureNode("tEmissive"), ifTexCoords!!.output).outColor
                        val emissiveLin = gammaNode(emissive).outColor
                        inEmissive = if (cfg.isMultiplyEmissiveMap) {
                            val fac = pushConstantNodeColor("uEmissive").output
                            multiplyNode(emissiveLin, fac).output
                        } else {
                            emissiveLin
                        }
                    } else {
                        inEmissive = pushConstantNodeColor("uEmissive").output
                    }

                    val rmoSamplers = mutableMapOf<String, ShaderNodeIoVar>()
                    if (cfg.isRoughnessMapped) {
                        val roughness = textureSamplerNode(textureNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor
                        rmoSamplers[cfg.roughnessTexName] = roughness
                        val rawRoughness = splitNode(roughness, cfg.roughnessChannel).output
                        inRoughness = if (cfg.isMultiplyRoughnessMap) {
                            val fac = pushConstantNode1f("uRoughness").output
                            multiplyNode(rawRoughness, fac).output
                        } else {
                            rawRoughness
                        }
                    } else {
                        inRoughness = pushConstantNode1f("uRoughness").output
                    }
                    if (cfg.isMetallicMapped) {
                        val metallic = rmoSamplers.getOrPut(cfg.metallicTexName) { textureSamplerNode(textureNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
                        rmoSamplers[cfg.metallicTexName] = metallic
                        val rawMetallic = splitNode(metallic, cfg.metallicChannel).output
                        inMetallic = if (cfg.isMultiplyMetallicMap) {
                            val fac = pushConstantNode1f("uMetallic").output
                            multiplyNode(rawMetallic, fac).output
                        } else {
                            rawMetallic
                        }
                    } else {
                        inMetallic = pushConstantNode1f("uMetallic").output
                    }
                    var aoFactor = constFloat(1f)
                    if (cfg.isOcclusionMapped) {
                        val occlusion = rmoSamplers.getOrPut(cfg.occlusionTexName) { textureSamplerNode(textureNode(cfg.occlusionTexName), ifTexCoords!!.output).outColor }
                        rmoSamplers[cfg.occlusionTexName] = occlusion
                        val rawAo = splitNode(occlusion, cfg.occlusionChannel).output
                        aoFactor = if (cfg.occlusionStrength != 1f) {
                            val str = cfg.occlusionStrength
                            addNode(constFloat(1f - str), multiplyNode(rawAo, str).output).output
                        } else {
                            rawAo
                        }
                    }

                    if (cfg.isScrSpcAmbientOcclusion) {
                        val aoMap = textureNode("ssaoMap")
                        val aoNode = addNode(AoMapSampleNode(aoMap, graph))
                        aoNode.inViewport = mvpFrag.outViewport

                        aoFactor = if (!cfg.isOcclusionMapped) {
                            aoNode.outAo
                        } else {
                            multiplyNode(aoFactor, aoNode.outAo).output
                        }
                    }
                    inAmbientOccl = aoFactor
                }

                val matOutColor = if (cfg.isHdrOutput) {
                    mat.outColor
                } else {
                    hdrToLdrNode(mat.outColor).outColor
                }
                when (cfg.alphaMode) {
                    is AlphaModeBlend -> colorOutput(matOutColor)
                    is AlphaModeMask -> colorOutput(matOutColor, alpha = constFloat(1f))
                    is AlphaModeOpaque -> colorOutput(matOutColor, alpha = constFloat(1f))
                }
            }
        }
    }
}