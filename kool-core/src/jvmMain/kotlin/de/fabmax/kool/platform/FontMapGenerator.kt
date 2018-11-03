package de.fabmax.kool.platform

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.gl.GL_ALPHA
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Font
import de.fabmax.kool.util.FontProps
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage

/**
 * @author fabmax
 */

internal class FontMapGenerator(val maxWidth: Int, val maxHeight: Int) {

    private val canvas = BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB)
    private val clearColor = Color(0, 0, 0, 0)

    private val availableFamilies: Set<String>

    init {
        val families: MutableSet<String> = mutableSetOf()
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        for (family in ge.availableFontFamilyNames) {
            families.add(family)
        }
        availableFamilies = families
    }

    fun createCharMap(fontProps: FontProps): CharMap {
        val g = canvas.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // clear canvas
        g.background = clearColor
        g.clearRect(0, 0, maxWidth, maxHeight)

        var style = java.awt.Font.PLAIN
        if (fontProps.style and Font.BOLD != 0) {
            style = java.awt.Font.BOLD
        }
        if (fontProps.style and Font.ITALIC != 0) {
            style += java.awt.Font.ITALIC
        }

        var family = java.awt.Font.SANS_SERIF
        val fams = fontProps.family.split(",")
        for (fam in fams) {
            val f = fam.trim().replace("\"", "")
            if (f == "sans-serif") {
                family = java.awt.Font.SANS_SERIF
                break
            } else if (f == "monospaced") {
                family = java.awt.Font.MONOSPACED
                break
            } else if (f in availableFamilies) {
                family = f
                break
            }
        }

        g.font = java.awt.Font(family, style, Math.round(fontProps.sizePts))
        g.color = Color.BLACK

        val metrics: MutableMap<Char, CharMetrics> = mutableMapOf()
        val texHeight = makeMap(fontProps, g, metrics)
        val buffer = bufferedImageToBuffer(canvas, GL_ALPHA, maxWidth, texHeight)
        return CharMap(BufferedTextureData(buffer, maxWidth, texHeight, GL_ALPHA), metrics, fontProps)
    }

    private fun makeMap(fontProps: FontProps, g: Graphics2D, map: MutableMap<Char, CharMetrics>): Int {
        val padding = 3
        // line height above baseline
        val hab = Math.round(fontProps.sizePts * 1.1f)
        // line height below baseline
        val hbb = Math.round(fontProps.sizePts * 0.5f)
        // overall line height
        val height = Math.round(fontProps.sizePts * 1.6f)
        val fm = g.fontMetrics

        // first pixel is opaque
        g.drawLine(0, 0, 0, 1)

        var x = 1
        var y = hab
        for (c in fontProps.chars) {
            // super-ugly special treatment for 'j' which has a negative x-offset for most fonts
            if (c == 'j') {
                x += Math.round(fontProps.sizePts * 0.1f)
            }

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
            metrics.width = widthPx * fontProps.sizeUnits / fontProps.sizePts
            metrics.height = heightPx * fontProps.sizeUnits / fontProps.sizePts
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() * fontProps.sizeUnits / fontProps.sizePts
            metrics.advance = metrics.width

            metrics.uvMin.set((x + padding).toFloat(), (y - hab).toFloat())
            metrics.uvMax.set((x + padding + widthPx), (y - hab).toFloat() + heightPx)
            map[c] = metrics

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
