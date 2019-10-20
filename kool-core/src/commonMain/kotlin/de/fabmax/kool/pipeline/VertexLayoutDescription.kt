package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.IndexedVertexList

class VertexLayoutDescription(val bindings: List<Binding>) {
    fun getAttributeLocation(attribName: String): Int {
        for (i in bindings.indices) {
            bindings[i].attributes.find { it.name == attribName }?.let { return it.location }
        }
        throw NoSuchElementException("Attribute $attribName not found")
    }

    data class Binding(val binding: Int, val inputRate: InputRate, val attributes: List<Attribute>) {
        val strideBytes: Int = attributes.sumBy { it.type.size }
    }

    data class Attribute(val binding: Int, val location: Int, val offset: Int, val type: AttributeType, val name: String)

    companion object {
        fun forMesh(mesh: Mesh): VertexLayoutDescription = forVertices(mesh.meshData.vertexList)

        fun forVertices(vertexList: IndexedVertexList): VertexLayoutDescription {
            val attribs = mutableListOf<Attribute>()

            var iAtrrib = 0
            vertexList.attributeOffsets.forEach { (attrib, off) ->
                // fixme: unify / replace old de.fabmax.kool.shading.AttributeType by new one (which is not OpenGL specific)
                val attribType = when (attrib.type) {
                    de.fabmax.kool.shading.AttributeType.FLOAT -> AttributeType.FLOAT
                    de.fabmax.kool.shading.AttributeType.VEC_2F -> AttributeType.VEC_2F
                    de.fabmax.kool.shading.AttributeType.VEC_3F -> AttributeType.VEC_3F
                    de.fabmax.kool.shading.AttributeType.VEC_4F -> AttributeType.VEC_4F
                    de.fabmax.kool.shading.AttributeType.COLOR_4F -> AttributeType.COLOR_4F
                    // fixme: support int types
                    else -> throw IllegalStateException("Attribute is not a float type")
                }
                attribs += Attribute(0, iAtrrib++, off * 4, attribType, attrib.name)
            }

            // fixme: support multiple bindings
            val bindings = listOf(Binding(0, InputRate.VERTEX, attribs))
            return VertexLayoutDescription(bindings)
        }
    }
}

enum class InputRate {
    VERTEX,
    INSTANCE
}

enum class AttributeType(val size: Int, val isInt: Boolean) {
    FLOAT(4, false),
    VEC_2F(8, false),
    VEC_3F(12, false),
    VEC_4F(16, false),
    COLOR_4F(16, false),

    INT(4, true),
    VEC_2I(8, true),
    VEC_3I(12, true),
    VEC_4I(16, true)
}
