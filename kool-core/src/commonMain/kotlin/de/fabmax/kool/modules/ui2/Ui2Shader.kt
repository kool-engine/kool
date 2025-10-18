package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.VertexLayouts
import de.fabmax.kool.scene.vertexAttrib
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MemoryLayout
import de.fabmax.kool.util.Struct

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
                    texCoords.input set vertexAttrib(UiVertexLayout.texCoord)
                    color.input set vertexAttrib(UiVertexLayout.color)
                    clipBounds.input set vertexAttrib(UiVertexLayout.clip)

                    val vertexPos by float4Value(vertexAttrib(UiVertexLayout.position), 1f)
                    screenPos.input set vertexPos.xy
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    val alpha by sampleTexture(texture2d("uFontTex"), texCoords.output).r * color.output.a
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

object UiVertexLayout : Struct("UiVertex", MemoryLayout.TightlyPacked) {
    val position = include(VertexLayouts.Position.position)
    val color = include(VertexLayouts.Color.color)
    val clip = float4("attr_clip")
    val texCoord = include(VertexLayouts.TexCoord.texCoord)
}