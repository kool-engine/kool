package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.editor.data.ModelComponentData
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.scene.Scene

object CachedAppAssets : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<String, MutableStateValue<EnvironmentMaps?>>()
    private val loadedModels = mutableMapOf<String, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<String, MutableStateValue<Texture2d?>>()

    override suspend fun loadHdriEnvironment(scene: Scene, path: String): EnvironmentMaps {
        val hdriState = loadedHdris.getOrPut(path) {
            mutableStateOf(EnvironmentHelper.hdriEnvironment(scene, loadTexture2d(path), autoDispose = false))
        }
        return hdriState.value ?: EnvironmentHelper.hdriEnvironment(scene, loadTexture2d(path), autoDispose = false).also { hdriState.set(it) }
    }

    override suspend fun loadModel(model: ModelComponentData): GltfFile {
        val modelState = loadedModels.getOrPut(model.modelPath) { mutableStateOf(Assets.loadGltfFile(model.modelPath)) }
        return modelState.value ?: Assets.loadGltfFile(model.modelPath).also { modelState.set(it) }
    }

    override suspend fun loadTexture2d(path: String): Texture2d {
        val texState = loadedTextures2d.getOrPut(path) { mutableStateOf(Assets.loadTexture2d(path)) }
        return texState.value ?: Assets.loadTexture2d(path).also { texState.set(it) }
    }

    fun getHdriEnvironmentWhenLoaded(path: String): MutableStateValue<EnvironmentMaps?> {
        return loadedHdris.getOrPut(path) { mutableStateOf(null) }
    }

    fun getModelWhenLoaded(path: String): MutableStateValue<GltfFile?> {
        return loadedModels.getOrPut(path) { mutableStateOf(null) }
    }

    fun getTextureWhenLoaded(path: String): MutableStateValue<Texture2d?> {
        return loadedTextures2d.getOrPut(path) { mutableStateOf(null) }
    }
}