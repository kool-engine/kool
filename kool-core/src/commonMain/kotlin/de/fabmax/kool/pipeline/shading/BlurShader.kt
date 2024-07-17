package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.ColorSpaceConversion
import de.fabmax.kool.modules.ksl.blocks.convertColorSpace
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.FullscreenShaderUtil
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import kotlin.math.exp

class BlurShader(cfg: BlurShaderConfig, model: Model = Model(cfg)) :
    KslShader(model, FullscreenShaderUtil.fullscreenShaderPipelineCfg)
{

    var blurInput by texture2d("tBlurInput")
    var direction by uniform2f("uBlurDirection", Vec2f(0.001f, 0f))
    var strength by uniform1f("uOutputStrength", 1f)

    fun setXDirectionByTexWidth(width: Int, scale: Float = 1f) {
        direction = Vec2f(1f / width * scale, 0f)
    }

    fun setYDirectionByTexHeight(height: Int, scale: Float = 1f) {
        direction = Vec2f(0f, 1f / height * scale)
    }

    class Model(cfg: BlurShaderConfig) : KslProgram("Blur Shader") {
        init {
            val texCoord = interStageFloat2("uv")
            fullscreenQuadVertexStage(texCoord)

            fragmentStage {
                main {
                    val input = texture2d("tBlurInput")
                    val dir = uniformFloat2("uBlurDirection")
                    val str = uniformFloat1("uOutputStrength")
                    val uv = texCoord.output

                    val output = float4Var(sampleTexture(input, uv) * cfg.kernel[0].const)
                    for (i in 1 .. cfg.kernel.lastIndex) {
                        val lt = float2Var(uv - dir * i.toFloat().const)
                        val rt = float2Var(uv + dir * i.toFloat().const)
                        output += sampleTexture(input, lt) * cfg.kernel[i].const
                        output += sampleTexture(input, rt) * cfg.kernel[i].const
                    }

                    colorOutput(convertColorSpace(output.rgb, cfg.colorSpaceConversion) * str)
                }
            }
        }
    }

    companion object {
        fun blurKernel(radius: Int, sigma: Float = radius / 2.5f): FloatArray {
            val size = radius + 1
            val values = FloatArray(size)
            var sum = 0f
            for (i in 0 until size) {
                val a = i / sigma
                values[i] = exp(-0.5f * a * a)
                sum += values[i] * if (i == 0) 1f else 2f
            }
            for (i in 0 until size) {
                values[i] /= sum
            }
            return values
        }
    }
}

class BlurShaderConfig {
    var kernel = BlurShader.blurKernel(8)
    var colorSpaceConversion = ColorSpaceConversion.AsIs

    var kernelRadius: Int
        get() = kernel.size - 1
        set(value) {
            kernel = BlurShader.blurKernel(value)
        }
}