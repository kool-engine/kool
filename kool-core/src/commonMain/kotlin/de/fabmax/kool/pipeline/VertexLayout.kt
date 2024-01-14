package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.LongHash

class VertexLayout(val bindings: List<Binding>, val primitiveType: PrimitiveType) {

    val hash: Long = LongHash().let {
        it += primitiveType
        bindings.forEach { b ->
            it += b.hash
        }
        it.hash
    }

    data class Binding(
        val binding: Int,
        val inputRate: InputRate,
        val vertexAttributes: List<VertexAttribute>,
        val strideBytes: Int = vertexAttributes.sumOf { it.attribute.type.byteSize }
    ) {
        val hash: Long = LongHash().let {
            it += binding
            it += inputRate
            it += strideBytes
            vertexAttributes.forEach { attr ->
                it += attr.hashCode()
            }
            it.hash
        }
    }

    data class VertexAttribute(val location: Int, val offset: Int, val attribute: Attribute) {
        val name: String
            get() = attribute.name
        val type: GpuType
            get() = attribute.type
    }
}

enum class InputRate {
    VERTEX,
    INSTANCE
}
