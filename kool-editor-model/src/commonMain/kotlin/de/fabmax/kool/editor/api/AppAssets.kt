package de.fabmax.kool.editor.api

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE

interface AppAssetsLoader {
    val assetLoader: AssetLoader

    suspend fun loadHdri(ref: AssetReference.Hdri): EnvironmentMaps?
    suspend fun loadModel(ref: AssetReference.Model): GltfFile?
    suspend fun loadTexture2d(ref: AssetReference.Texture): Texture2d?
    suspend fun loadHeightmap(ref: AssetReference.Heightmap): Heightmap?
    suspend fun loadBlob(ref: AssetReference.Blob): Uint8Buffer?
}

suspend fun AppAssetsLoader.loadTexture2d(path: String) = loadTexture2d(AssetReference.Texture(path))
suspend fun AppAssetsLoader.loadModel(path: String) = loadModel(AssetReference.Model(path))
suspend fun AppAssetsLoader.loadHdri(path: String) = loadHdri(AssetReference.Hdri(path))
suspend fun AppAssetsLoader.loadBlob(path: String) = loadBlob(AssetReference.Blob(path))

suspend fun AppAssetsLoader.requireHdriEnvironment(ref: AssetReference.Hdri): EnvironmentMaps = checkNotNull(loadHdri(ref))
suspend fun AppAssetsLoader.requireModel(ref: AssetReference.Model): GltfFile = checkNotNull(loadModel(ref))
suspend fun AppAssetsLoader.requireTexture2d(ref: AssetReference.Texture): Texture2d = checkNotNull(loadTexture2d(ref))
suspend fun AppAssetsLoader.requireHeightmap(ref: AssetReference.Heightmap): Heightmap = checkNotNull(loadHeightmap(ref))
suspend fun AppAssetsLoader.requireBlob(ref: AssetReference.Blob): Uint8Buffer = checkNotNull(loadBlob(ref))

suspend fun AppAssetsLoader.cacheAsset(ref: AssetReference): Boolean {
    return when (ref) {
        is AssetReference.Blob -> loadBlob(ref) != null
        is AssetReference.Hdri -> loadHdri(ref) != null
        is AssetReference.Heightmap -> loadHeightmap(ref) != null
        is AssetReference.Model -> loadModel(ref) != null
        is AssetReference.Texture -> loadTexture2d(ref) != null
    }
}

object AppAssets : AppAssetsLoader {
    var impl: AppAssetsLoader = DefaultLoader("assets")
    override val assetLoader: AssetLoader
        get() = impl.assetLoader

    override suspend fun loadHdri(ref: AssetReference.Hdri): EnvironmentMaps? = impl.loadHdri(ref)
    override suspend fun loadModel(ref: AssetReference.Model): GltfFile? = impl.loadModel(ref)
    override suspend fun loadTexture2d(ref: AssetReference.Texture): Texture2d? = impl.loadTexture2d(ref)
    override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Heightmap? = impl.loadHeightmap(ref)
    override suspend fun loadBlob(ref: AssetReference.Blob): Uint8Buffer? = impl.loadBlob(ref)

    class DefaultLoader(val pathPrefix: String) : AppAssetsLoader {
        private val cache = mutableMapOf<AssetReference, Any>()

        override val assetLoader: AssetLoader
            get() = Assets.defaultLoader

        override suspend fun loadHdri(ref: AssetReference.Hdri): EnvironmentMaps? {
            cache[ref]?.let { return it as EnvironmentMaps }

            val prefixed = "${pathPrefix}/${ref.path}"
            return try {
                EnvironmentHelper.hdriEnvironment(Assets.loadTexture2d(prefixed)).also { cache[ref] = it }
            } catch (e: Exception) {
                logE { "Failed loading HDRI: $prefixed" }
                null
            }
        }

        override suspend fun loadModel(ref: AssetReference.Model): GltfFile? {
            cache[ref]?.let { return it as GltfFile }

            val prefixed = "${pathPrefix}/${ref.path}"
            return try {
                Assets.loadGltfFile(prefixed).also { cache[ref] = it }
            } catch (e: Exception) {
                logE { "Failed loading model: $prefixed" }
                null
            }
        }

        override suspend fun loadTexture2d(ref: AssetReference.Texture): Texture2d? {
            val prefixed = "${pathPrefix}/${ref.path}"
            return try {
                val props = TextureProps(ref.texFormat)
                Assets.loadTexture2d(prefixed, props).also { cache[ref] = it }
            } catch (e: Exception) {
                logE { "Failed loading texture: $prefixed" }
                null
            }
        }

        override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Heightmap? {
            val prefixed = "${pathPrefix}/${ref.path}"
            return try {
                val blob = Assets.loadBlobAsset(prefixed)
                Heightmap.fromRawData(blob, ref.heightScale, ref.rows, ref.columns, ref.heightOffset).also { cache[ref] = it }
            } catch (e: Exception) {
                logE { "Failed loading heightmap: $prefixed" }
                null
            }
        }

        override suspend fun loadBlob(ref: AssetReference.Blob): Uint8Buffer? {
            val prefixed = "${pathPrefix}/${ref.path}"
            return try {
                Assets.loadBlobAsset(prefixed).also { cache[ref] = it }
            } catch (e: Exception) {
                logE { "Failed loading blob: $prefixed" }
                null
            }
        }
    }
}