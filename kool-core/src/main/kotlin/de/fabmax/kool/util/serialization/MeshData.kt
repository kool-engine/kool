package de.fabmax.kool.util.serialization

import de.fabmax.kool.KoolException
import de.fabmax.kool.shading.AttributeType
import de.fabmax.kool.util.serialization.MeshData.Companion.ATTRIB_POSITIONS
import kotlinx.serialization.Optional
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class MeshData(
    /**
     * Name of the mesh
     */
    @SerialId(1) val name: String,

    /**
     * Primitive type of included geometry
     */
    @SerialId(2) val primitiveType: PrimitiveType,

    /**
     * List of vertex indices. If empty each n vertices form a primitive with n = 1 (points), 2 (lines), 3 (triangles)
     */
    @SerialId(3) @Optional val indices: List<Int> = emptyList(),

    /**
     * Map of vertex attributes. Must at least contain [ATTRIB_POSITIONS]. Can also contain custom attributes with
     * arbitrary names.
     */
    @SerialId(4) val attributes: Map<String, AttributeList>,

    /**
     * List of bones forming the mesh armature. Might be empty.
     */
    @SerialId(5) @Optional val armature: List<BoneData> = emptyList(),

    /**
     * List of mesh armature animations. Might be empty.
     */
    @SerialId(6) @Optional val animations: List<AnimationData> = emptyList()
) {

    @Transient var numVertices: Int = 0
        private set
    @Transient var hasNormals: Boolean = false
        private set
    @Transient var hasTexCoords: Boolean = false
        private set
    @Transient var hasColors: Boolean = false
        private set
    @Transient var hasTangents: Boolean = false
        private set

    init {
        numVertices = (attributes[ATTRIB_POSITIONS] ?: throw KoolException("MeshData does not contain positions")).size / 3
        hasNormals = attributes.containsKey(ATTRIB_NORMALS)
        hasTexCoords = attributes.containsKey(ATTRIB_TEXTURE_COORDS)
        hasColors = attributes.containsKey(ATTRIB_COLORS)
        hasTangents = attributes.containsKey(ATTRIB_TANGENTS)

        for ((name, attrib) in attributes) {
            if (attrib.size / attrib.type.size != numVertices) {
                throw KoolException("Mesh attribute $name has wrong value count: ${attrib.size} (should be ${numVertices * attrib.type.size}, type: ${attrib.type})")
            }
        }
    }

    companion object {
        const val ATTRIB_POSITIONS = "positions"
        const val ATTRIB_NORMALS = "normals"
        const val ATTRIB_TEXTURE_COORDS = "textureCoords"
        const val ATTRIB_COLORS = "colors"
        const val ATTRIB_TANGENTS = "tangents"
    }
}

enum class PrimitiveType {
    LINES,
    POINTS,
    TRIANGLES
}

@Serializable
data class AttributeList(
        /**
         * Type of the attribute
         */
        @SerialId(1) val type: AttributeType,

        /**
         * Attribute values. There are [type].size values per vertex
         */
        @SerialId(2) val values: List<Float>
) {
    @Transient val size: Int = values.size

    operator fun get(i: Int): Float = values[i]
}
