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

    data class Binding(
            val binding: Int,
            val inputRate: InputRate,
            val vertexAttributes: List<VertexAttribute>,
            val strideBytes: Int = vertexAttributes.sumBy { it.attribute.type.size })
    {
        val longHash: ULong

        init {
            var hash = binding.toULong()
            hash = (hash * 71023UL) + inputRate.hashCode().toULong()
            hash = (hash * 71023UL) + strideBytes.toULong()
            vertexAttributes.forEach { attr ->
                hash = (hash * 71023UL) + attr.hashCode().toULong()
            }
            longHash = hash
        }
    }

    data class VertexAttribute(val location: Int, val offset: Int, val attribute: Attribute) {
        val name: String
            get() = attribute.name
        val type: GlslType
            get() = attribute.type
    }

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
