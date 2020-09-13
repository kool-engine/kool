package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

class UiShader(font: Texture2d? = SingleColorTexture(Color.WHITE)) : ModeledShader(uiShaderModel()) {

    private var uAlpha: PushConstantNode1f? = null
    private var uFontSampler: TextureSampler2d? = null

    var alpha = 1f
        set(value) {
            field = value
            uAlpha?.uniform?.value = value
        }

    var font = font
        set(value) {
            field = value
            uFontSampler?.texture = value
        }

    override fun onPipelineSetup(builder: Pipeline.Builder, mesh: Mesh, ctx: KoolContext) {
        builder.blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
        builder.cullMethod = CullMethod.NO_CULLING
        builder.depthTest = DepthCompareOp.LESS_EQUAL
        super.onPipelineSetup(builder, mesh, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline, mesh: Mesh, ctx: KoolContext) {
        uAlpha = model.findNode(U_ALPHA)
        uAlpha?.uniform?.value = alpha

        uFontSampler = model.findNode<Texture2dNode>(U_FONT_TEX)?.sampler
        uFontSampler?.texture = font
        super.onPipelineCreated(pipeline, mesh, ctx)
    }

    companion object {
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