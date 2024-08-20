package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.scene.Model
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

suspend fun AssetLoader.loadGltfFile(assetPath: String): Result<GltfFile> = loadGltfFileAsync(assetPath).await()

fun AssetLoader.loadGltfFileAsync(assetPath: String): Deferred<Result<GltfFile>> = Assets.async {
    loadBlob(assetPath).mapCatching { GltfFile(it, assetPath, this@loadGltfFileAsync).getOrThrow() }
}

suspend fun AssetLoader.loadGltfModel(
    assetPath: String,
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Result<Model> = loadGltfModelAsync(assetPath, modelCfg, scene).await()

fun AssetLoader.loadGltfModelAsync(
    assetPath: String,
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Deferred<Result<Model>> = Assets.async {
    val cfg = if (modelCfg.assetLoader == null) modelCfg.copy(assetLoader = this@loadGltfModelAsync) else modelCfg
    loadGltfFileAsync(assetPath).await().mapCatching { it.makeModel(cfg, scene) }
}

suspend fun Assets.loadGltfFile(assetPath: String): Result<GltfFile> =
    defaultLoader.loadGltfFileAsync(assetPath).await()
suspend fun Assets.loadGltfModel(assetPath: String, modelCfg: GltfLoadConfig = GltfLoadConfig(), scene: Int = 0): Result<Model> =
    defaultLoader.loadGltfModelAsync(assetPath, modelCfg, scene).await()

fun Assets.loadGltfFileAsync(assetPath: String): Deferred<Result<GltfFile>> =
    defaultLoader.loadGltfFileAsync(assetPath)
fun Assets.loadGltfModelAsync(assetPath: String, modelCfg: GltfLoadConfig = GltfLoadConfig(), scene: Int = 0): Deferred<Result<Model>> =
    defaultLoader.loadGltfModelAsync(assetPath, modelCfg, scene)
