package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.NormalLightRange
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import kotlin.math.PI

context(builder: KslScopeBuilder)
fun pbrMaterialBlock(
    maxNumberOfLights: Int,
    reflectionMaps: List<KslExpression<KslColorSamplerCube>>,
    brdfLut: KslExpression<KslColorSampler2d>,
    normalLightRange: NormalLightRange,
    block: PbrMaterialBlock.() -> Unit
): PbrMaterialBlock {
    val pbrMaterialBlock = PbrMaterialBlock(
        maxNumberOfLights,
        builder.parentStage.program.nextName("pbrMaterialBlock"),
        reflectionMaps,
        brdfLut,
        normalLightRange,
        builder
    )
    builder.ops += pbrMaterialBlock.apply(block)
    return pbrMaterialBlock
}

class PbrMaterialBlock(
    maxNumberOfLights: Int,
    name: String,
    reflectionMaps: List<KslExpression<KslColorSamplerCube>>,
    brdfLut: KslExpression<KslColorSampler2d>,
    normalLightRange: NormalLightRange,
    parentScope: KslScopeBuilder,
) : LitMaterialBlock(maxNumberOfLights, name, parentScope) {

    val inRoughness = inFloat1("inRoughness", 0f.const)
    val inMetallic = inFloat1("inMetallic", 0f.const)

    // environment reflection map(s)
    val inReflectionMapWeights = inFloat2("inReflectionMapWeights", float2Value(1f, 0f))
    val inReflectionStrength = inFloat3("inReflectionStrength", float3Value(1f, 1f, 1f))

    val inAmbientOrientation = inMat3("inAmbientOrientation")
    val inIrradiance = inFloat3("inIrradiance")
    val inAoFactor = inFloat1("inAoFactor", 1f.const)

    val outSpecular = outFloat3("outSpecular")
    val outSpecularFactor = outFloat3("outSpecularFactor")
    val outAmbient = outFloat3("outAmbient")
    val outLight = outFloat3("outLight")

    init {
        body.apply {
            val baseColorRgb = inBaseColor.rgb
            val viewDir by normalize(inCamPos - inFragmentPos)
            val roughness by clamp(inRoughness, 0.05f.const, 1f.const)
            val f0 = mix(Vec3f(0.04f).const, baseColorRgb, inMetallic)
            val lo by Vec3f.ZERO.const

            fori(0.const, inLightCount) { i ->
                val light = pbrLightBlock(true, normalLightRange) {
                    inViewDir(viewDir)
                    inNormalLight(inNormal)
                    inFragmentPosLight(inFragmentPos)
                    inBaseColorRgb(baseColorRgb)

                    inRoughnessLight(roughness)
                    inMetallicLight(inMetallic)
                    inF0(f0)

                    inEncodedLightPos(inEncodedLightPositions[i])
                    inEncodedLightDir(inEncodedLightDirections[i])
                    inEncodedLightColor(inEncodedLightColors[i])
                    inLightStr(inLightStrength)
                    inShadowFac(inShadowFactors[i])
                }
                lo += light.outRadiance
            }

            // image based (ambient) lighting and reflection
            val normalDotView by max(dot(inNormal, viewDir), 0f.const)
            val fAmbient by fresnelSchlickRoughness(normalDotView, f0, roughness)
            val kDAmbient by (1f.const - fAmbient) * (1f.const - inMetallic)
            val diffuse by inIrradiance * baseColorRgb

            // use irradiance / ambient color as fallback reflection color in case no reflection map is used
            // ambient color is supposed to be uniform in this case because reflection direction is not considered
            val reflectionColor by inIrradiance
            if (reflectionMaps.isNotEmpty()) {
                // sample reflection map in reflection direction
                val r = inAmbientOrientation * reflect(-viewDir, inNormal)
                val mipLevel by (1f.const - pow(1f.const - roughness, 1.25f.const)) * (ReflectionMapPass.REFLECTION_MIP_LEVELS - 1).toFloat().const
                reflectionColor set reflectionMaps[0].sample(r, mipLevel).rgb * inReflectionMapWeights.x
                if (reflectionMaps.size > 1) {
                    `if` (inReflectionMapWeights.y gt 0f.const) {
                        reflectionColor += reflectionMaps[1].sample(r, mipLevel).rgb * inReflectionMapWeights.y
                    }
                }
            }
            reflectionColor set reflectionColor * inReflectionStrength

            val brdf by brdfLut.sample(float2Value(normalDotView, roughness)).rg
            outSpecular set reflectionColor
            outSpecularFactor set (fAmbient * brdf.r + brdf.g)
            outAmbient set kDAmbient * diffuse * inAoFactor
            outLight set lo
            outColor set outAmbient + outLight + outSpecular * outSpecularFactor * inAoFactor / inBaseColor.a
        }
    }
}

context(builder: KslScopeBuilder)
fun pbrLightBlock(
    isInfiniteSoi: Boolean,
    normalLightRange: NormalLightRange,
    block: PbrLightBlock.() -> Unit
): PbrLightBlock {
    val pbrLightBlock = PbrLightBlock(builder.parentStage.program.nextName("pbrLightBlock"), isInfiniteSoi, normalLightRange, builder)
    builder.ops += pbrLightBlock.apply(block)
    return pbrLightBlock
}

class PbrLightBlock(
    name: String,
    isInfiniteSoi: Boolean,
    normalLightRange: NormalLightRange,
    parentScope: KslScopeBuilder,
) : KslBlock(name, parentScope) {
    val inViewDir = inFloat3("inViewDir")
    val inNormalLight = inFloat3("inNormalLight")
    val inFragmentPosLight = inFloat3("inFragmentPosLight")
    val inBaseColorRgb = inFloat3("inBaseColorRgb")

    val inRoughnessLight = inFloat1("inRoughnessLight")
    val inMetallicLight = inFloat1("inMetallicLight")
    val inF0 = inFloat3("inF0")

    val inEncodedLightPos = inFloat4("inEncLightPos")
    val inEncodedLightDir = inFloat4("inEncLightDir")
    val inEncodedLightColor = inFloat4("inEncLightColor")
    val inLightRadius = inFloat1("inNormalLight", KslValueFloat1(0f))
    val inLightStr = inFloat1("inLightStrength")
    val inShadowFac = inFloat1("inShadowFactor")

    val outRadiance = outFloat3("outColor")

    init {
        body.apply {
            val lightDir by normalize(getLightDirectionFromFragPos(inFragmentPosLight, inEncodedLightPos))
            val h by normalize(inViewDir + lightDir)
            val normalDotLight by dot(inNormalLight, lightDir)

            val radiance by inShadowFac * inLightStr * if (isInfiniteSoi) {
                getLightRadiance(
                    inFragmentPosLight,
                    inEncodedLightPos,
                    inEncodedLightDir,
                    inEncodedLightColor
                )
            } else {
                getLightRadianceFiniteSoi(
                    inFragmentPosLight,
                    inEncodedLightPos,
                    inEncodedLightDir,
                    inEncodedLightColor,
                    inLightRadius
                )
            }

            val nDotL by when (normalLightRange) {
                NormalLightRange.ZeroToOne -> max(normalDotLight, 0f.const)
                NormalLightRange.MinusOneToOne -> saturate(normalDotLight * 0.5f.const + 0.5f.const)
            }
            `if`(nDotL gt 0f.const) {
                // cook-torrance BRDF
                val ndf by distributionGgx(inNormalLight, h, inRoughnessLight)
                val g by geometrySmith(inNormalLight, inViewDir, lightDir, inRoughnessLight)
                val f by fresnelSchlick(max(dot(h, inViewDir), 0f.const), inF0)

                val kD by (1f.const - f) * (1f.const - inMetallicLight)

                val num by ndf * g * f
                val denom by 4f.const * max(dot(inNormalLight, inViewDir), 0f.const) * nDotL
                val specular by num / max(denom, 0.001f.const)

                // add to outgoing radiance
                outRadiance set (kD * inBaseColorRgb / PI.const + specular) * radiance * nDotL
            }.`else` {
                outRadiance set Vec3f.ZERO.const
            }
        }
    }
}