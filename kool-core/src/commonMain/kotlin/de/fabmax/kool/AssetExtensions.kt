package de.fabmax.kool

import de.fabmax.kool.modules.audio.AudioClip
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.pipeline.ibl.hdriEnvironment
import de.fabmax.kool.pipeline.ibl.hdriEnvironmentAsync
import de.fabmax.kool.util.Uint8Buffer
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll

/**
 * Loads the texture data at the given path and returns it as [ImageData].
 */
fun AssetLoader.loadImage2dAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<ImageData2d>> =
    Assets.async { loadImage2d(assetPath, props) }

/**
 * Similar to [loadImage2dAsync], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 * Notice that [BufferedImageData2d] extends [ImageData2d] and hence it's possible to always use this method
 * instead of [loadImage2dAsync]. However, loading textures from the non-buffered [ImageData2d] returned by
 * [loadImage2dAsync] can be faster on some platforms (especially on JS).
 */
fun AssetLoader.loadBufferedImage2dAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<BufferedImageData2d>> =
    Assets.async { loadBufferedImage2d(assetPath, props) }

/**
 * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
fun AssetLoader.loadImageAtlasAsync(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): Deferred<Result<ImageData3d>> =
    Assets.async { loadImageAtlas(assetPath, tilesX, tilesY, props) }

/**
 * Loads a cube map from the given image paths (one for each side).
 */
fun AssetLoader.loadImageCubeAsync(
    pathFront: String,
    pathBack: String,
    pathLeft: String,
    pathRight: String,
    pathUp: String,
    pathDown: String
): Deferred<Result<ImageDataCube>> = Assets.async {
    loadImageCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
}

/**
 * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
 */
fun AssetLoader.loadBlobAsync(assetPath: String): Deferred<Result<Uint8Buffer>> =
    Assets.async { loadBlob(assetPath) }

fun AssetLoader.loadAudioClipAsync(assetPath: String,): Deferred<Result<AudioClip>> =
    Assets.async { loadAudioClip(assetPath) }



suspend fun AssetLoader.loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Result<Texture2d> =
    loadImage2d(assetPath, props).map { it.toTexture(props, AssetLoader.trimAssetPath(assetPath)) }

suspend fun AssetLoader.loadTexture2dArray(assetPaths: List<String>, props: TextureProps = TextureProps()): Result<Texture2dArray> {
    val images = assetPaths.map { loadImage2dAsync(it, props) }.awaitAll()
    return try {
        val imageArray = ImageData2dArray(images.map { it.getOrThrow() })
        val texName = "Tex2dArray[${AssetLoader.trimAssetPath(assetPaths[0])},...]"
        Result.success(Texture2dArray(imageArray, props, texName))
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

suspend fun AssetLoader.loadTexture3d(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    props: TextureProps = TextureProps()
): Result<Texture3d> = loadImageAtlas(assetPath, tilesX, tilesY).map { it.toTexture(props, AssetLoader.trimAssetPath(assetPath)) }

suspend fun AssetLoader.loadTextureCube(
    pathFront: String,
    pathBack: String,
    pathLeft: String,
    pathRight: String,
    pathUp: String,
    pathDown: String,
    props: TextureProps = TextureProps()
): Result<TextureCube> = loadImageCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown).map {
    val name = AssetLoader.trimCubeMapAssetPath(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)
    it.toTexture(props, name)
}

fun AssetLoader.loadTexture2dAsync(assetPath: String, props: TextureProps = TextureProps()): Deferred<Result<Texture2d>> =
    Assets.async { loadTexture2d(assetPath, props) }

fun AssetLoader.loadTexture3dAsync(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    props: TextureProps = TextureProps()
): Deferred<Result<Texture3d>> = Assets.async { loadTexture3d(assetPath, tilesX, tilesY, props) }

fun AssetLoader.loadTextureCubeAsync(
    pathFront: String,
    pathBack: String,
    pathLeft: String,
    pathRight: String,
    pathUp: String,
    pathDown: String,
    props: TextureProps = TextureProps()
): Deferred<Result<TextureCube>> = Assets.async {
    loadTextureCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props)
}



/**
 * Loads the image data at the given path and returns it as [ImageData].
 */
suspend fun Assets.loadImage2d(assetPath: String, props: TextureProps? = null): Result<ImageData2d> =
    defaultLoader.loadImage2d(assetPath, props)

/**
 * Similar to [loadImage2d], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 */
suspend fun Assets.loadBufferedImage(assetPath: String, props: TextureProps? = null): Result<BufferedImageData2d> =
    defaultLoader.loadBufferedImage2d(assetPath, props)

/**
 * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
suspend fun Assets.loadImageAtlas(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): Result<ImageData3d> =
    defaultLoader.loadImageAtlas(assetPath, tilesX, tilesY, props)

/**
 * Loads a cube map from the given image paths (one for each side).
 */
suspend fun Assets.loadImageCube(
    pathFront: String, pathBack: String, pathLeft: String, pathRight: String, pathUp: String, pathDown: String
): Result<ImageDataCube> = defaultLoader.loadImageCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)

suspend fun Assets.loadTexture2d(assetPath: String, props: TextureProps = TextureProps()): Result<Texture2d> =
    defaultLoader.loadTexture2d(assetPath, props)

suspend fun Assets.loadTexture2dArray(assetPaths: List<String>, props: TextureProps = TextureProps()): Result<Texture2dArray> =
    defaultLoader.loadTexture2dArray(assetPaths, props)

suspend fun Assets.loadTexture3d(
    assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps = TextureProps()
): Result<Texture3d> = defaultLoader.loadTexture3d(assetPath, tilesX, tilesY, props)

suspend fun Assets.loadTextureCube(
    pathFront: String, pathBack: String, pathLeft: String, pathRight: String, pathUp: String, pathDown: String,
    props: TextureProps = TextureProps()
): Result<TextureCube> = defaultLoader.loadTextureCube(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props)

suspend fun Assets.loadBlob(assetPath: String,): Result<Uint8Buffer> = defaultLoader.loadBlob(assetPath)

suspend fun Assets.loadAudioClip(assetPath: String): Result<AudioClip> = defaultLoader.loadAudioClip(assetPath)

suspend fun Assets.hdriEnvironment(hdriPath: String, brightness: Float = 1f): Result<EnvironmentMap> =
    defaultLoader.hdriEnvironment(hdriPath, brightness)


/**
 * Asynchronously loads the image data at the given path and returns it as [ImageData].
 */
fun Assets.loadImage2dAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<ImageData2d>> =
    defaultLoader.loadImage2dAsync(assetPath, props)

/**
 * Similar to [loadImage2dAsync], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 * Notice that [BufferedImageData2d] extends [ImageData2d] and hence it's possible to always use this method
 * instead of [loadImage2dAsync]. However, loading textures from the non-buffered [ImageData2d] returned by
 * [loadImage2dAsync] can be faster on some platforms (especially on JS).
 */
fun Assets.loadBufferedImageAsync(assetPath: String, props: TextureProps? = null): Deferred<Result<BufferedImageData2d>> =
    defaultLoader.loadBufferedImage2dAsync(assetPath, props)

/**
 * Asynchronously loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
fun Assets.loadImageAtlasAsync(assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps? = null): Deferred<Result<ImageData3d>> =
    defaultLoader.loadImageAtlasAsync(assetPath, tilesX, tilesY, props)

/**
 * Asynchronously loads a cube map from the given image paths (one for each side).
 */
fun Assets.loadImageCubeAsync(
    pathFront: String, pathBack: String, pathLeft: String, pathRight: String, pathUp: String, pathDown: String
): Deferred<Result<ImageDataCube>> = defaultLoader.loadImageCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown)

fun Assets.loadTexture2dAsync(assetPath: String, props: TextureProps = TextureProps()): Deferred<Result<Texture2d>> =
    defaultLoader.loadTexture2dAsync(assetPath, props)

fun Assets.loadTexture3dAsync(
    assetPath: String, tilesX: Int, tilesY: Int, props: TextureProps = TextureProps()
): Deferred<Result<Texture3d>> = defaultLoader.loadTexture3dAsync(assetPath, tilesX, tilesY, props)

fun Assets.loadTextureCubeAsync(
    pathFront: String, pathBack: String, pathLeft: String, pathRight: String, pathUp: String, pathDown: String,
    props: TextureProps = TextureProps()
): Deferred<Result<TextureCube>> = defaultLoader.loadTextureCubeAsync(pathFront, pathBack, pathLeft, pathRight, pathUp, pathDown, props)

/**
 * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
 */
fun Assets.loadBlobAsync(assetPath: String): Deferred<Result<Uint8Buffer>> = defaultLoader.loadBlobAsync(assetPath)

fun Assets.loadAudioClipAsync(assetPath: String): Deferred<Result<AudioClip>> = defaultLoader.loadAudioClipAsync(assetPath)

fun Assets.hdriEnvironmentAsync(hdriPath: String, brightness: Float = 1f): Deferred<Result<EnvironmentMap>> =
    defaultLoader.hdriEnvironmentAsync(hdriPath, brightness)
