package de.fabmax.kool.util.serialization

import de.fabmax.kool.gl.GL_LINES
import de.fabmax.kool.gl.GL_TRIANGLES
import de.fabmax.kool.math.*
import de.fabmax.kool.scene.MeshData
import de.fabmax.kool.shading.Attribute
import de.fabmax.kool.shading.AttributeType

class MeshSerializer {

    var includeNormals = true
    var includeColors = true
    var includeTexCoords = true
    var includeTangents = true

    var generateNormals = false
    var generateTangents = false

    // if > 0 normals will be oct encoded with the specified number of bits
    var normalBits = 0

    fun convertToModel(mesh: MeshData, name: String = "", materialData: MaterialData? = null): ModelData {
        val materials = if (materialData != null) listOf(materialData) else emptyList()
        val meshData = convertMesh(mesh, name, 0)
        val node = ModelNodeData(name, Mat4f().setIdentity().toList(), meshes = listOf(0))
        return ModelData(ModelData.VERSION, name, listOf(meshData), listOf(node), materials)
    }

    fun convertMesh(mesh: MeshData, name: String = "", materialIdx: Int = 0): ModelMeshData {
        if (generateNormals) {
            mesh.generateNormals()
        }
        if (generateTangents) {
            mesh.generateTangents()
        }

        val indices = mutableListOf<Int>()
        val posList = mutableListOf<Float>()
        val normalList = mutableListOf<Float>()
        val encNormalList = mutableListOf<Int>()
        val uvList = mutableListOf<Float>()
        val colorList = mutableListOf<Float>()
        val tangentList = mutableListOf<Float>()

        for (i in 0 until mesh.numIndices) {
            indices += mesh.vertexList.indices[i]
        }

        mesh.vertexList.forEach {
            posList += it.position
            if (mesh.hasAttribute(Attribute.NORMALS) && includeNormals) {
                if (normalBits <= 0) {
                    normalList += it.normal
                } else {
                    encodeNormal(it.normal, normalBits, encNormalList)
                }
            }
            if (mesh.hasAttribute(Attribute.TEXTURE_COORDS) && includeTexCoords) {
                uvList += it.texCoord
            }
            if (mesh.hasAttribute(Attribute.COLORS) && includeColors) {
                colorList += it.color
            }
            if (mesh.hasAttribute(Attribute.TANGENTS) && includeTangents) {
                tangentList += it.tangent
            }
        }

        val attribs = mutableMapOf<String, AttributeList>()
        val tags = mutableListOf<String>()
        val intAttribs = mutableMapOf<String, IntAttributeList>()

        attribs[ModelMeshData.ATTRIB_POSITIONS] = AttributeList(AttributeType.VEC_3F, posList)
        if (normalList.isNotEmpty()) {
            attribs[ModelMeshData.ATTRIB_NORMALS] = AttributeList(AttributeType.VEC_3F, normalList)
        }
        if (encNormalList.isNotEmpty()) {
            intAttribs[ModelMeshData.ATTRIB_NORMALS_OCT_COMPRESSED] = IntAttributeList(AttributeType.VEC_2I, encNormalList)
            tags += "${ModelMeshData.ATTRIB_NORMALS_OCT_COMPRESSED}=$normalBits"
        }
        if (uvList.isNotEmpty()) {
            attribs[ModelMeshData.ATTRIB_TEXTURE_COORDS] = AttributeList(AttributeType.VEC_2F, uvList)
        }
        if (colorList.isNotEmpty()) {
            attribs[ModelMeshData.ATTRIB_COLORS] = AttributeList(AttributeType.VEC_4F, colorList)
        }
        if (tangentList.isNotEmpty()) {
            attribs[ModelMeshData.ATTRIB_TANGENTS] = AttributeList(AttributeType.VEC_3F, tangentList)
        }

        val primitiveType = when(mesh.primitiveType) {
            GL_TRIANGLES -> PrimitiveType.TRIANGLES
            GL_LINES -> PrimitiveType.LINES
            else -> PrimitiveType.POINTS
        }

        return ModelMeshData(name = name, tags = tags, primitiveType = primitiveType, indices = indices,
                attributes = attribs, intAttributes = intAttribs, material = materialIdx,
                animations = emptyList(), armature = emptyList())
    }

    private fun encodeNormal(normal: Vec3f, bits: Int, target: MutableList<Int>) {
        val f = (1 shl bits) - 1
        val o = NormalOctCoding.encodeNormalToOct(normal, MutableVec2f())
        target += Math.round(o.x * f)
        target += Math.round(o.y * f)
    }

    private operator fun MutableList<Float>.plusAssign(v: Vec2f) {
        this += v.x
        this += v.y
    }

    private operator fun MutableList<Float>.plusAssign(v: Vec3f) {
        this += v.x
        this += v.y
        this += v.z
    }

    private operator fun MutableList<Float>.plusAssign(v: Vec4f) {
        this += v.x
        this += v.y
        this += v.z
        this += v.w
    }
}