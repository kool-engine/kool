package de.fabmax.kool

import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.shading.GlslGenerator
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.WebGLRenderingContext
import org.khronos.webgl.get
import org.w3c.dom.HTMLDivElement
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.browser.document
import kotlin.browser.window
import kotlin.js.Date

/**
 * Javascript / WebGL platform implementation
 *
 * @author fabmax
 */

actual val supportsMultiContext: Boolean = false

actual val supportsUint32Indices: Boolean = true

fun createContext() = createContext(JsContext.InitProps())

actual fun createContext(props: RenderContext.InitProps): RenderContext = JsImpl.createContext(props)

actual fun createCharMap(fontProps: FontProps): CharMap = JsImpl.fontGenerator.createCharMap(fontProps)

actual fun currentTimeMillis(): Long = Date().getTime().toLong()

actual fun defaultGlslInjector(): GlslGenerator.GlslInjector = JsImpl.defaultGlslInjector

actual fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
    val req = XMLHttpRequest()
    req.open("GET", assetPath)
    req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
    req.onload = { evt ->
        val array = Uint8Array(req.response as ArrayBuffer)
        val bytes = ByteArray(array.length)
        for (i in 0 until array.length) {
            bytes[i] = array[i]
        }
        onLoad(bytes)
    }
    req.send()
}

actual fun loadTextureAsset(assetPath: String): TextureData {
    val img = js("new Image();")
    val data = ImageTextureData(img)
    img.crossOrigin = ""
    img.src = assetPath
    return data
}

actual fun openUrl(url: String) {
    window.open(url)
}

internal object JsImpl {
    private const val MAX_GENERATED_TEX_WIDTH = 1024
    private const val MAX_GENERATED_TEX_HEIGHT = 1024

    val dpi: Float
    var ctx: JsContext? = null
    val gl: WebGLRenderingContext
        get() = ctx?.gl ?: throw KoolException("Platform.createContext() not called")
    val fontGenerator: FontMapGenerator by lazy { FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT) }
    val defaultGlslInjector = object : GlslGenerator.GlslInjector {
        override fun vsHeader(text: StringBuilder) {
            text.append("#version 100\n")
        }

        override fun fsHeader(text: StringBuilder) {
            text.append("#version 100\n")
        }
    }

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

    fun createContext(props: RenderContext.InitProps): RenderContext {
        if (ctx != null) {
            throw KoolException("Context was already creates (multi-context is currently not supported in js")
        }
        if (props is JsContext.InitProps) {
            ctx = JsContext(props)
            return ctx!!
        } else {
            throw IllegalArgumentException("Props must be of JsContext.InitProps")
        }
    }
}

/*class PlatformImpl private constructor() : Platform() {

    companion object {
        internal var jsContext: JsContext? = null
        internal val gl: WebGLRenderingContext
            get() = jsContext?.gl ?: throw KoolException("Platform.createContext() not called")

    }

    private var audioImpl: AudioGenerator? = null
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
            audioImpl = AudioGenerator(this)
        }
        return audioImpl!!
    }

}*/
