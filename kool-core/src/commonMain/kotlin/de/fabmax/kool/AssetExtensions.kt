package de.fabmax.kool

import de.fabmax.kool.math.Vec2i
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
fun AssetLoader.loadImage2dAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageData2d>> = Assets.async { loadImage2d(assetPath, format, resolveSize) }

/**
 * Similar to [loadImage2dAsync], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 * Notice that [BufferedImageData2d] extends [ImageData2d] and hence it's possible to always use this method
 * instead of [loadImage2dAsync]. However, loading textures from the non-buffered [ImageData2d] returned by
 * [loadImage2dAsync] can be faster on some platforms (especially on JS).
 */
fun AssetLoader.loadBufferedImage2dAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<BufferedImageData2d>> = Assets.async { loadBufferedImage2d(assetPath, format, resolveSize) }

/**
 * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
fun AssetLoader.loadImageAtlasAsync(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageData3d>> = Assets.async { loadImageAtlas(assetPath, tilesX, tilesY, format, resolveSize) }

/**
 * Loads a cube map from the given image paths (one for each side).
 */
fun AssetLoader.loadImageCubeAsync(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageDataCube>> = Assets.async {
    loadImageCube(negX, posX, negY, posY, negZ, posZ, format, resolveSize)
}

/**
 * Loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
 */
fun AssetLoader.loadBlobAsync(assetPath: String): Deferred<Result<Uint8Buffer>> =
    Assets.async { loadBlob(assetPath) }

fun AssetLoader.loadAudioClipAsync(assetPath: String,): Deferred<Result<AudioClip>> =
    Assets.async { loadAudioClip(assetPath) }



suspend fun AssetLoader.loadTexture2d(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture2d> = loadImage2d(assetPath, format, resolveSize).map {
    it.toTexture(mipMapping, samplerSettings, AssetLoader.trimAssetPath(assetPath))
}

suspend fun AssetLoader.loadTexture2dArray(
    assetPaths: List<String>,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture2dArray> {
    val images = assetPaths.map { loadImage2dAsync(it, format, resolveSize) }.awaitAll()
    return try {
        val imageArray = ImageData2dArray(images.map { it.getOrThrow() })
        val texName = "Tex2dArray[${AssetLoader.trimAssetPath(assetPaths[0])},...]"
        Result.success(Texture2dArray(imageArray, mipMapping, samplerSettings, texName))
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

suspend fun AssetLoader.loadTexture3d(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture3d> = loadImageAtlas(assetPath, tilesX, tilesY).map {
    it.toTexture(mipMapping, samplerSettings, AssetLoader.trimAssetPath(assetPath))
}

suspend fun AssetLoader.loadTextureCube(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<TextureCube> = loadImageCube(negX, posX, negY, posY, negZ, posZ, format, resolveSize).map {
    val name = AssetLoader.trimCubeMapAssetPath(negZ, posZ, negX, posX, posY, negY)
    it.toTexture(mipMapping, samplerSettings, name)
}

fun AssetLoader.loadTexture2dAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<Texture2d>> =
    Assets.async { loadTexture2d(assetPath, format, mipMapping, samplerSettings, resolveSize) }

fun AssetLoader.loadTexture3dAsync(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<Texture3d>> = Assets.async { loadTexture3d(assetPath, tilesX, tilesY, format, mipMapping, samplerSettings, resolveSize) }

fun AssetLoader.loadTextureCubeAsync(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<TextureCube>> = Assets.async {
    loadTextureCube(negZ, posZ, negX, posX, posY, negY, format, mipMapping, samplerSettings, resolveSize)
}



/**
 * Loads the image data at the given path and returns it as [ImageData].
 */
suspend fun Assets.loadImage2d(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Result<ImageData2d> = defaultLoader.loadImage2d(assetPath, format, resolveSize)

/**
 * Similar to [loadImage2d], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 */
suspend fun Assets.loadBufferedImage(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Result<BufferedImageData2d> = defaultLoader.loadBufferedImage2d(assetPath, format, resolveSize)

/**
 * Loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
suspend fun Assets.loadImageAtlas(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Result<ImageData3d> = defaultLoader.loadImageAtlas(assetPath, tilesX, tilesY, format, resolveSize)

/**
 * Loads a cube map from the given image paths (one for each side).
 */
suspend fun Assets.loadImageCube(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Result<ImageDataCube> = defaultLoader.loadImageCube(negX, posX, negY, posY, negZ, posZ, format, resolveSize)

suspend fun Assets.loadTexture2d(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture2d> = defaultLoader.loadTexture2d(assetPath, format, mipMapping, samplerSettings, resolveSize)

suspend fun Assets.loadTexture2dArray(
    assetPaths: List<String>,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture2dArray> = defaultLoader.loadTexture2dArray(assetPaths, format, mipMapping, samplerSettings, resolveSize)

suspend fun Assets.loadTexture3d(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<Texture3d> = defaultLoader.loadTexture3d(assetPath, tilesX, tilesY, format, mipMapping, samplerSettings, resolveSize)

suspend fun Assets.loadTextureCube(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Result<TextureCube> = defaultLoader.loadTextureCube(negZ, posZ, negX, posX, posY, negY, format, mipMapping, samplerSettings, resolveSize)

suspend fun Assets.loadBlob(assetPath: String,): Result<Uint8Buffer> = defaultLoader.loadBlob(assetPath)

suspend fun Assets.loadAudioClip(assetPath: String): Result<AudioClip> = defaultLoader.loadAudioClip(assetPath)

suspend fun Assets.hdriEnvironment(hdriPath: String, brightness: Float = 1f): Result<EnvironmentMap> =
    defaultLoader.hdriEnvironment(hdriPath, brightness)


/**
 * Asynchronously loads the image data at the given path and returns it as [ImageData].
 */
fun Assets.loadImage2dAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageData2d>> = defaultLoader.loadImage2dAsync(assetPath, format, resolveSize)

/**
 * Similar to [loadImage2dAsync], but returns the image data as [BufferedImageData2d] object, which stores the pixels
 * in a CPU accessible buffer. This is particular useful if the image data is used to drive procedural stuff like
 * building heightmap geometry from a greyscale image.
 * Notice that [BufferedImageData2d] extends [ImageData2d] and hence it's possible to always use this method
 * instead of [loadImage2dAsync]. However, loading textures from the non-buffered [ImageData2d] returned by
 * [loadImage2dAsync] can be faster on some platforms (especially on JS).
 */
fun Assets.loadBufferedImageAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<BufferedImageData2d>> = defaultLoader.loadBufferedImage2dAsync(assetPath, format, resolveSize)

/**
 * Asynchronously loads the texture data at the given path and splits it into an atlas of [tilesX] * [tilesY] individual
 * image tiles. The texture atlas data is returned as [ImageData].
 */
fun Assets.loadImageAtlasAsync(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageData3d>> =
    defaultLoader.loadImageAtlasAsync(assetPath, tilesX, tilesY, format, resolveSize)

/**
 * Asynchronously loads a cube map from the given image paths (one for each side).
 */
fun Assets.loadImageCubeAsync(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    resolveSize: Vec2i? = null
): Deferred<Result<ImageDataCube>> = defaultLoader.loadImageCubeAsync(negZ, posZ, negX, posX, posY, negY, format, resolveSize)

fun Assets.loadTexture2dAsync(
    assetPath: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<Texture2d>> = defaultLoader.loadTexture2dAsync(assetPath, format, mipMapping, samplerSettings, resolveSize)

fun Assets.loadTexture3dAsync(
    assetPath: String,
    tilesX: Int,
    tilesY: Int,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<Texture3d>> = defaultLoader.loadTexture3dAsync(assetPath, tilesX, tilesY, format, mipMapping, samplerSettings, resolveSize)

fun Assets.loadTextureCubeAsync(
    negX: String,
    posX: String,
    negY: String,
    posY: String,
    negZ: String,
    posZ: String,
    format: TexFormat = TexFormat.RGBA,
    mipMapping: MipMapping = MipMapping.Full,
    samplerSettings: SamplerSettings = SamplerSettings(),
    resolveSize: Vec2i? = null
): Deferred<Result<TextureCube>> = defaultLoader.loadTextureCubeAsync(negZ, posZ, negX, posX, posY, negY, format, mipMapping, samplerSettings, resolveSize)

/**
 * Asynchronously loads the binary data asset at the given path and returns the data as an [Uint8Buffer].
 */
fun Assets.loadBlobAsync(assetPath: String): Deferred<Result<Uint8Buffer>> = defaultLoader.loadBlobAsync(assetPath)

fun Assets.loadAudioClipAsync(assetPath: String): Deferred<Result<AudioClip>> = defaultLoader.loadAudioClipAsync(assetPath)

fun Assets.hdriEnvironmentAsync(hdriPath: String, brightness: Float = 1f): Deferred<Result<EnvironmentMap>> =
    defaultLoader.hdriEnvironmentAsync(hdriPath, brightness)
