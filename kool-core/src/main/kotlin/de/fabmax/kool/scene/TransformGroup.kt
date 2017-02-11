package de.fabmax.kool.scene

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.Mat4f
import de.fabmax.kool.util.MutableVec3f
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
    protected var transformDirty = true

    var animation: (TransformGroup.(RenderContext) -> Unit)? = null

    private fun checkInverse() {
        if (transformDirty) {
            transform.invert(invTransform)
            transformDirty = false
        }
    }

    override fun render(ctx: RenderContext) {
        val anim = animation
        if (anim != null) {
            this.anim(ctx)
        }

        ctx.mvpState.modelMatrix.push()
        ctx.mvpState.modelMatrix.mul(transform)
        ctx.mvpState.update(ctx)

        super.render(ctx)

        ctx.mvpState.modelMatrix.pop()
        ctx.mvpState.update(ctx)
    }

    override fun toGlobalCoords(vec: MutableVec3f, w: Float): MutableVec3f {
        transform.transform(vec, w)
        return super.toGlobalCoords(vec, w)
    }

    override fun toLocalCoords(vec: MutableVec3f, w: Float): MutableVec3f {
        checkInverse()
        super.toLocalCoords(vec, w)
        return invTransform.transform(vec, w)
    }

    fun translate(tx: Float, ty: Float, tz: Float): TransformGroup {
        transform.translate(tx, ty, tz)
        transformDirty = true
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float): TransformGroup {
        transform.rotate(angleDeg, axX, axY, axZ)
        transformDirty = true
        return this
    }

    fun rotateEuler(xDeg: Float, yDeg: Float, zDeg: Float): TransformGroup {
        transform.rotateEuler(xDeg, yDeg, zDeg)
        transformDirty = true
        return this
    }

    fun scale(sx: Float, sy: Float, sz: Float): TransformGroup {
        transform.scale(sx, sy, sz)
        transformDirty = true
        return this
    }

    fun mul(mat: Mat4f): TransformGroup {
        transform.mul(mat)
        transformDirty = true
        return this
    }

    fun set(mat: Mat4f): TransformGroup {
        transform.set(mat)
        transformDirty = true
        return this
    }

    fun setIdentity(): TransformGroup {
        transform.setIdentity()
        transformDirty = true
        return this
    }
}