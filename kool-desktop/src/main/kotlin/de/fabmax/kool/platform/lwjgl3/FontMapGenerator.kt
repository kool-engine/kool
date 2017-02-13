package de.fabmax.kool.platform.lwjgl3

import de.fabmax.kool.BufferedTexture2d
import de.fabmax.kool.TextureResource
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

class FontMapGenerator {

    companion object {
        val MAXIMUM_TEX_WIDTH = 1024
        val MAXIMUM_TEX_HEIGHT = 1024
    }

    private val canvas = BufferedImage(MAXIMUM_TEX_WIDTH, MAXIMUM_TEX_HEIGHT, BufferedImage.TYPE_INT_ARGB)
    private val clearColor = Color(0, 0, 0, 0)

    fun createCharMap(font: Font, chars: String): CharMap {
        val g = canvas.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // clear canvas
        g.background = clearColor
        g.clearRect(0, 0, MAXIMUM_TEX_WIDTH, MAXIMUM_TEX_HEIGHT)

        var style = java.awt.Font.PLAIN
        if (font.style and Font.BOLD != 0) {
            style = java.awt.Font.BOLD
        }
        if (font.style and Font.ITALIC != 0) {
            style += java.awt.Font.ITALIC
        }
        g.font = java.awt.Font(font.family, style, Math.round(font.size))
        g.color = Color.BLACK

        val metrics = makeMap(chars, font.size, g)

        val props = TextureResource.Props(GL.LINEAR, GL.LINEAR, GL.CLAMP_TO_EDGE, GL.CLAMP_TO_EDGE)

        val format = GL.ALPHA
        val buffer = bufferedImageToBuffer(canvas, format)
        return CharMap(BufferedTexture2d(buffer, MAXIMUM_TEX_WIDTH, MAXIMUM_TEX_HEIGHT, format, props), metrics)
    }

    private fun makeMap(chars: String, size: Float, g: Graphics2D): Map<Char, CharMetrics> {
        val padding = 3
        // line height above baseline
        val hab = Math.round(size * 1.1f)
        // line height below baseline
        val hbb = Math.round(size * 0.5f)
        // overall line height
        val height = Math.round(size * 1.6f)
        val fm = g.fontMetrics

        val map: MutableMap<Char, CharMetrics> = mutableMapOf()

        var x = 0
        var y = hab
        for (c in chars) {
            val charW = fm.charWidth(c)
            val paddedWidth = charW + padding * 2
            if (x + paddedWidth > MAXIMUM_TEX_WIDTH) {
                x = 0
                y += height + 10
                if (y + hbb > MAXIMUM_TEX_HEIGHT) {
                    break
                }
            }

            val metrics = CharMetrics()
            metrics.width = charW.toFloat() //(charW + padding * 2).toFloat()
            metrics.height = height.toFloat()
            metrics.xOffset = 0f //padding.toFloat()
            metrics.yBaseline = hab.toFloat()
            metrics.advance = charW.toFloat()

            metrics.uvMin.set((x + padding).toFloat() / MAXIMUM_TEX_WIDTH, (y.toFloat() - hab.toFloat()) / MAXIMUM_TEX_HEIGHT)
            metrics.uvMax.set((x + padding + metrics.width) / MAXIMUM_TEX_WIDTH,
                    (y.toFloat() - hab.toFloat() + metrics.height) / MAXIMUM_TEX_HEIGHT)
            map.put(c, metrics)

            g.drawString("$c", x + padding, y)
            x += paddedWidth
        }

        return map
    }

}
