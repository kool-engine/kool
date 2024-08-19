package de.fabmax.kool

import de.fabmax.kool.Assets.loadTextureData
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
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
    private val loadedAssetChannel = Channel<LoadedAsset<*>>()
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

    private fun loadWorker(assetRefs: ReceiveChannel<AssetRef>,loadedAssets: SendChannel<LoadedAsset<*>>) = Assets.launch {
        for (ref in assetRefs) {
            loadedAssets.send(loadAsset(ref))
        }
    }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset<*> {
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
    fun loadTextureDataAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<TextureData>> = Assets.async {
        val ref = TextureAssetRef(assetPath, props)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        loaded.result
            .onSuccess {
                logD("AssetLoader.loadTextureDataAsync") { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height})" }
            }
            .onFailure {
                logE("AssetLoader.loadTextureDataAsync") { "Failed loading ${trimAssetPath(assetPath)}: $it" }
            }
    }

    /**
     * Similar to [loadTextureDataAsync], but returns the image data as [TextureData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     */
    fun loadTextureData2dAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<TextureData2d>> = Assets.async {
        val ref = TextureData2dRef(assetPath, props)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset
        try {
            val texData = loaded.result.getOrThrow() as TextureData2d
            logD("AssetLoader.loadTextureData2dAsync") { "Loaded ${trimAssetPath(assetPath)} (${texData.format}, ${texData.width}x${texData.height})" }
            Result.success(texData)
        } catch (t: Throwable) {
            logE("AssetLoader.loadTextureData2dAsync") { "Failed loading ${trimAssetPath(assetPath)}: $t" }
            Result.failure(t)
        }
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
    ): Deferred<Result<TextureData>> = Assets.async {
        val ref = TextureAtlasAssetRef(assetPath, props, tilesX, tilesY)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedTextureAsset

        loaded.result
            .onSuccess {
                logD("AssetLoader.loadTextureDataAtlasAsync") { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}x${it.depth})" }
            }
            .onFailure {
                logE("AssetLoader.loadTextureDataAtlasAsync") { "Failed loading ${trimAssetPath(assetPath)}: $it" }
            }
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
    ): Deferred<Result<TextureDataCube>> = Assets.async {
        try {
            val ftd = loadTextureDataAsync(pathFront).await().getOrThrow()
            val bkd = loadTextureDataAsync(pathBack).await().getOrThrow()
            val ltd = loadTextureDataAsync(pathLeft).await().getOrThrow()
            val rtd = loadTextureDataAsync(pathRight).await().getOrThrow()
            val upd = loadTextureDataAsync(pathUp).await().getOrThrow()
            val dnd = loadTextureDataAsync(pathDown).await().getOrThrow()
            Result.success(TextureDataCube(ftd, bkd, ltd, rtd, upd, dnd))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    ////////////////////////////////////////////////////////////////////
    // Prepared Textures
    ////////////////////////////////////////////////////////////////////

    fun loadTexture2dAsync(assetPath: String, props: TextureProps = TextureProps()): Deferred<Result<Texture2d>> = Assets.async {
        loadTextureData(assetPath, props).map { Assets.loadTexture2d(it, props, trimAssetPath(assetPath)) }
    }

    fun loadTexture3dAsync(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        props: TextureProps = TextureProps()
    ): Deferred<Result<Texture3d>> = Assets.async {
        loadTextureDataAtlas(assetPath, tilesX, tilesY).map { Assets.loadTexture3d(it, props, trimAssetPath(assetPath)) }
    }

    fun loadTextureCubeAsync(
        pathFront: String,
        pathBack: String,
        pathLeft: String,
        pathRight: String,
        pathUp: String,
        pathDown: String,
        props: TextureProps = TextureProps()
    ): Deferred<Result<TextureCube>> = Assets.async {
        loadTextureDataCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown).map {
            val name = trimCubeMapAssetPath(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
            Assets.loadTextureCube(it, props, name)
        }
    }

    ////////////////////////////////////////////////////////////////////
    // Non-texture assets
    ////////////////////////////////////////////////////////////////////

    /**
     * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    fun loadBlobAssetAsync(assetPath: String): Deferred<Result<Uint8Buffer>> = Assets.async {
        val ref = BlobAssetRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedBlobAsset
        loaded.result
            .onSuccess {
                logD("AssetLoader.loadBlobAssetAsync") { "Loaded blob ${trimAssetPath(assetPath)} (${(it.capacity / 1_048_576.0).toString(1)} mb)" }
            }
            .onFailure {
                logE("AssetLoader.loadBlobAssetAsync") { "Failed loading blob ${trimAssetPath(assetPath)}: $it" }
            }
    }

    fun loadAudioClipAsync(assetPath: String): Deferred<Result<AudioClip>> = Assets.async {
        val ref = AudioClipRef(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        awaitedAssetsChannel.send(awaitedAsset)
        val loaded = awaitedAsset.awaiting.await() as LoadedAudioClipAsset
        loaded.result
            .onSuccess {
                logD("AssetLoader.loadAudioClipAsync") { "Loaded audio ${trimAssetPath(assetPath)} (${it.duration} secs)" }
            }
            .onFailure {
                logE("AssetLoader.loadAudioClipAsync") { "Failed loading audio ${trimAssetPath(assetPath)}: $it" }
            }
    }

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

    private class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset<*>> = CompletableDeferred(Assets.job))

    companion object {
        private const val NUM_LOAD_WORKERS = 8

        val textureDataLoadFailed = TextureData2d.singleColor(Color.MAGENTA)
    }
}

/**
 * Loads the texture data at the given path and returns it as [TextureData].
 */
suspend fun AssetLoader.loadTextureData(assetPath: String, props: TextureProps? = null): Result<TextureData> =
    loadTextureDataAsync(assetPath, props).await()

/**
 * Similar to [loadTextureData], but returns the image data as [TextureData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 */
suspend fun AssetLoader.loadTextureData2d(assetPath: String, props: TextureProps? = null): Result<TextureData2d> =
    loadTextureData2dAsync(assetPath, props).await()

/**
 * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [TextureData].
 */
suspend fun AssetLoader.loadTextureDataAtlas(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): Result<TextureData> =
    loadTextureDataAtlasAsync(assetPath, tilesX, tilesY, props).await()

/**
 * Loads a cube map from the given image paths (one for each side).
 */
suspend fun AssetLoader.loadTextureDataCube(
    pathFront: String,
    pathBack: String,
    pathLeft: String,
    pathRight: String,
    pathUp: String,
    pathDown: String
): Result<TextureDataCube> = loadTextureDataCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown).await()

suspend fun AssetLoader.loadTexture3d(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    props: TextureProps = TextureProps()
): Result<Texture3d> = loadTexture3dAsync(assetPath, tilesX, tilesY, props).await()

suspend fun AssetLoader.loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Result<Texture2d> =
    loadTexture2dAsync(assetPath, props).await()

suspend fun AssetLoader.loadTextureCube(
    pathFront: String,
    pathBack: String,
    pathLeft: String,
    pathRight: String,
    pathUp: String,
    pathDown: String,
    props: TextureProps = TextureProps()
): Result<TextureCube> = loadTextureCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props).await()

/**
 * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
 */
suspend fun AssetLoader.loadBlobAsset(assetPath: String): Result<Uint8Buffer> = loadBlobAssetAsync(assetPath).await()

suspend fun AssetLoader.loadAudioClip(assetPath: String,): Result<AudioClip> = loadAudioClipAsync(assetPath).await()
