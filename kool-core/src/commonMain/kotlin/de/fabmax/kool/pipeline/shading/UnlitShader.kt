package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

inline fun unlitShader(block: UnlitMaterialConfig.() -> Unit): UnlitShader {
    val cfg = UnlitMaterialConfig()
    cfg.block()
    return UnlitShader(cfg)
}

open class UnlitShader(cfg: UnlitMaterialConfig, model: ShaderModel = defaultUnlitModel(cfg)) : ModeledShader(model) {

    private val cullMethod = cfg.cullMethod
    private val isBlending = cfg.alphaMode is AlphaModeBlend
    private val lineWidth = cfg.lineWidth

    private var uColor: PushConstantNodeColor? = null
    var color: Color = cfg.color
        set(value) {
            field = value
            uColor?.uniform?.value?.set(value)
        }

    private var colorSampler: TextureSampler2d? = null
    var colorMap: Texture2d? = cfg.colorMap
        set(value) {
            field = value
            colorSampler?.texture = value
        }

    private var colorCubeSampler: TextureSamplerCube? = null
    var colorCubeMap: TextureCube? = cfg.colorCubeMap
        set(value) {
            field = value
            colorCubeSampler?.texture = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.cullMethod = cullMethod
        builder.blendMode = if (isBlending) BlendMode.BLEND_PREMULTIPLIED_ALPHA else BlendMode.DISABLED
        builder.lineWidth = lineWidth
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        uColor = model.findNode("uColor")
        uColor?.uniform?.value?.set(color)
        colorSampler = model.findNode<Texture2dNode>("tColor")?.sampler
        colorSampler?.let { it.texture = colorMap }
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultUnlitModel(cfg: UnlitMaterialConfig) = ShaderModel("defaultPbrModel()").apply {
            val ifColors: StageInterfaceNode?
            val ifTexCoords: StageInterfaceNode?
            val ifFragPos: StageInterfaceNode?

            vertexStage {
                var modelMat: ShaderNodeIoVar
                var mvpMat: ShaderNodeIoVar

                val mvpNode = mvpNode()
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
                ifColors = if (cfg.colorSource == Albedo.VERTEX_ALBEDO) {
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

                ifFragPos = if (cfg.colorSource == Albedo.CUBE_MAP_ALBEDO) {
                    val worldPos = vec3TransformNode(localPos, modelMat, 1f).outVec3
                    stageInterfaceNode("ifFragPos", worldPos)
                } else {
                    null
                }
                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }

            fragmentStage {
                var color = when (cfg.colorSource) {
                    Albedo.VERTEX_ALBEDO -> ifColors!!.output
                    Albedo.STATIC_ALBEDO -> pushConstantNodeColor("uColor").output
                    Albedo.TEXTURE_ALBEDO -> {
                        val colorSampler = texture2dSamplerNode(texture2dNode("tColor"), ifTexCoords!!.output)
                        if (cfg.isMultiplyColorMap) {
                            val fac = pushConstantNodeColor("uColor").output
                            multiplyNode(colorSampler.outColor, fac).output
                        } else {
                            colorSampler.outColor
                        }
                    }
                    Albedo.CUBE_MAP_ALBEDO -> {
                        val colorSampler = textureCubeSamplerNode(textureCubeNode("tCubeColor"), ifFragPos!!.output)
                        if (cfg.isMultiplyColorMap) {
                            val fac = pushConstantNodeColor("uColor").output
                            multiplyNode(colorSampler.outColor, fac).output
                        } else {
                            colorSampler.outColor
                        }
                    }
                }

                (cfg.alphaMode as? AlphaModeMask)?.let { mask ->
                    discardAlpha(splitNode(color, "a").output, constFloat(mask.cutOff))
                }
                if (cfg.alphaMode !is AlphaModeBlend) {
                    color = combineXyzWNode(color, constFloat(1f)).output
                }

                colorOutput(unlitMaterialNode(color).outColor)
            }
        }
    }
}

class UnlitMaterialConfig {
    var colorSource = Albedo.VERTEX_ALBEDO
    var isMultiplyColorMap = false

    var isInstanced = false
    var isSkinned = false
    var maxJoints = 64
    val morphAttributes = mutableListOf<Attribute>()

    var cullMethod = CullMethod.CULL_BACK_FACES
    var alphaMode: AlphaMode = AlphaModeOpaque()
    var lineWidth = 1f

    var color = Color.GRAY
    var colorMap: Texture2d? = null
    var colorCubeMap: TextureCube? = null

    fun useStaticColor(color: Color) {
        colorSource = Albedo.STATIC_ALBEDO
        this.color = color
    }

    fun useColorMap(colorMap: String, isMultiplyColorMap: Boolean = false) =
            useColorMap(Texture2d(colorMap), isMultiplyColorMap)

    fun useColorMap(colorMap: Texture2d?, isMultiplyColorMap: Boolean = false) {
        this.colorMap = colorMap
        this.isMultiplyColorMap = isMultiplyColorMap
        colorSource = Albedo.TEXTURE_ALBEDO
    }

    fun useColorCubeMap(colorCubeMap: TextureCube?, isMultiplyColorMap: Boolean = false) {
        this.colorCubeMap = colorCubeMap
        this.isMultiplyColorMap = isMultiplyColorMap
        colorSource = Albedo.CUBE_MAP_ALBEDO
    }

    fun requiresTexCoords(): Boolean {
        return colorSource == Albedo.TEXTURE_ALBEDO
    }
}
