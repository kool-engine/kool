package de.fabmax.kool.platform

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.gl.GL_ALPHA
import de.fabmax.kool.util.*
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.math.round

/**
 * @author fabmax
 */

class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    init {
        canvas.width = maxWidth
        canvas.height = maxHeight
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D
    }

    fun createCharMap(fontProps: FontProps): CharMap {
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
        return CharMap(BufferedTextureData(buffer, maxWidth, texHeight, GL_ALPHA), metrics, fontProps)
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

//            val cwi = Math.ceil(charW).toDouble()
//            canvasCtx.beginPath()
//            canvasCtx.moveTo(x + padding - 0.5, y + hbb - 0.5)
//            canvasCtx.lineTo(x + padding + cwi - 0.5, y + hbb - 0.5)
//            canvasCtx.lineTo(x + padding + cwi - 0.5, y - hab + 0.5)
//            canvasCtx.lineTo(x + padding - 0.5, y - hab + 0.5)
//            canvasCtx.lineTo(x + padding - 0.5, y + hbb - 0.5)
//            canvasCtx.stroke()

            val widthPx = charW.toFloat()
            val heightPx = height.toFloat()
            val metrics = CharMetrics()
            metrics.width = widthPx * fontProps.sizeUnits / fontProps.sizePts
            metrics.height = heightPx * fontProps.sizeUnits / fontProps.sizePts
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() * fontProps.sizeUnits / fontProps.sizePts
            metrics.advance = metrics.width

            metrics.uvMin.set((x + padding).toFloat(), (y - hab).toFloat())
            metrics.uvMax.set((x + padding + widthPx).toFloat(), (y - hab).toFloat() + heightPx)
            map.put(c, metrics)

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
