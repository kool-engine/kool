package de.fabmax.kool.modules.ksl.blocks

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

            outLightIndex set vertexStage.cfg.lightIndex.const

            val shadowPos = float3Var(vertexStage.positionLightSpace.output.xyz / vertexStage.positionLightSpace.output.w)
            outShadowFactor set 0f.const
            `if` (vertexStage.normalZLightSpace.output lt 0f.const) {
                outShadowFactor set sampleDepthTexture(vertexStage.shadowMapData.depthMap, shadowPos)
            }
        }
    }
}

class SimpleShadowMapConfig(val shadowMap: SimpleShadowMap, val lightIndex: Int)
