package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.*

abstract class LitMaterialBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val inCamPos = inFloat3("inCamPos")
    val inNormal = inFloat3("inNormal")
    val inFragmentPos = inFloat3("inFragmentPos")
    val inBaseColor = inFloat3("inBaseColor")

    val inLightCount = inInt1("inLightCount")
    val inEncodedLightPositions = inFloat4Array("inEncodedLightPositions")
    val inEncodedLightDirections = inFloat4Array("inEncodedLightDirections")
    val inEncodedLightColors = inFloat4Array("inEncodedLightColors")
    val inLightStrength = inFloat1("inLightStrength")

    var inShadowFactors = inFloat1Array("inShadowFactors")

    val outColor = outFloat3("outColor")

    fun setLightData(
        lightData: SceneLightData,
        shadowFactors: KslExprFloat1Array,
        lightStrength: KslExprFloat1 = KslValueFloat1(1f)
    ) {
        inShadowFactors(shadowFactors)
        inLightCount(lightData.lightCount)
        inEncodedLightPositions(lightData.encodedPositions)
        inEncodedLightDirections(lightData.encodedDirections)
        inEncodedLightColors(lightData.encodedColors)
        inLightStrength(lightStrength)
    }
}