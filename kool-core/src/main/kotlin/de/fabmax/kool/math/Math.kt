@file:Suppress("NOTHING_TO_INLINE")

package de.fabmax.kool.math

import kotlin.math.PI
import kotlin.math.abs

const val DEG_2_RAD = PI / 180.0
const val RAD_2_DEG = 180.0 / PI

inline fun Float.toDeg() = this * RAD_2_DEG.toFloat()
inline fun Float.toRad() = this * DEG_2_RAD.toFloat()
inline fun Double.toDeg() = this * RAD_2_DEG
inline fun Double.toRad() = this * DEG_2_RAD

inline fun isEqual(a: Float, b: Float) = (a - b).isZero()
inline fun isEqual(a: Double, b: Double) = (a - b).isZero()

inline fun Float.isZero() = abs(this) < 1e-5
inline fun Double.isZero() = abs(this) < 1e-10

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
