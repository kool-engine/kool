package de.fabmax.kool

import de.fabmax.kool.gl.GL_CLAMP_TO_EDGE
import de.fabmax.kool.gl.GL_LINEAR
import de.fabmax.kool.gl.GL_TEXTURE_CUBE_MAP
import de.fabmax.kool.util.CharMap
import de.fabmax.kool.util.FontProps
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.serialization.ModelData
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

    private val loader = loader(awaitedAssetsChannel, assetRefChannel, loadedAssetChannel)
    private val workers = List(NUM_LOAD_WORKERS) { loadWorker(assetRefChannel, loadedAssetChannel) }

    open fun close() {
        job.cancel()
    }

    private fun loader(awaitedAssets: ReceiveChannel<AwaitedAsset>, assetRefs: SendChannel<AssetRef>, loadedAssets: ReceiveChannel<LoadedAsset>) = launch {
        val requested = mutableMapOf<AssetRef, MutableList<AwaitedAsset>>()
        while (true) {
            select<Unit> {
                awaitedAssets.onReceive { awaited ->
                    val awaiting = requested[awaited.ref]
                    if (awaiting == null) {
                        requested[awaited.ref] = mutableListOf(awaited)
                        assetRefs.send(awaited.ref)
                    } else {
                        awaiting.add(awaited)
                    }
                }
                loadedAssets.onReceive { loaded ->
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

    abstract fun inflate(zipData: ByteArray): ByteArray

    protected open fun isHttpAsset(assetPath: String): Boolean =
            // maybe use something less naive here?
            assetPath.startsWith("http://", true) ||
            assetPath.startsWith("https://", true)

    fun loadAsset(assetPath: String, onLoad: (ByteArray?) -> Unit) {
        launch {
            val ref = if (isHttpAsset(assetPath)) {
                HttpRawAssetRef(assetPath)
            } else {
                LocalRawAssetRef("$assetsBaseDir/$assetPath")
            }
            val awaitedAsset = AwaitedAsset(ref)
            awaitedAssetsChannel.send(awaitedAsset)
            val loaded = awaitedAsset.awaiting.await() as LoadedRawAsset
            onLoad(loaded.data)
        }
    }

    fun loadModel(modelPath: String, onLoad: (ModelData?) -> Unit) {
        loadAsset(modelPath) { loadedData ->
            val model: ModelData? = if (loadedData == null) {
                logE { "Failed loading model $modelPath" }
                null
            } else {
                val data = if (modelPath.endsWith(".kmfz", true)) inflate(loadedData) else loadedData
                ModelData.load(data)
            }
            onLoad(model)
        }
    }

    fun loadTextureAsset(assetPath: String): TextureData  {
        val proxy = TextureDataProxy()
        launch {
            val ref = if (isHttpAsset(assetPath)) {
                HttpTextureAssetRef(assetPath)
            } else {
                LocalTextureAssetRef("$assetsBaseDir/$assetPath")
            }
            val awaitedAsset = AwaitedAsset(ref)
            awaitedAssetsChannel.send(awaitedAsset)
            val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
            proxy.proxyData = loaded.data
        }
        return proxy
    }

    private inner class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset> = CompletableDeferred(job))

    private class TextureDataProxy : TextureData() {
        var proxyData: TextureData? = null

        override val isAvailable: Boolean
            get() = proxyData?.isAvailable ?: false
        override var width: Int
            get() = proxyData?.width ?: 0
            set(_) {}
        override var height: Int
            get() = proxyData?.height ?: 0
            set(_) {}

        override fun onLoad(texture: Texture, target: Int, ctx: KoolContext) = proxyData!!.onLoad(texture, target, ctx)
    }

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
class LoadedRawAsset(ref: AssetRef, val data: ByteArray?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)

fun assetTexture(assetPath: String, delayLoading: Boolean = true): Texture {
    return assetTexture(defaultProps(assetPath), delayLoading)
}

fun assetTexture(props: TextureProps, delayLoading: Boolean = true): Texture {
    return Texture(props) { ctx ->
        this.delayLoading = delayLoading
        ctx.assetMgr.loadTextureAsset(props.id)
    }
}

fun assetTextureCubeMap(frontPath: String, backPath: String, leftPath: String, rightPath: String, upPath: String,
                        downPath: String, delayLoading: Boolean = true): CubeMapTexture {
    val id = "$frontPath-$backPath-$leftPath-$rightPath-$upPath-$downPath"
    val props = TextureProps(id, GL_LINEAR, GL_LINEAR, GL_CLAMP_TO_EDGE, GL_CLAMP_TO_EDGE, 0, GL_TEXTURE_CUBE_MAP)
    return CubeMapTexture(props) { ctx ->
        this.delayLoading = delayLoading
        val ft = ctx.assetMgr.loadTextureAsset(frontPath)
        val bk = ctx.assetMgr.loadTextureAsset(backPath)
        val lt = ctx.assetMgr.loadTextureAsset(leftPath)
        val rt = ctx.assetMgr.loadTextureAsset(rightPath)
        val up = ctx.assetMgr.loadTextureAsset(upPath)
        val dn = ctx.assetMgr.loadTextureAsset(downPath)
        CubeMapTextureData(ft, bk, lt, rt, up, dn)
    }
}
