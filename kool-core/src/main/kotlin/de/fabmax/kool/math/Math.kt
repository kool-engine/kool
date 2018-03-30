@file:Suppress("NOTHING_TO_INLINE")

package de.fabmax.kool.math

import kotlin.math.PI
import kotlin.math.abs

const val DEG_2_RAD = PI / 180.0
const val RAD_2_DEG = 180.0 / PI

const val FUZZY_EQ_F = 1e-5f
const val FUZZY_EQ_D = 1e-10

/**
 * The difference between 1 and the smallest floating point number of type float that is greater than 1.
 */
const val FLT_EPSILON = 1.19209290e-7f

/**
 * Square-root of 0.5f
 */
const val SQRT_1_2 = 0.707106781f

inline fun Float.toDeg() = this * RAD_2_DEG.toFloat()
inline fun Float.toRad() = this * DEG_2_RAD.toFloat()
inline fun Double.toDeg() = this * RAD_2_DEG
inline fun Double.toRad() = this * DEG_2_RAD

inline fun isFuzzyEqual(a: Float, b: Float, eps: Float = FUZZY_EQ_F) = (a - b).isFuzzyZero(eps)
inline fun isFuzzyEqual(a: Double, b: Double, eps: Double = FUZZY_EQ_D) = (a - b).isFuzzyZero(eps)

inline fun Float.isFuzzyZero(eps: Float = FUZZY_EQ_F) = abs(this) <= eps
inline fun Double.isFuzzyZero(eps: Double = FUZZY_EQ_D) = abs(this) <= eps

inline fun Int.clamp(min: Int, max: Int): Int = when {
    this < min -> min
    this > max -> max
    else -> this
}

inline fun Float.clamp(min: Float = 0f, max: Float = 1f): Float = when {
    this < min -> min
    this > max -> max
    else -> this
}

inline fun Double.clamp(min: Double = 0.0, max: Double = 1.0): Double = when {
    this < min -> min
    this > max -> max
    else -> this
}
