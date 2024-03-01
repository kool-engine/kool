package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import kotlin.math.sqrt

fun Vec3f.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3f.toMutableVec3d(result: MutableVec3d = MutableVec3d()) = result.set(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3f.toVec3i() = Vec3i(x.toInt(), y.toInt(), z.toInt())
fun Vec3f.toMutableVec3i(result: MutableVec3i = MutableVec3i()) = result.set(x.toInt(), y.toInt(), z.toInt())
fun MutableVec3f.set(that: Vec3d) = set(that.x.toFloat(), that.y.toFloat(), that.z.toFloat())
fun MutableVec3f.set(that: Vec3i) = set(that.x.toFloat(), that.y.toFloat(), that.z.toFloat())

fun Vec3d.toVec3f() = Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3d.toMutableVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3d.toVec3i() = Vec3i(x.toInt(), y.toInt(), z.toInt())
fun Vec3d.toMutableVec3i(result: MutableVec3i = MutableVec3i()) = result.set(x.toInt(), y.toInt(), z.toInt())
fun MutableVec3d.set(that: Vec3f) = set(that.x.toDouble(), that.y.toDouble(), that.z.toDouble())
fun MutableVec3d.set(that: Vec3i) = set(that.x.toDouble(), that.y.toDouble(), that.z.toDouble())

fun Vec3i.toVec3f() = Vec3f(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3i.toMutableVec3f(result: MutableVec3f = MutableVec3f()) = result.set(x.toFloat(), y.toFloat(), z.toFloat())
fun Vec3i.toVec3d() = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())
fun Vec3i.toMutableVec3d(result: MutableVec3d = MutableVec3d()) = result.set(x.toDouble(), y.toDouble(), z.toDouble())
fun MutableVec3i.set(that: Vec3f) = set(that.x.toInt(), that.y.toInt(), that.z.toInt())
fun MutableVec3i.set(that: Vec3d) = set(that.x.toInt(), that.y.toInt(), that.z.toInt())

// <template> Changes made within the template section will also affect the other type variants of this class

fun Vec3f(xy: Vec2f, z: Float): Vec3f = Vec3f(xy.x, xy.y, z)
fun Vec3f(x: Float, yz: Vec2f): Vec3f = Vec3f(x, yz.x, yz.y)

open class Vec3f(open val x: Float, open val y: Float, open val z: Float) {

    constructor(f: Float): this(f, f, f)
    constructor(v: Vec3f): this(v.x, v.y, v.z)

    val xy: Vec2f get() = Vec2f(x, y)
    val yz: Vec2f get() = Vec2f(y, z)

    operator fun component1(): Float = x
    operator fun component2(): Float = y
    operator fun component3(): Float = z

    /**
     * Component-wise addition with the given [Vec3f]. Returns the result as a new [Vec3f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec3f) = Vec3f(x + that.x, y + that.y, z + that.z)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec3f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Float) = Vec3f(x + that, y + that, z + that)

    /**
     * Component-wise subtraction with the given [Vec3f]. Returns the result as a new [Vec3f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec3f) = Vec3f(x - that.x, y - that.y, z - that.z)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec3f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Float) = Vec3f(x - that, y - that, z - that)

    /**
     * Component-wise multiplication with the given [Vec3f]. Returns the result as a new [Vec3f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec3f) = Vec3f(x * that.x, y * that.y, z * that.z)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec3f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Float) = Vec3f(x * that, y * that, z * that)

    /**
     * Component-wise division with the given [Vec3f]. Returns the result as a new [Vec3f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec3f) = Vec3f(x / that.x, y / that.y, z / that.z)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec3f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Float) = Vec3f(x / that, y / that, z / that)

    /**
     * Component-wise addition with the given [Vec3f]. Returns the result in a provided [MutableVec3f].
     */
    fun add(that: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a optionally provided [MutableVec3f].
     */
    fun add(that: Float, result: MutableVec3f): MutableVec3f = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec3f]. Returns the result in a provided [MutableVec3f].
     */
    fun subtract(that: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec3f].
     */
    fun subtract(that: Float, result: MutableVec3f): MutableVec3f = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec3f]. Returns the result in a provided [MutableVec3f].
     */
    fun mul(that: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result in a provided [MutableVec3f].
     */
    fun mul(that: Float, result: MutableVec3f): MutableVec3f = result.set(this).mul(that)

    override fun toString(): String = "($x, $y, $z)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec3f) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec3f] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
    }

    /**
     * Appends the components of this [Vec3f] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
        target.putFloat32(z)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec3f): Float = x * that.x + y * that.y + z * that.z

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Computes the cross-product of this and the given vector and returns the result as an (optionally provided)
     * [MutableVec3f].
     */
    fun cross(that: Vec3f, result: MutableVec3f): MutableVec3f {
        result.x = y * that.z - z * that.y
        result.y = z * that.x - x * that.z
        result.z = x * that.y - y * that.x
        return result
    }

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec3f): Float = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec3f): Float {
        val dx = x - that.x
        val dy = y - that.y
        val dz = z - that.z
        return dx*dx + dy*dy + dz*dz
    }

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Float = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Float = x*x + y*y + z*z

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec3f]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec3f, weight: Float, result: MutableVec3f = MutableVec3f()): MutableVec3f {
        result.x = that.x * weight + x * (1f - weight)
        result.y = that.y * weight + y * (1f - weight)
        result.z = that.z * weight + z * (1f - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result in an (optionally provided) [MutableVec3f].
     */
    fun normed(result: MutableVec3f = MutableVec3f()): MutableVec3f = result.set(this).norm()

    /**
     * Returns a unit vector orthogonal to this vector.
     */
    fun ortho(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        val ax = when {
            this.dot(X_AXIS) < 0.5f -> X_AXIS
            this.dot(Y_AXIS) < 0.5f -> Y_AXIS
            else -> Z_AXIS
        }
        return ax.cross(this, result).norm()
    }

    /**
     * Rotates this vector by the given [AngleF] around the given axis and returns the result in a
     * provided [MutableVec3f].
     */
    fun rotate(angle: AngleF, axis: Vec3f, result: MutableVec3f): MutableVec3f {
        return result.set(this).rotate(angle, axis)
    }

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec3f, eps: Float = FUZZY_EQ_F): Boolean {
        return isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps)
    }

    // </noInt>

    companion object {
        val ZERO = Vec3f(0f)
        val ONES = Vec3f(1f)
        val X_AXIS = Vec3f(1f, 0f, 0f)
        val Y_AXIS = Vec3f(0f, 1f, 0f)
        val Z_AXIS = Vec3f(0f, 0f, 1f)
        val NEG_X_AXIS = Vec3f(-1f, 0f, 0f)
        val NEG_Y_AXIS = Vec3f(0f, -1f, 0f)
        val NEG_Z_AXIS = Vec3f(0f, 0f, -1f)
    }
}

open class MutableVec3f(override var x: Float, override var y: Float, override var z: Float) : Vec3f(x, y, z) {

    constructor(): this(0f, 0f, 0f)
    constructor(f: Float): this(f, f, f)
    constructor(v: Vec3f): this(v.x, v.y, v.z)

    fun set(x: Float, y: Float, z: Float): MutableVec3f {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(that: Vec3f): MutableVec3f {
        x = that.x
        y = that.y
        z = that.z
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec3f] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec3f) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Float) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec3f] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec3f) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Float) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec3f] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec3f) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Float) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec3f].
     */
    operator fun divAssign(that: Vec3f) {
        x /= that.x
        y /= that.y
        z /= that.z
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Float) { mul(1f / div) }

    /**
     * Inplace operation: Adds the given [Vec3f] component-wise to this vector.
     */
    fun add(that: Vec3f): MutableVec3f {
        x += that.x
        y += that.y
        z += that.z
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Float): MutableVec3f {
        x += that
        y += that
        z += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec3f] component-wise from this vector.
     */
    fun subtract(that: Vec3f): MutableVec3f {
        x -= that.x
        y -= that.y
        z -= that.z
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Float): MutableVec3f {
        x -= that
        y -= that
        z -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec3f] component-wise with this vector.
     */
    fun mul(that: Vec3f): MutableVec3f {
        x *= that.x
        y *= that.y
        z *= that.z
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Float): MutableVec3f {
        x *= that
        y *= that
        z *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Float) = mul(factor)

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec3f {
        val l = length()
        return if (l != 0f) {
            mul(1f / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Inplace operation: Rotates this vector by the given [AngleF] around the given axis.
     */
    fun rotate(angle: AngleF, axis: Vec3f): MutableVec3f {
        val c = angle.cos
        val c1 = 1f - c
        val s = angle.sin

        val axX = axis.x
        val axY = axis.y
        val axZ = axis.z

        val rx = x * (axX * axX * c1 + c) + y * (axX * axY * c1 - axZ * s) + z * (axX * axZ * c1 + axY * s)
        val ry = x * (axY * axX * c1 + axZ * s) + y * (axY * axY * c1 + c) + z * (axY * axZ * c1 - axX * s)
        val rz = x * (axX * axZ * c1 - axY * s) + y * (axY * axZ * c1 + axX * s) + z * (axZ * axZ * c1 + c)
        x = rx
        y = ry
        z = rz
        return this
    }

    // </noInt>
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


fun Vec3d(xy: Vec2d, z: Double): Vec3d = Vec3d(xy.x, xy.y, z)
fun Vec3d(x: Double, yz: Vec2d): Vec3d = Vec3d(x, yz.x, yz.y)

open class Vec3d(open val x: Double, open val y: Double, open val z: Double) {

    constructor(f: Double): this(f, f, f)
    constructor(v: Vec3d): this(v.x, v.y, v.z)

    val xy: Vec2d get() = Vec2d(x, y)
    val yz: Vec2d get() = Vec2d(y, z)

    operator fun component1(): Double = x
    operator fun component2(): Double = y
    operator fun component3(): Double = z

    /**
     * Component-wise addition with the given [Vec3d]. Returns the result as a new [Vec3d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec3d) = Vec3d(x + that.x, y + that.y, z + that.z)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec3d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Double) = Vec3d(x + that, y + that, z + that)

    /**
     * Component-wise subtraction with the given [Vec3d]. Returns the result as a new [Vec3d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec3d) = Vec3d(x - that.x, y - that.y, z - that.z)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec3d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Double) = Vec3d(x - that, y - that, z - that)

    /**
     * Component-wise multiplication with the given [Vec3d]. Returns the result as a new [Vec3d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec3d) = Vec3d(x * that.x, y * that.y, z * that.z)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec3d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Double) = Vec3d(x * that, y * that, z * that)

    /**
     * Component-wise division with the given [Vec3d]. Returns the result as a new [Vec3d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec3d) = Vec3d(x / that.x, y / that.y, z / that.z)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec3d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Double) = Vec3d(x / that, y / that, z / that)

    /**
     * Component-wise addition with the given [Vec3d]. Returns the result in a provided [MutableVec3d].
     */
    fun add(that: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a optionally provided [MutableVec3d].
     */
    fun add(that: Double, result: MutableVec3d): MutableVec3d = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec3d]. Returns the result in a provided [MutableVec3d].
     */
    fun subtract(that: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec3d].
     */
    fun subtract(that: Double, result: MutableVec3d): MutableVec3d = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec3d]. Returns the result in a provided [MutableVec3d].
     */
    fun mul(that: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result in a provided [MutableVec3d].
     */
    fun mul(that: Double, result: MutableVec3d): MutableVec3d = result.set(this).mul(that)

    override fun toString(): String = "($x, $y, $z)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec3d) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec3d] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
    }

    /**
     * Appends the components of this [Vec3d] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
        target.putFloat32(z)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec3d): Double = x * that.x + y * that.y + z * that.z

    /**
     * Computes the cross-product of this and the given vector and returns the result as an (optionally provided)
     * [MutableVec3d].
     */
    fun cross(that: Vec3d, result: MutableVec3d): MutableVec3d {
        result.x = y * that.z - z * that.y
        result.y = z * that.x - x * that.z
        result.z = x * that.y - y * that.x
        return result
    }

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec3d): Double = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec3d): Double {
        val dx = x - that.x
        val dy = y - that.y
        val dz = z - that.z
        return dx*dx + dy*dy + dz*dz
    }

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Double = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Double = x*x + y*y + z*z

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec3d]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec3d, weight: Double, result: MutableVec3d = MutableVec3d()): MutableVec3d {
        result.x = that.x * weight + x * (1.0 - weight)
        result.y = that.y * weight + y * (1.0 - weight)
        result.z = that.z * weight + z * (1.0 - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result in an (optionally provided) [MutableVec3d].
     */
    fun normed(result: MutableVec3d = MutableVec3d()): MutableVec3d = result.set(this).norm()

    /**
     * Returns a unit vector orthogonal to this vector.
     */
    fun ortho(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        val ax = when {
            this.dot(X_AXIS) < 0.5 -> X_AXIS
            this.dot(Y_AXIS) < 0.5 -> Y_AXIS
            else -> Z_AXIS
        }
        return ax.cross(this, result).norm()
    }

    /**
     * Rotates this vector by the given [AngleD] around the given axis and returns the result in a
     * provided [MutableVec3d].
     */
    fun rotate(angle: AngleD, axis: Vec3d, result: MutableVec3d): MutableVec3d {
        return result.set(this).rotate(angle, axis)
    }

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(that: Vec3d, eps: Double = FUZZY_EQ_D): Boolean {
        return isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps) && isFuzzyEqual(z, that.z, eps)
    }

    companion object {
        val ZERO = Vec3d(0.0)
        val ONES = Vec3d(1.0)
        val X_AXIS = Vec3d(1.0, 0.0, 0.0)
        val Y_AXIS = Vec3d(0.0, 1.0, 0.0)
        val Z_AXIS = Vec3d(0.0, 0.0, 1.0)
        val NEG_X_AXIS = Vec3d(-1.0, 0.0, 0.0)
        val NEG_Y_AXIS = Vec3d(0.0, -1.0, 0.0)
        val NEG_Z_AXIS = Vec3d(0.0, 0.0, -1.0)
    }
}

open class MutableVec3d(override var x: Double, override var y: Double, override var z: Double) : Vec3d(x, y, z) {

    constructor(): this(0.0, 0.0, 0.0)
    constructor(f: Double): this(f, f, f)
    constructor(v: Vec3d): this(v.x, v.y, v.z)

    fun set(x: Double, y: Double, z: Double): MutableVec3d {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(that: Vec3d): MutableVec3d {
        x = that.x
        y = that.y
        z = that.z
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec3d] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec3d) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Double) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec3d] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec3d) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Double) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec3d] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec3d) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Double) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec3d].
     */
    operator fun divAssign(that: Vec3d) {
        x /= that.x
        y /= that.y
        z /= that.z
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Double) { mul(1.0 / div) }

    /**
     * Inplace operation: Adds the given [Vec3d] component-wise to this vector.
     */
    fun add(that: Vec3d): MutableVec3d {
        x += that.x
        y += that.y
        z += that.z
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Double): MutableVec3d {
        x += that
        y += that
        z += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec3d] component-wise from this vector.
     */
    fun subtract(that: Vec3d): MutableVec3d {
        x -= that.x
        y -= that.y
        z -= that.z
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Double): MutableVec3d {
        x -= that
        y -= that
        z -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec3d] component-wise with this vector.
     */
    fun mul(that: Vec3d): MutableVec3d {
        x *= that.x
        y *= that.y
        z *= that.z
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Double): MutableVec3d {
        x *= that
        y *= that
        z *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Double) = mul(factor)

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec3d {
        val l = length()
        return if (l != 0.0) {
            mul(1.0 / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Inplace operation: Rotates this vector by the given [AngleD] around the given axis.
     */
    fun rotate(angle: AngleD, axis: Vec3d): MutableVec3d {
        val c = angle.cos
        val c1 = 1.0 - c
        val s = angle.sin

        val axX = axis.x
        val axY = axis.y
        val axZ = axis.z

        val rx = x * (axX * axX * c1 + c) + y * (axX * axY * c1 - axZ * s) + z * (axX * axZ * c1 + axY * s)
        val ry = x * (axY * axX * c1 + axZ * s) + y * (axY * axY * c1 + c) + z * (axY * axZ * c1 - axX * s)
        val rz = x * (axX * axZ * c1 - axY * s) + y * (axY * axZ * c1 + axX * s) + z * (axZ * axZ * c1 + c)
        x = rx
        y = ry
        z = rz
        return this
    }

}


fun Vec3i(xy: Vec2i, z: Int): Vec3i = Vec3i(xy.x, xy.y, z)
fun Vec3i(x: Int, yz: Vec2i): Vec3i = Vec3i(x, yz.x, yz.y)

open class Vec3i(open val x: Int, open val y: Int, open val z: Int) {

    constructor(f: Int): this(f, f, f)
    constructor(v: Vec3i): this(v.x, v.y, v.z)

    val xy: Vec2i get() = Vec2i(x, y)
    val yz: Vec2i get() = Vec2i(y, z)

    operator fun component1(): Int = x
    operator fun component2(): Int = y
    operator fun component3(): Int = z

    /**
     * Component-wise addition with the given [Vec3i]. Returns the result as a new [Vec3i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec3i) = Vec3i(x + that.x, y + that.y, z + that.z)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec3i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Int) = Vec3i(x + that, y + that, z + that)

    /**
     * Component-wise subtraction with the given [Vec3i]. Returns the result as a new [Vec3i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec3i) = Vec3i(x - that.x, y - that.y, z - that.z)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec3i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Int) = Vec3i(x - that, y - that, z - that)

    /**
     * Component-wise multiplication with the given [Vec3i]. Returns the result as a new [Vec3i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec3i) = Vec3i(x * that.x, y * that.y, z * that.z)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec3i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Int) = Vec3i(x * that, y * that, z * that)

    /**
     * Component-wise division with the given [Vec3i]. Returns the result as a new [Vec3i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec3i) = Vec3i(x / that.x, y / that.y, z / that.z)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec3i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Int) = Vec3i(x / that, y / that, z / that)

    /**
     * Component-wise addition with the given [Vec3i]. Returns the result in a provided [MutableVec3i].
     */
    fun add(that: Vec3i, result: MutableVec3i): MutableVec3i = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a optionally provided [MutableVec3i].
     */
    fun add(that: Int, result: MutableVec3i): MutableVec3i = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec3i]. Returns the result in a provided [MutableVec3i].
     */
    fun subtract(that: Vec3i, result: MutableVec3i): MutableVec3i = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec3i].
     */
    fun subtract(that: Int, result: MutableVec3i): MutableVec3i = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec3i]. Returns the result in a provided [MutableVec3i].
     */
    fun mul(that: Vec3i, result: MutableVec3i): MutableVec3i = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar (i.e. scaling). Returns the result in a provided [MutableVec3i].
     */
    fun mul(that: Int, result: MutableVec3i): MutableVec3i = result.set(this).mul(that)

    override fun toString(): String = "($x, $y, $z)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec3i) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec3i] to the given [Int32Buffer].
     */
    fun putTo(target: Int32Buffer) {
        target.put(x)
        target.put(y)
        target.put(z)
    }

    /**
     * Appends the components of this [Vec3i] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putInt32(x)
        target.putInt32(y)
        target.putInt32(z)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec3i): Int = x * that.x + y * that.y + z * that.z

    companion object {
        val ZERO = Vec3i(0)
        val ONES = Vec3i(1)
        val X_AXIS = Vec3i(1, 0, 0)
        val Y_AXIS = Vec3i(0, 1, 0)
        val Z_AXIS = Vec3i(0, 0, 1)
        val NEG_X_AXIS = Vec3i(-1, 0, 0)
        val NEG_Y_AXIS = Vec3i(0, -1, 0)
        val NEG_Z_AXIS = Vec3i(0, 0, -1)
    }
}

open class MutableVec3i(override var x: Int, override var y: Int, override var z: Int) : Vec3i(x, y, z) {

    constructor(): this(0, 0, 0)
    constructor(f: Int): this(f, f, f)
    constructor(v: Vec3i): this(v.x, v.y, v.z)

    fun set(x: Int, y: Int, z: Int): MutableVec3i {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(that: Vec3i): MutableVec3i {
        x = that.x
        y = that.y
        z = that.z
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec3i] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec3i) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Int) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec3i] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec3i) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Int) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec3i] component-wise with this vector.
     */
    operator fun timesAssign(that: Vec3i) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Int) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec3i].
     */
    operator fun divAssign(that: Vec3i) {
        x /= that.x
        y /= that.y
        z /= that.z
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Int) { mul(1 / div) }

    /**
     * Inplace operation: Adds the given [Vec3i] component-wise to this vector.
     */
    fun add(that: Vec3i): MutableVec3i {
        x += that.x
        y += that.y
        z += that.z
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Int): MutableVec3i {
        x += that
        y += that
        z += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec3i] component-wise from this vector.
     */
    fun subtract(that: Vec3i): MutableVec3i {
        x -= that.x
        y -= that.y
        z -= that.z
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Int): MutableVec3i {
        x -= that
        y -= that
        z -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec3i] component-wise with this vector.
     */
    fun mul(that: Vec3i): MutableVec3i {
        x *= that.x
        y *= that.y
        z *= that.z
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Int): MutableVec3i {
        x *= that
        y *= that
        z *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Int) = mul(factor)
}
