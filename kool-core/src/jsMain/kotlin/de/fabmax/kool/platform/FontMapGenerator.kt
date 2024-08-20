package de.fabmax.kool.platform

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.math.clamp
import de.fabmax.kool.math.smoothStep
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.*
import kotlinx.browser.document
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise
import kotlin.math.ceil
import kotlin.math.pow
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    val loadingFonts = mutableListOf<Promise<FontFace>>()

    init {
        val pixelRatio = (KoolSystem.getContextOrNull() as JsContext?)?.pixelRatio ?: 1.0
        canvas.style.width = "${(maxWidth / pixelRatio).roundToInt()}"
        canvas.style.height = "${(maxHeight / pixelRatio).roundToInt()}"
        canvas.width = maxWidth
        canvas.height = maxHeight
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D

        KoolSystem.configJs.customTtfFonts.forEach { (family, url) ->
            loadFont(family, url)
        }
    }

    private fun loadFont(family: String, url: String) {
        val font = FontFace(family, "url($url)")
        val promise = font.load()
        loadingFonts += promise
        promise.then { f ->
            js("document.fonts.add(f);")
            logD { "Loaded custom font: ${f.family}" }
        }
    }

    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        val fontSize = (font.sizePts * fontScale).roundToInt()

        // clear canvas
        canvasCtx.fillStyle = "#000000"
        canvasCtx.fillRect(0.0, 0.0, maxWidth.toDouble(), maxHeight.toDouble())
        // draw font chars
        val texHeight = makeMap(font, fontSize, outMetrics)
        // copy image data
        val data = canvasCtx.getImageData(0.0, 0.0, maxWidth.toDouble(), texHeight.toDouble())

        // alpha correction lut:
        // boost font contrast by increasing contrast / reducing anti-aliasing (otherwise small fonts appear quite
        // blurry, especially in Chrome)
        val alphaLut = ByteArray(256) { i ->
            val a = i / 255f
            // corrected value: boosted contrast
            val ac = a.pow(1.5f) * 1.3f - 0.15f
            // mix original value and corrected one based on font size:
            // max correction for sizes <= 12, no correction for sizes >= 40
            val cw = smoothStep(12f, 40f, fontSize.toFloat())
            val c = a * cw + ac * (1f - cw)
            (c.clamp(0f, 1f) * 255f).toInt().toByte()
        }

        // alpha texture
        val buffer = Uint8Buffer(maxWidth * texHeight)
        for (i in 0 until buffer.capacity) {
            val a = data.data[i*4].toInt() and 0xff
            buffer.put(alphaLut[a])
        }
        logD { "Generated font map for (${font}, scale=${fontScale})" }
        return BufferedImageData2d(buffer, maxWidth, texHeight, TexFormat.R)
    }

    private fun makeMap(font: AtlasFont, size: Int, outMetrics: MutableMap<Char, CharMetrics>): Int {
        var style = ""
        if (font.style and AtlasFont.BOLD != 0) {
            style = "bold "
        }
        if (font.style and AtlasFont.ITALIC != 0) {
            style += "italic "
        }

        val fontStr = "$style ${size}px ${font.family}"
        canvasCtx.font = fontStr
        canvasCtx.fillStyle = "#ffffff"
        canvasCtx.strokeStyle = "#ffffff"

        logD { "generate font: $fontStr" }

        val fm = canvasCtx.measureText("A")

        val padLeft = ceil(size / 10f).toInt()
        val padRight = ceil(size / 10f).toInt()
        val padTop = 0
        val padBottom = ceil(size / 10f).toInt()

        var fontAscent = ceil(size * 1.05).toInt()
        var fontDescent = ceil(size * 0.35).toInt()

        try {
            fontAscent = ceil(fm.fontBoundingBoxAscent).toInt()
            fontDescent = ceil(fm.fontBoundingBoxAscent).toInt()
        } catch (e: Exception) {
            // silently ignored: firefox currently does not have font ascent / descent measures
            // use defualts instead
        }

        val ascent = if (font.ascentEm == 0f) fontAscent else ceil(font.ascentEm * size).toInt()
        val descent = if (font.descentEm == 0f) fontDescent else ceil(font.descentEm * size).toInt()
        val height = if (font.heightEm == 0f) ascent + descent else ceil(font.heightEm * size).toInt()

        // first pixel is opaque
        canvasCtx.beginPath()
        canvasCtx.moveTo(0.5, 0.0)
        canvasCtx.lineTo(0.5, 1.0)
        canvasCtx.stroke()

        var x = 1
        var y = ascent
        for (c in font.chars) {
            val txt = "$c"
            val txtMetrics = canvasCtx.measureText(txt)
            val charW = ceil(txtMetrics.actualBoundingBoxRight + txtMetrics.actualBoundingBoxLeft).toInt()
            val paddedWidth = charW + padLeft + padRight
            if (x + paddedWidth > maxWidth) {
                x = 0
                y += (height + padBottom + padTop)
                if (y + descent > maxHeight) {
                    logE { "Unable to render full font map: Maximum texture size exceeded" }
                    break
                }
            }

            val xOff = txtMetrics.actualBoundingBoxLeft + padLeft
            val metrics = CharMetrics()
            metrics.width = paddedWidth.toFloat()
            metrics.height = (height + padBottom + padTop).toFloat()
            metrics.xOffset = xOff.toFloat()
            metrics.yBaseline = ascent.toFloat()
            metrics.advance = txtMetrics.width.toFloat()

            metrics.uvMin.set(
                x.toFloat(),
                (y - ascent - padTop).toFloat()
            )
            metrics.uvMax.set(
                (x + paddedWidth).toFloat(),
                (y - ascent + padBottom + height).toFloat()
            )
            outMetrics[c] = metrics

            canvasCtx.fillText(txt, x + xOff, y.toDouble())
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

external class FontFace(family: String, source: String) {
    val family: String

    fun load(): Promise<FontFace>
}
