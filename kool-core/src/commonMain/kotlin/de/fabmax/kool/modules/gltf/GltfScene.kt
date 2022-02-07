package de.fabmax.kool.modules.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

/**
 * The root nodes of a scene.
 *
 * @param nodes The indices of each root node.
 * @param name  The user-defined name of this object.
 */
@Serializable
data class GltfScene(
    val nodes: List<Int>,
    val name: String? = null
) {
    @Transient
    lateinit var nodeRefs: List<GltfNode>
}