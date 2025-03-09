package de.fabmax.kool.modules.ui2

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*

class MsdfUiShader(
    model: Model = Model(),
    pipelineCfg: PipelineConfig = PipelineConfig(
        cullMethod = CullMethod.NO_CULLING,
        blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
    )
) : KslShader(model, pipelineCfg) {
    var fontMap by texture2d("tFontMap")
    var pxRangeScale by uniform1f("uPxRange", 1f)

    class Model : KslProgram("Msdf UI2 Shader") {
        init {
            val fgColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val glowColor = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val msdfProps = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)
            val screenPos = interStageFloat2()
            val uv = interStageFloat2()

            vertexStage {
                main {
                    fgColor.input set vertexAttribFloat4(Attribute.COLORS.name)
                    glowColor.input set vertexAttribFloat4(ATTRIB_GLOW_COLOR.name)
                    msdfProps.input set vertexAttribFloat4(ATTRIB_MSDF_PROPS.name)
                    clipBounds.input set vertexAttribFloat4(Ui2Shader.ATTRIB_CLIP.name)
                    uv.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)

                    val mvp = mat4Var(mvpMatrix().matrix)
                    val vertexPos = float4Var(float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f))
                    screenPos.input set vertexPos.xy
                    outPosition set mvp * vertexPos
                }
            }

            fragmentStage {
                val median3 = functionFloat1("median") {
                    val p = paramFloat3("p")
                    body {
                        max(min(p.x, p.y), min(max(p.x, p.y), p.z))
                    }
                }

                val computeOpacity = functionFloat1("computeOpacity") {
                    val msdf = paramFloat3("msdf")
                    val props = paramFloat4("props")

                    body {
                        val sd = float1Var(median3(msdf))
                        val dist = float1Var(sd - 0.5f.const + props.y)

                        // branch-less version of "if (dist > cutoff) dist = 2.0 * cutoff - dist"
                        val p = step(props.z, dist)
                        dist set dist + p * 2f.const * (props.z - dist)

                        val screenPxDistance = float1Var(props.x * dist)
                        clamp(screenPxDistance + 0.5f.const, 0f.const, 1f.const)
                    }
                }

                main {
                    val fontMap = texture2d("tFontMap")

                    `if` (any(screenPos.output lt clipBounds.output.xy) or
                            any(screenPos.output gt clipBounds.output.zw)) {
                        discard()

                    }.`else` {
                        val msdfVals = float4Var(sampleTexture(fontMap, uv.output, 0f.const))
                        val color = float4Var(fgColor.output)
                        val pxRange = float1Var(msdfProps.output.x * uniformFloat1("uPxRange"))
                        val weight = msdfProps.output.y

                        // sample regular sdf map (stored in texture alpha channel)
                        val dist = float1Var(msdfVals.a - 0.5f.const + weight)
                        val screenPxDistance = float1Var(pxRange * dist)
                        val sdfOpa = float1Var(clamp(screenPxDistance + 0.5f.const, 0f.const, 1f.const))

                        // sample multi-sdf map (stored in texture rgb channels)
                        val msdfOpa = float1Var(computeOpacity(msdfVals.rgb, msdfProps.output))

                        // use sdf for small fonts and msdf for large fonts
                        val wMsdf = float1Var(smoothStep(5f.const, 10f.const, pxRange))
                        color.a *= sdfOpa * (1f.const - wMsdf) + msdfOpa * wMsdf

                        val glow = float4Var(glowColor.output)
                        glow.a *= smoothStep(0f.const, 1f.const, msdfVals.a * 1.5f.const) * (1f.const - color.a)

                        colorOutput(color.rgb * color.a + glow.rgb * glow.a, color.a + glow.a)
                    }
                }
            }
        }
    }

    companion object {
        val ATTRIB_MSDF_PROPS = Attribute("aMsdfProps", GpuType.Float4)
        val ATTRIB_GLOW_COLOR = Attribute("aGlowColor", GpuType.Float4)

        val MSDF_UI_MESH_ATTRIBS = listOf(
            ATTRIB_MSDF_PROPS,
            Attribute.COLORS,
            ATTRIB_GLOW_COLOR,
            Ui2Shader.ATTRIB_CLIP,
            Attribute.POSITIONS,
            Attribute.TEXTURE_COORDS
        )
    }
}