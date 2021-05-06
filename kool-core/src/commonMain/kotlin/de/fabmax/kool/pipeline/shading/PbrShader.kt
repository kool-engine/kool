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

open class PbrShader(cfg: PbrMaterialConfig, model: ShaderModel = defaultPbrModel(cfg)) : ModeledShader(model) {

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
    private var albedoSampler: TextureSampler2d? = null
    private var emissiveSampler: TextureSampler2d? = null
    private var normalSampler: TextureSampler2d? = null
    private var metallicSampler: TextureSampler2d? = null
    private var roughnessSampler: TextureSampler2d? = null
    private var occlusionSampler: TextureSampler2d? = null
    private var displacementSampler: TextureSampler2d? = null
    private var uDispStrength: PushConstantNode1f? = null

    private val metallicTexName = cfg.metallicTexName
    private val roughnessTexName = cfg.roughnessTexName
    private val occlusionTexName = cfg.occlusionTexName

    var albedoMap: Texture2d? = cfg.albedoMap
        set(value) {
            field = value
            albedoSampler?.texture = value
        }
    var emissiveMap: Texture2d? = cfg.emissiveMap
        set(value) {
            field = value
            emissiveSampler?.texture = value
        }
    var normalMap: Texture2d? = cfg.normalMap
        set(value) {
            field = value
            normalSampler?.texture = value
        }
    var metallicMap: Texture2d? = cfg.metallicMap
        set(value) {
            field = value
            metallicSampler?.texture = value
        }
    var roughnessMap: Texture2d? = cfg.roughnessMap
        set(value) {
            field = value
            roughnessSampler?.texture = value
        }
    var occlusionMap: Texture2d? = cfg.occlusionMap
        set(value) {
            field = value
            occlusionSampler?.texture = value
        }
    var displacementMap: Texture2d? = cfg.displacementMap
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
    private val depthSamplers = Array<TextureSampler2d?>(shadowMaps.size) { null }

    var ambient = Color(0.03f, 0.03f, 0.03f, 1f)
        set(value) {
            field = value
            uAmbient?.uniform?.value?.set(value)
        }

    private var uAmbientShadowFactor: Uniform1f? = null
    var ambientShadowFactor = cfg.ambientShadowFactor
        set(value) {
            field = value
            uAmbientShadowFactor?.value = value
        }

    // Image based lighting maps
    private var irradianceMapSampler: TextureSamplerCube? = null
    private var reflectionMapSampler: TextureSamplerCube? = null
    private var brdfLutSampler: TextureSampler2d? = null

    var irradianceMap: TextureCube? = cfg.environmentMaps?.irradianceMap
        set(value) {
            field = value
            irradianceMapSampler?.texture = value
        }
    var reflectionMap: TextureCube? = cfg.environmentMaps?.reflectionMap
        set(value) {
            field = value
            reflectionMapSampler?.texture = value
        }
    var brdfLut: Texture2d? = cfg.environmentMaps?.brdfLut
        set(value) {
            field = value
            brdfLutSampler?.texture = value
        }

    // Screen space ambient occlusion map
    private var ssaoSampler: TextureSampler2d? = null
    var scrSpcAmbientOcclusionMap: Texture2d? = cfg.scrSpcAmbientOcclusionMap
        set(value) {
            field = value
            ssaoSampler?.texture = value
        }

    // Refraction parameters
    private var uMaterialThickness: PushConstantNode1f? = null
    var materialThickness = cfg.materialThickness
        set(value) {
            field = value
            uMaterialThickness?.uniform?.value = value
        }

    private var refractionColorSampler: TextureSampler2d? = null
    var refractionColorMap: Texture2d? = cfg.refractionColorMap
        set(value) {
            field = value
            refractionColorSampler?.texture = value
        }

    private var refractionDepthSampler: TextureSampler2d? = null
    var refractionDepthMap: Texture2d? = cfg.refractionDepthMap
        set(value) {
            field = value
            refractionDepthSampler?.texture = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.cullMethod = cullMethod
        builder.blendMode = if (isBlending) BlendMode.BLEND_PREMULTIPLIED_ALPHA else BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
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
                val sampler = model.findNode<Texture2dNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
            uAmbientShadowFactor = model.findNode<PushConstantNode1f>("uAmbientShadowFactor")?.uniform
            uAmbientShadowFactor?.value = ambientShadowFactor
        }

        irradianceMapSampler = model.findNode<TextureCubeNode>("irradianceMap")?.sampler
        irradianceMapSampler?.let { it.texture = irradianceMap }
        reflectionMapSampler = model.findNode<TextureCubeNode>("reflectionMap")?.sampler
        reflectionMapSampler?.let { it.texture = reflectionMap }
        brdfLutSampler = model.findNode<Texture2dNode>("brdfLut")?.sampler
        brdfLutSampler?.let { it.texture = brdfLut }

        ssaoSampler = model.findNode<Texture2dNode>("ssaoMap")?.sampler
        ssaoSampler?.let { it.texture = scrSpcAmbientOcclusionMap }

        uMaterialThickness = model.findNode("uMaterialThickness")
        uMaterialThickness?.let { it.uniform.value = materialThickness }
        refractionColorSampler = model.findNode<Texture2dNode>("tRefractionColor")?.sampler
        refractionColorSampler?.let { it.texture = refractionColorMap }
        refractionDepthSampler = model.findNode<Texture2dNode>("tRefractionDepth")?.sampler
        refractionDepthSampler?.let { it.texture = refractionDepthMap }

        albedoSampler = model.findNode<Texture2dNode>("tAlbedo")?.sampler
        albedoSampler?.let { it.texture = albedoMap }
        emissiveSampler = model.findNode<Texture2dNode>("tEmissive")?.sampler
        emissiveSampler?.let { it.texture = emissiveMap }
        normalSampler = model.findNode<Texture2dNode>("tNormal")?.sampler
        normalSampler?.let { it.texture = normalMap }
        metallicSampler = model.findNode<Texture2dNode>(metallicTexName)?.sampler
        metallicSampler?.let { it.texture = metallicMap }
        roughnessSampler = model.findNode<Texture2dNode>(roughnessTexName)?.sampler
        roughnessSampler?.let { it.texture = roughnessMap }
        occlusionSampler = model.findNode<Texture2dNode>(occlusionTexName)?.sampler
        occlusionSampler?.let { it.texture = occlusionMap }
        displacementSampler = model.findNode<Texture2dNode>("tDisplacement")?.sampler
        displacementSampler?.let { it.texture = displacementMap }
        uDispStrength = model.findNode("uDispStrength")
        uDispStrength?.let { it.uniform.value = displacementStrength }

        super.onPipelineCreated(pipeline, mesh, ctx)
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

                val nrm = vec3TransformNode(localNormalMorphed.output, modelMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

                val viewPos = vec4TransformNode(worldPos, mvpNode.outViewMat).outVec4
                cfg.shadowMaps.forEachIndexed { i, map ->
                    when (map) {
                        is CascadedShadowMap -> shadowMapNodes += cascadedShadowMapNode(map, "depthMap_$i", viewPos, worldPos)
                        is SimpleShadowMap -> shadowMapNodes += simpleShadowMapNode(map, "depthMap_$i", worldPos)
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

                (cfg.alphaMode as? AlphaModeMask)?.let { mask ->
                    discardAlpha(splitNode(albedo, "a").output, constFloat(mask.cutOff))
                }
                if (cfg.alphaMode !is AlphaModeBlend) {
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
                        val emissive = texture2dSamplerNode(texture2dNode("tEmissive"), ifTexCoords!!.output).outColor
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
                        val roughness = texture2dSamplerNode(texture2dNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor
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
                        val metallic = rmoSamplers.getOrPut(cfg.metallicTexName) { texture2dSamplerNode(texture2dNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
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
                        val occlusion = rmoSamplers.getOrPut(cfg.occlusionTexName) { texture2dSamplerNode(texture2dNode(cfg.occlusionTexName), ifTexCoords!!.output).outColor }
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
                        val aoMap = texture2dNode("ssaoMap")
                        val aoNode = addNode(AoMapSampleNode(aoMap, stage))
                        aoNode.inViewport = mvpFrag.outViewport

                        aoFactor = if (!cfg.isOcclusionMapped) {
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
                    is AlphaModeBlend -> colorOutput(outColor)
                    is AlphaModeMask -> colorOutput(outColor, alpha = constFloat(1f))
                    is AlphaModeOpaque -> colorOutput(outColor, alpha = constFloat(1f))
                }
            }
        }
    }
}