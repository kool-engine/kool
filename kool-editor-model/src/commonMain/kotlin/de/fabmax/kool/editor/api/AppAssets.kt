package de.fabmax.kool.editor.api

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.loadTexture2d
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.ImageData2d
import de.fabmax.kool.pipeline.ImageData2dArray
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture2dArray
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.Uint8Buffer

interface AppAssetsLoader {
    val assetLoader: AssetLoader

    suspend fun loadHdri(ref: AssetReference.Hdri): Result<EnvironmentMap>
    suspend fun loadModel(ref: AssetReference.Model): Result<GltfFile>
    suspend fun loadTexture2d(ref: AssetReference.Texture): Result<Texture2d>
    suspend fun loadTexture2dArray(ref: AssetReference.TextureArray): Result<Texture2dArray>
    suspend fun loadHeightmap(ref: AssetReference.Heightmap): Result<Heightmap>
    suspend fun loadBlob(ref: AssetReference.Blob): Result<Uint8Buffer>
}

suspend fun AppAssetsLoader.loadTexture2d(path: String) = loadTexture2d(AssetReference.Texture(path))
suspend fun AppAssetsLoader.loadModel(path: String) = loadModel(AssetReference.Model(path))
suspend fun AppAssetsLoader.loadHdri(path: String) = loadHdri(AssetReference.Hdri(path))
suspend fun AppAssetsLoader.loadHeightmap(path: String) = loadHeightmap(AssetReference.Heightmap(path))
suspend fun AppAssetsLoader.loadBlob(path: String) = loadBlob(AssetReference.Blob(path))

suspend fun AppAssetsLoader.loadTexture2dOrNull(path: String) = loadTexture2d(AssetReference.Texture(path)).getOrNull()
suspend fun AppAssetsLoader.loadModelOrNull(path: String) = loadModel(AssetReference.Model(path)).getOrNull()
suspend fun AppAssetsLoader.loadHdriOrNull(path: String) = loadHdri(AssetReference.Hdri(path)).getOrNull()
suspend fun AppAssetsLoader.loadHeightmapOrNull(path: String) = loadHeightmap(AssetReference.Heightmap(path)).getOrNull()
suspend fun AppAssetsLoader.loadBlobOrNull(path: String) = loadBlob(AssetReference.Blob(path)).getOrNull()

suspend fun AppAssetsLoader.requireHdriEnvironment(ref: AssetReference.Hdri): EnvironmentMap = loadHdri(ref).getOrThrow()
suspend fun AppAssetsLoader.requireModel(ref: AssetReference.Model): GltfFile = loadModel(ref).getOrThrow()
suspend fun AppAssetsLoader.requireTexture2d(ref: AssetReference.Texture): Texture2d = loadTexture2d(ref).getOrThrow()
suspend fun AppAssetsLoader.requireHeightmap(ref: AssetReference.Heightmap): Heightmap = loadHeightmap(ref).getOrThrow()
suspend fun AppAssetsLoader.requireBlob(ref: AssetReference.Blob): Uint8Buffer = loadBlob(ref).getOrThrow()

suspend fun AppAssetsLoader.loadHdriEnvironmentOrNull(ref: AssetReference.Hdri): EnvironmentMap? = loadHdri(ref).getOrNull()
suspend fun AppAssetsLoader.loadModelOrNull(ref: AssetReference.Model): GltfFile? = loadModel(ref).getOrNull()
suspend fun AppAssetsLoader.loadTexture2dOrNull(ref: AssetReference.Texture): Texture2d? = loadTexture2d(ref).getOrNull()
suspend fun AppAssetsLoader.loadHeightmapOrNull(ref: AssetReference.Heightmap): Heightmap? = loadHeightmap(ref).getOrNull()
suspend fun AppAssetsLoader.loadBlobOrNull(ref: AssetReference.Blob): Uint8Buffer? = loadBlob(ref).getOrNull()

suspend fun AppAssetsLoader.cacheAsset(ref: AssetReference): Boolean {
    return when (ref) {
        is AssetReference.Blob -> loadBlob(ref).isSuccess
        is AssetReference.Hdri -> loadHdri(ref).isSuccess
        is AssetReference.Heightmap -> loadHeightmap(ref).isSuccess
        is AssetReference.Model -> loadModel(ref).isSuccess
        is AssetReference.Texture -> loadTexture2d(ref).isSuccess
        is AssetReference.TextureArray -> loadTexture2dArray(ref).isSuccess
    }
}

object AppAssets : AppAssetsLoader {
    var impl: AppAssetsLoader = DefaultLoader("assets/")
    override val assetLoader: AssetLoader
        get() = impl.assetLoader

    override suspend fun loadHdri(ref: AssetReference.Hdri): Result<EnvironmentMap> = impl.loadHdri(ref)
    override suspend fun loadModel(ref: AssetReference.Model): Result<GltfFile> = impl.loadModel(ref)
    override suspend fun loadTexture2d(ref: AssetReference.Texture): Result<Texture2d> = impl.loadTexture2d(ref)
    override suspend fun loadTexture2dArray(ref: AssetReference.TextureArray): Result<Texture2dArray> = impl.loadTexture2dArray(ref)
    override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Result<Heightmap> = impl.loadHeightmap(ref)
    override suspend fun loadBlob(ref: AssetReference.Blob): Result<Uint8Buffer> = impl.loadBlob(ref)
}

open class DefaultLoader(val pathPrefix: String) : AppAssetsLoader {
    private val cache = mutableMapOf<AssetReference, Result<Any>>()

    override val assetLoader: AssetLoader
        get() = Assets.defaultLoader

    override suspend fun loadHdri(ref: AssetReference.Hdri): Result<EnvironmentMap> {
        cache[ref]?.let { return it.mapCatching { r -> r as EnvironmentMap } }
        val prefixed = "${pathPrefix}${ref.path}"
        return assetLoader.loadTexture2d(prefixed)
            .map { EnvironmentMap.fromHdriTexture(it) }
            .also { cache[ref] = it }
    }

    override suspend fun loadModel(ref: AssetReference.Model): Result<GltfFile> {
        cache[ref]?.let { return it.mapCatching { r -> r as GltfFile } }
        val prefixed = "${pathPrefix}${ref.path}"
        return assetLoader.loadGltfFile(prefixed)
            .also { cache[ref] = it }
    }

    override suspend fun loadTexture2d(ref: AssetReference.Texture): Result<Texture2d> {
        val prefixed = "${pathPrefix}${ref.path}"
        val image: Result<ImageData2d> = cache[ref]?.let { it.mapCatching { r -> r as ImageData2d } }
            ?: assetLoader.loadImage2d(prefixed, ref.texFormat).also { cache[ref] = it }
        return image.mapCatching { Texture2d(it, name = prefixed) }
    }

    override suspend fun loadTexture2dArray(ref: AssetReference.TextureArray): Result<Texture2dArray> {
        val images: List<Result<ImageData2d>> = ref.paths.map { path ->
            val prefixed = "$pathPrefix$path"
            val texRef = AssetReference.Texture(prefixed, ref.texFormat)
            cache[texRef]?.let { it.mapCatching { r -> r as ImageData2d } }
                ?: assetLoader.loadImage2d(prefixed, ref.texFormat).also { cache[texRef] = it }
        }
        return try {
            val data = ImageData2dArray(images.map { it.getOrThrow() })
            Result.success(Texture2dArray(data, name = "Texture2dArray[${data.id}]"))
        } catch (t: Throwable) {
            Result.failure(t)
        }
    }

    override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Result<Heightmap> {
        cache[ref]?.let { return it.mapCatching { r -> r as Heightmap } }
        val prefixed = "${pathPrefix}${ref.path}"
        return assetLoader.loadBlob(prefixed)
            .map { Heightmap.fromRawData(it, ref.heightScale, ref.rows, ref.columns, ref.heightOffset) }
            .also { cache[ref] = it }
    }

    override suspend fun loadBlob(ref: AssetReference.Blob): Result<Uint8Buffer> {
        cache[ref]?.let { return it.mapCatching { r -> r as Uint8Buffer } }
        val prefixed = "${pathPrefix}${ref.path}"
        return assetLoader.loadBlob(prefixed)
            .also { cache[ref] = it }
    }
}