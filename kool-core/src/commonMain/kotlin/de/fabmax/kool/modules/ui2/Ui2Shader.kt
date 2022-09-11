package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.util.Color

class Ui2Shader : KslShader(Model(), pipelineConfig) {
    var fontTex by texture2d("uFontTex", noFontTex)

    fun setFont(font: Font, ctx: KoolContext) {
        fontTex = font.getOrInitCharMap(ctx).texture
    }

    private class Model : KslProgram("UI2 Shader") {
        init {
            val texCoords = interStageFloat2()
            val screenPos = interStageFloat2()
            val color = interStageFloat4()
            val bounds = interStageFloat4(interpolation = KslInterStageInterpolation.Flat)

            vertexStage {
                main {
                    texCoords.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                    color.input set vertexAttribFloat4(Attribute.COLORS.name)
                    bounds.input set vertexAttribFloat4(ATTRIB_BOUNDS.name)

                    val vertexPos = float4Var(float4Value(vertexAttribFloat3(Attribute.POSITIONS.name), 1f))
                    screenPos.input set vertexPos.xy
                    outPosition set mvpMatrix().matrix * vertexPos
                }
            }
            fragmentStage {
                main {
                    `if` (all(screenPos.output gt bounds.output.xy) and
                            all(screenPos.output lt bounds.output.zw)) {
                        val alpha = sampleTexture(texture2d("uFontTex"), texCoords.output).r * color.output.a
                        colorOutput(color.output.rgb * alpha, alpha)
                    }.`else` {
                        discard()
                    }
                }
            }
        }
    }

    companion object {
        val ATTRIB_BOUNDS = Attribute("aBounds", GlslType.VEC_4F)

        val UI_MESH_ATTRIBS = listOf(Attribute.POSITIONS, Attribute.COLORS, ATTRIB_BOUNDS, Attribute.TEXTURE_COORDS)

        private val noFontTex = SingleColorTexture(Color.WHITE)
        private val pipelineConfig = PipelineConfig().apply {
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
            cullMethod = CullMethod.NO_CULLING
            depthTest = DepthCompareOp.DISABLED
        }
    }
}