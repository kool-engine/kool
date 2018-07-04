package de.fabmax.kool.math

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * @author fabmax
 */

fun add(a: Vec2f, b: Vec2f): MutableVec2f = a.add(b, MutableVec2f())
fun add(a: Vec3f, b: Vec3f): MutableVec3f = a.add(b, MutableVec3f())
fun add(a: Vec4f, b: Vec4f): MutableVec4f = a.add(b, MutableVec4f())
fun add(a: Vec2d, b: Vec2d): MutableVec2d = a.add(b, MutableVec2d())
fun add(a: Vec3d, b: Vec3d): MutableVec3d = a.add(b, MutableVec3d())
fun add(a: Vec4d, b: Vec4d): MutableVec4d = a.add(b, MutableVec4d())

fun subtract(a: Vec2f, b: Vec2f): MutableVec2f = a.subtract(b, MutableVec2f())
fun subtract(a: Vec3f, b: Vec3f): MutableVec3f = a.subtract(b, MutableVec3f())
fun subtract(a: Vec4f, b: Vec4f): MutableVec4f = a.subtract(b, MutableVec4f())
fun subtract(a: Vec2d, b: Vec2d): MutableVec2d = a.subtract(b, MutableVec2d())
fun subtract(a: Vec3d, b: Vec3d): MutableVec3d = a.subtract(b, MutableVec3d())
fun subtract(a: Vec4d, b: Vec4d): MutableVec4d = a.subtract(b, MutableVec4d())

fun scale(a: Vec2f, fac: Float): MutableVec2f = a.scale(fac, MutableVec2f())
fun scale(a: Vec3f, fac: Float): MutableVec3f = a.scale(fac, MutableVec3f())
fun scale(a: Vec4f, fac: Float): MutableVec4f = a.scale(fac, MutableVec4f())
fun scale(a: Vec2d, fac: Double): MutableVec2d = a.scale(fac, MutableVec2d())
fun scale(a: Vec3d, fac: Double): MutableVec3d = a.scale(fac, MutableVec3d())
fun scale(a: Vec4d, fac: Double): MutableVec4d = a.scale(fac, MutableVec4d())

fun norm(a: Vec2f): MutableVec2f = a.norm(MutableVec2f())
fun norm(a: Vec3f): MutableVec3f = a.norm(MutableVec3f())
fun norm(a: Vec2d): MutableVec2d = a.norm(MutableVec2d())
fun norm(a: Vec3d): MutableVec3d = a.norm(MutableVec3d())

fun cross(a: Vec3f, b: Vec3f): MutableVec3f = a.cross(b, MutableVec3f())
fun cross(a: Vec3d, b: Vec3d): MutableVec3d = a.cross(b, MutableVec3d())

private val slerpTmpAf = MutableVec4f()
private val slerpTmpBf = MutableVec4f()
private val slerpTmpCf = MutableVec4f()
fun slerp(quatA: Vec4f, quatB: Vec4f, f: Float, result: MutableVec4f): MutableVec4f {
    synchronized(slerpTmpAf) {
        quatA.norm(slerpTmpAf)
        quatB.norm(slerpTmpBf)

        val t = f.clamp(0f, 1f)

        var dot = slerpTmpAf.dot(slerpTmpBf).clamp(-1f, 1f)
        if (dot < 0) {
            slerpTmpAf.scale(-1f)
            dot = -dot
        }

        if (dot > 0.9995f) {
            slerpTmpBf.subtract(slerpTmpAf, result).scale(t).add(slerpTmpAf).norm()
        } else {
            val theta0 = acos(dot)
            val theta = theta0 * t

            slerpTmpAf.scale(-dot, slerpTmpCf).add(slerpTmpBf).norm()

            slerpTmpAf.scale(cos(theta))
            slerpTmpCf.scale(sin(theta))
            result.set(slerpTmpAf).add(slerpTmpCf)
        }
    }
    return result
}

private val slerpTmpAd = MutableVec4d()
private val slerpTmpBd = MutableVec4d()
private val slerpTmpCd = MutableVec4d()
fun slerp(quatA: Vec4d, quatB: Vec4d, f: Double, result: MutableVec4d): MutableVec4d {
    synchronized(slerpTmpAd) {
        quatA.norm(slerpTmpAd)
        quatB.norm(slerpTmpBd)

        val t = f.clamp(0.0, 1.0)

        var dot = slerpTmpAd.dot(slerpTmpBd).clamp(-1.0, 1.0)
        if (dot < 0) {
            slerpTmpAd.scale(-1.0)
            dot = -dot
        }

        if (dot > 0.9999995) {
            slerpTmpBd.subtract(slerpTmpAd, result).scale(t).add(slerpTmpAd).norm()
        } else {
            val theta0 = acos(dot)
            val theta = theta0 * t

            slerpTmpAd.scale(-dot, slerpTmpCd).add(slerpTmpBd).norm()

            slerpTmpAd.scale(cos(theta))
            slerpTmpCd.scale(sin(theta))
            result.set(slerpTmpAd).add(slerpTmpCd)
        }
    }
    return result
}
