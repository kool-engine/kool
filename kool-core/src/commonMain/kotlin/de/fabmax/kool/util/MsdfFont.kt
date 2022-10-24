package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import kotlin.math.abs
import kotlin.math.max

/**
 * Multi-Signed-Distance-Field based font. Provides good-looking text for pretty much arbitrary font sizes from a
 * single relatively small texture. However, unlike traditional texture-atlas based fonts the MSDF font texture has
 * to be pre-generated. See this awesome GitHub repo for more details:
 *
 *     https://github.com/Chlumsky/msdf-atlas-gen
 */
class MsdfFont(
    val data: MsdfFontData = DEFAULT_FONT_DATA,
    sizePts: Float = 12f,
    val italic: Float = ITALIC_NONE,
    val weight: Float = WEIGHT_REGULAR,
    val cutoff: Float = CUTOFF_SOLID,
    val glowColor: Color? = null
) : Font(sizePts) {

    override var scale: Float = 1f
    override val lineHeight: Float get() = scale * sizePts * data.meta.metrics.lineHeight

    private val emScale: Float
        get() = scale * sizePts

    override fun setScale(scale: Float, ctx: KoolContext) {
        this.scale = scale
    }

    override fun textDimensions(text: String, result: TextMetrics): TextMetrics {
        var lineWidth = 0f
        result.width = 0f
        result.height = lineHeight
        result.yBaseline = data.meta.metrics.ascender * emScale
        result.numLines = 1
        result.ascentPx = data.meta.metrics.ascender * emScale
        result.descentPx = data.meta.metrics.descender * emScale

        for (i in text.indices) {
            val c = text[i]
            if (c == '\n') {
                result.width = max(result.width, lineWidth)
                result.height += lineHeight
                result.numLines++
                lineWidth = 0f

            } else {
                val metrics = data.glyphMap[c] ?: continue
                lineWidth += metrics.advance * emScale
            }
        }
        result.width = max(result.width, lineWidth)
        result.width += abs(italic) * emScale
        return result
    }

    override fun charWidth(char: Char): Float {
        val g = data.glyphMap[char] ?: return 0f
        return g.advance * emScale
    }

    override fun charHeight(char: Char): Float {
        val g = data.glyphMap[char] ?: return 0f
        return (g.planeBounds.top - g.planeBounds.bottom) * emScale
    }

    override fun derive(sizePts: Float) = MsdfFont(data, sizePts, italic, weight, cutoff, glowColor)

    fun copy(
        sizePts: Float = this.sizePts,
        italic: Float = this.italic,
        weight: Float = this.weight,
        cutoff: Float = this.cutoff,
        glowColor: Color? = this.glowColor
    ) = MsdfFont(data, sizePts, italic, weight, cutoff, glowColor)

    override fun toString(): String {
        return "MsdfFont { name: ${data.meta.name}, info: ${data.meta.atlas} }"
    }

    companion object {
        const val WEIGHT_EXTRA_LIGHT = -0.09f
        const val WEIGHT_LIGHT = -0.06f
        const val WEIGHT_REGULAR = 0.0f
        const val WEIGHT_BOLD = 0.1f
        const val WEIGHT_EXTRA_BOLD = 0.15f

        const val ITALIC_NONE = 0.0f
        const val ITALIC_STD = 0.25f

        const val CUTOFF_SOLID = 1f
        const val CUTOFF_OUTLINED_THICK = 0.15f
        const val CUTOFF_OUTLINED_THIN = 0.1f

        val MSDF_TEX_PROPS = TextureProps(mipMapping = false, maxAnisotropy = 0)

        suspend fun create(fontPath: String, ctx: KoolContext) = create("${fontPath}.png", "${fontPath}.json", ctx)

        suspend fun create(fontMapPath: String, fontMetaPath: String, ctx: KoolContext): MsdfFont {
            val data = ctx.assetMgr.loadAsset(fontMetaPath) ?: throw IllegalStateException("Failed to load MsdfMeta $fontMetaPath")
            val meta = Json.Default.decodeFromString<MsdfMeta>(data.toArray().decodeToString())
            val fontData = MsdfFontData(ctx.assetMgr.loadAndPrepareTexture(fontMapPath, MSDF_TEX_PROPS), meta)
            return MsdfFont(fontData)
        }

        val DEFAULT_FONT_DATA: MsdfFontData
        val DEFAULT_FONT: MsdfFont

        init {
            val robotoMeta = Json.Default.decodeFromString<MsdfMeta>(
                BufferUtil.inflate(BufferUtil.decodeBase64(RobotoRegularMeta.data)).toArray().decodeToString())
            DEFAULT_FONT_DATA = MsdfFontData(Texture2d("fonts/font-roboto-regular.png", props = MSDF_TEX_PROPS), robotoMeta)
            DEFAULT_FONT = MsdfFont(DEFAULT_FONT_DATA)
        }
    }
}

class MsdfFontData(val map: Texture2d, val meta: MsdfMeta) {
    val glyphMap = meta.glyphs.associateBy { it.unicode.toChar() }
    val kerning = meta.kerning.associate { (it.unicode1 shl 16) or it.unicode2 to it.advance }
}

@Serializable
data class MsdfMeta(
    val atlas: MsdfAtlasInfo,
    val name: String,
    val metrics: MsdfMetrics,
    val glyphs: List<MsdfGlyph>,
    val kerning: List<MsdfKerning>
)

@Serializable
data class MsdfAtlasInfo(
    val type: String,
    val distanceRange: Float,
    val size: Int,
    val width: Int,
    val height: Int,
    val yOrigin: String
)

@Serializable
data class MsdfMetrics(
    val emSize: Float,
    val lineHeight: Float,
    val ascender: Float,
    val descender: Float,
    val underlineY: Float,
    val underlineThickness: Float
)

@Serializable
data class MsdfGlyph(
    val unicode: Int,
    val advance: Float,
    val planeBounds: MsdfRect = MsdfRect(0f, 0f, 0f, 0f),
    val atlasBounds: MsdfRect = MsdfRect(0f, 0f, 0f, 0f),
) {
    fun isEmpty(): Boolean = planeBounds.left == planeBounds.right
}

@Serializable
data class MsdfRect(
    val left: Float,
    val bottom: Float,
    val right: Float,
    val top: Float
)

@Serializable
data class MsdfKerning(
    val unicode1: Int,
    val unicode2: Int,
    val advance: Float
)

//
// Base64 encoded gzipped Roboto-regular MSDF meta-data (our default font).
// We keep this here instead of as an asset to have it instantaneously available. If it were an extra asset we would
// have to load it on startup (which would be an async operation in javascript). Hence, we could not use it in static
// initializers.
//
private object RobotoRegularMeta {
    val data = """
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
""".lineSequence().joinToString("")
}