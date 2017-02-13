package de.fabmax.kool.util

import de.fabmax.kool.Texture2d
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.platform.ShaderGenerator
import de.fabmax.kool.shading.BasicShader
import de.fabmax.kool.shading.ShaderProps

/**
 * @author fabmax
 */

class Font(val family: String, val size: Float, val style: Int = Font.PLAIN) {
    companion object {
        val PLAIN = 0
        val BOLD = 1
        val ITALIC = 2

        val DEFAULT_FONT: Font

        // todo: For now char maps are created with a hardcoded set of characters (ASCII + a few german ones)
        // todo: theoretically arbitrary unicode characters are supported
        private val STD_CHARS: String
        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß"
            STD_CHARS = str

            DEFAULT_FONT = Font("sans-serif", 12f, PLAIN)
        }
    }

    val charMap: CharMap = Platform.createCharMap(this, STD_CHARS)

    var lineSpace = size * 1.2f

    fun stringWidth(string: String): Float {
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
}

fun fontShader(font: Font?, color: Color = Color.WHITE, propsInit: ShaderProps.() -> Unit = { }): BasicShader {
    val props = ShaderProps()
    props.propsInit()
    // static color and texture color are required to render fonts
    props.isStaticColor = true
    props.isTextureColor = true
    val generator = Platform.createDefaultShaderGenerator()

    // inject shader code to take color from static color and alpha from texture
    // static color rgb has to be pre-multiplied with texture alpha
    generator.injectors += object: ShaderGenerator.GlslInjector {
        override fun fsAfterSampling(shaderProps: ShaderProps, text: StringBuilder) {
            text.append(ShaderGenerator.LOCAL_NAME_FRAG_COLOR).append(" = vec4(")
                    .append(ShaderGenerator.LOCAL_NAME_STATIC_COLOR).append(".rgb * ")
                    .append(ShaderGenerator.LOCAL_NAME_TEX_COLOR).append(".a, ")
                    .append(ShaderGenerator.LOCAL_NAME_TEX_COLOR).append(".a);\n")
        }
    }

    val shader = BasicShader(props, generator)
    shader.texture = font?.charMap?.fontTexture
    shader.staticColor.set(color)
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

class CharMap(val fontTexture: Texture2d, private val map: Map<Char, CharMetrics>) : Map<Char, CharMetrics> by map
