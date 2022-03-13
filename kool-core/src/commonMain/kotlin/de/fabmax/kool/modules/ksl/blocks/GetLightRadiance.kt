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
                val dist = floatVar(length(fragPos - encLightPos.xyz))
                val strength = floatVar(1f.const / (dist * dist + 1f.const))
                `if` (encLightPos.w eq Light.Type.POINT.encoded.const) {
                    `return`(encLightColor.rgb * strength)

                }.`else` {
                    // spot light
                    val lightDirToFrag = float3Var((fragPos - encLightPos.xyz) / dist)
                    val outerAngle = encLightDir.w
                    val innerFac = encLightColor.w
                    val innerAngle = floatVar(outerAngle + (1f.const - outerAngle) * (1f.const - innerFac))
                    val angle = floatVar(dot(lightDirToFrag, encLightDir.xyz))
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
    fragPos: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>,
    encodedLightPos: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
    encodedLightDir: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>,
    encodedLightColor: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>
): KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    val func = parentStage.getOrCreateFunction(GetLightRadiance.FUNC_NAME) { GetLightRadiance(this) }
    return KslInvokeFunctionVector(func, KslTypeFloat3, fragPos, encodedLightPos, encodedLightDir, encodedLightColor)
}
