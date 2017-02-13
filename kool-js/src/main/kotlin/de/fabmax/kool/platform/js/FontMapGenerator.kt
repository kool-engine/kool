package de.fabmax.kool.platform.js

import de.fabmax.kool.BufferedTexture2d
import de.fabmax.kool.TextureResource
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.Platform
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Font
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.browser.document

/**
 * @author fabmax
 */

class FontMapGenerator {

    companion object {
        val MAXIMUM_TEX_WIDTH = 1024
        val MAXIMUM_TEX_HEIGHT = 1024
    }

    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    init {
        canvas.width = MAXIMUM_TEX_WIDTH
        canvas.height = MAXIMUM_TEX_HEIGHT
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D
    }

    fun createCharMap(font: Font, chars: String): CharMap {
        // clear canvas
        canvasCtx.fillStyle = "transparent"
        canvasCtx.fillRect(0.0, 0.0, MAXIMUM_TEX_WIDTH.toDouble(), MAXIMUM_TEX_HEIGHT.toDouble())

        var style = ""
        if (font.style and Font.BOLD != 0) {
            style = "bold "
        }
        if (font.style and Font.ITALIC != 0) {
            style += "italic "
        }

        val metrics = makeMap(chars, font.family, font.size, style)

        val props = TextureResource.Props(GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)
        val data = canvasCtx.getImageData(0.0, 0.0, MAXIMUM_TEX_WIDTH.toDouble(), MAXIMUM_TEX_HEIGHT.toDouble())

        // alpha texture
        val buffer = Platform.createUint8Buffer(MAXIMUM_TEX_WIDTH * MAXIMUM_TEX_HEIGHT)
        for (i in 0..buffer.capacity-1) {
            buffer.put(data.data[i*4+3])
        }
        return CharMap(BufferedTexture2d(buffer, MAXIMUM_TEX_WIDTH, MAXIMUM_TEX_HEIGHT, GL.ALPHA, props), metrics)
    }

    private fun makeMap(chars: String, family: String, size: Float, style: String): Map<Char, CharMetrics> {
        canvasCtx.font = "$style${size}px \"$family\""
        canvasCtx.fillStyle = "#ffffff"
//        canvasCtx.strokeStyle = "#ff0000"

        val padding = 3.0
        // line height above baseline
        val hab = Math.round(size * 1.1).toDouble()
        // line height below baseline
        val hbb = Math.round(size * 0.5).toDouble()
        // overall line height
        val height = Math.round(size * 1.6).toDouble()

        val map: MutableMap<Char, CharMetrics> = mutableMapOf()

        var x = 0.0
        var y = hab
        for (c in chars) {
            val txt = "$c"
            val charW = canvasCtx.measureText(txt).width
            val paddedWidth = Math.round(charW + padding * 2)
            if (x + paddedWidth > MAXIMUM_TEX_WIDTH) {
                x = 0.0
                y += height + 10
                if (y + hbb > MAXIMUM_TEX_HEIGHT) {
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

            val metrics = CharMetrics()
            metrics.width = charW.toFloat() //(charW + padding * 2).toFloat()
            metrics.height = height.toFloat()
            metrics.xOffset = 0f //padding.toFloat()
            metrics.yBaseline = hab.toFloat()
            metrics.advance = charW.toFloat()

            metrics.uvMin.set((x + padding).toFloat() / MAXIMUM_TEX_WIDTH, (y.toFloat() - hab.toFloat()) / MAXIMUM_TEX_HEIGHT)
            metrics.uvMax.set((x + padding + metrics.width).toFloat() / MAXIMUM_TEX_WIDTH,
                    (y.toFloat() - hab.toFloat() + metrics.height) / MAXIMUM_TEX_HEIGHT)
            map.put(c, metrics)

            canvasCtx.fillText(txt, x + padding, y)
            x += paddedWidth
        }

        return map
    }

}
