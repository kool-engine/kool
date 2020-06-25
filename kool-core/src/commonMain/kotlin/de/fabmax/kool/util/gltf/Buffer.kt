package de.fabmax.kool.util.gltf

import de.fabmax.kool.util.Uint8Buffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Buffer(
        val byteLength: Int,
        val uri: String? = null
) {
    @Transient
    lateinit var data: Uint8Buffer
}