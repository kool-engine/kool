package de.fabmax.kool.util.gltf

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.util.IndexedVertexList
import de.fabmax.kool.util.logW
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Geometry to be rendered with the given material.
 *
 * @param attributes A dictionary object, where each key corresponds to mesh attribute semantic and each value is the
 *                   index of the accessor containing attribute's data.
 * @param indices    The index of the accessor that contains the indices.
 * @param material   The index of the material to apply to this primitive when rendering.
 * @param mode       The type of primitives to render.
 */
@Serializable
data class MeshPrimitive(
        val attributes: Map<String, Int>,
        val indices: Int = -1,
        val material: Int = -1,
        val mode: Int = MODE_TRIANGLES
) {
    @Transient
    var materialRef: Material? = null
    @Transient
    var indexAccessorRef: Accessor? = null
    @Transient
    val attribAccessorRefs = mutableMapOf<String, Accessor>()

    fun toGeometry(generateNormals: Boolean): IndexedVertexList {
        val positionAcc = attribAccessorRefs[ATTRIBUTE_POSITION]
        val normalAcc = attribAccessorRefs[ATTRIBUTE_NORMAL]
        val tangentAcc = attribAccessorRefs[ATTRIBUTE_TANGENT]
        val texCoordAcc = attribAccessorRefs[ATTRIBUTE_TEXCOORD_0]
        val colorAcc = attribAccessorRefs[ATTRIBUTE_COLOR_0]
        val jointAcc = attribAccessorRefs[ATTRIBUTE_JOINTS_0]
        val weightAcc = attribAccessorRefs[ATTRIBUTE_WEIGHTS_0]

        if (positionAcc == null) {
            logW { "MeshPrimitive without position attribute" }
            return IndexedVertexList()
        }

        var generateTangents = false

        val attribs = mutableListOf<Attribute>()

        // for PbrShader positions and normals are always required
        attribs += Attribute.POSITIONS
        attribs += Attribute.NORMALS

        if (colorAcc != null) { attribs += Attribute.COLORS }
        if (texCoordAcc != null) { attribs += Attribute.TEXTURE_COORDS }
        if (tangentAcc != null) {
            attribs += Attribute.TANGENTS
        } else if(materialRef?.normalTexture != null) {
            attribs += Attribute.TANGENTS
            generateTangents = true
        }
        if (jointAcc != null) { attribs += Attribute.JOINTS }
        if (weightAcc != null) { attribs += Attribute.WEIGHTS }

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
        if (generateNormals || normalAcc == null) {
            verts.generateNormals()
        }
        return verts
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