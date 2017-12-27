package de.fabmax.kool.scene

import de.fabmax.kool.RenderContext
import de.fabmax.kool.util.Mat4f
import de.fabmax.kool.util.MutableVec3f
import de.fabmax.kool.util.RayTest
import de.fabmax.kool.util.Vec3f

/**
 * @author fabmax
 */

fun transformGroup(name: String? = null, block: TransformGroup.() -> Unit): TransformGroup {
    val tg = TransformGroup(name)
    tg.block()
    return tg
}

open class TransformGroup(name: String? = null) : Group(name) {
    protected val transform = Mat4f()
    protected val invTransform = Mat4f()
    protected var isIdentity = false
    protected var isDirty = false

    open var animation: (TransformGroup.(RenderContext) -> Unit)? = null

    private val tmpTransformVec = MutableVec3f()

    protected fun checkInverse() {
        if (isDirty) {
            transform.invert(invTransform)
            isDirty = false
        }
    }

    protected fun setDirty() {
        isDirty = true
        isIdentity = false
    }

    override fun render(ctx: RenderContext) {
        if (!isVisible) {
            return
        }

        animation?.invoke(this, ctx)

        // apply transformation
        val wasIdentity = isIdentity
        if (!wasIdentity) {
            ctx.mvpState.modelMatrix.push()
            ctx.mvpState.modelMatrix.mul(transform)
            ctx.mvpState.update(ctx)
        }

        // draw all child nodes
        super.render(ctx)

        // isRendered flag is ignored because this group's bounds aren't valid before children are rendered
        // therefore the frustum check is not reliable

        // transform updated bounding box
        if (!bounds.isEmpty && !wasIdentity) {
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

        // clear transformation
        if (!wasIdentity) {
            ctx.mvpState.modelMatrix.pop()
            ctx.mvpState.update(ctx)
        }
    }

    override fun toGlobalCoords(vec: MutableVec3f, w: Float): MutableVec3f {
        if (!isIdentity) {
            transform.transform(vec, w)
        }
        return super.toGlobalCoords(vec, w)
    }

    override fun toLocalCoords(vec: MutableVec3f, w: Float): MutableVec3f {
        super.toLocalCoords(vec, w)
        if (!isIdentity) {
            checkInverse()
            return invTransform.transform(vec, w)
        } else {
            return vec
        }
    }

    override fun rayTest(test: RayTest) {
        if (!isIdentity) {
            // transform ray to local coordinates
            checkInverse()
            invTransform.transform(test.ray.origin, 1f)
            invTransform.transform(test.ray.direction, 0f)
        }

        super.rayTest(test)

        if (!isIdentity) {
            // transform ray back to previous coordinates
            transform.transform(test.ray.origin, 1f)
            transform.transform(test.ray.direction, 0f)
        }
    }

    fun translate(t: Vec3f): TransformGroup {
        return translate(t.x, t.y, t.z)
    }

    fun translate(tx: Float, ty: Float, tz: Float): TransformGroup {
        transform.translate(tx, ty, tz)
        setDirty()
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float): TransformGroup {
        transform.rotate(angleDeg, axX, axY, axZ)
        setDirty()
        return this
    }

//    fun rotateEuler(xDeg: Float, yDeg: Float, zDeg: Float): TransformGroup {
//        transform.rotateEuler(xDeg, yDeg, zDeg)
//        isDirty = true
//        isIdentity = false
//        return this
//    }

    fun scale(sx: Float, sy: Float, sz: Float): TransformGroup {
        transform.scale(sx, sy, sz)
        setDirty()
        return this
    }

    fun mul(mat: Mat4f): TransformGroup {
        transform.mul(mat)
        setDirty()
        return this
    }

    fun set(mat: Mat4f): TransformGroup {
        transform.set(mat)
        setDirty()
        return this
    }

    fun setIdentity(): TransformGroup {
        transform.setIdentity()
        invTransform.setIdentity()
        isDirty = false
        isIdentity = true
        return this
    }
}