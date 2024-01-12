package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.copy

class VertexLayout(val bindings: List<Binding>, val primitiveType: PrimitiveType) {

    val hash = LongHash()

    init {
        hash += primitiveType
        bindings.forEach {
            hash += it.hash
        }
    }

    data class Binding(
        val binding: Int,
        val inputRate: InputRate,
        val vertexAttributes: List<VertexAttribute>,
        val strideBytes: Int = vertexAttributes.sumOf { it.attribute.type.byteSize }
    ) {
        val hash = LongHash()

        init {
            hash += binding
            hash += inputRate
            hash += strideBytes
            vertexAttributes.forEach { attr ->
                hash += attr.hashCode()
            }
        }
    }

    data class VertexAttribute(val location: Int, val offset: Int, val attribute: Attribute) {
        val name: String
            get() = attribute.name
        val type: GpuType
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
