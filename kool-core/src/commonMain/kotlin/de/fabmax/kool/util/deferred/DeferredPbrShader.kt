package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

/**
 * 1st pass shader for deferred pbr shading: Renders view space position, normals, albedo, roughness, metallic and
 * texture-based AO into three separate texture outputs.
 */
class DeferredPbrShader(cfg: PbrMaterialConfig, model: ShaderModel = defaultMrtPbrModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod

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
    var displacementStrength = 0.1f
        set(value) {
            field = value
            uDispStrength?.uniform?.value = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.cullMethod = cullMethod
        builder.blendMode = BlendMode.DISABLED
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

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultMrtPbrModel(cfg: PbrMaterialConfig) = ShaderModel("defaultMrtPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifNormals: StageInterfaceNode
            val ifTangents: StageInterfaceNode?
            val ifViewPos: StageInterfaceNode
            val ifTexCoords: StageInterfaceNode?
            val mvpNode: UniformBufferMvp

            vertexStage {
                var modelViewMat: ShaderNodeIoVar
                var mvpMat: ShaderNodeIoVar

                mvpNode = mvpNode()
                if (cfg.isInstanced) {
                    val modelMat = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
                    modelViewMat = multiplyNode(mvpNode.outViewMat, modelMat).output
                    mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                } else {
                    modelViewMat = multiplyNode(mvpNode.outViewMat, mvpNode.outModelMat).output
                    mvpMat = mvpNode.outMvpMat
                }
                if (cfg.isSkinned) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output, cfg.maxJoints)
                    modelViewMat = multiplyNode(modelViewMat, skinNd.outJointMat).output
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
                    val tan = vec3TransformNode(localTan, modelViewMat, 0f)
                    val tan4 = combineXyzWNode(tan.outVec3, splitNode(tanAttr, "w").output)
                    stageInterfaceNode("ifTangents", tan4.output)
                } else {
                    null
                }

                if (cfg.isDisplacementMapped) {
                    val dispTex = textureNode("tDisplacement")
                    val dispNd = displacementMapNode(dispTex, ifTexCoords!!.input, attrPositions().output, attrNormals().output).apply {
                        inStrength = pushConstantNode1f("uDispStrength").output
                    }
                    localPos = dispNd.outPosition
                }

                val viewNrm = vec3TransformNode(attrNormals().output, modelViewMat, 0f).outVec3
                ifNormals = stageInterfaceNode("ifNormals", viewNrm)

                val pos = vec3TransformNode(localPos, modelViewMat, 1f).outVec3
                ifViewPos = stageInterfaceNode("ifViewPos", pos)

                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage {
                val viewPos = ifViewPos.output

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

                val emissive = if (cfg.isEmissiveMapped) {
                    val emissiveTex = textureSamplerNode(textureNode("tEmissive"), ifTexCoords!!.output).outColor
                    val emissiveLin = gammaNode(emissiveTex).outColor
                    if (cfg.isMultiplyEmissiveMap) {
                        val fac = pushConstantNodeColor("uEmissive").output
                        multiplyNode(emissiveLin, fac).output
                    } else {
                        emissiveLin
                    }
                } else {
                    pushConstantNodeColor("uEmissive").output
                }

                var viewNormal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(textureNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                    bumpNormal.inStrength = ShaderNodeIoVar(ModelVar1fConst(cfg.normalStrength))
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }
                viewNormal = flipBacksideNormalNode(viewNormal).outNormal

                val roughness: ShaderNodeIoVar
                val metallic: ShaderNodeIoVar
                var aoFactor = constFloat(1f)

                val rmoSamplers = mutableMapOf<String, ShaderNodeIoVar>()
                if (cfg.isRoughnessMapped) {
                    val roughnessSampler = textureSamplerNode(textureNode(cfg.roughnessTexName), ifTexCoords!!.output).outColor
                    rmoSamplers[cfg.roughnessTexName] = roughnessSampler
                    val rawRoughness = splitNode(roughnessSampler, cfg.roughnessChannel).output
                    roughness = if (cfg.isMultiplyRoughnessMap) {
                        val fac = pushConstantNode1f("uRoughness").output
                        multiplyNode(rawRoughness, fac).output
                    } else {
                        rawRoughness
                    }
                } else {
                    roughness = pushConstantNode1f("uRoughness").output
                }
                if (cfg.isMetallicMapped) {
                    val metallicSampler = rmoSamplers.getOrPut(cfg.metallicTexName) { textureSamplerNode(textureNode(cfg.metallicTexName), ifTexCoords!!.output).outColor }
                    rmoSamplers[cfg.metallicTexName] = metallicSampler
                    val rawMetallic = splitNode(metallicSampler, cfg.metallicChannel).output
                    metallic = if (cfg.isMultiplyMetallicMap) {
                        val fac = pushConstantNode1f("uMetallic").output
                        multiplyNode(rawMetallic, fac).output
                    } else {
                        rawMetallic
                    }
                } else {
                    metallic = pushConstantNode1f("uMetallic").output
                }
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

                val mrtMultiplexNode = addNode(MrtMultiplexNode(stage)).apply {
                    inViewPos = viewPos
                    inAlbedo = albedo
                    inEmissive = emissive
                    inViewNormal = viewNormal
                    inRoughness = roughness
                    inMetallic = metallic
                    inAo = aoFactor
                }

                colorOutput(channels = 4).apply {
                    inColors[0] = mrtMultiplexNode.outPositionAo
                    inColors[1] = mrtMultiplexNode.outNormalRough
                    inColors[2] = mrtMultiplexNode.outAlbedoMetallic
                    // fixme: depending on mrt pass setup emissive color attachment might not exist, in this case
                    //  writing to it isn't particularly good style (and produces a Vulkan Validation Layer warning)
                    //  however written values are simply discarded and shouldn't do much harm
                    inColors[3] = mrtMultiplexNode.outEmissive
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

        val outPositionAo = ShaderNodeIoVar(ModelVar4f("outPositionAo"), this)
        val outNormalRough = ShaderNodeIoVar(ModelVar4f("outNormalRough"), this)
        val outAlbedoMetallic = ShaderNodeIoVar(ModelVar4f("outAlbedoMetallic"), this)
        val outEmissive = ShaderNodeIoVar(ModelVar4f("outEmissive"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inViewPos, inAlbedo, inViewNormal, inRoughness, inMetallic, inAo)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outPositionAo.declare()} = vec4(${inViewPos.ref3f()}, ${inAo.ref1f()});
                ${outNormalRough.declare()} = vec4(normalize(${inViewNormal.ref3f()}), ${inRoughness.ref1f()});
                ${outAlbedoMetallic.declare()} = vec4(${inAlbedo.ref3f()}, ${inMetallic.ref1f()});
                ${outEmissive.declare()} = vec4(${inEmissive.ref3f()}, 1.0);
            """)
        }
    }

    class MrtDeMultiplexNode(graph: ShaderGraph) : ShaderNode("mrtDeMultiplex", graph, ShaderStage.FRAGMENT_SHADER.mask) {
        var inPositionAo = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inNormalRough = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inAlbedoMetallic = ShaderNodeIoVar(ModelVar4fConst(Vec4f.ZERO))
        var inEmissive = ShaderNodeIoVar(ModelVar3fConst(Vec3f.ZERO))

        val outViewPos = ShaderNodeIoVar(ModelVar4f("outViewPos"), this)
        val outAlbedo = ShaderNodeIoVar(ModelVar4f("outAlbedo"), this)
        val outEmissive = ShaderNodeIoVar(ModelVar3f("outEmissive"), this)
        val outViewNormal = ShaderNodeIoVar(ModelVar3f("outViewNormal"), this)
        val outRoughness = ShaderNodeIoVar(ModelVar1f("outRoughness"), this)
        val outMetallic = ShaderNodeIoVar(ModelVar1f("outMetallic"), this)
        val outAo = ShaderNodeIoVar(ModelVar1f("outAo"), this)

        override fun setup(shaderGraph: ShaderGraph) {
            super.setup(shaderGraph)
            dependsOn(inPositionAo, inNormalRough, inAlbedoMetallic)
        }

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                ${outViewPos.declare()} = vec4(${inPositionAo.ref3f()}, 1.0);
                ${outAlbedo.declare()} = vec4(${inAlbedoMetallic.ref3f()}, 1.0);
                ${outEmissive.declare()} = ${inEmissive.ref3f()};
                ${outViewNormal.declare()} = vec3(${inNormalRough.ref3f()});
                ${outRoughness.declare()} = ${inNormalRough.ref4f()}.a;
                ${outMetallic.declare()} = ${inAlbedoMetallic.ref4f()}.a;
                ${outAo.declare()} = ${inPositionAo.ref4f()}.a;
            """)
        }
    }
}