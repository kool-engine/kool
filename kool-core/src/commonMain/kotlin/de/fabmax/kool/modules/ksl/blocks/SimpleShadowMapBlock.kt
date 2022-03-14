package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.SimpleShadowMap

fun KslScopeBuilder.vertexSimpleShadowMap(cfg: SimpleShadowMapConfig): SimpleShadowMapBlockVertexStage {
    val shadowBlock = SimpleShadowMapBlockVertexStage(cfg, parentStage.program.nextName("simpleShadowMap"), this)
    ops += shadowBlock
    return shadowBlock
}

fun KslScopeBuilder.fragmentSimpleShadowMap(vertexStage: SimpleShadowMapBlockVertexStage): SimpleShadowMapBlockFragmentStage {
    val colorBlock = SimpleShadowMapBlockFragmentStage(vertexStage, parentStage.program.nextName("simpleShadowMap"), this)
    ops += colorBlock
    return colorBlock
}

class SimpleShadowMapBlockVertexStage(val cfg: SimpleShadowMapConfig, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    var inPositionWorldSpace by inFloat3("inPositionWorldSpace")
    var inNormalWorldSpace by inFloat3("inNormalWorldSpace")

    val shadowMapData: ShadowMapData
    val positionLightSpace: KslInterStageVector<KslTypeFloat4, KslTypeFloat1>
    val normalZLightSpace: KslInterStageScalar<KslTypeFloat1>

    init {
        body.apply {
            check(parentStage is KslVertexStage) { "SimpleShadowMapBlockVertexStage can only be added to KslVertexStage" }

            parentScope.parentStage.program.apply {
                shadowMapData = ShadowMapData(cfg.shadowMap, this)
                uniformBuffers += shadowMapData

                positionLightSpace = interStageFloat4()
                normalZLightSpace = interStageFloat1()
            }

            val normalLightSpace = float3Var((shadowMapData.shadowMapViewProj * constFloat4(inNormalWorldSpace, 0f.const)).xyz)
            normalZLightSpace.input set normalLightSpace.z
            positionLightSpace.input set shadowMapData.shadowMapViewProj * constFloat4(inPositionWorldSpace, 1f.const)
            positionLightSpace.input.xyz += normalLightSpace * 0.005f.const
        }
    }
}

class SimpleShadowMapBlockFragmentStage(vertexStage: SimpleShadowMapBlockVertexStage, name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val outLightIndex = outInt1("outLightIndex")
    val outShadowFactor = outFloat1("outShadowFactor")

    init {
        body.apply {
            check(parentStage is KslFragmentStage) { "SimpleShadowMapBlockFragmentStage can only be added to KslFragmentStage" }

            val cfg = vertexStage.cfg
            val depthMap = vertexStage.shadowMapData.depthMap
            val sampleStep = float2Var(1f.const / textureSize2d(depthMap).toFloat2())

            outLightIndex set cfg.lightIndex.const
            outShadowFactor set 0f.const
            `if` (vertexStage.normalZLightSpace.output lt 0f.const) {
                cfg.samplerPattern.forEach { pos ->
                    val shadowPos = float3Var(vertexStage.positionLightSpace.output.xyz / vertexStage.positionLightSpace.output.w)
                    shadowPos.float2("xy") += pos.const * sampleStep
                    outShadowFactor += sampleDepthTexture(depthMap, shadowPos)
                }
                outShadowFactor *= (1f / cfg.samplerPattern.size).const
            }
        }
    }
}

class SimpleShadowMapConfig(val shadowMap: SimpleShadowMap, val lightIndex: Int, val samplerPattern: List<Vec2f> = SAMPLE_PATTERN_4x4) {

    companion object {
        val SAMPLE_PATTERN_1x1 = listOf(Vec2f.ZERO)
        val SAMPLE_PATTERN_4x4: List<Vec2f>

        init {
            SAMPLE_PATTERN_4x4 = mutableListOf<Vec2f>().apply {
                for (y in 0..3) {
                    for (x in 0..3) {
                        this += Vec2f(-1.5f + x, -1.5f + y)
                    }
                }
            }
        }
    }
}
