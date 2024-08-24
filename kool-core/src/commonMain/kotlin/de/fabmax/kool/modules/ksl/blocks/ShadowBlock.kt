package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.LightingConfig
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.CascadedShadowMap
import de.fabmax.kool.util.SimpleShadowMap

fun KslScopeBuilder.vertexShadowBlock(cfg: LightingConfig, block: ShadowBlockVertexStage.() -> Unit): ShadowBlockVertexStage {
    val shadowBlock = ShadowBlockVertexStage(cfg, parentStage.program.nextName("shadowBlock"), this)
    shadowBlock.block()
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentShadowBlock(vertexStage: ShadowBlockVertexStage, shadowFactors: KslArrayScalar<KslFloat1>): ShadowBlockFragmentStage {
    val shadowBlock = ShadowBlockFragmentStage(
        lightSpacePositions = vertexStage.positionsLightSpace.map { it.output },
        lightSpaceNormalZs = vertexStage.normalZsLightSpace.map { it.output },
        shadowData = vertexStage.shadowData,
        shadowFactors = shadowFactors,
        name = parentStage.program.nextName("shadowBlock"),
        parentScope = this
    )
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentOnlyShadowBlock(
    cfg: LightingConfig,
    positionWorldSPace: KslExprFloat3,
    normalWorldSPace: KslExprFloat3,
    shadowFactors: KslArrayScalar<KslFloat1>
): ShadowBlockFragmentStage {
    val shadowData = parentStage.program.shadowData(cfg)
    val normalZs = List(shadowData.numSubMaps) { float1Var() }
    val lightSpacePos = List(shadowData.numSubMaps) { float4Var() }

    shadowData.shadowMapInfos.forEach { mapInfo ->
        mapInfo.subMaps.forEachIndexed { i, subMap ->
            val subMapIdx = mapInfo.fromIndexIncl + i
            val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
            val normalLightSpace = float3Var(normalize((viewProj * float4Value(normalWorldSPace, 0f.const)).xyz))
            normalZs[subMapIdx] set normalLightSpace.z
            lightSpacePos[subMapIdx] set viewProj * float4Value(positionWorldSPace, 1f.const)
            lightSpacePos[subMapIdx].xyz += normalLightSpace * subMap.shaderDepthOffset.const * sign(normalLightSpace.z)
        }
    }

    val shadowBlock = ShadowBlockFragmentStage(
        lightSpacePositions = lightSpacePos,
        lightSpaceNormalZs = normalZs,
        shadowData = shadowData,
        shadowFactors = shadowFactors,
        name = parentStage.program.nextName("shadowBlock"),
        parentScope = this
    )
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentShadowBlock(
    lightSpacePositions: List<KslExprFloat4>,
    lightSpaceNormalZs: List<KslExprFloat1>,
    shadowData: ShadowData,
    shadowFactors: KslArrayScalar<KslFloat1>
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

class ShadowBlockVertexStage(cfg: LightingConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    var inPositionWorldSpace = inFloat3("inPositionWorldSpace")
    var inNormalWorldSpace = inFloat3("inNormalWorldSpace")

    val shadowData: ShadowData
    val positionsLightSpace: MutableList<KslInterStageVector<KslFloat4, KslFloat1>> = mutableListOf()
    val normalZsLightSpace: MutableList<KslInterStageScalar<KslFloat1>> = mutableListOf()

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowData = shadowData(cfg)
                repeat(shadowData.numSubMaps) { positionsLightSpace += interStageFloat4() }
                repeat(shadowData.numSubMaps) { normalZsLightSpace += interStageFloat1() }
            }

            shadowData.shadowMapInfos.forEach { mapInfo ->
                mapInfo.subMaps.forEachIndexed { i, subMap ->
                    val subMapIdx = mapInfo.fromIndexIncl + i
                    val viewProj = shadowData.shadowMapViewProjMats[subMapIdx]
                    val normalLightSpace = float3Var(normalize((viewProj * float4Value(inNormalWorldSpace, 0f.const)).xyz))
                    normalZsLightSpace[subMapIdx].input set normalLightSpace.z
                    positionsLightSpace[subMapIdx].input set viewProj * float4Value(inPositionWorldSpace, 1f.const)
                    positionsLightSpace[subMapIdx].input.xyz += normalLightSpace * subMap.shaderDepthOffset.const * sign(normalLightSpace.z)
                }
            }
        }
    }
}

class ShadowBlockFragmentStage(
    lightSpacePositions: List<KslExprFloat4>,
    lightSpaceNormalZs: List<KslExprFloat1>,
    val shadowData: ShadowData,
    val shadowFactors: KslArrayScalar<KslFloat1>,
    name: String,
    parentScope: KslScopeBuilder
) : KslBlock(name, parentScope) {

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "SimpleShadowMapBlockFragmentStage can only be added to KslFragmentStage" }
            if (lightSpacePositions.isEmpty() || lightSpaceNormalZs.isEmpty()) {
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
        lightSpacePositions: List<KslExprFloat4>,
        lightSpaceNormalZs: List<KslExprFloat1>,
        mapInfo: ShadowData.ShadowMapInfo
    ) {
        val light = requireNotNull(mapInfo.shadowMap.light) { "ShadowMap light must be set before creating a shader with it" }
        val subMapIdx = mapInfo.fromIndexIncl
        val posLightSpace = lightSpacePositions[subMapIdx]

        `if` (shadowData.shadowCfg.flipBacksideNormals.const or (lightSpaceNormalZs[subMapIdx] lt 0f.const)) {
            // normal points towards light source, compute shadow factor
            shadowFactors[light.lightIndex] set getShadowMapFactor(shadowData.depthMaps[subMapIdx], posLightSpace, mapInfo.samplePattern)
        }.`else` {
            // normal points away from light source, set shadow factor to 0 (shadowed)
            shadowFactors[light.lightIndex] set 0f.const
        }
    }

    private fun KslScopeBuilder.sampleCascadedShadowMap(
        lightSpacePositions: List<KslExprFloat4>,
        lightSpaceNormalZs: List<KslExprFloat1>,
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
                    sampleSum += getShadowMapFactor(shadowData.depthMaps[i], posLightSpace, mapInfo.samplePattern) * w
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
