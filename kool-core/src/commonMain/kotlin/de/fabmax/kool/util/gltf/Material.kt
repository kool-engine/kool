package de.fabmax.kool.util.gltf

import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.util.Color
import kotlinx.serialization.Serializable

@Serializable
data class Material(
        val doubleSided: Boolean = false,
        val name: String? = null,
        val pbrMetallicRoughness: PbrMetallicRoughness = PbrMetallicRoughness(baseColorFactor = listOf(0.5f, 0.5f, 0.5f, 1f)),
        val normalTexture: MaterialMap? = null,
        val occlusionTexture: MaterialMap? = null,
        val emissiveFactor: List<Float>? = null,
        val emissiveTexture: MaterialMap? = null,
        val alphaMode: String = "OPAQUE",
        val alphaCutoff: Float = 0.5f
) {

    fun applyTo(cfg: PbrShader.PbrConfig, useVertexColor: Boolean, gltfFile: GltfFile) {
        val albedoTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.baseColorTexture?.getTexture(gltfFile)
        val normalTexture: de.fabmax.kool.pipeline.Texture? = this.normalTexture?.getTexture(gltfFile)
        val metallicTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val roughnessTexture: de.fabmax.kool.pipeline.Texture? = pbrMetallicRoughness.metallicRoughnessTexture?.getTexture(gltfFile)
        val occlusionTexture: de.fabmax.kool.pipeline.Texture? = occlusionTexture?.getTexture(gltfFile)
        val colorFac = pbrMetallicRoughness.baseColorFactor

        when {
            useVertexColor -> {
                // todo: consider albedoFactor (extend shader to multiply vertex color values with factor)
                cfg.albedoSource = Albedo.VERTEX_ALBEDO
            }
            albedoTexture != null -> {
                // todo: consider albedoFactor (extend shader to multiply tex values with factor)
                cfg.albedoSource = Albedo.TEXTURE_ALBEDO
                cfg.albedoMap = albedoTexture
            }
            else -> {
                cfg.albedoSource = Albedo.STATIC_ALBEDO
                val a = if (colorFac.size > 3) colorFac[3] else 1f
                cfg.albedo = Color(colorFac[0], colorFac[1], colorFac[2], a)
            }
        }

        if (normalTexture != null) {
            cfg.isNormalMapped = true
            cfg.normalMap = normalTexture
        } else {
            cfg.isNormalMapped = false
        }

        if (roughnessTexture != null) {
            // todo: consider roughnessFactor (extend shader to multiply tex values with factor)
            cfg.isRoughnessMapped = true
            cfg.roughnessMap = roughnessTexture
        } else {
            cfg.isRoughnessMapped = false
            cfg.roughness = pbrMetallicRoughness.roughnessFactor
        }

        if (metallicTexture != null) {
            // todo: consider metallicFactor (extend shader to multiply tex values with factor)
            cfg.isMetallicMapped = true
            cfg.metallicMap = metallicTexture
        } else {
            cfg.isMetallicMapped = false
            cfg.metallic = pbrMetallicRoughness.metallicFactor
        }

        if (occlusionTexture != null) {
            cfg.isAmbientOcclusionMapped = true
            cfg.ambientOcclusionMap = occlusionTexture
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
}