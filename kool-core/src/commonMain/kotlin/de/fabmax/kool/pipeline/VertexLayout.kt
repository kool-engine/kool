package de.fabmax.kool.pipeline

import de.fabmax.kool.scene.geometry.PrimitiveType
import de.fabmax.kool.util.LongHash

class VertexLayout(val bindings: List<Binding>, val primitiveType: PrimitiveType) {

    val hash: LongHash = LongHash {
        this += primitiveType
        bindings.forEach { b ->
            this += b.hash
        }
    }

    data class Binding(
        val binding: Int,
        val inputRate: InputRate,
        val vertexAttributes: List<VertexAttribute>,
        val strideBytes: Int = vertexAttributes.sumOf { it.type.byteSize }
    ) {
        val hash: LongHash = LongHash {
            this += binding
            this += inputRate
            this += strideBytes
            vertexAttributes.forEach { attr ->
                this += attr.hashCode()
            }
        }
    }

    data class VertexAttribute(val index: Int, val bufferOffset: Int, val name: String, val type: GpuType)
}

enum class InputRate {
    VERTEX,
    INSTANCE
}
