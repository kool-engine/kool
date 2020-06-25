package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

@Serializable
data class Mesh(
        val primitives: List<MeshPrimitive>,
        val name: String? = null
)