package de.fabmax.kool.platform

/**
 * Since kotlin 1.1.0 java.lang.Math doesn't work in javascript anymore, so we need to provide it by platform...
 *
 * @author fabmax
 */
@Suppress("NOTHING_TO_INLINE")
class Math private constructor() {
    companion object {
        val impl = Platform.getMathImpl()

        const val PI = 3.1415926535897932
        const val E = 2.7182818284590452
        const val DEG_2_RAD = PI / 180.0
        const val RAD_2_DEG = 180.0 / PI

        inline fun toDeg(rad: Float): Float {
            return rad * RAD_2_DEG.toFloat()
        }

        inline fun toRad(deg: Float): Float {
            return deg * DEG_2_RAD.toFloat()
        }

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

        fun isEqual(a: Float, b: Float): Boolean {
            return isZero(a - b)
        }

        fun isEqual(a: Double, b: Double): Boolean {
            return isZero(a - b)
        }

        fun isZero(value: Float): Boolean {
            return abs(value.toDouble()) < 1e-5
        }

        fun isZero(value: Double): Boolean {
            return abs(value) < 1e-10
        }

        fun sign(f: Float): Int {
            return if (f < 0) -1 else 1
        }

        fun sign(d: Double): Int {
            return if (d < 0) -1 else 1
        }

        fun random(): Double = impl.random()
        fun abs(value: Double): Double = impl.abs(value)
        fun acos(value: Double): Double = impl.acos(value)
        fun asin(value: Double): Double = impl.asin(value)
        fun atan(value: Double): Double = impl.atan(value)
        fun atan2(y: Double, x: Double): Double = impl.atan2(y, x)
        fun cos(value: Double): Double = impl.cos(value)
        fun sin(value: Double): Double = impl.sin(value)
        fun exp(value: Double): Double = impl.exp(value)
        fun max(a: Int, b: Int): Int = impl.max(a, b)
        fun max(a: Float, b: Float): Float = impl.max(a, b)
        fun max(a: Double, b: Double): Double = impl.max(a, b)
        fun min(a: Int, b: Int): Int = impl.min(a, b)
        fun min(a: Float, b: Float): Float = impl.min(a, b)
        fun min(a: Double, b: Double): Double = impl.min(a, b)
        fun sqrt(value: Double): Double = impl.sqrt(value)
        fun tan(value: Double): Double = impl.tan(value)
        fun log(value: Double): Double = impl.log(value)
        fun pow(base: Double, exp: Double): Double = impl.pow(base, exp)
        fun round(value: Double): Int = impl.round(value)
        fun round(value: Float): Int = impl.round(value)
        fun floor(value: Double): Int = impl.floor(value)
        fun floor(value: Float): Int = impl.floor(value)
        fun ceil(value: Double): Int = impl.ceil(value)
        fun ceil(value: Float): Int = impl.ceil(value)
    }

    interface Impl {
        fun random(): Double
        fun abs(value: Double): Double
        fun acos(value: Double): Double
        fun asin(value: Double): Double
        fun atan(value: Double): Double
        fun atan2(y: Double, x: Double): Double
        fun cos(value: Double): Double
        fun sin(value: Double): Double
        fun exp(value: Double): Double
        fun max(a: Int, b: Int): Int
        fun max(a: Float, b: Float): Float
        fun max(a: Double, b: Double): Double
        fun min(a: Int, b: Int): Int
        fun min(a: Float, b: Float): Float
        fun min(a: Double, b: Double): Double
        fun sqrt(value: Double): Double
        fun tan(value: Double): Double
        fun log(value: Double): Double
        fun pow(base: Double, exp: Double): Double
        fun round(value: Double): Int
        fun round(value: Float): Int
        fun floor(value: Double): Int
        fun floor(value: Float): Int
        fun ceil(value: Double): Int
        fun ceil(value: Float): Int
    }
}
