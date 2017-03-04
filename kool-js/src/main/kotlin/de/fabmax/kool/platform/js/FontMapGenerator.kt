package de.fabmax.kool.platform.js

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Font
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document
import kotlin.js.Math

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

    fun createCharMap(font: Font, chars: String): CharMap {
        // clear canvas
        canvasCtx.clearRect(0.0, 0.0, maxWidth.toDouble(), maxHeight.toDouble())

        // in canvas fonts seem to be pretty heavy by default, use lighter to compensate that
        var style = "lighter "
        if (font.style and Font.BOLD != 0) {
            style = "bold "
        }
        if (font.style and Font.ITALIC != 0) {
            style += "italic "
        }

        val metrics: MutableMap<Char, CharMetrics> = mutableMapOf()
        val texHeight = makeMap(chars, font, style, metrics)

        val data = canvasCtx.getImageData(0.0, 0.0, maxWidth.toDouble(), texHeight.toDouble())

        // alpha texture
        val buffer = Platform.createUint8Buffer(maxWidth * texHeight)
        for (i in 0..buffer.capacity-1) {
            buffer.put(data.data[i*4+3])
        }
        return CharMap(BufferedTextureData(buffer, maxWidth, texHeight, GL.ALPHA), metrics)
    }

    private fun makeMap(chars: String, font: Font, style: String, map: MutableMap<Char, CharMetrics>): Int {
        canvasCtx.font = "$style${font.sizePts}px ${font.family}"
        canvasCtx.fillStyle = "#ffffff"

        val padding = 3.0
        // line height above baseline
        val hab = Math.round(font.sizePts * 1.1).toDouble()
        // line height below baseline
        val hbb = Math.round(font.sizePts * 0.5).toDouble()
        // overall line height
        val height = Math.round(font.sizePts * 1.6).toDouble()

        // first pixel is opaque
        canvasCtx.beginPath()
        canvasCtx.moveTo(0.5, 0.0)
        canvasCtx.lineTo(0.5, 1.0)
        canvasCtx.stroke()

        var x = 1.0
        var y = hab
        for (c in chars) {
            val txt = "$c"
            val charW = Math.round(canvasCtx.measureText(txt).width)
            val paddedWidth = Math.round(charW + padding * 2)
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
            metrics.width = widthPx * font.sizeUnits / font.sizePts
            metrics.height = heightPx * font.sizeUnits / font.sizePts
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() * font.sizeUnits / font.sizePts
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
