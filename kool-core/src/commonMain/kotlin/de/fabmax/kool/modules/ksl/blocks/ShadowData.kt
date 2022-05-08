package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.Pipeline
import de.fabmax.kool.pipeline.UniformMat4fv
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap

class ShadowData(shadowCfg: ShadowConfig, program: KslProgram) : KslDataBlock, KslShaderListener {
    override val name = NAME

    val shadowMapInfos: List<ShadowMapInfo>
    val numSubMaps: Int

    val shadowMapViewProjMats: KslUniformMatrixArray<KslTypeMat4, KslTypeFloat4>
    val depthMaps: KslUniformArray<KslTypeDepthSampler2d>

    private var uShadowMapViewProjMats: UniformMat4fv? = null

    init {
        var i = 0
        val mapInfos = mutableListOf<ShadowMapInfo>()
        for (shadowMap in shadowCfg.shadowMaps) {
            val info = ShadowMapInfo(shadowMap.shadowMap, i, shadowMap.samplePattern)
            i = info.toIndexExcl
            mapInfos += info
        }
        shadowMapInfos = mapInfos
        numSubMaps = i

        // If shadowCfg is empty, uniforms are created with array size 0, which is kind of invalid. However, they are
        // also not referenced later on and therefore removed before shader is generated (again because shadowCfg is empty)
        shadowMapViewProjMats = program.uniformMat4Array(UNIFORM_NAME_SHADOW_VP_MATS, numSubMaps)
        depthMaps = program.depthTextureArray2d(SAMPLER_NAME_SHADOW_MAPS, numSubMaps)

        if (numSubMaps > 0) {
            program.shaderListeners += this
        }
    }

    override fun onShaderCreated(shader: KslShader, pipeline: Pipeline, ctx: KoolContext) {
        uShadowMapViewProjMats = shader.uniforms[UNIFORM_NAME_SHADOW_VP_MATS] as? UniformMat4fv

        shader.texSamplers2d[SAMPLER_NAME_SHADOW_MAPS]?.let {
            shadowMapInfos.forEach { mapInfo ->
                for (i in mapInfo.subMaps.indices) {
                    val subMapIdx = mapInfo.fromIndexIncl + i
                    it.textures[subMapIdx] = mapInfo.subMaps[i].depthTexture
                }
            }
        }
    }

    override fun onUpdate(cmd: DrawCommand) {
        uShadowMapViewProjMats?.let { mats ->
            for (i in shadowMapInfos.indices) {
                val mapInfo = shadowMapInfos[i]
                for (j in mapInfo.subMaps.indices) {
                    mats.value[mapInfo.fromIndexIncl + j].set(mapInfo.subMaps[j].lightViewProjMat)
                }
            }
        }
    }

    class ShadowMapInfo(val shadowMap: ShadowMap, val fromIndexIncl: Int, val samplePattern: List<Vec2f>) {
        val subMaps: List<SimpleShadowMap> = when (shadowMap) {
            is SimpleShadowMap -> listOf(shadowMap)
            is CascadedShadowMap -> listOf(*shadowMap.cascades)
            else -> throw IllegalArgumentException("Unsupported ShadowMap type: $shadowMap")
        }

        val toIndexExcl = fromIndexIncl + subMaps.size
    }

    companion object {
        const val NAME = "ShadowData"

        const val UNIFORM_NAME_SHADOW_VP_MATS = "uShadowMapViewProjMats"
        const val SAMPLER_NAME_SHADOW_MAPS = "tDepthMaps"
    }
}