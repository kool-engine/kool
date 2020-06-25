package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class Scene(
        val nodes: List<Int>,
        val name: String? = null
) {
    @Transient
    lateinit var nodeRefs: List<Node>
}