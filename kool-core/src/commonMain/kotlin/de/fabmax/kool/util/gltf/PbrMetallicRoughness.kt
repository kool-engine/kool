package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

@Serializable
data class PbrMetallicRoughness(
        val baseColorFactor: List<Float> = listOf(1f, 1f, 1f, 1f),
        val baseColorTexture: MaterialMap? = null,
        val metallicFactor: Float = 1f,
        val roughnessFactor: Float = 1f,
        val metallicRoughnessTexture: MaterialMap? = null
)