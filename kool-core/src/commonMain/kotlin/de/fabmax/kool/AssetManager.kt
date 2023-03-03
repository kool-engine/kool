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

abstract class AssetManager : CoroutineScope {

    protected val job = Job()

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

    abstract val storage: KeyValueStorage

    private fun loadWorker(assetRefs: ReceiveChannel<AssetRef>, loadedAssets: SendChannel<LoadedAsset>) = launch {
        for (ref in assetRefs) {
            loadedAssets.send(loadAsset(ref))
        }
    }

    open fun close() {
        job.cancel()
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset {
        return when(ref) {
            is RawAssetRef -> loadRaw(ref)
            is TextureAssetRef -> loadTexture(ref)
        }
    }

    protected abstract suspend fun loadRaw(rawRef: RawAssetRef): LoadedRawAsset

    protected abstract suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset

    abstract suspend fun waitForFonts()

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

    abstract fun createFontMapData(font: AtlasFont, fontScale: Float, outMetrics: MutableMap<Char, CharMetrics>): TextureData2d

    abstract suspend fun loadFileByUser(filterList: String? = null): LoadedFile

    abstract fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String = "application/octet-stream"): String?

    protected open fun isHttpAsset(assetPath: String): Boolean =
            // maybe use something less naive here?
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true) ||
            assetPath.startsWith("data:", true)

    fun launch(block: suspend AssetManager.() -> Unit) {
        (this as CoroutineScope).launch {
            block.invoke(this@AssetManager)
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
    abstract suspend fun loadTextureData2d(imagePath: String, format: TexFormat? = null): TextureData2d

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

    open suspend fun loadCubeMapTextureData(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): TextureDataCube {
        val ftd = loadTextureData(ft)
        val bkd = loadTextureData(bk)
        val ltd = loadTextureData(lt)
        val rtd = loadTextureData(rt)
        val upd = loadTextureData(up)
        val dnd = loadTextureData(dn)
        return TextureDataCube(ftd, bkd, ltd, rtd, upd, dnd)
    }

    abstract suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData

    abstract suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps = TextureProps()): Texture2d

    abstract suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                       props: TextureProps = TextureProps()): TextureCube

    abstract suspend fun loadAndPrepareTexture(texData: TextureData, props: TextureProps = TextureProps(), name: String? = null): Texture2d

    abstract suspend fun loadAndPrepareCubeMap(texData: TextureDataCube, props: TextureProps = TextureProps(), name: String? = null): TextureCube

    abstract suspend fun loadAudioClip(assetPath: String): AudioClip

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

    protected inner class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset> = CompletableDeferred(job))

    companion object {
        const val NUM_LOAD_WORKERS = 8
    }
}

sealed class AssetRef
data class RawAssetRef(val url: String, val isLocal: Boolean) : AssetRef()
data class TextureAssetRef(val url: String, val isLocal: Boolean, val fmt: TexFormat?, val isAtlas: Boolean, val tilesX: Int = 1, val tilesY: Int = 1) : AssetRef()

sealed class LoadedAsset(val ref: AssetRef, val successfull: Boolean)
class LoadedRawAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)
data class LoadedFile(val path: String?, val data: Uint8Buffer?)
