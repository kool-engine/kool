package de.fabmax.kool.math

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

fun Vec3f.xy(): Vec2f = Vec2f(x, y)
fun MutableVec3f.xy(): MutableVec2f = MutableVec2f(x, y)
