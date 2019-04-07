package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import kotlinx.coroutines.CompletableDeferred
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.w3c.dom.HTMLImageElement
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.browser.document

class JsAssetManager internal constructor(assetsBaseDir: String) : AssetManager(assetsBaseDir) {

    private val pako = js("require('pako_inflate.min');")
    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override suspend fun loadLocalRaw(localRawRef: LocalRawAssetRef) = LoadedRawAsset(localRawRef, loadRaw(localRawRef.url))

    override suspend fun loadHttpRaw(httpRawRef: HttpRawAssetRef) = LoadedRawAsset(httpRawRef, loadRaw(httpRawRef.url))

    override suspend fun loadHttpTexture(httpTextureRef: HttpTextureAssetRef) = LoadedTextureAsset(httpTextureRef, loadImage(httpTextureRef.url))

    override suspend fun loadLocalTexture(localTextureRef: LocalTextureAssetRef) = LoadedTextureAsset(localTextureRef, loadImage(localTextureRef.url))

    private suspend fun loadRaw(url: String): ByteArray? {
        val data = CompletableDeferred<ByteArray?>(job)
        val req = XMLHttpRequest()
        req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
        req.onload = {
            val array = Uint8Array(req.response as ArrayBuffer)
            val bytes = ByteArray(array.length)
            for (i in 0 until array.length) {
                bytes[i] = array[i]
            }
            data.complete(bytes)
        }
        req.onerror = {
            data.complete(null)
            logE { "Failed loading resource $url: $it" }
        }
        req.open("GET", url)
        req.send()

        return data.await()
    }

    private fun loadImage(url: String): ImageTextureData {
        val img = document.createElement("img") as HTMLImageElement
        val data = ImageTextureData(img)
        img.crossOrigin = ""
        js("if ('decoding' in img) { img.decoding = 'async'; }")
        img.src = url
        return data
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.createCharMap(fontProps)

    override fun inflate(zipData: ByteArray): ByteArray {
        val uint8Data = Uint8Array(zipData.size)
        for (i in zipData.indices) {
            uint8Data[i] = zipData[i]
        }
        val inflated = pako.inflate(uint8Data) as Uint8Array
        return ByteArray(inflated.length) { inflated[it] }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}