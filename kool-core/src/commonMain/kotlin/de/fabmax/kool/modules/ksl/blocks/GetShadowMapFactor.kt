package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
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

            `if` (all(projPos gt Vec3f(0f, 0f, -1f).const) and
                    all(projPos lt Vec3f(1f, 1f, 1f).const)) {
                // projected position is inside shadow map bounds
                samplePattern.forEach { pos ->
                    val shadowPos = constFloat3(projPos.x + pos.x.const * sampleStep.x, projPos.y + pos.y.const * sampleStep.y, projPos.z)
                    shadowFactor += sampleDepthTexture(depthMap, shadowPos)
                }
                if (samplePattern.size > 1) {
                    shadowFactor *= (1f / samplePattern.size).const
                }
            }.`else` {
                shadowFactor set 1f.const
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
    return KslInvokeFunctionScalar(func, this, KslTypeFloat1, depthMap, positionLightSpace)
}
