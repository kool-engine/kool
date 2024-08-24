package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*

class GetShadowMapFactor(name: String, parentScope: KslScopeBuilder, samplePattern: List<Vec2f>) :
    KslFunction<KslFloat1>(name, KslFloat1, parentScope.parentStage) {

    init {
        val depthMap = paramDepthTex2d("depthMap")
        val positionLightSpace = paramFloat4("positionLightSpace")

        body {
            val sampleStep = float2Var(1f.const / textureSize2d(depthMap).toFloat2())
            val shadowFactor = float1Var(0f.const)
            val projPos = float3Var(positionLightSpace.xyz / positionLightSpace.w)

            `if`(all(projPos gt Vec3f(0f, 0f, -1f).const) and all(projPos lt Vec3f(1f, 1f, 1f).const)) {
                // projected position is inside shadow map bounds
                samplePattern.forEach { pos ->
                    val shadowCoord = float2Var(projPos.xy + pos.const * sampleStep)
                    shadowFactor += sampleDepthTexture(depthMap, shadowCoord, projPos.z)
                }
                if (samplePattern.size > 1) {
                    shadowFactor *= (1f / samplePattern.size).const
                }
            }.`else` {
                shadowFactor set 1f.const
            }
            return@body shadowFactor
        }
    }

    companion object {
        const val FUNC_NAME_PREFIX = "getShadowMapFactor"
    }
}

fun KslScopeBuilder.getShadowMapFactor(
    depthMap: KslExpression<KslDepthSampler2d>,
    positionLightSpace: KslExprFloat4,
    samplePattern: List<Vec2f>
): KslExprFloat1 {
    val funcName = "${GetShadowMapFactor.FUNC_NAME_PREFIX}_${samplePattern.size}"
    val func = parentStage.getOrCreateFunction(funcName) { GetShadowMapFactor(funcName, this, samplePattern) }
    return func(depthMap, positionLightSpace)
}
