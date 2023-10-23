package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.LazyMat4d

abstract class Transform {

    abstract val matrix: Mat4d
    abstract val matrixInverse: Mat4d

    abstract val isIdentity: Boolean

    abstract fun markDirty()

    abstract fun setIdentity(): Transform

    abstract fun getPosition(result: MutableVec3d): MutableVec3d

    abstract fun setPosition(x: Double, y: Double, z: Double): Transform

    abstract fun setPosition(position: Vec3d): Transform

    abstract fun translate(t: Vec3f): Transform
    abstract fun translate(tx: Float, ty: Float, tz: Float): Transform
    abstract fun translate(t: Vec3d): Transform
    abstract fun translate(tx: Double, ty: Double, tz: Double): Transform

    abstract fun rotate(angleDeg: Float, axis: Vec3f): Transform
    abstract fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float): Transform
    abstract fun rotate(angleDeg: Double, axis: Vec3d): Transform
    abstract fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Transform

    abstract fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float): Transform
    abstract fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Transform

    abstract fun scale(s: Float): Transform
    abstract fun scale(sx: Float, sy: Float, sz: Float): Transform
    abstract fun scale(s: Double): Transform
    abstract fun scale(sx: Double, sy: Double, sz: Double): Transform

    open fun transform(vec: MutableVec3f, w: Float = 1f) = matrix.transform(vec, w)
    open fun transform(vec: Vec3f, w: Float, result: MutableVec3f) = matrix.transform(vec, w, result)
    open fun transform(vec: MutableVec4f) = matrix.transform(vec)
    open fun transform(vec: Vec4f, result: MutableVec4f) = matrix.transform(vec, result)

    open fun transform(vec: MutableVec3d, w: Double = 1.0) = matrix.transform(vec, w)
    open fun transform(vec: Vec3d, w: Double, result: MutableVec3d) = matrix.transform(vec, w, result)
    open fun transform(vec: MutableVec4d) = matrix.transform(vec)
    open fun transform(vec: Vec4d, result: MutableVec4d) = matrix.transform(vec, result)

}

/**
 * Translation, rotation, scale based transform implementation. This is the standard transform implementation.
 * In contrast to [MatrixTransform], the TRS components are always relative to the parent coordinate frame and
 * completely independent of one another.
 *
 * The final transform matrix is computed equivalently to this:
 * ```
 *     val matrix = Mat4d()
 *         .translate(translation)
 *         .rotate(rotation)
 *         .scale(scale)
 * ```
 */
class TrsTransform : Transform() {

    val translation = MutableVec3d()
    val rotation = Mat3d()
    val scale = MutableVec3d(Vec3d.ONES)

    private val lazyMatrix = LazyMat4d {
        it.setIdentity()
            .setRotate(rotation)
            .setOrigin(translation)
            .scale(scale)
    }
    private val lazyInvMatrix = LazyMat4d { matrix.invert(it) }

    override val matrix: Mat4d
        get() = lazyMatrix.get()
    override val matrixInverse: Mat4d
        get() = lazyInvMatrix.get()

    override var isIdentity = true
        private set

    override fun markDirty() {
        lazyMatrix.isDirty = true
        lazyInvMatrix.isDirty = true
        isIdentity = false
    }

    override fun setIdentity(): Transform {
        lazyMatrix.setIdentity()
        lazyInvMatrix.setIdentity()
        translation.set(Vec3d.ZERO)
        rotation.setIdentity()
        scale.set(Vec3d.ONES)
        isIdentity = true
        return this
    }

    override fun getPosition(result: MutableVec3d): MutableVec3d {
        return result.set(translation)
    }

    override fun setPosition(position: Vec3d) = setPosition(position.x, position.y, position.z)
    override fun setPosition(x: Double, y: Double, z: Double): TrsTransform {
        translation.set(x, y, z)
        markDirty()
        return this
    }

    override fun translate(t: Vec3f) = translate(t.x.toDouble(), t.y.toDouble(), t.z.toDouble())
    override fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())
    override fun translate(t: Vec3d) = translate(t.x, t.y, t.z)
    override fun translate(tx: Double, ty: Double, tz: Double): TrsTransform {
        translation.x += tx
        translation.y += ty
        translation.z += tz
        markDirty()
        return this
    }

    fun getRotation(result: Mat3d): Mat3d {
        return result.set(rotation)
    }

    fun getRotation(resultQuaternion: MutableQuatD): MutableQuatD {
        return rotation.getRotation(resultQuaternion)
    }

    fun setRotation(rotation: Mat3f): TrsTransform {
        this.rotation.set(rotation)
        markDirty()
        return this
    }

    fun setRotation(rotation: Mat3d): TrsTransform {
        this.rotation.set(rotation)
        markDirty()
        return this
    }

    fun setRotation(quaternion: QuatF): TrsTransform {
        this.rotation.setRotate(quaternion.toQuatD())
        markDirty()
        return this
    }

    fun setRotation(quaternion: QuatD): TrsTransform {
        this.rotation.setRotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)
    override fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())
    override fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)
    override fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): TrsTransform {
        rotation.rotate(angleDeg, axX, axY, axZ)
        markDirty()
        return this
    }

    override fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float) = rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())
    override fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): TrsTransform {
        rotation.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    fun getScale(result: MutableVec3d): MutableVec3d {
        return result.set(scale)
    }

    fun setScale(scale: Vec3d): TrsTransform {
        this.scale.set(scale)
        return this
    }

    override fun scale(s: Float) = scale(s.toDouble(), s.toDouble(), s.toDouble())
    override fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())
    override fun scale(s: Double) = scale(s, s, s)
    override fun scale(sx: Double, sy: Double, sz: Double): TrsTransform {
        scale.x *= sx
        scale.y *= sy
        scale.z *= sz
        markDirty()
        return this
    }

    fun set(other: TrsTransform): TrsTransform {
        translation.set(other.translation)
        rotation.set(other.rotation)
        scale.set(other.scale)
        markDirty()
        return this
    }

    fun set(mat: Mat4d): TrsTransform {
        translation.set(mat.getOrigin(MutableVec3d()))
        rotation.setRotate(mat.getRotation(MutableQuatD()))
        scale.set(mat.getScale(MutableVec3d()))
        markDirty()
        return this
    }

    fun set(mat: Mat4f): TrsTransform {
        translation.set(mat.getOrigin(MutableVec3f()))
        rotation.setRotate(mat.getRotation(MutableQuatF()).toQuatD())
        scale.set(mat.getScale(MutableVec3f()))
        markDirty()
        return this
    }
}

/**
 * Matrix based transform implementation. All operations directly affect the underlying 4x4 transform matrix and, thus,
 * are not independent of each other (i.e. the order of calls to translate / rotate / scale functions matters). This
 * can be useful for, e.g., animations but is generally less intuitive than the [TrsTransform] behavior.
 */
class MatrixTransform : Transform() {

    private val lazyInvMatrix = LazyMat4d { matrix.invert(it) }

    override val matrix = Mat4d()
    override val matrixInverse: Mat4d
        get() = lazyInvMatrix.get()
    override var isIdentity = true
        private set

    override fun markDirty() {
        lazyInvMatrix.isDirty = true
        isIdentity = false
    }

    override fun setIdentity(): MatrixTransform {
        matrix.setIdentity()
        lazyInvMatrix.setIdentity()
        isIdentity = true
        return this
    }

    override fun getPosition(result: MutableVec3d): MutableVec3d {
        return matrix.getOrigin(result)
    }
    override fun setPosition(x: Double, y: Double, z: Double) = setPosition(Vec3d(x, y, z))
    override fun setPosition(position: Vec3d): MatrixTransform {
        matrix.setOrigin(position)
        markDirty()
        return this
    }

    override fun translate(t: Vec3f) = translate(t.x, t.y, t.z)
    override fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())
    override fun translate(t: Vec3d) = translate(t.x, t.y, t.z)
    override fun translate(tx: Double, ty: Double, tz: Double): MatrixTransform {
        matrix.translate(tx, ty, tz)
        markDirty()
        return this
    }

    override fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)
    override fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())
    override fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)
    override fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): MatrixTransform {
        matrix.rotate(angleDeg, axX, axY, axZ)
        markDirty()
        return this
    }

    override fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float) = rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())
    override fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): MatrixTransform {
        matrix.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    override fun scale(s: Float) = scale(s, s, s)
    override fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())
    override fun scale(s: Double) = scale(s, s, s)
    override fun scale(sx: Double, sy: Double, sz: Double): MatrixTransform {
        matrix.scale(sx, sy, sz)
        markDirty()
        return this
    }

    fun mul(mat: Mat4d): MatrixTransform {
        matrix.mul(mat)
        markDirty()
        return this
    }

    fun set(mat: Mat4f): MatrixTransform {
        matrix.set(mat)
        markDirty()
        return this
    }

    fun set(mat: Mat4d): MatrixTransform {
        matrix.set(mat)
        markDirty()
        return this
    }

}
