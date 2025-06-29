package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.AtlasFont
import de.fabmax.kool.util.CharMetrics
import de.fabmax.kool.util.FontMap
import de.fabmax.kool.util.Uint8Buffer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlin.coroutines.CoroutineContext
import kotlin.jvm.JvmInline

object Assets : CoroutineScope {

    var defaultLoader = KoolSystem.config.defaultAssetLoader

    private const val NUM_LOAD_WORKERS = 8
    internal val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val platformAssets = PlatformAssets()
    private val loadedAtlasFontMaps = mutableMapOf<AtlasFont, FontMap>()

    internal fun close() {
        job.cancel()
    }

    /**
     * Suspends until custom fonts are loaded. This is useful in case a loading screen uses some text loading message
     * which cannot be displayed before fonts are loaded.
     */
    suspend fun waitForFonts() {
        platformAssets.waitForFonts()
    }

    /**
     * Returns the (cached or newly created) [FontMap] / texture for the given [AtlasFont] and display scale.
     */
    @Deprecated("getOrCreateAtlasFontMap() is deprecated, use superior MsdfFont instead of AtlasFont")
    fun getOrCreateAtlasFontMap(font: AtlasFont, fontScale: Float): FontMap {
        return loadedAtlasFontMaps.getOrPut(font) {
            @Suppress("DEPRECATION")
            updateAtlasFontMap(font, fontScale)
        }
    }

    /**
     * Updates the [FontMap] / texture for the given [AtlasFont] to match the given display scale. This is an expensive
     * operation as the font is rendered into a new image and the underlying font map texture is updated.
     */
    @Deprecated("updateAtlasFontMap() is deprecated, use superior MsdfFont instead of AtlasFont")
    fun updateAtlasFontMap(font: AtlasFont, fontScale: Float): FontMap {
        var map = font.map
        val metrics = mutableMapOf<Char, CharMetrics>()
        @Suppress("DEPRECATION")
        val texData = createAtlasFontMapData(font, fontScale, metrics)

        if (map == null) {
            val tex = Texture2d(texData, font.mipMapping, font.samplerSettings, font.toString())
            map = FontMap(font, tex, metrics)
            font.scale = fontScale
            font.map = map

        } else {
            map.texture.uploadLazy(texData)
            font.scale = fontScale
            map.putAll(metrics)
        }
        return map
    }

    /**
     * Renders the given [AtlasFont] into a new image and stores the individual character metrics into the
     * [outMetrics] map. This function is usually not called directly (but you can if you want to).
     */
    @Deprecated("createAtlasFontMapData() is deprecated, use superior MsdfFont instead of AtlasFont")
    fun createAtlasFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d {
        return platformAssets.createFontMapData(font, fontScale, outMetrics)
    }

    /**
     * Opens a file chooser dialog for the user to select and load a file. Returns the selected files or an empty list
     * if the user canceled the dialog or loading failed.
     *
     * @param filterList Optional file filter list (e.g. ("Images", ".jpg, .png"); only supported on JVM, ignored on js).
     * @param multiSelect Determines if multiple files can be selected or only a single one.
     * @return The list of [LoadableFile]s selected by the user (is empty if the operation was canceled).
     */
    suspend fun loadFileByUser(filterList: List<FileFilterItem> = emptyList(), multiSelect: Boolean = false): List<LoadableFile> {
        return platformAssets.loadFileByUser(filterList, multiSelect)
    }

    /**
     * Opens a file chooser dialog for the user to select a destination file for the given data.
     *
     * @return On JVM the selected path is returned or null if the user canceled the operation. On js only the file
     *         name is returned.
     */
    suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String? = null,
        filterList: List<FileFilterItem> = emptyList(),
        mimeType: MimeType = MimeType.BINARY_DATA
    ): String? {
        return platformAssets.saveFileByUser(data, defaultFileName, filterList, mimeType)
    }

    fun isHttpAsset(assetPath: String): Boolean =
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true) ||
            assetPath.startsWith("data:", true)

    fun isDataUri(uri: String) = uri.startsWith("data:", true)

    /**
     * Loads the texture data from the given byte buffer using the image type specified in [mimeType] to decode the
     * image (e.g. 'image/png') and returns the image as [ImageData].
     */
    suspend fun loadImageFromBuffer(
        texData: Uint8Buffer,
        mimeType: MimeType,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): ImageData2d = platformAssets.loadImageFromBuffer(texData, mimeType, format, resolveSize)

    /**
     * Asynchronously loads the texture data from the given byte buffer using the image type specified in [mimeType]
     * to decode the image (e.g. 'image/png') and returns the image as [ImageData].
     */
    fun loadImageFromBufferAsync(
        texData: Uint8Buffer,
        mimeType: MimeType,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): Deferred<ImageData2d> = async { loadImageFromBuffer(texData, mimeType, format, resolveSize) }
}

expect fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader

expect suspend fun decodeDataUri(dataUri: String): Uint8Buffer

data class FileFilterItem(val name: String, val mimeType: MimeType, val fileExtensions: List<String>)

internal expect fun PlatformAssets(): PlatformAssets

internal interface PlatformAssets {
    suspend fun loadImageFromBuffer(texData: Uint8Buffer, mimeType: MimeType, format: TexFormat, resolveSize: Vec2i?): ImageData2d

    suspend fun waitForFonts()

    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): BufferedImageData2d

    suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile>
    suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: MimeType = MimeType.BINARY_DATA
    ): String?
}

@JvmInline
value class MimeType(val value: String) {
    companion object {
        val BINARY_DATA = MimeType("application/octet-stream")
        val ZIP = MimeType("application/x-zip")
        val IMAGE_PNG = MimeType("image/png")
        val IMAGE_JPG = MimeType("image/jpeg")
        val IMAGE_SVG = MimeType("image/svg+xml")

        fun forFileName(fileName: String): MimeType {
            return when (fileName.substringAfterLast('.').lowercase()) {
                "png" -> IMAGE_PNG
                "jpg" -> IMAGE_JPG
                "jpeg" -> IMAGE_JPG
                "svg" -> IMAGE_SVG
                else -> BINARY_DATA
            }
        }
    }
}
