package physx

import de.fabmax.kool.math.*

external interface PxFoundation

external interface Enum {
    val value: Int
}

class PxQuat {
    var x = 0f
    var y = 0f
    var z = 0f
    var w = 1f
}
fun PxQuat.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x, y, z, w)
fun PxQuat.set(v: Vec4f): PxQuat { x = v.x; y = v.y; z = v.z; w = v.w; return this }
fun Vec4f.toPxQuat(result: PxQuat = PxQuat()) = result.set(this)

class PxTransform {
    var rotation = PxQuat()
    var translation = PxVec3()
}

fun PxTransform.set(mat: Mat4f): PxTransform {
    mat.getRotation(MutableVec4f()).toPxQuat(rotation)
    translation.x = mat[0, 3]
    translation.y = mat[1, 3]
    translation.z = mat[2, 3]
    return this
}
fun Mat4f.toPxTransform(t: PxTransform = PxTransform()) = t.set(this)

fun PxTransform.toMat4f(result: Mat4f): Mat4f {
    result.setRotate(rotation.toVec4f())
    result[0, 3] = translation.x
    result[1, 3] = translation.y
    result[2, 3] = translation.z
    return result
}

class PxVec2 {
    var x = 0f
    var y = 0f
}
fun PxVec2.toVec2f(result: MutableVec2f = MutableVec2f()) = result.set(x, y)
fun PxVec2.set(v: Vec2f): PxVec2 { x = v.x; y = v.y; return this }
fun Vec2f.toPxVec2(result: PxVec2 = PxVec2()) = result.set(this)

class PxVec3 {
    var x = 0f
    var y = 0f
    var z = 0f
}
fun PxVec3.toVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x, y, z)
fun PxVec3.set(v: Vec3f): PxVec3 { x = v.x; y = v.y; z = v.z; return this }
fun Vec3f.toPxVec3(result: PxVec3 = PxVec3()) = result.set(this)

external interface PxVec3Vector {
    fun push_back(v: PxVec3)
    fun get(at: Int): PxVec3
    fun delete()
}

class PxVec4 {
    var x = 0f
    var y = 0f
    var z = 0f
    var w = 0f
}
fun PxVec4.toVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x, y, z, w)
fun PxVec4.set(v: Vec4f): PxVec4 { x = v.x; y = v.y; z = v.z; w = v.w; return this }
fun Vec4f.toPxVec4(result: PxVec4) = result.set(this)
