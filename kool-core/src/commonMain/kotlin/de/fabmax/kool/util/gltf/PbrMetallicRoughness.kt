package de.fabmax.kool.util.gltf

import kotlinx.serialization.Serializable

/**
 * A set of parameter values that are used to define the metallic-roughness material model from Physically-Based
 * Rendering (PBR) methodology.
 *
 * @param baseColorFactor          The material's base color factor.
 * @param baseColorTexture         The base color texture.
 * @param metallicFactor           The metalness of the material.
 * @param roughnessFactor          The roughness of the material.
 * @param metallicRoughnessTexture The metallic-roughness texture.
 */
@Serializable
data class PbrMetallicRoughness(
        val baseColorFactor: List<Float> = listOf(1f, 1f, 1f, 1f),
        val baseColorTexture: TextureInfo? = null,
        val metallicFactor: Float = 1f,
        val roughnessFactor: Float = 1f,
        val metallicRoughnessTexture: TextureInfo? = null
)