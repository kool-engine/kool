package de.fabmax.kool.platform

/**
 * Since kotlin 1.1.0 java.lang.Math doesn't work in javascript anymore, so we need to provide it by platform...
 *
 * @author fabmax
 */
class Math private constructor() {
    companion object {
        val impl = Platform.getMathImpl()

        const val PI = 3.1415926535897932
        const val E = 2.7182818284590452
        const val DEG_2_RAD = PI / 180.0
        const val RAD_2_DEG = 180.0 / PI

        fun toDeg(rad: Float) = rad * RAD_2_DEG.toFloat()
        fun toRad(deg: Float) = deg * DEG_2_RAD.toFloat()

        fun isEqual(a: Float, b: Float) = isZero(a - b)
        fun isEqual(a: Double, b: Double) = isZero(a - b)

        fun isZero(value: Float) = abs(value.toDouble()) < 1e-5
        fun isZero(value: Double) = abs(value) < 1e-10

        fun sign(f: Float) = if (f < 0) -1 else 1
        fun sign(d: Double) = if (d < 0) -1 else 1

        fun clamp(value: Int, min: Int = 0, max: Int = 1): Int {
            if (value < min) {
                return min
            } else if (value > max) {
                return max
            } else {
                return value
            }
        }

        fun clamp(value: Float, min: Float = 0f, max: Float = 1f): Float {
            if (value < min) {
                return min
            } else if (value > max) {
                return max
            } else {
                return value
            }
        }

        fun clamp(value: Double, min: Double = 0.0, max: Double = 1.0): Double {
            if (value < min) {
                return min
            } else if (value > max) {
                return max
            } else {
                return value
            }
        }

        fun random(): Double = impl.random()
        fun abs(value: Double): Double = impl.abs(value)
        fun abs(value: Float): Float = impl.abs(value)
        fun acos(value: Double): Double = impl.acos(value)
        fun acos(value: Float): Float = impl.acos(value)
        fun asin(value: Double): Double = impl.asin(value)
        fun asin(value: Float): Float = impl.asin(value)
        fun atan(value: Double): Double = impl.atan(value)
        fun atan(value: Float): Float = impl.atan(value)
        fun atan2(y: Double, x: Double): Double = impl.atan2(y, x)
        fun atan2(y: Float, x: Float): Float = impl.atan2(y, x)
        fun cos(value: Double): Double = impl.cos(value)
        fun cos(value: Float): Float = impl.cos(value)
        fun cosh(value: Double): Double = impl.cosh(value)
        fun cosh(value: Float): Float = impl.cosh(value)
        fun sin(value: Double): Double = impl.sin(value)
        fun sin(value: Float): Float = impl.sin(value)
        fun sinh(value: Double): Double = impl.sinh(value)
        fun sinh(value: Float): Float = impl.sinh(value)
        fun exp(value: Double): Double = impl.exp(value)
        fun exp(value: Float): Float = impl.exp(value)
        fun sqrt(value: Double): Double = impl.sqrt(value)
        fun sqrt(value: Float): Float = impl.sqrt(value)
        fun tan(value: Double): Double = impl.tan(value)
        fun tan(value: Float): Float = impl.tan(value)
        fun tanh(value: Double): Double = impl.tanh(value)
        fun tanh(value: Float): Float = impl.tanh(value)
        fun log(value: Double): Double = impl.log(value)
        fun log(value: Float): Float = impl.log(value)
        fun pow(base: Double, exp: Double): Double = impl.pow(base, exp)
        fun pow(base: Float, exp: Float): Float = impl.pow(base, exp)
        fun max(a: Int, b: Int): Int = impl.max(a, b)
        fun max(a: Float, b: Float): Float = impl.max(a, b)
        fun max(a: Double, b: Double): Double = impl.max(a, b)
        fun min(a: Int, b: Int): Int = impl.min(a, b)
        fun min(a: Float, b: Float): Float = impl.min(a, b)
        fun min(a: Double, b: Double): Double = impl.min(a, b)
        fun round(value: Double): Int = impl.round(value)
        fun round(value: Float): Int = impl.round(value)
        fun floor(value: Double): Int = impl.floor(value)
        fun floor(value: Float): Int = impl.floor(value)
        fun ceil(value: Double): Int = impl.ceil(value)
        fun ceil(value: Float): Int = impl.ceil(value)
    }

    interface Api {
        fun random(): Double
        fun abs(value: Double): Double
        fun abs(value: Float): Float = abs(value.toDouble()).toFloat()
        fun acos(value: Double): Double
        fun acos(value: Float): Float = acos(value.toDouble()).toFloat()
        fun asin(value: Double): Double
        fun asin(value: Float): Float = asin(value.toDouble()).toFloat()
        fun atan(value: Double): Double
        fun atan(value: Float): Float = atan(value.toDouble()).toFloat()
        fun atan2(y: Double, x: Double): Double
        fun atan2(y: Float, x: Float): Float = atan2(y.toDouble(), x.toDouble()).toFloat()
        fun cos(value: Double): Double
        fun cos(value: Float): Float = cos(value.toDouble()).toFloat()
        fun cosh(value: Double): Double
        fun cosh(value: Float): Float = cosh(value.toDouble()).toFloat()
        fun sin(value: Double): Double
        fun sin(value: Float): Float = sin(value.toDouble()).toFloat()
        fun sinh(value: Double): Double
        fun sinh(value: Float): Float = sinh(value.toDouble()).toFloat()
        fun exp(value: Double): Double
        fun exp(value: Float): Float = exp(value.toDouble()).toFloat()
        fun sqrt(value: Double): Double
        fun sqrt(value: Float): Float = sqrt(value.toDouble()).toFloat()
        fun tan(value: Double): Double
        fun tan(value: Float): Float = tan(value.toDouble()).toFloat()
        fun tanh(value: Double): Double
        fun tanh(value: Float): Float = tanh(value.toDouble()).toFloat()
        fun log(value: Double): Double
        fun log(value: Float): Float = log(value.toDouble()).toFloat()
        fun pow(base: Double, exp: Double): Double
        fun pow(base: Float, exp: Float): Float = pow(base.toDouble(), exp.toDouble()).toFloat()
        fun max(a: Int, b: Int): Int
        fun max(a: Float, b: Float): Float
        fun max(a: Double, b: Double): Double
        fun min(a: Int, b: Int): Int
        fun min(a: Float, b: Float): Float
        fun min(a: Double, b: Double): Double
        fun round(value: Double): Int
        fun round(value: Float): Int
        fun floor(value: Double): Int
        fun floor(value: Float): Int
        fun ceil(value: Double): Int
        fun ceil(value: Float): Int
    }
}
