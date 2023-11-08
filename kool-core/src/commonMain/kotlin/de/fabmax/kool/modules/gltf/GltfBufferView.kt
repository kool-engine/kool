package de.fabmax.kool.modules.gltf

import de.fabmax.kool.util.Uint8Buffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A view into a buffer generally representing a subset of the buffer.
 *
 * @param buffer     The index of the buffer.
 * @param byteOffset The offset into the buffer in bytes.
 * @param byteLength The length of the bufferView in bytes.
 * @param byteStride The stride, in bytes.
 * @param target     The target that the GPU buffer should be bound to.
 * @param name       The user-defined name of this object.
 */
@Serializable
data class GltfBufferView(
    val buffer: Int,
    val byteOffset: Int = 0,
    val byteLength: Int,
    val byteStride: Int = 0,
    val target: Int = 0,
    val name: String? = null
) {
    @Transient
    lateinit var bufferRef: GltfBuffer

    fun getData(): Uint8Buffer {
        val array = Uint8Buffer(byteLength)
        for (i in 0 until byteLength) {
            array[i] = bufferRef.data[byteOffset + i]
        }
        return array
    }
}