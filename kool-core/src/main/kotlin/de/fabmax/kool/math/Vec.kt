package de.fabmax.kool.math

import de.fabmax.kool.KoolException
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

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

    // backing fields for properties are declared individual, otherwise overriding the properties and using
    // the super field in mutable sub-class is super-slow in javascript
    protected var xField = x
    protected var yField = y

    open val x get() = xField
    open val y get() = yField

    constructor(f: Float) : this(f, f)

    constructor(v: Vec2f) : this(v.x, v.y)

    fun add(other: Vec2f, result: MutableVec2f): MutableVec2f = result.set(this).add(other)

    fun distance(other: Vec2f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec2f): Float = x * other.x + y * other.y

    fun isEqual(other: Vec2f): Boolean = isEqual(x, other.x) && isEqual(y, other.y)

    fun length(): Float = sqrt(sqrLength())

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

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    operator fun times(other: Vec2f): Float = dot(other)

    override fun toString(): String = "($x, $y)"

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
        get() = xField
        set(value) { xField = value }
    override var y
        get() = yField
        set(value) { yField = value }

    constructor() : this(0f, 0f)

    constructor(v: Vec2f) : this(v.x, v.y)

    fun add(other: Vec2f): MutableVec2f {
        x += other.x
        y += other.y
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

    operator fun set(i: Int, v: Float) {
        when (i) {
            0 -> x = v
            1 -> y = v
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    operator fun timesAssign(factor : Float) { scale(factor) }
}

open class Vec3f(x: Float, y: Float, z: Float) {

    // backing fields for properties are declared individual, otherwise overriding the properties and using
    // the super field in mutable sub-class is super-slow in javascript
    protected var xField = x
    protected var yField = y
    protected var zField = z

    open val x get() = xField
    open val y get() = yField
    open val z get() = zField

    constructor(f: Float) : this(f, f, f)

    constructor(v: Vec3f) : this(v.x, v.y, v.z)

    fun add(other: Vec3f, result: MutableVec3f): MutableVec3f = result.set(this).add(other)

    fun cross(other: Vec3f, result: MutableVec3f): MutableVec3f {
        result.x = y * other.z - z * other.y
        result.y = z * other.x - x * other.z
        result.z = x * other.y - y * other.x
        return result
    }

    fun distance(other: Vec3f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec3f): Float = x * other.x + y * other.y + z * other.z

    fun isEqual(other: Vec3f): Boolean =
            isEqual(x, other.x) && isEqual(y, other.y) && isEqual(z, other.z)

    fun length(): Float = sqrt(sqrLength())

    fun norm(result: MutableVec3f): MutableVec3f = result.set(this).norm()

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

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    operator fun times(other: Vec3f): Float = dot(other)

    override fun toString(): String = "($x, $y, $z)"

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
        get() = xField
        set(value) { xField = value }
    override var y
        get() = yField
        set(value) { yField = value }
    override var z
        get() = zField
        set(value) { zField = value }

    constructor() : this(0f, 0f, 0f)

    constructor(v: Vec3f) : this(v.x, v.y, v.z)

    fun add(other: Vec3f): MutableVec3f {
        x += other.x
        y += other.y
        z += other.z
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

    operator fun set(i: Int, v: Float) {
        when (i) {
            0 -> x = v
            1 -> y = v
            2 -> z = v
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    operator fun timesAssign(factor : Float) { scale(factor) }
}

open class Vec4f(x: Float, y: Float, z: Float, w: Float) {

    // backing fields for properties are declared individual, otherwise overriding the properties and using
    // the super field in mutable sub-class is super-slow in javascript
    protected var xField = x
    protected var yField = y
    protected var zField = z
    protected var wField = w

    open val x get() = xField
    open val y get() = yField
    open val z get() = zField
    open val w get() = wField

    constructor(f: Float) : this(f, f, f, f)

    constructor(v: Vec4f) : this(v.x, v.y, v.z, v.w)

    fun add(other: Vec4f, result: MutableVec4f): MutableVec4f = result.set(this).add(other)

    fun distance(other: Vec4f): Float = sqrt(sqrDistance(other))

    fun dot(other: Vec4f): Float = x * other.x + y * other.y + z * other.z + w * other.w

    fun isEqual(other: Vec4f): Boolean =
        isEqual(x, other.x) && isEqual(y, other.y) && isEqual(z, other.z) && isEqual(w, other.w)

    fun length(): Float = sqrt(sqrLength())

    fun norm(result: MutableVec4f): MutableVec4f = result.set(this).norm()

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

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    override fun toString(): String = "($x, $y, $z, $w)"

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
        get() = xField
        set(value) { xField = value }
    override var y
        get() = yField
        set(value) { yField = value }
    override var z
        get() = zField
        set(value) { zField = value }
    override var w
        get() = wField
        set(value) { wField = value }

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(other: Vec4f) : this(other.x, other.y, other.z, other.w)

    fun add(other: Vec4f): MutableVec4f {
        x += other.x
        y += other.y
        z += other.z
        w += other.w
        return this
    }

    fun norm(): MutableVec4f = scale(1f / length())

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

    fun subtract(other: Vec4f): MutableVec4f {
        x -= other.x
        y -= other.y
        z -= other.z
        w -= other.w
        return this
    }

    operator fun plusAssign(other: Vec4f) { add(other) }

    operator fun minusAssign(other: Vec4f) { subtract(other) }

    operator fun set(i: Int, v: Float) {
        when (i) {
            0 -> x = v
            1 -> y = v
            2 -> z = v
            3 -> w = v
            else -> throw KoolException("Invalid index: " + i)
        }
    }
}
