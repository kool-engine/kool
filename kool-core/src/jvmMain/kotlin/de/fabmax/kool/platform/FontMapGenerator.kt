package de.fabmax.kool.platform

import de.fabmax.kool.math.clamp
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.scene.ui.CharMap
import de.fabmax.kool.scene.ui.CharMetrics
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.runBlocking
import java.awt.Color
import java.awt.Graphics2D
import java.awt.GraphicsEnvironment
import java.awt.RenderingHints
import java.awt.image.BufferedImage
import java.awt.image.DataBufferInt
import java.io.ByteArrayInputStream
import java.io.IOException
import kotlin.math.roundToInt

/**
 * @author fabmax
 */

private typealias AwtFont = java.awt.Font

internal class FontMapGenerator(val maxWidth: Int, val maxHeight: Int, val ctx: Lwjgl3Context) {

    private val canvas = BufferedImage(maxWidth, maxHeight, BufferedImage.TYPE_INT_ARGB)
    private val clearColor = Color(0, 0, 0, 0)

    private val charMaps = mutableMapOf<FontProps, CharMap>()

    private val availableFamilies: Set<String>
    private val customFonts = mutableMapOf<String, AwtFont>()

    init {
        val families: MutableSet<String> = mutableSetOf()
        val ge = GraphicsEnvironment.getLocalGraphicsEnvironment()
        for (family in ge.availableFontFamilyNames) {
            families.add(family)
        }
        availableFamilies = families

        ctx.onWindowScaleChanged += {
            charMaps.values.forEach {
                if (it.fontProps.isScaledByWindowScale) {
                    updateCharMap(it, 0f)
                }
            }
        }
    }

    internal fun loadCustomFonts(props: Lwjgl3Context.InitProps, assetMgr: JvmAssetManager) {
        props.customFonts.forEach { (family, path) ->
            try {
                val inStream = runBlocking {
                    ByteArrayInputStream(assetMgr.loadAsset(path)!!.toArray())
                }
                val ttfFont = java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inStream)
                customFonts[family] = ttfFont
                logD { "Loaded custom font: $family" }
            } catch (e: IOException) {
                logE { "Failed loading font $family: $e" }
                e.printStackTrace()
            }
        }
    }

    fun getCharMap(fontProps: FontProps, fontScale: Float): CharMap = charMaps.computeIfAbsent(fontProps) { updateCharMap(CharMap(it), fontScale) }

    fun updateCharMap(charMap: CharMap, fontScale: Float): CharMap {
        val fontProps = charMap.fontProps
        val g = canvas.graphics as Graphics2D
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON)

        // clear canvas
        g.background = clearColor
        g.clearRect(0, 0, maxWidth, maxHeight)

        var style = AwtFont.PLAIN
        if (fontProps.style and Font.BOLD != 0) {
            style = AwtFont.BOLD
        }
        if (fontProps.style and Font.ITALIC != 0) {
            style += AwtFont.ITALIC
        }

        var customFont: AwtFont? = null
        var family = AwtFont.SANS_SERIF
        val fams = fontProps.family.split(",")
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

        val scale = when {
            fontProps.isScaledByWindowScale && fontScale == 0f -> ctx.windowScale
            fontProps.isScaledByWindowScale && fontScale != 0f -> ctx.windowScale * fontScale
            fontScale != 0f -> fontScale
            else -> 1f
        }

        val size = fontProps.sizePts * scale * fontProps.sampleScale
        val font: AwtFont = customFont?.deriveFont(fontProps.style, size) ?: AwtFont(family, style, size.roundToInt())

        g.font = font
        g.color = Color.BLACK

        val texHeight = makeMap(fontProps, size, g, charMap)
        val buffer = getCanvasAlphaData(maxWidth, texHeight)
        val texData = TextureData2d(buffer, maxWidth, texHeight, TexFormat.R)
        charMap.textureData = texData
        charMap.applyScale(scale)

        //logD { "Updated char map: ${charMap.fontProps}, scaled size: $size" }
        logD { "Generated font map for ($font, scale=${scale}x${fontProps.sampleScale})" }
        //ImageIO.write(canvas, "png", File("${font.family}-${font.size}.png"))

        return charMap
    }

    private fun getCanvasAlphaData(width: Int, height: Int): Uint8Buffer {
        val imgBuf = canvas.data.dataBuffer as DataBufferInt
        val pixels = imgBuf.bankData[0]
        val buffer = createUint8Buffer(width * height)
        for (i in 0 until width * height) {
            buffer.put((pixels[i] shr 24).toByte())
        }
        buffer.flip()
        return buffer
    }

    private fun makeMap(fontProps: FontProps, size: Float, g: Graphics2D, map: MutableMap<Char, CharMetrics>): Int {
        val padding = (if (fontProps.style == Font.ITALIC) 3 else 6) * (size / 30f).clamp(1f, 3f).roundToInt()
        // line height above baseline
        val hab = (size * 1.1f).roundToInt()
        // line height below baseline
        val hbb = (size * 0.5f).roundToInt()
        // overall line height
        val height = (size * 1.6f).roundToInt()
        val fm = g.fontMetrics

        // first pixel is opaque
        g.fillRect(0, 0, 1, 1)

        var x = 1
        var y = hab
        for (c in fontProps.chars) {
            // super-ugly special treatment for 'j' which has a negative x-offset for most fonts
            if (c == 'j') {
                x += (size * 0.1f).roundToInt()
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
            metrics.width = widthPx / fontProps.sampleScale
            metrics.height = heightPx / fontProps.sampleScale
            metrics.xOffset = 0f
            metrics.yBaseline = hab.toFloat() / fontProps.sampleScale
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
