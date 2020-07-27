package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CubeMapTexture
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Texture
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.ibl.EnvironmentMaps

class PbrMaterialConfig {
    // shader configuration
    var albedoSource = Albedo.VERTEX_ALBEDO
    var isEmissiveMapped = false
    var isNormalMapped = false
    var isRoughnessMapped = false
    var isMetallicMapped = false
    var isOcclusionMapped = false
    var isDisplacementMapped = false

    var isInstanced = false
    var isSkinned = false
    var maxJoints = 64

    val morphAttributes = mutableListOf<Attribute>()

    var isMultiplyAlbedoMap = false
    var isMultiplyEmissiveMap = false
    var isMultiplyRoughnessMap = false
    var isMultiplyMetallicMap = false

    var isImageBasedLighting = false
    var isScrSpcAmbientOcclusion = false

    var maxLights = 4
    val shadowMaps = mutableListOf<ShadowMap>()
    var lightBacksides = false

    var cullMethod = CullMethod.CULL_BACK_FACES
    var alphaMode: AlphaMode = AlphaModeOpaque()
    var isHdrOutput = false

    var roughnessChannel = "r"
    var roughnessTexName = "tRoughness"
    var metallicChannel = "r"
    var metallicTexName = "tMetallic"
    var occlusionChannel = "r"
    var occlusionTexName = "tOcclusion"

    // initial shader attribute values
    var albedo = Color.GRAY
    var emissive = Color.BLACK
    var roughness = 0.5f
    var metallic = 0.0f

    var normalStrength = 1f
    var displacementStrength = 0.1f
    var occlusionStrength = 1f

    var albedoMap: Texture? = null
    var emissiveMap: Texture? = null
    var normalMap: Texture? = null
    var displacementMap: Texture? = null
    var roughnessMap: Texture? = null
    var metallicMap: Texture? = null
    var occlusionMap: Texture? = null

    var irradianceMap: CubeMapTexture? = null
    var reflectionMap: CubeMapTexture? = null
    var brdfLut: Texture? = null

    var scrSpcAmbientOcclusionMap: Texture? = null

    fun useStaticAlbedo(albedo: Color) {
        albedoSource = Albedo.STATIC_ALBEDO
        this.albedo = albedo
    }

    fun useAlbedoMap(albedoMap: String, isMultiplyAlbedoMap: Boolean = false) =
            useAlbedoMap(Texture(albedoMap), isMultiplyAlbedoMap)

    fun useAlbedoMap(albedoMap: Texture?, isMultiplyAlbedoMap: Boolean = false) {
        this.albedoMap = albedoMap
        this.isMultiplyAlbedoMap = isMultiplyAlbedoMap
        albedoSource = Albedo.TEXTURE_ALBEDO
    }

    fun useNormalMap(normalMap: String, normalStrength: Float = this.normalStrength) =
            useNormalMap(Texture(normalMap), normalStrength)

    fun useNormalMap(normalMap: Texture?, normalStrength: Float = this.normalStrength) {
        this.normalMap = normalMap
        this.normalStrength = normalStrength
        isNormalMapped = true
    }

    fun useOcclusionMap(occlusionMap: String, occlusionStrength: Float = this.occlusionStrength) =
            useOcclusionMap(Texture(occlusionMap), occlusionStrength)

    fun useOcclusionMap(occlusionMap: Texture?, occlusionStrength: Float = this.occlusionStrength) {
        this.occlusionMap = occlusionMap
        this.occlusionStrength = occlusionStrength
        isOcclusionMapped = true
    }

    fun useDisplacementMap(displacementMap: String, displacementStrength: Float = this.displacementStrength) =
            useDisplacementMap(Texture(displacementMap), displacementStrength)

    fun useDisplacementMap(displacementMap: Texture?, displacementStrength: Float = this.displacementStrength) {
        this.displacementMap = displacementMap
        this.displacementStrength = displacementStrength
        isDisplacementMapped = true
    }

    fun useEmissiveMap(emissiveMap: String, isMultiplyEmissiveMap: Boolean = false) =
            useEmissiveMap(Texture(emissiveMap), isMultiplyEmissiveMap)

    fun useEmissiveMap(emissiveMap: Texture?, isMultiplyEmissiveMap: Boolean = false) {
        this.emissiveMap = emissiveMap
        this.isMultiplyEmissiveMap = isMultiplyEmissiveMap
        isEmissiveMapped = true
    }

    fun useMetallicMap(metallicMap: String, isMultiplyMetallicMap: Boolean = false) =
            useMetallicMap(Texture(metallicMap), isMultiplyMetallicMap)

    fun useMetallicMap(metallicMap: Texture?, isMultiplyMetallicMap: Boolean = false) {
        this.metallicMap = metallicMap
        this.isMultiplyMetallicMap = isMultiplyMetallicMap
        isMetallicMapped = true
    }

    fun useRoughnessMap(roughnessMap: String, isMultiplyRoughnessMap: Boolean = false) =
            useRoughnessMap(Texture(roughnessMap), isMultiplyRoughnessMap)

    fun useRoughnessMap(roughnessMap: Texture?, isMultiplyRoughnessMap: Boolean = false) {
        this.roughnessMap = roughnessMap
        this.isMultiplyRoughnessMap = isMultiplyRoughnessMap
        isRoughnessMapped = true
    }

    fun useScreenSpaceAmbientOcclusion(ssaoMap: Texture?) {
        this.scrSpcAmbientOcclusionMap = ssaoMap
        isScrSpcAmbientOcclusion = true
    }

    fun useImageBasedLighting(environmentMaps: EnvironmentMaps) {
        useImageBasedLighting(environmentMaps.irradianceMap, environmentMaps.reflectionMap, environmentMaps.brdfLut)
    }

    fun useImageBasedLighting(irradianceMap: CubeMapTexture?, reflectionMap: CubeMapTexture?, brdfLut: Texture?) {
        this.irradianceMap = irradianceMap
        this.reflectionMap = reflectionMap
        this.brdfLut = brdfLut
        isImageBasedLighting = irradianceMap != null && reflectionMap != null && brdfLut != null
    }

    fun requiresTexCoords(): Boolean {
        return albedoSource == Albedo.TEXTURE_ALBEDO ||
                isNormalMapped ||
                isRoughnessMapped ||
                isMetallicMapped ||
                isOcclusionMapped ||
                isDisplacementMapped
    }
}