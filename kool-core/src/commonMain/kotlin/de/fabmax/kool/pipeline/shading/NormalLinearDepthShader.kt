package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

class NormalLinearDepthShader(val cfg: DepthShaderConfig, model: ShaderModel = defaultDepthShaderModel(cfg)) : ModeledShader(model) {

    private var alphaMaskSampler: TextureSampler2d? = null
    var alphaMask: Texture2d? = cfg.alphaMask
        set(value) {
            field = value
            alphaMaskSampler?.texture = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.blendMode = BlendMode.DISABLED
        builder.cullMethod = cfg.cullMethod
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        alphaMaskSampler = model.findNode<Texture2dNode>("tAlphaMask")?.sampler
        alphaMaskSampler?.let { it.texture = alphaMask }
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        fun defaultDepthShaderModel(cfg: DepthShaderConfig) = ShaderModel().apply {
            val ifNormals: StageInterfaceNode
            var ifTexCoords: StageInterfaceNode? = null

            vertexStage {
                val mvpNode = mvpNode()
                var modelViewMat = multiplyNode(mvpNode.outViewMat, mvpNode.outModelMat).output
                var mvpMat = mvpNode.outMvpMat

                if (cfg.isInstanced) {
                    mvpMat = multiplyNode(mvpMat, instanceAttrModelMat().output).output
                    modelViewMat = multiplyNode(modelViewMat, instanceAttrModelMat().output).output
                }
                if (cfg.isSkinned) {
                    val skinNd = skinTransformNode(attrJoints().output, attrWeights().output)
                    mvpMat = multiplyNode(mvpMat, skinNd.outJointMat).output
                    modelViewMat = multiplyNode(modelViewMat, skinNd.outJointMat).output
                }
                if (cfg.alphaMode is AlphaModeMask) {
                    ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                }

                var localPos = attrPositions().output
                var localNrm = attrNormals().output
                if (cfg.nMorphWeights > 0 && cfg.morphAttributes.any {
                        it.name.startsWith(Attribute.POSITIONS.name) || it.name.startsWith(Attribute.NORMALS.name)
                }) {
                    val morphWeights = morphWeightsNode(cfg.nMorphWeights)
                    cfg.morphAttributes.filter { it.name.startsWith(Attribute.POSITIONS.name) }.forEach { attrib ->
                        val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(attrib), morphWeights)
                        val posDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                        localPos = addNode(localPos, posDisplacement.output).output
                    }
                    cfg.morphAttributes.filter { it.name.startsWith(Attribute.NORMALS.name) }.forEach { attrib ->
                        val weight = getMorphWeightNode(cfg.morphAttributes.indexOf(attrib), morphWeights)
                        val nrmDisplacement = multiplyNode(attributeNode(attrib).output, weight.outWeight)
                        localNrm = addNode(localNrm, nrmDisplacement.output).output
                    }
                }
                val nrm = vec3TransformNode(localNrm, modelViewMat, 0f)
                ifNormals = stageInterfaceNode("ifNormals", nrm.outVec3)

                positionOutput = vec4TransformNode(localPos, mvpMat).outVec4
            }
            fragmentStage {
                val alphaMode = cfg.alphaMode
                if (alphaMode is AlphaModeMask) {
                    val color = texture2dSamplerNode(texture2dNode("tAlphaMask"), ifTexCoords!!.output).outColor
                    discardAlpha(splitNode(color, "a").output, constFloat(alphaMode.cutOff))
                }
                val normal = flipBacksideNormalNode(ifNormals.output).outNormal
                val linDepth = addNode(NormalLinearDepthNode(normal, stage))
                colorOutput(linDepth.outColor)
            }
        }
    }

    private class NormalLinearDepthNode(val inNormals: ShaderNodeIoVar, graph: ShaderGraph) : ShaderNode("normalLinearDepth", graph) {
        val outColor = ShaderNodeIoVar(ModelVar4f("normaLinearDepth"), this)

        override fun generateCode(generator: CodeGenerator) {
            generator.appendMain("""
                float d = gl_FragCoord.z / gl_FragCoord.w;
                ${outColor.declare()} = vec4(normalize(${inNormals.ref3f()}), -d);
            """)
        }
    }
}