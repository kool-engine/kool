package de.fabmax.kool.editor

import de.fabmax.kool.Assets
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.logE

object CachedAppAssets : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<String, MutableStateValue<EnvironmentMaps?>>()
    private val loadedModels = mutableMapOf<String, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<String, MutableStateValue<Texture2d?>>()

    override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? {
        val hdriState = loadedHdris.getOrPut(path) { mutableStateOf(null) }
        return try {
            val hdriTex = Assets.loadTexture2d(path)
            hdriState.value ?: EnvironmentHelper.hdriEnvironment(hdriTex).also { hdriState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading HDRI: $path" }
            null
        }
    }

    override suspend fun loadModel(modelPath: String): GltfFile? {
        val modelState = loadedModels.getOrPut(modelPath) { mutableStateOf(null) }
        return try {
            modelState.value ?: Assets.loadGltfFile(modelPath).also { modelState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading model: $modelPath" }
            null
        }
    }

    override suspend fun loadTexture2d(path: String): Texture2d? {
        val texState = loadedTextures2d.getOrPut(path) { mutableStateOf(null) }
        return try {
            texState.value ?: Assets.loadTexture2d(path).also { texState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading texture: $path" }
            null
        }
    }

    fun getHdriEnvironmentIfLoaded(path: String): MutableStateValue<EnvironmentMaps?> {
        return loadedHdris.getOrPut(path) { mutableStateOf(null) }
    }

    fun getModelIfLoaded(path: String): MutableStateValue<GltfFile?> {
        return loadedModels.getOrPut(path) { mutableStateOf(null) }
    }

    fun getTextureIfLoaded(path: String): MutableStateValue<Texture2d?> {
        return loadedTextures2d.getOrPut(path) { mutableStateOf(null) }
    }
}