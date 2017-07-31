package de.fabmax.kool.platform

import de.fabmax.kool.KoolException
import de.fabmax.kool.Texture
import de.fabmax.kool.TextureData
import de.fabmax.kool.platform.js.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import org.khronos.webgl.WebGLRenderingContext
import org.w3c.dom.HTMLDivElement
import org.w3c.dom.HTMLImageElement
import kotlin.browser.document
import kotlin.js.Date

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */
class PlatformImpl private constructor() : Platform() {

    companion object {
        internal var jsContext: JsContext? = null
        internal val gl: WebGLRenderingContext
            get() = jsContext?.gl ?: throw KoolException("Platform.createContext() not called")
        internal val dpi: Float

        init {
            val measure = document.getElementById("dpiMeasure")
            if (measure == null) {
                println("dpiMeasure element not found, falling back to 96 dpi")
                println("Add this hidden div to your html:")
                println("<div id=\"dpiMeasure\" style=\"height: 1in; width: 1in; left: 100%; position: fixed; top: 100%;\"></div>")
                dpi = 96f
            } else {
                dpi = (measure as HTMLDivElement).offsetWidth.toFloat()
            }
        }

        fun init() {
            if (!isInited) {
                initPlatform(PlatformImpl())
            }
        }

        fun initContext(props: JsContext.InitProps = JsContext.InitProps()): RenderContext {
            init()
            return createContext(props)
        }

        const val MAX_GENERATED_TEX_WIDTH = 1024
        const val MAX_GENERATED_TEX_HEIGHT = 1024
    }

    private val mathImpl = JsMath()
    private var audioImpl: AudioImpl? = null
    private var fontGenerator: FontMapGenerator? = null

    override val supportsMultiContext = false

    override val supportsUint32Indices: Boolean
        get() = jsContext?.supportsUint32Indices ?: throw KoolException("Platform.createContext() not called")

    override fun createContext(props: RenderContext.InitProps): RenderContext {
        var ctx = jsContext
        if (ctx == null) {
            if (props is JsContext.InitProps) {
                ctx = JsContext(props)
                jsContext = ctx
            } else {
                throw IllegalArgumentException("Props must be of JsContext.InitProps")
            }
        }
        return ctx
    }

    override fun getAudioImpl(): Audio {
        if (audioImpl == null) {
            audioImpl = AudioImpl(this)
        }
        return audioImpl!!
    }

    override fun getGlImpl(): GL.Api {
        return WebGlImpl.instance
    }

    override fun getMathImpl(): Math.Api {
        return mathImpl
    }

    override fun createUint8Buffer(capacity: Int): Uint8Buffer {
        return Uint8BufferImpl(capacity)
    }

    override fun createUint16Buffer(capacity: Int): Uint16Buffer {
        return Uint16BufferImpl(capacity)
    }

    override fun createUint32Buffer(capacity: Int): Uint32Buffer {
        return Uint32BufferImpl(capacity)
    }

    override fun createFloat32Buffer(capacity: Int): Float32Buffer {
        return Float32BufferImpl(capacity)
    }

    override fun currentTimeMillis(): Long {
        return Date().getTime().toLong()
    }

    override fun loadTextureAsset(assetPath: String): TextureData {
        val img = js("new Image();")
        val data = ImageTextureData(img)
        img.crossOrigin = ""
        img.src = assetPath
        return data
    }

    override fun loadTextureAssetHttp(url: String, cachePath: String?): TextureData = loadTextureAsset(url)

    override fun createCharMap(fontProps: FontProps): CharMap {
        if (fontGenerator == null) {
            fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)
        }
        return fontGenerator!!.createCharMap(fontProps)
    }

    private class JsMath : Math.Api {
        override fun random() = kotlin.js.Math.random()
        override fun abs(value: Double) = kotlin.js.Math.abs(value)
        override fun acos(value: Double) = kotlin.js.Math.acos(value)
        override fun asin(value: Double) = kotlin.js.Math.asin(value)
        override fun atan(value: Double) = kotlin.js.Math.atan(value)
        override fun atan2(y: Double, x: Double) = kotlin.js.Math.atan2(y, x)
        override fun cos(value: Double) = kotlin.js.Math.cos(value)
        override fun cosh(value: Double) = js("Math.cosh(value)")
        override fun sin(value: Double) = kotlin.js.Math.sin(value)
        override fun sinh(value: Double) = js("Math.sinh(value)")
        override fun exp(value: Double) = kotlin.js.Math.exp(value)
        override fun max(a: Int, b: Int) = kotlin.js.Math.max(a, b)
        override fun max(a: Float, b: Float) = kotlin.js.Math.max(a, b)
        override fun max(a: Double, b: Double) = kotlin.js.Math.max(a, b)
        override fun min(a: Int, b: Int) = kotlin.js.Math.min(a, b)
        override fun min(a: Float, b: Float) = kotlin.js.Math.min(a, b)
        override fun min(a: Double, b: Double) = kotlin.js.Math.min(a, b)
        override fun sqrt(value: Double) = kotlin.js.Math.sqrt(value)
        override fun tan(value: Double) = kotlin.js.Math.tan(value)
        override fun tanh(value: Double) = js("Math.tanh(value)")
        override fun log(value: Double) = kotlin.js.Math.log(value)
        override fun pow(base: Double, exp: Double) = kotlin.js.Math.pow(base, exp)
        override fun round(value: Double): Int = kotlin.js.Math.round(value)
        override fun round(value: Float): Int = kotlin.js.Math.round(value)
        override fun floor(value: Double): Int = kotlin.js.Math.floor(value)
        override fun floor(value: Float): Int = kotlin.js.Math.floor(value.toDouble())
        override fun ceil(value: Double): Int = kotlin.js.Math.ceil(value)
        override fun ceil(value: Float): Int = kotlin.js.Math.ceil(value.toDouble())
    }
}

class ImageTextureData(val image: HTMLImageElement) : TextureData() {
    override var isAvailable: Boolean
        get() = image.complete
        set(value) {}

    override fun onLoad(texture: Texture, ctx: RenderContext) {
        // fixme: is there a way to find out if the image has an alpha channel and set the GL format accordingly?
        PlatformImpl.gl.texImage2D(GL.TEXTURE_2D, 0, GL.RGBA, GL.RGBA, GL.UNSIGNED_BYTE, image)
        width = image.width
        height = image.height
        val size = width * height * 4
        ctx.memoryMgr.memoryAllocated(texture.res!!, size)
    }
}
