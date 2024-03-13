package de.fabmax.kool

import de.fabmax.kool.Assets.loadTextureData
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logD
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select

abstract class AssetLoader {

    private val assetRefChannel = Channel<AssetRef>(Channel.UNLIMITED)
    private val loadedAssetChannel = Channel<LoadedAsset>()
    private val awaitedAssetsChannel = Channel<AwaitedAsset>()
        get() {
            // start loader coroutines the first time an asset is requested
            loadController
            return field
        }

    private val loadController by lazy {
        Assets.launch {
            @Suppress("UNUSED_VARIABLE")
            val workers = List(NUM_LOAD_WORKERS) { loadWorker(assetRefChannel, loadedAssetChannel) }
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
    }

    private fun loadWorker(assetRefs: ReceiveChannel<AssetRef>,loadedAssets: SendChannel<LoadedAsset>) = Assets.launch {
        for (ref in assetRefs) {
            loadedAssets.send(loadAsset(ref))
        }
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset {
        return when(ref) {
            is BlobAssetRef -> loadBlob(ref)
            is TextureAssetRef -> loadTexture(ref)
            is TextureAtlasAssetRef -> loadTextureAtlas(ref)
            is TextureData2dRef -> loadTextureData2d(ref)
            is AudioClipRef -> loadAudioClip(ref)
        }
    }

    protected abstract suspend fun loadBlob(blobRef: BlobAssetRef): LoadedBlobAsset
    protected abstract suspend fun loadTexture(textureRef: TextureAssetRef): LoadedTextureAsset
    protected abstract suspend fun loadTextureAtlas(textureRef: TextureAtlasAssetRef): LoadedTextureAsset
    protected abstract suspend fun loadTextureData2d(textureData2dRef: TextureData2dRef): LoadedTextureAsset
    protected abstract suspend fun loadAudioClip(audioRef: AudioClipRef): LoadedAudioClipAsset

    ////////////////////////////////////////////////////////////////////
    // Texture Data
    ////////////////////////////////////////////////////////////////////

    /**
     * Asynchronously loads the texture data at the given path and returns it as [TextureData].
     */
    fun loadTextureDataAsync(assetPath: String, props: TextureProps? = null): Deferred<TextureData> = Assets.async {
        val ref = TextureAssetRef(assetPath, props)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        loaded.data ?: error("Failed loading texture ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads the texture data at the given path and returns it as [TextureData].
     */
    suspend fun loadTextureData(assetPath: String, props: TextureProps? = null): TextureData {
        return loadTextureDataAsync(assetPath, props).await()
    }

    /**
     * Similar to [loadTextureDataAsync], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     */
    fun loadTextureData2dAsync(assetPath: String, props: TextureProps? = null): Deferred<TextureData2d> = Assets.async {
        val ref = TextureData2dRef(assetPath, props)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height})" }
        }
        loaded.data as TextureData2d? ?: error("Failed loading texture ${trimAssetPath(assetPath)}")
    }

    /**
     * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     */
    suspend fun loadTextureData2d(assetPath: String, props: TextureProps? = null): TextureData2d {
        return loadTextureData2dAsync(assetPath, props).await()
    }

    /**
     * Asynchronously loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     */
    fun loadTextureDataAtlasAsync(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps? = null
    ): Deferred<TextureData> = Assets.async {
        val ref = TextureAtlasAssetRef(assetPath, props, tilesX, tilesY)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.data?.let {
            logD { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}x${it.depth})" }
        }
        loaded.data ?: error("Failed loading texture atlas ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
     * image tiles. The texture atlas data is returned as [TextureData].
     */
    suspend fun loadTextureDataAtlas(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): TextureData {
        return loadTextureDataAtlasAsync(assetPath, tilesX, tilesY, props).await()
    }

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
    ): Deferred<TextureDataCube> = Assets.async {
        val ftd = loadTextureDataAsync(pathFront).await()
        val bkd = loadTextureDataAsync(pathBack).await()
        val ltd = loadTextureDataAsync(pathLeft).await()
        val rtd = loadTextureDataAsync(pathRight).await()
        val upd = loadTextureDataAsync(pathUp).await()
        val dnd = loadTextureDataAsync(pathDown).await()
        TextureDataCube(ftd, bkd, ltd, rtd, upd, dnd)
    }

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
    ): TextureDataCube = loadTextureDataCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown).await()

    ////////////////////////////////////////////////////////////////////
    // Prepared Textures
    ////////////////////////////////////////////////////////////////////

    fun loadTexture2dAsync(assetPath: String, props: TextureProps = TextureProps()): Deferred<Texture2d> = Assets.async {
        Assets.loadTexture2d(loadTextureDataAsync(assetPath, props).await(), props, trimAssetPath(assetPath))
    }

    suspend fun loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Texture2d {
        return loadTexture2dAsync(assetPath, props).await()
    }

    fun loadTexture3dAsync(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Deferred<Texture3d> = Assets.async {
        val layers = loadTextureDataAtlas(assetPath, tilesX, tilesY)
        Assets.loadTexture3d(layers, props, trimAssetPath(assetPath))
    }

    suspend fun loadTexture3d(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Texture3d = loadTexture3dAsync(assetPath, tilesX, tilesY, props).await()

    fun loadTextureCubeAsync(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): Deferred<TextureCube> = Assets.async {
        val name = trimCubeMapAssetPath(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
        val texData = loadTextureDataCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown).await()
        Assets.loadTextureCube(texData, props, name)
    }

    suspend fun loadTextureCube(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): TextureCube = loadTextureCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props).await()

    ////////////////////////////////////////////////////////////////////
    // Non-texture assets
    ////////////////////////////////////////////////////////////////////

    /**
     * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    fun loadBlobAssetAsync(assetPath: String): Deferred<Uint8Buffer> = Assets.async {
        val ref = BlobAssetRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedBlobAsset
        if (loaded.data != null) {
            logD { "Loaded ${trimAssetPath(assetPath)} (${(loaded.data.capacity / 1_048_576.0).toString(1)} mb)" }
        }
        loaded.data ?: error("Failed loading blob asset ${trimAssetPath(assetPath)}")
    }

    /**
     * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    suspend fun loadBlobAsset(assetPath: String): Uint8Buffer = loadBlobAssetAsync(assetPath).await()

    fun loadAudioClipAsync(assetPath: String): Deferred<AudioClip> = Assets.async {
        val ref = AudioClipRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedAudioClipAsset
        loaded.data ?: error("Failed loading audio clip ${trimAssetPath(assetPath)}")
    }

    suspend fun loadAudioClip(assetPath: String,): AudioClip = loadAudioClipAsync(assetPath).await()

    ////////////////////////////////////////////////////////////////////
    // Helper stuff
    ////////////////////////////////////////////////////////////////////

    private fun trimCubeMapAssetPath(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): String {
        return "cubeMap(ft:${trimAssetPath(ft)}, bk:${trimAssetPath(bk)}, lt:${trimAssetPath(lt)}, rt:${trimAssetPath(rt)}, up:${trimAssetPath(up)}, dn:${trimAssetPath(dn)})"
    }

    private fun trimAssetPath(assetPath: String): String {
        return if (assetPath.startsWith("data:", true)) {
            val idx = assetPath.indexOf(';')
            assetPath.substring(0 until idx)
        } else {
            assetPath
        }
    }

    private class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset> = CompletableDeferred(Assets.job))

    companion object {
        private const val NUM_LOAD_WORKERS = 8
    }
}

sealed class AssetRef(path: String) {
    val isHttp: Boolean = Assets.isHttpAsset(path)
}

data class BlobAssetRef(
    val path: String
) : AssetRef(path)

data class TextureAssetRef(
    val path: String,
    val props: TextureProps?
) : AssetRef(path)

data class TextureAtlasAssetRef(
    val path: String,
    val props: TextureProps?,
    val tilesX: Int = 1,
    val tilesY: Int = 1
) : AssetRef(path)

data class TextureData2dRef(
    val path: String,
    val props: TextureProps?
) : AssetRef(path)

data class AudioClipRef(
    val path: String
) : AssetRef(path)

sealed class LoadedAsset(val ref: AssetRef, val successful: Boolean)
class LoadedBlobAsset(ref: AssetRef, val data: Uint8Buffer?) : LoadedAsset(ref, data != null)
class LoadedTextureAsset(ref: AssetRef, val data: TextureData?) : LoadedAsset(ref, data != null)
class LoadedAudioClipAsset(ref: AssetRef, val data: AudioClip?) : LoadedAsset(ref, data != null)
