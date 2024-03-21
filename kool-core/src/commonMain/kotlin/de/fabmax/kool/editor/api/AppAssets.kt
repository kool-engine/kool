package de.fabmax.kool.editor.api

import de.fabmax.kool.Assets
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.logE

interface AppAssetsLoader {
    suspend fun loadHdriEnvironment(path: String): EnvironmentMaps?
    suspend fun loadModel(modelPath: String): GltfFile?
    suspend fun loadTexture2d(path: String): Texture2d?
}

object AppAssets : AppAssetsLoader {
    var impl: AppAssetsLoader = DefaultLoader("assets")

    override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? = impl.loadHdriEnvironment(path)
    override suspend fun loadModel(modelPath: String): GltfFile? = impl.loadModel(modelPath)
    override suspend fun loadTexture2d(path: String): Texture2d? = impl.loadTexture2d(path)

    class DefaultLoader(val pathPrefix: String) : AppAssetsLoader {
        override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? {
            val prefixed = "${pathPrefix}/${path}"
            return try {
                EnvironmentHelper.hdriEnvironment(Assets.loadTexture2d(prefixed))
            } catch (e: Exception) {
                logE { "Failed loading HDRI: $prefixed" }
                null
            }
        }

        override suspend fun loadModel(modelPath: String): GltfFile? {
            val prefixed = "${pathPrefix}/${modelPath}"
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
    }
}