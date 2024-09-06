package de.fabmax.kool.scene

import de.fabmax.kool.math.*

// <template> Changes made within the template section will also affect the other type variants of this class

/**
 * Matrix based transform implementation. All operations directly affect the underlying 4x4 transform matrix and, thus,
 * are not independent of each other (i.e. the order of calls to translate / rotate / scale functions matters). This
 * can be useful for, e.g., animations but is generally less intuitive than the [TrsTransformF] behavior.
 */
class MatrixTransformF : TransformF() {

    override val matrixF = MutableMat4f()

    override fun setIdentity(): MatrixTransformF {
        super.setIdentity()
        matrixF.setIdentity()
        markDirty()
        return this
    }

    override fun setMatrix(transformMat: Mat4f): MatrixTransformF {
        matrixF.set(transformMat)
        markDirty()
        return this
    }

    override fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): MatrixTransformF {
        matrixF.setIdentity().compose(translation, rotation, scale)
        return this
    }

    fun set(other: MatrixTransformF): MatrixTransformF {
        matrixF.set(other.matrixF)
        markDirty()
        return this
    }

    override fun translate(tx: Float, ty: Float, tz: Float): MatrixTransformF {
        matrixF.translate(tx, ty, tz)
        markDirty()
        return this
    }

    override fun rotate(angle: AngleF, axis: Vec3f): MatrixTransformF {
        matrixF.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatF): MatrixTransformF {
        matrixF.rotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF): MatrixTransformF {
        matrixF.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    override fun scale(s: Float): MatrixTransformF {
        matrixF.scale(s)
        markDirty()
        return this
    }

    override fun scale(s: Vec3f): MatrixTransformF {
        matrixF.scale(s)
        markDirty()
        return this
    }

    override fun decompose(translation: MutableVec3f?, rotation: MutableQuatF?, scale: MutableVec3f?) {
        matrixF.decompose(translation, rotation, scale)
    }

    fun mul(mat: Mat4f): MatrixTransformF {
        matrixF.mul(mat)
        markDirty()
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


/**
 * Matrix based transform implementation. All operations directly affect the underlying 4x4 transform matrix and, thus,
 * are not independent of each other (i.e. the order of calls to translate / rotate / scale functions matters). This
 * can be useful for, e.g., animations but is generally less intuitive than the [TrsTransformD] behavior.
 */
class MatrixTransformD : TransformD() {

    override val matrixD = MutableMat4d()

    override fun setIdentity(): MatrixTransformD {
        super.setIdentity()
        matrixD.setIdentity()
        markDirty()
        return this
    }

    override fun setMatrix(transformMat: Mat4d): MatrixTransformD {
        matrixD.set(transformMat)
        markDirty()
        return this
    }

    override fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): MatrixTransformD {
        matrixD.setIdentity().compose(translation, rotation, scale)
        return this
    }

    fun set(other: MatrixTransformD): MatrixTransformD {
        matrixD.set(other.matrixD)
        markDirty()
        return this
    }

    override fun translate(tx: Double, ty: Double, tz: Double): MatrixTransformD {
        matrixD.translate(tx, ty, tz)
        markDirty()
        return this
    }

    override fun rotate(angle: AngleD, axis: Vec3d): MatrixTransformD {
        matrixD.rotate(angle, axis)
        markDirty()
        return this
    }

    override fun rotate(quaternion: QuatD): MatrixTransformD {
        matrixD.rotate(quaternion)
        markDirty()
        return this
    }

    override fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): MatrixTransformD {
        matrixD.rotate(eulerX, eulerY, eulerZ)
        markDirty()
        return this
    }

    override fun scale(s: Double): MatrixTransformD {
        matrixD.scale(s)
        markDirty()
        return this
    }

    override fun scale(s: Vec3d): MatrixTransformD {
        matrixD.scale(s)
        markDirty()
        return this
    }

    override fun decompose(translation: MutableVec3d?, rotation: MutableQuatD?, scale: MutableVec3d?) {
        matrixD.decompose(translation, rotation, scale)
    }

    fun mul(mat: Mat4d): MatrixTransformD {
        matrixD.mul(mat)
        markDirty()
        return this
    }
}
