package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Texture2d
import kotlin.math.max
import kotlin.math.round

/**
 * @author fabmax
 */

data class Font(
    val family: String,
    val sizePts: Float,
    val style: Int = PLAIN,
    val chars: String = STD_CHARS,
    val magFilter: FilterMethod = FilterMethod.LINEAR,
    val minFilter: FilterMethod = FilterMethod.LINEAR,
    val mipMapping: Boolean = false,
    val maxAnisotropy: Int = 0,
    val sampleScale: Float = 1f,
    val isCustomMap: Boolean = false
) {

    var map: FontMap? = null

    val isLoaded: Boolean get() = map != null

    val scale: Float get() = map?.scale ?: 0f
    val lineSpace: Float get() = map?.lineSpace ?: 0f
    val normHeight: Float get() = map?.normHeight ?: 0f

    fun textWidth(text: String): Float {
        return map?.textWidth(text) ?: run {
            logE { "Unable to measure text $text with font ${toStringShort()}: Font is not loaded" }
            0f
        }
    }

    fun textDimensions(text: String, result: TextMetrics = TextMetrics()): TextMetrics {
        return map?.textDimensions(text, result) ?: run {
            logE { "Unable to measure text $text with font ${toStringShort()}: Font is not loaded" }
            result
        }
    }

    fun charWidth(char: Char): Float {
        return map?.charWidth(char) ?: run {
            logE { "Unable to measure char with font ${toStringShort()}: Font is not loaded" }
            0f
        }
    }

    fun charHeight(char: Char): Float {
        return map?.charHeight(char) ?: run {
            logE { "Unable to measure char with font ${toStringShort()}: Font is not loaded" }
            0f
        }
    }

    fun getOrLoadFontMap(ctx: KoolContext, scale: Float = 1f): FontMap {
        val map = map ?: ctx.assetMgr.getOrCreateFontMap(this, scale).also { map = it }
        if (map.scale != scale && !isCustomMap) {
            ctx.assetMgr.updateFontMap(this, scale)
        }
        return map
    }

    fun toStringShort(): String {
        return "Font { family: $family, size: $sizePts, style: $style }"
    }

    companion object {
        const val PLAIN = 0
        const val BOLD = 1
        const val ITALIC = 2

        const val SYSTEM_FONT = "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen, Ubuntu, Cantarell, \"Open Sans\", \"Helvetica Neue\", sans-serif"

        val STD_CHARS: String
        val DEFAULT_FONT: Font

        init {
            var str = ""
            for (i in 32..126) {
                str += i.toChar()
            }
            str += "äÄöÖüÜß°©"
            STD_CHARS = str
            DEFAULT_FONT = Font(SYSTEM_FONT, 12f)
        }
    }
}

class FontMap(
    val font: Font,
    val texture: Texture2d,
    private val map: MutableMap<Char, CharMetrics> = mutableMapOf()
) : MutableMap<Char, CharMetrics> by map {

    var scale = 1f
        private set
    var lineSpace = round(font.sizePts * 1.2f)
        private set
    var normHeight = font.sizePts * 0.7f
        private set

    fun textWidth(text: String): Float {
        var width = 0f
        var maxWidth = 0f
        for (i in text.indices) {
            val c = text[i]
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

    fun textDimensions(text: String, result: TextMetrics = TextMetrics()): TextMetrics {
        var lineWidth = 0f
        result.width = 0f
        result.height = 0f
        result.yBaseline = 0f
        result.numLines = 1

        for (i in text.indices) {
            val c = text[i]

            if (c == '\n') {
                result.width = max(result.width, lineWidth)
                result.height += lineSpace
                result.numLines++
                lineWidth = 0f

            } else {
                val metrics = map[c] ?: continue
                lineWidth += metrics.advance
                if (i == 0) {
                    result.height = metrics.height
                    result.yBaseline = metrics.yBaseline
                }
            }
        }
        result.width = max(result.width, lineWidth)
        return result
    }

    fun charWidth(char: Char): Float {
        return map[char]?.advance ?: 0f
    }

    fun charHeight(char: Char): Float {
        return map[char]?.height ?: 0f
    }

    fun applyScale(scale: Float) {
        this.scale = scale
        lineSpace = round(font.sizePts * 1.2f * scale)
        normHeight = font.sizePts * 0.7f * scale
    }
}

class TextMetrics {
    var width = 0f
    var height = 0f
    var yBaseline = 0f
    var numLines = 0
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
