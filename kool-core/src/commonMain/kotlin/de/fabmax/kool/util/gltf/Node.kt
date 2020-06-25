package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Node(
        val mesh: Int = -1,
        val name: String = "",
        val children: List<Int> = emptyList(),
        val translation: List<Float>? = null,
        val rotation: List<Float>? = null,
        val scale: List<Float>? = null,
        val matrix: List<Float>? = null
) {
    @Transient
    lateinit var childRefs: List<Node>
    @Transient
    var meshRef: Mesh? = null
}