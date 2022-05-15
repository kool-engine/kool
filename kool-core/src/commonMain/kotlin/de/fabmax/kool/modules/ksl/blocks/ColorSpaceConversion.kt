package de.fabmax.kool.modules.ksl.blocks

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.Color

enum class ColorSpaceConversion {
    AS_IS,
    sRGB_TO_LINEAR,
    LINEAR_TO_sRGB,
    LINEAR_TO_sRGB_HDR
}

/**
 * Uncharted 2 tone mapping operator from: http://filmicworlds.com/blog/filmic-tonemapping-operators/
 */
class ToneMapLinearColorUncharted2(parentScope: KslScopeBuilder) :
    KslFunction<KslTypeFloat3>(FUNC_NAME, KslTypeFloat3, parentScope.parentStage) {

    init {
        val linearColor = paramFloat3("linearColor")

        body.apply {
            val a = A.const
            val b = B.const
            val c = C.const
            val d = D.const
            val e = E.const
            val f = F.const

            val x = float3Var(linearColor * exposureBias.const)
            val curr = float3Var(((x * (a*x + c*b) + d*e) / (x * (a*x + b) + d*f)) - e/f)
            `return`(curr * whiteScale.const)
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

fun KslScopeBuilder.convertColorSpace(inputColor: KslExprFloat3, conversion: ColorSpaceConversion):
        KslVectorExpression<KslTypeFloat3, KslTypeFloat1> {
    return when(conversion) {
        ColorSpaceConversion.AS_IS -> inputColor
        ColorSpaceConversion.sRGB_TO_LINEAR -> pow(inputColor, Vec3f(Color.GAMMA_sRGB_TO_LINEAR).const)
        ColorSpaceConversion.LINEAR_TO_sRGB -> pow(inputColor, Vec3f(Color.GAMMA_LINEAR_TO_sRGB).const)
        ColorSpaceConversion.LINEAR_TO_sRGB_HDR -> {
            val func = parentStage.getOrCreateFunction(ToneMapLinearColorUncharted2.FUNC_NAME) { ToneMapLinearColorUncharted2(this) }
            pow(KslInvokeFunctionVector(func, this, KslTypeFloat3, inputColor), Vec3f(Color.GAMMA_LINEAR_TO_sRGB).const)
        }
    }
}
