package de.fabmax.kool.platform

import de.fabmax.kool.AssetManager
import de.fabmax.kool.TextureData
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.dom.HTMLImageElement
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.browser.document

class JsAssetManager internal constructor(assetsBaseDir: String) : AssetManager(assetsBaseDir) {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override fun loadHttpAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) {
        val req = XMLHttpRequest()
        req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
        req.onload = {
            val array = Uint8Array(req.response as ArrayBuffer)
            val bytes = ByteArray(array.length)
            for (i in 0 until array.length) {
                bytes[i] = array[i]
            }
            onLoad(bytes)
        }
        req.onerror = {
            onLoad(null)
            logE { "Failed loading resource $assetPath: $it" }
        }
        req.open("GET", assetPath)
        req.send()
    }

    override fun loadHttpTexture(assetPath: String): TextureData {
        val img = document.createElement("img") as HTMLImageElement
        val data = ImageTextureData(img)
        img.crossOrigin = ""
        img.src = assetPath
        return data
    }

    // in js everything is http...
    override fun loadLocalAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) = loadHttpAsset(assetPath, onLoad)

    override fun loadLocalTexture(assetPath: String): TextureData = loadHttpTexture(assetPath)

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 1024
        private const val MAX_GENERATED_TEX_HEIGHT = 1024
    }
}