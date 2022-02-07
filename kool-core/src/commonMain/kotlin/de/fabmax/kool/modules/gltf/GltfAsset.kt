package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable

/**
 * Metadata about the glTF asset.
 *
 * @param copyright  A copyright message suitable for display to credit the content creator.
 * @param generator  Tool that generated this glTF model. Useful for debugging.
 * @param version    The glTF version that this asset targets.
 * @param minVersion The minimum glTF version that this asset targets.
 */
@Serializable
data class GltfAsset(
    val copyright: String? = null,
    val generator: String? = null,
    val version: String,
    val minVersion: String? = null
)