package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

fun KslScopeBuilder.blinnPhongMaterialBlock(block: BlinnPhongMaterialBlock.() -> Unit): BlinnPhongMaterialBlock {
    val blinnPhongMaterialBlock = BlinnPhongMaterialBlock(parentStage.program.nextName("blinnPhongMaterialBlock"), this)
    ops += blinnPhongMaterialBlock.apply(block)
    return blinnPhongMaterialBlock
}

class BlinnPhongMaterialBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {

    var inCamPos by inFloat3("inCamPos")
    var inNormal by inFloat3("inNormal")
    var inFragmentPos by inFloat3("inFragmentPos")
    var inFragmentColor by inFloat3("inFragmentColor")

    var inAmbientColor by inFloat3("inAmbientColor")
    var inSpecularColor by inFloat3("inSpecularColor")
    var inShininess by inFloat1("inShininess", KslConstFloat1(16f))
    var inSpecularGain by inFloat1("inSpecularGain", KslConstFloat1(1f))

    var inLightCount by inInt1("inLightCount")
    var inEncodedLightPositions by inFloat4Array("inEncodedLightPositions")
    var inEncodedLightDirections by inFloat4Array("inEncodedLightDirections")
    var inEncodedLightColors by inFloat4Array("inEncodedLightColors")

    var inShadowFactors by inFloat1Array("inShadowFactors")

    val outColor = outFloat3("outColor")

    init {
        body.apply {
            val ambientColor = float3Var(inFragmentColor * inAmbientColor)
            val diffuseColor = float3Var()
            val specularColor = float3Var()

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
                        specular set pow(specAngle, inShininess) * inSpecularGain
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
        inShadowFactors = shadowFactors
        inLightCount = lightData.lightCount
        inEncodedLightPositions = lightData.encodedPositions
        inEncodedLightDirections = lightData.encodedDirections
        inEncodedLightColors = lightData.encodedColors
    }
}