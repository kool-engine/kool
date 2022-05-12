package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.TextureSampler2d
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

open class PbrShader(cfg: PbrMaterialConfig, model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod
    private val isBlending = cfg.alphaMode is AlphaMode.Blend
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    // Simple material props
    val roughness = FloatInput("uRoughness", cfg.roughness)
    val metallic = FloatInput("uMetallic", cfg.metallic)
    val albedo = ColorInput("uAlbedo", cfg.albedo)
    val emissive = ColorInput("uEmissive", cfg.emissive)

    // Material maps
    val albedoMap = Texture2dInput("tAlbedo", cfg.albedoMap)
    val emissiveMap = Texture2dInput("tEmissive", cfg.emissiveMap)
    val normalMap = Texture2dInput("tNormal", cfg.normalMap)
    val roughnessMap = Texture2dInput(cfg.roughnessTexName, cfg.roughnessMap)
    val metallicMap = Texture2dInput(cfg.metallicTexName, cfg.metallicMap)
    val aoMap = Texture2dInput(cfg.aoTexName, cfg.aoMap)
    val displacementMap = Texture2dInput("tDisplacement", cfg.displacementMap)
    val displacementStrength = FloatInput("uDispStrength", cfg.displacementStrength)

    // Lighting props
    val ambient = ColorInput("uAmbient", Color(0.03f, 0.03f, 0.03f, 1f))
    val ambientShadowFactor = FloatInput("uAmbientShadowFactor", cfg.ambientShadowFactor)

    // Image based lighting maps
    val irradianceMap = TextureCubeInput("irradianceMap", cfg.environmentMaps?.irradianceMap)
    val reflectionMap = TextureCubeInput("reflectionMap", cfg.environmentMaps?.reflectionMap)
    val brdfLut = Texture2dInput("brdfLut")

    // Screen space ambient occlusion map
    val scrSpcAmbientOcclusionMap = Texture2dInput("ssaoMap", cfg.scrSpcAmbientOcclusionMap)

    // Refraction parameters
    val materialThickness = FloatInput("uMaterialThickness", cfg.materialThickness)
    val refractionColorMap = Texture2dInput("tRefractionColor", cfg.refractionColorMap)
    val refractionDepthMap = Texture2dInput("tRefractionDepth", cfg.refractionColorMap)

    private val depthSamplers = Array<TextureSampler2d?>(shadowMaps.size) { null }

    init{
        cfg.onPipelineSetup?.let {
            onPipelineSetup += { pb, _, _ -> this.it(pb) }
        }
        cfg.onPipelineCreated?.let {
            onPipelineCreated += { _, _, _ -> this.it() }
        }
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        brdfLut(ctx.defaultPbrBrdfLut)
        builder.cullMethod = cullMethod
        builder.blendMode = if (isBlending) BlendMode.BLEND_PREMULTIPLIED_ALPHA else BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        roughness.connect(model)
        metallic.connect(model)
        albedo.connect(model)
        emissive.connect(model)

        albedoMap.connect(model)
        emissiveMap.connect(model)
        normalMap.connect(model)
        roughnessMap.connect(model)
        metallicMap.connect(model)
        aoMap.connect(model)
        displacementMap.connect(model)
        displacementStrength.connect(model)

        ambient.connect(model)
        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<Texture2dNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
            ambientShadowFactor.connect(model)
        }

        irradianceMap.connect(model)
        reflectionMap.connect(model)
        brdfLut.connect(model)

        scrSpcAmbientOcclusionMap.connect(model)

        materialThickness.connect(model)
        refractionColorMap.connect(model)
        refractionDepthMap.connect(model)

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultPbrModel(cfg: PbrMaterialConfig) = ShaderModel("defaultPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val ifEmissive: StageInterfaceNode?
            val ifMetalRough: StageInterfaceNode?
            val mvpNode: UniformBufferMvp
            val shadowMapNodes = mutableListOf<ShadowMapNode>()

            vertexStage {
                mvpNode = mvpNode()

                val modelMatNd = namedVariable("modelMat", mvpNode.outModelMat)
                val mvpMatNd = namedVariable("mvpMat", mvpNode.outMvpMat)
                val modelMat = modelMatNd.output
                val mvpMat = mvpMatNd.output

                if (cfg.isInstanced) {
                    modelMatNd.input = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
                    mvpMatNd.input = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                }
                if (cfg.isSkinned) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output, cfg.maxJoints)
                    modelMatNd.input = multiplyNode(modelMatNd.input, skinNd.outJointMat).output
                    mvpMatNd.input = multiplyNode(mvpMatNd.input, skinNd.outJointMat).output
                }

                ifTexCoords = if (cfg.requiresTexCoords()) {
                    val texCoordInput = namedVariable("texCoordInput", attrTexCoords().output)
                    stageInterfaceNode("ifTexCoords", texCoordInput.output)
                } else {
                    null
                }
                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO
                            || (cfg.albedoSource == Albedo.TEXTURE_ALBEDO && cfg.albedoMapMode == AlbedoMapMode.MULTIPLY_BY_VERTEX)) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifEmissive = if (cfg.useVertexAttributeEmissive) {
                    stageInterfaceNode("ifEmissive", attributeNode(Attribute.EMISSIVE_COLOR).output)
                } else {
                    null
                }
                ifMetalRough = if (cfg.useVertexAttributeMetalRough) {
                    stageInterfaceNode("ifMetalRough", attributeNode(Attribute.METAL_ROUGH).output)
                } else {
                    null
                }

                val morphWeights = if (cfg.morphAttributes.isNotEmpty()) {
                    morphWeightsNode(cfg.morphAttributes.size)
                } else {
                    null
                }

                val localPosInput = namedVariable("localPosInput", attrPositions().output)
                val localNormalInput = namedVariable("localNormalInput", attrNormals().output)

                var morphPos = localPosInput.output
                cfg.morphAttributes.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { morphAttrib ->
                    val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                    val posDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                    morphPos = addNode(morphPos, posDisplacement.output).output
                }
                val localPosMorphed = namedVariable("localPosMorphed", morphPos)

                var morphNrm = localNormalInput.output
                cfg.morphAttributes.filter { it.name.startsWith(Attribute.NORMALS.name) }.forEach { morphAttrib ->
                    val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                    val nrmDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                    morphNrm = addNode(morphNrm, nrmDisplacement.output).output
                }
                val localNormalMorphed = namedVariable("localNormalMorphed", morphNrm)

                ifTangents = if (cfg.isNormalMapped) {
                    val localTangentInput = namedVariable("localTangentInput", attrTangents().output)
                    var localTan = splitNode(localTangentInput.output, "xyz").output
                    cfg.morphAttributes.filter { it.name.startsWith(Attribute.TANGENTS.name) }.forEach { morphAttrib ->
                        val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(morphAttrib), morphWeights!!)
                        val tanDisplacement = multiplyNode(attributeNode(morphAttrib).output, weight.outWeight)
                        localTan = addNode(localTan, tanDisplacement.output).output
                    }
                    val tan = vec3TransformNode(localTan, modelMat, 0f)
                    val tan4 = combineXyzWNode(tan.outVec3, splitNode(localTangentInput.output, "w").output)
                    stageInterfaceNode("ifTangents", tan4.output)
                } else {
                    null
                }

                val localPosDisplaced = namedVariable("localPosDisplaced", localPosMorphed.output)
                if (cfg.isDisplacementMapped) {
                    val dispTex = texture2dNode("tDisplacement")
                    val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, localPosMorphed.output, localNormalMorphed.output).apply {
                        inStrength = pushConstantNode1f("uDispStrength").output
                    }
                    localPosDisplaced.input = dispNd.outPosition
                }

                val worldPos = vec3TransformNode(localPosDisplaced.output, modelMat, 1f).outVec3
                ifFragPos = stageInterfaceNode("ifFragPos", worldPos)

                val worldNrm = vec3TransformNode(localNormalMorphed.output, modelMat, 0f).outVec3
                ifNormals = stageInterfaceNode("ifNormals", worldNrm)

                val viewPos = vec4TransformNode(worldPos, mvpNode.outViewMat).outVec4
                cfg.shadowMaps.forEachIndexed { i, map ->
                    when (map) {
                        is CascadedShadowMap -> shadowMapNodes += cascadedShadowMapNode(map, "depthMap_$i", viewPos, worldPos, worldNrm)
                        is SimpleShadowMap -> shadowMapNodes += simpleShadowMapNode(map, "depthMap_$i", worldPos, worldNrm)
                    }
                }
                positionOutput = vec4TransformNode(localPosDisplaced.output, mvpMat).outVec4
            }

            fragmentStage {
                var albedo = when (cfg.albedoSource) {
                    Albedo.VERTEX_ALBEDO -> ifColors!!.output
                    Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uAlbedo").output
                    Albedo.TEXTURE_ALBEDO -> {
                        val albedoSampler = texture2dSamplerNode(texture2dNode("tAlbedo"), ifTexCoords!!.output)
                        val albedoLin = gammaNode(albedoSampler.outColor)
                        when (cfg.albedoMapMode) {
                            AlbedoMapMode.UNMODIFIED -> albedoLin.outColor
                            AlbedoMapMode.MULTIPLY_BY_UNIFORM -> {
                                val fac = pushConstantNodeColor("uAlbedo").output
                                multiplyNode(albedoLin.outColor, fac).output
                            }
                            AlbedoMapMode.MULTIPLY_BY_VERTEX -> {
                                multiplyNode(albedoLin.outColor, ifColors!!.output).output
                            }
                        }
                    }
                    Albedo.CUBE_MAP_ALBEDO -> throw IllegalStateException("CUBE_MAP_ALBEDO is not allowed for PbrShader")
                }

                (cfg.alphaMode as? AlphaMode.Mask)?.let { mask ->
                    discardAlpha(splitNode(albedo, "a").output, constFloat(mask.cutOff))
                }
                if (cfg.alphaMode !is AlphaMode.Blend) {
                    albedo = combineXyzWNode(albedo, constFloat(1f)).output
                }

                val mvpFrag = mvpNode.addToStage(fragmentStageGraph)
                val lightNode = multiLightNode(ifFragPos.output, cfg.maxLights)
                shadowMapNodes.forEach {
                    lightNode.inShadowFacs[it.lightIndex] = it.outShadowFac
                }
                val avgShadow = lightNode.outAvgShadowFac
                var normal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(texture2dNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                    bumpNormal.inStrength = constFloat(cfg.normalStrength)
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }
                normal = normalizeNode(flipBacksideNormalNode(normal).outNormal).output
                val viewDir = viewDirNode(mvpFrag.outCamPos, ifFragPos.output).output

                val reflMap: TextureCubeNode?
                val brdfLut: Texture2dNode?
                val irrSampler: TextureCubeSamplerNode?

                if (cfg.isImageBasedLighting) {
                    val irrMap = textureCubeNode("irradianceMap")
                    irrSampler = textureCubeSamplerNode(irrMap, ifNormals.output)
                    reflMap = textureCubeNode("reflectionMap")
                    brdfLut = texture2dNode("brdfLut")
                } else {
                    irrSampler = null
                    reflMap = null
                    brdfLut = null
                }

                val mat = pbrMaterialNode(reflMap, brdfLut).apply {
                    inAlbedo = albedo
                    inNormal = normal
                    inAlwaysLit = if (cfg.isAlwaysLit) constInt(1) else constInt(0)
                    inFragPos = ifFragPos.output
                    inViewDir = viewDir

                    inLightCount = lightNode.outLightCount
                    inFragToLight = lightNode.outFragToLightDirection
                    inRadiance = lightNode.outRadiance

                    val irr = irrSampler?.outColor ?: pushConstantNodeColor("uAmbient").output
                    val ambientShadowFac = pushConstantNode1f("uAmbientShadowFactor").output
                    val shadowStr = multiplyNode(subtractNode(constFloat(1f), avgShadow).output, ambientShadowFac)
                    val ambientStr = subtractNode(constFloat(1f), shadowStr.output).output
                    inIrradiance = multiplyNode(irr, ambientStr).output
                    inReflectionStrength = ambientStr

                    if (cfg.isEmissiveMapped) {
                        val emissiveLin: ShaderNodeIoVar = if (ifEmissive != null) {
                            ifEmissive.output
                        } else {
                            val emissiveMap = texture2dSamplerNode(texture2dNode("tEmissive"), ifTexCoords!!.output).outColor
                            splitNode(gammaNode(emissiveMap).outColor, "rgb").output
                        }
                        inEmissive = if (cfg.isMultiplyEmissive) {
                            val fac = splitNode(pushConstantNodeColor("uEmissive").output, "rgb").output
                            multiplyNode(emissiveLin, fac).output
                        } else {
                            emissiveLin
                        }
                    } else {
                        inEmissive = pushConstantNodeColor("uEmissive").output
                    }

                    val rmoSamplers = mutableMapOf<String, ShaderNodeIoVar>()
                    if (cfg.isRoughnessMapped) {
                        val roughnessVal: ShaderNodeIoVar = if (ifMetalRough != null) {
                            splitNode(ifMetalRough.output, "y").output
                        } else {
                            val roughnessMap = rmoSamplers.getOrPut(cfg.roughnessTexName) { texture2dSamplerNode(texture2dNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor }
                            splitNode(roughnessMap, cfg.roughnessChannel).output
                        }
                        inRoughness = if (cfg.isMultiplyRoughness) {
                            val fac = pushConstantNode1f("uRoughness").output
                            multiplyNode(roughnessVal, fac).output
                        } else {
                            roughnessVal
                        }
                    } else {
                        inRoughness = pushConstantNode1f("uRoughness").output
                    }
                    if (cfg.isMetallicMapped) {
                        val metallicVal: ShaderNodeIoVar = if (ifMetalRough != null) {
                            splitNode(ifMetalRough.output, "x").output
                        } else {
                            val metallicMap = rmoSamplers.getOrPut(cfg.metallicTexName) { texture2dSamplerNode(texture2dNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
                            splitNode(metallicMap, cfg.metallicChannel).output
                        }
                        inMetallic = if (cfg.isMultiplyMetallic) {
                            val fac = pushConstantNode1f("uMetallic").output
                            multiplyNode(metallicVal, fac).output
                        } else {
                            metallicVal
                        }
                    } else {
                        inMetallic = pushConstantNode1f("uMetallic").output
                    }
                    var aoFactor = constFloat(1f)
                    if (cfg.isAoMapped) {
                        val occlusion = rmoSamplers.getOrPut(cfg.aoTexName) { texture2dSamplerNode(texture2dNode(cfg.aoTexName), ifTexCoords!!.output).outColor }
                        val rawAo = splitNode(occlusion, cfg.occlusionChannel).output
                        aoFactor = if (cfg.aoStrength != 1f) {
                            val str = cfg.aoStrength
                            addNode(constFloat(1f - str), multiplyNode(rawAo, str).output).output
                        } else {
                            rawAo
                        }
                    }

                    if (cfg.isScrSpcAmbientOcclusion) {
                        val aoMap = texture2dNode("ssaoMap")
                        val aoNode = addNode(AoMapSampleNode(aoMap, stage))
                        aoNode.inViewport = mvpFrag.outViewport

                        aoFactor = if (!cfg.isAoMapped) {
                            aoNode.outAo
                        } else {
                            multiplyNode(aoFactor, aoNode.outAo).output
                        }
                    }
                    inAmbientOccl = aoFactor
                }

                var outColor = mat.outColor
                if (cfg.isRefraction) {
                    val refrSampler = addNode(RefractionSamplerNode(stage)).apply {
                        reflectionMap = reflMap
                        refractionColor = texture2dNode("tRefractionColor")
                        view = mvpFrag.outViewMat
                        viewProj = multiplyNode(mvpFrag.outProjMat, mvpFrag.outViewMat).output
                        inMaterialThickness = pushConstantNode1f("uMaterialThickness").output
                        inFragPos = ifFragPos.output
                        inRefractionDir = refractNode(viewDir, normal, constFloat(1f / cfg.refractionIor)).outDirection
                        if (cfg.isRefractByDepthMap) {
                            refractionDepth = texture2dNode("tRefractionDepth")
                        }
                    }
                    val refrColor = refrSampler.outColor
                    val refrWeight = subtractNode(constFloat(1f), splitNode(mat.outColor, "a").output).output
                    val mixColor = multiplyNode(refrColor, refrWeight).output
                    outColor = combineXyzWNode(addNode(outColor, mixColor).output, constFloat(1f)).output
                }

                if (!cfg.isHdrOutput) {
                    outColor = hdrToLdrNode(outColor).outColor
                }
                when (cfg.alphaMode) {
                    is AlphaMode.Blend -> colorOutput(outColor)
                    is AlphaMode.Mask -> colorOutput(outColor, alpha = constFloat(1f))
                    is AlphaMode.Opaque -> colorOutput(outColor, alpha = constFloat(1f))
                }
            }
        }
    }
}