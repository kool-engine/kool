package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MixedBuffer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun QuatF.toQuatD() = QuatD(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun QuatF.toMutableQuatD(result: MutableQuatD = MutableQuatD()) = result.set(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun MutableQuatF.set(that: QuatD) = set(that.x.toFloat(), that.y.toFloat(), that.z.toFloat(), that.w.toFloat())

fun QuatD.toQuatF() = QuatF(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun QuatD.toMutableQuatF(result: MutableQuatF = MutableQuatF()) = result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun MutableQuatD.set(that: QuatF) = set(that.x.toDouble(), that.y.toDouble(), that.z.toDouble(), that.w.toDouble())

// <template> Changes made within the template section will also affect the other type variants of this class

open class QuatF(open val x: Float, open val y: Float, open val z: Float, open val w: Float) {

    constructor(q: QuatF) : this(q.x, q.y, q.z, q.w)

    /**
     * Multiplies this quaternion with the given one. Consider using [mul] with a pre-allocated result quaternion
     * in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: QuatF): QuatF {
        val rx = w * that.x + x * that.w + y * that.z - z * that.y
        val ry = w * that.y - x * that.z + y * that.w + z * that.x
        val rz = w * that.z + x * that.y - y * that.x + z * that.w
        val rw = w * that.w - x * that.x - y * that.y - z * that.z
        return QuatF(rx, ry, rz, rw)
    }

    /**
     * Multiplies this quaternion with the given one and returns the result in a provided [MutableQuatF].
     */
    fun mul(that: QuatF, result: MutableQuatF): MutableQuatF = result.set(this).mul(that)

    /**
     * Computes the length / magnitude of this quaternion. For valid rotation quaternions, the length should always be
     * equal to 1.
     */
    fun length(): Float = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this quaternion. For valid rotation quaternions, the length should
     * always be equal to 1.
     */
    fun sqrLength(): Float = x*x + y*y + z*z + w*w

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec4f]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: QuatF, weight: Float, result: MutableQuatF = MutableQuatF()): MutableQuatF {
        result.x = that.x * weight + x * (1f - weight)
        result.y = that.y * weight + y * (1f - weight)
        result.z = that.z * weight + z * (1f - weight)
        result.w = that.w * weight + w * (1f - weight)
        return result.norm()
    }

    /**
     * Norms the length of this vector and returns the result in a provided [MutableVec4f].
     */
    fun norm(result: MutableQuatF): MutableQuatF = result.set(this).norm()

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec4f, eps: Float = FUZZY_EQ_F): Boolean =
        isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps) && isFuzzyEqual(w, that.w, eps)

    fun toVec4f(): Vec4f = Vec4f(x, y, z, w)

    fun toMutableVec4f(result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(x, y, z, w)

    override fun toString(): String = "($x, $y, $z, $w)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec4f) return false
        return x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec4f] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
        target.put(w)
    }

    /**
     * Appends the components of this [Vec4f] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
        target.putFloat32(z)
        target.putFloat32(w)
    }

    companion object {
        val IDENTITY = QuatF(0f, 0f, 0f, 1f)
    }
}

open class MutableQuatF(override var x: Float, override var y: Float, override var z: Float, override var w: Float) : QuatF(x, y, z, w) {

    constructor(that: QuatF) : this(that.x, that.y, that.z, that.w)
    constructor() : this(IDENTITY)

    fun set(x: Float, y: Float, z: Float, w: Float): MutableQuatF {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(that: QuatF): MutableQuatF {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    fun set(that: Vec4f): MutableQuatF {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    /**
     * Sets this quaternion to represent the given rotation.
     */
    fun set(angle: AngleF, axis: Vec3f): MutableQuatF {
        var s = axis.sqrLength()
        if (!isFuzzyEqual(s, 1f)) {
            s = 1f / sqrt(s)
        }

        val rad2 = angle.rad * 0.5f
        val factor = sin(rad2) * s
        x = axis.x * factor
        y = axis.y * factor
        z = axis.z * factor
        w = cos(rad2)
        return this
    }

    /**
     * Inplace operation: Multiplies this quaternion with the given one and stores the result in this [MutableQuatF].
     */
    operator fun timesAssign(that: QuatF) { mul(that) }

    /**
     * Inplace operation: Multiplies this quaternion with the given one and stores the result in this [MutableQuatF].
     */
    fun mul(that: QuatF): MutableQuatF {
        val rx = w * that.x + x * that.w + y * that.z - z * that.y
        val ry = w * that.y - x * that.z + y * that.w + z * that.x
        val rz = w * that.z + x * that.y - y * that.x + z * that.w
        val rw = w * that.w - x * that.x - y * that.y - z * that.z
        return set(rx, ry, rz, rw)
    }

    /**
     * Rotates this quaternion by the given angle around the given axis.
     */
    fun rotate(angle: AngleF, axis: Vec3f): MutableQuatF {
        var s = axis.sqrLength()
        if (!isFuzzyEqual(s, 1f)) {
            s = 1f / sqrt(s)
        }

        val rad2 = angle.rad * 0.5f
        val factor = sin(rad2)
        val qx = axis.x * factor * s
        val qy = axis.y * factor * s
        val qz = axis.z * factor * s
        val qw = cos(rad2)

        x = w * qx + x * qw + y * qz - z * qy
        y = w * qy - x * qz + y * qw + z * qx
        z = w * qz + x * qy - y * qx + z * qw
        w = w * qw - x * qx - y * qy - z * qz
        return norm()
    }

    /**
     * Inplace operation: Scales this quaternion to unit length. Special case: A zero-length quaternion
     * results in [QuatF.IDENTITY].
     */
    fun norm(): MutableQuatF {
        val l = length()
        if (l != 0f) {
            val r = 1f / l
            x *= r
            y *= r
            z *= r
            w *= r
        } else {
            set(IDENTITY)
        }
        return this
    }
}

fun QuatF(angle: AngleF, axis: Vec3f): QuatF = MutableQuatF().set(angle, axis)

// </template>


open class QuatD(open val x: Double, open val y: Double, open val z: Double, open val w: Double) {

    constructor(q: QuatD) : this(q.x, q.y, q.z, q.w)

    /**
     * Multiplies this quaternion with the given one. Consider using [mul] with a pre-allocated result quaternion
     * in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: QuatD): QuatD {
        val rx = w * that.x + x * that.w + y * that.z - z * that.y
        val ry = w * that.y - x * that.z + y * that.w + z * that.x
        val rz = w * that.z + x * that.y - y * that.x + z * that.w
        val rw = w * that.w - x * that.x - y * that.y - z * that.z
        return QuatD(rx, ry, rz, rw)
    }

    /**
     * Multiplies this quaternion with the given one and returns the result in a provided [MutableQuatD].
     */
    fun mul(that: QuatD, result: MutableQuatD): MutableQuatD = result.set(this).mul(that)

    /**
     * Computes the length / magnitude of this quaternion. For valid rotation quaternions, the length should always be
     * equal to 1.
     */
    fun length(): Double = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this quaternion. For valid rotation quaternions, the length should
     * always be equal to 1.
     */
    fun sqrLength(): Double = x*x + y*y + z*z + w*w

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec4d]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: QuatD, weight: Double, result: MutableQuatD = MutableQuatD()): MutableQuatD {
        result.x = that.x * weight + x * (1.0 - weight)
        result.y = that.y * weight + y * (1.0 - weight)
        result.z = that.z * weight + z * (1.0 - weight)
        result.w = that.w * weight + w * (1.0 - weight)
        return result.norm()
    }

    /**
     * Norms the length of this vector and returns the result in a provided [MutableVec4d].
     */
    fun norm(result: MutableQuatD): MutableQuatD = result.set(this).norm()

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec4d, eps: Double = FUZZY_EQ_D): Boolean =
        isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps) && isFuzzyEqual(w, that.w, eps)

    fun toVec4f(): Vec4d = Vec4d(x, y, z, w)

    fun toMutableVec4f(result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(x, y, z, w)

    override fun toString(): String = "($x, $y, $z, $w)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec4d) return false
        return x == other.x && y == other.y && z == other.z && w == other.w
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec4d] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
        target.put(w)
    }

    /**
     * Appends the components of this [Vec4d] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
        target.putFloat32(z)
        target.putFloat32(w)
    }

    companion object {
        val IDENTITY = QuatD(0.0, 0.0, 0.0, 1.0)
    }
}

open class MutableQuatD(override var x: Double, override var y: Double, override var z: Double, override var w: Double) : QuatD(x, y, z, w) {

    constructor(that: QuatD) : this(that.x, that.y, that.z, that.w)
    constructor() : this(IDENTITY)

    fun set(x: Double, y: Double, z: Double, w: Double): MutableQuatD {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(that: QuatD): MutableQuatD {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    fun set(that: Vec4d): MutableQuatD {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    /**
     * Sets this quaternion to represent the given rotation.
     */
    fun set(angle: AngleD, axis: Vec3d): MutableQuatD {
        var s = axis.sqrLength()
        if (!isFuzzyEqual(s, 1.0)) {
            s = 1.0 / sqrt(s)
        }

        val rad2 = angle.rad * 0.5
        val factor = sin(rad2) * s
        x = axis.x * factor
        y = axis.y * factor
        z = axis.z * factor
        w = cos(rad2)
        return this
    }

    /**
     * Inplace operation: Multiplies this quaternion with the given one and stores the result in this [MutableQuatD].
     */
    operator fun timesAssign(that: QuatD) { mul(that) }

    /**
     * Inplace operation: Multiplies this quaternion with the given one and stores the result in this [MutableQuatD].
     */
    fun mul(that: QuatD): MutableQuatD {
        val rx = w * that.x + x * that.w + y * that.z - z * that.y
        val ry = w * that.y - x * that.z + y * that.w + z * that.x
        val rz = w * that.z + x * that.y - y * that.x + z * that.w
        val rw = w * that.w - x * that.x - y * that.y - z * that.z
        return set(rx, ry, rz, rw)
    }

    /**
     * Rotates this quaternion by the given angle around the given axis.
     */
    fun rotate(angle: AngleD, axis: Vec3d): MutableQuatD {
        var s = axis.sqrLength()
        if (!isFuzzyEqual(s, 1.0)) {
            s = 1.0 / sqrt(s)
        }

        val rad2 = angle.rad * 0.5
        val factor = sin(rad2)
        val qx = axis.x * factor * s
        val qy = axis.y * factor * s
        val qz = axis.z * factor * s
        val qw = cos(rad2)

        x = w * qx + x * qw + y * qz - z * qy
        y = w * qy - x * qz + y * qw + z * qx
        z = w * qz + x * qy - y * qx + z * qw
        w = w * qw - x * qx - y * qy - z * qz
        return norm()
    }

    /**
     * Inplace operation: Scales this quaternion to unit length. Special case: A zero-length quaternion
     * results in [QuatD.IDENTITY].
     */
    fun norm(): MutableQuatD {
        val l = length()
        if (l != 0.0) {
            val r = 1.0 / l
            x *= r
            y *= r
            z *= r
            w *= r
        } else {
            set(IDENTITY)
        }
        return this
    }
}

fun QuatD(angle: AngleD, axis: Vec3d): QuatD = MutableQuatD().set(angle, axis)
