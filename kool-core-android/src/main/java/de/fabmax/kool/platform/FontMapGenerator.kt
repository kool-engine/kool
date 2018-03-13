package de.fabmax.kool.platform

import android.content.Context
import android.graphics.*
import android.graphics.Color
import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.gl.GL_ALPHA
import de.fabmax.kool.util.*
import java.nio.ByteBuffer


/**
 * Generates font map textures for arbitrary fonts.
 */
internal class FontMapGenerator(val appCtx: Context, val maxWidth: Int, val maxHeight: Int) {

    private val loadedFonts = mutableMapOf<String, Typeface>()
    private val bitmap = Bitmap.createBitmap(maxWidth, maxHeight, Bitmap.Config.ALPHA_8)
    private val outputBuf = ByteBuffer.allocate(maxWidth * maxHeight)
    private val canvas = Canvas(bitmap)

    fun createCharMap(fontProps: FontProps): CharMap {
        val charMetrics = mutableMapOf<Char, CharMetrics>()
        val texH = renderFontImage(fontProps, charMetrics)

        outputBuf.clear()
        bitmap.copyPixelsToBuffer(outputBuf)
        outputBuf.flip()

        val texBuf = createUint8Buffer(maxWidth * texH) as Uint8BufferImpl
        outputBuf.limit(maxWidth * texH)
        texBuf.buffer.put(outputBuf)

        val texData = BufferedTextureData(texBuf, maxWidth, texH, GL_ALPHA)
        return CharMap(texData, charMetrics, fontProps)
    }

    private fun loadFromAssets(family: String): Typeface? {
        if (loadedFonts.containsKey(family)) {
            return loadedFonts[family]
        }

        return try {
            val tf = Typeface.createFromAsset(appCtx.assets, family)
            loadedFonts[family] = tf
            tf

        } catch (e: Exception) {
            null
        }
    }

    private fun getTypeface(fontProps: FontProps): Typeface {
        var typeface = Typeface.DEFAULT
        val fams = fontProps.family.split(",")
        for (fam in fams) {
            val f = fam.trim().replace("\"", "")
            if (f == "sans-serif") {
                typeface = Typeface.SANS_SERIF
                break
            } else if (f == "monospaced") {
                typeface = Typeface.MONOSPACE
                break
            } else {
                val tf = loadFromAssets(f)
                if (tf != null) {
                    typeface = tf
                    break
                }
            }
        }

        return if (fontProps.style == Font.PLAIN) {
            typeface
        } else {
            val style = when(fontProps.style) {
                Font.BOLD -> Typeface.BOLD
                Font.ITALIC -> Typeface.ITALIC
                Font.BOLD and Font.ITALIC -> Typeface.BOLD_ITALIC
                else -> Typeface.NORMAL
            }
            Typeface.create(typeface, style)
        }
    }

    private fun renderFontImage(fontProps: FontProps, map: MutableMap<Char, CharMetrics>): Int {
        val paint = Paint()
        paint.typeface = getTypeface(fontProps)
        paint.textSize = fontProps.sizePts
        paint.flags = Paint.ANTI_ALIAS_FLAG or Paint.LINEAR_TEXT_FLAG
        paint.style = Paint.Style.FILL
        paint.color = Color.WHITE

        // clear canvas
        paint.xfermode = CLEAR
        canvas.drawRect(0f, 0f, maxWidth.toFloat(), maxHeight.toFloat(), paint)
        paint.xfermode = SRC_OVER

        // upper left pixel needs to be opaque
        canvas.drawLine(0f, 0f, 0f, 1f, paint)

        val padding = 3
        // line height above baseline
        val hab = Math.round(fontProps.sizePts * 1.1f)
        // line height below baseline
        val hbb = Math.round(fontProps.sizePts * 0.5f)
        // overall line height
        val height = Math.round(fontProps.sizePts * 1.6f)

        val width = FloatArray(1)
        val str = CharArray(1)
        val r = Rect()

        var x = 1
        var y = hab
        for (c in fontProps.chars) {
            // super-ugly special treatment for 'j' which has a negative x-offset for most fonts
            if (c == 'j') {
                x += Math.round(fontProps.sizePts * 0.1f)
            }

            str[0] = c
            paint.getTextBounds(str, 0, 1, r)
            paint.getTextWidths(str, 0, 1, width)

            val charW = width[0]
            val paddedWidth = charW + padding * 2
            if (x + paddedWidth > maxWidth) {
                x = 0
                y += height + 10
                if (y + hbb > maxHeight) {
                    break
                }
            }

            val heightPx = height.toFloat()
            val metrics = CharMetrics()
            metrics.width = charW * fontProps.sizeUnits / fontProps.sizePts
            metrics.height = heightPx * fontProps.sizeUnits / fontProps.sizePts
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() * fontProps.sizeUnits / fontProps.sizePts
            metrics.advance = metrics.width

            metrics.uvMin.set((x + padding).toFloat(), (y - hab).toFloat())
            metrics.uvMax.set((x + padding + charW), (y - hab).toFloat() + heightPx)
            map[c] = metrics

            //g.drawString("$c", x + padding, y)
            canvas.drawText(str, 0, 1, (x + padding).toFloat(), y.toFloat(), paint)
            x += paddedWidth.toInt()
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

    companion object {
        private val CLEAR = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
        private val SRC_OVER = PorterDuffXfermode(PorterDuff.Mode.SRC_OVER)
    }
}
