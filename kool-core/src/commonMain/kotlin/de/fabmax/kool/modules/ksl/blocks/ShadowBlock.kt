package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.ShadowMap
import de.fabmax.kool.util.SimpleShadowMap

fun KslScopeBuilder.vertexShadowBlock(cfg: ShadowConfig, block: ShadowBlockVertexStage.() -> Unit): ShadowBlockVertexStage {
    val shadowBlock = ShadowBlockVertexStage(cfg, parentStage.program.nextName("shadowBlock"), this)
    shadowBlock.block()
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentShadowBlock(vertexStage: ShadowBlockVertexStage, shadowFactors: KslArrayScalar<KslTypeFloat1>): ShadowBlockFragmentStage {
    val shadowBlock = ShadowBlockFragmentStage(
        lightSpacePositions = vertexStage.positionsLightSpace?.output,
        lightSpaceNormalZs = vertexStage.normalZsLightSpace?.output,
        shadowData = vertexStage.shadowData,
        shadowFactors = shadowFactors,
        name = parentStage.program.nextName("shadowBlock"),
        parentScope = this
    )
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentShadowBlock(
    lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>,
    lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>,
    shadowData: ShadowData,
    shadowFactors: KslArrayScalar<KslTypeFloat1>
): ShadowBlockFragmentStage {
    val shadowBlock = ShadowBlockFragmentStage(
        lightSpacePositions = lightSpacePositions,
        lightSpaceNormalZs = lightSpaceNormalZs,
        shadowData = shadowData,
        shadowFactors = shadowFactors,
        name = parentStage.program.nextName("shadowBlock"),
        parentScope = this
    )
    ops += shadowBlock
    return shadowBlock
}

class ShadowBlockVertexStage(cfg: ShadowConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    var inPositionWorldSpace = inFloat3("inPositionWorldSpace")
    var inNormalWorldSpace = inFloat3("inNormalWorldSpace")

    val shadowData: ShadowData
    var positionsLightSpace: KslInterStageVectorArray<KslTypeFloat4, KslTypeFloat1>? = null
        private set
    var normalZsLightSpace: KslInterStageScalarArray<KslTypeFloat1>? = null
        private set

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowData = shadowData(cfg)
                if (shadowData.numSubMaps > 0) {
                    positionsLightSpace = interStageFloat4Array(shadowData.numSubMaps)
                    normalZsLightSpace = interStageFloat1Array(shadowData.numSubMaps)
                }
            }

            shadowData.shadowMapInfos.forEach { mapInfo ->
                mapInfo.subMaps.forEachIndexed { i, subMap ->
                    val subMapIdx = mapInfo.fromIndexIncl + i
                    val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                    val normalLightSpace = float3Var(normalize((viewProj * float4Value(inNormalWorldSpace, 0f.const)).xyz))
                    normalZsLightSpace!!.input[subMapIdx] set normalLightSpace.z
                    positionsLightSpace!!.input[subMapIdx] set viewProj * float4Value(inPositionWorldSpace, 1f.const)
                    positionsLightSpace!!.input[subMapIdx].xyz += normalLightSpace * subMap.shaderDepthOffset.const * sign(normalLightSpace.z)
                }
            }
        }
    }
}

class ShadowBlockFragmentStage(
    lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>?,
    lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>?,
    val shadowData: ShadowData,
    val shadowFactors: KslArrayScalar<KslTypeFloat1>,
    name: String,
    parentScope: KslScopeBuilder
) : KslBlock(name, parentScope) {

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "SimpleShadowMapBlockFragmentStage can only be added to KslFragmentStage" }
            if (lightSpacePositions == null || lightSpaceNormalZs == null) {
                return@apply
            }

            shadowData.shadowMapInfos.forEach { mapInfo ->
                val light = requireNotNull(mapInfo.shadowMap.light) { "ShadowMap light must be set before creating a shader with it" }
                shadowFactors[light.lightIndex] set 1f.const

                when (mapInfo.shadowMap) {
                    is SimpleShadowMap -> {
                        sampleSimpleShadowMap(lightSpacePositions, lightSpaceNormalZs, mapInfo)
                    }
                    is CascadedShadowMap -> {
                        sampleCascadedShadowMap(lightSpacePositions, lightSpaceNormalZs, mapInfo)
                    }
                }
            }
        }
    }

    private fun KslScopeBuilder.sampleSimpleShadowMap(
        lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>,
        lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>,
        mapInfo: ShadowData.ShadowMapInfo
    ) {
        val light = requireNotNull(mapInfo.shadowMap.light) { "ShadowMap light must be set before creating a shader with it" }
        val subMapIdx = mapInfo.fromIndexIncl
        val posLightSpace = lightSpacePositions[subMapIdx]

        `if` (shadowData.shadowCfg.flipBacksideNormals.const or (lightSpaceNormalZs[subMapIdx] lt 0f.const)) {
            // normal points towards light source, compute shadow factor
            shadowFactors[light.lightIndex] set getShadowMapFactor(shadowData.depthMaps.value[subMapIdx], posLightSpace, mapInfo.samplePattern)
        }.`else` {
            // normal points away from light source, set shadow factor to 0 (shadowed)
            shadowFactors[light.lightIndex] set 0f.const
        }
    }

    private fun KslScopeBuilder.sampleCascadedShadowMap(
        lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>,
        lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>,
        mapInfo: ShadowData.ShadowMapInfo
    ) {
        val lightIdx = mapInfo.shadowMap.light?.lightIndex ?: 0

        `if`(shadowData.shadowCfg.flipBacksideNormals.const or (lightSpaceNormalZs[mapInfo.fromIndexIncl] lt 0f.const)) {
            // normal points towards light source, compute shadow factor
            val sampleW = float1Var(0f.const)
            val sampleSum = float1Var(0f.const)
            val projPos = float3Var()

            for (i in mapInfo.fromIndexIncl until mapInfo.toIndexExcl) {
                // check for each shadow map (sorted from high detail to low detail) if projected
                // position is inside map bounds, if so sample it and stop
                val posLightSpace = lightSpacePositions[i]
                projPos set posLightSpace.xyz / posLightSpace.w
                `if`(sampleW lt 0.999f.const and
                        all(projPos gt Vec3f(0f, 0f, -1f).const) and
                        all(projPos lt Vec3f(1f, 1f, 1f).const)) {

                    // determine how close proj pos is to shadow map border and use that to blend
                    // between cascades
                    val p = float2Var(abs((projPos.xy - 0.5f.const) * 2f.const))
                    val c = 1f.const - clamp(max(p.x, p.y) - 0.9f.const, 0f.const, 0.05f.const) * 10f.const
                    val w = float1Var(c * (1f.const - sampleW))

                    // projected position is inside shadow map bounds, sample shadow map
                    sampleSum += getShadowMapFactor(shadowData.depthMaps.value[i], posLightSpace, mapInfo.samplePattern) * w
                    sampleW += w
                }
            }
            `if`(sampleW gt 0f.const) {
                shadowFactors[lightIdx] set sampleSum / sampleW
            }

        }.`else` {
            // normal points away from light source, set shadow factor to 0 (shadowed)
            shadowFactors[lightIdx] set 0f.const
        }
    }
}

class ShadowConfig {
    val shadowMaps = mutableListOf<ShadowMapConfig>()
    var flipBacksideNormals = false

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
