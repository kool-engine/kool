package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import kotlin.math.PI

fun KslScopeBuilder.pbrMaterialBlock(reflectionMaps: KslArrayExpression<KslTypeColorSamplerCube>,
                                     brdfLut: KslExpression<KslTypeColorSampler2d>,
                                     block: PbrMaterialBlock.() -> Unit): PbrMaterialBlock {
    val pbrMaterialBlock = PbrMaterialBlock(parentStage.program.nextName("pbrMaterialBlock"), reflectionMaps, brdfLut, this)
    ops += pbrMaterialBlock.apply(block)
    return pbrMaterialBlock
}

class PbrMaterialBlock(
    name: String,
    reflectionMaps: KslArrayExpression<KslTypeColorSamplerCube>,
    brdfLut: KslExpression<KslTypeColorSampler2d>,
    parentScope: KslScopeBuilder
) : LitMaterialBlock(name, parentScope) {

    val inRoughness = inFloat1("inRoughness", KslValueFloat1(0.5f))
    val inMetallic = inFloat1("inMetallic", KslValueFloat1(0f))

    val inReflectionMapWeights = inFloat2("inReflectionMapWeights", KslValueFloat2(1f, 0f))
    val inReflectionStrength = inFloat3("inReflectionStrength", KslValueFloat3(1f, 1f, 1f))

    val inAmbientOrientation = inMat3("inAmbientOrientation")
    val inIrradiance = inFloat3("inIrradiance")
    val inAoFactor = inFloat1("inAoFactor", KslValueFloat1(1f))

    init {
        body.apply {
            val viewDir = float3Var(normalize(inCamPos - inFragmentPos))
            val roughness = float1Var(clamp(inRoughness, 0.05f.const, 1f.const))
            val f0 = mix(Vec3f(0.04f).const, inBaseColor, inMetallic)
            val lo = float3Var(Vec3f.ZERO.const)

            fori(0.const, inLightCount) { i ->
                val lightDir =
                    float3Var(normalize(getLightDirectionFromFragPos(inFragmentPos, inEncodedLightPositions[i])))
                val h = float3Var(normalize(viewDir + lightDir))
                val normalDotLight = float1Var(dot(inNormal, lightDir))
                val radiance = float3Var(inShadowFactors[i] * inLightStrength *
                        getLightRadiance(inFragmentPos, inEncodedLightPositions[i], inEncodedLightDirections[i], inEncodedLightColors[i]))

                // cook-torrance BRDF
                val ndf = float1Var(distributionGgx(inNormal, h, roughness))
                val g = float1Var(geometrySmith(inNormal, viewDir, lightDir, roughness))
                val f = float3Var(fresnelSchlick(max(dot(h, viewDir), 0f.const), f0))

                val kD = float3Var(1f.const - f) * (1f.const - inMetallic)

                val nDotL = float1Var(max(normalDotLight, 0f.const))
                val num = ndf * g * f
                val denom = 4f.const * max(dot(inNormal, viewDir), 0f.const) * nDotL
                val specular = float3Var(num / max(denom, 0.001f.const))

                // add to outgoing radiance
                lo += (kD * inBaseColor / PI.const + specular) * radiance * nDotL
            }

            // image based (ambient) lighting and reflection
            val normalDotView = float1Var(max(dot(inNormal, viewDir), 0f.const))
            val f = float3Var(fresnelSchlickRoughness(normalDotView, f0, roughness))
            val kD = float3Var((1f.const - f) * (1f.const - inMetallic))
            val diffuse = float3Var(inIrradiance * inBaseColor)

            val r = inAmbientOrientation * reflect(-viewDir, inNormal)
            val prefilteredColor = float3Var(sampleTexture(reflectionMaps[0], r, roughness * 6f.const).rgb * inReflectionMapWeights.x)
            `if` (inReflectionMapWeights.y gt 0f.const) {
                prefilteredColor += sampleTexture(reflectionMaps[1], r, roughness * 6f.const).rgb * inReflectionMapWeights.y
            }
            prefilteredColor set prefilteredColor * inReflectionStrength

            val brdf = float2Var(sampleTexture(brdfLut, float2Value(normalDotView, roughness)).float2("rg"))
            val specular = float3Var(prefilteredColor * (f * brdf.r + brdf.g))
            val ambient = float3Var(kD * diffuse * inAoFactor)
            val reflection = float3Var(specular * inAoFactor)
            outColor set ambient + lo + reflection
        }
    }
}
