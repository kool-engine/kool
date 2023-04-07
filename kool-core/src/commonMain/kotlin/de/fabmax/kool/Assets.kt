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

    fun close() {
        job.cancel()
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset {
        return when(ref) {
            is RawAssetRef -> PlatformAssets.loadRaw(ref)
            is TextureAssetRef -> PlatformAssets.loadTexture(ref)
        }
    }

    suspend fun waitForFonts() {
        PlatformAssets.waitForFonts()
    }

    fun getOrCreateFontMap(font: AtlasFont, fontScale: Float): FontMap = loadedAtlasFontMaps.getOrPut(font) {
        updateFontMap(font, fontScale)
    }

    fun updateFontMap(font: AtlasFont, fontScale: Float): FontMap {
        var map = font.map
        val metrics = mutableMapOf<Char, CharMetrics>()
        val texData = createFontMapData(font, fontScale, metrics)

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
                logE { "Unable to update texture data of font ${font}" }
            }
        }
        return map
    }

    fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d {
        return PlatformAssets.createFontMapData(font, fontScale, outMetrics)
    }

    /**
     * Opens a file chooser dialog for the user to select and load a file. Returns the loaded file or null if the
     * user canceled the dialog or loading failed. On JVM the returned [LoadedFile] also contains the path of the
     * loaded file.
     *
     * @param filterList Optional file filter list as comma-separated string of file extensions (e.g. "jpg,png"; only
     *                   supported on JVM, ignored on js).
     * @return The [LoadedFile] containing the file data or null if the operation was canceled.
     */
    suspend fun loadFileByUser(filterList: String? = null): LoadedFile? {
        return PlatformAssets.loadFileByUser(filterList)
    }

    /**
     * Opens a file chooser dialog for the user to select a destination file for the given data.
     *
     * @return On JVM the selected path is returned or null if the suer canceled the operation. On js null is always
     *         returned.
     */
    fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String = "application/octet-stream"): String? {
        return PlatformAssets.saveFileByUser(data, fileName, mimeType)
    }

    fun isHttpAsset(assetPath: String): Boolean =
            // maybe use something less naive here?
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true) ||
            assetPath.startsWith("data:", true)

    fun launch(block: suspend Assets.() -> Unit) {
        (this as CoroutineScope).launch {
            block.invoke(this@Assets)
        }
    }

    fun makeAssetRef(assetPath: String): RawAssetRef {
        return RawAssetRef(assetPath, !isHttpAsset(assetPath))
    }

    suspend fun loadAsset(assetPath: String): Uint8Buffer? {
        val ref = makeAssetRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedRawAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${(it.capacity / 1024.0 / 1024.0).toString(1)} mb)" }
        }
        return loaded.data
    }

    suspend fun loadTextureData(assetPath: String, format: TexFormat? = null): TextureData {
        val ref = TextureAssetRef(assetPath, !isHttpAsset(assetPath), format, false)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture")
    }

    /**
     * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful to do procedural stuff like building heightmap geometry
     * from a greyscale image.
     */
    suspend fun loadTextureData2d(imagePath: String, format: TexFormat? = null): TextureData2d {
        return PlatformAssets.loadTextureData2d(imagePath, format)
    }

    suspend fun loadTextureAtlasData(assetPath: String, tilesX: Int, tilesY: Int, format: TexFormat? = null): TextureData {
        val ref = TextureAssetRef(assetPath, !isHttpAsset(assetPath), format, true, tilesX, tilesY)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${it.format}, ${it.width}x${it.height}x${it.depth})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture")
    }

    suspend fun loadCubeMapTextureData(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): TextureDataCube {
        val ftd = loadTextureData(ft)
        val bkd = loadTextureData(bk)
        val ltd = loadTextureData(lt)
        val rtd = loadTextureData(rt)
        val upd = loadTextureData(up)
        val dnd = loadTextureData(dn)
        return TextureDataCube(ftd, bkd, ltd, rtd, upd, dnd)
    }

    suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData {
        return PlatformAssets.createTextureData(texData, mimeType)
    }

    suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps = TextureProps()): Texture2d {
        return PlatformAssets.loadAndPrepareTexture(assetPath, props)
    }

    suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                      props: TextureProps = TextureProps()): TextureCube {
        return PlatformAssets.loadAndPrepareCubeMap(ft, bk, lt, rt, up, dn, props)
    }

    suspend fun loadAndPrepareTexture(texData: TextureData, props: TextureProps = TextureProps(), name: String? = null): Texture2d {
        return PlatformAssets.loadAndPrepareTexture(texData, props, name)
    }

    suspend fun loadAndPrepareCubeMap(texData: TextureDataCube, props: TextureProps = TextureProps(), name: String? = null): TextureCube {
        return PlatformAssets.loadAndPrepareCubeMap(texData, props, name)
    }

    suspend fun loadAudioClip(assetPath: String): AudioClip {
        return PlatformAssets.loadAudioClip(assetPath)
    }

    fun assetPathToName(assetPath: String): String {
        return if (assetPath.startsWith("data:", true)) {
            val idx = assetPath.indexOf(';')
            assetPath.substring(0 until idx)
        } else {
            assetPath
        }
    }

    fun cubeMapAssetPathToName(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): String {
        return "cubeMap(ft:${assetPathToName(ft)}, bk:${assetPathToName(bk)}, lt:${assetPathToName(lt)}, rt:${assetPathToName(rt)}, up:${assetPathToName(up)}, dn:${assetPathToName(dn)})"
    }

    private class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset> = CompletableDeferred(job))
}

sealed class AssetRef
data class RawAssetRef(val url: String, val isLocal: Boolean) : AssetRef()
data class TextureAssetRef(val url: String, val isLocal: Boolean, val fmt: TexFormat?, val isAtlas: Boolean, val tilesX: Int = 1, val tilesY: Int = 1) : AssetRef()

sealed class LoadedAsset(val ref: AssetRef, val successfull: Boolean)
class LoadedRawAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)

data class LoadedFile(val path: String?, val data: Uint8Buffer)

expect object PlatformAssets {
    internal suspend fun loadRaw(rawRef: RawAssetRef): LoadedRawAsset
    internal suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset

    internal suspend fun waitForFonts()
    internal fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d

    internal suspend fun loadFileByUser(filterList: String?): LoadedFile?
    internal fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String = "application/octet-stream"): String?

    internal suspend fun loadTextureData2d(imagePath: String, format: TexFormat?): TextureData2d
    internal suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData

    internal suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps): Texture2d
    internal suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                      props: TextureProps): TextureCube
    internal suspend fun loadAndPrepareTexture(texData: TextureData, props: TextureProps, name: String?): Texture2d
    internal suspend fun loadAndPrepareCubeMap(texData: TextureDataCube, props: TextureProps, name: String?): TextureCube

    internal suspend fun loadAudioClip(assetPath: String): AudioClip
}
