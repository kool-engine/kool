package de.fabmax.kool.modules.gltf

import de.fabmax.kool.AssetLoader
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.modules.ksl.blocks.ColorBlockConfig
import de.fabmax.kool.modules.ksl.blocks.PropertyBlockConfig
import de.fabmax.kool.pipeline.BlendMode
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture2d
import de.fabmax.kool.pipeline.shading.AlphaMode
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

    fun applyTo(cfg: KslPbrShader.Config.Builder, useVertexColor: Boolean, gltfFile: GltfFile, assetLoader: AssetLoader) {
        val baseColorTexture: Texture2d? = pbrMetallicRoughness.baseColorTexture?.getTexture(gltfFile, assetLoader)
        val emissiveTexture: Texture2d? = emissiveTexture?.getTexture(gltfFile, assetLoader)
        val normalTexture: Texture2d? = this.normalTexture?.getTexture(gltfFile, assetLoader)
        val metallicTexture: Texture2d? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile, assetLoader)
        val roughnessTexture: Texture2d? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile, assetLoader)
        val occlusionTexture: Texture2d? = occlusionTexture?.getTexture(gltfFile, assetLoader)
        val colorFac = pbrMetallicRoughness.baseColorFactor

        cfg.alphaMode = when (alphaMode) {
            ALPHA_MODE_BLEND -> AlphaMode.Blend
            ALPHA_MODE_MASK -> AlphaMode.Mask(alphaCutoff)
            else -> AlphaMode.Opaque
        }

        cfg.pipeline {
            cullMethod = if (doubleSided) CullMethod.NO_CULLING else CullMethod.CULL_BACK_FACES
            // use pre-multiplied alpha blending for bright reflections of highly translucent materials
            blendMode = BlendMode.BLEND_PREMULTIPLIED_ALPHA
        }

        cfg.color {
            val colorFactor = if (colorFac.size == 4) Color(colorFac[0], colorFac[1], colorFac[2], colorFac[3]) else Color.WHITE
            when {
                useVertexColor -> {
                    vertexColor()
                    if (colorFactor != Color.WHITE) {
                        uniformColor(colorFactor, blendMode = ColorBlockConfig.BlendMode.Multiply)
                    }
                }
                baseColorTexture != null -> {
                    textureColor(baseColorTexture)
                    if (colorFactor != Color.WHITE) {
                        uniformColor(colorFactor, blendMode = ColorBlockConfig.BlendMode.Multiply)
                    }
                }
                else -> {
                    uniformColor(colorFactor)
                }
            }
        }

        cfg.emission {
            if (emissiveTexture != null) {
                textureColor(emissiveTexture)
                if (emissiveFactor != null) {
                    uniformColor(Color(emissiveFactor[0], emissiveFactor[1], emissiveFactor[2], 1f), blendMode = ColorBlockConfig.BlendMode.Multiply)
                }
            } else if (emissiveFactor != null) {
                uniformColor(Color(emissiveFactor[0], emissiveFactor[1], emissiveFactor[2], 1f))
            }
        }

        cfg.normalMapping {
            if (normalTexture != null) {
                useNormalMap(normalTexture)
            }
        }

        cfg.roughness {
            if (roughnessTexture != null) {
                if (roughnessTexture !== metallicTexture && roughnessTexture !== occlusionTexture) {
                    textureProperty(roughnessTexture)
                } else {
                    val texName = when {
                        roughnessTexture === metallicTexture && roughnessTexture === occlusionTexture -> "tOcclRoughMetal"
                        roughnessTexture === metallicTexture -> "tRoughMetal"
                        else -> "tOcclRough"
                    }
                    textureProperty(roughnessTexture, 1, texName)
                }
                if (pbrMetallicRoughness.roughnessFactor != 1f) {
                    constProperty(pbrMetallicRoughness.roughnessFactor, blendMode = PropertyBlockConfig.BlendMode.Multiply)
                }
            } else {
                constProperty(pbrMetallicRoughness.roughnessFactor)
            }
        }

        cfg.metallic {
            if (metallicTexture != null) {
                if (metallicTexture !== roughnessTexture && metallicTexture !== occlusionTexture) {
                    textureProperty(metallicTexture)
                } else {
                    val texName = when {
                        metallicTexture === roughnessTexture && metallicTexture === occlusionTexture -> "tOcclRoughMetal"
                        metallicTexture === roughnessTexture -> "tRoughMetal"
                        else -> "tOcclMetal"
                    }
                    textureProperty(roughnessTexture, 2, texName)
                }
                if (pbrMetallicRoughness.metallicFactor != 1f) {
                    constProperty(pbrMetallicRoughness.metallicFactor, blendMode = PropertyBlockConfig.BlendMode.Multiply)
                }
            } else {
                constProperty(pbrMetallicRoughness.metallicFactor)
            }
        }

        cfg.ao {
            if (occlusionTexture != null) {
                if (occlusionTexture !== roughnessTexture && occlusionTexture !== metallicTexture) {
                    textureProperty(occlusionTexture)
                } else {
                    val texName = when {
                        occlusionTexture === roughnessTexture && occlusionTexture === metallicTexture -> "tOcclRoughMetal"
                        occlusionTexture === roughnessTexture -> "tOcclRough"
                        else -> "tOcclMetal"
                    }
                    textureProperty(roughnessTexture, 0, texName)
                }
                val occlusionFactor = this@GltfMaterial.occlusionTexture?.strength ?: 1f
                if (occlusionFactor != 1f) {
                    constProperty(occlusionFactor, blendMode = PropertyBlockConfig.BlendMode.Multiply)
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