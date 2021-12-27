package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.coroutines.CoroutineContext

abstract class AssetManager(var assetsBaseDir: String) : CoroutineScope {

    protected val job = Job()

    override val coroutineContext: CoroutineContext
        get() = job

    private val awaitedAssetsChannel = Channel<AwaitedAsset>()
    private val assetRefChannel = Channel<AssetRef>(Channel.UNLIMITED)
    private val loadedAssetChannel = Channel<LoadedAsset>()

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

    abstract fun createCharMap(fontProps: FontProps): CharMap

    abstract fun inflate(zipData: Uint8Buffer): Uint8Buffer

    abstract fun deflate(data: Uint8Buffer): Uint8Buffer

    abstract suspend fun loadFileByUser(): Uint8Buffer?

    abstract fun saveFileByUser(data: Uint8Buffer, fileName: String, mimeType: String = "application/octet-stream")

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
        return if (isHttpAsset(assetPath)) {
            RawAssetRef(assetPath, false)
        } else {
            RawAssetRef("$assetsBaseDir/$assetPath", true)
        }
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
        val ref = if (isHttpAsset(assetPath)) {
            TextureAssetRef(assetPath, false, format, false)
        } else {
            TextureAssetRef("$assetsBaseDir/$assetPath", true, format, false)
        }
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture")
    }

    suspend fun loadTextureAtlasData(assetPath: String, tilesX: Int, tilesY: Int, format: TexFormat? = null): TextureData {
        val ref = if (isHttpAsset(assetPath)) {
            TextureAssetRef(assetPath, false, format, true, tilesX, tilesY)
        } else {
            TextureAssetRef("$assetsBaseDir/$assetPath", true, format, true, tilesX, tilesY)
        }
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
