package de.fabmax.kool.editor.api

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE

interface AppAssetsLoader {
    val assetLoader: AssetLoader

    suspend fun loadHdriEnvironment(path: String): EnvironmentMaps?
    suspend fun loadModel(path: String): GltfFile?
    suspend fun loadTexture2d(path: String): Texture2d?
    suspend fun loadBlob(path: String): Uint8Buffer?
}

object AppAssets : AppAssetsLoader {
    var impl: AppAssetsLoader = DefaultLoader("assets")
    override val assetLoader: AssetLoader
        get() = impl.assetLoader

    override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? = impl.loadHdriEnvironment(path)
    override suspend fun loadModel(path: String): GltfFile? = impl.loadModel(path)
    override suspend fun loadTexture2d(path: String): Texture2d? = impl.loadTexture2d(path)
    override suspend fun loadBlob(path: String): Uint8Buffer? = impl.loadBlob(path)

    class DefaultLoader(val pathPrefix: String) : AppAssetsLoader {
        override val assetLoader: AssetLoader
            get() = Assets.defaultLoader

        override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? {
            val prefixed = "${pathPrefix}/${path}"
            return try {
                EnvironmentHelper.hdriEnvironment(Assets.loadTexture2d(prefixed))
            } catch (e: Exception) {
                logE { "Failed loading HDRI: $prefixed" }
                null
            }
        }

        override suspend fun loadModel(path: String): GltfFile? {
            val prefixed = "${pathPrefix}/${path}"
            return try {
                Assets.loadGltfFile(prefixed)
            } catch (e: Exception) {
                logE { "Failed loading model: $prefixed" }
                null
            }
        }

        override suspend fun loadTexture2d(path: String): Texture2d? {
            val prefixed = "${pathPrefix}/${path}"
            return try {
                Assets.loadTexture2d(prefixed)
            } catch (e: Exception) {
                logE { "Failed loading texture: $prefixed" }
                null
            }
        }

        override suspend fun loadBlob(path: String): Uint8Buffer? {
            val prefixed = "${pathPrefix}/${path}"
            return try {
                Assets.loadBlobAsset(prefixed)
            } catch (e: Exception) {
                logE { "Failed loading blob: $prefixed" }
                null
            }
        }
    }
}