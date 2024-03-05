package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.modules.audio.AudioClipImpl
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.*
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileList
import org.w3c.files.get
import kotlin.js.Promise

internal actual fun PlatformAssets(): PlatformAssets = PlatformAssetsImpl

private object PlatformAssetsImpl : PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset {
        val url = blobRef.path
        val prefixedUrl = if (Assets.isHttpAsset(url)) url else "${Assets.assetsBasePath}/$url"
        val response = fetch(prefixedUrl).await()

        val data = if (!response.ok) {
            logE { "Failed loading resource $prefixedUrl: ${response.status} ${response.statusText}" }
            null
        } else {
            val arrayBuffer = response.arrayBuffer().await()
            Uint8BufferImpl(Uint8Array(arrayBuffer))
        }
        return LoadedBlobAsset(blobRef, data)
    }

    override suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset {
        val resolveSz = textureRef.props?.resolveSize
        val img = loadImageBitmap(textureRef.path, textureRef.isHttp, resolveSz)
        val texData = ImageTextureData(img, textureRef.props?.format)
        return LoadedTextureAsset(textureRef, texData)
    }

    override suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset {
        val resolveSz = textureRef.props?.resolveSize
        val texData = ImageAtlasTextureData(
            loadImageBitmap(textureRef.path, textureRef.isHttp, resolveSz),
            textureRef.tilesX,
            textureRef.tilesY,
            textureRef.props?.format
        )
        return LoadedTextureAsset(textureRef, texData)
    }

    private suspend fun loadImageBitmap(path: String, isHttp: Boolean, resize: Vec2i?): ImageBitmap {
        val mime = MimeType.forFileName(path)
        val prefixedUrl = if (isHttp) path else "${Assets.assetsBasePath}/${path}"

        return if (mime != MimeType.IMAGE_SVG) {
            // raster image type -> fetch blob and create ImageBitmap directly
            val response = fetch(prefixedUrl).await()
            val imgBlob = response.blob().await()
            createImageBitmap(imgBlob, ImageBitmapOptions(resize)).await()

        } else {
            // svg image -> use an Image element to convert it to an ImageBitmap
            val deferredBitmap = CompletableDeferred<ImageBitmap>()
            val img = resize?.let { Image(it.x, it.y) } ?: Image()
            img.onload = {
                createImageBitmap(img, ImageBitmapOptions(resize)).then { bmp -> deferredBitmap.complete(bmp) }
            }
            img.onerror = { _, _, _, _, _ ->
                deferredBitmap.completeExceptionally(IllegalStateException("Failed loading tex from $prefixedUrl"))
            }
            img.crossOrigin = ""
            img.src = prefixedUrl
            deferredBitmap.await()
        }
    }

    override suspend fun waitForFonts() {
        if (fontGenerator.loadingFonts.isNotEmpty()) {
            fontGenerator.loadingFonts.forEach { it.await() }
            fontGenerator.loadingFonts.clear()
        }
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        document.body?.let { body ->
            val accept = filterList.joinToString { item ->
                item.fileExtensions
                    .split(',')
                    .joinToString(", ") { ".${it.trim().removePrefix(".")}" }
            }

            val deferred = CompletableDeferred<FileList>()
            val chooser = document.createElement("input")
            chooser.setAttribute("type", "file")
            chooser.setAttribute("accept", accept)
            if (multiSelect) {
                chooser.setAttribute("multiple", "true")
            }
            chooser.addEventListener("change", callback = { deferred.complete(chooser.asDynamic().files as FileList) })
            chooser.asDynamic().style.display = "none"
            body.appendChild(chooser)
            chooser.asDynamic().click()

            val fileList = deferred.await()
            val selectedFiles = mutableListOf<LoadableFile>()
            for (i in 0 until fileList.length) {
                fileList[i]?.let { selectedFiles += LoadableFileImpl(it) }
            }

            body.removeChild(chooser)
            return selectedFiles
        }
        return emptyList()
    }

    override suspend fun saveFileByUser(
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
        FileSaver.saveAs(data, fName ?: "", mimeType)
        return null
    }

    override suspend fun loadTextureData2d(imagePath: String, props: TextureProps?): TextureData2d {
        val p = props ?: TextureProps()
        val texData = Assets.loadTextureData(imagePath, props) as ImageTextureData
        return TextureData2d(ImageTextureData.imageBitmapToBuffer(texData.data, p), texData.width, texData.height, p.format)
    }

    override suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData {
        val array = (texData as Uint8BufferImpl).buffer
        val imgBlob = Blob(arrayOf(array), BlobPropertyBag(mimeType))
        val imgBitmap = createImageBitmap(imgBlob, ImageBitmapOptions(props?.resolveSize)).await()
        return ImageTextureData(imgBitmap, null)
    }

    override suspend fun loadAudioClip(assetPath: String): AudioClip {
        return if (Assets.isHttpAsset(assetPath)) {
            AudioClipImpl(assetPath)
        } else {
            AudioClipImpl("${Assets.assetsBasePath}/$assetPath")
        }
    }
}

external fun createImageBitmap(blob: Blob, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
external fun createImageBitmap(image: Image, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>

external fun fetch(resource: String): Promise<Response>

external interface ImageBitmapOptions

@Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
fun ImageBitmapOptions(resize: Vec2i? = null, resizeQuality: String = "high"): ImageBitmapOptions {
    val o = js("({})")
    o["premultiplyAlpha"] = "none"
    if (resize != null) {
        o["resizeWidth"] = resize.x
        o["resizeHeight"] = resize.y
        o["resizeQuality"] = resizeQuality
    }
    return o
}

external interface Response {
    val ok: Boolean
    val status: Int
    val statusText: String

    fun arrayBuffer(): Promise<ArrayBuffer>
    fun blob(): Promise<Blob>
    fun text(): Promise<String>
}
