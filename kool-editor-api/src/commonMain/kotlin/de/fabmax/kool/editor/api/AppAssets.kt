package de.fabmax.kool.editor.api

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene

interface AppAssetsLoader {
    suspend fun loadHdriEnvironment(scene: Scene, path: String): EnvironmentMaps
    suspend fun loadModel(model: ModelComponentData): GltfFile
    suspend fun loadTexture2d(path: String): Texture2d
}

object AppAssets : AppAssetsLoader {
    var impl: AppAssetsLoader = DefaultLoader()

    override suspend fun loadHdriEnvironment(scene: Scene, path: String): EnvironmentMaps = impl.loadHdriEnvironment(scene, path)
    override suspend fun loadModel(model: ModelComponentData): GltfFile = impl.loadModel(model)
    override suspend fun loadTexture2d(path: String): Texture2d = impl.loadTexture2d(path)

    class DefaultLoader : AppAssetsLoader {
        override suspend fun loadHdriEnvironment(scene: Scene, path: String): EnvironmentMaps {
            return EnvironmentHelper.hdriEnvironment(scene, loadTexture2d(path), autoDispose = false)
        }

        override suspend fun loadModel(model: ModelComponentData): GltfFile = Assets.loadGltfFile(model.modelPath)

        override suspend fun loadTexture2d(path: String): Texture2d = Assets.loadTexture2d(path)
    }
}