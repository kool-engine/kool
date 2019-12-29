package de.fabmax.kool.util.serialization

import de.fabmax.kool.KoolException
import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.AttributeType
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.scene.animation.Armature
import de.fabmax.kool.scene.animation.Bone
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.serialization.ModelMeshData.Companion.ATTRIB_NORMALS_OCT_COMPRESSED
import de.fabmax.kool.util.serialization.ModelMeshData.Companion.ATTRIB_POSITIONS
import kotlinx.serialization.SerialId
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class ModelMeshData(
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
    @SerialId(4) val indices: List<Int> = emptyList(),

    /**
     * List of bones forming the mesh armature. Might be empty.
     */
    @SerialId(5) val armature: List<BoneData> = emptyList(),

    /**
     * List of mesh armature animations. Might be empty.
     */
    @SerialId(6) val animations: List<AnimationData> = emptyList(),

    /**
     * Material index. -1 if not preset.
     */
    @SerialId(7) val material: Int = -1,

    /**
     * Optional list of arbitrary tags.
     */
    @SerialId(8) val tags: List<String> = emptyList(),

    /**
     * Optional map of additional integer vertex attributes. E.g. compressed normals [ATTRIB_NORMALS_OCT_COMPRESSED]
     */
    @SerialId(9) val intAttributes: Map<String, IntAttributeList> = emptyMap()
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
        numVertices = (attributes[ATTRIB_POSITIONS] ?: throw KoolException("ModelMeshData does not contain positions")).size / 3
        hasNormals = attributes.containsKey(ATTRIB_NORMALS) || intAttributes.containsKey(ATTRIB_NORMALS_OCT_COMPRESSED)
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

        val meshData = MeshData(attribs)

        // add mesh vertices
        val positions = attributes[ATTRIB_POSITIONS] ?: throw KoolException("Mesh has no positions")
        val normals = attributes[ATTRIB_NORMALS]
        val texCoords = attributes[ATTRIB_TEXTURE_COORDS]
        val colors = attributes[ATTRIB_COLORS]
        val tangents = attributes[ATTRIB_TANGENTS]
        val normalsOct = intAttributes[ATTRIB_NORMALS_OCT_COMPRESSED]
        val octBits = getNormalOctBits()

        for (i in 0 until positions.size / 3) {
            meshData.addVertex {
                position.set(positions[i*3], positions[i*3+1], positions[i*3+2])
                if (normals != null) {
                    normal.set(normals[i*3], normals[i*3+1], normals[i*3+2])
                } else if (normalsOct != null && octBits > 0) {
                    NormalOctCoding.decodeOctToNormal(normalsOct[i*2], normalsOct[i*2+1], octBits, normal)
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

        // add tags
        if (tags.isNotEmpty()) {
            tags.forEach {
                mesh.tags += it
            }
        }

        return mesh
    }

    private fun getNormalOctBits(): Int {
        val octBitsKey = "$ATTRIB_NORMALS_OCT_COMPRESSED="
        val octBitsTag = tags.find { it.startsWith(octBitsKey) }
        if (octBitsTag != null) {
            return octBitsTag.substring(octBitsKey.length).toInt()
        }
        return -1
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
        const val ATTRIB_NORMALS_OCT_COMPRESSED = "normalsOct"
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

@Serializable
data class IntAttributeList(
        /**
         * Type of the attribute
         */
        @SerialId(1) val type: AttributeType,

        /**
         * Attribute values. There are [type].size values per vertex
         */
        @SerialId(2) val values: List<Int>
) {
    @Transient val size: Int = values.size

    operator fun get(i: Int): Int = values[i]
}
