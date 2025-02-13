package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.logE
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlin.time.measureTimedValue

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

    private fun loadWorker(assetRefs: ReceiveChannel<AssetRef>, loadedAssets: SendChannel<LoadedAsset<*>>) =
        Assets.launch {
            for (ref in assetRefs) {
                loadedAssets.send(loadAsset(ref))
            }
        }

    private suspend fun loadAsset(ref: AssetRef): LoadedAsset<*> {
        return when(ref) {
            is AssetRef.Audio -> loadAudio(ref)
            is AssetRef.Blob -> loadBlob(ref)
            is AssetRef.BufferedImage2d -> loadBufferedImage2d(ref)
            is AssetRef.Image2d -> loadImage2d(ref)
            is AssetRef.ImageAtlas -> loadImageAtlas(ref)
        }
    }

    protected abstract suspend fun loadAudio(ref: AssetRef.Audio): LoadedAsset.Audio
    protected abstract suspend fun loadBlob(ref: AssetRef.Blob): LoadedAsset.Blob
    protected abstract suspend fun loadBufferedImage2d(ref: AssetRef.BufferedImage2d): LoadedAsset.BufferedImage2d
    protected abstract suspend fun loadImage2d(ref: AssetRef.Image2d): LoadedAsset.Image2d
    protected abstract suspend fun loadImageAtlas(ref: AssetRef.ImageAtlas): LoadedAsset.ImageAtlas

    /**
     * Asynchronously loads the image data at the given path and returns it as [ImageData].
     */
    suspend fun loadImage2d(
        assetPath: String,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): Result<ImageData2d> {
        val ref = AssetRef.Image2d(assetPath, format, resolveSize)
        val awaitedAsset = AwaitedAsset(ref)
        val (loaded, time) = measureTimedValue {
            awaitedAssetsChannel.send(awaitedAsset)
            awaitedAsset.awaiting.await() as LoadedAsset.Image2d
        }
        return loaded.result
            .onSuccess {
                logD("AssetLoader.loadImage2d") { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}, $time)" }
            }
            .onFailure {
                logE("AssetLoader.loadImage2d") { "Failed loading ${trimAssetPath(assetPath)}: $it" }
            }
    }

    /**
     * Similar to [loadImage2d], but returns the image data as [BufferedImageData2d] object, which stores the pixels
     * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
     * building heightmap geometry from a greyscale image.
     * Notice that [BufferedImageData2d] extends [ImageData2d] and hence it's possible to always use this method
     * instead of [loadImage2d]. However, loading textures from the non-buffered [ImageData2d] returned by
     * [loadImage2d] can be faster on some platforms (especially on JS).
     */
    suspend fun loadBufferedImage2d(
        assetPath: String,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): Result<BufferedImageData2d> {
        val ref = AssetRef.BufferedImage2d(assetPath, format, resolveSize)
        val awaitedAsset = AwaitedAsset(ref)
        val (loaded, time) = measureTimedValue {
            awaitedAssetsChannel.send(awaitedAsset)
            awaitedAsset.awaiting.await() as LoadedAsset.BufferedImage2d
        }
        return loaded.result
            .onSuccess {
                logD("AssetLoader.loadBufferedImage2d") { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}, $time)" }
            }
            .onFailure {
                logE("AssetLoader.loadBufferedImage2d") { "Failed loading ${trimAssetPath(assetPath)}: $it" }
            }
    }

    /**
     * Asynchronously loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY]
     * individual image tiles. The texture atlas data is returned as [ImageData].
     */
    suspend fun loadImageAtlas(
        assetPath: String,
        tilesX: Int,
        tilesY: Int,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): Result<ImageData3d> {
        val ref = AssetRef.ImageAtlas(assetPath, format, resolveSize, tilesX, tilesY)
        val awaitedAsset = AwaitedAsset(ref)
        val (loaded, time) = measureTimedValue {
            awaitedAssetsChannel.send(awaitedAsset)
            awaitedAsset.awaiting.await() as LoadedAsset.ImageAtlas
        }
        return loaded.result
            .onSuccess {
                logD("AssetLoader.loadImageAtlas") { "Loaded ${trimAssetPath(assetPath)} (${it.format}, ${it.width}x${it.height}x${it.depth}, $time)" }
            }
            .onFailure {
                logE("AssetLoader.loadImageAtlas") { "Failed loading ${trimAssetPath(assetPath)}: $it" }
            }
    }

    /**
     * Asynchronously loads a cube map from the given image paths (one for each side).
     */
    suspend fun loadImageCube(
        negX: String,
        posX: String,
        negY: String,
        posY: String,
        negZ: String,
        posZ: String,
        format: TexFormat = TexFormat.RGBA,
        resolveSize: Vec2i? = null
    ): Result<ImageDataCube> {
        return try {
            val nx = loadImage2d(negX, format, resolveSize).getOrThrow()
            val px = loadImage2d(posX, format, resolveSize).getOrThrow()
            val nz = loadImage2d(negZ, format, resolveSize).getOrThrow()
            val pz = loadImage2d(posZ, format, resolveSize).getOrThrow()
            val ny = loadImage2d(negY, format, resolveSize).getOrThrow()
            val py = loadImage2d(posY, format, resolveSize).getOrThrow()
            Result.success(ImageDataCube(nx, px, ny, py, nz, pz))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    /**
     * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
     */
    suspend fun loadBlob(assetPath: String): Result<Uint8Buffer> {
        val ref = AssetRef.Blob(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        val (loaded, time) = measureTimedValue {
            awaitedAssetsChannel.send(awaitedAsset)
            awaitedAsset.awaiting.await() as LoadedAsset.Blob
        }
        return loaded.result
            .onSuccess {
                logD("AssetLoader.loadBlob") { "Loaded blob ${trimAssetPath(assetPath)} (${(it.capacity / 1_048_576.0).toString(1)} mb, $time)" }
            }
            .onFailure {
                logE("AssetLoader.loadBlob") { "Failed loading blob ${trimAssetPath(assetPath)}: $it" }
            }
    }

    suspend fun loadAudioClip(assetPath: String): Result<AudioClip> {
        val ref = AssetRef.Audio(assetPath)
        val awaitedAsset = AwaitedAsset(ref)
        val (loaded, time) = measureTimedValue {
            awaitedAssetsChannel.send(awaitedAsset)
            awaitedAsset.awaiting.await() as LoadedAsset.Audio
        }
        return loaded.result
            .onSuccess {
                logD("AssetLoader.loadAudioClip") { "Loaded audio ${trimAssetPath(assetPath)} (${it.duration} secs, loaded in $time)" }
            }
            .onFailure {
                logE("AssetLoader.loadAudioClip") { "Failed loading audio ${trimAssetPath(assetPath)}: $it" }
            }
    }

    private class AwaitedAsset(val ref: AssetRef, val awaiting: CompletableDeferred<LoadedAsset<*>> = CompletableDeferred(Assets.job))

    companion object {
        private const val NUM_LOAD_WORKERS = 8

        val textureDataLoadFailed = BufferedImageData2d.singleColor(Color.MAGENTA)

        internal fun trimCubeMapAssetPath(ft: String, bk: String, lt: String, rt: String, up: String, dn: String): String {
            return "cubeMap(ft:${trimAssetPath(ft)}, bk:${trimAssetPath(bk)}, lt:${trimAssetPath(lt)}, rt:${trimAssetPath(rt)}, up:${trimAssetPath(up)}, dn:${trimAssetPath(dn)})"
        }

        internal fun trimAssetPath(assetPath: String): String {
            return if (assetPath.startsWith("data:", true)) {
                val idx = assetPath.indexOf(';')
                assetPath.substring(0 until idx)
            } else {
                assetPath
            }
        }

    }
}
