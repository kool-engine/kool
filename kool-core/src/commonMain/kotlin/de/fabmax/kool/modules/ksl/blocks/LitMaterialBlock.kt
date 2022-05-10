package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.modules.ksl.lang.KslBlock
import de.fabmax.kool.modules.ksl.lang.KslScalarArrayExpression
import de.fabmax.kool.modules.ksl.lang.KslScopeBuilder
import de.fabmax.kool.modules.ksl.lang.KslTypeFloat1

abstract class LitMaterialBlock(name: String, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
    val inCamPos = inFloat3("inCamPos")
    val inNormal = inFloat3("inNormal")
    val inFragmentPos = inFloat3("inFragmentPos")
    val inBaseColor = inFloat3("inBaseColor")

    val inLightCount = inInt1("inLightCount")
    val inEncodedLightPositions = inFloat4Array("inEncodedLightPositions")
    val inEncodedLightDirections = inFloat4Array("inEncodedLightDirections")
    val inEncodedLightColors = inFloat4Array("inEncodedLightColors")

    var inShadowFactors = inFloat1Array("inShadowFactors")

    val outColor = outFloat3("outColor")

    fun setLightData(lightData: SceneLightData, shadowFactors: KslScalarArrayExpression<KslTypeFloat1>) {
        inShadowFactors(shadowFactors)
        inLightCount(lightData.lightCount)
        inEncodedLightPositions(lightData.encodedPositions)
        inEncodedLightDirections(lightData.encodedDirections)
        inEncodedLightColors(lightData.encodedColors)
    }
}