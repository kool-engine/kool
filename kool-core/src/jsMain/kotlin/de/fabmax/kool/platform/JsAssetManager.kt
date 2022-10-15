package de.fabmax.kool.platform

import de.fabmax.kool.*
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.webgl.TextureLoader
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.coroutines.*
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.dom.Element
import org.w3c.dom.Image
import org.w3c.dom.events.Event
import org.w3c.files.FileList
import org.w3c.files.get
import org.w3c.xhr.ARRAYBUFFER
import org.w3c.xhr.XMLHttpRequest
import org.w3c.xhr.XMLHttpRequestResponseType
import kotlin.js.Promise

class JsAssetManager internal constructor(props: JsContext.InitProps, val ctx: JsContext) : AssetManager() {

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT, props, this, ctx)
    private val localAssetsPath = props.localAssetPath

    private var fileLoadDeferred: CompletableDeferred<Uint8Buffer?>? = null
    private val onFileSelectionChanged: (Event) -> Unit = { loadSelectedFile() }
    private val fileChooser: Element = document.createElement("input").apply {
        setAttribute("type", "file")
        addEventListener("change", onFileSelectionChanged)
        asDynamic().style.display = "none"
        document.body?.appendChild(this)
    }

    override val storage = KeyValueStorageJs()

    override suspend fun loadRaw(rawRef: RawAssetRef) = LoadedRawAsset(rawRef, loadRaw(rawRef.url))

    override suspend fun loadTexture(textureRef: TextureAssetRef) = LoadedTextureAsset(textureRef, loadImage(textureRef))

    private suspend fun loadRaw(url: String): Uint8Buffer? {
        val prefixedUrl = if (isHttpAsset(url)) url else "$localAssetsPath/$url"

        val data = CompletableDeferred<Uint8Buffer?>(job)
        val req = XMLHttpRequest()
        req.responseType = XMLHttpRequestResponseType.ARRAYBUFFER
        req.onload = {
            val array = Uint8Array(req.response as ArrayBuffer)
            data.complete(Uint8BufferImpl(array))
        }
        req.onerror = {
            data.complete(null)
            logE { "Failed loading resource $prefixedUrl: $it" }
        }
        req.open("GET", prefixedUrl)
        req.send()

        return data.await()
    }

    private suspend fun loadImage(ref: TextureAssetRef): TextureData {
        val deferred = CompletableDeferred<Image>()
        val prefixedUrl = if (isHttpAsset(ref.url)) ref.url else "$localAssetsPath/${ref.url}"

        val img = Image()
        img.onload = {
            deferred.complete(img)
        }
        img.onerror = { _, _, _, _, _ ->
            if (prefixedUrl.startsWith("data:")) {
                deferred.completeExceptionally(KoolException("Failed loading tex from data URL"))
            } else {
                deferred.completeExceptionally(KoolException("Failed loading tex from $prefixedUrl"))
            }
        }
        img.crossOrigin = ""
        img.src = prefixedUrl

        return if (ref.isAtlas) {
            ImageAtlasTextureData(deferred.await(), ref.tilesX, ref.tilesY, ref.fmt)
        } else {
            ImageTextureData(deferred.await(), ref.fmt)
        }
    }

    fun loadTextureAsync(loader: suspend CoroutineScope.(AssetManager) -> TextureData): Deferred<TextureData> {
        return async { loader(this@JsAssetManager) }
    }

    override suspend fun waitForFonts() {
        if (fontGenerator.loadingFonts.isNotEmpty()) {
            fontGenerator.loadingFonts.forEach { it.await() }
            fontGenerator.loadingFonts.clear()
        }
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>) =
        fontGenerator.createFontMapData(font, fontScale, outMetrics)

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

    override suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d {
        val image = (loadTextureData(imagePath, format) as ImageTextureData).image
        return BufferedImageTextureData(image, format)
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
        val base64 = BufferUtil.binToBase64((this as Uint8BufferImpl).buffer)
        return "data:$mimeType;base64,$base64"
    }

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
            AudioClip("$localAssetsPath/$assetPath")
        }
    }

    companion object {
        private const val MAX_GENERATED_TEX_WIDTH = 2048
        private const val MAX_GENERATED_TEX_HEIGHT = 2048
    }
}