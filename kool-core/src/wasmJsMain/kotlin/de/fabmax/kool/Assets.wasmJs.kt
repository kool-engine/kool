package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoaderWasm
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.BufferedImageData2d
import de.fabmax.kool.pipeline.ImageData
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.util.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.await
import org.khronos.webgl.ArrayBuffer
import org.khronos.webgl.Uint8Array
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.Image
import org.w3c.dom.ImageBitmap
import org.w3c.dom.ImageBitmapOptions
import org.w3c.fetch.Response
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag
import org.w3c.files.FileList
import org.w3c.files.get

internal actual fun PlatformAssets(): PlatformAssets = PlatformAssetsImpl

actual fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader = FileSystemAssetLoaderWasm(baseDir)

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

            val deferred = CompletableDeferred<FileList?>()
            val chooser = document.createElement("input") as HTMLInputElement
            chooser.setAttribute("type", "file")
            chooser.setAttribute("accept", accept)
            if (multiSelect) {
                chooser.setAttribute("multiple", "true")
            }
            chooser.addEventListener("change", callback = { deferred.complete(chooser.files) })
            chooser.style.display = "none"
            body.appendChild(chooser)
            chooser.click()

            val selectedFiles = mutableListOf<LoadableFile>()
            deferred.await()?.let { fileList ->
                for (i in 0 until fileList.length) {
                    fileList[i]?.let { selectedFiles += LoadableFileImpl(it) }
                }
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
        logE { "saveFileByUser() not implemented for WASM" }
        return null
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
            val parts = JsArray<JsAny?>()
            parts[0] = array
            val imgBlob = Blob(parts, BlobPropertyBag(mimeType.value))
            window.createImageBitmap(imgBlob, ImageBitmapOptions(resolveSize)).await()
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
            window.createImageBitmap(img, ImageBitmapOptions(resolveSize)).then<ImageBitmap> { bmp ->
                deferredBitmap.complete(bmp)
                bmp
            }
        }
        img.onerror = { r, _, _, _, _ ->
            deferredBitmap.completeExceptionally(IllegalStateException("Failed decoding SVG image from buffer"))
            r
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

fun ImageBitmapOptions(size: Vec2i?) = ImageBitmapOptions(size?.x, size?.y)
private fun ImageBitmapOptions(width: Int?, height: Int?): ImageBitmapOptions =
    js("""({
        premultiplyAlpha: 'none',
        resizeWidth: (width != null ? width : undefined),
        resizeHeight: (height != null ? height : undefined),
        resizeQuality: (width != null ? 'high' : undefined)
    })""")

external fun encodeURIComponent(string: String): String

actual suspend fun decodeDataUri(dataUri: String): Uint8Buffer {
    val response: Response = window.fetch(dataUri).await()
    val arrayBuffer: ArrayBuffer = response.arrayBuffer().await()
    return Uint8BufferImpl(Uint8Array(arrayBuffer))
}
