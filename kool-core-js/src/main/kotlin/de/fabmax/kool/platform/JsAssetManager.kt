package de.fabmax.kool.platform

import de.fabmax.kool.AssetManager
import de.fabmax.kool.TextureData
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

class JsAssetManager : AssetManager() {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override fun loadAsset(assetPath: String, onLoad: (ByteArray) -> Unit) {
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

    override fun loadTextureAsset(assetPath: String): TextureData {
        val img = js("new Image();")
        val data = ImageTextureData(img)
        img.crossOrigin = ""
        img.src = assetPath
        return data
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 1024
        private const val MAX_GENERATED_TEX_HEIGHT = 1024
    }
}