package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.modules.ksl.lang.*

class GetShadowMapFactor(name: String, parentScope: KslScopeBuilder, samplePattern: List<Vec2f>) :
    KslFunction<KslTypeFloat1>(name, KslTypeFloat1, parentScope.parentStage) {

    init {
        val depthMap = paramDepthTex2d("depthMap")
        val positionLightSpace = paramFloat4("positionLightSpace")

        body.apply {
            val sampleStep = float2Var(1f.const / textureSize2d(depthMap).toFloat2())
            val shadowFactor = floatVar(0f.const)
            val projPos = float3Var(positionLightSpace.xyz / positionLightSpace.w)

            if (samplePattern === ShadowConfig.SAMPLE_PATTERN_4x4) {
                // use optimized sampling (corners first)
                for (i in 0..3) {
                    val pos = samplePattern[i]
                    val shadowPos = constFloat3(projPos.x + pos.x.const * sampleStep.x, projPos.y + pos.y.const * sampleStep.y, projPos.z)
                    shadowFactor += sampleDepthTexture(depthMap, shadowPos)
                }
                `if` ((shadowFactor gt 0.01f.const) and (shadowFactor lt 3.99f.const)) {
                    for (i in 4..15) {
                        val pos = samplePattern[i]
                        val shadowPos = constFloat3(projPos.x + pos.x.const * sampleStep.x, projPos.y + pos.y.const * sampleStep.y, projPos.z)
                        shadowFactor += sampleDepthTexture(depthMap, shadowPos)
                    }
                    shadowFactor *= (1f / 16f).const
                }.`else` {
                    shadowFactor *= (1f / 4f).const
                }

            } else {
                samplePattern.forEach { pos ->
                    val shadowPos = constFloat3(projPos.x + pos.x.const * sampleStep.x, projPos.y + pos.y.const * sampleStep.y, projPos.z)
                    shadowFactor += sampleDepthTexture(depthMap, shadowPos)
                }
                if (samplePattern.size > 1) {
                    shadowFactor *= (1f / samplePattern.size).const
                }
            }
            `return`(shadowFactor)
        }
    }

    companion object {
        const val FUNC_NAME_PREFIX = "getShadowMapFactor"
    }
}

fun KslScopeBuilder.getShadowMapFactor(
    depthMap: KslExpression<KslTypeDepthSampler2d>,
    positionLightSpace: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
    samplePattern: List<Vec2f>
): KslScalarExpression<KslTypeFloat1> {
    val funcName = "${GetShadowMapFactor.FUNC_NAME_PREFIX}_${samplePattern.size}"
    val func = parentStage.getOrCreateFunction(funcName) { GetShadowMapFactor(funcName, this, samplePattern) }
    return KslInvokeFunctionScalar(func, KslTypeFloat1, depthMap, positionLightSpace)
}
