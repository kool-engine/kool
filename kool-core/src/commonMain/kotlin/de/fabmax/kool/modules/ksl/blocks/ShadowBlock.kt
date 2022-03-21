package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.CascadedShadowMap
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

class ShadowBlockVertexStage(cfg: ShadowConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    var inPositionWorldSpace by inFloat3("inPositionWorldSpace")
    var inNormalWorldSpace by inFloat3("inNormalWorldSpace")

    val shadowData: ShadowData
    var positionsLightSpace: KslInterStageVectorArray<KslTypeFloat4, KslTypeFloat1>? = null
        private set
    var normalZsLightSpace: KslInterStageScalarArray<KslTypeFloat1>? = null
        private set

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowData = ShadowData(cfg, this)
                if (shadowData.numSubMaps > 0) {
                    uniformBuffers += shadowData
                    positionsLightSpace = interStageFloat4Array(shadowData.numSubMaps)
                    normalZsLightSpace = interStageFloat1Array(shadowData.numSubMaps)
                }
            }

            shadowData.shadowMapInfos.forEach { mapInfo ->
                mapInfo.subMaps.forEachIndexed { i, subMap ->
                    val subMapIdx = mapInfo.fromIndexIncl + i
                    val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                    val normalLightSpace = float3Var(normalize((viewProj * constFloat4(inNormalWorldSpace, 0f.const)).xyz))
                    normalZsLightSpace!!.input[subMapIdx] set normalLightSpace.z
                    positionsLightSpace!!.input[subMapIdx] set viewProj * constFloat4(inPositionWorldSpace, 1f.const)
                    positionsLightSpace!!.input[subMapIdx].xyz += normalLightSpace * abs(subMap.shaderDepthOffset).const
                }
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

            val depthMaps = vertexStage.shadowData.depthMaps
            vertexStage.shadowData.shadowMapInfos.forEach { mapInfo ->
                val lightIdx = mapInfo.shadowMap.lightIndex
                shadowFactors[lightIdx] set 0f.const

                val positionsLightSpace = vertexStage.positionsLightSpace!!
                val normalZsLightSpace = vertexStage.normalZsLightSpace!!

                when (mapInfo.shadowMap) {
                    is SimpleShadowMap -> {
                        val subMapIdx = mapInfo.fromIndexIncl
                        val posLightSpace = positionsLightSpace.output[subMapIdx]

                        `if` (normalZsLightSpace.output[subMapIdx] lt 0f.const) {
                            // normal points towards light source, compute shadow factor (otherwise it is definitely shadowed)
                            shadowFactors[lightIdx] set getShadowMapFactor(depthMaps.value[subMapIdx], posLightSpace, mapInfo.samplePattern)
                        }
                    }
                    is CascadedShadowMap -> {
                        `if` (normalZsLightSpace.output[mapInfo.fromIndexIncl] lt 0f.const) {
                            val isSampled = boolVar(false.const)
                            val projPos = float3Var()

                            for (i in mapInfo.fromIndexIncl until mapInfo.toIndexExcl) {
                                val posLightSpace = positionsLightSpace.output[i]
                                projPos set posLightSpace.xyz / posLightSpace.w
                                `if` (!isSampled and
                                        all(projPos gt Vec3f(0f, 0f, -1f).const) and
                                        all(projPos lt Vec3f(1f, 1f, 1f).const)) {
                                    // normal points towards light source, compute shadow factor (otherwise it is definitely shadowed)
                                    shadowFactors[lightIdx] set getShadowMapFactor(depthMaps.value[i], posLightSpace, mapInfo.samplePattern)
                                    isSampled set true.const
                                }
                            }
                        }
                    }
                    else -> throw IllegalArgumentException("Unsupported ShadowMap type: ${mapInfo.shadowMap}")
                }
            }
        }
    }
}

class ShadowConfig {
    val shadowMaps = mutableListOf<ShadowMapConfig>()

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
                    val v = Vec2f(-1.5f + x, -1.5f + y)
                    if (v !in this) {
                        add(v)
                    }
                }
            }
        }
    }
}
