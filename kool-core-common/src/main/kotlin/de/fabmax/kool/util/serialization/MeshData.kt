package de.fabmax.kool.util.serialization

import de.fabmax.kool.KoolException
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.scene.animation.Bone
import de.fabmax.kool.shading.*
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
     * Map of vertex attributes. Must at least contain [ATTRIB_POSITIONS]. Can also contain custom attributes with
     * arbitrary names.
     */
    @SerialId(3) val attributes: Map<String, AttributeList>,

    /**
     * List of vertex indices. If empty each n vertices form a primitive with n = 1 (points), 2 (lines), 3 (triangles)
     */
    @SerialId(4) @Optional val indices: List<Int> = emptyList(),

    /**
     * List of bones forming the mesh armature. Might be empty.
     */
    @SerialId(5) @Optional val armature: List<BoneData> = emptyList(),

    /**
     * List of mesh armature animations. Might be empty.
     */
    @SerialId(6) @Optional val animations: List<AnimationData> = emptyList(),

    /**
     * Material index. -1 if not preset.
     */
    @SerialId(7) @Optional val material: Int = -1,

    /**
     * Optional list of arbitrary tags.
     */
    @SerialId(8) @Optional val tags: List<String> = emptyList()
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

    fun toMesh(model: ModelData? = null, generateNormals: Boolean = true, generateTangents: Boolean = false): Mesh {
        val attribs = mutableSetOf(Attribute.POSITIONS)
        if (hasNormals || generateNormals) { attribs += Attribute.NORMALS }
        if (hasTangents || generateTangents) { attribs += Attribute.TANGENTS }
        if (hasColors) { attribs += Attribute.COLORS }
        if (hasTexCoords) { attribs += Attribute.TEXTURE_COORDS }

        val meshData = de.fabmax.kool.scene.MeshData(attribs)

        // add mesh vertices
        val positions = attributes[ATTRIB_POSITIONS] ?: throw KoolException("Mesh has no positions")
        val normals = attributes[MeshData.ATTRIB_NORMALS]
        val texCoords = attributes[MeshData.ATTRIB_TEXTURE_COORDS]
        val colors = attributes[MeshData.ATTRIB_COLORS]
        val tangents = attributes[MeshData.ATTRIB_TANGENTS]
        for (i in 0 until positions.size / 3) {
            meshData.addVertex {
                position.set(positions[i*3], positions[i*3+1], positions[i*3+2])
                if (normals != null) {
                    normal.set(normals[i*3], normals[i*3+1], normals[i*3+2])
                }
                if (texCoords != null) {
                    texCoord.set(texCoords[i*2], texCoords[i*2+1])
                }
                if (colors != null) {
                    color.set(colors[i*4], colors[i*4+1], colors[i*4+2], colors[i*4+3])
                }
                if (tangents!= null) {
                    tangent.set(tangents[i*3], tangents[i*3+1], tangents[i*3+2])
                }
            }
        }

        // add vertex indices
        if (indices.isEmpty()) {
            for (i in 0 until numVertices) {
                meshData.addIndex(i)
            }
        } else {
            meshData.addIndices(indices)
        }

        if (!hasNormals && generateNormals) {
            meshData.generateNormals()
        }
        if (!hasTangents && generateTangents) {
            meshData.generateTangents()
        }

        val mesh = if (!armature.isEmpty()) {
            buildAramature(meshData)
        } else {
            Mesh(meshData, name)
        }

        if (model != null && material in model.materials.indices) {
            mesh.shader = basicShader {
                lightModel = LightModel.PHONG_LIGHTING
                colorModel = ColorModel.STATIC_COLOR
                staticColor = model.materials[material].getDiffuseColor()
            }
        }

        return mesh
    }


    private fun buildAramature(meshData: de.fabmax.kool.scene.MeshData): Armature {
        // create armature with bones
        val mesh = Armature(meshData, name)

        // 1st pass: create bones
        armature.forEach {
            val bone = Bone(it.name, it.vertexIds.size)
            mesh.bones[bone.name] = bone

            bone.offsetMatrix.set(it.offsetMatrix)
            for (i in it.vertexIds.indices) {
                bone.vertexIds[i] = it.vertexIds[i]
                bone.vertexWeights[i] = it.vertexWeights[i]
            }
        }

        // 2nd pass: build bone hierarchy
        armature.forEach {
            val bone = mesh.bones[it.name]!!
            bone.parent = mesh.bones[it.parent]
            if (bone.parent == null) {
                mesh.rootBones += bone
            }

            it.children.forEach { childName ->
                val child = mesh.bones[childName]
                if (child != null) {
                    bone.children += child
                }
            }
        }

        // apply bones to mesh data
        mesh.updateBones()
        // load animations
        animations.forEach { mesh.addAnimation(it.name, it.getAnimation(mesh.bones)) }
        return mesh
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
        @SerialId(2) val values: MutableList<Float>
) {
    @Transient val size: Int = values.size

    operator fun get(i: Int): Float = values[i]
}
