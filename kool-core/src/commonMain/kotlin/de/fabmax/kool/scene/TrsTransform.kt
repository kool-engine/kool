package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.LazyMat4f

// <template> Changes made within the template section will also affect the other type variants of this class

/**
 * Translation, rotation, scale based transform implementation. This is the standard transform implementation.
 * In contrast to [MatrixTransformF], the TRS components are always relative to the parent coordinate frame and
 * completely independent of one another.
 *
 * The final transform matrix is computed equivalently to this:
 * ```
 *     val matrix = MutableMat4f()
 *         .translate(translation)
 *         .rotate(rotation)
 *         .scale(scale)
 * ```
 */
class TrsTransformF : TransformF() {

    val translation = MutableVec3f()
    val rotation = MutableQuatF()
    val scale = MutableVec3f(Vec3f.ONES)

    private val lazyTransformMat = LazyMat4f {
        it.setIdentity()
            .translate(translation)
            .rotate(rotation)
            .scale(scale)
    }

    override val matrixF: Mat4f get() = lazyTransformMat.get()

    private val tmpMat3 = MutableMat3f()

    override fun markDirty() {
        super.markDirty()
        lazyTransformMat.isDirty = true
    }

    override fun setIdentity(): TrsTransformF {
        super.setIdentity()
        translation.set(Vec3d.ZERO)
        rotation.set(QuatF.IDENTITY)
        scale.set(Vec3f.ONES)
        lazyTransformMat.setIdentity()
        markDirty()
        return this
    }

    override fun setMatrix(transformMat: Mat4f): TrsTransformF {
        transformMat.decompose(translation, rotation, scale)
        markDirty()
        return this
    }

    override fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): TrsTransformF{
        this.translation.set(translation)
        this.rotation.set(rotation)
        this.scale.set(scale)
        markDirty()
        return this
    }

    fun set(other: TrsTransformF): TrsTransformF {
        translation.set(other.translation)
        rotation.set(other.rotation)
        scale.set(other.scale)
        markDirty()
        return this
    }

    override fun translate(tx: Float, ty: Float, tz: Float): TrsTransformF {
        translation.x += tx
        translation.y += ty
        translation.z += tz
        markDirty()
        return this
    }

    override fun rotate(angle: AngleF, axis: Vec3f): TrsTransformF {
        rotation.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatF): TrsTransformF {
        rotation.mul(quaternion)
        markDirty()
        return this
    }
    override fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF): TrsTransformF {
        tmpMat3
            .setIdentity()
            .rotate(rotation)
            .rotate(eulerX, eulerY, eulerZ)
            .getRotation(rotation)
        markDirty()
        return this
    }

    override fun scale(s: Float): TrsTransformF {
        scale *= s
        markDirty()
        return this
    }

    override fun scale(s: Vec3f): TrsTransformF {
        scale *= s
        markDirty()
        return this
    }

    override fun decompose(translation: MutableVec3f?, rotation: MutableQuatF?, scale: MutableVec3f?) {
        translation?.set(this.translation)
        rotation?.set(this.rotation)
        scale?.set(this.scale)
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


/**
 * Translation, rotation, scale based transform implementation. This is the standard transform implementation.
 * In contrast to [MatrixTransformD], the TRS components are always relative to the parent coordinate frame and
 * completely independent of one another.
 *
 * The final transform matrix is computed equivalently to this:
 * ```
 *     val matrix = MutableMat4d()
 *         .translate(translation)
 *         .rotate(rotation)
 *         .scale(scale)
 * ```
 */
class TrsTransformD : TransformD() {

    val translation = MutableVec3d()
    val rotation = MutableQuatD()
    val scale = MutableVec3d(Vec3d.ONES)

    private val lazyTransformMat = LazyMat4d {
        it.setIdentity()
            .translate(translation)
            .rotate(rotation)
            .scale(scale)
    }

    override val matrixD: Mat4d get() = lazyTransformMat.get()

    private val tmpMat3 = MutableMat3d()

    override fun markDirty() {
        super.markDirty()
        lazyTransformMat.isDirty = true
    }

    override fun setIdentity(): TrsTransformD {
        super.setIdentity()
        translation.set(Vec3d.ZERO)
        rotation.set(QuatD.IDENTITY)
        scale.set(Vec3d.ONES)
        lazyTransformMat.setIdentity()
        markDirty()
        return this
    }

    override fun setMatrix(transformMat: Mat4d): TrsTransformD {
        transformMat.decompose(translation, rotation, scale)
        markDirty()
        return this
    }

    override fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): TrsTransformD{
        this.translation.set(translation)
        this.rotation.set(rotation)
        this.scale.set(scale)
        markDirty()
        return this
    }

    fun set(other: TrsTransformD): TrsTransformD {
        translation.set(other.translation)
        rotation.set(other.rotation)
        scale.set(other.scale)
        markDirty()
        return this
    }

    override fun translate(tx: Double, ty: Double, tz: Double): TrsTransformD {
        translation.x += tx
        translation.y += ty
        translation.z += tz
        markDirty()
        return this
    }

    override fun rotate(angle: AngleD, axis: Vec3d): TrsTransformD {
        rotation.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatD): TrsTransformD {
        rotation.mul(quaternion)
        markDirty()
        return this
    }
    override fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): TrsTransformD {
        tmpMat3
            .setIdentity()
            .rotate(rotation)
            .rotate(eulerX, eulerY, eulerZ)
            .getRotation(rotation)
        markDirty()
        return this
    }

    override fun scale(s: Double): TrsTransformD {
        scale *= s
        markDirty()
        return this
    }

    override fun scale(s: Vec3d): TrsTransformD {
        scale *= s
        markDirty()
        return this
    }

    override fun decompose(translation: MutableVec3d?, rotation: MutableQuatD?, scale: MutableVec3d?) {
        translation?.set(this.translation)
        rotation?.set(this.rotation)
        scale?.set(this.scale)
    }
}
