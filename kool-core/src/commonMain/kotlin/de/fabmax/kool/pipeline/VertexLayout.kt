package de.fabmax.kool.pipeline

import de.fabmax.kool.util.PrimitiveType
import de.fabmax.kool.util.copy

class VertexLayout(val bindings: List<Binding>, val primitiveType: PrimitiveType) {

    val longHash: ULong

    init {
        var hash = 71023UL * primitiveType.hashCode().toULong()
        bindings.forEach {
            hash = (hash * 71023UL) + it.longHash
        }
        longHash = hash
    }

    fun getAttributeLocation(attribName: String): Int {
        for (i in bindings.indices) {
            bindings[i].attributes.find { it.name == attribName }?.let { return it.location }
        }
        throw NoSuchElementException("Attribute $attribName not found")
    }

    data class Binding(val binding: Int, val inputRate: InputRate, val attributes: List<Attribute>, val strideBytes: Int = attributes.sumBy { it.type.size }) {
        val longHash: ULong

        init {
            var hash = binding.toULong()
            hash = (hash * 71023UL) + inputRate.hashCode().toULong()
            hash = (hash * 71023UL) + strideBytes.toULong()
            attributes.forEach { attr ->
                hash = (hash * 71023UL) + attr.hashCode().toULong()
            }
            longHash = hash
        }
    }

    data class Attribute(val location: Int, val offset: Int, val type: GlslType, val name: String)

    class Builder {
        var primitiveType = PrimitiveType.TRIANGLES
        val bindings = mutableListOf<Binding>()

        fun create(): VertexLayout {
            return VertexLayout(bindings.copy(), primitiveType)
        }
    }
}

enum class InputRate {
    VERTEX,
    INSTANCE
}
