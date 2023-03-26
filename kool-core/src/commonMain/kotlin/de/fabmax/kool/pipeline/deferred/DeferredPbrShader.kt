package de.fabmax.kool.pipeline.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.logE

inline fun deferredKslPbrShader(block: DeferredKslPbrShader.Config.() -> Unit): DeferredKslPbrShader {
    val cfg = DeferredKslPbrShader.Config()
    cfg.block()
    return DeferredKslPbrShader(cfg)
}

/**
 * 1st pass shader for deferred pbr shading: Renders view space position, normals, albedo, roughness, metallic and
 * texture-based AO into three separate texture outputs.
 */
open class DeferredKslPbrShader(cfg: Config) : KslShader(deferredPbrModel(cfg), cfg.pipelineCfg) {

    var color: Vec4f by colorUniform(cfg.colorCfg)
    var colorMap: Texture2d? by colorTexture(cfg.colorCfg)

    var normalMap: Texture2d? by texture2d(cfg.normalMapCfg.normalMapName, cfg.normalMapCfg.defaultNormalMap)
    var normalMapStrength: Float by propertyUniform(cfg.normalMapCfg.strengthCfg)

    var displacement: Float by propertyUniform(cfg.vertexCfg.displacementCfg)
    var displacementMap: Texture2d? by propertyTexture(cfg.vertexCfg.displacementCfg)

    var emission: Vec4f by colorUniform(cfg.emissionCfg)
    var emissionMap: Texture2d? by colorTexture(cfg.emissionCfg)

    var materialAo: Float by propertyUniform(cfg.aoCfg.materialAo)
    var materialAoMap: Texture2d? by propertyTexture(cfg.aoCfg.materialAo)

    var metallic: Float by propertyUniform(cfg.metallicCfg)
    var metallicMap: Texture2d? by propertyTexture(cfg.metallicCfg)

    var roughness: Float by propertyUniform(cfg.roughnessCfg)
    var roughnessMap: Texture2d? by propertyTexture(cfg.roughnessCfg)

    init {
        if (cfg.pipelineCfg.blendMode != BlendMode.DISABLED) {
            logE { "DeferredPbrShader is created with blendMode ${cfg.pipelineCfg.blendMode}, " +
                    "which results in undefined behavior (blendMode must be DISABLED)" }
        }
    }

    companion object {
        private fun deferredPbrModel(cfg: Config) = KslProgram("DeferredPbrShader").apply {
            val camData = cameraData()
            val positionViewSpace = interStageFloat3("positionWorldSpace")
            val normalViewSpace = interStageFloat3("normalWorldSpace")
            var tangentViewSpace: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>? = null

            val texCoordBlock: TexCoordAttributeBlock

            vertexStage {
                main {
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inModelMat(modelMatrix().matrix)
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                        inLocalNormal(vertexAttribFloat3(Attribute.NORMALS.name))

                        if (cfg.normalMapCfg.isNormalMapped) {
                            // if normal mapping is enabled, the input vertex data is expected to have a tangent attribute
                            inLocalTangent(vertexAttribFloat4(Attribute.TANGENTS.name))
                        }
                    }

                    // world position and normal are made available via ports for custom models to modify them
                    val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                    val worldNormal = float3Port("worldNormal", vertexBlock.outWorldNormal)

                    val viewPos = float4Var(camData.viewMat * float4Value(worldPos, 1f))
                    outPosition set camData.projMat * viewPos

                    positionViewSpace.input set viewPos.xyz
                    normalViewSpace.input set (camData.viewMat * float4Value(worldNormal, 0f)).xyz

                    if (cfg.normalMapCfg.isNormalMapped) {
                        tangentViewSpace = interStageFloat4().apply {
                            input.xyz set (camData.viewMat * float4Value(vertexBlock.outWorldTangent.xyz, 0f)).xyz
                            input.w set vertexBlock.outWorldTangent.w
                        }
                    }

                    // texCoordBlock is used by various other blocks to access texture coordinate vertex
                    // attributes (usually either none, or Attribute.TEXTURE_COORDS but there can be more)
                    texCoordBlock = texCoordAttributeBlock()
                }
            }
            fragmentStage {
                main {
                    // determine main color (albedo)
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val baseColorPort = float4Port("baseColor", colorBlock.outColor)
                    (cfg.alphaMode as? AlphaMode.Mask)?.let {
                        `if`(baseColorPort.a lt it.cutOff.const) {
                            discard()
                        }
                    }

                    val emissionBlock = fragmentColorBlock(cfg.emissionCfg)
                    val emissionColorPort = float4Port("emissionColor", emissionBlock.outColor)

                    val vertexNormal = float3Var(normalize(normalViewSpace.output))
                    if (cfg.pipelineCfg.cullMethod.isBackVisible && cfg.vertexCfg.isFlipBacksideNormals) {
                        `if`(!inIsFrontFacing) {
                            vertexNormal *= (-1f).const3
                        }
                    }

                    // do normal map computations (if enabled) and adjust material block input normal accordingly
                    val bumpedNormal = if (cfg.normalMapCfg.isNormalMapped) {
                        val normalMapStrength = fragmentPropertyBlock(cfg.normalMapCfg.strengthCfg).outProperty
                        normalMapBlock(cfg.normalMapCfg) {
                            inTangentWorldSpace(tangentViewSpace!!.output)
                            inNormalWorldSpace(vertexNormal)
                            inStrength(normalMapStrength)
                            inTexCoords(texCoordBlock.getAttributeCoords(cfg.normalMapCfg.coordAttribute))
                        }.outBumpNormal
                    } else {
                        vertexNormal
                    }

                    val normal = float3Port("normal", bumpedNormal)
                    val roughness = float1Port("roughness", fragmentPropertyBlock(cfg.roughnessCfg).outProperty)
                    val metallic = float1Port("metallic", fragmentPropertyBlock(cfg.metallicCfg).outProperty)
                    val aoFactor = float1Port("aoFactor", fragmentPropertyBlock(cfg.aoCfg.materialAo).outProperty)

                    colorOutput(positionViewSpace.output, cfg.materialFlags.toFloat().const, location = 0)
                    colorOutput(normal.output, roughness, location = 1)
                    colorOutput(baseColorPort.rgb, metallic, location = 2)
                    colorOutput(emissionColorPort.rgb, aoFactor, location = 3)
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }

    class Config : KslPbrShader.Config() {
        var materialFlags = 0

        init {
            pipelineCfg.blendMode = BlendMode.DISABLED
        }

        companion object {
            const val MATERIAL_FLAG_ALWAYS_LIT = 1
            const val MATERIAL_FLAG_IS_MOVING = 2
        }
    }
}

@Deprecated("Use DeferredKslPbrShader instead")
open class DeferredPbrShader(cfg: PbrMaterialConfig, model: ShaderModel = defaultMrtPbrModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod

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

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.cullMethod = cullMethod
        builder.blendMode = BlendMode.DISABLED
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

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        const val MATERIAL_FLAG_ALWAYS_LIT = 1
        const val MATERIAL_FLAG_IS_MOVING = 2

        fun defaultMrtPbrModel(cfg: PbrMaterialConfig) = ShaderModel("defaultMrtPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifViewPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val ifEmissive: StageInterfaceNode?
            val ifMetalRough: StageInterfaceNode?
            val mvpNode: UniformBufferMvp

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

                val modelViewMatNd = namedVariable("modelViewMat", multiplyNode(mvpNode.outViewMat, modelMat).output)
                val modelViewMat = modelViewMatNd.output

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
                    val tan = vec3TransformNode(localTan, modelViewMat, 0f)
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

                val viewNrm = vec3TransformNode(localNormalMorphed.output, modelViewMat, 0f).outVec3
                ifNormals = stageInterfaceNode("ifNormals", viewNrm)

                val pos = vec3TransformNode(localPosDisplaced.output, modelViewMat, 1f).outVec3
                ifViewPos = stageInterfaceNode("ifViewPos", pos)

                positionOutput = vec4TransformNode(localPosDisplaced.output, mvpMat).outVec4
            }
            fragmentStage {
                val viewPos = ifViewPos.output

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

                val emissive: ShaderNodeIoVar
                if (cfg.isEmissiveMapped) {
                    val emissiveLin: ShaderNodeIoVar = if (ifEmissive != null) {
                        ifEmissive.output
                    } else {
                        val emissiveMap = texture2dSamplerNode(texture2dNode("tEmissive"), ifTexCoords!!.output).outColor
                        splitNode(gammaNode(emissiveMap).outColor, "rgb").output
                    }
                    emissive = if (cfg.isMultiplyEmissive) {
                        val fac = splitNode(pushConstantNodeColor("uEmissive").output, "rgb").output
                        multiplyNode(emissiveLin, fac).output
                    } else {
                        emissiveLin
                    }
                } else {
                    emissive = pushConstantNodeColor("uEmissive").output
                }

                val normal = normalizeNode(ifNormals.output).output
                var viewNormal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(texture2dNode("tNormal"), ifTexCoords!!.output, normal, ifTangents.output)
                    bumpNormal.inStrength = ShaderNodeIoVar(ModelVar1fConst(cfg.normalStrength))
                    bumpNormal.outNormal
                } else {
                    normal
                }
                viewNormal = flipBacksideNormalNode(viewNormal).outNormal

                val roughness: ShaderNodeIoVar
                val metallic: ShaderNodeIoVar
                var aoFactor = constFloat(1f)

                val rmoSamplers = mutableMapOf<String, ShaderNodeIoVar>()
                if (cfg.isRoughnessMapped) {
                    val roughnessVal: ShaderNodeIoVar = if (ifMetalRough != null) {
                        splitNode(ifMetalRough.output, "y").output
                    } else {
                        val roughnessMap = rmoSamplers.getOrPut(cfg.roughnessTexName) { texture2dSamplerNode(texture2dNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor }
                        splitNode(roughnessMap, cfg.roughnessChannel).output
                    }
                    roughness = if (cfg.isMultiplyRoughness) {
                        val fac = pushConstantNode1f("uRoughness").output
                        multiplyNode(roughnessVal, fac).output
                    } else {
                        roughnessVal
                    }
                } else {
                    roughness = pushConstantNode1f("uRoughness").output
                }
                if (cfg.isMetallicMapped) {
                    val metallicVal: ShaderNodeIoVar = if (ifMetalRough != null) {
                        splitNode(ifMetalRough.output, "x").output
                    } else {
                        val metallicMap = rmoSamplers.getOrPut(cfg.metallicTexName) { texture2dSamplerNode(texture2dNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
                        splitNode(metallicMap, cfg.metallicChannel).output
                    }
                    metallic = if (cfg.isMultiplyMetallic) {
                        val fac = pushConstantNode1f("uMetallic").output
                        multiplyNode(metallicVal, fac).output
                    } else {
                        metallicVal
                    }
                } else {
                    metallic = pushConstantNode1f("uMetallic").output
                }
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

                val mrtMultiplexNode = addNode(MrtMultiplexNode(stage)).apply {
                    inViewPos = viewPos
                    inAlbedo = albedo
                    inEmissive = emissive
                    inViewNormal = viewNormal
                    inRoughness = roughness
                    inMetallic = metallic
                    inAo = aoFactor
                    inMaterialFlags = constFloat(cfg.materialFlags.toFloat())
                }

                colorOutput(channels = 4).apply {
                    inColors[0] = mrtMultiplexNode.outPositionFlags
                    inColors[1] = mrtMultiplexNode.outNormalRough
                    inColors[2] = mrtMultiplexNode.outAlbedoMetallic
                    inColors[3] = mrtMultiplexNode.outEmissiveAo
                }
            }
        }
    }

    class MrtMultiplexNode(graph: ShaderGraph) : ShaderNode("mrtMultiplex", graph, ShaderStage.FRAGMENT_SHADER.mask) {
        var inViewPos = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inAlbedo = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inEmissive = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inViewNormal = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))
        var inRoughness = ShaderNodeIoVar(ModelVar1fConst(0.5f))
        var inMetallic = ShaderNodeIoVar(ModelVar1fConst(0f))
        var inAo = ShaderNodeIoVar(ModelVar1fConst(1f))
        var inMaterialFlags = ShaderNodeIoVar(ModelVar1fConst(0f))

        val outPositionFlags = ShaderNodeIoVar(ModelVar4f("outPositionFlags"), this)
        val outNormalRough = ShaderNodeIoVar(ModelVar4f("outNormalRough"), this)
        val outAlbedoMetallic = ShaderNodeIoVar(ModelVar4f("outAlbedoMetallic"), this)
        val outEmissiveAo = ShaderNodeIoVar(ModelVar4f("outEmissiveAo"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos, inAlbedo, inEmissive, inViewNormal, inRoughness, inMetallic, inAo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outPositionFlags.declare()} = vec4(${inViewPos.ref3f()}, ${inMaterialFlags.ref1f()});
                ${outNormalRough.declare()} = vec4(${inViewNormal.ref3f()}, ${inRoughness.ref1f()});
                ${outAlbedoMetallic.declare()} = vec4(${inAlbedo.ref3f()}, ${inMetallic.ref1f()});
                ${outEmissiveAo.declare()} = vec4(${inEmissive.ref3f()}, ${inAo.ref1f()});
            """)
        }
    }

    class MrtDeMultiplexNode(graph: ShaderGraph) : ShaderNode("mrtDeMultiplex", graph, ShaderStage.FRAGMENT_SHADER.mask) {
        var inPositionFlags = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inNormalRough = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inAlbedoMetallic = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inEmissiveAo = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))

        val outViewPos = ShaderNodeIoVar(ModelVar4f("outViewPos"), this)
        val outAlbedo = ShaderNodeIoVar(ModelVar4f("outAlbedo"), this)
        val outEmissive = ShaderNodeIoVar(ModelVar3f("outEmissive"), this)
        val outViewNormal = ShaderNodeIoVar(ModelVar3f("outViewNormal"), this)
        val outRoughness = ShaderNodeIoVar(ModelVar1f("outRoughness"), this)
        val outMetallic = ShaderNodeIoVar(ModelVar1f("outMetallic"), this)
        val outAo = ShaderNodeIoVar(ModelVar1f("outAo"), this)
        val outMaterialFlags = ShaderNodeIoVar(ModelVar1i("outMaterialBits"), this)
        val outLightBacksides = ShaderNodeIoVar(ModelVar1i("outLightBacksides"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPositionFlags, inNormalRough, inAlbedoMetallic, inEmissiveAo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outViewPos.declare()} = vec4(${inPositionFlags.ref3f()}, 1.0);
                ${outAlbedo.declare()} = vec4(${inAlbedoMetallic.ref3f()}, 1.0);
                ${outEmissive.declare()} = ${inEmissiveAo.ref3f()};
                ${outViewNormal.declare()} = normalize(${inNormalRough.ref3f()});
                ${outRoughness.declare()} = ${inNormalRough.ref4f()}.a;
                ${outMetallic.declare()} = ${inAlbedoMetallic.ref4f()}.a;
                ${outAo.declare()} = ${inEmissiveAo.ref4f()}.a;
                ${outMaterialFlags.declare()} = int(${inPositionFlags.ref4f()}.a);
                ${outLightBacksides.declare()} = $outMaterialFlags & 1;
            """)
        }
    }
}
