package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.scene.Light

class GetLightRadiance(parentScope: KslScopeBuilder, isFiniteSoi: Boolean) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage) {

    init {
        val fragPos = paramFloat3("fragPos")
        val encLightPos = paramFloat4("encLightPos")
        val encLightDir = paramFloat4("encLightDir")
        val encLightColor = paramFloat4("encLightColor")
        val lightRadius = paramFloat1("lightRadius")

        body {
            val radiance = float3Var()
            `if` (encLightPos.w eq Light.Directional.ENCODING.const) {
                radiance set encLightColor.rgb

            }.`else` {
                // spot or point light
                val lightToFrag by fragPos - encLightPos.xyz
                val dist by length(lightToFrag)
                val strength by 1f.const / (dist * dist + 1f.const)
                if (isFiniteSoi) {
                    strength *= clamp((lightRadius - dist) / lightRadius, 0f.const, 1f.const)
                }

                `if`(encLightPos.w eq Light.Point.ENCODING.const) {
                    radiance set encLightColor.rgb * strength
                }.`else` {
                    // spot light
                    val lightDirToFrag by lightToFrag / dist
                    val outerAngle by encLightDir.w
                    val innerFac by encLightColor.w
                    val innerAngle by outerAngle + (1f.const - outerAngle) * (1f.const - innerFac)
                    val angle by dot(lightDirToFrag, encLightDir.xyz)
                    val angleStrength by 1f.const - smoothStep(innerAngle, outerAngle, angle)
                    radiance set encLightColor.rgb * strength * angleStrength
                }
            }
            return@body radiance
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
    val func = parentStage.getOrCreateFunction(GetLightRadiance.FUNC_NAME) { GetLightRadiance(this, false) }
    return func(fragPos, encodedLightPos, encodedLightDir, encodedLightColor, 0f.const)
}

fun KslScopeBuilder.getLightRadianceFiniteSoi(
    fragPos: KslExprFloat3,
    encodedLightPos: KslExprFloat4,
    encodedLightDir: KslExprFloat4,
    encodedLightColor: KslExprFloat4,
    lightRadius: KslExprFloat1
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction(GetLightRadiance.FUNC_NAME) { GetLightRadiance(this, true) }
    return func(fragPos, encodedLightPos, encodedLightDir, encodedLightColor, lightRadius)
}
