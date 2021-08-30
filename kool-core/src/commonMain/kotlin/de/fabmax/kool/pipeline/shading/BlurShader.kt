package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.scene.Mesh

class BlurShader(cfg: BlurShaderConfig) : ModeledShader(defaultBlurModel(cfg)) {
    val isVertical = IntInput("uIsVertical")
    val blurInput = Texture2dInput("tBlurInput")
    val minBrightness = Vec2fInput("uMinBrightness", Vec2f(1f, 2f))
    val radiusFac = Vec2fInput("uRadiusFac", Vec2f(1f / 800f, 1f / 450f))

    var minBrightnessLower: Float
        get() = minBrightness.value.x
        set(value) {
            minBrightness.value = Vec2f(value, minBrightness.value.y)
        }
    var minBrightnessUpper: Float
        get() = minBrightness.value.y
        set(value) {
            minBrightness.value = Vec2f(minBrightness.value.x, value)
        }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        isVertical.connect(model)
        blurInput.connect(model)
        minBrightness.connect(model)
        radiusFac.connect(model)
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
                        minBrightness = pushConstantNode2f("uMinBrightness").output
                    }
                }

                if (cfg.convertOutputHdrToLdr) {
                    colorOutput(hdrToLdrNode(blurNd.outColor).outColor)
                } else {
                    colorOutput(blurNd.outColor)
                }
            }
        }
    }
}

class BlurShaderConfig {
    var kernelRadius = 4
    var isWithMinBrightness = false
    var convertOutputHdrToLdr = false
}