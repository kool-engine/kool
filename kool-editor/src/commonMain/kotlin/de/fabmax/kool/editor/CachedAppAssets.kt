package de.fabmax.kool.editor

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE

class CachedAppAssets(override val assetLoader: AssetLoader) : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<String, MutableStateValue<EnvironmentMaps?>>()
    private val loadedModels = mutableMapOf<String, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<String, MutableStateValue<Texture2d?>>()
    private val loadedBlobs = mutableMapOf<String, MutableStateValue<Uint8Buffer?>>()

    override suspend fun loadHdriEnvironment(path: String): EnvironmentMaps? {
        val hdriState = loadedHdris.getOrPut(path) { mutableStateOf(null) }
        return try {
            val hdriTex = assetLoader.loadTexture2d(path)
            hdriState.value ?: EnvironmentHelper.hdriEnvironment(hdriTex).also { hdriState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading HDRI: $path" }
            null
        }
    }

    override suspend fun loadModel(path: String): GltfFile? {
        val modelState = loadedModels.getOrPut(path) { mutableStateOf(null) }
        return try {
            modelState.value ?: assetLoader.loadGltfFile(path).also { modelState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading model: $path" }
            null
        }
    }

    override suspend fun loadTexture2d(path: String): Texture2d? {
        val texState = loadedTextures2d.getOrPut(path) { mutableStateOf(null) }
        return try {
            texState.value ?: assetLoader.loadTexture2d(path).also { texState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading texture: $path" }
            null
        }
    }

    override suspend fun loadBlob(path: String): Uint8Buffer? {
        val blobState = loadedBlobs.getOrPut(path) { mutableStateOf(null) }
        return try {
            blobState.value ?: assetLoader.loadBlobAsset(path).also { blobState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading blob: $path" }
            null
        }
    }

    fun getHdriEnvironmentMutableState(path: String): MutableStateValue<EnvironmentMaps?> {
        return loadedHdris.getOrPut(path) { mutableStateOf(null) }
    }

    fun getModelMutableState(path: String): MutableStateValue<GltfFile?> {
        return loadedModels.getOrPut(path) { mutableStateOf(null) }
    }

    fun getTextureMutableState(path: String): MutableStateValue<Texture2d?> {
        return loadedTextures2d.getOrPut(path) { mutableStateOf(null) }
    }

    fun getBlobMutableState(path: String): MutableStateValue<Uint8Buffer?> {
        return loadedBlobs.getOrPut(path) { mutableStateOf(null) }
    }
}