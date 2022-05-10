package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.Color
import kotlin.math.PI

fun KslScopeBuilder.pbrMaterialBlock(block: PbrMaterialBlock.() -> Unit): PbrMaterialBlock {
    val pbrMaterialBlock = PbrMaterialBlock(parentStage.program.nextName("pbrMaterialBlock"), this)
    ops += pbrMaterialBlock.apply(block)
    return pbrMaterialBlock
}

class PbrMaterialBlock(name: String, parentScope: KslScopeBuilder) : LitMaterialBlock(name, parentScope) {

    val inRoughness = inFloat1("inRoughness")
    val inMetallic = inFloat1("inRoughness")

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
                val radiance = float3Var(inShadowFactors[i] *
                        getLightRadiance(inFragmentPos, inEncodedLightPositions[i], inEncodedLightDirections[i], inEncodedLightColors[i]))

                // cook-torrance BRDF
                val ndf = floatVar(distributionGgx(inNormal, h, roughness))
                val g = floatVar(geometrySmith(inNormal, viewDir, lightDir, roughness))
                val f = float3Var(fresnelSchlick(max(dot(h, viewDir), 0f.const), f0))

                val kD = float3Var(Vec3f(1f).const - f)
                kD set kD * 1f.const - inMetallic

                val num = ndf * g * f
                val denom = 4f.const * max(dot(inNormal, viewDir), 0f.const) * max(normalDotLight, 0f.const)
                val specular = float3Var(num / max(denom, 0.001f.const))

                // add to outgoing radiance
                lo set lo + (kD * inBaseColor / PI.const + specular) * radiance * max(normalDotLight, 0f.const)
            }

            val kS = float3Var(fresnelSchlickRoughness(max(dot(inNormal, viewDir), 0f.const), f0, roughness))
            val kD = float3Var(1f.const - kS)
            val diffuse = float3Var(Color.DARK_GRAY.toLinear().const.rgb * inBaseColor)
            val ambient = float3Var(kD * diffuse)

            outColor set ambient + lo
        }
    }
}
