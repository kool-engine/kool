package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.*
import de.fabmax.kool.platform.webgl.TextureLoader
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.await
import kotlinx.coroutines.withContext
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

actual object PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    private var fileLoadDeferred: CompletableDeferred<Uint8Buffer?>? = null
    private val onFileSelectionChanged: (Event) -> Unit = { loadSelectedFile() }
    private val fileChooser: Element = document.createElement("input").apply {
        setAttribute("type", "file")
        addEventListener("change", onFileSelectionChanged)
        asDynamic().style.display = "none"
        document.body?.appendChild(this)
    }

    internal actual suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        return LoadedBlobAsset(blobRef, loadBlob(blobRef.path))
    }

    internal actual suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val texData = ImageTextureData(loadImage(textureRef.path, textureRef.isHttp), textureRef.fmt)
        return LoadedTextureAsset(textureRef, texData)
    }

    internal actual suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val texData = ImageAtlasTextureData(
            loadImage(textureRef.path, textureRef.isHttp),
            textureRef.tilesX,
            textureRef.tilesY,
            textureRef.fmt
        )
        return LoadedTextureAsset(textureRef, texData)
    }

    private suspend fun loadBlob(url: String): Uint8Buffer? {
        val prefixedUrl = if (Assets.isHttpAsset(url)) url else "${KoolSystem.config.assetPath}/$url"

        val data = CompletableDeferred<Uint8Buffer?>(Assets.job)
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

    private suspend fun loadImage(path: String, isHttp: Boolean): Image {
        val deferred = CompletableDeferred<Image>()
        val prefixedUrl = if (isHttp) path else "${KoolSystem.config.assetPath}/${path}"

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
        return deferred.await()
    }

    internal actual suspend fun waitForFonts() {
        if (fontGenerator.loadingFonts.isNotEmpty()) {
            fontGenerator.loadingFonts.forEach { it.await() }
            fontGenerator.loadingFonts.clear()
        }
    }

    internal actual fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    internal actual suspend fun loadFileByUser(filterList: List<FileFilterItem>): LoadedFile? {
        val deferred = CompletableDeferred<Uint8Buffer?>()
        fileLoadDeferred = deferred
        fileChooser.asDynamic().click()
        try {
            return deferred.await()?.let { LoadedFile(null, it) }
        } catch (e: Exception) {
            logE { "Failed loading file: $e" }
        }
        return null
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

    internal actual fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: String
    ): String? {
        val fName = if (defaultFileName != null && filterList.isNotEmpty()) {
            val extension = filterList.first().fileExtensions.split(',')[0]
            "${defaultFileName}.${extension}"
        } else {
            defaultFileName
        }
        document.body?.let { body ->
            val element = document.createElement("a")
            element.setAttribute("href", data.toDataUrl(mimeType))
            fName?.let { element.setAttribute("download", it) }

            element.asDynamic().style.display = "none"
            body.appendChild(element)
            element.asDynamic().click()
            body.removeChild(element)
        }
        return null
    }

    private fun Uint8Buffer.toDataUrl(mimeType: String): String {
        val base64 = BufferUtil.encodeBase64(this)
        return "data:$mimeType;base64,$base64"
    }

    internal actual suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d {
        val image = (Assets.loadTextureData(imagePath, format) as ImageTextureData).image
        return BufferedImageTextureData(image, format)
    }

    internal actual suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String): TextureData {
        return ImageTextureData(loadImage(texData.toDataUrl(mimeType), true), null)
    }

    internal actual suspend fun uploadTextureToGpu(texture: Texture, texData: TextureData) {
        withContext(Dispatchers.RenderLoop) {
            val ctx = KoolSystem.requireContext() as JsContext
            texture.loadedTexture = when (texture) {
                is Texture1d -> TextureLoader.loadTexture1d(ctx, texture.props, texData)
                is Texture2d -> TextureLoader.loadTexture2d(ctx, texture.props, texData)
                is Texture3d -> TextureLoader.loadTexture3d(ctx, texture.props, texData)
                is TextureCube -> TextureLoader.loadTextureCube(ctx, texture.props, texData)
                else -> throw IllegalArgumentException("Unsupported texture type: $texture")
            }
            texture.loadingState = Texture.LoadingState.LOADED
        }
    }

    internal actual suspend fun loadAudioClip(assetPath: String): AudioClip {
        return if (Assets.isHttpAsset(assetPath)) {
            AudioClip(assetPath)
        } else {
            AudioClip("${KoolSystem.config.assetPath}/$assetPath")
        }
    }

}