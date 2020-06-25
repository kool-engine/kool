package de.fabmax.kool.scene

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.PbrShader
import de.fabmax.kool.util.Color

class Model(name: String? = null) : TransformGroup(name) {

    val nodes = mutableMapOf<String, TransformGroup>()
    val meshes = mutableMapOf<String, Mesh>()
    val textures = mutableMapOf<String, Texture>()
    val materials = mutableSetOf<PbrMaterial>()

    override fun dispose(ctx: KoolContext) {
        textures.values.forEach { it.dispose() }
        materials.forEach { it.dispose() }
        super.dispose(ctx)
    }

    data class PbrMaterial(
            val albedoFactor: Color = Color.WHITE,
            val roughnessFactor: Float = 1f,
            val metallicFactor: Float = 1f,
            val albedoTexture: Texture? = null,
            val roughnessTexture: Texture? = null,
            val metallicTexture: Texture? = null,
            val occlusionTexture: Texture? = null,
            val useVertexColor: Boolean = false
    ) {
        fun dispose() {
            albedoTexture?.dispose()
            roughnessTexture?.dispose()
            metallicTexture?.dispose()
            occlusionTexture?.dispose()
        }

        fun applyTo(cfg: PbrShader.PbrConfig) {
            when {
                useVertexColor -> {
                    // todo: consider albedoFactor (extend shader to multiply tex values with factor)
                    cfg.albedoSource = Albedo.VERTEX_ALBEDO
                }
                albedoTexture != null -> {
                    // todo: consider albedoFactor (extend shader to multiply tex values with factor)
                    cfg.albedoSource = Albedo.TEXTURE_ALBEDO
                    cfg.albedoMap = albedoTexture
                }
                else -> {
                    cfg.albedoSource = Albedo.STATIC_ALBEDO
                    cfg.albedo = albedoFactor
                }
            }

            if (roughnessTexture != null) {
                // todo: consider roughnessFactor (extend shader to multiply tex values with factor)
                cfg.isRoughnessMapped = true
                cfg.roughnessMap = roughnessTexture
            } else {
                cfg.isRoughnessMapped = false
                cfg.roughness = roughnessFactor
            }

            if (metallicTexture != null) {
                // todo: consider metallicFactor (extend shader to multiply tex values with factor)
                cfg.isMetallicMapped = true
                cfg.metallicMap = metallicTexture
            } else {
                cfg.isMetallicMapped = false
                cfg.metallic = metallicFactor
            }

            if (occlusionTexture != null) {
                cfg.isAmbientOcclusionMapped = true
                cfg.ambientOcclusionMap = occlusionTexture
            } else {
                cfg.isAmbientOcclusionMapped = false
            }

            // roughness, metallic and occlusion can optionally use the same texture (using different color channels)
            // check if textures are the same and choose texture names and color channels correspondingly
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