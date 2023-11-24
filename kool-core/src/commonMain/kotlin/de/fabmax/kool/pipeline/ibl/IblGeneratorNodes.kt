package de.fabmax.kool.pipeline.ibl

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import kotlin.math.PI

class Hammersley(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat2>("Hammersley", KslFloat2, parentScope.parentStage) {

    init {
        val i = paramInt1("i")
        val n = paramInt1("n")

        body {
            val bits = uint1Var(i.toUint1())
            bits set ((bits shl 16u.const) or (bits shr 16u.const))
            bits set (((bits and 0x55555555u.const) shl 1u.const) or ((bits and 0xaaaaaaaau.const) shr 1u.const))
            bits set (((bits and 0x33333333u.const) shl 2u.const) or ((bits and 0xccccccccu.const) shr 2u.const))
            bits set (((bits and 0x0f0f0f0fu.const) shl 4u.const) or ((bits and 0xf0f0f0f0u.const) shr 4u.const))
            bits set (((bits and 0x00ff00ffu.const) shl 8u.const) or ((bits and 0xff00ff00u.const) shr 8u.const))
            val radicalInverse = float1Var(bits.toFloat1() * (1f / 0x100000000).const)
            return@body float2Value(i.toFloat1() / n.toFloat1(), radicalInverse)
        }
    }
}

fun KslScopeBuilder.hammersley(
    i: KslExprInt1,
    n: KslExprInt1
): KslExprFloat2 {
    val func = parentStage.getOrCreateFunction("Hammersley") { Hammersley(this) }
    return func(i, n)
}

class ImportanceSampleGgx(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>("ImportanceSampleGgx", KslFloat3, parentScope.parentStage) {

    init {
        val xi = paramFloat2("xi")
        val n = paramFloat3("n")
        val roughness = paramFloat1("roughness")

        body {
            val a = float1Var(roughness * roughness)
            val phi = float1Var(2f.const * PI.const * xi.x)
            val cosTheta = float1Var(sqrt((1f.const - xi.y) / (1f.const + (a * a - 1f.const) * xi.y)))
            val sinTheta = float1Var(sqrt(1f.const - cosTheta * cosTheta))

            // from spherical coordinates to cartesian coordinates
            val h = float3Var(
                float3Value(
                    cos(phi) * sinTheta,
                    sin(phi) * sinTheta,
                    cosTheta
                )
            )

            // from tangent-space vector to world-space sample vector
            val up = float3Var(Vec3f.X_AXIS.const)
            `if`(abs(n.z) lt 0.9999f.const) {
                up set Vec3f.Z_AXIS.const
            }
            val tangent = float3Var(normalize(cross(up, n)))
            val bitangent = cross(n, tangent)

            // sample vector
            return@body normalize(tangent * h.x + bitangent * h.y + n * h.z)
        }
    }
}

fun KslScopeBuilder.importanceSampleGgx(
    xi: KslExprFloat2,
    n: KslExprFloat3,
    roughness: KslExprFloat1
): KslExprFloat3 {
    val func = parentStage.getOrCreateFunction("ImportanceSampleGgx") { ImportanceSampleGgx(this) }
    return func(xi, n, roughness)
}

fun KslShaderStage.environmentMapSampler2d(program: KslProgram, texName: String): KslFunction<KslFloat3> {
    val tex = program.texture2d(texName)
    return functionFloat3("sampleEnv2d") {
        val coord = paramFloat3("coord")
        val mipLevel = paramFloat1("mipLevel")
        body {
            val normalizedCoord = float3Var(normalize(coord))
            val uv = float2Var(float2Value(atan2(normalizedCoord.z, normalizedCoord.x), -asin(normalizedCoord.y)))
            uv set uv * float2Value(0.1591f, 0.3183f) + 0.5f.const

            return@body sampleTexture(tex, uv, mipLevel).rgb
        }
    }
}

fun KslShaderStage.environmentMapSamplerCube(program: KslProgram, texName: String): KslFunction<KslFloat3> {
    val tex = program.textureCube(texName)
    return functionFloat3("sampleEnvCube") {
        val coord = paramFloat3("coord")
        val mipLevel = paramFloat1("mipLevel")
        body {
            return@body sampleTexture(tex, coord, mipLevel).rgb
        }
    }
}
