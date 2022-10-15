package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import kotlin.math.max
import kotlin.math.round

class AtlasFont(
    val family: String = SYSTEM_FONT,
    sizePts: Float = 12f,
    style: Int = PLAIN,
    val ascentEm: Float = 1.05f,
    val descentEm: Float = 0.35f,
    val heightEm: Float = 1.4f,
    val chars: String = STD_CHARS,
    val fontMapProps: TextureProps = DEFAULT_FONT_TEX_PROPS,
    val isExternalMap: Boolean = false
) : Font(sizePts, style) {

    var map: FontMap? = null
    val isLoaded: Boolean get() = map != null

    override var scale = 1f
        set(value) {
            println("$family: scale set to $value")
            field = value
        }
    override val lineHeight: Float get() = round(sizePts * heightEm * scale)

    override fun setScale(scale: Float, ctx: KoolContext) {
        getOrLoadFontMap(ctx, scale)
    }

    override fun textDimensions(text: String, result: TextMetrics): TextMetrics {
        return map?.textDimensions(text, result) ?: run {
            logE { "Unable to measure text $text with font ${this}: Font is not loaded" }
            result
        }
    }

    override fun charWidth(char: Char): Float {
        return map?.charWidth(char) ?: run {
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

    override fun derive(sizePts: Float) = AtlasFont(family, sizePts, style, ascentEm, descentEm, heightEm, chars, fontMapProps, isExternalMap)

    override fun toString(): String {
        return "AtlasFont { family: $family, size: $sizePts, style: $style, isExternalMap: $isExternalMap }"
    }

    fun getOrLoadFontMap(ctx: KoolContext, scale: Float): FontMap {
        if (isExternalMap) {
            return map ?: throw IllegalStateException("External font map has not yet been set for font $this")
        }
        val map = map ?: ctx.assetMgr.getOrCreateFontMap(this, scale).also { map = it }
        if (this.scale != scale) {
            println("this.scale: ${this.scale} -> $scale")
            ctx.assetMgr.updateFontMap(this, scale)
            println("after update this.scale: ${this.scale}")
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
        const val SYSTEM_FONT = "-apple-system, BlinkMacSystemFont, \"Segoe UI\", Roboto, Oxygen, Ubuntu, Cantarell, \"Open Sans\", sans-serif"
        val STD_CHARS: String
        val DEFAULT_FONT: AtlasFont

        val DEFAULT_FONT_TEX_PROPS = TextureProps(
            format = TexFormat.R,
            mipMapping = false,
            maxAnisotropy = 0
        )

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
    val font: Font,
    val texture: Texture2d,
    private val map: MutableMap<Char, CharMetrics> = mutableMapOf()
) : MutableMap<Char, CharMetrics> by map {

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
                result.height += (font as AtlasFont).lineHeight
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
