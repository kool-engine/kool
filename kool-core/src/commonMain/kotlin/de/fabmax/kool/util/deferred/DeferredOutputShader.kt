package de.fabmax.kool.util.deferred

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh

class DeferredOutputShader(private val pbrOutput: Texture2d, private val depth: Texture2d) : ModeledShader(outputModel()) {
    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        val textureSampler = model.findNode<Texture2dNode>("deferredPbrOutput")?.sampler
        textureSampler?.texture = pbrOutput
        val depthSampler = model.findNode<Texture2dNode>("deferredDepthOutput")?.sampler
        depthSampler?.texture = depth
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        private fun outputModel() = ShaderModel("DeferredOutputShader").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val sampler = texture2dSamplerNode(texture2dNode("deferredPbrOutput"), ifTexCoords.output)
                colorOutput(hdrToLdrNode(sampler.outColor).outColor)
                val depthSampler = texture2dSamplerNode(texture2dNode("deferredDepthOutput"), ifTexCoords.output)
                depthOutput(depthSampler.outColor)
            }
        }
    }
}