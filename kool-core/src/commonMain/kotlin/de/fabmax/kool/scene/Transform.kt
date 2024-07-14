package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.util.LazyMat4d
import de.fabmax.kool.util.LazyMat4f
import de.fabmax.kool.util.SyncedMatrixFd

interface Transform {

    val matrixF: Mat4f
    val matrixD: Mat4d

    val invMatrixF: Mat4f
    val invMatrixD: Mat4d

    val isDoublePrecision: Boolean
    val modCount: Int

    fun applyToModelMat(parentModelMat: SyncedMatrixFd?, modelMat: SyncedMatrixFd): Boolean

    fun markDirty()

    fun setIdentity(): Transform

    fun setMatrix(transformMat: Mat4f): Transform
    fun setMatrix(transformMat: Mat4d): Transform
    fun setCompositionOf(translation: Vec3f = Vec3f.ZERO, rotation: QuatF = QuatF.IDENTITY, scale: Vec3f = Vec3f.ONES): Transform
    fun setCompositionOf(translation: Vec3d = Vec3d.ZERO, rotation: QuatD = QuatD.IDENTITY, scale: Vec3d = Vec3d.ONES): Transform

    fun translate(t: Vec3f): Transform
    fun translate(t: Vec3d): Transform
    fun translate(tx: Float, ty: Float, tz: Float): Transform
    fun translate(tx: Double, ty: Double, tz: Double): Transform

    fun rotate(angle: AngleF, axis: Vec3f): Transform
    fun rotate(angle: AngleD, axis: Vec3d): Transform
    fun rotate(quaternion: QuatF): Transform
    fun rotate(quaternion: QuatD): Transform
    fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF): Transform
    fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): Transform

    fun scale(s: Float): Transform
    fun scale(s: Double): Transform
    fun scale(s: Vec3f): Transform
    fun scale(s: Vec3d): Transform

    fun transform(vec: MutableVec3f, w: Float = 1f) = matrixF.transform(vec, w)
    fun transform(vec: MutableVec3d, w: Double = 1.0) = matrixD.transform(vec, w)
    fun transform(vec: Vec3f, w: Float, result: MutableVec3f) = matrixF.transform(vec, w, result)
    fun transform(vec: Vec3d, w: Double, result: MutableVec3d) = matrixD.transform(vec, w, result)
    fun transform(vec: MutableVec4f) = matrixF.transform(vec)
    fun transform(vec: MutableVec4d) = matrixD.transform(vec)
    fun transform(vec: Vec4f, result: MutableVec4f) = matrixF.transform(vec, result)
    fun transform(vec: Vec4d, result: MutableVec4d) = matrixD.transform(vec, result)

    fun decompose(translation: MutableVec3f? = null, rotation: MutableQuatF? = null, scale: MutableVec3f? = null)
    fun decompose(translation: MutableVec3d? = null, rotation: MutableQuatD? = null, scale: MutableVec3d? = null)

    fun getTranslationF(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        decompose(result)
        return result
    }
    fun getTranslationD(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        decompose(result)
        return result
    }
    fun getRotationF(result: MutableQuatF = MutableQuatF()): MutableQuatF {
        decompose(rotation = result)
        return result
    }
    fun getRotationD(result: MutableQuatD = MutableQuatD()): MutableQuatD {
        decompose(rotation = result)
        return result
    }
    fun getScaleF(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        decompose(scale = result)
        return result
    }
    fun getScaleD(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        decompose(scale = result)
        return result
    }
}

fun Transform.set(that: Transform) {
    when (that) {
        is TrsTransformF -> setCompositionOf(that.translation, that.rotation, that.scale)
        is TrsTransformD -> setCompositionOf(that.translation, that.rotation, that.scale)
        is MatrixTransformF -> setMatrix(that.matrixF)
        is MatrixTransformD -> setMatrix(that.matrixD)
        else -> {
            val t = MutableVec3d()
            val r = MutableQuatD()
            val s = MutableVec3d()
            setCompositionOf(t, r, s)
        }
    }
}

abstract class TransformF : Transform {
    override val isDoublePrecision = false

    protected val lazyTransformMatD = LazyMat4d { it.set(matrixF) }
    protected val lazyInvTransformMatF = LazyMat4f { matrixF.invert(it) }
    protected val lazyInvTransformMatD = LazyMat4d { it.set(invMatrixF) }

    override val matrixD: Mat4d get() = lazyTransformMatD.get()
    override val invMatrixF: Mat4f get() = lazyInvTransformMatF.get()
    override val invMatrixD: Mat4d get() = lazyInvTransformMatD.get()

    override var modCount: Int = 0
        protected set

    private val tmpMat4f = MutableMat4f()
    private val tmpVec3fa = MutableVec3f()
    private val tmpVec3fb = MutableVec3f()
    private val tmpQuatF = MutableQuatF()

    private var applyModCount = -1
    private var applyMatsModCount = -1
    private var applyParentMatsModCount = -1

    override fun applyToModelMat(parentModelMat: SyncedMatrixFd?, modelMat: SyncedMatrixFd): Boolean {
        val egoChanged = applyModCount != modCount || applyMatsModCount != modelMat.modCount
        if (parentModelMat != null && (egoChanged || applyParentMatsModCount != parentModelMat.modCount)) {
            modelMat.setMatF { parentModelMat.matF.mul(matrixF, it) }
            applyModCount = modCount
            applyMatsModCount = modelMat.modCount
            applyParentMatsModCount = parentModelMat.modCount
            return true

        } else if (egoChanged) {
            modelMat.setMatF { it.set(matrixF) }
            applyModCount = modCount
            applyMatsModCount = modelMat.modCount
            return true
        }
        return false
    }

    override fun markDirty() {
        lazyTransformMatD.isDirty = true
        lazyInvTransformMatF.isDirty = true
        lazyInvTransformMatD.isDirty = true
        modCount++
    }

    override fun setIdentity(): TransformF {
        lazyTransformMatD.setIdentity()
        lazyInvTransformMatF.setIdentity()
        lazyInvTransformMatD.setIdentity()
        return this
    }

    override fun setMatrix(transformMat: Mat4d): Transform = setMatrix(tmpMat4f.set(transformMat))

    override fun setCompositionOf(translation: Vec3d, rotation: QuatD, scale: Vec3d): Transform =
        setCompositionOf(tmpVec3fa.set(translation), tmpQuatF.set(rotation), tmpVec3fb.set(scale))

    override fun translate(t: Vec3f): Transform = translate(t.x, t.y, t.z)
    override fun translate(t: Vec3d): Transform = translate(t.x.toFloat(), t.y.toFloat(), t.z.toFloat())
    override fun translate(tx: Double, ty: Double, tz: Double): Transform = translate(tx.toFloat(), ty.toFloat(), tz.toFloat())

    override fun rotate(angle: AngleD, axis: Vec3d): Transform = rotate(angle.toAngleF(), tmpVec3fa.set(axis))
    override fun rotate(quaternion: QuatD): Transform = rotate(tmpQuatF.set(quaternion))
    override fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD): Transform =
        rotate(eulerX.toAngleF(), eulerY.toAngleF(), eulerZ.toAngleF())

    override fun scale(s: Double): Transform = scale(s.toFloat())
    override fun scale(s: Vec3d): Transform = scale(tmpVec3fa.set(s))

    override fun decompose(translation: MutableVec3d?, rotation: MutableQuatD?, scale: MutableVec3d?) {
        decompose(tmpVec3fa, tmpQuatF, tmpVec3fb)
        translation?.set(tmpVec3fa)
        rotation?.set(tmpQuatF)
        scale?.set(tmpVec3fb)
    }
}

abstract class TransformD : Transform {
    override val isDoublePrecision = true

    protected val lazyTransformMatF = LazyMat4f { it.set(matrixD) }
    protected val lazyInvTransformMatF = LazyMat4f { matrixF.invert(it) }
    protected val lazyInvTransformMatD = LazyMat4d { it.set(invMatrixF) }

    override val matrixF: Mat4f get() = lazyTransformMatF.get()
    override val invMatrixF: Mat4f get() = lazyInvTransformMatF.get()
    override val invMatrixD: Mat4d get() = lazyInvTransformMatD.get()

    override var modCount: Int = 0
        protected set

    private val tmpMat4d = MutableMat4d()
    private val tmpVec3da = MutableVec3d()
    private val tmpVec3db = MutableVec3d()
    private val tmpQuatD = MutableQuatD()

    private var applyModCount = -1
    private var applyMatsModCount = -1
    private var applyParentMatsModCount = -1

    override fun applyToModelMat(parentModelMat: SyncedMatrixFd?, modelMat: SyncedMatrixFd): Boolean {
        val egoChanged = applyModCount != modCount || applyMatsModCount != modelMat.modCount
        if (parentModelMat != null && (egoChanged || applyParentMatsModCount != parentModelMat.modCount)) {
            modelMat.setMatD { parentModelMat.matD.mul(matrixD, it) }
            applyModCount = modCount
            applyMatsModCount = modelMat.modCount
            applyParentMatsModCount = parentModelMat.modCount
            return true

        } else if (egoChanged) {
            modelMat.setMatD { it.set(matrixD) }
            applyModCount = modCount
            applyMatsModCount = modelMat.modCount
            return true
        }
        return false
    }

    override fun markDirty() {
        lazyTransformMatF.isDirty = true
        lazyInvTransformMatF.isDirty = true
        lazyInvTransformMatD.isDirty = true
        modCount++
    }

    override fun setIdentity(): TransformD {
        lazyTransformMatF.setIdentity()
        lazyInvTransformMatF.setIdentity()
        lazyInvTransformMatD.setIdentity()
        return this
    }

    override fun setMatrix(transformMat: Mat4f): Transform = setMatrix(tmpMat4d.set(transformMat))

    override fun setCompositionOf(translation: Vec3f, rotation: QuatF, scale: Vec3f): Transform =
        setCompositionOf(tmpVec3da.set(translation), tmpQuatD.set(rotation), tmpVec3db.set(scale))

    override fun translate(t: Vec3f): Transform = translate(t.x.toDouble(), t.y.toDouble(), t.z.toDouble())
    override fun translate(t: Vec3d): Transform = translate(t.x, t.y, t.z)
    override fun translate(tx: Float, ty: Float, tz: Float): Transform = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())

    override fun rotate(angle: AngleF, axis: Vec3f): Transform = rotate(angle.toAngleD(), tmpVec3da.set(axis))
    override fun rotate(quaternion: QuatF): Transform = rotate(tmpQuatD.set(quaternion))
    override fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF): Transform =
        rotate(eulerX.toAngleD(), eulerY.toAngleD(), eulerZ.toAngleD())

    override fun scale(s: Float): Transform = scale(s.toDouble())
    override fun scale(s: Vec3f): Transform = scale(tmpVec3da.set(s))

    override fun decompose(translation: MutableVec3f?, rotation: MutableQuatF?, scale: MutableVec3f?) {
        decompose(tmpVec3da, tmpQuatD, tmpVec3db)
        translation?.set(tmpVec3da)
        rotation?.set(tmpQuatD)
        scale?.set(tmpVec3db)
    }
}
