package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.webgl.TextureLoader
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.dom.*
import org.w3c.dom.events.Event
import org.w3c.files.FileList
import org.w3c.files.get
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise

class JsAssetManager internal constructor(assetsBaseDir: String, val ctx: JsContext) : AssetManager(assetsBaseDir) {

    private val pako = js("require('pako')")
    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    private var fileLoadDeferred: CompletableDeferred<Uint8Buffer?>? = null
    private val onFileSelectionChanged: (Event) -> Unit = { loadSelectedFile() }
    private val fileChooser: Element = document.createElement("input").apply {
        setAttribute("type", "file")
        addEventListener("change", onFileSelectionChanged)
        asDynamic().style.display = "none"
        document.body?.appendChild(this)
    }

    override suspend fun loadRaw(rawRef: RawAssetRef) = LoadedRawAsset(rawRef, loadRaw(rawRef.url))

    override suspend fun loadTexture(textureRef: TextureAssetRef) =
            LoadedTextureAsset(textureRef, loadImage(textureRef))

    private suspend fun loadRaw(url: String): Uint8Buffer? {
        val data = CompletableDeferred<Uint8Buffer?>(job)
        val req = XMLHttpRequest()
        req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
        req.onload = {
            val array = Uint8Array(req.response as ArrayBuffer)
            data.complete(Uint8BufferImpl(array))
        }
        req.onerror = {
            data.complete(null)
            logE { "Failed loading resource $url: $it" }
        }
        req.open("GET", url)
        req.send()

        return data.await()
    }

    private suspend fun loadImage(ref: TextureAssetRef): TextureData {
        val deferred = CompletableDeferred<Image>()

        val img = Image()
        img.onload = {
            deferred.complete(img)
        }
        img.onerror = { _, _, _, _, _ ->
            if (ref.url.startsWith("data:")) {
                deferred.completeExceptionally(KoolException("Failed loading tex from data URL"))
            } else {
                deferred.completeExceptionally(KoolException("Failed loading tex from ${ref.url}"))
            }
        }
        img.crossOrigin = ""
        img.src = ref.url

        return if (ref.isAtlas) {
            ImageAtlasTextureData(deferred.await(), ref.tilesX, ref.tilesY, ref.fmt)
        } else {
            ImageTextureData(deferred.await(), ref.fmt)
        }
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JsAssetManager) }
    }

    override fun createCharMap(fontProps: FontProps): CharMap = fontGenerator.getCharMap(fontProps)

    override fun inflate(zipData: Uint8Buffer): Uint8Buffer {
        val uint8Data = (zipData as Uint8BufferImpl).buffer
        return Uint8BufferImpl(pako.inflate(uint8Data) as Uint8Array)
    }

    override fun deflate(data: Uint8Buffer): Uint8Buffer {
        val uint8Data = (data as Uint8BufferImpl).buffer
        return Uint8BufferImpl(pako.deflate(uint8Data) as Uint8Array)
    }

    override fun store(key: String, data: Uint8Buffer): Boolean {
        return try {
            localStorage[key] = binToBase64((data as Uint8BufferImpl).buffer)
            true
        } catch (e: Exception) {
            logE { "Failed storing data '$key' to localStorage: $e" }
            false
        }
    }

    override fun storeString(key: String, data: String): Boolean {
        return try {
            localStorage[key] = data
            true
        } catch (e: Exception) {
            logE { "Failed storing string '$key' to localStorage: $e" }
            false
        }
    }

    override fun load(key: String): Uint8Buffer? {
        return localStorage[key]?.let { Uint8BufferImpl(base64ToBin(it)) }
    }

    override fun loadString(key: String): String? {
        return localStorage[key]
    }

    override suspend fun loadFileByUser(): Uint8Buffer? {
        val deferred = CompletableDeferred<Uint8Buffer?>()
        fileLoadDeferred = deferred
        fileChooser.asDynamic().click()
        try {
            return deferred.await()
        } catch (e: Exception) {
            logE { "Failed loading file: $e" }
        }
        return null
    }

    override fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String) {
        document.body?.let { body ->
            val element = document.createElement("a")
            element.setAttribute("href", data.toDataUrl(mimeType))
            element.setAttribute("download", fileName)

            element.asDynamic().style.display = "none"
            body.appendChild(element)
            element.asDynamic().click()
            body.removeChild(element)
        }
    }

    private fun loadSelectedFile() {
        val fileList = fileChooser.asDynamic().files as FileList
        if (fileList.length > 0) {
            val file = fileList[0]!!
            logD { "User selected file: ${file.name}" }
            val bufferPromise = file.asDynamic().arrayBuffer() as Promise<ArrayBuffer>
            bufferPromise.then(
                { data -> fileLoadDeferred?.complete(Uint8BufferImpl(Uint8Array(data))) },
                { e -> fileLoadDeferred?.completeExceptionally(e) })
        }
    }

    override suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData {
        return loadImage(TextureAssetRef(texData.toDataUrl(mimeType), true, null, false))
    }

    private fun Uint8Buffer.toDataUrl(mimeType: String): String {
        val base64 = binToBase64((this as Uint8BufferImpl).buffer)
        return "data:$mimeType;base64,$base64"
    }

    /**
     * Cumbersome / ugly method to convert Uint8Array into a base64 string in javascript
     */
    @Suppress("UNUSED_PARAMETER")
    private fun binToBase64(uint8Data: Uint8Array): String = js("""
        var chunkSize = 0x8000;
        var c = [];
        for (var i = 0; i < uint8Data.length; i += chunkSize) {
            c.push(String.fromCharCode.apply(null, uint8Data.subarray(i, i+chunkSize)));
        }
        return window.btoa(c.join(""));
    """) as String

    @Suppress("UNUSED_PARAMETER")
    private fun base64ToBin(base64: String): Uint8Array = js ("""
        var binary_string = window.atob(base64);
        var len = binary_string.length;
        var bytes = new Uint8Array(len);
        for (var i = 0; i < len; i++) {
            bytes[i] = binary_string.charCodeAt(i);
        }
        return bytes;
    """) as Uint8Array

    override suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps): Texture2d {
        val tex = Texture2d(props, assetPathToName(assetPath)) { it.loadTextureData(assetPath) }
        val data = loadTextureData(assetPath, props.format)
        tex.loadedTexture = TextureLoader.loadTexture2d(ctx, props, data)
        tex.loadingState = Texture.LoadingState.LOADED
        return tex
    }

    override suspend fun loadAndPrepareTexture(texData: TextureData, props: TextureProps, name: String?): Texture2d {
        val tex = Texture2d(props, name) { texData }
        tex.loadedTexture = TextureLoader.loadTexture2d(ctx, props, texData)
        tex.loadingState = Texture.LoadingState.LOADED
        return tex
    }

    override suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                       props: TextureProps): TextureCube {
        val name = cubeMapAssetPathToName(ft, bk, lt, rt, up, dn)
        val tex = TextureCube(props, name) { it.loadCubeMapTextureData(ft, bk, lt, rt, up, dn) }
        val data = loadCubeMapTextureData(ft, bk, lt, rt, up, dn)
        tex.loadedTexture = TextureLoader.loadTextureCube(ctx, props, data)
        tex.loadingState = Texture.LoadingState.LOADED
        return tex
    }

    override suspend fun loadAndPrepareCubeMap(texData: TextureDataCube, props: TextureProps, name: String?): TextureCube {
        val tex = TextureCube(props, name) { texData }
        tex.loadedTexture = TextureLoader.loadTextureCube(ctx, props, texData)
        tex.loadingState = Texture.LoadingState.LOADED
        return tex
    }

    override suspend fun loadAudioClip(assetPath: String): AudioClip {
        return if (isHttpAsset(assetPath)) {
            AudioClip(assetPath)
        } else {
            AudioClip("$assetsBaseDir/$assetPath")
        }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}