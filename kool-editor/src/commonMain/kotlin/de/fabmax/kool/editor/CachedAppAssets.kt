package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.editor.data.MaterialData
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene

class CachedAppAssets : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<String, EnvironmentMaps>()
    private val loadedModels = mutableMapOf<String, GltfFile>()
    private val loadedTextures2d = mutableMapOf<String, Texture2d>()
    private val loadedMaterials = mutableMapOf<String, MaterialData>()

    override suspend fun loadHdriEnvironment(scene: Scene, path: String): EnvironmentMaps {
        return loadedHdris.getOrPut(path) {
            EnvironmentHelper.hdriEnvironment(scene, loadTexture2d(path), autoDispose = false)
        }
    }

    override suspend fun loadModel(model: ModelComponentData): GltfFile {
        return loadedModels.getOrPut(model.modelPath) { Assets.loadGltfFile(model.modelPath) }
    }

    override suspend fun loadTexture2d(path: String): Texture2d {
        return loadedTextures2d.getOrPut(path) { Assets.loadTexture2d(path) }
    }
}