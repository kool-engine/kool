package de.fabmax.kool.util.gltf

import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.*
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

/**
 * The material appearance of a primitive.
 *
 * @param name                 The user-defined name of this object.
 * @param pbrMetallicRoughness A set of parameter values that are used to define the metallic-roughness material model
 *                             from Physically-Based Rendering (PBR) methodology. When not specified, all the default
 *                             values of pbrMetallicRoughness apply.
 * @param normalTexture        The normal map texture.
 * @param occlusionTexture     The occlusion map texture.
 * @param emissiveTexture      The emissive map texture.
 * @param emissiveFactor       The emissive color of the material.
 * @param alphaMode            The alpha rendering mode of the material.
 * @param alphaCutoff          The alpha cutoff value of the material.
 * @param doubleSided          Specifies whether the material is double sided.
 */
@Serializable
data class GltfMaterial(
        val name: String? = null,
        val pbrMetallicRoughness: PbrMetallicRoughness = PbrMetallicRoughness(baseColorFactor = listOf(0.5f, 0.5f, 0.5f, 1f)),
        val normalTexture: GltfTextureInfo? = null,
        val occlusionTexture: GltfTextureInfo? = null,
        val emissiveTexture: GltfTextureInfo? = null,
        val emissiveFactor: List<Float>? = null,
        val alphaMode: String = ALPHA_MODE_OPAQUE,
        val alphaCutoff: Float = 0.5f,
        val doubleSided: Boolean = false
) {

    fun applyTo(cfg: PbrMaterialConfig, useVertexColor: Boolean, gltfFile: GltfFile) {
        val albedoTexture: Texture2d? = pbrMetallicRoughness.baseColorTexture?.getTexture(gltfFile)
        val emissiveTexture: Texture2d? = emissiveTexture?.getTexture(gltfFile)
        val normalTexture: Texture2d? = this.normalTexture?.getTexture(gltfFile)
        val metallicTexture: Texture2d? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val roughnessTexture: Texture2d? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val occlusionTexture: Texture2d? = occlusionTexture?.getTexture(gltfFile)
        val colorFac = pbrMetallicRoughness.baseColorFactor

        cfg.alphaMode = when (alphaMode) {
            ALPHA_MODE_BLEND -> AlphaModeBlend()
            ALPHA_MODE_MASK -> AlphaModeMask(alphaCutoff)
            else -> AlphaModeOpaque()
        }

        cfg.cullMethod = if (doubleSided) {
            CullMethod.NO_CULLING
        } else {
            CullMethod.CULL_BACK_FACES
        }

        cfg.albedo = if (colorFac.size == 4) { Color(colorFac[0], colorFac[1], colorFac[2], colorFac[3]) } else { Color.WHITE }
        when {
            useVertexColor -> {
                // todo: consider albedoFactor (extend shader to multiply vertex color values with factor)
                cfg.albedoSource = Albedo.VERTEX_ALBEDO
            }
            albedoTexture != null -> {
                // todo: consider albedoFactor (extend shader to multiply tex values with factor)
                cfg.albedoSource = Albedo.TEXTURE_ALBEDO
                cfg.albedoMap = albedoTexture
                if (cfg.albedo != Color.WHITE) {
                    cfg.albedoMapMode = AlbedoMapMode.MULTIPLY_BY_UNIFORM
                }
            }
            else -> {
                cfg.albedoSource = Albedo.STATIC_ALBEDO
            }
        }

        emissiveTexture?.let { emTex ->
            cfg.isEmissiveMapped = true
            cfg.emissiveMap = emTex
            if (emissiveFactor != null) {
                cfg.isMultiplyEmissive = true
            }
        }
        if (emissiveFactor != null) {
            cfg.emissive = Color(emissiveFactor[0], emissiveFactor[1], emissiveFactor[2], 1f)
        }

        if (normalTexture != null) {
            cfg.isNormalMapped = true
            cfg.normalMap = normalTexture
        } else {
            cfg.isNormalMapped = false
        }

        cfg.roughness = pbrMetallicRoughness.roughnessFactor
        if (roughnessTexture != null) {
            // todo: consider roughnessFactor (extend shader to multiply tex values with factor)
            cfg.isRoughnessMapped = true
            cfg.roughnessMap = roughnessTexture
            if (pbrMetallicRoughness.roughnessFactor != 1f) {
                cfg.isMultiplyRoughness = true
            }
        } else {
            cfg.isRoughnessMapped = false
        }

        cfg.metallic = pbrMetallicRoughness.metallicFactor
        if (metallicTexture != null) {
            // todo: consider metallicFactor (extend shader to multiply tex values with factor)
            cfg.isMetallicMapped = true
            cfg.metallicMap = metallicTexture
            if (pbrMetallicRoughness.metallicFactor != 1f) {
                cfg.isMultiplyMetallic = true
            }
        } else {
            cfg.isMetallicMapped = false
        }

        if (occlusionTexture != null) {
            cfg.isAoMapped = true
            cfg.aoMap = occlusionTexture
            cfg.aoStrength = this.occlusionTexture?.strength ?: 1f
        } else {
            cfg.isAoMapped = false
        }

        // roughness, metallic and occlusion can optionally use the same texture (using different color channels)
        // check if textures are the same and choose texture names and color channels correspondingly
        if (cfg.isRoughnessMapped) {
            if (roughnessTexture !== metallicTexture && roughnessTexture !== occlusionTexture) {
                // standalone roughness texture -> use roughness data from red channel
                cfg.roughnessChannel = "r"
                cfg.roughnessTexName = "tRoughness"
            } else {
                // roughness texture is joined -> use roughness data from green channel (consistent with glTF spec)
                cfg.roughnessChannel = "g"
                if (roughnessTexture === metallicTexture && roughnessTexture === occlusionTexture) {
                    cfg.roughnessTexName = "tOcclRoughMetal"
                } else if (roughnessTexture === metallicTexture) {
                    cfg.roughnessTexName = "tRoughMetal"
                } else { // roughnessTexture === occlusionTexture
                    cfg.roughnessTexName = "tOcclRough"
                }
            }
        }

        if (cfg.isMetallicMapped) {
            if (metallicTexture !== roughnessTexture && metallicTexture !== occlusionTexture) {
                // standalone metallic texture -> use metallic data from red channel
                cfg.metallicChannel = "r"
                cfg.metallicTexName = "tMetallic"
            } else {
                // metallic texture is joined -> use metallic data from blue channel (consistent with glTF spec)
                cfg.metallicChannel = "b"
                if (metallicTexture === roughnessTexture && metallicTexture === occlusionTexture) {
                    cfg.metallicTexName = "tOcclRoughMetal"
                } else if (metallicTexture === roughnessTexture) {
                    cfg.metallicTexName = "tRoughMetal"
                } else { // metallicTexture === occlusionTexture
                    cfg.roughnessTexName = "tOcclMetal"
                }
            }
        }

        if (cfg.isAoMapped) {
            if (occlusionTexture !== roughnessTexture && occlusionTexture !== metallicTexture) {
                // standalone occlusion texture -> use occlusion data from red channel
                cfg.occlusionChannel = "r"
                cfg.aoTexName = "tOcclusion"
            } else {
                // occlusion texture is joined -> use occlusion data from red channel (consistent with glTF spec)
                cfg.occlusionChannel = "r"
                if (occlusionTexture === roughnessTexture && occlusionTexture === metallicTexture) {
                    cfg.aoTexName = "tOcclRoughMetal"
                } else if (occlusionTexture === roughnessTexture) {
                    cfg.roughnessTexName = "tOcclRough"
                } else { // occlusionTexture === metallicTexture
                    cfg.roughnessTexName = "tOcclMetal"
                }
            }
        }
    }

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
            val baseColorTexture: GltfTextureInfo? = null,
            val metallicFactor: Float = 1f,
            val roughnessFactor: Float = 1f,
            val metallicRoughnessTexture: GltfTextureInfo? = null
    )

    companion object {
        const val ALPHA_MODE_BLEND = "BLEND"
        const val ALPHA_MODE_MASK = "MASK"
        const val ALPHA_MODE_OPAQUE = "OPAQUE"
    }
}