package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.modules.filesystem.FileSystemAssetLoader
import de.fabmax.kool.modules.filesystem.FileSystemDirectory
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

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
            val tex = BufferedTexture2d(texData, font.fontMapProps, font.toString())
            map = FontMap(font, tex, metrics)
            font.scale = fontScale
            font.map = map

        } else {
            val tex = map.texture as? BufferedTexture2d
            if (tex != null) {
                tex.updateTextureData(texData)
                font.scale = fontScale
                map.putAll(metrics)
            } else {
                logE { "Unable to update texture data of font $font" }
            }
        }
        return map
    }

    /**
     * Renders the given [AtlasFont] into a new image and stores the individual character metrics into the
     * [outMetrics] map. This function is usually not called directly (but you can if you want to).
     */
    @Deprecated("createAtlasFontMapData() is deprecated, use superior MsdfFont instead of AtlasFont")
    fun createAtlasFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
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
     * @return On JVM the selected path is returned or null if the user canceled the operation. On js null is always
     *         returned.
     */
    suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String? = null,
        filterList: List<FileFilterItem> = emptyList(),
        mimeType: String = "application/octet-stream"
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
     * image (e.g. 'image/png') and returns the image as [TextureData].
     */
    suspend fun loadTextureDataFromBuffer(
        texData: Uint8Buffer,
        mimeType: String,
        props: TextureProps? = null
    ): TextureData = loadTextureDataFromBufferAsync(texData, mimeType, props).await()

    suspend fun loadTexture1d(
        texData: TextureData1d,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture1d")
    ): Texture1d = loadTexture1dAsync(texData, props, name).await()

    suspend fun loadTexture2d(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture2d")
    ): Texture2d = loadTexture2dAsync(texData, props, name).await()

    suspend fun loadTexture3d(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture3d")
    ): Texture3d = loadTexture3dAsync(texData, props, name).await()

    suspend fun loadTextureCube(
        texData: TextureDataCube,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("TextureCube")
    ): TextureCube = loadTextureCubeAsync(texData, props, name).await()

    /**
     * Asynchronously loads the texture data from the given byte buffer using the image type specified in [mimeType]
     * to decode the image (e.g. 'image/png') and returns the image as [TextureData].
     */
    fun loadTextureDataFromBufferAsync(texData: Uint8Buffer, mimeType: String, props: TextureProps? = null): Deferred<TextureData> = async {
        platformAssets.loadTextureDataFromBuffer(texData, mimeType, props)
    }

    fun loadTexture1dAsync(
        texData: TextureData1d,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture1d")
    ): Deferred<Texture1d> = async {
        val tex = Texture1d(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        tex
    }

    fun loadTexture2dAsync(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture2d")
    ): Deferred<Texture2d> = async {
        Texture2d(props, name, BufferedTextureLoader(texData)).also { uploadTextureToGpu(it, texData) }
    }

    fun loadTexture3dAsync(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture3d")
    ): Deferred<Texture3d> = async {
        val tex = Texture3d(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        tex
    }

    fun loadTextureCubeAsync(
        texData: TextureDataCube,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("TextureCube")
    ): Deferred<TextureCube> = async {
        val tex = TextureCube(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        tex
    }

    private suspend fun uploadTextureToGpu(texture: Texture, texData: TextureData) {
        withContext(Dispatchers.RenderLoop) {
            KoolSystem.requireContext().backend.writeTextureData(texture, texData)
        }
    }

    ////////////////////////////////////////////////////////////////////
    // Delegates to defaultLoader
    ////////////////////////////////////////////////////////////////////

    /**
     * Asynchronously loads the texture data at the given path and returns it as [TextureData].
     */
    fun loadTextureDataAsync(assetPath: String, props: TextureProps? = null): Deferred<TextureData> = defaultLoader.loadTextureDataAsync(assetPath, props)

    /**
     * Loads the texture data at the given path and returns it as [TextureData].
     */
    suspend fun loadTextureData(assetPath: String, props: TextureProps? = null): TextureData = defaultLoader.loadTextureData(assetPath, props)

    /**
     * Similar to [loadTextureDataAsync], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     */
    fun loadTextureData2dAsync(assetPath: String, props: TextureProps? = null): Deferred<TextureData2d> = defaultLoader.loadTextureData2dAsync(assetPath, props)

    /**
     * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     */
    suspend fun loadTextureData2d(assetPath: String, props: TextureProps? = null): TextureData2d = defaultLoader.loadTextureData2d(assetPath, props)

    /**
     * Asynchronously loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     */
    fun loadTextureDataAtlasAsync(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps? = null
    ): Deferred<TextureData> = defaultLoader.loadTextureDataAtlasAsync(assetPath, tilesX, tilesY, props)

    /**
     * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     */
    suspend fun loadTextureDataAtlas(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps? = null
    ): TextureData = defaultLoader.loadTextureDataAtlas(assetPath, tilesX, tilesY, props)

    /**
     * Asynchronously loads a cube map from the given image paths (one for each side).
     */
    fun loadTextureDataCubeAsync(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String
    ): Deferred<TextureDataCube> = defaultLoader.loadTextureDataCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)

    /**
     * Loads a cube map from the given image paths (one for each side).
     */
    suspend fun loadTextureDataCube(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String
    ): TextureDataCube = defaultLoader.loadTextureDataCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)

    fun loadTexture2dAsync(assetPath: String, props: TextureProps = TextureProps()): Deferred<Texture2d> = defaultLoader.loadTexture2dAsync(assetPath, props)

    suspend fun loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Texture2d = defaultLoader.loadTexture2d(assetPath, props)

    fun loadTexture3dAsync(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Deferred<Texture3d> = defaultLoader.loadTexture3dAsync(assetPath, tilesX, tilesY, props)

    suspend fun loadTexture3d(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Texture3d = defaultLoader.loadTexture3d(assetPath, tilesX, tilesY, props)

    fun loadTextureCubeAsync(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): Deferred<TextureCube> = defaultLoader.loadTextureCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props)

    suspend fun loadTextureCube(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): TextureCube = defaultLoader.loadTextureCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props)

    /**
     * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    fun loadBlobAssetAsync(assetPath: String): Deferred<Uint8Buffer> = defaultLoader.loadBlobAssetAsync(assetPath)

    /**
     * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    suspend fun loadBlobAsset(assetPath: String,): Uint8Buffer = defaultLoader.loadBlobAsset(assetPath)

    fun loadAudioClipAsync(assetPath: String): Deferred<AudioClip> = defaultLoader.loadAudioClipAsync(assetPath)

    suspend fun loadAudioClip(assetPath: String): AudioClip = defaultLoader.loadAudioClip(assetPath)
}

expect fun fileSystemAssetLoader(baseDir: FileSystemDirectory): FileSystemAssetLoader

expect suspend fun decodeDataUri(dataUri: String): Uint8Buffer

data class FileFilterItem(val name: String, val fileExtensions: String)

internal expect fun PlatformAssets(): PlatformAssets

internal interface PlatformAssets {
    suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData

    suspend fun waitForFonts()

    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d

    suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile>
    suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: String = MimeType.BINARY_DATA
    ): String?
}

object MimeType {
    const val BINARY_DATA = "application/octet-stream"
    const val IMAGE_PNG = "image/png"
    const val IMAGE_JPG = "image/jpeg"
    const val IMAGE_SVG = "image/svg+xml"

    fun forFileName(fileName: String): String {
        return when (fileName.substringAfterLast('.').lowercase()) {
            "png" -> IMAGE_PNG
            "jpg" -> IMAGE_JPG
            "jpeg" -> IMAGE_JPG
            "svg" -> IMAGE_SVG
            else -> BINARY_DATA
        }
    }
}
