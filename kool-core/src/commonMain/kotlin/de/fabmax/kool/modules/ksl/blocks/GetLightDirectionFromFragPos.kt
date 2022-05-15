package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Light

class GetLightDirectionFromFragPos(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val fragPos = paramFloat3("fragPos")
        val encLightPos = paramFloat4("encLightPos")

        body.apply {
            `if` (encLightPos.w eq Light.Type.DIRECTIONAL.encoded.const) {
                `return`(encLightPos.xyz * -(1f.const))
            }.`else` {
                `return`(encLightPos.xyz - fragPos)
            }
        }
    }

    companion object {
        const val FUNC_NAME = "getLightDirectionFromFragPos"
    }
}

fun KslScopeBuilder.getLightDirectionFromFragPos(
    fragPos: KslExprFloat3,
    encodedLightPos: KslExprFloat4
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(GetLightDirectionFromFragPos.FUNC_NAME) { GetLightDirectionFromFragPos(this) }
    return KslInvokeFunctionVector(func, this, KslTypeFloat3, fragPos, encodedLightPos)
}
