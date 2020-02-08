package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.webgl.TextureLoader
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.khronos.webgl.get
import org.khronos.webgl.set
import org.w3c.dom.Image
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType

class JsAssetManager internal constructor(assetsBaseDir: String, val ctx: JsContext) : AssetManager(assetsBaseDir) {

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

    private suspend fun loadImage(url: String): ImageTextureData {
        val deferred = CompletableDeferred<Image>()
        val img = Image()
        img.onload = {
            deferred.complete(img)
        }
        img.onerror = { _, _, _, _, _ ->
            deferred.completeExceptionally(KoolException("Failed loading tex from $url"))
        }
        img.crossOrigin = ""
        js("if ('decoding' in img) { img.decoding = 'async'; }")
        img.src = url
        return ImageTextureData(deferred.await())
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JsAssetManager) }
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.getCharMap(fontProps)

    override fun inflate(zipData: ByteArray): ByteArray {
        val uint8Data = Uint8Array(zipData.size)
        for (i in zipData.indices) {
            uint8Data[i] = zipData[i]
        }
        val inflated = pako.inflate(uint8Data) as Uint8Array
        return ByteArray(inflated.length) { inflated[it] }
    }

    override fun loadAndPrepareTexture(assetPath: String, props: TextureProps, recv: (Texture) -> Unit) {
        val tex = Texture(props) { it.loadTextureData(assetPath) }
        launch {
            val data = loadTextureData(assetPath)
            tex.loadedTexture = TextureLoader.loadTexture(ctx, props, data)
            tex.loadingState = Texture.LoadingState.LOADED
            recv(tex)
        }
    }

    override fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                       props: TextureProps, recv: (CubeMapTexture) -> Unit) {
        val tex = CubeMapTexture(props) { it.loadCubeMapTextureData(ft, bk, lt, rt, up, dn) }
        launch {
            val data = loadCubeMapTextureData(ft, bk, lt, rt, up, dn)
            tex.loadedTexture = TextureLoader.loadTexture(ctx, props, data)
            tex.loadingState = Texture.LoadingState.LOADED
            recv(tex)
        }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}