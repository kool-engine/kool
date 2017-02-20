package de.fabmax.kool.util

import de.fabmax.kool.KoolException

/**
 * @author fabmax
 */

fun add(a: Vec3f, b: Vec3f): MutableVec3f = a.add(MutableVec3f(), b)
fun subtract(a: Vec3f, b: Vec3f): MutableVec3f = a.subtract(MutableVec3f(), b)
fun scale(a: Vec3f, fac: Float): MutableVec3f = a.scale(MutableVec3f(), fac)
fun norm(a: Vec3f): MutableVec3f = a.norm(MutableVec3f())
fun cross(a: Vec3f, b: Vec3f): MutableVec3f = a.cross(MutableVec3f(), b)

open class Vec2f(x: Float, y: Float) {

    constructor(f: Float) : this(f, f)

    open var x = x
        protected set
    open var y = y
        protected set

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    companion object {
        val ZERO = Vec2f(0f)
    }

    override fun toString(): String {
        return "($x, $y)"
    }
}

open class MutableVec2f(x: Float, y: Float) : Vec2f(x, y) {

    override var x: Float
        get() = super.x
        public set(value) { super.x = value }
    override var y: Float
        get() = super.y
        public set(value) { super.y = value }

    constructor() : this(0f, 0f)

    constructor(other: Vec2f) : this(other.x, other.y)

    fun set(x: Float, y: Float): MutableVec2f  {
        this.x = x
        this.y = y
        return this
    }

    fun set(other: Vec2f): MutableVec2f {
        x = other.x
        y = other.y
        return this
    }

    operator fun set(i: Int, v: Float) {
        when (i) {
            0 -> x = v
            1 -> y = v
            else -> throw KoolException("Invalid index: " + i)
        }
    }
}

open class Vec3f(x: Float, y: Float, z: Float) {

    constructor(f: Float) : this(f, f, f)

    open var x = x
        protected set
    open var y = y
        protected set
    open var z = z
        protected set

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    operator fun times(other: Vec3f): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun sqrLength(): Float {
        return x*x + y*y + z*z
    }

    fun length(): Float {
        return Math.sqrt(sqrLength().toDouble()).toFloat()
    }

    fun add(result: MutableVec3f, other: Vec3f): MutableVec3f {
        result.x = x + other.x
        result.y = y + other.y
        result.z = z + other.z
        return result
    }

    fun subtract(result: MutableVec3f, other: Vec3f): MutableVec3f {
        result.x = x - other.x
        result.y = y - other.y
        result.z = z - other.z
        return result
    }

    fun scale(result: MutableVec3f, factor: Float): MutableVec3f {
        result.x = x * factor
        result.y = y * factor
        result.z = z * factor
        return result
    }

    fun norm(result: MutableVec3f): MutableVec3f {
        val lenReciproc = 1f / length()
        result.x = x * lenReciproc
        result.y = y * lenReciproc
        result.z = z * lenReciproc
        return result
    }

    fun dot(other: Vec3f): Float {
        return x * other.x + y * other.y + z * other.z
    }

    fun cross(result: MutableVec3f, other: Vec3f): MutableVec3f {
        result.x = y * other.z - z * other.y
        result.y = z * other.x - x * other.z
        result.z = x * other.x - y * other.x
        return result
    }

    companion object {
        val X_AXIS = Vec3f(1f, 0f, 0f)
        val Y_AXIS = Vec3f(0f, 1f, 0f)
        val Z_AXIS = Vec3f(0f, 0f, 1f)
        val NEG_X_AXIS = Vec3f(-1f, 0f, 0f)
        val NEG_Y_AXIS = Vec3f(0f, -1f, 0f)
        val NEG_Z_AXIS = Vec3f(0f, 0f, -1f)

        val ZERO = Vec3f(0f)
    }

    override fun toString(): String {
        return "($x, $y, $z)"
    }
}

open class MutableVec3f(x: Float, y: Float, z: Float) : Vec3f(x, y, z) {

    override var x: Float
        get() = super.x
        public set(value) { super.x = value }
    override var y: Float
        get() = super.y
        public set(value) { super.y = value }
    override var z: Float
        get() = super.z
        public set(value) { super.z = value }

    constructor() : this(0f, 0f, 0f)

    constructor(other: Vec3f) : this(other.x, other.y, other.z)

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

    fun add(other: Vec3f): MutableVec3f {
        x += other.x
        y += other.y
        z += other.z
        return this
    }

    operator fun plusAssign(other: Vec3f) { add(other) }

    fun subtract(other: Vec3f): MutableVec3f {
        x -= other.x
        y -= other.y
        z -= other.z
        return this
    }

    operator fun minusAssign(other: Vec3f) { subtract(other) }

    fun scale(factor : Float): MutableVec3f {
        x *= factor
        y *= factor
        z *= factor
        return this
    }

    operator fun timesAssign(factor : Float) { scale(factor) }
    operator fun divAssign(div : Float) { scale(1f / div) }

    fun norm(): MutableVec3f {
        scale(1f / length())
        return this
    }

    fun rotate(angleDeg: Float, axis: Vec3f): MutableVec3f {
        return rotate(angleDeg, axis.x, axis.y, axis.z)
    }

    fun rotate(angleDeg: Float, axisX: Float, axisY: Float, axisZ: Float): MutableVec3f {
        val rad = toRad(angleDeg).toDouble()
        val c = Math.cos(rad).toFloat()
        val c1 = 1f - c
        val s = Math.sin(rad).toFloat()

        val tx = x * (axisX * axisX * c1 + c) + y * (axisX * axisY * c1 - axisZ * s) + z * (axisX * axisZ * c1 + axisY * s)
        val ty = x * (axisY * axisX * c1 + axisZ * s) + y * (axisY * axisY * c1 + c) + z * (axisY * axisZ * c1 - axisX * s)
        val tz = x * (axisX * axisZ * c1 - axisY * s) + y * (axisY * axisZ * c1 + axisX * s) + z * (axisZ * axisZ * c1 + c)
        x = tx
        y = ty
        z = tz
        return this
    }

    operator fun set(i: Int, v: Float) {
        when (i) {
            0 -> x = v
            1 -> y = v
            2 -> z = v
            else -> throw KoolException("Invalid index: " + i)
        }
    }
}

open class Vec4f(x: Float, y: Float, z: Float, w: Float) {

    constructor(f: Float) : this(f, f, f, f)

    open var x = x
        protected set
    open var y = y
        protected set
    open var z = z
        protected set
    open var w = w
        protected set

    operator fun get(i: Int): Float {
        return when (i) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw KoolException("Invalid index: " + i)
        }
    }

    companion object {
        val ZERO = Vec4f(0f)
    }

    override fun toString(): String {
        return "($x, $y, $z, $w)"
    }
}

open class MutableVec4f(x: Float, y: Float, z: Float, w: Float) : Vec4f(x, y, z, w) {

    override var x: Float
        get() = super.x
        public set(value) { super.x = value }
    override var y: Float
        get() = super.y
        public set(value) { super.y = value }
    override var z: Float
        get() = super.z
        public set(value) { super.z = value }
    override var w: Float
        get() = super.w
        public set(value) { super.w = value }

    constructor() : this(0f, 0f, 0f, 0f)

    constructor(other: Vec4f) : this(other.x, other.y, other.z, other.w)

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
