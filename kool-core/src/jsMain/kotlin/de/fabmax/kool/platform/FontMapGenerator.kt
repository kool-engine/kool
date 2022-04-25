package de.fabmax.kool.platform

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.scene.ui.CharMap
import de.fabmax.kool.scene.ui.CharMetrics
import de.fabmax.kool.scene.ui.Font
import de.fabmax.kool.scene.ui.FontProps
import de.fabmax.kool.util.createUint8Buffer
import de.fabmax.kool.util.logD
import kotlinx.browser.document
import org.khronos.webgl.get
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise
import kotlin.math.ceil
import kotlin.math.round

/**
 * @author fabmax
 */

class FontMapGenerator(val maxWidth: Int, val maxHeight: Int, props: JsContext.InitProps, assetManager: JsAssetManager, val ctx: KoolContext) {

    private val canvas = document.createElement("canvas") as HTMLCanvasElement
    private val canvasCtx: CanvasRenderingContext2D

    private val charMaps = mutableMapOf<FontProps, CharMap>()

    val loadingFonts = mutableListOf<Promise<FontFace>>()

    init {
        canvas.width = maxWidth
        canvas.height = maxHeight
        canvasCtx = canvas.getContext("2d") as CanvasRenderingContext2D

        props.customFonts.forEach { (family, url) ->
            loadFont(family, assetManager.makeAssetRef(url).url)
        }

        ctx.onWindowScaleChanged += {
            charMaps.values.forEach {
                if (it.fontProps.isScaledByWindowScale) {
                    updateCharMap(it, 0f)
                }
            }
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

    fun getCharMap(fontProps: FontProps, fontScale: Float): CharMap = charMaps.getOrPut(fontProps) { updateCharMap(CharMap(fontProps), fontScale) }

    fun updateCharMap(charMap: CharMap, fontScale: Float): CharMap {
        val fontProps = charMap.fontProps

        val scale = when {
            fontProps.isScaledByWindowScale && fontScale == 0f -> ctx.windowScale
            fontProps.isScaledByWindowScale && fontScale != 0f -> ctx.windowScale * fontScale
            fontScale != 0f -> fontScale
            else -> 1f
        }

        // clear canvas
        canvasCtx.fillStyle = "#ffffff"
        canvasCtx.fillRect(0.0, 0.0, maxWidth.toDouble(), maxHeight.toDouble())
        // draw font chars
        val texHeight = makeMap(fontProps, scale, charMap)
        // copy image data
        val data = canvasCtx.getImageData(0.0, 0.0, maxWidth.toDouble(), texHeight.toDouble())

        // alpha texture
        val buffer = createUint8Buffer(maxWidth * texHeight)
        for (i in 0 until buffer.capacity) {
            buffer.put((255 - (data.data[i*4].toInt() and 0xff)).toByte())
        }
        charMap.textureData = TextureData2d(buffer, maxWidth, texHeight, TexFormat.R)
        charMap.applyScale(scale)
        return charMap
    }

    private fun makeMap(fontProps: FontProps, fontScale: Float, map: MutableMap<Char, CharMetrics>): Int {
        var style = ""
        if (fontProps.style and Font.BOLD != 0) {
            style = "bold "
        }
        if (fontProps.style and Font.ITALIC != 0) {
            style += "italic "
        }

        val fontSize = round(fontProps.sizePts * fontScale)
        val fontStr = "$style ${fontSize}px ${fontProps.family}"
        canvasCtx.font = fontStr
        canvasCtx.fillStyle = "#000000"
        canvasCtx.strokeStyle = "#000000"

        logD { "generate font: $fontStr" }

        // line height above baseline
        val hab = round(fontSize * 1.1).toInt()
        // line height below baseline
        val hbb = round(fontSize * 0.5)
        // overall line height
        val height = round(fontSize * 1.6)

        // first pixel is opaque
        canvasCtx.beginPath()
        canvasCtx.moveTo(0.5, 0.0)
        canvasCtx.lineTo(0.5, 1.0)
        canvasCtx.stroke()

        // enforce constant width for numeric char (0..9)
        val numericChars = setOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9')
        val numericWidth = numericChars.maxOf { c ->
            val txtMetrics = canvasCtx.measureText("$c")
            ceil(txtMetrics.actualBoundingBoxRight + txtMetrics.actualBoundingBoxLeft).toFloat()
        }

        var x = 1
        var y = hab
        for (c in fontProps.chars) {
            val txt = "$c"
            val txtMetrics = canvasCtx.measureText(txt)
            val charW = if (c in numericChars) numericWidth else ceil(txtMetrics.actualBoundingBoxRight + txtMetrics.actualBoundingBoxLeft).toFloat()

            if (x + charW > maxWidth) {
                x = 0
                y += (height + 10).toInt()
                if (y + hbb > maxHeight) {
                    break
                }
            }

            val heightPx = height.toFloat()
            val metrics = CharMetrics()
            metrics.width = charW
            metrics.height = heightPx
            metrics.xOffset = txtMetrics.actualBoundingBoxLeft.toFloat()
            metrics.yBaseline = hab.toFloat()
            metrics.advance = txtMetrics.width.toFloat()

            metrics.uvMin.set(x.toFloat(), (y - hab).toFloat())
            metrics.uvMax.set(x + charW, (y - hab).toFloat() + heightPx)
            map[c] = metrics

            canvasCtx.fillText(txt, x + metrics.xOffset.toDouble(), y.toDouble())
            x += metrics.width.toInt() + 1
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

external class FontFace(family: String, source: String) {
    val family: String

    fun load(): Promise<FontFace>
}
