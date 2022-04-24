package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.blocks.mvpMatrix
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color

class UiShader : KslShader(Model(), pipelineConfig) {
    var alpha by uniform1f("uAlpha", 1f)
    var fontTex by texture2d("uFontTex", noFontTex)

    fun setFont(font: Font, ctx: KoolContext) {
        fontTex = font.getOrInitCharMap(ctx).texture
    }

    private class Model : KslProgram("UI Shader") {
        init {
            val texCoords = interStageFloat2()
            val color = interStageFloat4()

            vertexStage {
                main {
                    texCoords.input set vertexAttribFloat2(Attribute.TEXTURE_COORDS.name)
                    color.input set vertexAttribFloat4(Attribute.COLORS.name)
                    outPosition set mvpMatrix().matrix * constFloat4(vertexAttribFloat3(Attribute.POSITIONS.name), 1f)
                }
            }
            fragmentStage {
                main {
                    val alpha = sampleTexture(texture2d("uFontTex"), texCoords.output).r * uniformFloat1("uAlpha") * color.output.a
                    colorOutput(color.output.rgb * alpha, alpha)
                }
            }
        }
    }

    companion object {
        val UI_MESH_ATTRIBS = listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, Attribute.COLORS)

        private val noFontTex = SingleColorTexture(Color.WHITE)
        private val pipelineConfig = PipelineConfig().apply {
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
            cullMethod = CullMethod.NO_CULLING
            depthTest = DepthCompareOp.LESS_EQUAL
        }
    }
}