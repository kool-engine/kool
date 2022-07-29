package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Light

class GetLightRadiance(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val fragPos = paramFloat3("fragPos")
        val encLightPos = paramFloat4("encLightPos")
        val encLightDir = paramFloat4("encLightDir")
        val encLightColor = paramFloat4("encLightColor")

        body.apply {
            `if` (encLightPos.w eq Light.Type.DIRECTIONAL.encoded.const) {
                `return`(encLightColor.rgb)

            }.`else` {
                // spot or point light
                val dist = float1Var(length(fragPos - encLightPos.xyz))
                val strength = float1Var(1f.const / (dist * dist + 1f.const))
                `if`(encLightPos.w eq Light.Type.POINT.encoded.const) {
                    `return`(encLightColor.rgb * strength)

                }.`else` {
                    // spot light
                    val lightDirToFrag = float3Var((fragPos - encLightPos.xyz) / dist)
                    val outerAngle = encLightDir.w
                    val innerFac = encLightColor.w
                    val innerAngle = float1Var(outerAngle + (1f.const - outerAngle) * (1f.const - innerFac))
                    val angle = float1Var(dot(lightDirToFrag, encLightDir.xyz))
                    val angleStrength = 1f.const - smoothStep(innerAngle, outerAngle, angle)
                    `return`(encLightColor.rgb * strength * angleStrength)
                }
            }
        }
    }

    companion object {
        const val FUNC_NAME = "getLightRadiance"
    }
}

fun KslScopeBuilder.getLightRadiance(
    fragPos: KslExprFloat3,
    encodedLightPos: KslExprFloat4,
    encodedLightDir: KslExprFloat4,
    encodedLightColor: KslExprFloat4
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(GetLightRadiance.FUNC_NAME) { GetLightRadiance(this) }
    return KslInvokeFunctionVector(func, this, KslTypeFloat3, fragPos, encodedLightPos, encodedLightDir, encodedLightColor)
}
