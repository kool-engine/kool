package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.LazyMat4d

class Transform {

    val matrix = Mat4d()
    val matrixInverse: Mat4d
        get() = lazyInvMatrix.get()

    private val lazyInvMatrix = LazyMat4d { matrix.invert(it) }
    var isIdentity = true
        private set

    fun markDirty() {
        lazyInvMatrix.isDirty = true
        isIdentity = false
    }

    fun setIdentity(): Transform {
        matrix.setIdentity()
        lazyInvMatrix.setIdentity()
        isIdentity = true
        return this
    }

    fun translate(t: Vec3f) = translate(t.x, t.y, t.z)
    fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())
    fun translate(t: Vec3d) = translate(t.x, t.y, t.z)
    fun translate(tx: Double, ty: Double, tz: Double): Transform {
        matrix.translate(tx, ty, tz)
        markDirty()
        return this
    }

    fun setTranslate(translation: Vec3f) = setTranslate(translation.x, translation.y, translation.z)
    fun setTranslate(x: Float, y: Float, z: Float) = setTranslate(x.toDouble(), y.toDouble(), z.toDouble())
    fun setTranslate(translation: Vec3d) = setTranslate(translation.x, translation.y, translation.z)
    fun setTranslate(x: Double, y: Double, z: Double): Transform {
        matrix.setTranslate(x, y, z)
        markDirty()
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)
    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())
    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)
    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Transform {
        matrix.rotate(angleDeg, axX, axY, axZ)
        markDirty()
        return this
    }

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float) = rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())
    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Transform {
        matrix.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    fun setRotate(angleDeg: Float, axis: Vec3f) = setRotate(angleDeg, axis.x, axis.y, axis.z)
    fun setRotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = setRotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())
    fun setRotate(angleDeg: Double, axis: Vec3d) = setRotate(angleDeg, axis.x, axis.y, axis.z)
    fun setRotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Transform {
        matrix.setRotate(angleDeg, axX, axY, axZ)
        markDirty()
        return this
    }

    fun setRotate(eulerX: Float, eulerY: Float, eulerZ: Float) = setRotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())
    fun setRotate(eulerX: Double, eulerY: Double, eulerZ: Double): Transform {
        matrix.setRotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    fun setRotate(quaternion: Vec4d): Transform {
        matrix.setRotate(quaternion)
        markDirty()
        return this
    }

    fun setRotate(mat3: Mat3f): Transform {
        matrix.setRotate(mat3)
        markDirty()
        return this
    }

    fun setRotate(mat4: Mat4d): Transform {
        matrix.setRotate(mat4)
        markDirty()
        return this
    }

    fun scale(s: Float) = scale(s, s, s)
    fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())
    fun scale(s: Double) = scale(s, s, s)
    fun scale(sx: Double, sy: Double, sz: Double): Transform {
        matrix.scale(sx, sy, sz)
        markDirty()
        return this
    }

    fun setScale(s: Float) = setScale(s, s, s)
    fun setScale(sx: Float, sy: Float, sz: Float) = setScale(sx.toDouble(), sy.toDouble(), sz.toDouble())
    fun setScale(s: Double) = setScale(s, s, s)
    fun setScale(sx: Double, sy: Double, sz: Double): Transform {
        matrix.setIdentity().scale(sx, sy, sz)
        markDirty()
        return this
    }

    fun mul(mat: Mat4d): Transform {
        matrix.mul(mat)
        markDirty()
        return this
    }

    fun set(mat: Mat4f): Transform {
        matrix.set(mat)
        markDirty()
        return this
    }

    fun set(mat: Mat4d): Transform {
        matrix.set(mat)
        markDirty()
        return this
    }

    fun transform(vec: MutableVec3f, w: Float = 1.0f) = matrix.transform(vec, w)
    fun transform(vec: Vec3f, w: Float, result: MutableVec3f) = matrix.transform(vec, w, result)
    fun transform(vec: MutableVec4f) = matrix.transform(vec)
    fun transform(vec: Vec4f, result: MutableVec4f) = matrix.transform(vec, result)

    fun transform(vec: MutableVec3d, w: Double = 1.0) = matrix.transform(vec, w)
    fun transform(vec: Vec3d, w: Double = 1.0, result: MutableVec3d) = matrix.transform(vec, w, result)
    fun transform(vec: MutableVec4d) = matrix.transform(vec)
    fun transform(vec: Vec4d, result: MutableVec4d) = matrix.transform(vec, result)

}
