package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

object Assets : CoroutineScope {

    var assetsBasePath = KoolSystem.config.assetPath
        set(value) {
            field = value
            logI { "Asset base path set to: $value" }
        }

    internal val job = Job()
    private const val NUM_LOAD_WORKERS = 8

    override val coroutineContext: CoroutineContext
        get() = job

    private val awaitedAssetsChannel = Channel<AwaitedAsset>()
    private val assetRefChannel = Channel<AssetRef>(Channel.UNLIMITED)
    private val loadedAssetChannel = Channel<LoadedAsset>()

    private val loadedAtlasFontMaps = mutableMapOf<AtlasFont, FontMap>()

    private val workers = List(NUM_LOAD_WORKERS) { loadWorker(assetRefChannel, loadedAssetChannel) }

    private val loader = launch {
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

    private fun loadWorker(assetRefs: ReceiveChannel<AssetRef>, loadedAssets: SendChannel<LoadedAsset>) = launch {
        for (ref in assetRefs) {
            loadedAssets.send(loadAsset(ref))
        }
    }

    internal fun close() {
        job.cancel()
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset {
        return when(ref) {
            is BlobAssetRef -> PlatformAssets.loadBlob(ref)
            is TextureAssetRef -> PlatformAssets.loadTexture(ref)
            is TextureAtlasAssetRef -> PlatformAssets.loadTextureAtlas(ref)
        }
    }

    /**
     * Suspends until custom fonts are loaded. This is useful in case a loading screen uses some text loading message
     * which cannot be displayed before fonts are loaded.
     */
    suspend fun waitForFonts() {
        PlatformAssets.waitForFonts()
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
        return PlatformAssets.createFontMapData(font, fontScale, outMetrics)
    }

    /**
     * Opens a file chooser dialog for the user to select and load a file. Returns the loaded file or null if the
     * user canceled the dialog or loading failed. On JVM the returned [LoadedFile] also contains the path of the
     * loaded file.
     *
     * @param filterList Optional file filter list (e.g. ("Images", "jpg,png"); only supported on JVM, ignored on js).
     * @return The [LoadedFile] containing the file data or null if the operation was canceled.
     */
    suspend fun loadFileByUser(filterList: List<FileFilterItem> = emptyList()): LoadedFile? {
        return PlatformAssets.loadFileByUser(filterList)
    }

    /**
     * Opens a file chooser dialog for the user to select a destination file for the given data.
     *
     * @return On JVM the selected path is returned or null if the user canceled the operation. On js null is always
     *         returned.
     */
    fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String? = null,
        filterList: List<FileFilterItem> = emptyList(),
        mimeType: String = "application/octet-stream"
    ): String? {
        return PlatformAssets.saveFileByUser(data, defaultFileName, filterList, mimeType)
    }

    fun isHttpAsset(assetPath: String): Boolean =
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true) ||
            assetPath.startsWith("data:", true)

    /**
     * Launches a coroutine in the Assets CoroutineScope and executes the given block from within the [Assets] scope
     * for convenience.
     */
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
    suspend fun loadTextureData(assetPath: String, format: TexFormat? = null): TextureData {
        val ref = TextureAssetRef(assetPath, format)
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
    suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String): TextureData {
        return PlatformAssets.loadTextureDataFromBuffer(texData, mimeType)
    }

    /**
     * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadTextureData2d(imagePath: String, format: TexFormat? = null): TextureData2d {
        return PlatformAssets.loadTextureData2d(imagePath, format)
    }

    /**
     * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     *
     * @throws KoolException if loading failed
     */
    suspend fun loadTextureAtlasData(assetPath: String, tilesX: Int, tilesY: Int, format: TexFormat? = null): TextureData {
        val ref = TextureAtlasAssetRef(assetPath, format, tilesX, tilesY)
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

    suspend fun loadTexture1d(texData: TextureData1d, props: TextureProps = TextureProps(), name: String? = null): Texture1d {
        val tex = Texture1d(props, name) { texData }
        PlatformAssets.uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture2d(texData: TextureData, props: TextureProps = TextureProps(), name: String? = null): Texture2d {
        val tex = Texture2d(props, name) { texData }
        PlatformAssets.uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Texture2d {
        return loadTexture2d(loadTextureData(assetPath), props, trimAssetPath(assetPath))
    }

    suspend fun loadTexture3d(texData: TextureData, props: TextureProps = TextureProps(), name: String? = null): Texture3d {
        val tex = Texture3d(props, name) { texData }
        PlatformAssets.uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadTexture3d(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps = TextureProps()): Texture3d {
        return loadTexture3d(loadTextureAtlasData(assetPath, tilesX, tilesY), props, trimAssetPath(assetPath))
    }

    suspend fun loadCubeMap(texData: TextureDataCube, props: TextureProps = TextureProps(), name: String? = null): TextureCube {
        val tex = TextureCube(props, name) { texData }
        PlatformAssets.uploadTextureToGpu(tex, texData)
        return tex
    }

    suspend fun loadCubeMap(
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
        return loadCubeMap(texData, props, name)
    }

    suspend fun loadAudioClip(assetPath: String): AudioClip {
        return PlatformAssets.loadAudioClip(assetPath)
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
    val fmt: TexFormat?
) : AssetRef(path)

data class TextureAtlasAssetRef(
    val path: String,
    val fmt: TexFormat?,
    val tilesX: Int = 1,
    val tilesY: Int = 1,
) : AssetRef(path)

sealed class LoadedAsset(val ref: AssetRef, val successfull: Boolean)
class LoadedBlobAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)

data class LoadedFile(val path: String?, val data: Uint8Buffer)

data class FileFilterItem(val name: String, val fileExtensions: String)

expect object PlatformAssets {
    internal suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset
    internal suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset
    internal suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset

    internal suspend fun waitForFonts()
    internal fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d

    internal suspend fun loadFileByUser(filterList: List<FileFilterItem>): LoadedFile?
    internal fun saveFileByUser(
        data: Uint8Buffer,
        defaultFileName: String?,
        filterList: List<FileFilterItem>,
        mimeType: String = "application/octet-stream"
    ): String?

    internal suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d
    internal suspend fun loadTextureDataFromBuffer(texData: Uint8Buffer, mimeType: String): TextureData
    internal suspend fun uploadTextureToGpu(texture: Texture, texData: TextureData)

    internal suspend fun loadAudioClip(assetPath: String): AudioClip
}
