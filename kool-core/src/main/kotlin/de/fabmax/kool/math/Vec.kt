package de.fabmax.kool.math

import kotlin.math.*

/**
 * @author fabmax
 */

fun add(a: Vec2f, b: Vec2f): MutableVec2f = a.add(b, MutableVec2f())
fun add(a: Vec3f, b: Vec3f): MutableVec3f = a.add(b, MutableVec3f())
fun add(a: Vec4f, b: Vec4f): MutableVec4f = a.add(b, MutableVec4f())

fun subtract(a: Vec2f, b: Vec2f): MutableVec2f = a.subtract(b, MutableVec2f())
fun subtract(a: Vec3f, b: Vec3f): MutableVec3f = a.subtract(b, MutableVec3f())
fun subtract(a: Vec4f, b: Vec4f): MutableVec4f = a.subtract(b, MutableVec4f())

fun scale(a: Vec2f, fac: Float): MutableVec2f = a.scale(fac, MutableVec2f())
fun scale(a: Vec3f, fac: Float): MutableVec3f = a.scale(fac, MutableVec3f())
fun scale(a: Vec4f, fac: Float): MutableVec4f = a.scale(fac, MutableVec4f())

fun norm(a: Vec2f): MutableVec2f = a.norm(MutableVec2f())
fun norm(a: Vec3f): MutableVec3f = a.norm(MutableVec3f())

fun cross(a: Vec3f, b: Vec3f): MutableVec3f = a.cross(b, MutableVec3f())

private val slerpTmpA = MutableVec4f()
private val slerpTmpB = MutableVec4f()
private val slerpTmpC = MutableVec4f()
fun slerp(quatA: Vec4f, quatB: Vec4f, f: Float, result: MutableVec4f): MutableVec4f {
    synchronized(slerpTmpA) {
        quatA.norm(slerpTmpA)
        quatB.norm(slerpTmpB)

        val t = f.clamp(0f, 1f)

        var dot = slerpTmpA.dot(slerpTmpB).clamp(-1f, 1f)
        if (dot < 0) {
            slerpTmpA.scale(-1f)
            dot = -dot
        }

        if (dot > 0.9995f) {
            slerpTmpB.subtract(slerpTmpA, result).scale(t).add(slerpTmpA).norm()
        } else {
            val theta0 = acos(dot)
            val theta = theta0 * t

            slerpTmpA.scale(-dot, slerpTmpC).add(slerpTmpB).norm()

            slerpTmpA.scale(cos(theta))
            slerpTmpC.scale(sin(theta))
            result.set(slerpTmpA).add(slerpTmpC)
        }
    }
    return result
}

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

    fun scale(factor : Float): MutableVec3f {
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

    operator fun divAssign(div : Float) { scale(1f / div) }

    operator fun minusAssign(other: Vec3f) { subtract(other) }

    operator fun plusAssign(other: Vec3f) { add(other) }

    open operator fun set(i: Int, v: Float) { fields[i] = v }

    operator fun timesAssign(factor : Float) { scale(factor) }
}

open class Vec4f(x: Float, y: Float, z: Float, w: Float) {

    protected val fields = FloatArray(4)

    open val x get() = this[0]
    open val y get() = this[1]
    open val z get() = this[2]
    open val w get() = this[3]

    constructor(f: Float) : this(f, f, f, f)
    constructor(xyz: Vec3f, w: Float) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(v: Vec4f) : this(v.x, v.y, v.z, v.w)

    init {
        fields[0] = x
        fields[1] = y
        fields[2] = z
        fields[3] = w
    }

    fun add(other: Vec4f, result: MutableVec4f): MutableVec4f = result.set(this).add(other)

    fun distance(other: Vec4f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec4f): Float = x * other.x + y * other.y + z * other.z + w * other.w

    /**
     * Checks vector components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal [eps].
     */
    fun isFuzzyEqual(other: Vec4f, eps: Float = FUZZY_EQ_F): Boolean =
        isFuzzyEqual(x, other.x, eps) && isFuzzyEqual(y, other.y, eps) && isFuzzyEqual(z, other.z, eps) && isFuzzyEqual(w, other.w, eps)

    fun length(): Float = sqrt(sqrLength())

    fun mul(other: Vec4f, result: MutableVec4f): MutableVec4f = result.set(this).mul(other)

    fun norm(result: MutableVec4f): MutableVec4f = result.set(this).norm()

    fun quatProduct(otherQuat: Vec4f, result: MutableVec4f): MutableVec4f {
        result.x = w * otherQuat.x + x * otherQuat.w + y * otherQuat.z - z * otherQuat.y
        result.y = w * otherQuat.y + y * otherQuat.w + z * otherQuat.x - x * otherQuat.z
        result.z = w * otherQuat.z + z * otherQuat.w + x * otherQuat.y - y * otherQuat.x
        result.w = w * otherQuat.w - x * otherQuat.x - y * otherQuat.y - z * otherQuat.z
        return result
    }

    fun scale(factor: Float, result: MutableVec4f): MutableVec4f = result.set(this).scale(factor)

    fun sqrDistance(other: Vec4f): Float {
        val dx = x - other.x
        val dy = y - other.y
        val dz = z - other.z
        val dw = z - other.w
        return dx*dx + dy*dy + dz*dz + dw*dw
    }

    fun sqrLength(): Float = x*x + y*y + z*z + w*w

    fun subtract(other: Vec4f, result: MutableVec4f): MutableVec4f = result.set(this).subtract(other)

    fun getXyz(result: MutableVec3f): MutableVec3f {
        result.x = x
        result.y = y
        result.z = z
        return result
    }

    open operator fun get(i: Int): Float = fields[i]

    operator fun times(other: Vec4f): Float = dot(other)

    override fun toString(): String = "($x, $y, $z, $w)"

    /**
     * Checks vector components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Vec4f) return false

        if (x != other.x) return false
        if (y != other.y) return false
        if (z != other.z) return false
        if (w != other.w) return false

        return true
    }

    override fun hashCode(): Int {
        var result = x.hashCode()
        result = 31 * result + y.hashCode()
        result = 31 * result + z.hashCode()
        result = 31 * result + w.hashCode()
        return result
    }

    companion object {
        val ZERO = Vec4f(0f)
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

open class MutableVec4f(x: Float, y: Float, z: Float, w: Float) : Vec4f(x, y, z, w) {

    override var x
        get() = this[0]
        set(value) { this[0] = value }
    override var y
        get() = this[1]
        set(value) { this[1] = value }
    override var z
        get() = this[2]
        set(value) { this[2] = value }
    override var w
        get() = this[3]
        set(value) { this[3] = value }

    constructor() : this(0f, 0f, 0f, 0f)
    constructor(f: Float) : this(f, f, f, f)
    constructor(xyz: Vec3f, w: Float) : this(xyz.x, xyz.y, xyz.z, w)
    constructor(other: Vec4f) : this(other.x, other.y, other.z, other.w)

    fun add(other: Vec4f): MutableVec4f {
        x += other.x
        y += other.y
        z += other.z
        w += other.w
        return this
    }

    fun mul(other: Vec4f): MutableVec4f {
        x *= other.x
        y *= other.y
        z *= other.z
        w *= other.w
        return this
    }

    fun norm(): MutableVec4f = scale(1f / length())

    fun quatProduct(otherQuat: Vec4f): MutableVec4f {
        val px = w * otherQuat.x + x * otherQuat.w + y * otherQuat.z - z * otherQuat.y
        val py = w * otherQuat.y + y * otherQuat.w + z * otherQuat.x - x * otherQuat.z
        val pz = w * otherQuat.z + z * otherQuat.w + x * otherQuat.y - y * otherQuat.x
        val pw = w * otherQuat.w - x * otherQuat.x - y * otherQuat.y - z * otherQuat.z
        set(px, py, pz, pw)
        return this
    }

    fun scale(factor : Float): MutableVec4f {
        x *= factor
        y *= factor
        z *= factor
        w *= factor
        return this
    }

    fun set(x: Float, y: Float, z: Float, w: Float): MutableVec4f {
        this.x = x
        this.y = y
        this.z = z
        this.w = w
        return this
    }

    fun set(other: Vec4f): MutableVec4f {
        x = other.x
        y = other.y
        z = other.z
        w = other.w
        return this
    }

    fun set(xyz: Vec3f, w: Float = 0f): MutableVec4f {
        x = xyz.x
        y = xyz.y
        z = xyz.z
        this.w = w
        return this
    }

    fun subtract(other: Vec4f): MutableVec4f {
        x -= other.x
        y -= other.y
        z -= other.z
        w -= other.w
        return this
    }

    operator fun plusAssign(other: Vec4f) { add(other) }

    operator fun minusAssign(other: Vec4f) { subtract(other) }

    open operator fun set(i: Int, v: Float) { fields[i] = v }
}
