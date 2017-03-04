package de.fabmax.kool.platform

/**
 * Since kotlin 1.1.0 java.lang.Math doesn't work in javascript anymore, so we need to provide it by platform...
 *
 * @author fabmax
 */
class Math private constructor() {
    companion object {
        private val impl = Platform.getMathImpl()

        val PI: Double
            get() = impl.PI

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
        val PI: Double
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