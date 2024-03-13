package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.scene.Model
import de.fabmax.kool.util.DataStream
import de.fabmax.kool.util.inflate
import de.fabmax.kool.util.logW
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async

suspend fun AssetLoader.loadGltfFile(assetPath: String): GltfFile = loadGltfFileAsync(assetPath).await()

suspend fun AssetLoader.loadGltfModel(
    assetPath: String,
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Model = loadGltfModelAsync(assetPath, modelCfg, scene).await()

fun AssetLoader.loadGltfFileAsync(assetPath: String): Deferred<GltfFile> = Assets.async {
    val blob = loadBlobAsset(assetPath)
    GltfFile(blob, assetPath)
}

fun AssetLoader.loadGltfModelAsync(
    assetPath: String,
    modelCfg: GltfLoadConfig = GltfLoadConfig(),
    scene: Int = 0
): Deferred<Model> = Assets.async {
    loadGltfFileAsync(assetPath).await().makeModel(modelCfg, scene)
}

suspend fun Assets.loadGltfFile(assetPath: String): GltfFile = defaultLoader.loadGltfFileAsync(assetPath).await()
suspend fun Assets.loadGltfModel(assetPath: String, modelCfg: GltfLoadConfig = GltfLoadConfig(), scene: Int = 0): Model = defaultLoader.loadGltfModelAsync(assetPath, modelCfg, scene).await()
fun Assets.loadGltfFileAsync(assetPath: String): Deferred<GltfFile> = defaultLoader.loadGltfFileAsync(assetPath)
fun Assets.loadGltfModelAsync(assetPath: String, modelCfg: GltfLoadConfig = GltfLoadConfig(), scene: Int = 0): Deferred<Model> = defaultLoader.loadGltfModelAsync(assetPath, modelCfg, scene)

private fun isGltf(assetPath: String): Boolean {
    return assetPath.endsWith(".gltf", true) || assetPath.endsWith(".gltf.gz", true)
}

private fun isBinaryGltf(assetPath: String): Boolean {
    return assetPath.endsWith(".glb", true) || assetPath.endsWith(".glb.gz", true)
}

private suspend fun Assets.loadGltf(assetPath: String): GltfFile {
    var data = loadBlobAsset(assetPath)
    if (assetPath.endsWith(".gz", true)) {
        data = data.inflate()
    }
    return GltfFile.fromJson(data.toArray().decodeToString())
}

private suspend fun Assets.loadGlb(assetPath: String): GltfFile {
    var data = loadBlobAsset(assetPath)
    if (assetPath.endsWith(".gz", true)) {
        data = data.inflate()
    }
    val str = DataStream(data)

    // file header
    val magic = str.readUInt()
    val version = str.readUInt()
    //val fileLength = str.readUInt()
    str.readUInt()
    if (magic != GltfFile.GLB_FILE_MAGIC) {
        error("Unexpected glTF magic number: $magic (should be ${GltfFile.GLB_FILE_MAGIC} / 'glTF')")
    }
    if (version != 2) {
        logW { "Unexpected glTF version: $version (should be 2) - stuff might not work as expected" }
    }

    // chunk 0 - JSON content
    var chunkLen = str.readUInt()
    var chunkType = str.readUInt()
    if (chunkType != GltfFile.GLB_CHUNK_MAGIC_JSON) {
        error("Unexpected chunk type for chunk 0: $chunkType (should be ${GltfFile.GLB_CHUNK_MAGIC_JSON} / 'JSON')")
    }
    val jsonData = str.readData(chunkLen).toArray()
    val model = GltfFile.fromJson(jsonData.decodeToString())

    // remaining data chunks
    var iChunk = 1
    while (str.hasRemaining()) {
        chunkLen = str.readUInt()
        chunkType = str.readUInt()
        if (chunkType == GltfFile.GLB_CHUNK_MAGIC_BIN) {
            model.buffers[iChunk-1].data = str.readData(chunkLen)

        } else {
            logW { "Unexpected chunk type for chunk $iChunk: $chunkType (should be ${GltfFile.GLB_CHUNK_MAGIC_BIN} / ' BIN')" }
            str.index += chunkLen
        }
        iChunk++
    }

    return model
}
