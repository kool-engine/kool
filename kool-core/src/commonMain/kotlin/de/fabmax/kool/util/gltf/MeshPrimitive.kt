package de.fabmax.kool.util.gltf

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.IndexedVertexList
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

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

    fun toGeometry(generateNormals: Boolean): IndexedVertexList {
        val positionAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_POSITION]
        val normalAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_NORMAL]
        val tangentAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_TANGENT]
        val texCoordAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_TEXCOORD_0]
        val colorAcc = attribAccessorRefs[GltfFile.MESH_ATTRIBUTE_COLOR_0]

        var generateTangents = false

        val attribs = mutableListOf<Attribute>()
        if (positionAcc != null) { attribs += Attribute.POSITIONS
        }
        if (normalAcc != null || generateNormals) { attribs += Attribute.NORMALS
        }
        if (colorAcc != null) { attribs += Attribute.COLORS
        }
        if (texCoordAcc != null) { attribs += Attribute.TEXTURE_COORDS
        }
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
            val tans = if (tangentAcc != null) Vec4fAccessor(tangentAcc) else null
            val texs = if (texCoordAcc != null) Vec2fAccessor(texCoordAcc) else null
            val cols = if (colorAcc != null) Vec4fAccessor(colorAcc) else null

            for (i in 0 until positionAcc.count) {
                verts.addVertex {
                    poss.next(position)
                    nrms?.next(normal)
                    tans?.next()?.let { tan -> tangent.set(tan.x, tan.y, tan.z) }
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
            if (generateNormals) {
                verts.generateNormals()
            }
        }
        return verts
    }
}