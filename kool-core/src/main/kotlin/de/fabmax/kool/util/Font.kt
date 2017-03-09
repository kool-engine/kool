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

fun uiFont(family: String, sizeDp: Float, dpi: Float, style: Int = Font.PLAIN): Font {
    val pts = (sizeDp * dpi / 96f)
    return Font(family, pts, pts, style)
}

class Font(val family: String, val sizePts: Float, val sizeUnits: Float, val style: Int = Font.PLAIN) {
    companion object {
        val PLAIN = 0
        val BOLD = 1
        val ITALIC = 2

        val DEFAULT_FONT: Font

        // todo: For now char maps are created with a hardcoded set of characters (ASCII + a few german ones)
        // theoretically arbitrary unicode characters are supported
        private val STD_CHARS: String
        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß"
            STD_CHARS = str

            DEFAULT_FONT = uiFont("sans-serif", 12f, 96f, PLAIN)
        }
    }

    val charMap: CharMap = Platform.createCharMap(this, STD_CHARS)
    val texture = Texture(TextureProps(toString(), GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)) {
        charMap.textureData
    }

    var lineSpace = sizeUnits * 1.2f

    fun textWidth(string: String): Float {
        var width = 0f
        var maxWidth = 0f

        for (i in string.indices) {
            val c = string[i]
            width += charMap[c]?.advance ?: 0f
            if (width > maxWidth) {
                maxWidth = width
            }
            if (c == '\n') {
                width = 0f
            }
        }
        return maxWidth
    }

    override fun toString(): String {
        return "$family-${sizePts}pt/${sizeUnits}units-$style"
    }
}

fun fontShader(font: Font?, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    val props = ShaderProps()
    props.propsInit()
    // vertex color and texture color are required to render fonts
    props.isVertexColor = true
    props.isTextureColor = true
    val generator = Platform.createDefaultShaderGenerator()

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
    shader.texture = font?.texture
    return shader
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
