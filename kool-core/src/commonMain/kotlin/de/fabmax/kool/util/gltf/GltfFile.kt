package de.fabmax.kool.util.gltf

import de.fabmax.kool.AssetManager
import de.fabmax.kool.KoolException
import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.TransformGroup
import de.fabmax.kool.toString
import de.fabmax.kool.util.*
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.UnstableDefault
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import kotlinx.serialization.parse

fun AssetManager.loadGltfModel(assetPath: String, onLoad: (GltfFile?) -> Unit) {
    launch {
        val model = when {
            assetPath.endsWith(".gltf", true) -> loadGltf(assetPath)
            assetPath.endsWith(".glb", true) -> loadGlb(assetPath)
            else -> null
        }

        val modelBasePath = assetPath.substring(0, assetPath.lastIndexOf('/'))
        model?.let { m ->
            m.buffers.filter { it.uri != null }.forEach {
                val uri = it.uri!!
                val bufferPath = if (uri.startsWith("data:", true)) { uri } else { "$modelBasePath/$uri" }
                it.data = loadAsset(bufferPath)!!
            }
            m.makeReferences()
        }

        onLoad(model)
    }
}

private suspend fun AssetManager.loadGltf(assetPath: String): GltfFile? {
    val data = loadAsset(assetPath)
    return if (data != null) { GltfFile.fromJson(data.toArray().decodeToString()) } else { null }
}

private suspend fun AssetManager.loadGlb(assetPath: String): GltfFile? {
    val data = loadAsset(assetPath) ?: return null
    val str = DataStream(data)

    // file header
    val magic = str.readUInt()
    val version = str.readUInt()
    val fileLength = str.readUInt()
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

    logD { "Loaded glTF model $assetPath (${(fileLength / 1024.0 / 1024.0).toString(1)} mb)" }

    return model
}

@Serializable
data class GltfFile(
        val asset: Asset,
        val scene: Int = 0,
        val scenes: List<Scene> = emptyList(),
        val nodes: List<Node> = emptyList(),
        val meshes: List<Mesh> = emptyList(),
        val materials: List<Material> = emptyList(),
        val textures: List<Texture> = emptyList(),
        val images: List<Image> = emptyList(),
        val buffers: List<Buffer> = emptyList(),
        val bufferViews: List<BufferView> = emptyList(),
        val accessors: List<Accessor> = emptyList(),
        val extensionsUsed: List<String> = emptyList(),
        val extensionsRequired: List<String> = emptyList()
) {

    fun makeModel(scene: Int = this.scene): TransformGroup {
        val scn = scenes[scene]
        val grp = TransformGroup(scn.name)
        scn.nodeRefs.forEach { nd ->
            grp += nd.makeNode()
        }
        return grp
    }

    private fun Node.makeNode(): TransformGroup {
        val grp = TransformGroup(name)

        if (matrix != null) {
            grp.transform.set(matrix.map { it.toDouble() })
        } else {
            if (translation != null) {
                grp.translate(translation[0], translation[1], translation[2])
            }
            if (rotation != null) {
                val rotMat = Mat4d().setRotate(Vec4d(rotation[0].toDouble(), rotation[1].toDouble(), rotation[2].toDouble(), rotation[3].toDouble()))
                grp.transform.mul(rotMat)
            }
            if (scale != null) {
                grp.scale(scale[0], scale[1], scale[2])
            }
        }

        childRefs.forEach {
            grp += it.makeNode()
        }

        meshRef?.primitives?.forEachIndexed { index, p ->
            val mesh = de.fabmax.kool.scene.Mesh(p.toGeometry(), "${name}_$index")
            grp += mesh
            mesh.pipelineLoader = pbrShader {
                val pbrMat = p.materialRef?.pbrMetallicRoughness
                if (pbrMat != null) {
                    if (pbrMat.baseColorTexture != null) {
                        val tex = textures[pbrMat.baseColorTexture.index]
                        albedoMap = tex.makeTexture()
                        albedoSource = Albedo.TEXTURE_ALBEDO
                    } else {
                        albedo = Color(pbrMat.baseColorFactor[0], pbrMat.baseColorFactor[1], pbrMat.baseColorFactor[2], pbrMat.baseColorFactor[3])
                        albedoSource = Albedo.STATIC_ALBEDO
                    }

                    if (pbrMat.metallicRoughnessTexture != null) {
                        val tex = textures[pbrMat.metallicRoughnessTexture.index]
                        metallicRoughnessMap = tex.makeTexture()
                        isMetallicRoughnessMapped = true
                    } else {
                        metallic = pbrMat.metallicFactor
                        roughness = pbrMat.roughnessFactor
                    }

                } else {
                    albedo = Color.GRAY
                    albedoSource = Albedo.STATIC_ALBEDO
                }

                if (p.materialRef?.normalTexture != null) {
                    val nrmTex = textures[p.materialRef?.normalTexture!!.index]
                    normalMap = nrmTex.makeTexture()
                    isNormalMapped = true
                }
            }
        }

        return grp
    }

    fun makeReferences() {
        accessors.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
        bufferViews.forEach { it.bufferRef = buffers[it.buffer] }
        meshes.forEach { mesh ->
            mesh.primitives.forEach {
                if (it.material >= 0) {
                    it.materialRef = materials[it.material]
                }
                if (it.indices >= 0) {
                    it.indexAccessorRef = accessors[it.indices]
                }
                it.attributes.forEach { (attrib, iAcc) ->
                    it.attribAccessorRefs[attrib] = accessors[iAcc]
                }
            }
        }
        scenes.forEach { it.nodeRefs = it.nodes.map { iNd -> nodes[iNd] } }
        nodes.forEach {
            it.childRefs = it.children.map { iNd -> nodes[iNd] }
            if (it.mesh >= 0) {
                it.meshRef = meshes[it.mesh]
            }
        }
        textures.forEach { it.imageRef = images[it.source] }
        images.forEach { it.bufferViewRef = bufferViews[it.bufferView] }
    }

    companion object {
        const val ACCESSOR_TYPE_SCALAR = "SCALAR"
        const val ACCESSOR_TYPE_VEC2 = "VEC2"
        const val ACCESSOR_TYPE_VEC3 = "VEC3"
        const val ACCESSOR_TYPE_VEC4 = "VEC4"

        const val COMP_TYPE_BYTE = 5120
        const val COMP_TYPE_UNSIGNED_BYTE = 5121
        const val COMP_TYPE_SHORT = 5122
        const val COMP_TYPE_UNSIGNED_SHORT = 5123
        const val COMP_TYPE_INT = 5124
        const val COMP_TYPE_UNSIGNED_INT = 5125
        const val COMP_TYPE_FLOAT = 5126

        const val MODE_POINTS = 0
        const val MODE_LINES = 1
        const val MODE_LINE_LOOP = 2
        const val MODE_LINE_STRIP = 3
        const val MODE_TRIANGLES = 4
        const val MODE_TRIANGLE_STRIP = 5
        const val MODE_TRIANGLE_FAN = 6
        const val MODE_QUADS = 7
        const val MODE_QUAD_STRIP = 8
        const val MODE_POLYGON = 9

        const val MESH_ATTRIBUTE_POSITION = "POSITION"
        const val MESH_ATTRIBUTE_NORMAL = "NORMAL"
        const val MESH_ATTRIBUTE_TANGENT = "TANGENT"
        const val MESH_ATTRIBUTE_TEXCOORD_0 = "TEXCOORD_0"
        const val MESH_ATTRIBUTE_TEXCOORD_1 = "TEXCOORD_1"
        const val MESH_ATTRIBUTE_COLOR_0 = "COLOR_0"
        const val MESH_ATTRIBUTE_JOINTS_0 = "JOINTS_0"
        const val MESH_ATTRIBUTE_WEIGHTS_0 = "WEIGHTS_0"

        const val GLB_FILE_MAGIC = 0x46546c67
        const val GLB_CHUNK_MAGIC_JSON = 0x4e4f534a
        const val GLB_CHUNK_MAGIC_BIN = 0x004e4942

        @OptIn(UnstableDefault::class)
        fun fromJson(json: String): GltfFile {
            return Json(JsonConfiguration(
                    isLenient = true,
                    ignoreUnknownKeys = true,
                    serializeSpecialFloatingPointValues = true,
                    useArrayPolymorphism = true
            )).parse(json)
        }
    }
}

@Serializable
data class Asset(
        val version: String,
        val generator: String? = null
)

@Serializable
data class Scene(
        val nodes: List<Int>,
        val name: String? = null
) {
    @Transient
    lateinit var nodeRefs: List<Node>
}

@Serializable
data class Node(
        val mesh: Int = -1,
        val name: String = "",
        val children: List<Int> = emptyList(),
        val translation: List<Float>? = null,
        val rotation: List<Float>? = null,
        val scale: List<Float>? = null,
        val matrix: List<Float>? = null
) {
    @Transient
    lateinit var childRefs: List<Node>
    @Transient
    var meshRef: Mesh? = null
}

@Serializable
data class Mesh(
        val primitives: List<MeshPrimitive>,
        val name: String? = null
)

@Serializable
data class MeshPrimitive(
        val attributes: Map<String, Int>,
        val indices: Int = -1,
        val material: Int = -1,
        val mode: Int = GltfFile.MODE_TRIANGLES
) {
    @Transient
    var materialRef: Material? = null
    @Transient
    var indexAccessorRef: Accessor? = null
    @Transient
    val attribAccessorRefs = mutableMapOf<String, Accessor>()

    fun toGeometry(): IndexedVertexList {
        val positionAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_POSITION]
        val normalAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_NORMAL]
        val tangentAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_TANGENT]
        val texCoordAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_TEXCOORD_0]
        val colorAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_COLOR_0]

        var generateTangents = false

        val attribs = mutableListOf<Attribute>()
        if (positionAcc != null) { attribs += Attribute.POSITIONS }
        if (normalAcc != null) { attribs += Attribute.NORMALS }
        if (colorAcc != null) { attribs += Attribute.COLORS }
        if (texCoordAcc != null) { attribs += Attribute.TEXTURE_COORDS }
        if (tangentAcc != null) {
            attribs += Attribute.TANGENTS
        } else if(materialRef?.normalTexture != null) {
            attribs += Attribute.TANGENTS
            generateTangents = true
        }

        val verts = IndexedVertexList(attribs)
        if (positionAcc != null) {
            val poss = Vec3fAccessor(positionAcc)
            val nrms = if (normalAcc != null) Vec3fAccessor(normalAcc) else null
            val tans = if (tangentAcc != null) Vec3fAccessor(tangentAcc) else null
            val texs = if (texCoordAcc != null) Vec2fAccessor(texCoordAcc) else null
            val cols = if (colorAcc != null) Vec4fAccessor(colorAcc) else null

            for (i in 0 until positionAcc.count) {
                verts.addVertex {
                    poss.next(position)
                    nrms?.next(normal)
                    tans?.next(tangent)
                    texs?.next(texCoord)
                    cols?.next()?.let { col -> color.set(col.x, col.y, col.z, col.w) }
                }
            }

            val indexAcc = indexAccessorRef
            if (indexAcc != null) {
                val inds = IntAccessor(indexAcc)
                for (i in 0 until indexAcc.count) {
                    verts.addIndex(inds.next())
                }
            } else {
                for (i in 0 until positionAcc.count) {
                    verts.addIndex(i)
                }
            }

            if (generateTangents) {
                verts.generateTangents()
            }
        }
        return verts
    }
}

@Serializable
data class Material(
        val doubleSided: Boolean = false,
        val name: String? = null,
        val pbrMetallicRoughness: PbrMetallicRoughness = PbrMetallicRoughness(baseColorFactor = listOf(0.5f, 0.5f, 0.5f, 1f)),
        val normalTexture: MaterialMap? = null,
        val occlusionTexture: MaterialMap? = null,
        val emissiveFactor: List<Float>? = null,
        val emissiveTexture: MaterialMap? = null,
        val alphaMode: String = "OPAQUE",
        val alphaCutoff: Float = 0.5f
)

@Serializable
data class PbrMetallicRoughness(
        val baseColorFactor: List<Float> = listOf(1f, 1f, 1f, 1f),
        val baseColorTexture: MaterialMap? = null,
        val metallicFactor: Float = 1f,
        val roughnessFactor: Float = 1f,
        val metallicRoughnessTexture: MaterialMap? = null
)

@Serializable
data class MaterialMap(
        val index: Int,
        val texCoord: Int,
        val scale: Float = 1f
)

@Serializable
data class Texture(
        val source: Int = 0,
        val name: String? = null
) {
    @Transient
    lateinit var imageRef: Image

    @Transient
    private var createdTex: de.fabmax.kool.pipeline.Texture? = null

    fun makeTexture(): de.fabmax.kool.pipeline.Texture {
        if (createdTex == null) {
            createdTex = de.fabmax.kool.pipeline.Texture { assetMgr ->
                assetMgr.createTextureData(imageRef.bufferViewRef.getData(), imageRef.mimeType ?: "image/png")
            }
        }
        return createdTex!!
    }
}

@Serializable
data class Image(
        val bufferView: Int,
        val mimeType: String? = null,
        val name: String? = null
) {
    @Transient
    lateinit var bufferViewRef: BufferView
}

@Serializable
data class Buffer(
        val byteLength: Int,
        val uri: String? = null
) {
    @Transient
    lateinit var data: Uint8Buffer
}

@Serializable
data class BufferView(
        val buffer: Int,
        val byteLength: Int,
        val byteOffset: Int = 0,
        val target: Int = 0
) {
    @Transient
    lateinit var bufferRef: Buffer

    fun getData(): Uint8Buffer {
        val array = createUint8Buffer(byteLength)
        for (i in 0 until byteLength) {
            array[i] = bufferRef.data[byteOffset + i]
        }
        return array
    }
}

@Serializable
data class Accessor(
        val bufferView: Int,
        val componentType: Int,
        val count: Int,
        val type: String,
        val byteOffset: Int = 0,
        val min: List<Float>? = null,
        val max: List<Float>? = null
) {
    @Transient
    lateinit var bufferViewRef: BufferView
}

private class IntAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_BYTE -> 1
        GltfFile.COMP_TYPE_UNSIGNED_BYTE -> 1
        GltfFile.COMP_TYPE_SHORT -> 2
        GltfFile.COMP_TYPE_UNSIGNED_SHORT -> 2
        GltfFile.COMP_TYPE_INT -> 4
        GltfFile.COMP_TYPE_UNSIGNED_INT -> 4
        else -> throw IllegalArgumentException("Provided accessor does not have integer component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_SCALAR) {
            throw IllegalArgumentException("IntAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_SCALAR}, provided was ${accessor.type}")
        }
    }

    fun next(): Int {
        return if (index < accessor.count) {
            when (accessor.componentType) {
                GltfFile.COMP_TYPE_BYTE -> stream.readByte()
                GltfFile.COMP_TYPE_UNSIGNED_BYTE -> stream.readUByte()
                GltfFile.COMP_TYPE_SHORT -> stream.readShort()
                GltfFile.COMP_TYPE_UNSIGNED_SHORT -> stream.readUShort()
                GltfFile.COMP_TYPE_INT -> stream.readInt()
                GltfFile.COMP_TYPE_UNSIGNED_INT -> stream.readUInt()
                else -> 0
            }

        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
    }
}

private class Vec2fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 2
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC2) {
            throw IllegalArgumentException("Vec2fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC2}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec2f = next(MutableVec2f())

    fun next(result: MutableVec2f): MutableVec2f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}

private class Vec3fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 3
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC3) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC3}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec3f = next(MutableVec3f())

    fun next(result: MutableVec3f): MutableVec3f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
            result.z = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}

private class Vec4fAccessor(val accessor: Accessor) {
    private val stream = DataStream(accessor.bufferViewRef.bufferRef.data, accessor.byteOffset + accessor.bufferViewRef.byteOffset)

    private val elemSize: Int = when (accessor.componentType) {
        GltfFile.COMP_TYPE_FLOAT -> 4 * 4
        else -> throw IllegalArgumentException("Provided accessor does not have float component type")
    }

    var index: Int
        get() = stream.index / elemSize
        set(value) {
            stream.index = value * elemSize
        }

    init {
        if (accessor.type != GltfFile.ACCESSOR_TYPE_VEC4) {
            throw IllegalArgumentException("Vec3fAccessor requires accessor type ${GltfFile.ACCESSOR_TYPE_VEC4}, provided was ${accessor.type}")
        }
    }

    fun next(): MutableVec4f = next(MutableVec4f())

    fun next(result: MutableVec4f): MutableVec4f {
        if (index < accessor.count) {
            result.x = stream.readFloat()
            result.y = stream.readFloat()
            result.z = stream.readFloat()
            result.w = stream.readFloat()
        } else {
            throw IndexOutOfBoundsException("Accessor overflow")
        }
        return result
    }
}
