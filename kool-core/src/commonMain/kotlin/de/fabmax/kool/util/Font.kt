package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.TextureData
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shadermodel.*
import de.fabmax.kool.pipeline.shading.ModeledShader
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */

fun uiFont(family: String, sizeDp: Float, uiDpi: Float, ctx: KoolContext, style: Int = Font.PLAIN, chars: String = Font.STD_CHARS): Font {
    val pts = (sizeDp * uiDpi / 96f)
    return Font(FontProps(family, pts, style, chars), ctx)
}

private class MaskNode : ShaderNode("Font Mask Node") {
    var inColor = ShaderNodeIoVar(null, ModelVar4fConst(Color.MAGENTA))
    var inMask = ShaderNodeIoVar(null, ModelVar1fConst(1f))
    val outMaskedColor = ShaderNodeIoVar(this, ModelVar4f("maskedColor_outColor"))

    override fun setup(shaderGraph: ShaderGraph) {
        super.setup(shaderGraph)
        dependsOn(inColor, inMask)
    }

    override fun generateCode(generator: CodeGenerator, pipeline: Pipeline, ctx: KoolContext) {
        generator.appendMain("${outMaskedColor.variable.declare()} = vec4(${inColor.variable.ref3f()}, ${inMask.variable.ref1f()});")
    }
}

fun fontShaderLoader(): (Mesh, Pipeline.BuildContext, KoolContext) -> ModeledShader.TextureColor = { mesh, buildCtx, ctx ->
    val texName = "fontTex"
    val model = ShaderModel().apply {
        val ifTexCoords: StageInterfaceNode
        val ifColors: StageInterfaceNode

        vertexStage {
            ifTexCoords = stageInterfaceNode("ifTexCoords", attributeNode(Attribute.TEXTURE_COORDS).output)
            ifColors = stageInterfaceNode("ifColors", attributeNode(Attribute.COLORS).output)
            simpleVertexPositionNode()
        }
        fragmentStage {
            val sampler = textureSamplerNode(textureNode(texName), ifTexCoords.output)
            val maskedColor = addNode(MaskNode().apply { inColor = ifColors.output; inMask = sampler.outColor })
            unlitMaterialNode(maskedColor.outMaskedColor)
        }
    }
    model.setup(mesh, buildCtx, ctx)
    ModeledShader.TextureColor(model, texName)
}

fun fontShader(font: Font? = null, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    TODO()
//    val props = ShaderProps()
//    props.propsInit()
//    // vertex color and texture color are required to render fonts
//    props.isVertexColor = true
//    props.isTextureColor = true
//    props.isDiscardTranslucent = true
//    val generator = GlslGenerator()
//
//    // inject shader code to take color from static color and alpha from texture
//    // static color rgb has to be pre-multiplied with texture alpha
//    generator.injectors += object: GlslGenerator.GlslInjector {
//        override fun fsAfterSampling(shaderProps: ShaderProps, node: Node, text: StringBuilder, ctx: KoolContext) {
//            text.append("${ctx.glCapabilities.glslDialect.fragColorBody} = ${GlslGenerator.L_FS_VERTEX_COLOR} * ${GlslGenerator.L_FS_TEX_COLOR}.a;\n")
//        }
//    }
//
//    val shader = BasicShader(props, generator)
//    shader.texture = font
//    return shader
}

data class FontProps(
        val family: String,
        val sizePts: Float,
        val style: Int = Font.PLAIN,
        val chars: String = Font.STD_CHARS)

class Font(val charMap: CharMap) : Texture(loader = { charMap.textureData }) {

    val lineSpace = charMap.fontProps.sizePts * 1.2f
    val normHeight = charMap.fontProps.sizePts * 0.7f

    constructor(fontProps: FontProps, ctx: KoolContext) : this(ctx.assetMgr.createCharMap(fontProps))

    fun textWidth(string: String): Float {
        var width = 0f
        var maxWidth = 0f

        for (i in string.indices) {
            val c = string[i]
            width += charWidth(c)
            if (width > maxWidth) {
                maxWidth = width
            }
            if (c == '\n') {
                width = 0f
            }
        }
        return maxWidth
    }

    fun charWidth(char: Char): Float {
        return charMap[char]?.advance ?: 0f
    }

    override fun toString(): String {
        return "Font(${charMap.fontProps.family}, ${charMap.fontProps.sizePts}pts, ${charMap.fontProps.style})"
    }

    companion object {
        const val PLAIN = 0
        const val BOLD = 1
        const val ITALIC = 2

        const val SYSTEM_FONT = "-apple-system, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif"

        val STD_CHARS: String
        val DEFAULT_FONT_PROPS: FontProps

        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß°©"
            STD_CHARS = str
            DEFAULT_FONT_PROPS = FontProps(SYSTEM_FONT, 12f)
        }

        fun defaultFont(ctx: KoolContext): Font = Font(DEFAULT_FONT_PROPS, ctx)
    }
}

class CharMetrics {
    var width = 0f
    var height = 0f
    var xOffset = 0f
    var yBaseline = 0f
    var advance = 0f

    val uvMin = MutableVec2f()
    val uvMax = MutableVec2f()
}

class CharMap(val textureData: TextureData, private val map: Map<Char, CharMetrics>, val fontProps: FontProps) : Map<Char, CharMetrics> by map
