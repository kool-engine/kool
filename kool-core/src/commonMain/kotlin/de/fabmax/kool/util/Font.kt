package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.pipeline.*
import kotlin.math.round

/**
 * @author fabmax
 */

fun uiFont(family: String, sizeDp: Float, uiDpi: Float, ctx: KoolContext, style: Int = Font.PLAIN, chars: String = Font.STD_CHARS): Font {
    val pts = (sizeDp * uiDpi / 96f)
    return Font(FontProps(family, pts, style, chars), ctx)
}

data class FontProps(
        val family: String,
        val sizePts: Float,
        val style: Int = Font.PLAIN,
        val chars: String = Font.STD_CHARS) {

    override fun toString(): String {
        return "FontProps($family, ${sizePts}pts, $style)"
    }
}

class Font(val charMap: CharMap) : Texture2d(
        TextureProps(
                addressModeU = AddressMode.CLAMP_TO_EDGE,
                addressModeV = AddressMode.CLAMP_TO_EDGE
        ),
        charMap.toString(),
        loader = BufferedTextureLoader(charMap.textureData)) {

    override val type = "Font"

    val lineSpace = round(charMap.fontProps.sizePts * 1.2f)
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
        return "Font(${charMap.fontProps})"
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
