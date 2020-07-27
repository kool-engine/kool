package de.fabmax.kool

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
            is LocalRawAssetRef -> loadLocalRaw(ref)
            is HttpRawAssetRef -> loadHttpRaw(ref)
            is LocalTextureAssetRef -> loadLocalTexture(ref)
            is HttpTextureAssetRef -> loadHttpTexture(ref)
        }
    }

    protected abstract suspend fun loadLocalRaw(localRawRef: LocalRawAssetRef): LoadedRawAsset

    protected abstract suspend fun loadHttpRaw(httpRawRef: HttpRawAssetRef): LoadedRawAsset

    protected abstract suspend fun loadLocalTexture(localTextureRef: LocalTextureAssetRef): LoadedTextureAsset

    protected abstract suspend fun loadHttpTexture(httpTextureRef: HttpTextureAssetRef): LoadedTextureAsset

    abstract fun createCharMap(fontProps: FontProps): CharMap

    abstract fun inflate(zipData: Uint8Buffer): Uint8Buffer

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

    suspend fun loadAsset(assetPath: String): Uint8Buffer? {
        val ref = if (isHttpAsset(assetPath)) {
            HttpRawAssetRef(assetPath)
        } else {
            LocalRawAssetRef("$assetsBaseDir/$assetPath")
        }
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedRawAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${(it.capacity / 1024.0 / 1024.0).toString(1)} mb)" }
        }
        return loaded.data
    }

    suspend fun loadTextureData(assetPath: String): TextureData {
        val ref = if (isHttpAsset(assetPath)) {
            HttpTextureAssetRef(assetPath)
        } else {
            LocalTextureAssetRef("$assetsBaseDir/$assetPath")
        }
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${assetPathToName(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        return loaded.data ?: throw KoolException("Failed loading texture")
    }

    open suspend fun loadCubeMapTextureData(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): CubeMapTextureData {
        val ftd = loadTextureData(ft)
        val bkd = loadTextureData(bk)
        val ltd = loadTextureData(lt)
        val rtd = loadTextureData(rt)
        val upd = loadTextureData(up)
        val dnd = loadTextureData(dn)
        return CubeMapTextureData(ftd, bkd, ltd, rtd, upd, dnd)
    }

    abstract suspend fun createTextureData(texData: Uint8Buffer, mimeType: String): TextureData

    abstract suspend fun loadAndPrepareTexture(assetPath: String, props: TextureProps = TextureProps()): Texture

    abstract suspend fun loadAndPrepareCubeMap(ft: String, bk: String, lt: String, rt: String, up: String, dn: String,
                                       props: TextureProps = TextureProps()): CubeMapTexture

    abstract fun loadAndPrepareTexture(texData: TextureData, props: TextureProps = TextureProps(), name: String? = null): Texture

    abstract fun loadAndPrepareCubeMap(texData: CubeMapTextureData, props: TextureProps = TextureProps(), name: String? = null): CubeMapTexture

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
data class LocalRawAssetRef(val url: String) : AssetRef()
data class HttpRawAssetRef(val url: String) : AssetRef()
data class LocalTextureAssetRef(val url: String) : AssetRef()
data class HttpTextureAssetRef(val url: String) : AssetRef()

sealed class LoadedAsset(val ref: AssetRef, val successfull: Boolean)
class LoadedRawAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)
