package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh

class DeferredOutputShader(private val pbrOutput: Texture2d, private val depth: Texture2d, bloom: Texture2d?, private val depthMode: DepthCompareOp) :
    ModeledShader(outputModel(bloom != null)) {

    private var uBloomStrength: Uniform1f? = null
    var bloomStrength = 0.5f
        set(value) {
            field = value
            uBloomStrength?.value = value
        }

    private var bloomSampler: TextureSampler2d? = null
    var bloomMap: Texture2d? = bloom
        set(value) {
            field = value
            bloomSampler?.texture = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.depthTest = depthMode
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        val textureSampler = model.findNode<Texture2dNode>("deferredPbrOutput")?.sampler
        textureSampler?.texture = pbrOutput
        val depthSampler = model.findNode<Texture2dNode>("deferredDepthOutput")?.sampler
        depthSampler?.texture = depth
        bloomSampler = model.findNode<Texture2dNode>("bloom")?.sampler
        bloomSampler?.texture = bloomMap
        uBloomStrength = model.findNode<PushConstantNode1f>("uBloomStrength")?.uniform
        uBloomStrength?.value = bloomStrength
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        private fun outputModel(isWithBloom: Boolean) = ShaderModel("DeferredOutputShader").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val linearColor = texture2dSamplerNode(texture2dNode("deferredPbrOutput"), ifTexCoords.output).outColor
                if (isWithBloom) {
                    val bloom = texture2dSamplerNode(texture2dNode("bloom"), ifTexCoords.output).outColor
                    val bloomStrength = pushConstantNode1f("uBloomStrength").output
                    val bloomScaled = multiplyNode(bloom, bloomStrength).output
                    val composed = addNode(linearColor, bloomScaled).output
                    colorOutput(hdrToLdrNode(composed).outColor)
                } else {
                    colorOutput(hdrToLdrNode(linearColor).outColor)
                }
                val depthSampler = texture2dSamplerNode(texture2dNode("deferredDepthOutput"), ifTexCoords.output)
                depthOutput(depthSampler.outColor)
            }
        }
    }
}