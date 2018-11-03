package de.fabmax.kool.math

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


open class Vec2f(x: Float, y: Float) {

    protected val fields = FloatArray(2)

    open val x get() = this[0]
    open val y get() = this[1]

    constructor(f: Float) : this(f, f)
    constructor(v: Vec2f) : this(v.x, v.y)

    init {
        fields[0] = x
        fields[1] = y
    }

    fun add(other: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).add(other)

    fun distance(other: Vec2f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec2f): Float = x * other.x + y * other.y

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(other: Vec2f, eps: Float = FUZZY_EQ_F): Boolean =
            isFuzzyEqual(x, other.x, eps) && isFuzzyEqual(y, other.y, eps)

    fun length(): Float = sqrt(sqrLength())

    fun mul(other: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).mul(other)

    fun norm(result: MutableVec2f): MutableVec2f = result.set(this).norm()

    fun rotate(angleDeg: Float, result: MutableVec2f): MutableVec2f = result.set(this).rotate(angleDeg)

    fun scale(factor: Float, result: MutableVec2f): MutableVec2f = result.set(this).scale(factor)

    fun sqrDistance(other: Vec2f): Float {
        val dx = x - other.x
        val dy = y - other.y
        return dx*dx + dy*dy
    }

    fun sqrLength(): Float = x*x + y*y

    fun subtract(other: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).subtract(other)

    open operator fun get(i: Int): Float = fields[i]

    operator fun times(other: Vec2f): Float = dot(other)

    override fun toString(): String = "($x, $y)"

    fun toVec2d(): Vec2d = Vec2d(x.toDouble(), y.toDouble())

    fun toMutableVec2d(): MutableVec2d = toMutableVec2d(MutableVec2d())

    fun toMutableVec2d(result: MutableVec2d): MutableVec2d = result.set(x.toDouble(), y.toDouble())

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2f) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        val ZERO = Vec2f(0f)
        val X_AXIS = Vec2f(1f, 0f)
        val Y_AXIS = Vec2f(0f, 1f)
        val NEG_X_AXIS = Vec2f(-1f, 0f)
        val NEG_Y_AXIS = Vec2f(0f, -1f)
    }
}

open class MutableVec2f(x: Float, y: Float) : Vec2f(x, y) {

    override var x
        get() = this[0]
        set(value) { this[0] = value }
    override var y
        get() = this[1]
        set(value) { this[1] = value }

    constructor() : this(0f, 0f)
    constructor(f: Float) : this(f, f)
    constructor(v: Vec2f) : this(v.x, v.y)

    fun add(other: Vec2f): MutableVec2f {
        x += other.x
        y += other.y
        return this
    }

    fun mul(other: Vec2f): MutableVec2f {
        x *= other.x
        y *= other.y
        return this
    }

    fun norm(): MutableVec2f = scale(1f / length())

    fun rotate(angleDeg: Float): MutableVec2f {
        val rad = angleDeg.toRad()
        val cos = cos(rad)
        val sin = sin(rad)
        val rx = x * cos - y * sin
        val ry = x * sin + y * cos
        x = rx
        y = ry
        return this
    }

    fun scale(factor : Float): MutableVec2f {
        x *= factor
        y *= factor
        return this
    }

    fun set(x: Float, y: Float): MutableVec2f {
        this.x = x
        this.y = y
        return this
    }

    fun set(other: Vec2f): MutableVec2f {
        x = other.x
        y = other.y
        return this
    }

    fun subtract(other: Vec2f): MutableVec2f {
        x -= other.x
        y -= other.y
        return this
    }

    operator fun divAssign(div : Float) { scale(1f / div) }

    operator fun minusAssign(other: Vec2f) { subtract(other) }

    operator fun plusAssign(other: Vec2f) { add(other) }

    open operator fun set(i: Int, v: Float) { fields[i] = v }

    operator fun timesAssign(factor : Float) { scale(factor) }
}


open class Vec2d(x: Double, y: Double) {

    protected val fields = DoubleArray(2)

    open val x get() = this[0]
    open val y get() = this[1]

    constructor(f: Double) : this(f, f)
    constructor(v: Vec2d) : this(v.x, v.y)

    init {
        fields[0] = x
        fields[1] = y
    }

    fun add(other: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).add(other)

    fun distance(other: Vec2d): Double = sqrt(sqrDistance(other))

    fun dot(other: Vec2d): Double = x * other.x + y * other.y

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(other: Vec2d, eps: Double = FUZZY_EQ_D): Boolean =
            isFuzzyEqual(x, other.x, eps) && isFuzzyEqual(y, other.y, eps)

    fun length(): Double = sqrt(sqrLength())

    fun mul(other: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).mul(other)

    fun norm(result: MutableVec2d): MutableVec2d = result.set(this).norm()

    fun rotate(angleDeg: Double, result: MutableVec2d): MutableVec2d = result.set(this).rotate(angleDeg)

    fun scale(factor: Double, result: MutableVec2d): MutableVec2d = result.set(this).scale(factor)

    fun sqrDistance(other: Vec2d): Double {
        val dx = x - other.x
        val dy = y - other.y
        return dx*dx + dy*dy
    }

    fun sqrLength(): Double = x*x + y*y

    fun subtract(other: Vec2d, result: MutableVec2d): MutableVec2d = result.set(this).subtract(other)

    open operator fun get(i: Int): Double = fields[i]

    operator fun times(other: Vec2d): Double = dot(other)

    override fun toString(): String = "($x, $y)"

    fun toVec2f(): Vec2f = Vec2f(x.toFloat(), y.toFloat())

    fun toMutableVec2f(): MutableVec2f = toMutableVec2f(MutableVec2f())

    fun toMutableVec2f(result: MutableVec2f): MutableVec2f = result.set(x.toFloat(), y.toFloat())

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec2d) return false

        if (x != other.x) return false
        if (y != other.y) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        return result
    }

    companion object {
        val ZERO = Vec2d(0.0)
        val X_AXIS = Vec2d(1.0, 0.0)
        val Y_AXIS = Vec2d(0.0, 1.0)
        val NEG_X_AXIS = Vec2d(-1.0, 0.0)
        val NEG_Y_AXIS = Vec2d(0.0, -1.0)
    }
}

open class MutableVec2d(x: Double, y: Double) : Vec2d(x, y) {

    override var x
        get() = this[0]
        set(value) { this[0] = value }
    override var y
        get() = this[1]
        set(value) { this[1] = value }

    constructor() : this(0.0, 0.0)
    constructor(f: Double) : this(f, f)
    constructor(v: Vec2d) : this(v.x, v.y)

    fun add(other: Vec2d): MutableVec2d {
        x += other.x
        y += other.y
        return this
    }

    fun mul(other: Vec2d): MutableVec2d {
        x *= other.x
        y *= other.y
        return this
    }

    fun norm(): MutableVec2d = scale(1.0 / length())

    fun rotate(angleDeg: Double): MutableVec2d {
        val rad = angleDeg.toRad()
        val cos = cos(rad)
        val sin = sin(rad)
        val rx = x * cos - y * sin
        val ry = x * sin + y * cos
        x = rx
        y = ry
        return this
    }

    fun scale(factor : Double): MutableVec2d {
        x *= factor
        y *= factor
        return this
    }

    fun set(x: Double, y: Double): MutableVec2d {
        this.x = x
        this.y = y
        return this
    }

    fun set(other: Vec2d): MutableVec2d {
        x = other.x
        y = other.y
        return this
    }

    fun subtract(other: Vec2d): MutableVec2d {
        x -= other.x
        y -= other.y
        return this
    }

    operator fun divAssign(div : Double) { scale(1f / div) }

    operator fun minusAssign(other: Vec2d) { subtract(other) }

    operator fun plusAssign(other: Vec2d) { add(other) }

    open operator fun set(i: Int, v: Double) { fields[i] = v }

    operator fun timesAssign(factor : Double) { scale(factor) }
}