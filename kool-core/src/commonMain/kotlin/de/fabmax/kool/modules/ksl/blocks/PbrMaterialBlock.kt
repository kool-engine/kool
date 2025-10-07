package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.pipeline.ibl.ReflectionMapPass
import kotlin.math.PI

fun KslScopeBuilder.pbrMaterialBlock(
    maxNumberOfLights: Int,
    reflectionMaps: List<KslExpression<KslColorSamplerCube>>?,
    brdfLut: KslExpression<KslColorSampler2d>,
    block: PbrMaterialBlock.() -> Unit
): PbrMaterialBlock {
    val pbrMaterialBlock = PbrMaterialBlock(
        maxNumberOfLights,
        parentStage.program.nextName("pbrMaterialBlock"),
        reflectionMaps,
        brdfLut,
        this
    )
    ops += pbrMaterialBlock.apply(block)
    return pbrMaterialBlock
}

class PbrMaterialBlock(
    maxNumberOfLights: Int,
    name: String,
    reflectionMaps: List<KslExpression<KslColorSamplerCube>>?,
    brdfLut: KslExpression<KslColorSampler2d>,
    parentScope: KslScopeBuilder
) : LitMaterialBlock(maxNumberOfLights, name, parentScope) {

    val inRoughness = inFloat1("inRoughness", KslValueFloat1(0.5f))
    val inMetallic = inFloat1("inMetallic", KslValueFloat1(0f))

    // environment reflection map(s)
    val inReflectionMapWeights = inFloat2("inReflectionMapWeights", KslValueFloat2(1f, 0f))
    val inReflectionStrength = inFloat3("inReflectionStrength", KslValueFloat3(1f, 1f, 1f))
    // screen-space reflection
    val inReflectionColor = inFloat3("inReflectionColor", KslValueFloat3(1f, 1f, 1f))
    val inReflectionWeight = inFloat1("inReflectionWeight", KslValueFloat1(0f))

    val inAmbientOrientation = inMat3("inAmbientOrientation")
    val inIrradiance = inFloat3("inIrradiance")
    val inAoFactor = inFloat1("inAoFactor", KslValueFloat1(1f))

    init {
        body.apply {
            val baseColorRgb = inBaseColor.rgb
            val viewDir by normalize(inCamPos - inFragmentPos)
            val roughness by clamp(inRoughness, 0.05f.const, 1f.const)
            val f0 = mix(Vec3f(0.04f).const, baseColorRgb, inMetallic)
            val lo by Vec3f.ZERO.const

            fori(0.const, inLightCount) { i ->
                val light = pbrLightBlock(true) {
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
            if (reflectionMaps != null) {
                // sample reflection map in reflection direction
                val r = inAmbientOrientation * reflect(-viewDir, inNormal)
                val mipLevel by (1f.const - pow(1f.const - roughness, 1.25f.const)) * (ReflectionMapPass.REFLECTION_MIP_LEVELS - 1).toFloat().const
                reflectionColor set sampleTexture(reflectionMaps[0], r, mipLevel).rgb * inReflectionMapWeights.x
                `if` (inReflectionMapWeights.y gt 0f.const) {
                    reflectionColor += sampleTexture(reflectionMaps[1], r, mipLevel).rgb * inReflectionMapWeights.y
                }
            }
            reflectionColor set reflectionColor * inReflectionStrength

            // screen-space reflection
            reflectionColor set mix(reflectionColor, clamp(inReflectionColor, Vec3f.ZERO.const, Vec3f(5f).const), inReflectionWeight)

            val brdf by sampleTexture(brdfLut, float2Value(normalDotView, roughness)).rg
            val specular by reflectionColor * (fAmbient * brdf.r + brdf.g) / inBaseColor.a
            val ambient by kDAmbient * diffuse * inAoFactor
            val reflection by specular * inAoFactor
            outColor set ambient + lo + reflection
        }
    }
}

fun KslScopeBuilder.pbrLightBlock(isInfiniteSoi: Boolean, block: PbrLightBlock.() -> Unit): PbrLightBlock {
    val pbrLightBlock = PbrLightBlock(parentStage.program.nextName("pbrLightBlock"), isInfiniteSoi, this)
    ops += pbrLightBlock.apply(block)
    return pbrLightBlock
}

class PbrLightBlock(name: String, isInfiniteSoi: Boolean, parentScope: KslScopeBuilder) : KslBlock(name, parentScope) {
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

            val nDotL by max(normalDotLight, 0f.const)
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