package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * Image data used to create a texture. Image can be referenced by URI or bufferView index. mimeType is required in the
 * latter case.
 *
 * @param uri        The uri of the image.
 * @param mimeType   The image's MIME type.
 * @param bufferView The index of the bufferView that contains the image. Use this instead of the image's uri property.
 * @param name       The user-defined name of this object.
 */
@Serializable
data class GltfImage(
    var uri: String? = null,
    val mimeType: String? = null,
    val bufferView: Int = -1,
    val name: String? = null
) {
    @Transient
    var bufferViewRef: GltfBufferView? = null
}