package de.fabmax.kool.modules.gltf

import de.fabmax.kool.util.Uint8Buffer
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * A buffer points to binary geometry, animation, or skins.
 *
 * @param uri        The uri of the buffer.
 * @param byteLength The total byte length of the buffer view.
 * @param name       The user-defined name of this object.
 */
@Serializable
data class GltfBuffer(
    val uri: String? = null,
    val byteLength: Int,
    val name: String? = null
) {
    @Transient
    lateinit var data: Uint8Buffer
}