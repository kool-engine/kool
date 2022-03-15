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
    val positionsLightSpace: KslInterStageVectorArray<KslTypeFloat4, KslTypeFloat1>
    val normalZsLightSpace: KslInterStageScalarArray<KslTypeFloat1>
//    val positionsLightSpace = mutableListOf<KslInterStageVector<KslTypeFloat4, KslTypeFloat1>>()
//    val normalZsLightSpace = mutableListOf<KslInterStageScalar<KslTypeFloat1>>()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowData = ShadowData(cfg, this)
                if (cfg.numShadowMaps > 0) {
                    uniformBuffers += shadowData
                }

                positionsLightSpace = interStageFloat4Array(cfg.numShadowMaps)
                normalZsLightSpace = interStageFloat1Array(cfg.numShadowMaps)

//                repeat(cfg.shadowMaps.size) { i ->
//                    positionsLightSpace += interStageFloat4(name = "positionsLightSpace_$i")
//                    normalZsLightSpace += interStageFloat1(name = "normalZLightSpace_$i")
//                }
            }

            cfg.shadowMaps.forEachIndexed { i, shadowMapCfg ->
                val shadowMap = shadowMapCfg.shadowMap as? SimpleShadowMap ?: TODO()
                val viewProj = shadowData.shadowMapViewProjMats[i]
                val normalLightSpace = float3Var((viewProj * constFloat4(inNormalWorldSpace, 0f.const)).xyz)
                normalZsLightSpace.input[i] set normalLightSpace.z
                positionsLightSpace.input[i] set viewProj * constFloat4(inPositionWorldSpace, 1f.const)
                positionsLightSpace.input[i].xyz += normalLightSpace * abs(shadowMap.shaderDepthOffset).const
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
                `if` (vertexStage.normalZsLightSpace.output[i] lt 0f.const) {
                    val posLightSpace = vertexStage.positionsLightSpace.output[i]
                    shadowFactors[shadowMap.lightIndex] set getShadowMapFactor(depthMap.value[i], posLightSpace, shadowMapCfg.samplePattern)
                }
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
            // corners are sampled first
            add(Vec2f(-1.5f, -1.5f))
            add(Vec2f(-1.5f, 1.5f))
            add(Vec2f(1.5f, -1.5f))
            add(Vec2f(1.5f, 1.5f))

            // in between sampler (if corners have diverging shadow factors)
            for (y in 0..3) {
                for (x in 0..3) {
                    val v = Vec2f(-1.5f + x, -1.5f + y)
                    if (v !in this) {
                        add(v)
                    }
                }
            }
            check(size == 16)
        }
    }
}
