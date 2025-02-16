package de.fabmax.kool.editor

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.editor.api.AssetReference
import de.fabmax.kool.editor.api.DefaultLoader
import de.fabmax.kool.modules.gltf.GltfFile
import de.fabmax.kool.modules.ui2.MutableStateValue
import de.fabmax.kool.modules.ui2.mutableStateOf
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.Texture2dArray
import de.fabmax.kool.pipeline.ibl.EnvironmentMap
import de.fabmax.kool.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CachedAppAssets(override val assetLoader: AssetLoader) : DefaultLoader("") {
    private val loadedHdris = mutableMapOf<AssetReference.Hdri, MutableStateValue<EnvironmentMap?>>()
    private val loadedModels = mutableMapOf<AssetReference.Model, MutableStateValue<GltfFile?>>()
    private val loadedTextures2d = mutableMapOf<AssetReference.Texture, MutableStateValue<Texture2d?>>()
    private val loadedTextures2dArray = mutableMapOf<AssetReference.TextureArray, MutableStateValue<Texture2dArray?>>()
    private val loadedHeightmaps = mutableMapOf<AssetReference.Heightmap, MutableStateValue<Heightmap?>>()
    private val loadedBlobs = mutableMapOf<AssetReference.Blob, MutableStateValue<Uint8Buffer?>>()

    private val assetRefsByPath = mutableMapOf<String, MutableSet<AssetReference>>()

    override suspend fun loadHdri(ref: AssetReference.Hdri): Result<EnvironmentMap> {
        assetRefsByPath.getOrPut(ref.path) { mutableSetOf() } += ref
        val state = loadedHdris.getOrPut(ref) { mutableStateOf(null) }
        return state.value?.let { Result.success(it) } ?: super.loadHdri(ref).onSuccess { state.set(it) }
    }

    override suspend fun loadModel(ref: AssetReference.Model): Result<GltfFile> {
        assetRefsByPath.getOrPut(ref.path) { mutableSetOf() } += ref
        val state = loadedModels.getOrPut(ref) { mutableStateOf(null) }
        return state.value?.let { Result.success(it) } ?: super.loadModel(ref).onSuccess { state.set(it) }
    }

    override suspend fun loadTexture2d(ref: AssetReference.Texture): Result<Texture2d> {
        assetRefsByPath.getOrPut(ref.path) { mutableSetOf() } += ref
        val state = loadedTextures2d.getOrPut(ref) { mutableStateOf(null) }
        return super.loadTexture2d(ref).onSuccess { state.set(it) }
    }

    override suspend fun loadTexture2dArray(ref: AssetReference.TextureArray): Result<Texture2dArray> {
        ref.paths.forEach { assetRefsByPath.getOrPut(it) { mutableSetOf() } += ref }
        val state = loadedTextures2dArray.getOrPut(ref) { mutableStateOf(null) }
        return super.loadTexture2dArray(ref).onSuccess { state.set(it) }
    }

    override suspend fun loadHeightmap(ref: AssetReference.Heightmap): Result<Heightmap> {
        assetRefsByPath.getOrPut(ref.path) { mutableSetOf() } += ref
        loadedHeightmaps.keys.removeAll { it.path == ref.path && it != ref }
        val state = loadedHeightmaps.getOrPut(ref) { mutableStateOf(null) }
        return state.value?.let { Result.success(it) } ?: super.loadHeightmap(ref).onSuccess { state.set(it) }
    }

    override suspend fun loadBlob(ref: AssetReference.Blob): Result<Uint8Buffer> {
        assetRefsByPath.getOrPut(ref.path) { mutableSetOf() } += ref
        val state = loadedBlobs.getOrPut(ref) { mutableStateOf(null) }
        return state.value?.let { Result.success(it) } ?: super.loadBlob(ref).onSuccess { state.set(it) }
    }

    fun getHdriEnvironmentMutableState(ref: AssetReference.Hdri): MutableStateValue<EnvironmentMap?> {
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

    fun getTextureIfLoaded(ref: AssetReference.Texture): Texture2d? {
        return loadedTextures2d[ref]?.value
    }

    internal fun reloadAsset(assetItem: AssetItem) {
        val assetRefs = assetRefsByPath[assetItem.path]

        launchOnMainThread {
            assetRefs?.forEach { ref ->
                when (ref) {
                    is AssetReference.Texture -> {
                        val asset = loadedTextures2d[ref]?.value
                        if (asset != null) {
                            logD { "Texture ${assetItem.path} changed on disc, reloading..." }
                            asset.reloadTexture(assetItem.path)
                        }
                    }
                    is AssetReference.TextureArray -> {
                        val asset = loadedTextures2dArray.remove(ref)?.value
                        if (asset != null) {
                            logW { "Texture array element ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                        }
                    }
                    is AssetReference.Blob -> {
                        val asset = loadedBlobs.remove(ref)?.value
                        if (asset != null) {
                            logW { "Blob ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                        }
                    }
                    is AssetReference.Hdri -> {
                        val asset = loadedHdris.remove(ref)?.value
                        if (asset != null) {
                            logW { "HDRI ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                        }
                    }
                    is AssetReference.Heightmap -> {
                        val asset = loadedHeightmaps.remove(ref)?.value
                        if (asset != null) {
                            logW { "Heightmap ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                        }
                    }
                    is AssetReference.Model -> {
                        val asset = loadedModels.remove(ref)?.value
                        if (asset != null) {
                            logW { "Model ${assetItem.path} changed on disc, but hot-reload is not yet implemented" }
                        }
                    }
                }
            }
            KoolEditor.instance.ui.assetBrowser.onAssetItemChanged(assetItem)
        }
    }

    private suspend fun Texture2d.reloadTexture(texPath: String) {
        assetLoader.loadImage2d(texPath, format).getOrNull()?.let {
            withContext(Dispatchers.RenderLoop) {
                upload(it)
            }
        }
    }
}