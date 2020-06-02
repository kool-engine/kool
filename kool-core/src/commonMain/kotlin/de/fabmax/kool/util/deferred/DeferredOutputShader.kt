package de.fabmax.kool.util.deferred

import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader

class DeferredOutputShader(private val pbrOutput: Texture) : ModeledShader(outputModel()) {

    override fun onPipelineCreated(pipeline: Pipeline) {
        val textureSampler = model.findNode<TextureNode>("deferredPbrOutput")?.sampler
        textureSampler!!.texture = pbrOutput
        super.onPipelineCreated(pipeline)
    }

    companion object {
        private fun outputModel() = ShaderModel("DeferredOutputShader").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val sampler = textureSamplerNode(textureNode("deferredPbrOutput"), ifTexCoords.output)
                colorOutput(hdrToLdrNode(sampler.outColor).outColor)
            }
        }
    }
}