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
    var impl: AppAssetsLoader = DefaultLoader()

    override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? = impl.loadHdriEnvironment(path)
    override suspend fun loadModel(modelPath: String): GltfFile? = impl.loadModel(modelPath)
    override suspend fun loadTexture2d(path: String): Texture2d? = impl.loadTexture2d(path)

    class DefaultLoader : AppAssetsLoader {
        override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? {
            return try {
                EnvironmentHelper.hdriEnvironment(Assets.loadTexture2d(path))
            } catch (e: Exception) {
                logE { "Failed loading HDRI: $path" }
                null
            }
        }

        override suspend fun loadModel(modelPath: String): GltfFile? {
            return try {
                Assets.loadGltfFile(modelPath)
            } catch (e: Exception) {
                logE { "Failed loading model: $modelPath" }
                null
            }
        }

        override suspend fun loadTexture2d(path: String): Texture2d? {
            return try {
                Assets.loadTexture2d(path)
            } catch (e: Exception) {
                logE { "Failed loading texture: $path" }
                null
            }
        }
    }
}