package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.Color

class Ui2Shader : KslShader(Model(), pipelineConfig) {
    var fontTex by texture2d("uFontTex", noFontTex)

    fun setFont(font: AtlasFont) {
        fontTex = font.getOrLoadFontMap(UiScale.measuredScale).texture
    }

    private class Model : KslProgram("UI2 Shader") {
        init {
            val texCoords = interStageFloat2()
            val screenPos = interStageFloat2()
            val color = interStageFloat4()
            val clipBounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

            vertexStage {
                main {
                    texCoords.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                    color.input set vertexAttribFloat4(Attribute.COLORS.name)
                    clipBounds.input set vertexAttribFloat4(ATTRIB_CLIP.name)

                    val vertexPos = float4Var(float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f))
                    screenPos.input set vertexPos.xy
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    val alpha = float1Var(sampleTexture(texture2d("uFontTex"), texCoords.output).r * color.output.a)
                    `if` (any(screenPos.output lt clipBounds.output.xy) or
                            any(screenPos.output gt clipBounds.output.zw)) {
                        discard()
                    }.`else` {
                        colorOutput(color.output.rgb * alpha, alpha)
                    }
                }
            }
        }
    }

    companion object {
        val ATTRIB_CLIP = Attribute("aClip", GpuType.Float4)

        val UI_MESH_ATTRIBS = listOf(Attribute.POSITIONS, Attribute.COLORS, ATTRIB_CLIP, Attribute.TEXTURE_COORDS)

        private val noFontTex = SingleColorTexture(Color.WHITE)
        private val pipelineConfig = PipelineConfig(
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA,
            cullMethod = CullMethod.NO_CULLING,
            depthTest = DepthCompareOp.ALWAYS
        )

        init {
            KoolSystem.getContextOrNull()?.onShutdown += { noFontTex.release() }
        }
    }
}