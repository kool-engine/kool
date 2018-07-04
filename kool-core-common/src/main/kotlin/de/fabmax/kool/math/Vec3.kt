package de.fabmax.kool.math

import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

open class Vec3f(x: Float, y: Float, z: Float) {

    protected val fields = FloatArray(3)

    open val x get() = this[0]
    open val y get() = this[1]
    open val z get() = this[2]

    constructor(f: Float) : this(f, f, f)
    constructor(v: Vec3f) : this(v.x, v.y, v.z)

    init {
        fields[0] = x
        fields[1] = y
        fields[2] = z
    }

    fun add(other: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).add(other)

    fun cross(other: Vec3f, result: MutableVec3f): MutableVec3f {
        result.x = y * other.z - z * other.y
        result.y = z * other.x - x * other.z
        result.z = x * other.y - y * other.x
        return result
    }

    fun distance(other: Vec3f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec3f): Float = x * other.x + y * other.y + z * other.z

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(other: Vec3f, eps: Float = FUZZY_EQ_F): Boolean =
            isFuzzyEqual(x, other.x, eps) && isFuzzyEqual(y, other.y, eps) && isFuzzyEqual(z, other.z, eps)

    fun length(): Float = sqrt(sqrLength())

    fun mul(other: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).mul(other)

    fun norm(result: MutableVec3f): MutableVec3f = result.set(this).norm()

    fun planeSpace(p: MutableVec3f, q: MutableVec3f) {
        if (abs(z) > SQRT_1_2) {
            // choose p in y-z plane
            val a = y*y + z*z
            val k = 1f / sqrt(a)
            p.x = 0f
            p.y = -z * k
            p.z = y * k
            // q = this x p
            q.x = a * k
            q.y = -x * p.z
            q.z = x * p.y
        } else {
            // choose p in x-y plane
            val a = x*x + y*y
            val k = 1f / sqrt(a)
            p.x = -y * k
            p.y = x * k
            p.z = 0f
            // q = this x p
            q.x = -z * p.y
            q.y = z * p.x
            q.z = a * k
        }
    }

    fun rotate(angleDeg: Float, axisX: Float, axisY: Float, axisZ: Float, result: MutableVec3f): MutableVec3f =
            result.set(this).rotate(angleDeg, axisX, axisY, axisZ)

    fun rotate(angleDeg: Float, axis: Vec3f, result: MutableVec3f): MutableVec3f =
            result.set(this).rotate(angleDeg, axis.x, axis.y, axis.z)

    fun scale(factor: Float, result: MutableVec3f): MutableVec3f = result.set(this).scale(factor)

    fun sqrDistance(other: Vec3f): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx*dx + dy*dy + dz*dz
    }

    fun sqrLength(): Float = x*x + y*y + z*z

    fun subtract(other: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).subtract(other)

    open operator fun get(i: Int) = fields[i]

    operator fun times(other: Vec3f): Float = dot(other)

    override fun toString(): String = "($x, $y, $z)"

    fun toVec3d(): Vec3d = Vec3d(x.toDouble(), y.toDouble(), z.toDouble())

    fun toMutableVec3d(): MutableVec3d = toMutableVec3d(MutableVec3d())

    fun toMutableVec3d(result: MutableVec3d): MutableVec3d = result.set(x.toDouble(), y.toDouble(), z.toDouble())

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

    companion object {
        val ZERO = Vec3f(0f)
        val X_AXIS = Vec3f(1f, 0f, 0f)
        val Y_AXIS = Vec3f(0f, 1f, 0f)
        val Z_AXIS = Vec3f(0f, 0f, 1f)
        val NEG_X_AXIS = Vec3f(-1f, 0f, 0f)
        val NEG_Y_AXIS = Vec3f(0f, -1f, 0f)
        val NEG_Z_AXIS = Vec3f(0f, 0f, -1f)
    }
}

open class MutableVec3f(x: Float, y: Float, z: Float) : Vec3f(x, y, z) {

    override var x
        get() = this[0]
        set(value) { this[0] = value }
    override var y
        get() = this[1]
        set(value) { this[1] = value }
    override var z
        get() = this[2]
        set(value) { this[2] = value }

    constructor() : this(0f, 0f, 0f)
    constructor(f: Float) : this(f, f, f)
    constructor(v: Vec3f) : this(v.x, v.y, v.z)

    fun add(other: Vec3f): MutableVec3f {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun mul(other: Vec3f): MutableVec3f {
        x *= other.x
        y *= other.y
        z *= other.z
        return this
    }

    fun norm(): MutableVec3f = scale(1f / length())

    fun rotate(angleDeg: Float, axisX: Float, axisY: Float, axisZ: Float): MutableVec3f {
        val rad = angleDeg.toRad()
        val c = cos(rad)
        val c1 = 1f - c
        val s = sin(rad)

        val rx = x * (axisX * axisX * c1 + c) + y * (axisX * axisY * c1 - axisZ * s) + z * (axisX * axisZ * c1 + axisY * s)
        val ry = x * (axisY * axisX * c1 + axisZ * s) + y * (axisY * axisY * c1 + c) + z * (axisY * axisZ * c1 - axisX * s)
        val rz = x * (axisX * axisZ * c1 - axisY * s) + y * (axisY * axisZ * c1 + axisX * s) + z * (axisZ * axisZ * c1 + c)
        x = rx
        y = ry
        z = rz
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f): MutableVec3f = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun scale(factor: Float): MutableVec3f {
        x *= factor
        y *= factor
        z *= factor
        return this
    }

    fun set(x: Float, y: Float, z: Float): MutableVec3f {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(other: Vec3f): MutableVec3f {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun subtract(other: Vec3f): MutableVec3f {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    operator fun divAssign(div: Float) { scale(1f / div) }

    operator fun minusAssign(other: Vec3f) { subtract(other) }

    operator fun plusAssign(other: Vec3f) { add(other) }

    open operator fun set(i: Int, v: Float) { fields[i] = v }

    operator fun timesAssign(factor: Float) { scale(factor) }
}

open class Vec3d(x: Double, y: Double, z: Double) {

    protected val fields = DoubleArray(3)

    open val x get() = this[0]
    open val y get() = this[1]
    open val z get() = this[2]

    constructor(f: Double) : this(f, f, f)
    constructor(v: Vec3d) : this(v.x, v.y, v.z)

    init {
        fields[0] = x
        fields[1] = y
        fields[2] = z
    }

    fun add(other: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).add(other)

    fun cross(other: Vec3d, result: MutableVec3d): MutableVec3d {
        result.x = y * other.z - z * other.y
        result.y = z * other.x - x * other.z
        result.z = x * other.y - y * other.x
        return result
    }

    fun distance(other: Vec3d): Double = sqrt(sqrDistance(other))

    fun dot(other: Vec3d): Double = x * other.x + y * other.y + z * other.z

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(other: Vec3d, eps: Double = FUZZY_EQ_D): Boolean =
            isFuzzyEqual(x, other.x, eps) && isFuzzyEqual(y, other.y, eps) && isFuzzyEqual(z, other.z, eps)

    fun length(): Double = sqrt(sqrLength())

    fun mul(other: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).mul(other)

    fun norm(result: MutableVec3d): MutableVec3d = result.set(this).norm()

    fun planeSpace(p: MutableVec3d, q: MutableVec3d) {
        if (abs(z) > SQRT_1_2) {
            // choose p in y-z plane
            val a = y*y + z*z
            val k = 1.0 / sqrt(a)
            p.x = 0.0
            p.y = -z * k
            p.z = y * k
            // q = this x p
            q.x = a * k
            q.y = -x * p.z
            q.z = x * p.y
        } else {
            // choose p in x-y plane
            val a = x*x + y*y
            val k = 1.0 / sqrt(a)
            p.x = -y * k
            p.y = x * k
            p.z = 0.0
            // q = this x p
            q.x = -z * p.y
            q.y = z * p.x
            q.z = a * k
        }
    }

    fun rotate(angleDeg: Double, axisX: Double, axisY: Double, axisZ: Double, result: MutableVec3d): MutableVec3d =
            result.set(this).rotate(angleDeg, axisX, axisY, axisZ)

    fun rotate(angleDeg: Double, axis: Vec3d, result: MutableVec3d): MutableVec3d =
            result.set(this).rotate(angleDeg, axis.x, axis.y, axis.z)

    fun scale(factor: Double, result: MutableVec3d): MutableVec3d = result.set(this).scale(factor)

    fun sqrDistance(other: Vec3d): Double {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        return dx*dx + dy*dy + dz*dz
    }

    fun sqrLength(): Double = x*x + y*y + z*z

    fun subtract(other: Vec3d, result: MutableVec3d): MutableVec3d = result.set(this).subtract(other)

    open operator fun get(i: Int) = fields[i]

    operator fun times(other: Vec3d): Double = dot(other)

    override fun toString(): String = "($x, $y, $z)"

    fun toVec3f(): Vec3f = Vec3f(x.toFloat(), y.toFloat(), z.toFloat())

    fun toMutableVec3f(): MutableVec3f = toMutableVec3f(MutableVec3f())

    fun toMutableVec3f(result: MutableVec3f): MutableVec3f = result.set(x.toFloat(), y.toFloat(), z.toFloat())

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

    companion object {
        val ZERO = Vec3d(0.0)
        val X_AXIS = Vec3d(1.0, 0.0, 0.0)
        val Y_AXIS = Vec3d(0.0, 1.0, 0.0)
        val Z_AXIS = Vec3d(0.0, 0.0, 1.0)
        val NEG_X_AXIS = Vec3d(-1.0, 0.0, 0.0)
        val NEG_Y_AXIS = Vec3d(0.0, -1.0, 0.0)
        val NEG_Z_AXIS = Vec3d(0.0, 0.0, -1.0)
    }
}

open class MutableVec3d(x: Double, y: Double, z: Double) : Vec3d(x, y, z) {

    override var x
        get() = this[0]
        set(value) { this[0] = value }
    override var y
        get() = this[1]
        set(value) { this[1] = value }
    override var z
        get() = this[2]
        set(value) { this[2] = value }

    constructor() : this(0.0, 0.0, 0.0)
    constructor(f: Double) : this(f, f, f)
    constructor(v: Vec3d) : this(v.x, v.y, v.z)

    fun add(other: Vec3d): MutableVec3d {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    fun mul(other: Vec3d): MutableVec3d {
        x *= other.x
        y *= other.y
        z *= other.z
        return this
    }

    fun norm(): MutableVec3d = scale(1.0f / length())

    fun rotate(angleDeg: Double, axisX: Double, axisY: Double, axisZ: Double): MutableVec3d {
        val rad = angleDeg.toRad()
        val c = cos(rad)
        val c1 = 1f - c
        val s = sin(rad)

        val rx = x * (axisX * axisX * c1 + c) + y * (axisX * axisY * c1 - axisZ * s) + z * (axisX * axisZ * c1 + axisY * s)
        val ry = x * (axisY * axisX * c1 + axisZ * s) + y * (axisY * axisY * c1 + c) + z * (axisY * axisZ * c1 - axisX * s)
        val rz = x * (axisX * axisZ * c1 - axisY * s) + y * (axisY * axisZ * c1 + axisX * s) + z * (axisZ * axisZ * c1 + c)
        x = rx
        y = ry
        z = rz
        return this
    }

    fun rotate(angleDeg: Double, axis: Vec3d): MutableVec3d = rotate(angleDeg, axis.x, axis.y, axis.z)

    fun scale(factor: Double): MutableVec3d {
        x *= factor
        y *= factor
        z *= factor
        return this
    }

    fun set(x: Double, y: Double, z: Double): MutableVec3d {
        this.x = x
        this.y = y
        this.z = z
        return this
    }

    fun set(other: Vec3d): MutableVec3d {
        x = other.x
        y = other.y
        z = other.z
        return this
    }

    fun subtract(other: Vec3d): MutableVec3d {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    operator fun divAssign(div: Double) { scale(1.0 / div) }

    operator fun minusAssign(other: Vec3d) { subtract(other) }

    operator fun plusAssign(other: Vec3d) { add(other) }

    open operator fun set(i: Int, v: Double) { fields[i] = v }

    operator fun timesAssign(factor: Double) { scale(factor) }
}