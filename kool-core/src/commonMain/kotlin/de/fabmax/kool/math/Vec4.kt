package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.Uint32Buffer
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

fun Vec4f.toVec4d() = Vec4d(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Vec4f.toMutableVec4d(result: MutableVec4d = MutableVec4d()) = result.set(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Vec4f.toVec4i() = Vec4i(x.toInt(), y.toInt(), z.toInt(), w.toInt())
fun Vec4f.toMutableVec4i(result: MutableVec4i = MutableVec4i()) = result.set(x.toInt(), y.toInt(), z.toInt(), w.toInt())
fun MutableVec4f.set(that: Vec4d) = set(that.x.toFloat(), that.y.toFloat(), that.z.toFloat(), that.w.toFloat())
fun MutableVec4f.set(that: Vec4i) = set(that.x.toFloat(), that.y.toFloat(), that.z.toFloat(), that.w.toFloat())

fun Vec4d.toVec4f() = Vec4f(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun Vec4d.toMutableVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun Vec4d.toVec4i() = Vec4i(x.toInt(), y.toInt(), z.toInt(), w.toInt())
fun Vec4d.toMutableVec4i(result: MutableVec4i = MutableVec4i()) = result.set(x.toInt(), y.toInt(), z.toInt(), w.toInt())
fun MutableVec4d.set(that: Vec4f) = set(that.x.toDouble(), that.y.toDouble(), that.z.toDouble(), that.w.toDouble())
fun MutableVec4d.set(that: Vec4i) = set(that.x.toDouble(), that.y.toDouble(), that.z.toDouble(), that.w.toDouble())

fun Vec4i.toVec4f() = Vec4f(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun Vec4i.toMutableVec4f(result: MutableVec4f = MutableVec4f()) = result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
fun Vec4i.toVec4d() = Vec4d(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun Vec4i.toMutableVec4d(result: MutableVec4d = MutableVec4d()) = result.set(x.toDouble(), y.toDouble(), z.toDouble(), w.toDouble())
fun MutableVec4i.set(that: Vec4f) = set(that.x.toInt(), that.y.toInt(), that.z.toInt(), that.w.toInt())
fun MutableVec4i.set(that: Vec4d) = set(that.x.toInt(), that.y.toInt(), that.z.toInt(), that.w.toInt())

// <template> Changes made within the template section will also affect the other type variants of this class

open class Vec4f(open val x: Float, open val y: Float, open val z: Float, open val w: Float) {

    constructor(f: Float) : this(f, f, f, f)
    constructor(xyz: Vec3f, w: Float) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(v: Vec4f) : this(v.x, v.y, v.z, v.w)

    /**
     * Component-wise addition with the given [Vec4f]. Returns the result as a new [Vec4f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec4f) = Vec4f(x + that.x, y + that.y, z + that.z, w + that.w)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec4f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Float) = Vec4f(x + that, y + that, z + that, w + that)

    /**
     * Component-wise subtraction with the given [Vec4f]. Returns the result as a new [Vec4f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec4f) = Vec4f(x - that.x, y - that.y, z - that.z, w - that.w)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec4f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Float) = Vec4f(x - that, y - that, z - that, w - that)

    /**
     * Component-wise multiplication with the given [Vec4f]. Returns the result as a new [Vec4f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec4f) = Vec4f(x * that.x, y * that.y, z * that.z, w * that.w)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec4f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Float) = Vec4f(x * that, y * that, z * that, w * that)

    /**
     * Component-wise division with the given [Vec4f]. Returns the result as a new [Vec4f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec4f) = Vec4f(x / that.x, y / that.y, z / that.z, w / that.w)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec4f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Float) = Vec4f(x / that, y / that, z / that, w / that)

    /**
     * Component-wise addition with the given [Vec4f]. Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun add(that: Vec4f, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun add(that: Float, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec4f]. Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun subtract(that: Vec4f, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun subtract(that: Float, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec4f]. Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun mul(that: Vec4f, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result as an (optionally provided) [MutableVec4f].
     */
    fun mul(that: Float, result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).mul(that)

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor, result)"))
    fun scale(factor: Float, result: MutableVec4f = MutableVec4f()) = mul(factor, result)

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

    /**
     * Returns the xyz-components of this vector as an (optionally provided) [MutableVec3f].
     */
    fun getXyz(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        result.x = x
        result.y = y
        result.z = z
        return result
    }

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec4f): Float = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec4f): Float {
        val dx = x - that.x
        val dy = y - that.y
        val dz = z - that.z
        val dw = z - that.w
        return dx*dx + dy*dy + dz*dz + dw*dw
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    fun dot(that: Vec4f): Float = x * that.x + y * that.y + z * that.z + w * that.w

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Float = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Float = x*x + y*y + z*z + w*w

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec4f]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec4f, weight: Float, result: MutableVec4f = MutableVec4f()): MutableVec4f {
        result.x = that.x * weight + x * (1f - weight)
        result.y = that.y * weight + y * (1f - weight)
        result.z = that.z * weight + z * (1f - weight)
        result.w = that.w * weight + w * (1f - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result as an (optionally provided) [MutableVec4f].
     */
    fun norm(result: MutableVec4f = MutableVec4f()): MutableVec4f = result.set(this).norm()

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec4f, eps: Float = FUZZY_EQ_F): Boolean =
            isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps) && isFuzzyEqual(w, that.w, eps)

    fun quatProduct(that: Vec4f, result: MutableVec4f): MutableVec4f {
        result.x = w * that.x + x * that.w + y * that.z - z * that.y
        result.y = w * that.y + y * that.w + z * that.x - x * that.z
        result.z = w * that.z + z * that.w + x * that.y - y * that.x
        result.w = w * that.w - x * that.x - y * that.y - z * that.z
        return result
    }

    fun multiplyQuaternion(quat: Vec4f, result: MutableVec4f): MutableVec4f {
        val ax = x
        val ay = y
        val az = z
        val aw = w

        val qx = quat.x
        val qy = quat.y
        val qz = quat.z
        val qw = quat.w

        result.x = aw * qx + ax * qw + ay * qz - az * qy
        result.y = aw * qy - ax * qz + ay * qw + az * qx
        result.z = aw * qz + ax * qy - ay * qx + az * qw
        result.w = aw * qw - ax * qx - ay * qy - az * qz
        return result
    }

    // </noInt>

    companion object {
        val ZERO = Vec4f(0f)
        val ONES = Vec4f(1f)
        val X_AXIS = Vec4f(1f, 0f, 0f, 0f)
        val Y_AXIS = Vec4f(0f, 1f, 0f, 0f)
        val Z_AXIS = Vec4f(0f, 0f, 1f, 0f)
        val W_AXIS = Vec4f(0f, 0f, 0f, 1f)
        val NEG_X_AXIS = Vec4f(-1f, 0f, 0f, 0f)
        val NEG_Y_AXIS = Vec4f(0f, -1f, 0f, 0f)
        val NEG_Z_AXIS = Vec4f(0f, 0f, -1f, 0f)
        val NEG_W_AXIS = Vec4f(0f, 0f, 0f, -1f)
    }
}

open class MutableVec4f(override var x: Float, override var y: Float, override var z: Float, override var w: Float) : Vec4f(x, y, z, w) {

    constructor() : this(0f, 0f, 0f, 0f)
    constructor(f: Float) : this(f, f, f, f)
    constructor(xyz: Vec3f, w: Float) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(that: Vec4f) : this(that.x, that.y, that.z, that.w)

    fun set(x: Float, y: Float, z: Float, w: Float): MutableVec4f {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(that: Vec4f): MutableVec4f {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    fun set(xyz: Vec3f, w: Float = 0f): MutableVec4f {
        x = xyz.x
        y = xyz.y
        z = xyz.z
        this.w = w
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec4f] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec4f) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Float) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec4f] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec4f) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Float) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec4f] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec4f) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Float) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec4f].
     */
    operator fun divAssign(that: Vec4f) {
        x /= that.x
        y /= that.y
        z /= that.z
        w /= that.w
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Float) { mul(1f / div) }

    /**
     * Inplace operation: Adds the given [Vec4f] component-wise to this vector.
     */
    fun add(that: Vec4f): MutableVec4f {
        x += that.x
        y += that.y
        z += that.z
        w += that.w
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Float): MutableVec4f {
        x += that
        y += that
        z += that
        w += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec4f] component-wise from this vector.
     */
    fun subtract(that: Vec4f): MutableVec4f {
        x -= that.x
        y -= that.y
        z -= that.z
        w -= that.w
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Float): MutableVec4f {
        x -= that
        y -= that
        z -= that
        w -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec4f] component-wise with this vector.
     */
    fun mul(that: Vec4f): MutableVec4f {
        x *= that.x
        y *= that.y
        z *= that.z
        w *= that.w
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Float): MutableVec4f {
        x *= that
        y *= that
        z *= that
        w *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Float) = mul(factor)

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec4f {
        val l = length()
        return if (l != 0f) {
            mul(1f / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Sets this vector to the quaternion representation of the given rotation. The axis components must represent a
     * unit vector (i.e. the length of the vector ([axX], [axY], [axZ]) must be 1.0).
     *
     * @param angleDeg rotation angle in degrees
     * @param axX      rotation axis x-component
     * @param axY      rotation axis y-component
     * @param axZ      rotation axis z-component
     */
    fun setRotation(angleDeg: Float, axX: Float, axY: Float, axZ: Float): MutableVec4f {
        val rad2 = angleDeg.toRad() * 0.5f
        val factor = sin(rad2)
        x = axX * factor
        y = axY * factor
        z = axZ * factor
        w = cos(rad2)
        return this
    }

    fun setRotation(angleDeg: Float, axis: Vec3f): MutableVec4f = setRotation(angleDeg, axis.x, axis.y, axis.z)

    fun quatProduct(that: Vec4f): MutableVec4f {
        val px = w * that.x + x * that.w + y * that.z - z * that.y
        val py = w * that.y + y * that.w + z * that.x - x * that.z
        val pz = w * that.z + z * that.w + x * that.y - y * that.x
        val pw = w * that.w - x * that.x - y * that.y - z * that.z
        set(px, py, pz, pw)
        return this
    }

    /**
     * Rotates this vector by the given angle aorund the given axis, assuming that this vector already represents a
     * valid rotation quaternion. The axis components must represent a unit vector (i.e. the length of the vector
     * ([axX], [axY], [axZ]) must be 1.0).
     *
     * @param angleDeg rotation angle in degrees
     * @param axX      rotation axis x-component
     * @param axY      rotation axis y-component
     * @param axZ      rotation axis z-component
     */
    fun rotateQuaternion(angleDeg: Float, axX: Float, axY: Float, axZ: Float): MutableVec4f {
        val ax = x
        val ay = y
        val az = z
        val aw = w

        val rad2 = angleDeg.toRad() * 0.5f
        val factor = sin(rad2)
        val qx = axX * factor
        val qy = axY * factor
        val qz = axZ * factor
        val qw = cos(rad2)

        x = aw * qx + ax * qw + ay * qz - az * qy
        y = aw * qy - ax * qz + ay * qw + az * qx
        z = aw * qz + ax * qy - ay * qx + az * qw
        w = aw * qw - ax * qx - ay * qy - az * qz
        return this
    }

    fun rotateQuaternion(angleDeg: Float, axis: Vec3f): MutableVec4f = rotateQuaternion(angleDeg, axis.x, axis.y, axis.z)

    fun multiplyQuaternion(quat: Vec4f) = multiplyQuaternion(quat, this)

    // </noInt>
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


open class Vec4d(open val x: Double, open val y: Double, open val z: Double, open val w: Double) {

    constructor(f: Double) : this(f, f, f, f)
    constructor(xyz: Vec3d, w: Double) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(v: Vec4d) : this(v.x, v.y, v.z, v.w)

    /**
     * Component-wise addition with the given [Vec4d]. Returns the result as a new [Vec4d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec4d) = Vec4d(x + that.x, y + that.y, z + that.z, w + that.w)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec4d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Double) = Vec4d(x + that, y + that, z + that, w + that)

    /**
     * Component-wise subtraction with the given [Vec4d]. Returns the result as a new [Vec4d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec4d) = Vec4d(x - that.x, y - that.y, z - that.z, w - that.w)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec4d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Double) = Vec4d(x - that, y - that, z - that, w - that)

    /**
     * Component-wise multiplication with the given [Vec4d]. Returns the result as a new [Vec4d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec4d) = Vec4d(x * that.x, y * that.y, z * that.z, w * that.w)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec4d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Double) = Vec4d(x * that, y * that, z * that, w * that)

    /**
     * Component-wise division with the given [Vec4d]. Returns the result as a new [Vec4d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec4d) = Vec4d(x / that.x, y / that.y, z / that.z, w / that.w)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec4d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Double) = Vec4d(x / that, y / that, z / that, w / that)

    /**
     * Component-wise addition with the given [Vec4d]. Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun add(that: Vec4d, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun add(that: Double, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec4d]. Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun subtract(that: Vec4d, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun subtract(that: Double, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec4d]. Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun mul(that: Vec4d, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result as an (optionally provided) [MutableVec4d].
     */
    fun mul(that: Double, result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).mul(that)

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor, result)"))
    fun scale(factor: Double, result: MutableVec4d = MutableVec4d()) = mul(factor, result)

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

    /**
     * Returns the xyz-components of this vector as an (optionally provided) [MutableVec3d].
     */
    fun getXyz(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        result.x = x
        result.y = y
        result.z = z
        return result
    }

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec4d): Double = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec4d): Double {
        val dx = x - that.x
        val dy = y - that.y
        val dz = z - that.z
        val dw = z - that.w
        return dx*dx + dy*dy + dz*dz + dw*dw
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    fun dot(that: Vec4d): Double = x * that.x + y * that.y + z * that.z + w * that.w

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Double = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Double = x*x + y*y + z*z + w*w

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec4d]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec4d, weight: Double, result: MutableVec4d = MutableVec4d()): MutableVec4d {
        result.x = that.x * weight + x * (1.0 - weight)
        result.y = that.y * weight + y * (1.0 - weight)
        result.z = that.z * weight + z * (1.0 - weight)
        result.w = that.w * weight + w * (1.0 - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result as an (optionally provided) [MutableVec4d].
     */
    fun norm(result: MutableVec4d = MutableVec4d()): MutableVec4d = result.set(this).norm()

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec4d, eps: Double = FUZZY_EQ_D): Boolean =
            isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps) && isFuzzyEqual(w, that.w, eps)

    fun quatProduct(that: Vec4d, result: MutableVec4d): MutableVec4d {
        result.x = w * that.x + x * that.w + y * that.z - z * that.y
        result.y = w * that.y + y * that.w + z * that.x - x * that.z
        result.z = w * that.z + z * that.w + x * that.y - y * that.x
        result.w = w * that.w - x * that.x - y * that.y - z * that.z
        return result
    }

    fun multiplyQuaternion(quat: Vec4d, result: MutableVec4d): MutableVec4d {
        val ax = x
        val ay = y
        val az = z
        val aw = w

        val qx = quat.x
        val qy = quat.y
        val qz = quat.z
        val qw = quat.w

        result.x = aw * qx + ax * qw + ay * qz - az * qy
        result.y = aw * qy - ax * qz + ay * qw + az * qx
        result.z = aw * qz + ax * qy - ay * qx + az * qw
        result.w = aw * qw - ax * qx - ay * qy - az * qz
        return result
    }

    companion object {
        val ZERO = Vec4d(0.0)
        val ONES = Vec4d(1.0)
        val X_AXIS = Vec4d(1.0, 0.0, 0.0, 0.0)
        val Y_AXIS = Vec4d(0.0, 1.0, 0.0, 0.0)
        val Z_AXIS = Vec4d(0.0, 0.0, 1.0, 0.0)
        val W_AXIS = Vec4d(0.0, 0.0, 0.0, 1.0)
        val NEG_X_AXIS = Vec4d(-1.0, 0.0, 0.0, 0.0)
        val NEG_Y_AXIS = Vec4d(0.0, -1.0, 0.0, 0.0)
        val NEG_Z_AXIS = Vec4d(0.0, 0.0, -1.0, 0.0)
        val NEG_W_AXIS = Vec4d(0.0, 0.0, 0.0, -1.0)
    }
}

open class MutableVec4d(override var x: Double, override var y: Double, override var z: Double, override var w: Double) : Vec4d(x, y, z, w) {

    constructor() : this(0.0, 0.0, 0.0, 0.0)
    constructor(f: Double) : this(f, f, f, f)
    constructor(xyz: Vec3d, w: Double) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(that: Vec4d) : this(that.x, that.y, that.z, that.w)

    fun set(x: Double, y: Double, z: Double, w: Double): MutableVec4d {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(that: Vec4d): MutableVec4d {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    fun set(xyz: Vec3d, w: Double = 0.0): MutableVec4d {
        x = xyz.x
        y = xyz.y
        z = xyz.z
        this.w = w
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec4d] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec4d) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Double) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec4d] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec4d) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Double) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec4d] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec4d) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Double) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec4d].
     */
    operator fun divAssign(that: Vec4d) {
        x /= that.x
        y /= that.y
        z /= that.z
        w /= that.w
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Double) { mul(1.0 / div) }

    /**
     * Inplace operation: Adds the given [Vec4d] component-wise to this vector.
     */
    fun add(that: Vec4d): MutableVec4d {
        x += that.x
        y += that.y
        z += that.z
        w += that.w
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Double): MutableVec4d {
        x += that
        y += that
        z += that
        w += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec4d] component-wise from this vector.
     */
    fun subtract(that: Vec4d): MutableVec4d {
        x -= that.x
        y -= that.y
        z -= that.z
        w -= that.w
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Double): MutableVec4d {
        x -= that
        y -= that
        z -= that
        w -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec4d] component-wise with this vector.
     */
    fun mul(that: Vec4d): MutableVec4d {
        x *= that.x
        y *= that.y
        z *= that.z
        w *= that.w
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Double): MutableVec4d {
        x *= that
        y *= that
        z *= that
        w *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Double) = mul(factor)

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec4d {
        val l = length()
        return if (l != 0.0) {
            mul(1.0 / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Sets this vector to the quaternion representation of the given rotation. The axis components must represent a
     * unit vector (i.e. the length of the vector ([axX], [axY], [axZ]) must be 1.0).
     *
     * @param angleDeg rotation angle in degrees
     * @param axX      rotation axis x-component
     * @param axY      rotation axis y-component
     * @param axZ      rotation axis z-component
     */
    fun setRotation(angleDeg: Double, axX: Double, axY: Double, axZ: Double): MutableVec4d {
        val rad2 = angleDeg.toRad() * 0.5
        val factor = sin(rad2)
        x = axX * factor
        y = axY * factor
        z = axZ * factor
        w = cos(rad2)
        return this
    }

    fun setRotation(angleDeg: Double, axis: Vec3d): MutableVec4d = setRotation(angleDeg, axis.x, axis.y, axis.z)

    fun quatProduct(that: Vec4d): MutableVec4d {
        val px = w * that.x + x * that.w + y * that.z - z * that.y
        val py = w * that.y + y * that.w + z * that.x - x * that.z
        val pz = w * that.z + z * that.w + x * that.y - y * that.x
        val pw = w * that.w - x * that.x - y * that.y - z * that.z
        set(px, py, pz, pw)
        return this
    }

    /**
     * Rotates this vector by the given angle aorund the given axis, assuming that this vector already represents a
     * valid rotation quaternion. The axis components must represent a unit vector (i.e. the length of the vector
     * ([axX], [axY], [axZ]) must be 1.0).
     *
     * @param angleDeg rotation angle in degrees
     * @param axX      rotation axis x-component
     * @param axY      rotation axis y-component
     * @param axZ      rotation axis z-component
     */
    fun rotateQuaternion(angleDeg: Double, axX: Double, axY: Double, axZ: Double): MutableVec4d {
        val ax = x
        val ay = y
        val az = z
        val aw = w

        val rad2 = angleDeg.toRad() * 0.5
        val factor = sin(rad2)
        val qx = axX * factor
        val qy = axY * factor
        val qz = axZ * factor
        val qw = cos(rad2)

        x = aw * qx + ax * qw + ay * qz - az * qy
        y = aw * qy - ax * qz + ay * qw + az * qx
        z = aw * qz + ax * qy - ay * qx + az * qw
        w = aw * qw - ax * qx - ay * qy - az * qz
        return this
    }

    fun rotateQuaternion(angleDeg: Double, axis: Vec3d): MutableVec4d = rotateQuaternion(angleDeg, axis.x, axis.y, axis.z)

    fun multiplyQuaternion(quat: Vec4d) = multiplyQuaternion(quat, this)

}


open class Vec4i(open val x: Int, open val y: Int, open val z: Int, open val w: Int) {

    constructor(f: Int) : this(f, f, f, f)
    constructor(xyz: Vec3i, w: Int) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(v: Vec4i) : this(v.x, v.y, v.z, v.w)

    /**
     * Component-wise addition with the given [Vec4i]. Returns the result as a new [Vec4i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec4i) = Vec4i(x + that.x, y + that.y, z + that.z, w + that.w)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec4i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Int) = Vec4i(x + that, y + that, z + that, w + that)

    /**
     * Component-wise subtraction with the given [Vec4i]. Returns the result as a new [Vec4i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec4i) = Vec4i(x - that.x, y - that.y, z - that.z, w - that.w)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec4i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Int) = Vec4i(x - that, y - that, z - that, w - that)

    /**
     * Component-wise multiplication with the given [Vec4i]. Returns the result as a new [Vec4i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec4i) = Vec4i(x * that.x, y * that.y, z * that.z, w * that.w)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec4i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Int) = Vec4i(x * that, y * that, z * that, w * that)

    /**
     * Component-wise division with the given [Vec4i]. Returns the result as a new [Vec4i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec4i) = Vec4i(x / that.x, y / that.y, z / that.z, w / that.w)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec4i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Int) = Vec4i(x / that, y / that, z / that, w / that)

    /**
     * Component-wise addition with the given [Vec4i]. Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun add(that: Vec4i, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun add(that: Int, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec4i]. Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun subtract(that: Vec4i, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun subtract(that: Int, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec4i]. Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun mul(that: Vec4i, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result as an (optionally provided) [MutableVec4i].
     */
    fun mul(that: Int, result: MutableVec4i = MutableVec4i()): MutableVec4i = result.set(this).mul(that)

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor, result)"))
    fun scale(factor: Int, result: MutableVec4i = MutableVec4i()) = mul(factor, result)

    override fun toString(): String = "($x, $y, $z, $w)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec4i) return false
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
     * Appends the components of this [Vec4i] to the given [Uint32Buffer].
     */
    fun putTo(target: Uint32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
        target.put(w)
    }

    /**
     * Appends the components of this [Vec4i] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putUint32(x)
        target.putUint32(y)
        target.putUint32(z)
        target.putUint32(w)
    }

    /**
     * Returns the xyz-components of this vector as an (optionally provided) [MutableVec3i].
     */
    fun getXyz(result: MutableVec3i = MutableVec3i()): MutableVec3i {
        result.x = x
        result.y = y
        result.z = z
        return result
    }

    companion object {
        val ZERO = Vec4i(0)
        val ONES = Vec4i(1)
        val X_AXIS = Vec4i(1, 0, 0, 0)
        val Y_AXIS = Vec4i(0, 1, 0, 0)
        val Z_AXIS = Vec4i(0, 0, 1, 0)
        val W_AXIS = Vec4i(0, 0, 0, 1)
        val NEG_X_AXIS = Vec4i(-1, 0, 0, 0)
        val NEG_Y_AXIS = Vec4i(0, -1, 0, 0)
        val NEG_Z_AXIS = Vec4i(0, 0, -1, 0)
        val NEG_W_AXIS = Vec4i(0, 0, 0, -1)
    }
}

open class MutableVec4i(override var x: Int, override var y: Int, override var z: Int, override var w: Int) : Vec4i(x, y, z, w) {

    constructor() : this(0, 0, 0, 0)
    constructor(f: Int) : this(f, f, f, f)
    constructor(xyz: Vec3i, w: Int) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(that: Vec4i) : this(that.x, that.y, that.z, that.w)

    fun set(x: Int, y: Int, z: Int, w: Int): MutableVec4i {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(that: Vec4i): MutableVec4i {
        x = that.x
        y = that.y
        z = that.z
        w = that.w
        return this
    }

    fun set(xyz: Vec3i, w: Int = 0): MutableVec4i {
        x = xyz.x
        y = xyz.y
        z = xyz.z
        this.w = w
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec4i] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec4i) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Int) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec4i] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec4i) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Int) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec4i] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec4i) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Int) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec4i].
     */
    operator fun divAssign(that: Vec4i) {
        x /= that.x
        y /= that.y
        z /= that.z
        w /= that.w
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Int) { mul(1 / div) }

    /**
     * Inplace operation: Adds the given [Vec4i] component-wise to this vector.
     */
    fun add(that: Vec4i): MutableVec4i {
        x += that.x
        y += that.y
        z += that.z
        w += that.w
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Int): MutableVec4i {
        x += that
        y += that
        z += that
        w += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec4i] component-wise from this vector.
     */
    fun subtract(that: Vec4i): MutableVec4i {
        x -= that.x
        y -= that.y
        z -= that.z
        w -= that.w
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Int): MutableVec4i {
        x -= that
        y -= that
        z -= that
        w -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec4i] component-wise with this vector.
     */
    fun mul(that: Vec4i): MutableVec4i {
        x *= that.x
        y *= that.y
        z *= that.z
        w *= that.w
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Int): MutableVec4i {
        x *= that
        y *= that
        z *= that
        w *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Int) = mul(factor)
}
