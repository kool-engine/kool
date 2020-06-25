package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

@Serializable
data class Asset(
        val version: String,
        val generator: String? = null
)