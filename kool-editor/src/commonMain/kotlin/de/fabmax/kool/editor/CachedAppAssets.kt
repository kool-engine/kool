package de.fabmax.kool.editor

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.editor.api.AppAssetsLoader
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.gltf.loadGltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.BufferedTextureLoader
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.TextureProps
import de.fabmax.kool.pipeline.ibl.EnvironmentHelper
import de.fabmax.kool.pipeline.ibl.EnvironmentMaps
import de.fabmax.kool.util.*

class CachedAppAssets(override val assetLoader: AssetLoader) : AppAssetsLoader {
    private val loadedHdris = mutableMapOf<AssetReference.Hdri, MutableStateValue<EnvironmentMaps?>>()
    private val loadedModels = mutableMapOf<AssetReference.Model, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<AssetReference.Texture, MutableStateValue<Texture2d?>>()
    private val loadedHeightmaps = mutableMapOf<AssetReference.Heightmap, MutableStateValue<Heightmap?>>()
    private val loadedBlobs = mutableMapOf<AssetReference.Blob, MutableStateValue<Uint8Buffer?>>()

    private val assetRefsByPath = mutableMapOf<String, MutableSet<AssetReference>>()

    override suspend fun loadHdri(ref: AssetReference.Hdri): EnvironmentMaps? {
        val hdriState = loadedHdris.getOrPut(ref) { mutableStateOf(null) }
        return try {
            val path = requireNotNull(ref.path) { "invalid AssetReference: path is null" }
            assetRefsByPath.getOrPut(path) { mutableSetOf() } += ref
            val hdriTex = assetLoader.loadTexture2d(path)
            hdriState.value ?: EnvironmentHelper.hdriEnvironment(hdriTex).also { hdriState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading HDRI: ${ref.path}" }
            null
        }
    }

    override suspend fun loadModel(ref: AssetReference.Model): GltfFile? {
        val modelState = loadedModels.getOrPut(ref) { mutableStateOf(null) }
        return try {
            val path = requireNotNull(ref.path) { "invalid AssetReference: path is null" }
            assetRefsByPath.getOrPut(path) { mutableSetOf() } += ref
            modelState.value ?: assetLoader.loadGltfFile(path).also { modelState.set(it) }
        } catch (e: Exception) {
            logE { "Failed loading model: ${ref.path}" }
            null
        }
    }

    override suspend fun loadTexture2d(ref: AssetReference.Texture): Texture2d? {
        val texState = loadedTextures2d.getOrPut(ref) { mutableStateOf(null) }
        return try {
            val path = requireNotNull(ref.path) { "invalid AssetReference: path is null" }
            assetRefsByPath.getOrPut(path) { mutableSetOf() } += ref
            texState.value ?: assetLoader.loadTexture2d(path, TextureProps(ref.texFormat)).also { texState.set(it) }
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
            val path = requireNotNull(ref.path) { "invalid AssetReference: path is null" }
            assetRefsByPath.getOrPut(path) { mutableSetOf() } += ref
            val blob = assetLoader.loadBlobAsset(path)
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
            val path = requireNotNull(ref.path) { "invalid AssetReference: path is null" }
            assetRefsByPath.getOrPut(path) { mutableSetOf() } += ref
            blobState.value ?: assetLoader.loadBlobAsset(path).also { blobState.set(it) }
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

    internal fun reloadAsset(assetItem: AssetItem) {
        val assetRefs = assetRefsByPath[assetItem.path] ?: return
        assetRefs.forEach { ref ->
            when (ref) {
                is AssetReference.Texture -> {
                    logD { "Texture ${assetItem.path} changed on disc, reloading..." }
                    loadedTextures2d[ref]?.value?.reloadTexture(assetItem.path)
                }
                else -> {
                    logW { "Loaded asset ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                }
            }
        }
    }

    private fun Texture2d.reloadTexture(texPath: String) {
        val bufferedLoader = loader as? BufferedTextureLoader
        if (bufferedLoader != null) {
            launchOnMainThread {
                bufferedLoader.data = assetLoader.loadTextureData(texPath, props)
                dispose()
            }
        } else {
            logW { "Failed reloading texture: $texPath, loader is not a BufferedTextureLoader $loader" }
        }
    }
}