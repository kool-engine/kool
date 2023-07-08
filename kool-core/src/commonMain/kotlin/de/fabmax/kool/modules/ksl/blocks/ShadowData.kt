package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.ShadowMap

fun KslProgram.shadowData(shadowCfg: ShadowConfig): ShadowData {
    return (dataBlocks.find { it is ShadowData } as? ShadowData) ?: ShadowData(shadowCfg, this)
}

class ShadowData(val shadowCfg: ShadowConfig, program: KslProgram) : KslDataBlock {
    override val name = NAME

    val numActiveShadows: KslUniformScalar<KslTypeInt1>
    val shadowMapIndexRanges: KslUniformVectorArray<KslTypeInt2, KslTypeInt1>
    val shadowMapViewProjMats: KslUniformMatrixArray<KslTypeMat4, KslTypeFloat4>
    val shadowMapDepthOffsets: KslUniformScalarArray<KslTypeFloat1>
    val lightIndices: KslUniformScalarArray<KslTypeInt1>
    val depthMaps: KslUniformArray<KslTypeDepthSampler2d>

    val shadowUbo: KslUniformBuffer

    init {
        // todo: similar to CameraUbo a shared ubo would make much more sense here
        shadowUbo = KslUniformBuffer("ShadowUniforms", program, false).apply {
            shadowMapViewProjMats = uniformMat4Array(UNIFORM_NAME_SHADOW_VP_MATS, shadowCfg.maxNumShadowMaps)
            shadowMapIndexRanges = uniformInt2Array(UNIFORM_NAME_SHADOW_MAP_INDEX_RANGES, shadowCfg.maxNumShadowMaps)
            shadowMapDepthOffsets = uniformFloat1Array(UNIFORM_NAME_SHADOW_MAP_DEPTH_OFFSETS, shadowCfg.maxNumShadowMaps)
            lightIndices = uniformInt1Array(UNIFORM_NAME_LIGHT_INDICES, shadowCfg.maxNumShadowMaps)
            numActiveShadows = uniformInt1(UNIFORM_NAME_NUM_ACTIVE_SHADOW_MAPS)
        }

        depthMaps = program.depthTextureArray2d(SAMPLER_NAME_SHADOW_MAPS, shadowCfg.maxNumShadowMaps)

        program.dataBlocks += this
        program.uniformBuffers += shadowUbo
    }

    companion object {
        const val NAME = "ShadowData"

        const val UNIFORM_NAME_NUM_ACTIVE_SHADOW_MAPS = "uNumActiveShadowMaps"
        const val UNIFORM_NAME_SHADOW_MAP_INDEX_RANGES = "uShadowMapIndexRanges"
        const val UNIFORM_NAME_SHADOW_MAP_DEPTH_OFFSETS = "uShadowMapDepthOffsets"
        const val UNIFORM_NAME_LIGHT_INDICES = "uShadowMapLightIndices"
        const val UNIFORM_NAME_SHADOW_VP_MATS = "uShadowMapViewProjMats"
        const val SAMPLER_NAME_SHADOW_MAPS = "tShadowDepthMaps"
    }
}

fun KslShader.getShadowDataUniforms(): ShadowDataUniforms? {
    val shadowMapViewProjMats = uniforms[ShadowData.UNIFORM_NAME_SHADOW_VP_MATS] as? UniformMat4fv ?: return null
    val shadowMapIndexRanges = uniforms[ShadowData.UNIFORM_NAME_SHADOW_MAP_INDEX_RANGES] as? Uniform2iv ?: return null
    val shadowMapDepthOffsets = uniforms[ShadowData.UNIFORM_NAME_SHADOW_MAP_DEPTH_OFFSETS] as? Uniform1fv ?: return null
    val lightIndices = uniforms[ShadowData.UNIFORM_NAME_LIGHT_INDICES] as? Uniform1iv ?: return null
    val numActiveMaps = uniforms[ShadowData.UNIFORM_NAME_NUM_ACTIVE_SHADOW_MAPS] as? Uniform1i ?: return null
    val shadowDepthMaps = texSamplers2d[ShadowData.SAMPLER_NAME_SHADOW_MAPS] ?: return null

    return ShadowDataUniforms(
        numActiveMaps,
        lightIndices,
        shadowMapIndexRanges,
        shadowMapViewProjMats,
        shadowMapDepthOffsets,
        shadowDepthMaps
    )
}

class ShadowDataUniforms(
    val numActiveMaps: Uniform1i,
    val lightIndices: Uniform1iv,
    val shadowMapIndexRanges: Uniform2iv,
    val shadowMapViewProjMats: UniformMat4fv,
    val shadowMapDepthOffsets: Uniform1fv,
    val shadowDepthMaps: TextureSampler2d
) {

    fun updateShadowMaps(shadowMaps: List<ShadowMap>) {
        setDummyDepthMaps()

        val maxMaps = lightIndices.length
        val maxSubMaps = shadowMapViewProjMats.length
        var mapI = 0
        var subMapI = 0
        var numActive = 0

        while (mapI < shadowMaps.size && mapI < maxMaps && subMapI < maxSubMaps) {
            val map = shadowMaps[mapI]
            if (subMapI + map.subMaps.size <= maxSubMaps) {
                numActive++
                lightIndices.value[mapI] = map.lightIndex
                shadowMapIndexRanges.value[mapI].apply { x = subMapI; y = subMapI + map.subMaps.size }
                map.subMaps.forEachIndexed { i, subMap ->
                    shadowMapViewProjMats.value[subMapI + i].set(subMap.lightViewProjMat)
                    shadowMapDepthOffsets.value[subMapI + i] = subMap.shaderDepthOffset
                    shadowDepthMaps.textures[subMapI + i] = subMap.depthTexture
                }
            }

            subMapI += map.subMaps.size
            mapI++
        }
        numActiveMaps.value = numActive
    }

    private fun setDummyDepthMaps() {
        if (shadowDepthMaps.textures.last() == null) {
            for (i in shadowDepthMaps.textures.indices) {
                shadowDepthMaps.textures[i] = dummyDepthTex
            }
        }
    }

    companion object {
        private val dummyDepthTex = SingleColorTexture(Color.BLACK)
    }
}
