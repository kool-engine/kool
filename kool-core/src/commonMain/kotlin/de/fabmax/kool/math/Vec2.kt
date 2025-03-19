package de.fabmax.kool.math

import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.Int32Buffer
import de.fabmax.kool.util.MixedBuffer
import kotlin.math.sqrt

fun Vec2f.toVec2d() = Vec2d(x.toDouble(), y.toDouble())
fun Vec2f.toMutableVec2d(result: MutableVec2d = MutableVec2d()) = result.set(x.toDouble(), y.toDouble())
fun Vec2f.toVec2i() = Vec2i(x.toInt(), y.toInt())
fun Vec2f.toMutableVec2i(result: MutableVec2i = MutableVec2i()) = result.set(x.toInt(), y.toInt())
fun MutableVec2f.set(that: Vec2d) = set(that.x.toFloat(), that.y.toFloat())
fun MutableVec2f.set(that: Vec2i) = set(that.x.toFloat(), that.y.toFloat())

fun Vec2d.toVec2f() = Vec2f(x.toFloat(), y.toFloat())
fun Vec2d.toMutableVec2f(result: MutableVec2f = MutableVec2f()) = result.set(x.toFloat(), y.toFloat())
fun Vec2d.toVec2i() = Vec2i(x.toInt(), y.toInt())
fun Vec2d.toMutableVec2i(result: MutableVec2i = MutableVec2i()) = result.set(x.toInt(), y.toInt())
fun MutableVec2d.set(that: Vec2f) = set(that.x.toDouble(), that.y.toDouble())
fun MutableVec2d.set(that: Vec2i) = set(that.x.toDouble(), that.y.toDouble())

fun Vec2i.toVec2f() = Vec2f(x.toFloat(), y.toFloat())
fun Vec2i.toMutableVec2f(result: MutableVec2f = MutableVec2f()) = result.set(x.toFloat(), y.toFloat())
fun Vec2i.toVec2d() = Vec2d(x.toDouble(), y.toDouble())
fun Vec2i.toMutableVec2d(result: MutableVec2d = MutableVec2d()) = result.set(x.toDouble(), y.toDouble())
fun MutableVec2i.set(that: Vec2f) = set(that.x.toInt(), that.y.toInt())
fun MutableVec2i.set(that: Vec2d) = set(that.x.toInt(), that.y.toInt())

// <template> Changes made within the template section will also affect the other type variants of this class

open class Vec2f(open val x: Float, open val y: Float) {

    constructor(f: Float): this(f, f)
    constructor(v: Vec2f): this(v.x, v.y)

    operator fun component1(): Float = x
    operator fun component2(): Float = y

    /**
     * Component-wise addition with the given [Vec2f]. Returns the result as a new [Vec2f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec2f) = Vec2f(x + that.x, y + that.y)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec2f]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Float) = Vec2f(x + that, y + that)

    /**
     * Component-wise subtraction with the given [Vec2f]. Returns the result as a new [Vec2f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec2f) = Vec2f(x - that.x, y - that.y)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec2f]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Float) = Vec2f(x - that, y - that)

    /**
     * Component-wise multiplication with the given [Vec2f]. Returns the result as a new [Vec2f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec2f) = Vec2f(x * that.x, y * that.y)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec2f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Float) = Vec2f(x * that, y * that)

    /**
     * Component-wise division with the given [Vec2f]. Returns the result as a new [Vec2f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec2f) = Vec2f(x / that.x, y / that.y)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec2f]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Float) = Vec2f(x / that, y / that)

    /**
     * Component-wise addition with the given [Vec2f]. Returns the result in a provided [MutableVec2f].
     */
    fun add(that: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a provided [MutableVec2f].
     */
    fun add(that: Float, result: MutableVec2f): MutableVec2f = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec2f]. Returns the result as a provided [MutableVec2f].
     */
    fun subtract(that: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec2f].
     */
    fun subtract(that: Float, result: MutableVec2f): MutableVec2f = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec2f]. Returns the result in a provided [MutableVec2f].
     */
    fun mul(that: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar. Returns the result in a provided [MutableVec2f].
     */
    fun mul(that: Float, result: MutableVec2f): MutableVec2f = result.set(this).mul(that)

    override fun toString(): String = "($x, $y)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2f) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec2f] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
    }

    /**
     * Appends the components of this [Vec2f] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec2f): Float = x * that.x + y * that.y

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec2f): Float = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec2f): Float {
        val dx = x - that.x
        val dy = y - that.y
        return dx*dx + dy*dy
    }

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Float = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Float = x*x + y*y

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec2f]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec2f, weight: Float, result: MutableVec2f = MutableVec2f()): MutableVec2f {
        result.x = that.x * weight + x * (1f - weight)
        result.y = that.y * weight + y * (1f - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result in an (optionally provided) [MutableVec2f].
     */
    fun normed(result: MutableVec2f = MutableVec2f()) = result.set(this).norm()

    /**
     * Rotates this vector by the given [AngleF] and returns the result in a provided [MutableVec2f].
     */
    fun rotate(angle: AngleF, result: MutableVec2f): MutableVec2f = result.set(this).rotate(angle)

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Vec2f, eps: Float = FUZZY_EQ_F): Boolean {
        return isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps)
    }

    // </noInt>

    companion object {
        val ZERO = Vec2f(0f, 0f)
        val ONES = Vec2f(1f, 1f)
        val X_AXIS = Vec2f(1f, 0f)
        val Y_AXIS = Vec2f(0f, 1f)
        val NEG_X_AXIS = Vec2f(-1f, 0f)
        val NEG_Y_AXIS = Vec2f(0f, -1f)
    }
}

open class MutableVec2f(override var x: Float, override var y: Float) : Vec2f(x, y) {

    constructor(): this(0f, 0f)
    constructor(f: Float): this(f, f)
    constructor(v: Vec2f): this(v.x, v.y)

    fun set(x: Float, y: Float): MutableVec2f {
        this.x = x
        this.y = y
        return this
    }

    fun set(that: Vec2f): MutableVec2f {
        x = that.x
        y = that.y
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec2f] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec2f) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Float) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec2f] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec2f) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Float) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec2f] component-wise with this vector.
     */
    operator fun timesAssign(that : Vec2f) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Float) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec2f].
     */
    operator fun divAssign(that: Vec2f) {
        x /= that.x
        y /= that.y
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Float) { mul(1f / div) }

    /**
     * Inplace operation: Adds the given [Vec2f] component-wise to this vector.
     */
    fun add(that: Vec2f): MutableVec2f {
        x += that.x
        y += that.y
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Float): MutableVec2f {
        x += that
        y += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec2f] component-wise from this vector.
     */
    fun subtract(that: Vec2f): MutableVec2f {
        x -= that.x
        y -= that.y
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Float): MutableVec2f {
        x -= that
        y -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec2f] component-wise with this vector.
     */
    fun mul(that: Vec2f): MutableVec2f {
        x *= that.x
        y *= that.y
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Float): MutableVec2f {
        x *= that
        y *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Float) = mul(factor)

    // <noInt> The following section will not be included in the integer variant of this class

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec2f {
        val l = length()
        return if (l != 0f) {
            mul(1f / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Inplace operation: Rotates this vector by the given [AngleF].
     */
    fun rotate(angle: AngleF): MutableVec2f {
        val cos = angle.cos
        val sin = angle.sin
        val rx = x * cos - y * sin
        val ry = x * sin + y * cos
        x = rx
        y = ry
        return this
    }

    // </noInt>
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


open class Vec2d(open val x: Double, open val y: Double) {

    constructor(f: Double): this(f, f)
    constructor(v: Vec2d): this(v.x, v.y)

    operator fun component1(): Double = x
    operator fun component2(): Double = y

    /**
     * Component-wise addition with the given [Vec2d]. Returns the result as a new [Vec2d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec2d) = Vec2d(x + that.x, y + that.y)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec2d]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Double) = Vec2d(x + that, y + that)

    /**
     * Component-wise subtraction with the given [Vec2d]. Returns the result as a new [Vec2d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec2d) = Vec2d(x - that.x, y - that.y)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec2d]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Double) = Vec2d(x - that, y - that)

    /**
     * Component-wise multiplication with the given [Vec2d]. Returns the result as a new [Vec2d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec2d) = Vec2d(x * that.x, y * that.y)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec2d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Double) = Vec2d(x * that, y * that)

    /**
     * Component-wise division with the given [Vec2d]. Returns the result as a new [Vec2d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec2d) = Vec2d(x / that.x, y / that.y)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec2d]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Double) = Vec2d(x / that, y / that)

    /**
     * Component-wise addition with the given [Vec2d]. Returns the result in a provided [MutableVec2d].
     */
    fun add(that: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a provided [MutableVec2d].
     */
    fun add(that: Double, result: MutableVec2d): MutableVec2d = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec2d]. Returns the result as a provided [MutableVec2d].
     */
    fun subtract(that: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec2d].
     */
    fun subtract(that: Double, result: MutableVec2d): MutableVec2d = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec2d]. Returns the result in a provided [MutableVec2d].
     */
    fun mul(that: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar. Returns the result in a provided [MutableVec2d].
     */
    fun mul(that: Double, result: MutableVec2d): MutableVec2d = result.set(this).mul(that)

    override fun toString(): String = "($x, $y)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2d) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec2d] to the given [Float32Buffer].
     */
    fun putTo(target: Float32Buffer) {
        target.put(x)
        target.put(y)
    }

    /**
     * Appends the components of this [Vec2d] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(x)
        target.putFloat32(y)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec2d): Double = x * that.x + y * that.y

    /**
     * Computes the Euclidean distance between this and the given vector.
     */
    fun distance(that: Vec2d): Double = sqrt(sqrDistance(that))

    /**
     * Computes the squared Euclidean distance between this and the given vector.
     */
    fun sqrDistance(that: Vec2d): Double {
        val dx = x - that.x
        val dy = y - that.y
        return dx*dx + dy*dy
    }

    /**
     * Computes the length / magnitude of this vector.
     */
    fun length(): Double = sqrt(sqrLength())

    /**
     * Computes the squared length / magnitude of this vector.
     */
    fun sqrLength(): Double = x*x + y*y

    /**
     * Linearly interpolates the values of this and another vector and returns the result as an (optionally provided)
     * [MutableVec2d]: result = that * weight + this * (1 - weight).
     */
    fun mix(that: Vec2d, weight: Double, result: MutableVec2d = MutableVec2d()): MutableVec2d {
        result.x = that.x * weight + x * (1.0 - weight)
        result.y = that.y * weight + y * (1.0 - weight)
        return result
    }

    /**
     * Norms the length of this vector and returns the result in an (optionally provided) [MutableVec2d].
     */
    fun normed(result: MutableVec2d = MutableVec2d()) = result.set(this).norm()

    /**
     * Rotates this vector by the given [AngleD] and returns the result in a provided [MutableVec2d].
     */
    fun rotate(angle: AngleD, result: MutableVec2d): MutableVec2d = result.set(this).rotate(angle)

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Vec2d, eps: Double = FUZZY_EQ_D): Boolean {
        return isFuzzyEqual(x, that.x, eps) && isFuzzyEqual(y, that.y, eps)
    }

    companion object {
        val ZERO = Vec2d(0.0, 0.0)
        val ONES = Vec2d(1.0, 1.0)
        val X_AXIS = Vec2d(1.0, 0.0)
        val Y_AXIS = Vec2d(0.0, 1.0)
        val NEG_X_AXIS = Vec2d(-1.0, 0.0)
        val NEG_Y_AXIS = Vec2d(0.0, -1.0)
    }
}

open class MutableVec2d(override var x: Double, override var y: Double) : Vec2d(x, y) {

    constructor(): this(0.0, 0.0)
    constructor(f: Double): this(f, f)
    constructor(v: Vec2d): this(v.x, v.y)

    fun set(x: Double, y: Double): MutableVec2d {
        this.x = x
        this.y = y
        return this
    }

    fun set(that: Vec2d): MutableVec2d {
        x = that.x
        y = that.y
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec2d] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec2d) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Double) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec2d] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec2d) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Double) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec2d] component-wise with this vector.
     */
    operator fun timesAssign(that : Vec2d) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Double) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec2d].
     */
    operator fun divAssign(that: Vec2d) {
        x /= that.x
        y /= that.y
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Double) { mul(1.0 / div) }

    /**
     * Inplace operation: Adds the given [Vec2d] component-wise to this vector.
     */
    fun add(that: Vec2d): MutableVec2d {
        x += that.x
        y += that.y
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Double): MutableVec2d {
        x += that
        y += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec2d] component-wise from this vector.
     */
    fun subtract(that: Vec2d): MutableVec2d {
        x -= that.x
        y -= that.y
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Double): MutableVec2d {
        x -= that
        y -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec2d] component-wise with this vector.
     */
    fun mul(that: Vec2d): MutableVec2d {
        x *= that.x
        y *= that.y
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Double): MutableVec2d {
        x *= that
        y *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Double) = mul(factor)

    /**
     * Inplace operation: Scales this vector to unit length. Special case: A zero-vector remains zero-length.
     */
    fun norm(): MutableVec2d {
        val l = length()
        return if (l != 0.0) {
            mul(1.0 / l)
        } else {
            set(ZERO)
        }
    }

    /**
     * Inplace operation: Rotates this vector by the given [AngleD].
     */
    fun rotate(angle: AngleD): MutableVec2d {
        val cos = angle.cos
        val sin = angle.sin
        val rx = x * cos - y * sin
        val ry = x * sin + y * cos
        x = rx
        y = ry
        return this
    }

}


open class Vec2i(open val x: Int, open val y: Int) {

    constructor(f: Int): this(f, f)
    constructor(v: Vec2i): this(v.x, v.y)

    operator fun component1(): Int = x
    operator fun component2(): Int = y

    /**
     * Component-wise addition with the given [Vec2i]. Returns the result as a new [Vec2i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Vec2i) = Vec2i(x + that.x, y + that.y)

    /**
     * Component-wise addition with the given scalar. Returns the result as a new [Vec2i]. Consider using [add] with
     * a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun plus(that: Int) = Vec2i(x + that, y + that)

    /**
     * Component-wise subtraction with the given [Vec2i]. Returns the result as a new [Vec2i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Vec2i) = Vec2i(x - that.x, y - that.y)

    /**
     * Component-wise subtraction with the given scalar. Returns the result as a new [Vec2i]. Consider using [subtract]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun minus(that: Int) = Vec2i(x - that, y - that)

    /**
     * Component-wise multiplication with the given [Vec2i]. Returns the result as a new [Vec2i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Vec2i) = Vec2i(x * that.x, y * that.y)

    /**
     * Component-wise multiplication with the given scalar. Returns the result as a new [Vec2i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun times(that: Int) = Vec2i(x * that, y * that)

    /**
     * Component-wise division with the given [Vec2i]. Returns the result as a new [Vec2i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Vec2i) = Vec2i(x / that.x, y / that.y)

    /**
     * Component-wise division with the given scalar. Returns the result as a new [Vec2i]. Consider using [mul]
     * with a pre-allocated result vector in performance-critical situations, to avoid unnecessary object allocations.
     */
    operator fun div(that: Int) = Vec2i(x / that, y / that)

    /**
     * Component-wise addition with the given [Vec2i]. Returns the result in a provided [MutableVec2i].
     */
    fun add(that: Vec2i, result: MutableVec2i): MutableVec2i = result.set(this).add(that)

    /**
     * Component-wise addition with the given scalar. Returns the result in a provided [MutableVec2i].
     */
    fun add(that: Int, result: MutableVec2i): MutableVec2i = result.set(this).add(that)

    /**
     * Component-wise subtraction with the given [Vec2i]. Returns the result as a provided [MutableVec2i].
     */
    fun subtract(that: Vec2i, result: MutableVec2i): MutableVec2i = result.set(this).subtract(that)

    /**
     * Component-wise subtraction with the given scalar. Returns the result in a provided [MutableVec2i].
     */
    fun subtract(that: Int, result: MutableVec2i): MutableVec2i = result.set(this).subtract(that)

    /**
     * Component-wise multiplication with the given [Vec2i]. Returns the result in a provided [MutableVec2i].
     */
    fun mul(that: Vec2i, result: MutableVec2i): MutableVec2i = result.set(this).mul(that)

    /**
     * Component-wise multiplication with the given scalar. Returns the result in a provided [MutableVec2i].
     */
    fun mul(that: Int, result: MutableVec2i): MutableVec2i = result.set(this).mul(that)

    override fun toString(): String = "($x, $y)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2i) return false
        return x == other.x && y == other.y
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    /**
     * Appends the components of this [Vec2i] to the given [Int32Buffer].
     */
    fun putTo(target: Int32Buffer) {
        target.put(x)
        target.put(y)
    }

    /**
     * Appends the components of this [Vec2i] to the given [MixedBuffer].
     */
    fun putTo(target: MixedBuffer) {
        target.putInt32(x)
        target.putInt32(y)
    }

    /**
     * Computes the dot-product of this and the given vector.
     */
    infix fun dot(that: Vec2i): Int = x * that.x + y * that.y

    companion object {
        val ZERO = Vec2i(0, 0)
        val ONES = Vec2i(1, 1)
        val X_AXIS = Vec2i(1, 0)
        val Y_AXIS = Vec2i(0, 1)
        val NEG_X_AXIS = Vec2i(-1, 0)
        val NEG_Y_AXIS = Vec2i(0, -1)
    }
}

open class MutableVec2i(override var x: Int, override var y: Int) : Vec2i(x, y) {

    constructor(): this(0, 0)
    constructor(f: Int): this(f, f)
    constructor(v: Vec2i): this(v.x, v.y)

    fun set(x: Int, y: Int): MutableVec2i {
        this.x = x
        this.y = y
        return this
    }

    fun set(that: Vec2i): MutableVec2i {
        x = that.x
        y = that.y
        return this
    }

    /**
     * Inplace operation: Adds the given [Vec2i] component-wise to this vector.
     */
    operator fun plusAssign(that: Vec2i) { add(that) }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    operator fun plusAssign(that: Int) { add(that) }

    /**
     * Inplace operation: Subtracts the given [Vec2i] component-wise from this vector.
     */
    operator fun minusAssign(that: Vec2i) { subtract(that) }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    operator fun minusAssign(that: Int) { subtract(that) }

    /**
     * Inplace operation: Multiplies the given [Vec2i] component-wise with this vector.
     */
    operator fun timesAssign(that : Vec2i) { mul(that) }

    /**
     * Inplace operation: Multiplies the given scalar component-wise with this vector.
     */
    operator fun timesAssign(that: Int) { mul(that) }

    /**
     * Inplace operation: Divides this vector component-wise by the given [Vec2i].
     */
    operator fun divAssign(that: Vec2i) {
        x /= that.x
        y /= that.y
    }

    /**
     * Inplace operation: Divides this vector component-wise by the given scalar.
     */
    operator fun divAssign(div: Int) { mul(1 / div) }

    /**
     * Inplace operation: Adds the given [Vec2i] component-wise to this vector.
     */
    fun add(that: Vec2i): MutableVec2i {
        x += that.x
        y += that.y
        return this
    }

    /**
     * Inplace operation: Adds the given scalar component-wise to this vector.
     */
    fun add(that: Int): MutableVec2i {
        x += that
        y += that
        return this
    }

    /**
     * Inplace operation: Subtracts the given [Vec2i] component-wise from this vector.
     */
    fun subtract(that: Vec2i): MutableVec2i {
        x -= that.x
        y -= that.y
        return this
    }

    /**
     * Inplace operation: Subtracts the given scalar component-wise from this vector.
     */
    fun subtract(that: Int): MutableVec2i {
        x -= that
        y -= that
        return this
    }

    /**
     * Inplace operation: Multiplies the given [Vec2i] component-wise with this vector.
     */
    fun mul(that: Vec2i): MutableVec2i {
        x *= that.x
        y *= that.y
        return this
    }

    /**
     * Inplace operation: Scales this vector by the given factor.
     */
    fun mul(that : Int): MutableVec2i {
        x *= that
        y *= that
        return this
    }

    @Deprecated("Replace with mul()", ReplaceWith("mul(factor)"))
    fun scale(factor: Int) = mul(factor)
}
