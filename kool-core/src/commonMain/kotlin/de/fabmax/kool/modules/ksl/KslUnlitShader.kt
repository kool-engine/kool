package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.KslProgram
import de.fabmax.kool.modules.ksl.lang.a
import de.fabmax.kool.modules.ksl.lang.rgb
import de.fabmax.kool.modules.ksl.lang.times
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d

fun unlitShader(cfgBlock: KslUnlitShader.Config.() -> Unit): KslUnlitShader {
    val cfg = KslUnlitShader.Config().apply(cfgBlock)
    return KslUnlitShader(cfg)
}

class KslUnlitShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {
    var uniformColor: Vec4f by uniform4f(cfg.colorCfg.primaryUniformColor?.uniformName, cfg.colorCfg.primaryUniformColor?.defaultColor)
    var colorTexture: Texture2d? by texture2d(cfg.colorCfg.primaryTextureColor?.textureName, cfg.colorCfg.primaryTextureColor?.defaultTexture)

    class Config {
        val colorCfg = ColorBlockConfig()
        val pipelineCfg = PipelineConfig()

        var isInstanced = false
        var colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.apply(block)
        }

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.apply(block)
        }
    }

    class Model(cfg: Config) : KslProgram("Unlit Shader") {
        init {
            val uMvp = mvpMatrix()
            vertexStage {
                main {
                    val mvp = mat4Var(uMvp.matrix)
                    if (cfg.isInstanced) {
                        mvp *= instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                    }

                    outPosition set mvp * constFloat4(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                }
            }
            fragmentStage {
                main {
                    val fragmentColor = fragmentColorBlock(cfg.colorCfg).outColor
                    val outRgb = float3Var(fragmentColor.rgb)
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * fragmentColor.a
                    }
                    colorOutput(outRgb, fragmentColor.a)
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }
}