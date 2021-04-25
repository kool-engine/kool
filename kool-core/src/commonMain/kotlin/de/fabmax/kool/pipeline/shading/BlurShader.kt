package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

class BlurShader(cfg: BlurShaderConfig) : ModeledShader(defaultBlurModel(cfg)) {
    private var uIsVertical: Uniform1i? = null
    var isVertical = false
        set(value) {
            field = value
            uIsVertical?.value = if (value) 1 else 0
        }

    private var blurSampler: TextureSampler2d? = null
    var blurInput: Texture2d? = null
        set(value) {
            field = value
            blurSampler?.texture = value
        }

    private var uMinBrightness: Uniform1f? = null
    var minBrightness = 0.5f
        set(value) {
            field = value
            uMinBrightness?.value = value
        }

    private var uRadiusFac: Uniform2f? = null
    var radiusFac = Vec2f(1f / 800f, 1f / 450f)
        set(value) {
            field = value
            uRadiusFac?.value?.set(value)
        }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        uIsVertical = model.findNode<PushConstantNode1i>("uIsVertical")?.uniform
        uIsVertical?.value = if (isVertical) 1 else 0
        blurSampler = model.findNode<Texture2dNode>("tBlurInput")?.sampler
        blurSampler?.let { it.texture = blurInput }
        uMinBrightness = model.findNode<PushConstantNode1f>("uMinBrightness")?.uniform
        uMinBrightness?.value = minBrightness
        uRadiusFac = model.findNode<PushConstantNode2f>("uRadiusFac")?.uniform
        uRadiusFac?.value?.set(radiusFac)
        super.onPipelineCreated(pipeline, mesh, ctx)
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
                val inDir = pushConstantNode1i("uIsVertical").output
                val blurNd = blurNode(inTex, ifTexCoords.output, inDir).apply {
                    kernel = BlurNode.blurKernel(cfg.kernelRadius)
                    inRadiusFac = pushConstantNode2f("uRadiusFac").output
                    if (cfg.isWithMinBrightness) {
                        minBrightness = pushConstantNode1f("uMinBrightness").output
                    }
                }

                colorOutput(blurNd.outColor)
            }
        }
    }
}

class BlurShaderConfig {
    var kernelRadius = 4
    var isWithMinBrightness = false
}