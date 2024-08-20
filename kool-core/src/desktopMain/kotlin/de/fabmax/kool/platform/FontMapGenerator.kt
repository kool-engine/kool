package de.fabmax.kool.platform

import de.fabmax.kool.Assets
import de.fabmax.kool.loadBlob
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.*
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayInputStream
import kotlin.math.ceil
import kotlin.math.round

/**
 * @author fabmax
 */

private typealias AwtFont = java.awt.Font

internal class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB)
    private val clearColor = Color(0, 0, 0, 0)

    private val availableFamilies: Set<String>
    private val customFonts = mutableMapOf<String, AwtFont>()

    init {
        val families: MutableSet<String> = mutableSetOf()
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        for (family in ge.availableFontFamilyNames) {
            families.add(family)
        }
        availableFamilies = families
    }

    internal fun loadCustomFonts(customTtfFonts: Map<String, String>) {
        customTtfFonts.forEach { (family, path) ->
            runBlocking {
                Assets.loadBlob(path).onSuccess { blob ->
                    ByteArrayInputStream(blob.toArray()).use {
                        val ttfFont = AwtFont.createFont(AwtFont.TRUETYPE_FONT, it)
                        customFonts[family] = ttfFont
                        logD { "Loaded custom font: $family" }
                    }
                }
            }
        }
    }

    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        val g = canvas.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // clear canvas
        g.background = clearColor
        g.clearRect(0, 0, maxWidth, maxHeight)

        var style = AwtFont.PLAIN
        if (font.style and AtlasFont.BOLD != 0) {
            style = AwtFont.BOLD
        }
        if (font.style and AtlasFont.ITALIC != 0) {
            style += AwtFont.ITALIC
        }

        var customFont: AwtFont? = null
        var family = AwtFont.SANS_SERIF
        val fams = font.family.split(",")
        for (fam in fams) {
            val f = fam.trim().replace("\"", "")
            if (f in customFonts.keys) {
                customFont = customFonts[f]
                break
            } else if (f == "sans-serif") {
                family = AwtFont.SANS_SERIF
                break
            } else if (f == "monospaced") {
                family = AwtFont.MONOSPACED
                break
            } else if (f in availableFamilies) {
                family = f
                break
            }
        }

        val size = round(font.sizePts * fontScale)
        val awtFont = customFont?.deriveFont(font.style, size) ?: AwtFont(family, style, size.toInt())
        // theoretically we could specify an accurate font weight, however this does not have any effect for all fonts I tried
        //awtFont = awtFont.deriveFont(mapOf<TextAttribute, Any>(TextAttribute.WEIGHT to TextAttribute.WEIGHT_EXTRA_LIGHT))

        g.font = awtFont
        g.color = Color.BLACK

        outMetrics.clear()
        val texHeight = makeMap(font, size, g, outMetrics)
        val buffer = getCanvasAlphaData(maxWidth, texHeight)

        logD { "Generated font map for (${font}, scale=${fontScale})" }
        //ImageIO.write(canvas, "png", File("${g.font.family}-${g.font.size}.png"))

        return BufferedImageData2d(buffer, maxWidth, texHeight, TexFormat.R)
    }

    private fun getCanvasAlphaData(width: Int, height: Int): Uint8Buffer {
        val imgBuf = canvas.data.dataBuffer as DataBufferInt
        val pixels = imgBuf.bankData[0]
        val buffer = Uint8Buffer(width * height)
        for (i in 0 until width * height) {
            buffer.put((pixels[i] shr 24).toByte())
        }
        return buffer
    }

    private fun makeMap(font: AtlasFont, size: Float, g: Graphics2D, outMetrics: MutableMap<Char, CharMetrics>): Int {
        val fm = g.fontMetrics

        // unfortunately java font metrics don't provide methods to determine the precise pixel bounds of individual
        // characters and some characters (e.g. 'j', 'f') extend further to left / right than the given char width
        // therefore we need to add generous padding to avoid artefacts
        val isItalic = font.style == AtlasFont.ITALIC
        val padLeft = ceil(if (isItalic) size / 2f else size / 5f).toInt()
        val padRight = ceil(if (isItalic) size / 2f else size / 10f).toInt()
        val padTop = 0
        val padBottom = 0

        val ascent = if (font.ascentEm == 0f) (fm.ascent + fm.leading) else ceil(font.ascentEm * size).toInt()
        val descent = if (font.descentEm == 0f) fm.descent else ceil(font.descentEm * size).toInt()
        val height = if (font.heightEm == 0f) fm.height else ceil(font.heightEm * size).toInt()

        // first pixel is opaque
        g.fillRect(0, 0, 1, 1)

        var x = 1
        var y = ascent
        for (c in font.chars) {
            val charW = fm.charWidth(c)
            val paddedWidth = charW + padLeft + padRight
            if (x + paddedWidth > maxWidth) {
                x = 0
                y += height + padBottom + padTop
                if (y + descent > maxHeight) {
                    logE { "Unable to render full font map: Maximum texture size exceeded" }
                    break
                }
            }

            val metrics = CharMetrics()
            metrics.width = paddedWidth.toFloat()
            metrics.height = (height + padBottom + padTop).toFloat()
            metrics.xOffset = padLeft.toFloat()
            metrics.yBaseline = ascent.toFloat()
            metrics.advance = charW.toFloat()

            metrics.uvMin.set(
                x.toFloat(),
                (y - ascent - padTop).toFloat()
            )
            metrics.uvMax.set(
                (x + paddedWidth).toFloat(),
                (y - ascent + padBottom + height).toFloat()
            )
            outMetrics[c] = metrics

            g.drawString("$c", x + padLeft, y)
            x += paddedWidth
        }

        val texW = maxWidth
        val texH = nextPow2(y + descent)

        for (cm in outMetrics.values) {
            cm.uvMin.x /= texW
            cm.uvMin.y /= texH
            cm.uvMax.x /= texW
            cm.uvMax.y /= texH
        }
        return texH
    }

    private fun nextPow2(value: Int): Int {
        var pow2 = 16
        while (pow2 < value && pow2 < maxHeight) {
            pow2 = pow2 shl 1
        }
        return pow2
    }
}
