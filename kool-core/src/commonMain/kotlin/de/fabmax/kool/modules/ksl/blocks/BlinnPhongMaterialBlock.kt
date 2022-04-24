package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*

fun KslScopeBuilder.blinnPhongMaterialBlock(block: BlinnPhongMaterialBlock.() -> Unit): BlinnPhongMaterialBlock {
    val blinnPhongMaterialBlock = BlinnPhongMaterialBlock(parentStage.program.nextName("blinnPhongMaterialBlock"), this)
    ops += blinnPhongMaterialBlock.apply(block)
    return blinnPhongMaterialBlock
}

class BlinnPhongMaterialBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {

    val inCamPos = inFloat3("inCamPos")
    val inNormal = inFloat3("inNormal")
    val inFragmentPos = inFloat3("inFragmentPos")
    val inFragmentColor = inFloat3("inFragmentColor")

    val inAmbientColor = inFloat3("inAmbientColor")
    val inSpecularColor = inFloat3("inSpecularColor")
    val inShininess = inFloat1("inShininess", KslConstFloat1(16f))
    val inSpecularStrength = inFloat1("inSpecularStrength", KslConstFloat1(0f))

    val inLightCount = inInt1("inLightCount")
    val inEncodedLightPositions = inFloat4Array("inEncodedLightPositions")
    val inEncodedLightDirections = inFloat4Array("inEncodedLightDirections")
    val inEncodedLightColors = inFloat4Array("inEncodedLightColors")

    var inShadowFactors = inFloat1Array("inShadowFactors")

    val outColor = outFloat3("outColor")

    init {
        body.apply {
            val ambientColor = float3Var(inFragmentColor * inAmbientColor)
            val diffuseColor = float3Var(Vec3f.ZERO.const)
            val specularColor = float3Var(Vec3f.ZERO.const)

            fori (0.const, inLightCount) { i ->
                val lightDir = float3Var(getLightDirectionFromFragPos(inFragmentPos, inEncodedLightPositions[i]))
                val lightDistance = floatVar(length(lightDir))
                lightDistance *= lightDistance
                lightDir set normalize(lightDir)

                `if` (inShadowFactors[i] gt 0f.const) {
                    val lambertian = floatVar(max(dot(lightDir, inNormal), 0f.const))
                    val specular = floatVar(0f.const)
                    `if` (lambertian gt 0f.const) {
                        val viewDir = float3Var(normalize(inCamPos - inFragmentPos))
                        val halfDir = float3Var(normalize(lightDir + viewDir))
                        val specAngle = floatVar(max(dot(halfDir, inNormal), 0f.const))
                        specular set pow(specAngle, inShininess) * inSpecularStrength
                    }

                    val radiance = float3Var(inShadowFactors[i] *
                            getLightRadiance(inFragmentPos, inEncodedLightPositions[i], inEncodedLightDirections[i], inEncodedLightColors[i]))

                    diffuseColor += inFragmentColor * lambertian * radiance
                    specularColor += inSpecularColor * specular * radiance
                }
            }

            outColor set ambientColor + diffuseColor + specularColor
        }
    }

    fun setLightData(lightData: SceneLightData, shadowFactors: KslScalarArrayExpression<KslTypeFloat1>) {
        inShadowFactors(shadowFactors)
        inLightCount(lightData.lightCount)
        inEncodedLightPositions(lightData.encodedPositions)
        inEncodedLightDirections(lightData.encodedDirections)
        inEncodedLightColors(lightData.encodedColors)
    }
}