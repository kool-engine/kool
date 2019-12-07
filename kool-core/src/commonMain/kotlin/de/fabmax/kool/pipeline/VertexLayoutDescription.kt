package de.fabmax.kool.pipeline

import de.fabmax.kool.util.copy

class VertexLayoutDescription(val bindings: List<Binding>) {

    val longHash: Long

    init {
        var hash = 0L
        bindings.forEach {
            hash = (hash * 71023L) + it.hashCode().toLong()
            it.attributes.forEach { attr ->
                hash = (hash * 71023L) + attr.hashCode().toLong()
            }
        }
        longHash = hash
    }

    fun getAttributeLocation(attribName: String): Int {
        for (i in bindings.indices) {
            bindings[i].attributes.find { it.name == attribName }?.let { return it.location }
        }
        throw NoSuchElementException("Attribute $attribName not found")
    }

    data class Binding(val binding: Int, val inputRate: InputRate, val attributes: List<Attribute>, val strideBytes: Int = attributes.sumBy { it.type.size })

    data class Attribute(val binding: Int, val location: Int, val offset: Int, val type: AttributeType, val name: String)

    class Builder {
        val bindings = mutableListOf<Binding>()

        fun create(): VertexLayoutDescription {
            return VertexLayoutDescription(bindings.copy())
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
