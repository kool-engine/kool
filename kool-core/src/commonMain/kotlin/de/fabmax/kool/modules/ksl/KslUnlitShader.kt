package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d

open class KslUnlitShader(cfg: Config, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    constructor(block: Config.() -> Unit) : this(Config().apply(block))

    var color: Vec4f by uniform4f(cfg.colorCfg.primaryUniform?.uniformName, cfg.colorCfg.primaryUniform?.defaultColor)
    var colorMap: Texture2d? by texture2d(cfg.colorCfg.primaryTexture?.textureName, cfg.colorCfg.primaryTexture?.defaultTexture)

    class Config {
        val colorCfg = ColorBlockConfig("baseColor")
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
            vertexStage {
                main {
                    val mvp = mat4Var(mvpMatrix().matrix)
                    if (cfg.isInstanced) {
                        mvp *= instanceAttribMat4(Attribute.INSTANCE_MODEL_MAT.name)
                    }
                    outPosition set mvp * float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                }
            }
            fragmentStage {
                main {
                    val colorBlock = fragmentColorBlock(cfg.colorCfg)
                    val baseColor = float4Port("baseColor", colorBlock.outColor)
                    val outRgb = float3Var(baseColor.rgb)
                    outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                    if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                        outRgb set outRgb * baseColor.a
                    }
                    colorOutput(outRgb, baseColor.a)
                }
            }
            cfg.modelCustomizer?.invoke(this)
        }
    }
}