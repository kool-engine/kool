package de.fabmax.kool.platform.lwjgl3

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.TextureProps
import de.fabmax.kool.platform.GL
import de.fabmax.kool.platform.bufferedImageToBuffer
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Font
import java.awt.Color
import java.awt.Graphics2D
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * @author fabmax
 */

class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB)
    private val clearColor = Color(0, 0, 0, 0)

    fun createCharMap(font: Font, chars: String): CharMap {
        val g = canvas.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // clear canvas
        g.background = clearColor
        g.clearRect(0, 0, maxWidth, maxHeight)

        var style = java.awt.Font.PLAIN
        if (font.style and Font.BOLD != 0) {
            style = java.awt.Font.BOLD
        }
        if (font.style and Font.ITALIC != 0) {
            style += java.awt.Font.ITALIC
        }
        g.font = java.awt.Font(font.family, style, Math.round(font.sizePts))
        g.color = Color.BLACK

        val metrics: MutableMap<Char, CharMetrics> = mutableMapOf()
        val texHeight = makeMap(chars, font, g, metrics)

        val format = GL.ALPHA
        val buffer = bufferedImageToBuffer(canvas, format, maxWidth, texHeight)
        return CharMap(BufferedTextureData(buffer, maxWidth, texHeight, format), metrics)
    }

    private fun makeMap(chars: String, font: Font, g: Graphics2D, map: MutableMap<Char, CharMetrics>): Int {
        val padding = 3
        // line height above baseline
        val hab = Math.round(font.sizePts * 1.1f)
        // line height below baseline
        val hbb = Math.round(font.sizePts * 0.5f)
        // overall line height
        val height = Math.round(font.sizePts * 1.6f)
        val fm = g.fontMetrics

        var x = 0
        var y = hab
        for (c in chars) {
            val charW = fm.charWidth(c)
            val paddedWidth = charW + padding * 2
            if (x + paddedWidth > maxWidth) {
                x = 0
                y += height + 10
                if (y + hbb > maxHeight) {
                    break
                }
            }

            val widthPx = charW.toFloat()
            val heightPx = height.toFloat()
            val metrics = CharMetrics()
            metrics.width = widthPx * font.sizeUnits / font.sizePts
            metrics.height = heightPx * font.sizeUnits / font.sizePts
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() * font.sizeUnits / font.sizePts
            metrics.advance = metrics.width

            metrics.uvMin.set((x + padding).toFloat(), (y - hab).toFloat())
            metrics.uvMax.set((x + padding + widthPx), (y - hab).toFloat() + heightPx)
            map.put(c, metrics)

            g.drawString("$c", x + padding, y)
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

    private fun nextPow2(value: Int): Int {
        var pow2 = 16
        while (pow2 < value && pow2 < maxHeight) {
            pow2 = pow2 shl 1
        }
        return pow2
    }
}
