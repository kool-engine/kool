package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.LazyMat4d

abstract class Transform {

    abstract val matrix: Mat4d
    abstract val matrixInverse: Mat4d

    abstract val isIdentity: Boolean

    abstract fun markDirty()

    abstract fun setIdentity(): Transform

    abstract fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): Transform
    abstract fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): Transform

    abstract fun getPosition(result: MutableVec3d): MutableVec3d

    abstract fun setPosition(x: Double, y: Double, z: Double): Transform

    abstract fun setPosition(position: Vec3d): Transform

    abstract fun translate(t: Vec3f): Transform
    abstract fun translate(tx: Float, ty: Float, tz: Float): Transform
    abstract fun translate(t: Vec3d): Transform
    abstract fun translate(tx: Double, ty: Double, tz: Double): Transform

    abstract fun rotate(angle: AngleF, axis: Vec3f): Transform
    abstract fun rotate(angle: AngleD, axis: Vec3d): Transform
    abstract fun rotate(quaternion: QuatF): Transform
    abstract fun rotate(quaternion: QuatD): Transform
    abstract fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF): Transform
    abstract fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): Transform

    abstract fun scale(s: Float): Transform
    abstract fun scale(s: Vec3f): Transform
    abstract fun scale(s: Double): Transform
    abstract fun scale(s: Vec3d): Transform

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
    val rotation = MutableMat3d()
    val scale = MutableVec3d(Vec3d.ONES)

    private val lazyMatrix = LazyMat4d {
        it.setIdentity()
            .translate(translation)
            .mulUpperLeft(rotation)
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

    override fun setIdentity(): TrsTransform {
        lazyMatrix.setIdentity()
        lazyInvMatrix.setIdentity()
        translation.set(Vec3d.ZERO)
        rotation.setIdentity()
        scale.set(Vec3d.ONES)
        isIdentity = true
        return this
    }

    override fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): TrsTransform {
        this.translation.set(translation)
        this.rotation.setIdentity().rotate(rotation)
        this.scale.set(scale)
        markDirty()
        return this
    }

    override fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): TrsTransform {
        this.translation.set(translation)
        this.rotation.setIdentity().rotate(rotation.toQuatD())
        this.scale.set(scale)
        markDirty()
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

    fun getRotation(result: MutableMat3d): Mat3d {
        return result.set(rotation)
    }

    fun getRotation(resultQuaternion: MutableQuatD): MutableQuatD {
        rotation.decompose(resultQuaternion, MutableVec3d())
        return resultQuaternion
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
        this.rotation.setIdentity().rotate(quaternion.toQuatD())
        markDirty()
        return this
    }

    fun setRotation(quaternion: QuatD): TrsTransform {
        this.rotation.setIdentity().rotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(angle: AngleF, axis: Vec3f) = rotate(angle.toAngleD(), axis.toVec3d())
    override fun rotate(angle: AngleD, axis: Vec3d): TrsTransform {
        rotation.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatF): TrsTransform {
        rotation.rotate(quaternion.toQuatD())
        markDirty()
        return this
    }
    override fun rotate(quaternion: QuatD): TrsTransform {
        rotation.rotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF) = rotate(eulerX.toAngleD(), eulerY.toAngleD(), eulerZ.toAngleD())
    override fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): TrsTransform {
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

    override fun scale(s: Float) = scale(Vec3d(s.toDouble()))
    override fun scale(s: Vec3f) = scale(s.toVec3d())
    override fun scale(s: Double) = scale(Vec3d(s))
    override fun scale(s: Vec3d): TrsTransform {
        scale.x *= s.x
        scale.y *= s.y
        scale.z *= s.z
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
        val q = MutableQuatD()
        mat.decompose(translation, q, scale)
        rotation.setIdentity().rotate(q)
        markDirty()
        return this
    }

    fun set(mat: Mat4f): TrsTransform {
        val t = MutableVec3f()
        val q = MutableQuatF()
        val s = MutableVec3f()
        mat.decompose(t, q, s)
        translation.set(t)
        rotation.setIdentity().rotate(q.toQuatD())
        scale.set(s)
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

    override val matrix = MutableMat4d()
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

    override fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): MatrixTransform {
        matrix.setIdentity().compose(translation, rotation, scale)
        return this
    }

    override fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): MatrixTransform {
        matrix.setIdentity().compose(translation.toVec3d(), rotation.toQuatD(), scale.toVec3d())
        return this
    }

    override fun getPosition(result: MutableVec3d): MutableVec3d {
        return result.set(matrix.m03, matrix.m13, matrix.m23)
    }
    override fun setPosition(x: Double, y: Double, z: Double) = setPosition(Vec3d(x, y, z))
    override fun setPosition(position: Vec3d): MatrixTransform {
        matrix.m03 = position.x
        matrix.m13 = position.y
        matrix.m23 = position.z
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

    override fun rotate(angle: AngleF, axis: Vec3f) = rotate(angle.toAngleD(), axis.toVec3d())
    override fun rotate(angle: AngleD, axis: Vec3d): MatrixTransform {
        matrix.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatF): MatrixTransform {
        matrix.rotate(quaternion.toQuatD())
        markDirty()
        return this
    }
    override fun rotate(quaternion: QuatD): MatrixTransform {
        matrix.rotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF) = rotate(eulerX.toAngleD(), eulerY.toAngleD(), eulerZ.toAngleD())
    override fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): MatrixTransform {
        matrix.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    override fun scale(s: Float) = scale(Vec3d(s.toDouble()))
    override fun scale(s: Vec3f) = scale(s.toVec3d())
    override fun scale(s: Double) = scale(Vec3d(s))
    override fun scale(s: Vec3d): MatrixTransform {
        matrix.scale(s)
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
