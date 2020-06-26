package de.fabmax.kool.util.gltf

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolException
import de.fabmax.kool.util.DataStream
import de.fabmax.kool.util.logW
import kotlinx.coroutines.launch

fun AssetManager.loadGltfModel(assetPath: String, onLoad: (GltfFile?) -> Unit) {
    launch {
        val model = when {
            assetPath.endsWith(".gltf", true) || assetPath.endsWith(".gltf.gz", true) -> loadGltf(assetPath)
            assetPath.endsWith(".glb", true) || assetPath.endsWith(".glb.gz", true)-> loadGlb(assetPath)
            else -> null
        }

        val modelBasePath = if (assetPath.contains('/')) {
            assetPath.substring(0, assetPath.lastIndexOf('/'))
        } else { "." }
        model?.let { m ->
            m.buffers.filter { it.uri != null }.forEach {
                val uri = it.uri!!
                val bufferPath = if (uri.startsWith("data:", true)) { uri } else { "$modelBasePath/$uri" }
                it.data = loadAsset(bufferPath)!!
            }
            m.images.filter { it.uri != null }.forEach { it.uri = "$modelBasePath/${it.uri}" }
            m.updateReferences()
        }

        onLoad(model)
    }
}

private suspend fun AssetManager.loadGltf(assetPath: String): GltfFile? {
    var data = loadAsset(assetPath)
    if (data != null && assetPath.endsWith(".gz", true)) {
        data = inflate(data)
    }
    return if (data != null) { GltfFile.fromJson(data.toArray().decodeToString()) } else { null }
}

private suspend fun AssetManager.loadGlb(assetPath: String): GltfFile? {
    var data = loadAsset(assetPath) ?: return null
    if (assetPath.endsWith(".gz", true)) {
        data = inflate(data)
    }
    val str = DataStream(data)

    // file header
    val magic = str.readUInt()
    val version = str.readUInt()
    //val fileLength = str.readUInt()
    str.readUInt()
    if (magic != GltfFile.GLB_FILE_MAGIC) {
        throw KoolException("Unexpected glTF magic number: $magic (should be ${GltfFile.GLB_FILE_MAGIC} / 'glTF')")
    }
    if (version != 2) {
        logW { "Unexpected glTF version: $version (should be 2) - stuff might not work as expected" }
    }

    // chunk 0 - JSON content
    var chunkLen = str.readUInt()
    var chunkType = str.readUInt()
    if (chunkType != GltfFile.GLB_CHUNK_MAGIC_JSON) {
        throw KoolException("Unexpected chunk type for chunk 0: $chunkType (should be ${GltfFile.GLB_CHUNK_MAGIC_JSON} / 'JSON')")
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
