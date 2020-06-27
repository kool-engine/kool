package de.fabmax.kool.util.gltf

import de.fabmax.kool.pipeline.CullMethod
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
data class Material(
        val name: String? = null,
        val pbrMetallicRoughness: PbrMetallicRoughness = PbrMetallicRoughness(baseColorFactor = listOf(0.5f, 0.5f, 0.5f, 1f)),
        val normalTexture: TextureInfo? = null,
        val occlusionTexture: TextureInfo? = null,
        val emissiveTexture: TextureInfo? = null,
        val emissiveFactor: List<Float>? = null,
        val alphaMode: String = ALPHA_MODE_OPAQUE,
        val alphaCutoff: Float = 0.5f,
        val doubleSided: Boolean = false
) {

    fun applyTo(cfg: PbrShader.PbrConfig, useVertexColor: Boolean, gltfFile: GltfFile) {
        val albedoTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.baseColorTexture?.getTexture(gltfFile)
        val normalTexture: de.fabmax.kool.pipeline.Texture? = this.normalTexture?.getTexture(gltfFile)
        val metallicTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val roughnessTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val occlusionTexture: de.fabmax.kool.pipeline.Texture? = occlusionTexture?.getTexture(gltfFile)
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
                    cfg.isMultiplyAlbedoMap = true
                }
            }
            else -> {
                cfg.albedoSource = Albedo.STATIC_ALBEDO
            }
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
                cfg.isMultiplyRoughnessMap = true
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
                cfg.isMultiplyMetallicMap = true
            }
        } else {
            cfg.isMetallicMapped = false
        }

        if (occlusionTexture != null) {
            cfg.isAmbientOcclusionMapped = true
            cfg.ambientOcclusionMap = occlusionTexture
            cfg.ambientOcclusionStrength = this.occlusionTexture?.strength ?: 1f
        } else {
            cfg.isAmbientOcclusionMapped = false
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

        if (cfg.isAmbientOcclusionMapped) {
            if (occlusionTexture !== roughnessTexture && occlusionTexture !== metallicTexture) {
                // standalone occlusion texture -> use occlusion data from red channel
                cfg.ambientOcclusionChannel = "r"
                cfg.ambientOcclusionTexName = "tOcclusion"
            } else {
                // occlusion texture is joined -> use occlusion data from red channel (consistent with glTF spec)
                cfg.ambientOcclusionChannel = "r"
                if (occlusionTexture === roughnessTexture && occlusionTexture === metallicTexture) {
                    cfg.ambientOcclusionTexName = "tOcclRoughMetal"
                } else if (occlusionTexture === roughnessTexture) {
                    cfg.roughnessTexName = "tOcclRough"
                } else { // occlusionTexture === metallicTexture
                    cfg.roughnessTexName = "tOcclMetal"
                }
            }
        }
    }

    companion object {
        const val ALPHA_MODE_BLEND = "BLEND"
        const val ALPHA_MODE_MASK = "MASK"
        const val ALPHA_MODE_OPAQUE = "OPAQUE"
    }
}