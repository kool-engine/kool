package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.util.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

object Assets : CoroutineScope {

    var assetsBasePath = KoolSystem.config.assetPath
        set(value) {
            if (field != value) {
                logI { "Asset base changed from $field to: $value" }
                field = value
            }
        }

    private const val NUM_LOAD_WORKERS = 8
    internal val job = Job()
    override val coroutineContext: CoroutineContext
        get() = job

    private val platformAssets = PlatformAssets()

    private val awaitedAssetsChannel = Channel<AwaitedAsset>()
    private val assetRefChannel = Channel<AssetRef>(Channel.UNLIMITED)
    private val loadedAssetChannel = Channel<LoadedAsset>()

    private val loadedAtlasFontMaps = mutableMapOf<AtlasFont, FontMap>()

    private val workers = List(NUM_LOAD_WORKERS) { loadWorker(assetRefChannel, loadedAssetChannel) }

    private val loader = (this as CoroutineScope).launch {
        val requested = mutableMapOf<AssetRef, MutableList<AwaitedAsset>>()
        while (true) {
            select<Unit> {
                awaitedAssetsChannel.onReceive { awaited ->
                    val awaiting = requested[awaited.ref]
                    if (awaiting == null) {
                        requested[awaited.ref] = mutableListOf(awaited)
                        assetRefChannel.send(awaited.ref)
                    } else {
                        awaiting.add(awaited)
                    }
                }
                loadedAssetChannel.onReceive { loaded ->
                    val awaiting = requested.remove(loaded.ref)!!
                    for (awaited in awaiting) {
                        awaited.awaiting.complete(loaded)
                    }
                }
            }
        }
    }

    private fun loadWorker(
        assetRefs: ReceiveChannel<AssetRef>,
        loadedAssets: SendChannel<LoadedAsset>
    ) = (this as CoroutineScope).launch {
        for (ref in assetRefs) {
            loadedAssets.send(loadAsset(ref))
        }
    }

    internal fun close() {
        job.cancel()
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset {
        return when(ref) {
            is BlobAssetRef -> platformAssets.loadBlob(ref)
            is TextureAssetRef -> platformAssets.loadTexture(ref)
            is TextureAtlasAssetRef -> platformAssets.loadTextureAtlas(ref)
        }
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
    fun getOrCreateAtlasFontMap(font: AtlasFont, fontScale: Float): FontMap {
        return loadedAtlasFontMaps.getOrPut(font) {
            updateAtlasFontMap(font, fontScale)
        }
    }

    /**
     * Updates the [FontMap] / texture for the given [AtlasFont] to match the given display scale. This is an expensive
     * operation as the font is rendered into a new image and the underlying font map texture is updated.
     */
    fun updateAtlasFontMap(font: AtlasFont, fontScale: Float): FontMap {
        var map = font.map
        val metrics = mutableMapOf<Char, CharMetrics>()
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

    /**
     * Launches a coroutine in the Assets CoroutineScope and executes the given block from within the [Assets] scope
     * for convenience.
     *
     * This function is deprecated as it easily leads to non-deterministic bugs when a loaded asset is inserted into
     * a [Scene] from within the coroutine. This is because, on JVM, the coroutine is executed by a different thread
     * than the main thread and modifying scene content from a different thread leads to a race condition. You should
     * use [launchOnMainThread] instead.
     */
    @Deprecated("use launchOnMainThread { } instead", ReplaceWith("launchOnMainThread"))
    fun launch(block: suspend Assets.() -> Unit) {
        (this as CoroutineScope).launch {
            block.invoke(this@Assets)
        }
    }

    /**
     * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadBlobAsset(assetPath: String): Uint8Buffer {
        val ref = BlobAssetRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedBlobAsset
        if (loaded.data != null) {
            logD { "Loaded ${trimAssetPath(assetPath)} (${(loaded.data.capacity / 1_048_576.0).toString(1)} mb)" }
        }
        return loaded.data ?: throw KoolException("Failed loading blob asset ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads the texture data at the given path and returns the image as [TextureData].
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadTextureData(assetPath: String, props: TextureProps? = null): TextureData {
        val ref = TextureAssetRef(assetPath, props)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads the texture data from the given byte buffer using the image type specified in [mimeType] to decode the
     * image (e.g. 'image/png') and returns the image as [TextureData].
     */
    suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps? = null): TextureData {
        return platformAssets.loadTextureDataFromBuffer(texData, mimeType, props)
    }

    /**
     * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadTextureData2d(imagePath: String, props: TextureProps? = null): TextureData2d {
        return platformAssets.loadTextureData2d(imagePath, props)
    }

    /**
     * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadTextureAtlasData(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): TextureData {
        val ref = TextureAtlasAssetRef(assetPath, props, tilesX, tilesY)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}x${it.depth})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture atlas ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads a cube map from the given image paths (one for each side).
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadCubeMapTextureData(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String
    ): TextureDataCube {
        val ftd = loadTextureData(pathFront)
        val bkd = loadTextureData(pathBack)
        val ltd = loadTextureData(pathLeft)
        val rtd = loadTextureData(pathRight)
        val upd = loadTextureData(pathUp)
        val dnd = loadTextureData(pathDown)
        return TextureDataCube(ftd, bkd, ltd, rtd, upd, dnd)
    }

    suspend fun loadTexture1d(
        texData: TextureData1d,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture1d")
    ): Texture1d {
        val tex = Texture1d(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture2d(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture2d")
    ): Texture2d {
        val tex = Texture2d(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture2d(
        assetPath: String,
        props: TextureProps = TextureProps()
    ): Texture2d {
        return loadTexture2d(loadTextureData(assetPath), props, trimAssetPath(assetPath))
    }

    suspend fun loadTexture3d(
        texData: TextureData,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("Texture3d")
    ): Texture3d {
        val tex = Texture3d(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture3d(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Texture3d {
        return loadTexture3d(loadTextureAtlasData(assetPath, tilesX, tilesY), props, trimAssetPath(assetPath))
    }

    suspend fun loadTextureCube(
        texData: TextureDataCube,
        props: TextureProps = TextureProps(),
        name: String = UniqueId.nextId("TextureCube")
    ): TextureCube {
        val tex = TextureCube(props, name) { texData }
        uploadTextureToGpu(tex, texData)
        return tex
    }

    private suspend fun uploadTextureToGpu(texture: Texture, texData: TextureData) {
        withContext(Dispatchers.RenderLoop) {
            KoolSystem.requireContext().backend.uploadTextureToGpu(texture, texData)
        }
    }

    suspend fun loadTextureCube(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): TextureCube {
        val name = trimCubeMapAssetPath(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
        val texData = loadCubeMapTextureData(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
        return loadTextureCube(texData, props, name)
    }

    suspend fun loadAudioClip(assetPath: String): AudioClip {
        return platformAssets.loadAudioClip(assetPath)
    }

    fun trimAssetPath(assetPath: String): String {
        return if (assetPath.startsWith("data:", true)) {
            val idx = assetPath.indexOf(';')
            assetPath.substring(0 until idx)
        } else {
            assetPath
        }
    }

    fun trimCubeMapAssetPath(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): String {
        return "cubeMap(ft:${trimAssetPath(ft)}, bk:${trimAssetPath(bk)}, lt:${trimAssetPath(lt)}, rt:${trimAssetPath(rt)}, up:${trimAssetPath(up)}, dn:${trimAssetPath(dn)})"
    }

    private class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset> = CompletableDeferred(job))
}

sealed class AssetRef(path: String) {
    val isHttp: Boolean = Assets.isHttpAsset(path)
}

data class BlobAssetRef(
    val path: String,
) : AssetRef(path)

data class TextureAssetRef(
    val path: String,
    val props: TextureProps?
) : AssetRef(path)

data class TextureAtlasAssetRef(
    val path: String,
    val props: TextureProps?,
    val tilesX: Int = 1,
    val tilesY: Int = 1,
) : AssetRef(path)

sealed class LoadedAsset(val ref: AssetRef, val successful: Boolean)
class LoadedBlobAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)

data class FileFilterItem(val name: String, val fileExtensions: String)

internal expect fun PlatformAssets(): PlatformAssets

internal interface PlatformAssets {
    suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset
    suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset
    suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset

    suspend fun waitForFonts()
    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d

    suspend fun loadFileByUser(filterList: List<FileFilterItem>, multiSelect: Boolean): List<LoadableFile>
    suspend fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: String = MimeType.BINARY_DATA
    ): String?

    suspend fun loadTextureData2d(imagePath: String, props: TextureProps?): TextureData2d
    suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String, props: TextureProps?): TextureData

    suspend fun loadAudioClip(assetPath: String): AudioClip
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
