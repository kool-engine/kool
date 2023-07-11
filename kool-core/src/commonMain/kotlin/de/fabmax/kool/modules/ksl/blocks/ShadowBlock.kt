package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.ShadowMap

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

                positionsLightSpace = interStageFloat4Array(cfg.maxNumShadowMaps)
                normalZsLightSpace = interStageFloat1Array(cfg.maxNumShadowMaps)
            }

            `if`(shadowData.numActiveShadows gt 0.const) {
                val totalSubMaps = int1Var(shadowData.shadowMapIndexRanges[shadowData.numActiveShadows - 1.const].y)
                repeat(totalSubMaps) { subMapIdx ->
                    val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                    val normalLightSpace = float3Var(normalize((viewProj * float4Value(inNormalWorldSpace, 0f.const)).xyz))
                    normalZsLightSpace!!.input[subMapIdx] set normalLightSpace.z
                    positionsLightSpace!!.input[subMapIdx] set viewProj * float4Value(inPositionWorldSpace, 1f.const)
                    positionsLightSpace!!.input[subMapIdx].xyz += normalLightSpace * shadowData.shadowMapDepthOffsets[subMapIdx] * sign(normalLightSpace.z)
                }

                // fixme: we need to reference lightIndices here to prevent it being removed from ubo
                totalSubMaps set shadowData.lightIndices[shadowData.numActiveShadows]
            }
        }
    }
}

class ShadowBlockFragmentStage(
    lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>?,
    lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>?,
    private val shadowData: ShadowData,
    private val shadowFactors: KslArrayScalar<KslTypeFloat1>,
    name: String,
    parentScope: KslScopeBuilder
) : KslBlock(name, parentScope) {

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "SimpleShadowMapBlockFragmentStage can only be added to KslFragmentStage" }
            if (lightSpacePositions == null || lightSpaceNormalZs == null) {
                return@apply
            }

            repeat(shadowData.numActiveShadows) { mapI ->
                val lightIdx = shadowData.lightIndices[mapI]
                val mapRange = shadowData.shadowMapIndexRanges[mapI]
                val numSubMaps = mapRange.y - mapRange.x

                `if`(numSubMaps eq 1.const) {
                    // only one sub-map -> SimpleShadowMap
                    sampleSimpleShadowMap(lightSpacePositions, lightSpaceNormalZs, lightIdx, mapRange.x)
                }.`else` {
                    // more than one sub-map -> CascadedShadowMap
                    sampleCascadedShadowMap(lightSpacePositions, lightSpaceNormalZs, lightIdx, mapRange)
                }
            }
        }
    }

    private fun KslScopeBuilder.sampleSimpleShadowMap(
        lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>,
        lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>,
        lightIdx: KslExprInt1,
        subMapI: KslExprInt1
    ) {
        val posLightSpace = lightSpacePositions[subMapI]
        `if` (shadowData.shadowCfg.flipBacksideNormals.const or (lightSpaceNormalZs[subMapI] lt 0f.const)) {
            // normal points towards light source, compute shadow factor
            shadowFactors[lightIdx] set getShadowMapFactor(shadowData.depthMaps.value[subMapI], posLightSpace, ShadowConfig.SAMPLE_PATTERN_4x4)
        }.`else` {
            // normal points away from light source, set shadow factor to 0 (shadowed)
            shadowFactors[lightIdx] set 0f.const
        }
    }

    private fun KslScopeBuilder.sampleCascadedShadowMap(
        lightSpacePositions: KslArrayVector<KslTypeFloat4, KslTypeFloat1>,
        lightSpaceNormalZs: KslArrayScalar<KslTypeFloat1>,
        lightIdx: KslExprInt1,
        mapRange: KslExprInt2
    ) {
        val from = mapRange.x
        val to = mapRange.y
        `if`(shadowData.shadowCfg.flipBacksideNormals.const or (lightSpaceNormalZs[from] lt 0f.const)) {
            // normal points towards light source, compute shadow factor
            val sampleW = float1Var(0f.const)
            val sampleSum = float1Var(0f.const)
            val projPos = float3Var()

            fori(from, to) { i ->
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
                    sampleSum += getShadowMapFactor(shadowData.depthMaps.value[i], posLightSpace, ShadowConfig.SAMPLE_PATTERN_4x4) * w
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
    var maxNumShadowMaps = 4
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
