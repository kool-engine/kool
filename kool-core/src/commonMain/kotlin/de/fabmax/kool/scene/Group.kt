package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox
import de.fabmax.kool.pipeline.RenderPass
import de.fabmax.kool.util.LazyMat4d

/**
 * @author fabmax
 */

fun Node.group(name: String? = null, block: Group.() -> Unit): Group {
    val tg = Group(name)
    tg.block()
    addNode(tg)
    return tg
}

open class Group(name: String? = null) : Node(name) {
    val size: Int get() = intChildren.size

    val transform = Mat4d()

    protected val invTransform = LazyMat4d { transform.invert(it) }
    protected var isIdentity = false

    private val tmpTransformVec = MutableVec3f()
    private val tmpBounds = BoundingBox()

    init {
        setIdentity()
    }

    fun setDirty() {
        invTransform.isDirty = true
        isIdentity = false
    }

    override fun update(updateEvent: RenderPass.UpdateEvent) {
        // update must be called before children are updated, otherwise children use a potentially outdated
        // parent model mat and toGlobalCoords() might yield wrong results
        super.update(updateEvent)

        // update bounds based on updated children bounds
        globalCenterMut.set(bounds.center)
        globalExtentMut.set(bounds.max)
        modelMat.transform(globalCenterMut)
        modelMat.transform(globalExtentMut)
        globalRadius = globalCenter.distance(globalExtentMut)

        // transform group bounds
        if (!bounds.isEmpty && !isIdentity) {
            tmpBounds.clear()
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.min.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.min.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.max.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.min.x, bounds.max.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.min.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.min.y, bounds.max.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.max.y, bounds.min.z), 1f))
            tmpBounds.add(transform.transform(tmpTransformVec.set(bounds.max.x, bounds.max.y, bounds.max.z), 1f))
            bounds.set(tmpBounds)
        }
    }

    override fun updateModelMat() {
        super.updateModelMat()
        if (!isIdentity) {
            modelMat.mul(transform)
        }
    }

    override fun rayTest(test: RayTest) {
        if (!isIdentity) {
            // transform ray to local coordinates
            test.transformBy(invTransform.get())
        }

        super.rayTest(test)

        if (!isIdentity) {
            // transform ray back to previous coordinates
            test.transformBy(transform)
        }
    }

    fun getTransform(result: Mat4f): Mat4f = result.set(transform)

    fun getInverseTransform(result: Mat4f): Mat4f {
        return result.set(invTransform.get())
    }

    fun translate(t: Vec3f) = translate(t.x, t.y, t.z)

    fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())

    fun translate(t: Vec3d) = translate(t.x, t.y, t.z)

    fun translate(tx: Double, ty: Double, tz: Double): Group {
        transform.translate(tx, ty, tz)
        setDirty()
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) =
            rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())

    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Group {
        transform.rotate(angleDeg, axX, axY, axZ)
        setDirty()
        return this
    }

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float): Group =
        rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())

    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Group {
        transform.rotate(eulerX, eulerY, eulerZ)
        setDirty()
        return this
    }

    fun scale(s: Float) = scale(s.toDouble(), s.toDouble(), s.toDouble())

    fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())

    fun scale(s: Double) = scale(s, s, s)

    fun scale(sx: Double, sy: Double, sz: Double): Group {
        transform.scale(sx, sy, sz)
        setDirty()
        return this
    }

    fun mul(mat: Mat4d): Group {
        transform.mul(mat)
        setDirty()
        return this
    }

    fun set(mat: Mat4f): Group {
        transform.set(mat)
        setDirty()
        return this
    }

    fun set(mat: Mat4d): Group {
        transform.set(mat)
        setDirty()
        return this
    }

    fun setIdentity(): Group {
        transform.setIdentity()
        invTransform.clear()
        isIdentity = true
        return this
    }
}