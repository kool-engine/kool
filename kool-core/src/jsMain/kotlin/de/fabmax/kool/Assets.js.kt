package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.*
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.Uint8BufferImpl
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

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader = FileSystemAssetLoaderJs(baseDir)

object PlatformAssetsImpl : PlatformAssets {

    private const val MAX_GENERATED_TEX_WIDTH = 2048
    private const val MAX_GENERATED_TEX_HEIGHT = 2048

    private val fontGenerator = FontMapGenerator(MAX_GENERATED_TEX_WIDTH, MAX_GENERATED_TEX_HEIGHT)

    override suspend fun waitForFonts() {
        if (fontGenerator.loadingFonts.isNotEmpty()) {
            fontGenerator.loadingFonts.forEach { it.await() }
            fontGenerator.loadingFonts.clear()
        }
    }

    override fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        return fontGenerator.createFontMapData(font, fontScale, outMetrics)
    }

    override suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile> {
        document.body?.let { body ->
            val accept = filterList.joinToString { item ->
                item.fileExtensions.joinToString(", ") { ".${it.trim().removePrefix(".")}" }
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
        mimeType: MimeType
    ): String? {
        val fileHandle = CompletableDeferred<FileSystemFileHandle?>()
        showSaveFilePicker(FilePickerOptions(defaultFileName, filterList))
            .then { fileHandle.complete(it) }
            .catch { fileHandle.complete(null) }

        val buffer = (data as Uint8BufferImpl).buffer
        val file = fileHandle.await() ?: return null
        file.createWritable().await().apply {
            write(buffer)
            close()
        }
        return file.getFile().await().name
    }

    override suspend fun loadImageFromBuffer(
        texData: Uint8Buffer,
        mimeType: MimeType,
        format: TexFormat,
        resolveSize: Vec2i?
    ): ImageTextureData {
        val imgBitmap = if (mimeType == MimeType.IMAGE_SVG) {
            val svgData = texData.toArray().decodeToString()
            val dataUrl = "data:image/svg+xml;charset=utf-8," + encodeURIComponent(svgData)
            loadSvgImageFromUrl(dataUrl, resolveSize).getOrThrow()
        } else {
            val array = (texData as Uint8BufferImpl).buffer
            val imgBlob = Blob(arrayOf(array), BlobPropertyBag(mimeType.value))
            createImageBitmap(imgBlob, ImageBitmapOptions(resolveSize)).await()
        }
        return ImageTextureData(imgBitmap, ImageData.idForImageData("ImageTextureData", texData))
    }

    suspend fun loadSvgImageFromUrl(
        url: String,
        resolveSize: Vec2i?
    ): Result<ImageBitmap> {
        val deferredBitmap = CompletableDeferred<ImageBitmap>()
        val img = resolveSize?.let { Image(it.x, it.y) } ?: Image()
        img.onload = {
            createImageBitmap(img, ImageBitmapOptions(resolveSize)).then { bmp -> deferredBitmap.complete(bmp) }
        }
        img.onerror = { _, _, _, _, _ ->
            deferredBitmap.completeExceptionally(IllegalStateException("Failed decoding SVG image from buffer"))
        }
        img.crossOrigin = ""
        img.src = url
        return try {
            Result.success(deferredBitmap.await())
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }
}

external fun encodeURIComponent(string: String): String
external fun createImageBitmap(blob: Blob, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>
external fun createImageBitmap(image: Image, options: ImageBitmapOptions = definedExternally): Promise<ImageBitmap>

external fun fetch(resource: String): Promise<Response>

external interface ImageBitmapOptions

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

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    val response = fetch(dataUri).await()
    val arrayBuffer = response.arrayBuffer().await()
    return Uint8BufferImpl(Uint8Array(arrayBuffer))
}