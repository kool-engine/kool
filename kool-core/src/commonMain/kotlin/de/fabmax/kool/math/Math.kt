@file:Suppress("NOTHING_TO_INLINE")

package de.fabmax.kool.math

import de.fabmax.kool.util.Time
import kotlin.math.*

const val DEG_2_RAD = PI / 180.0
const val RAD_2_DEG = 180.0 / PI

const val FUZZY_EQ_F = 1e-5f
const val FUZZY_EQ_D = 1e-10

const val PI_F = PI.toFloat()

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

inline fun Int.clamp(min: Int, max: Int): Int {
    require(max >= min) { "max ($max) is smaller than min ($min) " }
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

inline fun Float.clamp(min: Float = 0f, max: Float = 1f): Float {
    require(max >= min) { "max ($max) is smaller than min ($min) " }
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

inline fun Double.clamp(min: Double = 0.0, max: Double = 1.0): Double {
    require(max >= min) { "max ($max) is smaller than min ($min) " }
    return when {
        this < min -> min
        this > max -> max
        else -> this
    }
}

fun Float.expDecay(target: Float, decay: Float, deltaT: Float = Time.deltaT): Float {
    return target + (this - target) * exp(-decay * deltaT)
}

fun Double.expDecay(target: Double, decay: Double, deltaT: Float = Time.deltaT): Double {
    return target + (this - target) * exp(-decay * deltaT)
}

fun MutableVec2f.expDecay(target: Vec2f, decay: Float, deltaT: Float = Time.deltaT): MutableVec2f {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    return this
}

fun MutableVec3f.expDecay(target: Vec3f, decay: Float, deltaT: Float = Time.deltaT): MutableVec3f {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    z = z.expDecay(target.z, decay, deltaT)
    return this
}

fun MutableVec4f.expDecay(target: Vec4f, decay: Float, deltaT: Float = Time.deltaT): MutableVec4f {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    z = z.expDecay(target.z, decay, deltaT)
    w = w.expDecay(target.w, decay, deltaT)
    return this
}

fun MutableVec2d.expDecay(target: Vec2d, decay: Double, deltaT: Float = Time.deltaT): MutableVec2d {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    return this
}

fun MutableVec3d.expDecay(target: Vec3d, decay: Double, deltaT: Float = Time.deltaT): MutableVec3d {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    z = z.expDecay(target.z, decay, deltaT)
    return this
}

fun MutableVec4d.expDecay(target: Vec4d, decay: Double, deltaT: Float = Time.deltaT): MutableVec4d {
    x = x.expDecay(target.x, decay, deltaT)
    y = y.expDecay(target.y, decay, deltaT)
    z = z.expDecay(target.z, decay, deltaT)
    w = w.expDecay(target.w, decay, deltaT)
    return this
}

fun stableAsin(x: Float): Float {
    val asin = asin(x)
    return if (!asin.isNaN()) {
        asin
    } else if (x > 0f) {
        (PI * 0.5).toFloat()
    } else {
        (PI * -0.5).toFloat()
    }
}

fun stableAcos(x: Float): Float {
    val acos = acos(x)
    return if (!acos.isNaN()) {
        acos
    } else if (x > 0f) {
        0f
    } else {
        PI.toFloat()
    }
}

fun Int.wrap(low: Int, high: Int): Int {
    val r = high - low
    var t = (this - low) % r
    if (t < 0) {
        t += r
    }
    return t + low
}

fun Float.wrap(low: Float, high: Float): Float {
    val r = high - low
    var t = (this - low) % r
    if (t < 0) {
        t += r
    }
    return t + low
}

fun Double.wrap(low: Double, high: Double): Double {
    val r = high - low
    var t = (this - low) % r
    if (t < 0) {
        t += r
    }
    return t + low
}

fun smoothStep(low: Float, high: Float, x: Float): Float {
    val nx = ((x - low) / (high - low)).clamp()
    return nx * nx * (3 - 2 * nx)
}

fun triArea(va: Vec3f, vb: Vec3f, vc: Vec3f): Float {
    val xAB = vb.x - va.x
    val yAB = vb.y - va.y
    val zAB = vb.z - va.z
    val xAC = vc.x - va.x
    val yAC = vc.y - va.y
    val zAC = vc.z - va.z
    val abSqr = xAB * xAB + yAB * yAB + zAB * zAB
    val acSqr = xAC * xAC + yAC * yAC + zAC * zAC
    val abcSqr = xAB * xAC + yAB * yAC + zAB * zAC
    return 0.5f * sqrt(abSqr * acSqr - abcSqr * abcSqr)
}

fun triAspectRatio(va: Vec3f, vb: Vec3f, vc: Vec3f): Float {
    val a = va.distance(vb)
    val b = vb.distance(vc)
    val c = vc.distance(va)
    val s = (a + b + c) / 2f
    return abs(a * b * c / (8f * (s - a) * (s - b) * (s - c)))
}

fun barycentricWeights(pt: Vec3f, va: Vec3f, vb: Vec3f, vc: Vec3f, result: MutableVec3f): MutableVec3f {
    val e1 = MutableVec3f(vb).subtract(va)
    val e2 = MutableVec3f(vc).subtract(va)
    val n = e1.cross(e2, MutableVec3f())

    val a = n.length()
    val aa = a * a
    val m = MutableVec3f()

    e1.set(vc).subtract(vb)
    e2.set(pt).subtract(vb)
    result.x = n.dot(e1.cross(e2, m)) / aa

    e1.set(va).subtract(vc)
    e2.set(pt).subtract(vc)
    result.y = n.dot(e1.cross(e2, m)) / aa

    e1.set(vb).subtract(va)
    e2.set(pt).subtract(va)
    result.z = n.dot(e1.cross(e2, m)) / aa

    return result
}

fun numMipLevels(width: Int, height: Int, depth: Int = 1): Int = floor(log2(maxOf(width, height, depth).toDouble())).toInt() + 1

inline fun float32ToFloat16(f32: Float, block: (high: Byte, low: Byte) -> Unit) {
    // from: https://stackoverflow.com/questions/3026441/float32-to-float16
    val f32bits = f32.toBits()
    var f16bits = (f32bits shr 31) shl 5
    var tmp = (f32bits shr 23) and 0xff
    tmp = (tmp - 0x70) and ((((0x70 - tmp) shr 4) shr 27) and 0x1f)
    f16bits = (f16bits or tmp) shl 10
    f16bits = f16bits or ((f32bits shr 13) and 0x3ff)

    val high = (f16bits shr 8).toByte()
    val low = (f16bits and 0xff).toByte()
    block(high, low)
}
