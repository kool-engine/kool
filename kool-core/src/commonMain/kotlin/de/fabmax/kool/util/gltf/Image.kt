package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Image(
        var uri: String? = null,
        val bufferView: Int = -1,
        val mimeType: String? = null,
        val name: String? = null
) {
    @Transient
    var bufferViewRef: BufferView? = null
}