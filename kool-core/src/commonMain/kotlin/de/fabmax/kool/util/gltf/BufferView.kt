package de.fabmax.kool.util.gltf

import de.fabmax.kool.util.Uint8Buffer
import de.fabmax.kool.util.createUint8Buffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class BufferView(
        val buffer: Int,
        val byteLength: Int,
        val byteOffset: Int = 0,
        val target: Int = 0
) {
    @Transient
    lateinit var bufferRef: Buffer

    fun getData(): Uint8Buffer {
        val array = createUint8Buffer(byteLength)
        for (i in 0 until byteLength) {
            array[i] = bufferRef.data[byteOffset + i]
        }
        return array
    }
}