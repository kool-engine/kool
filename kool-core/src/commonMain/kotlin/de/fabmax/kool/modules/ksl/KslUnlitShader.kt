package de.fabmax.kool.modules.ksl

import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.PipelineConfig
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.AlphaMode
import de.fabmax.kool.util.Color

open class KslUnlitShader(cfg: UnlitShaderConfig) : KslShader("Unlit Shader") {

    constructor(block: UnlitShaderConfig.Builder.() -> Unit) : this(UnlitShaderConfig.Builder().apply(block).build())

    var color: Color by colorUniform(cfg.colorCfg)
    var colorMap: Texture2d? by colorTexture(cfg.colorCfg)

    val colorCfg = cfg.colorCfg

    init {
        pipelineConfig = cfg.pipelineCfg
        program.unlitProgram(cfg)
        cfg.modelCustomizer?.invoke(program)
    }

    private fun KslProgram.unlitProgram(cfg: UnlitShaderConfig) {
        vertexStage {
            main {
                val viewProj = mat4Var(cameraData().viewProjMat)
                val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                    inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                }

                val worldPos = float3Port("worldPos", vertexBlock.outWorldPos)
                outPosition set viewProj * float4Value(worldPos, 1f)
            }
        }
        fragmentStage {
            main {
                val colorBlock = fragmentColorBlock(cfg.colorCfg)
                val baseColorPort = float4Port("baseColor", colorBlock.outColor)

                val baseColor = float4Var(baseColorPort)
                when (val alphaMode = cfg.alphaMode) {
                    is AlphaMode.Blend -> { }
                    is AlphaMode.Opaque -> baseColor.a set 1f.const
                    is AlphaMode.Mask -> {
                        `if`(baseColorPort.a lt alphaMode.cutOff.const) {
                            discard()
                        }
                    }
                }

                val outRgb = float3Var(baseColor.rgb)
                if (cfg.pipelineCfg.blendMode == BlendMode.BLEND_PREMULTIPLIED_ALPHA) {
                    outRgb set outRgb * baseColor.a
                }
                outRgb set convertColorSpace(outRgb, cfg.colorSpaceConversion)
                colorOutput(outRgb, baseColor.a)
            }
        }
    }

    open class UnlitShaderConfig(builder: Builder) {
        val vertexCfg: BasicVertexConfig = builder.vertexCfg.build()
        val colorCfg: ColorBlockConfig = builder.colorCfg.build()
        val pipelineCfg: PipelineConfig = builder.pipelineCfg.build()

        val colorSpaceConversion = builder.colorSpaceConversion
        val alphaMode: AlphaMode = builder.alphaMode

        val modelCustomizer: (KslProgram.() -> Unit)? = builder.modelCustomizer

        open class Builder {
            val vertexCfg = BasicVertexConfig.Builder()
            val colorCfg = ColorBlockConfig.Builder("baseColor").constColor(Color.GRAY)
            val pipelineCfg = PipelineConfig.Builder()

            var colorSpaceConversion: ColorSpaceConversion = ColorSpaceConversion.AsIs
            var alphaMode: AlphaMode = AlphaMode.Blend

            var modelCustomizer: (KslProgram.() -> Unit)? = null

            fun vertices(block: BasicVertexConfig.Builder.() -> Unit) {
                vertexCfg.block()
            }

            fun color(block: ColorBlockConfig.Builder.() -> Unit) {
                colorCfg.colorSources.clear()
                colorCfg.apply(block)
            }

            fun pipeline(block: PipelineConfig.Builder.() -> Unit) {
                pipelineCfg.apply(block)
            }

            open fun build() = UnlitShaderConfig(this)
        }
    }
}

fun UnlitShaderConfig(block: KslUnlitShader.UnlitShaderConfig.Builder.() -> Unit): KslUnlitShader.UnlitShaderConfig {
    val builder = KslUnlitShader.UnlitShaderConfig.Builder()
    builder.block()
    return builder.build()
}