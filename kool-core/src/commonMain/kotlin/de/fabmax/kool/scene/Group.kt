package de.fabmax.kool.scene

import de.fabmax.kool.math.*
import de.fabmax.kool.math.spatial.BoundingBox

/**
 * @author fabmax
 */

fun Node.group(name: String? = null, block: Node.() -> Unit): Node {
    val tg = Node(name)
    tg.block()
    addNode(tg)
    return tg
}

@Deprecated("Replaced by Node", replaceWith = ReplaceWith("Node"))
open class Group(name: String? = null) : Node(name) {
    private val tmpTransformVec = MutableVec3f()
    private val tmpBounds = BoundingBox()

    fun getTransform(result: Mat4f): Mat4f = result.set(transform.matrix)
    fun getInverseTransform(result: Mat4f): Mat4f = result.set(transform.matrixInverse)

    fun translate(t: Vec3f) = translate(t.x, t.y, t.z)
    fun translate(tx: Float, ty: Float, tz: Float) = translate(tx.toDouble(), ty.toDouble(), tz.toDouble())
    fun translate(t: Vec3d) = translate(t.x, t.y, t.z)
    fun translate(tx: Double, ty: Double, tz: Double): Node {
        transform.translate(tx, ty, tz)
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f) = rotate(angleDeg, axis.x, axis.y, axis.z)
    fun rotate(angleDeg: Float, axX: Float, axY: Float, axZ: Float) = rotate(angleDeg.toDouble(), axX.toDouble(), axY.toDouble(), axZ.toDouble())
    fun rotate(angleDeg: Double, axis: Vec3d) = rotate(angleDeg, axis.x, axis.y, axis.z)
    fun rotate(angleDeg: Double, axX: Double, axY: Double, axZ: Double): Node {
        transform.rotate(angleDeg, axX, axY, axZ)
        return this
    }

    fun rotate(eulerX: Float, eulerY: Float, eulerZ: Float) = rotate(eulerX.toDouble(), eulerY.toDouble(), eulerZ.toDouble())
    fun rotate(eulerX: Double, eulerY: Double, eulerZ: Double): Node {
        transform.rotate(eulerX, eulerY, eulerZ)
        return this
    }

    fun scale(s: Float) = scale(s.toDouble(), s.toDouble(), s.toDouble())
    fun scale(sx: Float, sy: Float, sz: Float) = scale(sx.toDouble(), sy.toDouble(), sz.toDouble())
    fun scale(s: Double) = scale(s, s, s)
    fun scale(sx: Double, sy: Double, sz: Double): Node {
        transform.scale(sx, sy, sz)
        return this
    }

    fun mul(mat: Mat4d): Node {
        transform.mul(mat)
        return this
    }

    fun set(mat: Mat4f): Node {
        transform.set(mat)
        return this
    }

    fun set(mat: Mat4d): Node {
        transform.set(mat)
        return this
    }

    fun setIdentity(): Node {
        transform.setIdentity()
        return this
    }
}