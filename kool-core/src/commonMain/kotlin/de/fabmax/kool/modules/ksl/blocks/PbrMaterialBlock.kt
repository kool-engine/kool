package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import kotlin.math.PI

fun KslScopeBuilder.pbrMaterialBlock(reflectionMap: KslExpression<KslTypeColorSamplerCube>,
                                     brdfLut: KslExpression<KslTypeColorSampler2d>,
                                     block: PbrMaterialBlock.() -> Unit): PbrMaterialBlock {
    val pbrMaterialBlock = PbrMaterialBlock(parentStage.program.nextName("pbrMaterialBlock"), reflectionMap, brdfLut, this)
    ops += pbrMaterialBlock.apply(block)
    return pbrMaterialBlock
}

class PbrMaterialBlock(
    name: String,
    reflectionMap: KslExpression<KslTypeColorSamplerCube>,
    brdfLut: KslExpression<KslTypeColorSampler2d>,
    parentScope: KslScopeBuilder
) : LitMaterialBlock(name, parentScope) {

    val inRoughness = inFloat1("inRoughness")
    val inMetallic = inFloat1("inRoughness")

    val inOcclusion = inFloat1("inOcclusion", KslValueFloat1(1f))
    val inReflectionStrength = inFloat1("inReflectionStrength", KslValueFloat1(1f))

    val inAmbientStrength = inFloat3("inAmbientStrength", KslValueFloat3(1f, 1f, 1f))
    val inIrradiance = inFloat3("inIrradiance")
    val inAmbientOrientation = inMat3("inAmbientOrientation")

    init {
        body.apply {
            val viewDir = float3Var(normalize(inCamPos - inFragmentPos))
            val roughness = floatVar(clamp(inRoughness, 0.05f.const, 1f.const))
            val f0 = mix(Vec3f(0.04f).const, inBaseColor, inMetallic)
            val lo = float3Var(Vec3f.ZERO.const)

            fori (0.const, inLightCount) { i ->
                val lightDir = float3Var(normalize(getLightDirectionFromFragPos(inFragmentPos, inEncodedLightPositions[i])))
                val h = float3Var(normalize(viewDir + lightDir))
                val normalDotLight = floatVar(dot(inNormal, lightDir))
                val radiance = float3Var(inShadowFactors[i] * inLightStrength *
                        getLightRadiance(inFragmentPos, inEncodedLightPositions[i], inEncodedLightDirections[i], inEncodedLightColors[i]))

                // cook-torrance BRDF
                val ndf = floatVar(distributionGgx(inNormal, h, roughness))
                val g = floatVar(geometrySmith(inNormal, viewDir, lightDir, roughness))
                val f = float3Var(fresnelSchlick(max(dot(h, viewDir), 0f.const), f0))

                val kD = float3Var(1f.const - f)
                kD set kD * 1f.const - inMetallic

                val num = ndf * g * f
                val denom = 4f.const * max(dot(inNormal, viewDir), 0f.const) * max(normalDotLight, 0f.const)
                val specular = float3Var(num / max(denom, 0.001f.const))

                // add to outgoing radiance
                lo set lo + (kD * inBaseColor / PI.const + specular) * radiance * max(normalDotLight, 0f.const)
            }

            val normalDotView = floatVar(max(dot(inNormal, viewDir), 0f.const))

            // simple version without ibl
//            val kS = float3Var(fresnelSchlickRoughness(normalDotView, f0, roughness))
//            val kD = float3Var(1f.const - kS)
//            val diffuse = float3Var(Color.DARK_GRAY.toLinear().const.rgb * inBaseColor)
//            val ambient = float3Var(kD * diffuse)
//            outColor set ambient + lo

            val f = float3Var(fresnelSchlickRoughness(normalDotView, f0, roughness))
            val kD = float3Var((1f.const - f) * (1f.const - inMetallic))
            val diffuse = float3Var(inIrradiance * inBaseColor)

            val r = inAmbientOrientation * reflect(-viewDir, inNormal)
            val prefilteredColor = float3Var(sampleTexture(reflectionMap, r, roughness * 6f.const).rgb * inAmbientStrength)

            val brdf = float2Var(sampleTexture(brdfLut, float2Value(normalDotView, roughness)).float2("rg"))
            val specular = float3Var(prefilteredColor * (f * brdf.r + brdf.g))
            val ambient = float3Var(kD * diffuse * inOcclusion)
            val reflection = float3Var(specular * inOcclusion * inReflectionStrength)
            outColor set ambient + lo + reflection

            //outColor set specular
        }
    }


    /*
    var orientedR = ""
        inReflectionMapOrientation?.let {
            orientedR = "R = $inReflectionMapOrientation * R;"
        }
        generator.appendMain("""
            vec3 F = fresnelSchlickRoughness(max(dot(N, V), 0.0), F0, rough);
            vec3 kS = F;
            vec3 kD = 1.0 - kS;
            kD *= 1.0 - metal;
            vec3 diffuse = ${inIrradiance.ref3f()} * albedo;

            // sample reflection map
            vec3 R = reflect(-V, N);
            $orientedR
            const float MAX_REFLECTION_LOD = 6.0;
            vec3 prefilteredColor = ${generator.sampleTextureCube(reflectionMap.name, "R", "rough * MAX_REFLECTION_LOD")}.rgb;
            prefilteredColor = mix(prefilteredColor, clamp(${inReflectionColor.ref3f()}, 0.0, 5.0), ${inReflectionWeight.ref1f()});

            vec2 brdfUv = vec2(max(dot(N, V), 0.0), rough);
            vec2 envBRDF = ${generator.sampleTexture2d(brdfLut.name, "brdfUv")}.rg;
            vec3 specular = prefilteredColor * (F * envBRDF.x + envBRDF.y);
            vec3 ambient = (kD * diffuse) * ${inAmbientOccl.ref1f()};
            vec3 reflection = specular * ${inAmbientOccl.ref1f()} * ${inReflectionStrength.ref1f()};
            vec3 color = (ambient + Lo + ${inEmissive.ref3f()}) * ${inAlbedo.ref4f()}.a + reflection;
            ${outColor.declare()} = vec4(color, ${inAlbedo.ref4f()}.a);;
     */
}
