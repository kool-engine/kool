package de.fabmax.kool.pipeline.shading

import de.fabmax.kool.pipeline.Attribute
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.Texture2d
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
    var isAoMapped = false
    var isDisplacementMapped = false

    var isInstanced = false
    var isSkinned = false
    var maxJoints = 64

    val morphAttributes = mutableListOf<Attribute>()

    var albedoMapMode = AlbedoMapMode.UNMODIFIED
    var isMultiplyEmissive = false
    var isMultiplyRoughness = false
    var isMultiplyMetallic = false

    var isImageBasedLighting = false
    var isScrSpcAmbientOcclusion = false
    var isRefraction = false
    var isRefractByDepthMap = false

    var maxLights = 4
    val shadowMaps = mutableListOf<ShadowMap>()
    var isAlwaysLit = false
    var ambientShadowFactor = 0f
    var isMoving = false

    var cullMethod = CullMethod.CULL_BACK_FACES
    var alphaMode: AlphaMode = AlphaModeOpaque()
    var isHdrOutput = false

    var roughnessChannel = "r"
    var roughnessTexName = "tRoughness"
    var metallicChannel = "r"
    var metallicTexName = "tMetallic"
    var occlusionChannel = "r"
    var aoTexName = "tOcclusion"

    // initial shader attribute values
    var albedo = Color.GRAY
    var emissive = Color.BLACK
    var roughness = 0.5f
    var metallic = 0.0f
    var refractionIor = 1.4f
    var materialThickness = 1f

    var useVertexAttributeEmissive = false
    var useVertexAttributeMetalRough = false

    var normalStrength = 1f
    var displacementStrength = 0.1f
    var aoStrength = 1f
    var reflectionStrength = 1f

    var albedoMap: Texture2d? = null
    var emissiveMap: Texture2d? = null
    var normalMap: Texture2d? = null
    var displacementMap: Texture2d? = null
    var roughnessMap: Texture2d? = null
    var metallicMap: Texture2d? = null
    var aoMap: Texture2d? = null

    var environmentMaps: EnvironmentMaps? = null

    var scrSpcAmbientOcclusionMap: Texture2d? = null
    var refractionColorMap: Texture2d? = null
    var refractionDepthMap: Texture2d? = null

    var onPipelineSetup: (PbrShader.(Pipeline.Builder) -> Unit)? = null
    var onPipelineCreated: (PbrShader.() -> Unit)? = null

    fun useStaticAlbedo(albedo: Color) {
        albedoSource = Albedo.STATIC_ALBEDO
        this.albedo = albedo
    }

    fun useAlbedoMap(albedoMap: String, albedoMapMode: AlbedoMapMode = AlbedoMapMode.UNMODIFIED) =
            useAlbedoMap(Texture2d(albedoMap), albedoMapMode)

    fun useAlbedoMap(albedoMap: Texture2d?, albedoMapMode: AlbedoMapMode = AlbedoMapMode.UNMODIFIED) {
        this.albedoMap = albedoMap
        this.albedoMapMode = albedoMapMode
        albedoSource = Albedo.TEXTURE_ALBEDO
    }

    fun useNormalMap(normalMap: String, normalStrength: Float = this.normalStrength) =
            useNormalMap(Texture2d(normalMap), normalStrength)

    fun useNormalMap(normalMap: Texture2d?, normalStrength: Float = this.normalStrength) {
        this.normalMap = normalMap
        this.normalStrength = normalStrength
        isNormalMapped = true
    }

    fun useAmbientOcclusionMap(aoMap: String, aoStrength: Float = this.aoStrength) =
            useAmbientOcclusionMap(Texture2d(aoMap), aoStrength)

    fun useAmbientOcclusionMap(aoMap: Texture2d?, aoStrength: Float = this.aoStrength) {
        this.aoMap = aoMap
        this.aoStrength = aoStrength
        isAoMapped = true
    }

    fun useDisplacementMap(displacementMap: String, displacementStrength: Float = this.displacementStrength) =
            useDisplacementMap(Texture2d(displacementMap), displacementStrength)

    fun useDisplacementMap(displacementMap: Texture2d?, displacementStrength: Float = this.displacementStrength) {
        this.displacementMap = displacementMap
        this.displacementStrength = displacementStrength
        isDisplacementMapped = true
    }

    fun useEmissiveMap(emissiveMap: String, isMultiplyEmissive: Boolean = false) =
            useEmissiveMap(Texture2d(emissiveMap), isMultiplyEmissive)

    fun useEmissiveMap(emissiveMap: Texture2d?, isMultiplyEmissiveMap: Boolean = false) {
        this.emissiveMap = emissiveMap
        this.isMultiplyEmissive = isMultiplyEmissiveMap
        isEmissiveMapped = true
    }

    fun useEmissiveVertexAttrib(isMultiplyEmissive: Boolean = false) {
        this.emissiveMap = null
        this.isMultiplyEmissive = isMultiplyEmissive
        useVertexAttributeEmissive = true
        isEmissiveMapped = true
    }

    fun useMetallicMap(metallicMap: String, isMultiplyMetallicMap: Boolean = false) =
            useMetallicMap(Texture2d(metallicMap), isMultiplyMetallicMap)

    fun useMetallicMap(metallicMap: Texture2d?, isMultiplyMetallicMap: Boolean = false) {
        this.metallicMap = metallicMap
        this.isMultiplyMetallic = isMultiplyMetallicMap
        isMetallicMapped = true
    }

    fun useRoughnessMap(roughnessMap: String, isMultiplyRoughnessMap: Boolean = false) =
            useRoughnessMap(Texture2d(roughnessMap), isMultiplyRoughnessMap)

    fun useRoughnessMap(roughnessMap: Texture2d?, isMultiplyRoughnessMap: Boolean = false) {
        this.roughnessMap = roughnessMap
        this.isMultiplyRoughness = isMultiplyRoughnessMap
        isRoughnessMapped = true
    }

    fun useMetalRoughVertexAttrib(isMultiplyMetalRough: Boolean = false) {
        this.metallicMap = null
        this.roughnessMap = null
        this.isMultiplyMetallic = isMultiplyMetalRough
        this.isMultiplyRoughness = isMultiplyMetalRough
        useVertexAttributeMetalRough = true
        isMetallicMapped = true
        isRoughnessMapped = true
    }

    fun useScreenSpaceAmbientOcclusion(ssaoMap: Texture2d?) {
        this.scrSpcAmbientOcclusionMap = ssaoMap
        isScrSpcAmbientOcclusion = true
    }

    fun useRefraction(refractionColorMap: Texture2d?, refractionDepthMap: Texture2d? = null) {
        this.refractionColorMap = refractionColorMap
        this.refractionDepthMap = refractionDepthMap
        isRefraction = this.refractionColorMap != null
        isRefractByDepthMap = this.refractionDepthMap != null
    }

    fun useImageBasedLighting(environmentMaps: EnvironmentMaps?) {
        this.environmentMaps = environmentMaps
        isImageBasedLighting = environmentMaps != null
    }

    fun requiresTexCoords(): Boolean {
        return albedoSource == Albedo.TEXTURE_ALBEDO ||
                isNormalMapped ||
                (isRoughnessMapped && !useVertexAttributeMetalRough) ||
                (isMetallicMapped && !useVertexAttributeMetalRough) ||
                isAoMapped ||
                isDisplacementMapped ||
                (isEmissiveMapped && !useVertexAttributeEmissive)
    }
}