package de.fabmax.kool.modules.ksl

import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.modules.ksl.blocks.*
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.Texture2d

open class KslUnlitShader(cfg: UnlitShaderConfig, model: KslProgram = Model(cfg)) : KslShader(model, cfg.pipelineCfg) {

    constructor(block: UnlitShaderConfig.() -> Unit) : this(UnlitShaderConfig().apply(block))

    var color: Vec4f by uniform4f(cfg.colorCfg.primaryUniform?.uniformName, cfg.colorCfg.primaryUniform?.defaultColor)
    var colorMap: Texture2d? by texture2d(cfg.colorCfg.primaryTexture?.textureName, cfg.colorCfg.primaryTexture?.defaultTexture)

    open class UnlitShaderConfig {
        val vertexCfg = BasicVertexConfig()
        val colorCfg = ColorBlockConfig("baseColor")
        val pipelineCfg = PipelineConfig()

        var colorSpaceConversion = ColorSpaceConversion.LINEAR_TO_sRGB

        var modelCustomizer: (KslProgram.() -> Unit)? = null

        fun vertices(block: BasicVertexConfig.() -> Unit) {
            vertexCfg.block()
        }

        fun color(block: ColorBlockConfig.() -> Unit) {
            colorCfg.apply(block)
        }

        fun pipeline(block: PipelineConfig.() -> Unit) {
            pipelineCfg.apply(block)
        }
    }

    class Model(cfg: UnlitShaderConfig) : KslProgram("Unlit Shader") {
        init {
            vertexStage {
                main {
                    val viewProj = mat4Var(cameraData().viewProjMat)
                    val vertexBlock = vertexTransformBlock(cfg.vertexCfg) {
                        inModelMat(modelMatrix().matrix)
                        inLocalPos(vertexAttribFloat3(Attribute.POSITIONS.name))
                    }
                    outPosition set viewProj * float4Value(vertexBlock.outWorldPos, 1f)
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