package de.fabmax.kool.util

import de.fabmax.kool.Assets
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.pipeline.MipMapping
import de.fabmax.kool.pipeline.SamplerSettings
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import kotlin.math.max
import kotlin.math.round

class AtlasFont(
    val family: String = SYSTEM_FONT,
    sizePts: Float = 12f,
    val style: Int = PLAIN,
    val ascentEm: Float = 1.05f,
    val descentEm: Float = 0.35f,
    val heightEm: Float = 1.4f,
    val chars: String = STD_CHARS,
    val format: TexFormat = TexFormat.R,
    val mipMapping: MipMapping = MipMapping.Off,
    val samplerSettings: SamplerSettings = SamplerSettings(),
    val isExternalMap: Boolean = false
) : Font(sizePts) {

    var map: FontMap? = null
    val isLoaded: Boolean get() = map != null

    override var scale = 1f
    override val lineHeight: Float get() = round(sizePts * heightEm * scale)

    override fun setScale(scale: Float, ctx: KoolContext) {
        getOrLoadFontMap(scale)
    }

    override fun textDimensions(text: String, result: TextMetrics, enforceSameWidthDigits: Boolean): TextMetrics {
        return map?.textDimensions(text, result, enforceSameWidthDigits) ?: run {
            logE { "Unable to measure text $text with font ${this}: Font is not loaded" }
            result
        }
    }

    override fun charWidth(char: Char, enforceSameWidthDigits: Boolean): Float {
        return map?.charWidth(char, enforceSameWidthDigits) ?: run {
            logE { "Unable to measure char with font ${this}: Font is not loaded" }
            0f
        }
    }

    override fun charHeight(char: Char): Float {
        return map?.charHeight(char) ?: run {
            logE { "Unable to measure char with font ${this}: Font is not loaded" }
            0f
        }
    }

    override fun derive(sizePts: Float) = AtlasFont(family, sizePts, style, ascentEm, descentEm, heightEm, chars, format, mipMapping, samplerSettings, isExternalMap)

    override fun toString(): String {
        return "AtlasFont { family: $family, size: $sizePts, style: $style, isExternalMap: $isExternalMap }"
    }

    @Suppress("DEPRECATION")
    fun getOrLoadFontMap(scale: Float): FontMap {
        if (isExternalMap) {
            return map ?: throw IllegalStateException("External font map has not yet been set for font $this")
        }
        val map = map ?: Assets.getOrCreateAtlasFontMap(this, scale).also { map = it }
        if (this.scale != scale) {
            Assets.updateAtlasFontMap(this, scale)
        }
        return map
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as AtlasFont

        if (family != other.family) return false
        if (sizePts != other.sizePts) return false
        if (style != other.style) return false
        if (chars != other.chars) return false
        if (isExternalMap != other.isExternalMap) return false

        return true
    }

    override fun hashCode(): Int {
        var result = family.hashCode()
        result = 31 * result + style
        result = 31 * result + sizePts.hashCode()
        result = 31 * result + chars.hashCode()
        result = 31 * result + isExternalMap.hashCode()
        return result
    }


    companion object {
        const val PLAIN = 0
        const val BOLD = 1
        const val ITALIC = 2

        const val SYSTEM_FONT = "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen, Ubuntu, Cantarell, \"Open Sans\", sans-serif"
        val STD_CHARS: String
        val DEFAULT_FONT: AtlasFont

        init {
            var str = ""
            // ascii characters
            for (i in 32..126) {
                str += i.toChar()
            }
            // common western latin characters
            for (i in 160..255) {
                str += i.toChar()
            }
            STD_CHARS = str
            DEFAULT_FONT = AtlasFont()
        }
    }
}

class FontMap(
    val font: AtlasFont,
    val texture: Texture2d,
    private val map: MutableMap<Char, CharMetrics> = mutableMapOf()
) : MutableMap<Char, CharMetrics> by map {

    val maxWidthDigit: CharMetrics? = ('0' ..'9').mapNotNull { map[it] }.maxByOrNull { it.advance }

    fun textDimensions(text: String, result: TextMetrics, enforceSameWidthDigits: Boolean): TextMetrics {
        var lineWidth = 0f
        result.baselineWidth = 0f
        result.height = 0f
        result.yBaseline = 0f
        result.numLines = 1
        result.ascentPx = font.ascentEm * font.sizePts * font.scale
        result.descentPx = font.descentEm * font.sizePts * font.scale

        for (i in text.indices) {
            val c = text[i]
            if (c == '\n') {
                result.baselineWidth = max(result.width, lineWidth)
                result.height += font.lineHeight
                result.numLines++
                lineWidth = 0f

            } else {
                val metrics = if (c.isDigit() && enforceSameWidthDigits) {
                    maxWidthDigit
                } else {
                    map[c]
                }?: continue
                lineWidth += metrics.advance
                if (i == 0) {
                    result.height = metrics.height
                    result.yBaseline = metrics.yBaseline
                }
            }
        }
        result.baselineWidth = max(result.width, lineWidth)
        if (font.style and AtlasFont.ITALIC != 0) {
            result.paddingEnd = 0.1f * font.sizePts * font.scale
        }
        return result
    }

    fun charWidth(char: Char, enforceSameWidthDigits: Boolean): Float {
        return if (char.isDigit() && enforceSameWidthDigits) {
            maxWidthDigit?.advance ?: map[char]?.advance ?: 0f
        } else {
            map[char]?.advance ?: 0f
        }
    }

    fun charHeight(char: Char): Float {
        return map[char]?.height ?: 0f
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

    val isEmpty: Boolean get() = width == 0f
}
