@file:Suppress("DEPRECATION")

package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap

@Deprecated("Replaced by KslBlinnPhongShader")
fun phongShader(cfgBlock: PhongShader.PhongConfig.() -> Unit): PhongShader {
    val cfg = PhongShader.PhongConfig()
    cfg.cfgBlock()
    return PhongShader(cfg)
}

@Deprecated("Replaced by KslBlinnPhongShader")
open class PhongShader(cfg: PhongConfig, model: ShaderModel = defaultPhongModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod
    private val isBlending = cfg.alphaMode is AlphaMode.Blend
    private val shadowMaps = Array(cfg.shadowMaps.size) { cfg.shadowMaps[it] }
    private val isReceivingShadow = cfg.shadowMaps.isNotEmpty()

    val shininess = FloatInput("uShininess", cfg.shininess)
    val specularIntensity = FloatInput("uSpecularIntensity", cfg.specularIntensity)
    val color = ColorInput("uColor", cfg.color)
    val colorMap = Texture2dInput("tColor", cfg.colorMap)
    val normalMap = Texture2dInput("tNormal", cfg.normalMap)
    val ambient = ColorInput("uAmbient", Color(0.22f, 0.22f, 0.22f))

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
        builder.cullMethod = cullMethod
        builder.blendMode = if (isBlending) BlendMode.BLEND_PREMULTIPLIED_ALPHA else BlendMode.DISABLED
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        shininess.connect(model)
        specularIntensity.connect(model)
        color.connect(model)
        colorMap.connect(model)
        normalMap.connect(model)
        ambient.connect(model)

        if (isReceivingShadow) {
            for (i in depthSamplers.indices) {
                val sampler = model.findNode<Texture2dNode>("depthMap_$i")?.sampler
                depthSamplers[i] = sampler
                shadowMaps[i].setupSampler(sampler)
            }
        }

        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultPhongModel(cfg: PhongConfig) = ShaderModel("defaultPhongModel()").apply {
            val ifNormals: StageInterfaceNode
            val ifColors: StageInterfaceNode?
            val ifTexCoords: StageInterfaceNode?
            val ifTangents: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode
            val mvpNode: UniformBufferMvp
            val shadowMapNodes = mutableListOf<ShadowMapNode>()

            vertexStage {
                val modelMat: ShaderNodeIoVar
                val mvpMat: ShaderNodeIoVar

                mvpNode = mvpNode()
                if (cfg.isInstanced) {
                    modelMat = multiplyNode(mvpNode.outModelMat, instanceAttrModelMat().output).output
                    mvpMat = multiplyNode(mvpNode.outMvpMat, instanceAttrModelMat().output).output
                } else {
                    modelMat = mvpNode.outModelMat
                    mvpMat = mvpNode.outMvpMat
                }

                ifColors = if (cfg.albedoSource == Albedo.VERTEX_ALBEDO) {
                    stageInterfaceNode("ifColors", attrColors().output)
                } else {
                    null
                }
                ifTexCoords = if (cfg.requiresTexCoords()) {
                    stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                } else {
                    null
                }
                ifTangents = if (cfg.isNormalMapped) {
                    val tanAttr = attrTangents().output
                    val tan = vec3TransformNode(splitNode(tanAttr, "xyz").output, modelMat, 0f)
                    val tan4 = combineXyzWNode(tan.outVec3, splitNode(tanAttr, "w").output)
                    stageInterfaceNode("ifTangents", tan4.output)
                } else {
                    null
                }

                val localPosInput = namedVariable("localPosInput", attrPositions().output)
                val localNormalInput = namedVariable("localNormalInput", attrNormals().output)

                val nrm = vec3TransformNode(localNormalInput.output, modelMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

                val worldPos = vec3TransformNode(localPosInput.output, modelMat, 1f).outVec3
                ifFragPos = stageInterfaceNode("ifFragPos", worldPos)

                val viewPos = vec4TransformNode(worldPos, mvpNode.outViewMat).outVec4

                cfg.shadowMaps.forEachIndexed { i, map ->
                    when (map) {
                        is CascadedShadowMap -> shadowMapNodes += cascadedShadowMapNode(map, "depthMap_$i", viewPos, worldPos)
                        is SimpleShadowMap -> shadowMapNodes += simpleShadowMapNode(map, "depthMap_$i", worldPos)
                    }
                }
                positionOutput = vec4TransformNode(localPosInput.output, mvpMat).outVec4
            }
            fragmentStage {
                val mvpFrag = mvpNode.addToStage(fragmentStageGraph)
                val lightNode = multiLightNode(ifFragPos.output, cfg.maxLights)
                shadowMapNodes.forEach {
                    lightNode.inShadowFacs[it.lightIndex] = it.outShadowFac
                }

                val albedo = when (cfg.albedoSource) {
                    Albedo.VERTEX_ALBEDO -> ifColors!!.output
                    Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uColor").output
                    Albedo.TEXTURE_ALBEDO -> {
                        texture2dSamplerNode(texture2dNode("tColor"), ifTexCoords!!.output, false).outColor
                    }
                    Albedo.CUBE_MAP_ALBEDO -> throw IllegalStateException("CUBE_MAP_ALBEDO is not allowed for PbrShader")
                }

                val normal = if (cfg.isNormalMapped && ifTangents != null) {
                    val bumpNormal = normalMapNode(texture2dNode("tNormal"), ifTexCoords!!.output, ifNormals.output, ifTangents.output)
                    bumpNormal.outNormal
                } else {
                    ifNormals.output
                }

                val phongMat = phongMaterialNode(albedo, normal, ifFragPos.output, mvpFrag.outCamPos).apply {
                    lightBacksides = cfg.lightBacksides
                    inShininess = pushConstantNode1f("uShininess").output
                    inSpecularIntensity = pushConstantNode1f("uSpecularIntensity").output
                    inAmbient = pushConstantNodeColor("uAmbient").output
                    inLightCount = lightNode.outLightCount
                    inFragToLight = lightNode.outFragToLightDirection
                    inRadiance = lightNode.outRadiance
                }
                colorOutput(phongMat.outColor)
            }
        }
    }

    class PhongConfig {
        var albedoSource = Albedo.VERTEX_ALBEDO
        var isNormalMapped = false

        // initial shader values
        var color = Color.GRAY
        var shininess = 20f
        var specularIntensity = 1f

        var maxLights = 4
        val shadowMaps = mutableListOf<ShadowMap>()
        var lightBacksides = false

        var cullMethod = CullMethod.CULL_BACK_FACES
        var alphaMode: AlphaMode = AlphaMode.Opaque()

        var isInstanced = false

        var colorMap: Texture2d? = null
        var normalMap: Texture2d? = null

        var onPipelineSetup: (PhongShader.(Pipeline.Builder) -> Unit)? = null
        var onPipelineCreated: (PhongShader.() -> Unit)? = null

        fun useColorMap(albedoMap: String) = useColorMap(Texture2d(albedoMap))

        fun useColorMap(albedoMap: Texture2d?) {
            this.colorMap = albedoMap
            albedoSource = Albedo.TEXTURE_ALBEDO
        }

        fun useNormalMap(normalMap: String) = useNormalMap(Texture2d(normalMap))

        fun useNormalMap(normalMap: Texture2d?) {
            this.normalMap = normalMap
            isNormalMapped = true
        }

        fun requiresTexCoords(): Boolean {
            return albedoSource == Albedo.TEXTURE_ALBEDO || isNormalMapped
        }
    }
}
