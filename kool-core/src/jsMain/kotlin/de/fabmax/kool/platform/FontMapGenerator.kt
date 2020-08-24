package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.BufferedTextureData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.util.*
import kotlinx.browser.document
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.round

/**
 * @author fabmax
 */

class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    private val charMaps = mutableMapOf<FontProps, CharMap>()

    init {
        canvas.width = maxWidth
        canvas.height = maxHeight
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D
    }

    fun getCharMap(fontProps: FontProps): CharMap = charMaps.getOrPut(fontProps) { generateCharMap(fontProps) }

    private fun generateCharMap(fontProps: FontProps): CharMap {
        // clear canvas
        canvasCtx.clearRect(0.0, 0.0, maxWidth.toDouble(), maxHeight.toDouble())

        var style = "lighter "
        if (fontProps.style and Font.BOLD != 0) {
            style = "bold "
        }
        if (fontProps.style and Font.ITALIC != 0) {
            style += "italic "
        }

        val metrics: MutableMap<Char, CharMetrics> = mutableMapOf()
        val texHeight = makeMap(fontProps, style, metrics)

        val data = canvasCtx.getImageData(0.0, 0.0, maxWidth.toDouble(), texHeight.toDouble())

        // alpha texture
        val buffer = createUint8Buffer(maxWidth * texHeight)
        for (i in 0 until buffer.capacity) {
            buffer.put(data.data[i*4+3])
        }
        return CharMap(BufferedTextureData(buffer, maxWidth, texHeight, TexFormat.R), metrics, fontProps)
    }

    private fun makeMap(fontProps: FontProps, style: String, map: MutableMap<Char, CharMetrics>): Int {
        canvasCtx.font = "$style${fontProps.sizePts}px ${fontProps.family}"
        canvasCtx.fillStyle = "#ffffff"

        val padding = 3.0
        // line height above baseline
        val hab = round(fontProps.sizePts * 1.1)
        // line height below baseline
        val hbb = round(fontProps.sizePts * 0.5)
        // overall line height
        val height = round(fontProps.sizePts * 1.6)

        // first pixel is opaque
        canvasCtx.beginPath()
        canvasCtx.moveTo(0.5, 0.0)
        canvasCtx.lineTo(0.5, 1.0)
        canvasCtx.stroke()

        var x = 1.0
        var y = hab
        for (c in fontProps.chars) {
            // super-ugly special treatment for 'j' which has a negative x-offset for most fonts
            if (c == 'j') {
                x += fontProps.sizePts * 0.1f
            }

            val txt = "$c"
            val charW = round(canvasCtx.measureText(txt).width)
            val paddedWidth = round(charW + padding * 2)
            if (x + paddedWidth > maxWidth) {
                x = 0.0
                y += height + 10
                if (y + hbb > maxHeight) {
                    break
                }
            }

            val widthPx = charW.toFloat()
            val heightPx = height.toFloat()
            val metrics = CharMetrics()
            metrics.width = widthPx
            metrics.height = heightPx
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat()
            metrics.advance = metrics.width

            metrics.uvMin.set((x + padding).toFloat(), (y - hab).toFloat())
            metrics.uvMax.set((x + padding + widthPx).toFloat(), (y - hab).toFloat() + heightPx)
            map[c] = metrics

            canvasCtx.fillText(txt, x + padding, y)
            x += paddedWidth
        }

        val texW = maxWidth
        val texH = nextPow2(y + hbb)

        for (cm in map.values) {
            cm.uvMin.x /= texW
            cm.uvMin.y /= texH
            cm.uvMax.x /= texW
            cm.uvMax.y /= texH
        }

        return texH
    }

    private fun nextPow2(value: Double): Int {
        var pow2 = 16
        while (pow2 < value && pow2 < maxHeight) {
            pow2 = pow2 shl 1
        }
        return pow2
    }
}
