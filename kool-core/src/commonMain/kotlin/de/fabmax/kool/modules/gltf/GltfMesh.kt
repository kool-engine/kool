package de.fabmax.kool.modules.gltf

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.GpuType
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A set of primitives to be rendered. A node can contain one mesh. A node's transform places the mesh in the scene.
 *
 * @param primitives An array of primitives, each defining geometry to be rendered with a material.
 * @param weights    Array of weights to be applied to the Morph Targets.
 * @param name       The user-defined name of this object.
 */
@Serializable
data class GltfMesh(
    val primitives: List<Primitive>,
    val weights: List<Float>? = null,
    val name: String? = null
) {

    /**
     * Geometry to be rendered with the given material.
     *
     * @param attributes A dictionary object, where each key corresponds to mesh attribute semantic and each value is
     *                   the index of the accessor containing attribute's data.
     * @param indices    The index of the accessor that contains the indices.
     * @param material   The index of the material to apply to this primitive when rendering.
     * @param mode       The type of primitives to render.
     * @param targets    An array of Morph Targets, each Morph Target is a dictionary mapping attributes (only
     *                   POSITION, NORMAL, and TANGENT supported) to their deviations in the Morph Target.
     */
    @Serializable
    data class Primitive(
            val attributes: Map<String, Int>,
            val indices: Int = -1,
            val material: Int = -1,
            val mode: Int = MODE_TRIANGLES,
            val targets: List<Map<String, Int>> = emptyList()
    ) {
        @Transient
        var materialRef: GltfMaterial? = null

        fun toGeometry(cfg: GltfLoadConfig, gltfAccessors: List<GltfAccessor>): IndexedVertexList {
            val indexAccessor = if (indices >= 0) gltfAccessors[indices] else null

            val positionAcc = attributes[ATTRIBUTE_POSITION]?.let { gltfAccessors[it] }
            val normalAcc = attributes[ATTRIBUTE_NORMAL]?.let { gltfAccessors[it] }
            val tangentAcc = attributes[ATTRIBUTE_TANGENT]?.let { gltfAccessors[it] }
            val texCoordAcc = attributes[ATTRIBUTE_TEXCOORD_0]?.let { gltfAccessors[it] }
            val colorAcc = attributes[ATTRIBUTE_COLOR_0]?.let { gltfAccessors[it] }
            val jointAcc = attributes[ATTRIBUTE_JOINTS_0]?.let { gltfAccessors[it] }
            val weightAcc = attributes[ATTRIBUTE_WEIGHTS_0]?.let { gltfAccessors[it] }

            if (attributes.containsKey(ATTRIBUTE_TEXCOORD_1)) {
                logW { "Second set of UVs is not yet supported and therefore ignored" }
            }

            if (positionAcc == null) {
                logW { "MeshPrimitive without position attribute" }
                return IndexedVertexList()
            }

            var generateTangents = false

            val attribs = mutableListOf<Attribute>()

            // for PbrShader positions and normals are always required
            attribs += Attribute.POSITIONS
            attribs += Attribute.NORMALS

            if (colorAcc != null || cfg.setVertexAttribsFromMaterial) { attribs += Attribute.COLORS }
            if (cfg.setVertexAttribsFromMaterial) {
                attribs += Attribute.EMISSIVE_COLOR
                attribs += Attribute.METAL_ROUGH
            }
            if (texCoordAcc != null) { attribs += Attribute.TEXTURE_COORDS }
            if (tangentAcc != null) {
                attribs += Attribute.TANGENTS
            } else if(materialRef?.normalTexture != null) {
                attribs += Attribute.TANGENTS
                generateTangents = true
            }
            if (jointAcc != null) { attribs += Attribute.JOINTS }
            if (weightAcc != null) { attribs += Attribute.WEIGHTS }

            val morphAccessors = makeMorphTargetAccessors(gltfAccessors)
            attribs += morphAccessors.keys

            val verts = IndexedVertexList(attribs)
            val poss = Vec3fAccessor(positionAcc)
            val nrms = if (normalAcc != null) Vec3fAccessor(normalAcc) else null
            val tans = if (tangentAcc != null) Vec4fAccessor(tangentAcc) else null
            val texs = if (texCoordAcc != null) Vec2fAccessor(texCoordAcc) else null
            val cols = if (colorAcc != null) Vec4fAccessor(colorAcc) else null
            val jnts = if (jointAcc != null) Vec4iAccessor(jointAcc) else null
            val wgts = if (weightAcc != null) Vec4fAccessor(weightAcc) else null

            for (i in 0 until positionAcc.count) {
                verts.addVertex {
                    poss.next(position)
                    nrms?.next(normal)
                    tans?.next(tangent)
                    texs?.next(texCoord)
                    cols?.next()?.let { col -> color.set(col) }
                    jnts?.next(joints)
                    wgts?.next(weights)

                    if (cfg.setVertexAttribsFromMaterial) {
                        metallicRoughness.set(0f, 0.5f)
                        materialRef?.let { mat ->
                            val col = mat.pbrMetallicRoughness.baseColorFactor
                            if (col.size == 4) {
                                color.set(col[0], col[1], col[2], col[3])
                            }
                            metallicRoughness.set(mat.pbrMetallicRoughness.metallicFactor, mat.pbrMetallicRoughness.roughnessFactor)
                            mat.emissiveFactor?.let { emissiveCol ->
                                if (emissiveCol.size >= 3) {
                                    emissiveColor.set(emissiveCol[0], emissiveCol[1], emissiveCol[2])
                                }
                            }
                        }
                    }

                    morphAccessors.forEach { (attrib, acc) ->
                        getVec3fAttribute(attrib)?.let { acc.next(it) }
                    }
                }
            }

            if (indexAccessor != null) {
                val inds = IntAccessor(indexAccessor)
                for (i in 0 until indexAccessor.count) {
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
            if (cfg.generateNormals || normalAcc == null) {
                verts.generateNormals()
            }
            return verts
        }

        private fun makeMorphTargetAccessors(gltfAccessors: List<GltfAccessor>): Map<Attribute, Vec3fAccessor> {
            val accessors = mutableMapOf<Attribute, Vec3fAccessor>()
            targets.forEachIndexed { index, morphTarget ->
                val postfix = "_${index + 1}"
                morphTarget[ATTRIBUTE_NORMAL]?.let { iAccessor ->
                    val attrib = Attribute("${Attribute.NORMALS.name}$postfix", GpuType.Float3)
                    accessors[attrib] = Vec3fAccessor(gltfAccessors[iAccessor])
                }
                morphTarget[ATTRIBUTE_POSITION]?.let { iAccessor ->
                    val attrib = Attribute("${Attribute.POSITIONS.name}$postfix", GpuType.Float3)
                    accessors[attrib] = Vec3fAccessor(gltfAccessors[iAccessor])
                }
                morphTarget[ATTRIBUTE_TANGENT]?.let { iAccessor ->
                    val attrib = Attribute("${Attribute.TANGENTS.name}$postfix", GpuType.Float3)
                    accessors[attrib] = Vec3fAccessor(gltfAccessors[iAccessor])
                }
            }
            return accessors
        }

        companion object {
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

            const val ATTRIBUTE_POSITION = "POSITION"
            const val ATTRIBUTE_NORMAL = "NORMAL"
            const val ATTRIBUTE_TANGENT = "TANGENT"
            const val ATTRIBUTE_TEXCOORD_0 = "TEXCOORD_0"
            const val ATTRIBUTE_TEXCOORD_1 = "TEXCOORD_1"
            const val ATTRIBUTE_COLOR_0 = "COLOR_0"
            const val ATTRIBUTE_JOINTS_0 = "JOINTS_0"
            const val ATTRIBUTE_WEIGHTS_0 = "WEIGHTS_0"
        }
    }
}