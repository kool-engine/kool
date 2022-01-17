package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

class DepthShader(val cfg: DepthShaderConfig, model: ShaderModel = defaultDepthShaderModel(cfg)) : ModeledShader(model) {

    val alphaMask = Texture2dInput("tAlphaMask", cfg.alphaMask)

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.blendMode = BlendMode.DISABLED
        builder.cullMethod = cfg.cullMethod
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        alphaMask.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultDepthShaderModel(cfg: DepthShaderConfig) = ShaderModel("depth-shader").apply {
            var ifTexCoords: StageInterfaceNode? = null

            vertexStage {
                var mvpMat = premultipliedMvpNode().outMvpMat
                if (cfg.isInstanced) {
                    mvpMat = multiplyNode(mvpMat, instanceAttrModelMat().output).output
                }
                if (cfg.isSkinned) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output, cfg.maxJoints)
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                }
                if (cfg.alphaMode is AlphaModeMask) {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                }
                var localPos = attrPositions().output

                val posMorphAttribs = cfg.morphAttributes.filter { it.name.startsWith(Attribute.POSITIONS.name) }
                if (cfg.nMorphWeights > 0 && posMorphAttribs.isNotEmpty()) {
                    val morphWeights = morphWeightsNode(cfg.nMorphWeights)
                    posMorphAttribs.forEach { attrib ->
                        val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(attrib), morphWeights)
                        val posDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                        localPos = addNode(localPos, posDisplacement.output).output
                    }
                }
                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage {
                val alphaMode = cfg.alphaMode
                if (alphaMode is AlphaModeMask) {
                    val color = texture2dSamplerNode(texture2dNode("tAlphaMask"), ifTexCoords!!.output).outColor
                    discardAlpha(splitNode(color, "a").output, constFloat(alphaMode.cutOff))
                }
                colorOutput(constVec4f(Color.RED))
            }
        }
    }
}

class DepthShaderConfig {
    var cullMethod = CullMethod.CULL_BACK_FACES
    var isInstanced = false
    var isSkinned = false
    var maxJoints = 16

    var alphaMode: AlphaMode = AlphaModeOpaque()
    var alphaMask: Texture2d? = null

    var nMorphWeights = 0
    val morphAttributes = mutableListOf<Attribute>()

    fun useAlphaMask(alphaMask: Texture2d, alphaCutOff: Float) {
        this.alphaMask = alphaMask
        alphaMode = AlphaModeMask(alphaCutOff)
    }
}