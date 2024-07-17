package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.Color

sealed class ColorSpaceConversion {
    data object AsIs : ColorSpaceConversion()
    data class SrgbToLinear(val gamma: Float = Color.GAMMA_sRGB_TO_LINEAR) : ColorSpaceConversion()
    data class LinearToSrgb(val gamma: Float = Color.GAMMA_LINEAR_TO_sRGB) : ColorSpaceConversion()
    data class LinearToSrgbHdr(
        val toneMapping: ToneMapping = ToneMapping.AcesApproximated,
        val exposure: Float = toneMapping.defaultExposure,
        val gamma: Float = toneMapping.defaultGamma
    ) : ColorSpaceConversion()
}

enum class ToneMapping(val defaultExposure: Float = 1f, val defaultGamma: Float = Color.GAMMA_LINEAR_TO_sRGB) {
    Aces(defaultGamma = 1f/2.4f),
    AcesApproximated(defaultGamma = 1f/2.4f),
    KhronosPbrNeutral,
    ReinhardJodie,
    Uncharted2
}

/**
 * Khronos PBR Neutral Tone Mapper
 * https://github.com/KhronosGroup/ToneMapping/tree/main/PBR_Neutral
 */
class ToneMapKhronosPbrNeutral(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage)
{
    init {
        val inColor = paramFloat3("color")
        body {
            val startCompression = 0.8f - 0.04f
            val desaturation = 0.15f

            val color = float3Var(inColor)
            val x = float1Var(min(color.r, min(color.g, color.b)))
            val offset = float1Var(0.04f.const)
            `if`(x lt 0.08f.const) {
                offset set x - 6.25f.const * x * x
            }
            color set color - offset

            val peak = float1Var(max(color.r, max(color.g, color.b)))
            `if`(peak lt startCompression.const) {
                `return`(color)
            }

            val d = (1f - startCompression).const
            val newPeak = float1Var(1f.const - d * d / (peak + d - startCompression.const))
            color set color * newPeak / peak

            val g = float1Var(1f.const - 1f.const / (desaturation.const * (peak - newPeak) + 1f.const))
            return@body mix(color, Vec3f.ONES.const * newPeak, g)
        }
    }

    companion object {
        const val FUNC_NAME = "toneMapKhronosPbrNeutral"
    }
}

/**
 * ACES Filmic tone mapping curve
 * https://github.com/TheRealMJP/BakingLab/blob/master/BakingLab/ACES.hlsl
 */
class ToneMapAces(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage)
{
    init {
        val c = paramFloat3("color")
        body {
            val acesInputMat = mat3Value(
                Vec3f(0.59719f, 0.07600f, 0.02840f).const,
                Vec3f(0.35458f, 0.90834f, 0.13383f).const,
                Vec3f(0.04823f, 0.01566f, 0.83777f).const
            )
            val acesOutputMat = mat3Value(
                Vec3f( 1.60475f, -0.10208f, -0.00327f).const,
                Vec3f(-0.53108f,  1.10813f, -0.07276f).const,
                Vec3f(-0.07367f, -0.00605f,  1.07602f).const
            )

            val v = float3Var(acesInputMat * c)
            val a = float3Var(v * (v + 0.0245786f.const) - 0.000090537f.const)
            val b = float3Var(v * (0.983729f.const * v + 0.4329510f.const) + 0.238081f.const)
            v set a / b
            return@body saturate(acesOutputMat * v)
        }
    }

    companion object {
        const val FUNC_NAME = "toneMapAces"
    }
}

/**
 * Approximated ACES Filmic tone mapping curve
 * https://knarkowicz.wordpress.com/2016/01/06/aces-filmic-tone-mapping-curve/
 */
class ToneMapAcesApproximated(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage)
{
    init {
        val color = paramFloat3("color")
        body {
            val x = float3Var(color * 0.55f.const)
            val a = 2.51f.const
            val b = 0.03f.const
            val c = 2.43f.const
            val d = 0.59f.const
            val e = 0.14f.const
            return@body saturate((x * (a * x + b)) / (x * (c * x + d) + e))
        }
    }

    companion object {
        const val FUNC_NAME = "toneMapAcesApproximated"
    }
}

/**
 * Extended Reinhard tone mapping
 * https://64.github.io/tonemapping/#reinhard-jodie
 */
class ToneMapReinhardJodie(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage)
{
    init {
        val c = paramFloat3("color")
        body {
            val l = float1Var(dot(c, float3Value(0.2126f, 0.7152f, 0.0722f)))
            val tc = float3Var(c / (1f.const + c))
            return@body mix(c / (l + 1f.const), tc, tc)
        }
    }

    companion object {
        const val FUNC_NAME = "toneMapReinhardJodie"
    }
}

/**
 * Uncharted 2 tone mapping operator
 * http://filmicworlds.com/blog/filmic-tonemapping-operators/
 */
class ToneMapLinearColorUncharted2(parentScope: KslScopeBuilder) :
    KslFunction<KslFloat3>(FUNC_NAME, KslFloat3, parentScope.parentStage)
{
    init {
        val linearColor = paramFloat3("linearColor")

        body {
            val a = A.const
            val b = B.const
            val c = C.const
            val d = D.const
            val e = E.const
            val f = F.const

            val x = float3Var(linearColor * exposureBias.const)
            val curr = float3Var(((x * (a*x + c*b) + d*e) / (x * (a*x + b) + d*f)) - e/f)
            return@body curr * whiteScale.const
        }
    }

    companion object {
        const val FUNC_NAME = "toneMapLinearColorUncharted2"

        private const val A = 0.15f     // shoulder strength
        private const val B = 0.50f     // linear strength
        private const val C = 0.10f     // linear angle
        private const val D = 0.20f     // toe strength
        private const val E = 0.02f     // toe numerator
        private const val F = 0.30f     // toe denominator  --> E/F = toe angle

        private fun toneMapFunc(x: Float): Float {
            return ((x * (A*x + C*B) + D*E) / (x * (A*x + B) + D*F)) - E/F
        }

        private const val W = 11.2f     // linear white point value
        private val whiteScale = Vec3f(1f / toneMapFunc(W))

        private const val exposureBias = 2f
    }
}

fun KslScopeBuilder.convertColorSpace(inputColor: KslExprFloat3, conversion: ColorSpaceConversion): KslVectorExpression<KslFloat3, KslFloat1> =
    when(conversion) {
        ColorSpaceConversion.AsIs -> inputColor
        is ColorSpaceConversion.SrgbToLinear -> pow(inputColor, Vec3f(conversion.gamma).const)
        is ColorSpaceConversion.LinearToSrgb -> pow(inputColor, Vec3f(conversion.gamma).const)
        is ColorSpaceConversion.LinearToSrgbHdr -> {
            val func = when (conversion.toneMapping) {
                ToneMapping.Aces -> parentStage.getOrCreateFunction(ToneMapAces.FUNC_NAME) { ToneMapAces(this) }
                ToneMapping.AcesApproximated -> parentStage.getOrCreateFunction(ToneMapAcesApproximated.FUNC_NAME) { ToneMapAcesApproximated(this) }
                ToneMapping.KhronosPbrNeutral -> parentStage.getOrCreateFunction(ToneMapKhronosPbrNeutral.FUNC_NAME) { ToneMapKhronosPbrNeutral(this) }
                ToneMapping.ReinhardJodie -> parentStage.getOrCreateFunction(ToneMapReinhardJodie.FUNC_NAME) { ToneMapReinhardJodie(this) }
                ToneMapping.Uncharted2 -> parentStage.getOrCreateFunction(ToneMapLinearColorUncharted2.FUNC_NAME) { ToneMapLinearColorUncharted2(this) }
            }
            pow(func(inputColor * conversion.exposure.const), Vec3f(conversion.gamma).const)
        }
    }
