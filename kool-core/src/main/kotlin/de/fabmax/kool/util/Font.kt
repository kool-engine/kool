package de.fabmax.kool.util

import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.GlslGenerator
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */

fun uiFont(family: String, sizeDp: Float, dpi: Float, style: Int = Font.PLAIN, chars: String = Font.STD_CHARS): Font {
    val pts = (sizeDp * dpi / 96f)
    return Font(FontProps(family, pts, style, pts, chars))
}

fun fontShader(font: Font? = null, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    val props = ShaderProps()
    props.propsInit()
    // vertex color and texture color are required to render fonts
    props.isVertexColor = true
    props.isTextureColor = true
    val generator = GlslGenerator()

    // inject shader code to take color from static color and alpha from texture
    // static color rgb has to be pre-multiplied with texture alpha
    generator.injectors += object: GlslGenerator.GlslInjector {
        override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
            text.append("if (").append(GlslGenerator.LOCAL_NAME_TEX_COLOR).append(".a == 0.0) { discard; }")
            text.append(GlslGenerator.LOCAL_NAME_FRAG_COLOR).append(" = ")
                    .append(GlslGenerator.LOCAL_NAME_VERTEX_COLOR).append(" * ")
                    .append(GlslGenerator.LOCAL_NAME_TEX_COLOR).append(".a;\n")
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

class Font(val fontProps: FontProps) :
        Texture(TextureProps(fontProps.toString(),
                GL.LINEAR_MIPMAP_LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE),
                { getCharMap(fontProps).textureData }) {

    companion object {
        val PLAIN = 0
        val BOLD = 1
        val ITALIC = 2

        val DEFAULT_FONT: Font
        val STD_CHARS: String

        val SYSTEM_FONT = "-apple-system, \"Segoe UI\", Roboto, Helvetica, Arial, sans-serif"

        private val charMaps: MutableMap<FontProps, CharMap> = mutableMapOf()

        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß"
            STD_CHARS = str

            DEFAULT_FONT = Font(FontProps(SYSTEM_FONT, 12f))
        }

        private fun getCharMap(fontProps: FontProps): CharMap {
            var map = charMaps[fontProps]
            if (map == null) {
                map = Platform.createCharMap(fontProps)
                charMaps[fontProps] = map
            }
            return map
        }
    }

    val charMap: CharMap = getCharMap(fontProps)

    val lineSpace = fontProps.sizeUnits * 1.2f
    val normHeight = fontProps.sizeUnits * 0.7f

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

class CharMap(val textureData: TextureData, private val map: Map<Char, CharMetrics>) : Map<Char, CharMetrics> by map
