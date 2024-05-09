package de.fabmax.kool.editor

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.Heightmap
import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.logE

class CachedAppAssets(override val assetLoader: AssetLoader) : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<AssetReference.Hdri, MutableStateValue<EnvironmentMaps?>>()
    private val loadedModels = mutableMapOf<AssetReference.Model, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<AssetReference.Texture, MutableStateValue<Texture2d?>>()
    private val loadedHeightmaps = mutableMapOf<AssetReference.Heightmap, MutableStateValue<Heightmap?>>()
    private val loadedBlobs = mutableMapOf<AssetReference.Blob, MutableStateValue<Uint8Buffer?>>()

    override suspend fun loadHdri(ref: AssetReference.Hdri): EnvironmentMaps? {
        val hdriState = loadedHdris.getOrPut(ref) { mutableStateOf(null) }
        return try {
            val hdriTex = assetLoader.loadTexture2d(ref.path)
            hdriState.value ?: EnvironmentHelper.hdriEnvironment(hdriTex).also { hdriState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading HDRI: ${ref.path}" }
            null
        }
    }

    override suspend fun loadModel(ref: AssetReference.Model): GltfFile? {
        val modelState = loadedModels.getOrPut(ref) { mutableStateOf(null) }
        return try {
            modelState.value ?: assetLoader.loadGltfFile(ref.path).also { modelState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading model: ${ref.path}" }
            null
        }
    }

    override suspend fun loadTexture2d(ref: AssetReference.Texture): Texture2d? {
        val texState = loadedTextures2d.getOrPut(ref) { mutableStateOf(null) }
        return try {
            texState.value ?: assetLoader.loadTexture2d(ref.path).also { texState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading texture: ${ref.path}" }
            null
        }
    }

    override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Heightmap? {
        loadedHeightmaps.keys.removeAll { it.path == ref.path && it != ref }

        val heightmapState = loadedHeightmaps.getOrPut(ref) { mutableStateOf(null) }
        heightmapState.value?.let { return it }

        return try {
            val blob = assetLoader.loadBlobAsset(ref.path)
            val heightmap = Heightmap.fromRawData(blob, ref.heightScale, ref.rows, ref.columns, ref.heightOffset)
            heightmap.also { heightmapState.set(it) }

        } catch (e: Exception) {
            logE { "Failed loading heightmap: ${ref.path}" }
            null
        }
    }

    override suspend fun loadBlob(ref: AssetReference.Blob): Uint8Buffer? {
        val blobState = loadedBlobs.getOrPut(ref) { mutableStateOf(null) }
        return try {
            blobState.value ?: assetLoader.loadBlobAsset(ref.path).also { blobState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading blob: ${ref.path}" }
            null
        }
    }

    fun getHdriEnvironmentMutableState(ref: AssetReference.Hdri): MutableStateValue<EnvironmentMaps?> {
        return loadedHdris.getOrPut(ref) { mutableStateOf(null) }
    }

    fun getModelMutableState(ref: AssetReference.Model): MutableStateValue<GltfFile?> {
        return loadedModels.getOrPut(ref) { mutableStateOf(null) }
    }

    fun getTextureMutableState(ref: AssetReference.Texture): MutableStateValue<Texture2d?> {
        return loadedTextures2d.getOrPut(ref) { mutableStateOf(null) }
    }

    fun getHeightmapMutableState(ref: AssetReference.Heightmap): MutableStateValue<Heightmap?> {
        return loadedHeightmaps.getOrPut(ref) { mutableStateOf(null) }
    }

    fun getBlobMutableState(ref: AssetReference.Blob): MutableStateValue<Uint8Buffer?> {
        return loadedBlobs.getOrPut(ref) { mutableStateOf(null) }
    }
}