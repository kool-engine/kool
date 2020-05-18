package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Color

class UiShader(font: Texture? = SingleColorTexture(Color.WHITE)) : ModeledShader(uiShaderModel()) {

    private var uAlpha: PushConstantNode1f? = null
    private var uFontSampler: TextureSampler? = null

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

    override fun createPipeline(mesh: Mesh, builder: Pipeline.Builder, ctx: KoolContext): Pipeline {
        builder.cullMethod = CullMethod.NO_CULLING
        builder.depthTest = DepthCompareOp.LESS_EQUAL
        return super.createPipeline(mesh, builder, ctx)
    }

    override fun onPipelineCreated(pipeline: Pipeline) {
        super.onPipelineCreated(pipeline)

        uAlpha = model.findNode(U_ALPHA)
        uAlpha?.uniform?.value = alpha

        uFontSampler = model.findNode<TextureNode>(U_FONT_TEX)?.sampler
        uFontSampler?.texture = font
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
                positionOutput = simpleVertexPositionNode().outPosition
            }
            fragmentStage {
                val alpha = pushConstantNode1f(U_ALPHA)
                val fontSampler = textureSamplerNode(textureNode(U_FONT_TEX), ifTexCoords.output)
                val mulAlpha = multiplyNode(fontSampler.outColor, alpha.output)
                val alphaColor = colorAlphaNode(ifColors.output, mulAlpha.output)
                colorOutput(unlitMaterialNode(alphaColor.outAlphaColor).outColor)
            }
        }
    }
}