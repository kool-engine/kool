package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap
import kotlin.math.abs

fun KslScopeBuilder.vertexShadowBlock(cfg: ShadowConfig, block: ShadowBlockVertexStage.() -> Unit): ShadowBlockVertexStage {
    val shadowBlock = ShadowBlockVertexStage(cfg, parentStage.program.nextName("shadowBlock"), this)
    shadowBlock.block()
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentShadowBlock(vertexStage: ShadowBlockVertexStage, shadowFactors: KslArrayScalar<KslTypeFloat1>): ShadowBlockFragmentStage {
    val colorBlock = ShadowBlockFragmentStage(vertexStage, shadowFactors, parentStage.program.nextName("shadowBlock"), this)
    ops += colorBlock
    return colorBlock
}

class ShadowBlockVertexStage(val cfg: ShadowConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    var inPositionWorldSpace by inFloat3("inPositionWorldSpace")
    var inNormalWorldSpace by inFloat3("inNormalWorldSpace")

    val shadowData: ShadowData
    val positionsLightSpace = mutableListOf<KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()
    val normalZsLightSpace = mutableListOf<KslInterStageScalar<KslTypeFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowData = ShadowData(cfg, this)
                if (cfg.numShadowMaps > 0) {
                    uniformBuffers += shadowData
                }

                repeat(cfg.shadowMaps.size) {
                    positionsLightSpace += interStageFloat4()
                    normalZsLightSpace += interStageFloat1()
                }
            }

            cfg.shadowMaps.forEachIndexed { i, shadowMapCfg ->
                val shadowMap = shadowMapCfg.shadowMap as? SimpleShadowMap ?: TODO()
                val viewProj = shadowData.shadowMapViewProjMats[i]
                val normalLightSpace = float3Var((viewProj * constFloat4(inNormalWorldSpace, 0f.const)).xyz)
                normalZsLightSpace[i].input set normalLightSpace.z
                positionsLightSpace[i].input set viewProj * constFloat4(inPositionWorldSpace, 1f.const)
                positionsLightSpace[i].input.xyz += normalLightSpace * abs(shadowMap.shaderDepthOffset).const
            }
        }
    }
}

class ShadowBlockFragmentStage(
    vertexStage: ShadowBlockVertexStage,
    shadowFactors: KslArrayScalar<KslTypeFloat1>,
    name: String, parentScope: KslScopeBuilder)
    : KslBlock(name, parentScope) {

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "SimpleShadowMapBlockFragmentStage can only be added to KslFragmentStage" }

            val cfg = vertexStage.cfg
            val depthMap = vertexStage.shadowData.depthMaps
            cfg.shadowMaps.forEachIndexed { i, shadowMapCfg ->
                val shadowMap = shadowMapCfg.shadowMap as? SimpleShadowMap ?: TODO()
                val posLightSpace = vertexStage.positionsLightSpace[i].output
                shadowFactors[shadowMap.lightIndex] set getShadowMapFactor(depthMap.value[i], posLightSpace, shadowMapCfg.samplePattern)
            }
        }
    }
}

class ShadowConfig {
    val shadowMaps = mutableListOf<ShadowMapConfig>()
    val numShadowMaps: Int
        get() = shadowMaps.size

    fun addShadowMap(shadowMap: ShadowMap, samplePattern: List<Vec2f> = SAMPLE_PATTERN_4x4) {
        shadowMaps += ShadowMapConfig(shadowMap, samplePattern)
    }

    fun addShadowMaps(shadowMaps: Collection<ShadowMap>, samplePattern: List<Vec2f> = SAMPLE_PATTERN_4x4) {
        this.shadowMaps += shadowMaps.map { ShadowMapConfig(it, samplePattern) }
    }

    class ShadowMapConfig(val shadowMap: ShadowMap, val samplePattern: List<Vec2f> = SAMPLE_PATTERN_4x4)

    companion object {
        val SAMPLE_PATTERN_1x1: List<Vec2f> = listOf(Vec2f.ZERO)
        val SAMPLE_PATTERN_4x4: List<Vec2f> = mutableListOf<Vec2f>().apply {
            for (y in 0..3) {
                for (x in 0..3) {
                    this += Vec2f(-1.5f + x, -1.5f + y)
                }
            }
        }
    }
}
