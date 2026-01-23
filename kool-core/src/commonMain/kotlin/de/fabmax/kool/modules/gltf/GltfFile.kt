package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.Assets
import de.fabmax.kool.scene.Model
import de.fabmax.kool.scene.ModelTemplate
import de.fabmax.kool.util.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

suspend fun GltfFile(data: Uint8Buffer, filePath: String, assetLoader: AssetLoader = Assets.defaultLoader): Result<GltfFile> {
    return try {
        val gltfData = if (filePath.lowercase().endsWith(".gz")) data.inflate() else data
        val gltfFile = when (val type = filePath.lowercase().removeSuffix(".gz").substringAfterLast('.')) {
            "gltf" -> GltfFile.fromJson(gltfData.decodeToString())
            "glb" -> loadGlb(gltfData)
            else -> error("Invalid gltf file type: $type ($filePath)")
        }

        val modelBasePath = if (filePath.contains('/')) filePath.substringBeforeLast('/') else "."
        gltfFile.let { m ->
            m.buffers.filter { it.uri != null }.forEach {
                val uri = it.uri!!
                val bufferUri = if (uri.startsWith("data:", true)) { uri } else { "$modelBasePath/$uri" }
                it.data = assetLoader.loadBlob(bufferUri).getOrThrow()
            }
            m.images.filter { it.uri != null }.forEach {
                val uri = it.uri!!
                val imageUri = if (uri.startsWith("data:", true)) { uri } else { "$modelBasePath/$uri" }
                it.uri = imageUri
            }
            m.updateReferences()
        }
        Result.success(gltfFile)
    } catch (t: Throwable) {
        Result.failure(t)
    }
}

private fun loadGlb(data: Uint8Buffer): GltfFile {
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
        logW("loadGlb") { "Unexpected glTF version: $version (should be 2) - stuff might not work as expected" }
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
            logW("loadGlb") { "Unexpected chunk type for chunk $iChunk: $chunkType (should be ${GltfFile.GLB_CHUNK_MAGIC_BIN} / ' BIN')" }
            str.index += chunkLen
        }
        iChunk++
    }

    return model
}

/**
 * The root object for a glTF asset.
 *
 * @param extensionsUsed     Names of glTF extensions used somewhere in this asset.
 * @param extensionsRequired Names of glTF extensions required to properly load this asset.
 * @param accessors          An array of accessors.
 * @param animations         An array of keyframe animations.
 * @param asset              Metadata about the glTF asset.
 * @param buffers            An array of buffers.
 * @param bufferViews        An array of bufferViews.
 * @param images             An array of images.
 * @param materials          An array of materials.
 * @param meshes             An array of meshes.
 * @param nodes              An array of nodes.
 * @param scene              The index of the default scene
 * @param scenes             An array of scenes.
 * @param skins              An array of skins.
 * @param textures           An array of textures.
 */
@Serializable
data class GltfFile(
    val extensionsUsed: List<String> = emptyList(),
    val extensionsRequired: List<String> = emptyList(),
    val accessors: List<GltfAccessor> = emptyList(),
    val animations: List<GltfAnimation> = emptyList(),
    val asset: GltfAsset,
    val buffers: List<GltfBuffer> = emptyList(),
    val bufferViews: List<GltfBufferView> = emptyList(),
    //val cameras List<Camera> = emptyList(),
    val images: List<GltfImage> = emptyList(),
    val materials: List<GltfMaterial> = emptyList(),
    val meshes: List<GltfMesh> = emptyList(),
    val nodes: List<GltfNode> = emptyList(),
    val samplers: List<GltfSampler> = emptyList(),
    val scene: Int = 0,
    val scenes: List<GltfScene> = emptyList(),
    val skins: List<GltfSkin> = emptyList(),
    val textures: List<GltfTexture> = emptyList()
) {

    @Deprecated(
        message = "Use makeModelTemplate if creating multiple Models or makeSingleModel if creating a single Model from this GltfFile",
        ReplaceWith("makeSingleModel(scene, modelCfg)")
    )
    fun makeModel(modelCfg: GltfLoadConfig = GltfLoadConfig(), scene: Int = this.scene): Model {
        return makeSingleModel(scene, modelCfg)
    }

    /**
     * Create a model template from this GltfFile. If you only need a single instances use [makeModel] instead.
     * @see GltfFile.makeModel
     */
    fun makeModelTemplate(scene: Int = this.scene): ModelTemplate {
        return ModelTemplate(scenes[scene], this)
    }

    /**
     * Create a single model from this GltfFile. If you want to create multiple instances use [makeModelTemplate] instead.
     * @see GltfFile.makeModelTemplate
     */
    fun makeSingleModel(scene: Int = this.scene, modelCfg: GltfLoadConfig = GltfLoadConfig()): Model {
        val template = makeModelTemplate(scene)
        return template.makeModel(modelCfg).apply {
            addDependingReleasable(template)
        }
    }

    internal fun updateReferences() {
        accessors.forEach {
            if (it.bufferView >= 0) {
                it.bufferViewRef = bufferViews[it.bufferView]
            }
            it.sparse?.let { sparse ->
                sparse.indices.bufferViewRef = bufferViews[sparse.indices.bufferView]
                sparse.values.bufferViewRef = bufferViews[sparse.values.bufferView]
            }
        }
        animations.forEach { anim ->
            anim.samplers.forEach {
                it.inputAccessorRef = accessors[it.input]
                it.outputAccessorRef = accessors[it.output]
            }
            anim.channels.forEach {
                it.samplerRef = anim.samplers[it.sampler]
                if (it.target.node >= 0) {
                    it.target.nodeRef = nodes[it.target.node]
                }
            }
        }
        bufferViews.forEach { it.bufferRef = buffers[it.buffer] }
        images.filter { it.bufferView >= 0 }.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
        meshes.forEach { mesh ->
            mesh.primitives.forEach {
                if (it.material >= 0) {
                    it.materialRef = materials[it.material]
                }
            }
        }
        nodes.forEach {
            it.childRefs = it.children.map { iNd -> nodes[iNd] }
            if (it.mesh >= 0) {
                it.meshRef = meshes[it.mesh]
            }
            if (it.skin >= 0) {
                it.skinRef = skins[it.skin]
            }
        }
        scenes.forEach { it.nodeRefs = it.nodes.map { iNd -> nodes[iNd] } }
        skins.forEach {
            if (it.inverseBindMatrices >= 0) {
                it.inverseBindMatrixAccessorRef = accessors[it.inverseBindMatrices]
            }
            it.jointRefs = it.joints.map { iJt -> nodes[iJt] }
        }
        textures.forEach {
            it.imageRef = images[it.source]
            it.samplerRef = samplers.getOrNull(it.sampler)
        }
    }

    companion object {
        const val GLB_FILE_MAGIC = 0x46546c67
        const val GLB_CHUNK_MAGIC_JSON = 0x4e4f534a
        const val GLB_CHUNK_MAGIC_BIN = 0x004e4942

        private val jsonFmt = Json {
            isLenient = true
            ignoreUnknownKeys = true
            allowSpecialFloatingPointValues = true
            useArrayPolymorphism = true
        }

        fun fromJson(json: String): GltfFile {
            return jsonFmt.decodeFromString(json)
        }
    }
}
