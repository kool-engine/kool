package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

/**
 * A set of primitives to be rendered. A node can contain one mesh. A node's transform places the mesh in the scene.
 *
 * @param primitives An array of primitives, each defining geometry to be rendered with a material.
 * @param weights    Array of weights to be applied to the Morph Targets.
 * @param name       The user-defined name of this object.
 */
@Serializable
data class Mesh(
        val primitives: List<MeshPrimitive>,
        val weights: List<Float>? = null,
        val name: String? = null
)