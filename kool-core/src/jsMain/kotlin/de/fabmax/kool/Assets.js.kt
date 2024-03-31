package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoaderJs
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.TextureData
import de.fabmax.kool.pipeline.TextureData2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.platform.FileSaver
import de.fabmax.kool.platform.FontMapGenerator
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.saveAs
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

    override suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData {
        val array = (texData as Uint8BufferImpl).buffer
        val imgBlob = Blob(arrayOf(array), BlobPropertyBag(mimeType))
        val imgBitmap = createImageBitmap(imgBlob, ImageBitmapOptions(props?.resolveSize)).await()
        return ImageTextureData(imgBitmap, null)
    }
}

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