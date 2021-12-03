package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

class BlurShader(cfg: BlurShaderConfig) : ModeledShader(defaultBlurModel(cfg)) {
    val blurInput = Texture2dInput("tBlurInput")
    val direction = Vec2fInput("uBlurDirection", Vec2f(0.001f, 0f))
    val strength = FloatInput("uBloomStrength", 1f)

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        blurInput.connect(model)
        direction.connect(model)
        strength.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    fun setXDirectionByTexWidth(width: Int, scale: Float = 1f) {
        direction.value = Vec2f(1f / width * scale, 0f)
    }

    fun setYDirectionByTexHeight(height: Int, scale: Float = 1f) {
        direction.value = Vec2f(0f, 1f / height * scale)
    }

    companion object {
        fun defaultBlurModel(cfg: BlurShaderConfig) = ShaderModel("defaultBlurModel").apply {
            val ifTexCoords: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                positionOutput = fullScreenQuadPositionNode(attrTexCoords().output).outQuadPos
            }
            fragmentStage {
                val inTex = texture2dNode("tBlurInput")
                val blurNd = blurNode(inTex, ifTexCoords.output).apply {
                    kernel = BlurNode.blurKernel(cfg.kernelRadius)
                    inDirection = pushConstantNode2f("uBlurDirection").output
                }

                val strength = pushConstantNode1f("uBloomStrength").output
                if (cfg.convertOutputHdrToLdr) {
                    colorOutput(multiplyNode(hdrToLdrNode(blurNd.outColor).outColor, strength).output)
                } else {
                    colorOutput(multiplyNode(blurNd.outColor, strength).output)
                }
            }
        }
    }
}

class BlurShaderConfig {
    var kernelRadius = 8
    var convertOutputHdrToLdr = false
}