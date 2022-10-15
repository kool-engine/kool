package de.fabmax.kool.util

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
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
    val weight: Float = WEIGHT_REGULAR,
    val cutoff: Float = CUTOFF_SOLID,
) : Font(sizePts, if (weight > 0.1f) BOLD else PLAIN) {

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

    override fun derive(sizePts: Float) = MsdfFont(data, sizePts, weight, cutoff)

    override fun toString(): String {
        return "MsdfFont { name: ${data.meta.name}, info: ${data.meta.atlas} }"
    }

    companion object {
        const val WEIGHT_EXTRA_LIGHT = -0.15f
        const val WEIGHT_LIGHT = -0.1f
        const val WEIGHT_REGULAR = 0.0f
        const val WEIGHT_BOLD = 0.17f
        const val WEIGHT_EXTRA_BOLD = 0.25f

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