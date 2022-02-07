package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.ShaderModel
import de.fabmax.kool.pipeline.shadermodel.StageInterfaceNode
import de.fabmax.kool.pipeline.shadermodel.fragmentStage
import de.fabmax.kool.pipeline.shadermodel.vertexStage
import de.fabmax.kool.pipeline.shading.FloatInput
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.pipeline.shading.Texture2dInput
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

class UiShader : ModeledShader(uiShaderModel()) {
    val alpha = FloatInput(U_ALPHA, 1f)
    val fontTex = Texture2dInput(U_FONT_TEX, nullFontTex)

    fun setFont(font: Font, ctx: KoolContext) {
        fontTex.texture = font.getOrInitCharMap(ctx).texture
    }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
        builder.cullMethod = CullMethod.NO_CULLING
        builder.depthTest = DepthCompareOp.LESS_EQUAL
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        alpha.connect(model)
        fontTex.connect(model)
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
        private val nullFontTex = SingleColorTexture(Color.WHITE)

        val UI_MESH_ATTRIBS = listOf(Attribute.POSITIONS, Attribute.TEXTURE_COORDS, Attribute.COLORS)

        private const val U_ALPHA = "uAlpha"
        private const val U_FONT_TEX = "uFontTex"

        private fun uiShaderModel() = ShaderModel("UI Shader").apply {
            val ifTexCoords: StageInterfaceNode
            val ifColors: StageInterfaceNode

            vertexStage {
                ifTexCoords = stageInterfaceNode("ifTexCoords", attrTexCoords().output)
                ifColors = stageInterfaceNode("ifColors", attrColors().output)
                positionOutput = simpleVertexPositionNode().outVec4
            }
            fragmentStage {
                val alpha = pushConstantNode1f(U_ALPHA)
                val fontSampler = texture2dSamplerNode(texture2dNode(U_FONT_TEX), ifTexCoords.output)
                val mulAlpha = multiplyNode(fontSampler.outColor, alpha.output)
                val alphaColor = colorAlphaNode(ifColors.output, mulAlpha.output)
                colorOutput(unlitMaterialNode(alphaColor.outAlphaColor).outColor)
            }
        }
    }
}