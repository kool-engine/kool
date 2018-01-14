package de.fabmax.kool.util.serialization

import kotlinx.serialization.Optional
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable

@Serializable
data class MeshData(
    /**
     * Name of the mesh
     */
    @SerialId(1) val name: String,

    /**
     * List of triangle vertex IDs: Three IDs per triangle.
     */
    @SerialId(2) val triangles: List<Int>,

    /**
     * Vertex positions: Three floats per vertex position. Vertex positions are always present.
     */
    @SerialId(3) val positions: List<Float>,

    /**
     * Vertex normals: Three floats per vertex normal. Might be empty if vertices don't have normals.
     */
    @SerialId(4) @Optional val normals: List<Float> = emptyList(),

    /**
     * Vertex texture coordinates: Two floats per vertex texture coordinate. Might be empty if vertices don't have
     * texture coordinates.
     */
    @SerialId(5) @Optional val uvs: List<Float> = emptyList(),

    /**
     * Vertex colors: Four floats per vertex color (rgba). Might be empty if vertices don't have colors.
     */
    @SerialId(6) @Optional val colors: List<Float> = emptyList(),

    /**
     * List of bones forming the mesh armature. Might be empty.
     */
    @SerialId(7) @Optional val armature: List<BoneData> = emptyList(),

    /**
     * List of mesh armature animations. Might be empty.
     */
    @SerialId(8) @Optional val animations: List<AnimationData> = emptyList()
) {

    fun hasNormals(): Boolean = !normals.isEmpty()

    fun hasTexCoords(): Boolean = !uvs.isEmpty()

    fun hasColors(): Boolean = !colors.isEmpty()

}
