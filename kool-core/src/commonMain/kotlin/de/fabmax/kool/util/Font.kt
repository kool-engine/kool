package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.defaultProps
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.GlslGenerator
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */

fun uiFont(family: String, sizeDp: Float, uiDpi: Float, ctx: KoolContext, style: Int = Font.PLAIN, chars: String = Font.STD_CHARS): Font {
    val pts = (sizeDp * uiDpi / 96f)
    return Font(FontProps(family, pts, style, pts, chars), ctx)
}

fun fontShader(font: Font? = null, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    val props = ShaderProps()
    props.propsInit()
    // vertex color and texture color are required to render fonts
    props.isVertexColor = true
    props.isTextureColor = true
    props.isDiscardTranslucent = true
    val generator = GlslGenerator()

    // inject shader code to take color from static color and alpha from texture
    // static color rgb has to be pre-multiplied with texture alpha
    generator.injectors += object: GlslGenerator.GlslInjector {
        override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder, ctx: KoolContext) {
            text.append("${ctx.glCapabilities.glslDialect.fragColorBody} = ${GlslGenerator.L_VERTEX_COLOR} * ${GlslGenerator.L_TEX_COLOR}.a;\n")
        }
    }

    val shader = BasicShader(props, generator)
    shader.texture = font
    return shader
}

data class FontProps(
        val family: String,
        val sizePts: Float,
        val style: Int = Font.PLAIN,
        val sizeUnits: Float = sizePts,
        var chars: String = Font.STD_CHARS)

class Font(val fontProps: FontProps, ctx: KoolContext) :
        Texture(defaultProps(fontProps.toString()), { getCharMap(fontProps, it).textureData }) {

    companion object {
        const val PLAIN = 0
        const val BOLD = 1
        const val ITALIC = 2

        const val SYSTEM_FONT = "-apple-system, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif"

        val STD_CHARS: String
        val DEFAULT_FONT_PROPS: FontProps

        private val charMaps: MutableMap<FontProps, CharMap> = mutableMapOf()

        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß°©"
            STD_CHARS = str
            DEFAULT_FONT_PROPS = FontProps(SYSTEM_FONT, 12f)
        }

        fun defaultFont(ctx: KoolContext): Font = Font(Font.DEFAULT_FONT_PROPS, ctx)

        private fun getCharMap(fontProps: FontProps, ctx: KoolContext): CharMap {
            var map = charMaps[fontProps]
            if (map == null) {
                map = ctx.assetMgr.createCharMap(fontProps)
                charMaps[fontProps] = map
            }
            return map
        }
    }

    val charMap: CharMap

    val lineSpace = fontProps.sizeUnits * 1.2f
    val normHeight = fontProps.sizeUnits * 0.7f

    init {
        charMap = getCharMap(fontProps, ctx)
    }

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
        return "Font(${fontProps.family}, ${fontProps.sizePts}pts, ${fontProps.style})"
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
