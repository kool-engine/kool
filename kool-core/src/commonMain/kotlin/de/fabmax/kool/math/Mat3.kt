package de.fabmax.kool.math

import de.fabmax.kool.toString
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MixedBuffer
import kotlin.math.abs
import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.sqrt

fun Mat3f.toMat3d() = Mat3d(
    m00.toDouble(), m01.toDouble(), m02.toDouble(),
    m10.toDouble(), m11.toDouble(), m12.toDouble(),
    m20.toDouble(), m21.toDouble(), m22.toDouble()
)

fun Mat3f.toMutableMat3d(result: MutableMat3d = MutableMat3d()): MutableMat3d = result.also {
    it.m00 = m00.toDouble(); it.m01 = m01.toDouble(); it.m02 = m02.toDouble()
    it.m10 = m10.toDouble(); it.m11 = m11.toDouble(); it.m12 = m12.toDouble()
    it.m20 = m20.toDouble(); it.m21 = m21.toDouble(); it.m22 = m22.toDouble()
}

fun MutableMat3f.set(that: Mat3d): MutableMat3f {
    m00 = that.m00.toFloat(); m01 = that.m01.toFloat(); m02 = that.m02.toFloat()
    m10 = that.m10.toFloat(); m11 = that.m11.toFloat(); m12 = that.m12.toFloat()
    m20 = that.m20.toFloat(); m21 = that.m21.toFloat(); m22 = that.m22.toFloat()
    return this
}

fun Mat3d.toMat3f() = Mat3f(
    m00.toFloat(), m01.toFloat(), m02.toFloat(),
    m10.toFloat(), m11.toFloat(), m12.toFloat(),
    m20.toFloat(), m21.toFloat(), m22.toFloat()
)

fun Mat3d.toMutableMat3f(result: MutableMat3f= MutableMat3f()): MutableMat3f = result.also {
    it.m00 = m00.toFloat(); it.m01 = m01.toFloat(); it.m02 = m02.toFloat()
    it.m10 = m10.toFloat(); it.m11 = m11.toFloat(); it.m12 = m12.toFloat()
    it.m20 = m20.toFloat(); it.m21 = m21.toFloat(); it.m22 = m22.toFloat()
}

fun MutableMat3d.set(that: Mat3f): MutableMat3d {
    m00 = that.m00.toDouble(); m01 = that.m01.toDouble(); m02 = that.m02.toDouble()
    m10 = that.m10.toDouble(); m11 = that.m11.toDouble(); m12 = that.m12.toDouble()
    m20 = that.m20.toDouble(); m21 = that.m21.toDouble(); m22 = that.m22.toDouble()
    return this
}

/**
 * Transforms (i.e. multiplies) the given [Vec3f] with this matrix and stores the resulting transformed vector
 * in [result].
 */
fun Mat3d.transform(that: Vec3f, result: MutableVec3f): MutableVec3f {
    val x = that.x * m00 + that.y * m01 + that.z * m02
    val y = that.x * m10 + that.y * m11 + that.z * m12
    val z = that.x * m20 + that.y * m21 + that.z * m22
    return result.set(x.toFloat(), y.toFloat(), z.toFloat())
}

/**
 * Transforms (i.e. multiplies) the given [Vec3f] in place with this matrix.
 */
fun Mat3d.transform(that: MutableVec3f): MutableVec3f = transform(that, that)

// <template> Changes made within the template section will also affect the other type variants of this class

open class Mat3f(
    open val m00: Float, open val m01: Float, open val m02: Float,
    open val m10: Float, open val m11: Float, open val m12: Float,
    open val m20: Float, open val m21: Float, open val m22: Float
) {

    constructor(mat: Mat3f): this(
        mat.m00, mat.m01, mat.m02,
        mat.m10, mat.m11, mat.m12,
        mat.m20, mat.m21, mat.m22
    )

    constructor(col0: Vec3f, col1: Vec3f, col2: Vec3f): this(
        col0.x, col1.x, col2.x,
        col0.y, col1.y, col2.y,
        col0.z, col1.z, col2.z
    )

    operator fun component1(): Vec3f = Vec3f(m00, m10, m20)
    operator fun component2(): Vec3f = Vec3f(m01, m11, m21)
    operator fun component3(): Vec3f = Vec3f(m02, m12, m22)

    operator fun times(that: Mat3f): MutableMat3f = mul(that, MutableMat3f())

    operator fun plus(that: Mat3f): MutableMat3f = add(that, MutableMat3f())

    operator fun minus(that: Mat3f): MutableMat3f = subtract(that, MutableMat3f())

    operator fun times(that: Vec3f): MutableVec3f = transform(that, MutableVec3f())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat3f, result: MutableMat3f): MutableMat3f = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat3f, result: MutableMat3f): MutableMat3f = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat3f, result: MutableMat3f): MutableMat3f = result.set(this).mul(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec3f] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec3f, result: MutableVec3f): MutableVec3f {
        val x = that.x * m00 + that.y * m01 + that.z * m02
        val y = that.x * m10 + that.y * m11 + that.z * m12
        val z = that.x * m20 + that.y * m21 + that.z * m22
        return result.set(x, y, z)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec3f] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec3f): MutableVec3f = transform(that, that)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3f.rotate
     */
    fun rotate(angle: AngleF, axis: Vec3f, result: MutableMat3f): MutableMat3f = result.set(this).rotate(angle, axis)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3f.rotate
     */
    fun rotate(quaternion: QuatF, result: MutableMat3f): MutableMat3f = result.set(this).rotate(quaternion)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3f.rotate
     */
    fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, result: MutableMat3f, order: EulerOrder = EulerOrder.ZYX): MutableMat3f {
        return result.set(this).rotate(eulerX, eulerY, eulerZ, order)
    }

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3f.scale
     */
    fun scale(scale: Float, result: MutableMat3f): MutableMat3f = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3f.scale
     */
    fun scale(scale: Vec3f, result: MutableMat3f): MutableMat3f = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat3f): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat3f): MutableMat3f = result.set(this).transpose()

    fun determinant(): Float {
        return m00*m11*m22 + m01*m12*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 - m02*m11*m20
    }

    /**
     * Copies the specified column into a [Vec3f] and returns it.
     */
    operator fun get(col: Int): Vec3f = getColumn(col)

    /**
     * Returns the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun get(row: Int, col: Int): Float {
        return when (row) {
            0 -> when (col) {
                0 -> m00
                1 -> m01
                2 -> m02
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                2 -> m12
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            2 -> when (col) {
                0 -> m20
                1 -> m21
                2 -> m22
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec3f] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec3f = MutableVec3f()): MutableVec3f {
        return when (col) {
            0 -> result.set(m00, m10, m20)
            1 -> result.set(m01, m11, m21)
            2 -> result.set(m02, m12, m22)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec3f] and returns it.
     */
    fun getRow(row: Int, result: MutableVec3f = MutableVec3f()): MutableVec3f {
        return when (row) {
            0 -> result.set(m00, m01, m02)
            1 -> result.set(m10, m11, m12)
            2 -> result.set(m20, m21, m22)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Decomposes this transform matrix into its translation, rotation and scale components and returns them
     * int the provided [rotation] and [scale] vectors.
     */
    fun decompose(rotation: MutableQuatF? = null, scale: MutableVec3f? = null) {
        var sx = sqrt(m00*m00 + m10*m10 + m20*m20)
        val sy = sqrt(m01*m01 + m11*m11 + m21*m21)
        val sz = sqrt(m02*m02 + m12*m12 + m22*m22)
        if (determinant() < 0f) {
            sx *= -1f
        }
        scale?.set(sx, sy, sz)

        if (rotation != null) {
            val r00 = m00 / sx; val r01 = m01 / sy; val r02 = m02 / sz
            val r10 = m10 / sx; val r11 = m11 / sy; val r12 = m12 / sz
            val r20 = m20 / sx; val r21 = m21 / sy; val r22 = m22 / sz

            val trace = r00 + r11 + r22
            if (trace > 0f) {
                val s = 0.5f / sqrt(trace + 1f)
                rotation.set((r21 - r12) * s, (r02 - r20) * s, (r10 - r01) * s, 0.25f / s)
            } else {
                if (r00 < r11) {
                    if (r11 < r22) {
                        var s = 0.5f / sqrt(r22 - r00 - r11 + 1f)
                        if (r10 < r01) s = -s   // ensure non-negative w
                        rotation.set((r02 + r20) * s, (r12 + r21) * s, 0.25f / s, (r10 - r01) * s)
                    } else {
                        var s = 0.5f / sqrt(r11 - r22 - r00 + 1f)
                        if (r02 < r20) s = -s   // ensure non-negative w
                        rotation.set((r01 + r10) * s, 0.25f / s, (r21 + r12) * s, (r02 - r20) * s)
                    }
                } else {
                    if (r00 < r22) {
                        var s = 0.5f / sqrt(r22 - r00 - r11 + 1f)
                        if (r10 < r01) s = -s   // ensure non-negative w
                        rotation.set((r02 + r20) * s, (r12 + r21) * s, 0.25f / s, (r10 - r01) * s)
                    } else {
                        var s = 0.5f / sqrt(r00 - r11 - r22 + 1f)
                        if (r21 < r12) s = -s   // ensure non-negative w
                        rotation.set(0.25f / s, (r10 + r01) * s, (r20 + r02) * s, (r21 - r12) * s)
                    }
                }
            }
            rotation.norm()
        }
    }

    /**
     * Returns the decomposed rotation component of this transform matrix.
     *
     * @see decompose
     */
    fun getRotation(result: MutableQuatF = MutableQuatF()): MutableQuatF {
        decompose(rotation = result)
        return result
    }

    /**
     * Returns the decomposed scale component of this transform matrix.
     *
     * @see decompose
     */
    fun getScale(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        decompose(scale = result)
        return result
    }

    /**
     * Returns the euler angle representation of this matrix's rotation component. Angles are returned in degrees
     * in the given [eulersDeg] vector. Euler angle order can bes set via [order], default is ZYX.
     */
    fun getEulerAngles(eulersDeg: MutableVec3f = MutableVec3f(), order: EulerOrder = EulerOrder.ZYX): MutableVec3f {
        var sx = sqrt(m00*m00 + m10*m10 + m20*m20)
        val sy = sqrt(m01*m01 + m11*m11 + m21*m21)
        val sz = sqrt(m02*m02 + m12*m12 + m22*m22)
        if (determinant() < 0f) {
            sx *= -1f
        }

        val r00 = m00 / sx; val r01 = m01 / sy; val r02 = m02 / sz
        val r10 = m10 / sx; val r11 = m11 / sy; val r12 = m12 / sz
        val r20 = m20 / sx; val r21 = m21 / sy; val r22 = m22 / sz

        when (order) {
            EulerOrder.XYZ -> {
                eulersDeg.y = asin(r02.clamp(-1f, 1f)).toDeg()
                if (abs(r02) < 0.9999999f) {
                    eulersDeg.x = atan2(-r12, r22).toDeg()
                    eulersDeg.z = atan2(-r01, r00).toDeg()
                } else {
                    eulersDeg.x = atan2(r21, r11).toDeg()
                    eulersDeg.z = 0f
                }
            }
            EulerOrder.XZY -> {
                eulersDeg.z = asin(-r01.clamp(-1f, 1f)).toDeg()
                if (abs(r01) < 0.9999999f) {
                    eulersDeg.x = atan2(r21, r11).toDeg()
                    eulersDeg.y = atan2(r02, r00).toDeg()
                } else {
                    eulersDeg.x = atan2(-r12, r22).toDeg()
                    eulersDeg.y = 0f
                }
            }
            EulerOrder.YXZ -> {
                eulersDeg.x = asin(-r12.clamp(-1f, 1f)).toDeg()
                if (abs(r12) < 0.9999999f) {
                    eulersDeg.y = atan2(r02, r22).toDeg()
                    eulersDeg.z = atan2(r10, r11).toDeg()
                } else {
                    eulersDeg.y = atan2(-r20, r00).toDeg()
                    eulersDeg.z = 0f
                }
            }
            EulerOrder.YZX -> {
                eulersDeg.z = asin(r10.clamp(-1f, 1f)).toDeg()
                if (abs(r10) < 0.9999999f) {
                    eulersDeg.x = atan2(-r12, r11).toDeg()
                    eulersDeg.y = atan2(-r20, r00).toDeg()
                } else {
                    eulersDeg.x = 0f
                    eulersDeg.y = atan2(r02, r22).toDeg()
                }
            }
            EulerOrder.ZXY -> {
                eulersDeg.x = asin(r21.clamp(-1f, 1f)).toDeg()
                if (abs(r21) < 0.9999999f) {
                    eulersDeg.y = atan2(-r20, r22).toDeg()
                    eulersDeg.z = atan2(-r01, r11).toDeg()
                } else {
                    eulersDeg.y = 0f
                    eulersDeg.z = atan2(r10, r00).toDeg()
                }
            }
            EulerOrder.ZYX -> {
                eulersDeg.y = asin(-r20.clamp(-1f, 1f)).toDeg()
                if (abs(r20) < 0.9999999f) {
                    eulersDeg.x = atan2(r21, r22).toDeg()
                    eulersDeg.z = atan2(r10, r00).toDeg()
                } else {
                    eulersDeg.x = 0f
                    eulersDeg.z = atan2(-r01, r11).toDeg()
                }
            }
        }

        return eulersDeg
    }

    /**
     * Appends the components of this matrix to the given [Float32Buffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: Float32Buffer, withPadding: Boolean = true) {
        target.put(m00); target.put(m10); target.put(m20)
        if (withPadding) target.put(0f)
        target.put(m01); target.put(m11); target.put(m21)
        if (withPadding) target.put(0f)
        target.put(m02); target.put(m12); target.put(m22)
        if (withPadding) target.put(0f)
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: MixedBuffer, withPadding: Boolean = true) {
        target.putFloat32(m00); target.putFloat32(m10); target.putFloat32(m20)
        if (withPadding) target.putFloat32(0f)
        target.putFloat32(m01); target.putFloat32(m11); target.putFloat32(m21)
        if (withPadding) target.putFloat32(0f)
        target.putFloat32(m02); target.putFloat32(m12); target.putFloat32(m22)
        if (withPadding) target.putFloat32(0f)
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}, ${m02.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}, ${m12.toString(precision).padStart(width)}]\n")
        append("[${m20.toString(precision).padStart(width)}, ${m21.toString(precision).padStart(width)}, ${m22.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10, $m20), col1: ($m01, $m11, $m21), col2: ($m02, $m12, $m22) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat3f, eps: Float = FUZZY_EQ_F): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) && isFuzzyEqual(m02, that.m02, eps) &&
               isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps) && isFuzzyEqual(m12, that.m12, eps) &&
               isFuzzyEqual(m20, that.m20, eps) && isFuzzyEqual(m21, that.m21, eps) && isFuzzyEqual(m22, that.m22, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat3f) return false
        return m00 == other.m00 && m01 == other.m01 && m02 == other.m02 &&
               m10 == other.m10 && m11 == other.m11 && m12 == other.m12 &&
               m20 == other.m20 && m21 == other.m21 && m22 == other.m22
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m02.hashCode()

        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        result = 31 * result + m12.hashCode()

        result = 31 * result + m20.hashCode()
        result = 31 * result + m21.hashCode()
        result = 31 * result + m22.hashCode()

        return result
    }

    companion object {
        val IDENTITY = Mat3f(
            1f, 0f, 0f,
            0f, 1f, 0f,
            0f, 0f, 1f
        )

        val ZERO = Mat3f(
            0f, 0f, 0f,
            0f, 0f, 0f,
            0f, 0f, 0f
        )

        fun fromArray(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat3f {
            return MutableMat3f().set(array, offset, order)
        }

        fun rotation(angle: AngleF, axis: Vec3f): Mat3f = MutableMat3f().rotate(angle, axis)

        fun rotation(quaternion: QuatF): Mat3f = MutableMat3f().rotate(quaternion)

        fun rotation(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, order: EulerOrder = EulerOrder.ZYX): Mat3f {
            return MutableMat3f().rotate(eulerX, eulerY, eulerZ, order)
        }

        fun scale(s: Float): Mat3f = MutableMat3f().scale(s)

        fun scale(s: Vec3f): Mat3f = MutableMat3f().scale(s)

        fun composition(rotation: QuatF, scale: Vec3f): Mat3f {
            return MutableMat3f().compose(rotation, scale)
        }
    }
}

open class MutableMat3f(
    override var m00: Float, override var m01: Float, override var m02: Float,
    override var m10: Float, override var m11: Float, override var m12: Float,
    override var m20: Float, override var m21: Float, override var m22: Float
) : Mat3f(
    m00, m01, m02,
    m10, m11, m12,
    m20, m21, m22
) {

    constructor(mat: Mat3f): this(
        mat.m00, mat.m01, mat.m02,
        mat.m10, mat.m11, mat.m12,
        mat.m20, mat.m21, mat.m22
    )

    constructor(col0: Vec3f, col1: Vec3f, col2: Vec3f): this(
        col0.x, col1.x, col2.x,
        col0.y, col1.y, col2.y,
        col0.z, col1.z, col2.z
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat3f): MutableMat3f {
        m00 = that.m00; m01 = that.m01; m02 = that.m02
        m10 = that.m10; m11 = that.m11; m12 = that.m12
        m20 = that.m20; m21 = that.m21; m22 = that.m22
        return this
    }

    fun set(col0: Vec3f, col1: Vec3f, col2: Vec3f): MutableMat3f {
        m00 = col0.x; m01 = col1.x; m02 = col2.x
        m10 = col0.y; m11 = col1.y; m12 = col2.y
        m20 = col0.z; m21 = col1.z; m22 = col2.z
        return this
    }

    fun set(
        t00: Float, t01: Float, t02: Float,
        t10: Float, t11: Float, t12: Float,
        t20: Float, t21: Float, t22: Float
    ): MutableMat3f {
        m00 = t00; m01 = t01; m02 = t02
        m10 = t10; m11 = t11; m12 = t12
        m20 = t20; m21 = t21; m22 = t22
        return this
    }

    fun set(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat3f {
        var i = offset
        m00 = array[i++]; m01 = array[i++]; m02 = array[i++]
        m10 = array[i++]; m11 = array[i++]; m12 = array[i++]
        m20 = array[i++]; m21 = array[i++]; m22 = array[i]
        if (order == MatrixArrayOrder.COLUMN_MAJOR) {
            transpose()
        }
        return this
    }

    fun setIdentity() = set(IDENTITY)

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    operator fun plusAssign(that: Mat3f) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat3f) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat3f) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat3f): MutableMat3f {
        m00 += that.m00; m01 += that.m01; m02 += that.m02
        m10 += that.m10; m11 += that.m11; m12 += that.m12
        m20 += that.m20; m21 += that.m21; m22 += that.m22
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat3f): MutableMat3f {
        m00 -= that.m00; m01 -= that.m01; m02 -= that.m02
        m10 -= that.m10; m11 -= that.m11; m12 -= that.m12
        m20 -= that.m20; m21 -= that.m21; m22 -= that.m22
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat3f): MutableMat3f = mul(
        that.m00, that.m01, that.m02,
        that.m10, that.m11, that.m12,
        that.m20, that.m21, that.m22
    )

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul(
        t00: Float, t01: Float, t02: Float,
        t10: Float, t11: Float, t12: Float,
        t20: Float, t21: Float, t22: Float
    ): MutableMat3f {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20
        val r10 = m10 * t00 + m11 * t10 + m12 * t20
        val r20 = m20 * t00 + m21 * t10 + m22 * t20

        val r01 = m00 * t01 + m01 * t11 + m02 * t21
        val r11 = m10 * t01 + m11 * t11 + m12 * t21
        val r21 = m20 * t01 + m21 * t11 + m22 * t21

        val r02 = m00 * t02 + m01 * t12 + m02 * t22
        val r12 = m10 * t02 + m11 * t12 + m12 * t22
        val r22 = m20 * t02 + m21 * t12 + m22 * t22

        m00 = r00; m01 = r01; m02 = r02
        m10 = r10; m11 = r11; m12 = r12
        m20 = r20; m21 = r21; m22 = r22
        return this
    }

    /**
     * Applies the composition of the given [rotation] and [scale] to this transform matrix.
     */
    fun compose(rotation: QuatF, scale: Vec3f): MutableMat3f {
        val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w
        val x2 = x + x;  val y2 = y + y;  val z2 = z + z
        val xx = x * x2; val xy = x * y2; val xz = x * z2
        val yy = y * y2; val yz = y * z2; val zz = z * z2
        val wx = w * x2; val wy = w * y2; val wz = w * z2

        val sx = scale.x; val sy = scale.y; val sz = scale.z

        return mul(
            (1 - (yy + zz)) * sx,   (xy - wz) * sy,         (xz + wy) * sz,
            (xy + wz) * sx,         (1 - (xx + zz)) * sy,   (yz - wx) * sz,
            (xz - wy) * sx,         (yz + wx) * sy,         (1 - (xx + yy)) * sz
        )
    }

    /**
     * Inplace operation: Rotates this matrix around the given [axis] by the given [angle].
     */
    fun rotate(angle: AngleF, axis: Vec3f): MutableMat3f {
        val t00: Float
        val t01: Float
        val t02: Float
        val t10: Float
        val t11: Float
        val t12: Float
        val t20: Float
        val t21: Float
        val t22: Float

        val s = angle.sin
        val c = angle.cos
        if (axis.x > 0f && axis.y == 0f && axis.z == 0f) {
            // positive x-axis rotation - fast version
            t00 = 1f; t01 = 0f; t02 = 0f
            t10 = 0f; t11 = c;  t12 = -s
            t20 = 0f; t21 = s;  t22 = c

        } else if (axis.x == 0f && axis.y > 0f && axis.z == 0f) {
            // positive y-axis rotation - fast version
            t00 = c;  t01 = 0f; t02 = s
            t10 = 0f; t11 = 1f; t12 = 0f
            t20 = -s; t21 = 0f; t22 = c

        } else if (axis.x == 0f && axis.y == 0f && axis.z > 0f) {
            // positive z-axis rotation - fast version
            t00 = c;  t01 = -s; t02 = 0f
            t10 = s;  t11 = c;  t12 = 0f
            t20 = 0f; t21 = 0f; t22 = 1f

        } else {
            // general case
            val recipLen = 1.0f / axis.length()
            val x = axis.x * recipLen
            val y = axis.y * recipLen
            val z = axis.z * recipLen

            val nc = 1.0f - c
            val xy = x * y
            val yz = y * z
            val zx = z * x
            val xs = x * s
            val ys = y * s
            val zs = z * s

            t00 = x * x * nc + c
            t01 = xy * nc - zs
            t02 = zx * nc + ys

            t10 = xy * nc + zs
            t11 = y * y * nc + c
            t12 = yz * nc - xs

            t20 = zx * nc - ys
            t21 = yz * nc + xs
            t22 = z * z * nc + c
        }

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given quaternion rotation. [quaternion] is expected to be a valid
     * rotation quaternion with unit length.
     */
    fun rotate(quaternion: QuatF): MutableMat3f {
        val r = quaternion.w
        val i = quaternion.x
        val j = quaternion.y
        val k = quaternion.z

        val t00 = 1 - 2 * (j*j + k*k)
        val t01 = 2 * (i*j - k*r)
        val t02 = 2 * (i*k + j*r)

        val t10 = 2 * (i*j + k*r)
        val t11 = 1 - 2 * (i*i + k*k)
        val t12 = 2 * (j*k - i*r)

        val t20 = 2 * (i*k - j*r)
        val t21 = 2 * (j*k + i*r)
        val t22 = 1 - 2 * (i*i + j*j)

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given euler angles.
     */
    fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, order: EulerOrder = EulerOrder.ZYX): MutableMat3f {
        val a = eulerX.cos
        val b = eulerX.sin
        val c = eulerY.cos
        val d = eulerY.sin
        val e = eulerZ.cos
        val f = eulerZ.sin

        val t00: Float
        val t01: Float
        val t02: Float
        val t10: Float
        val t11: Float
        val t12: Float
        val t20: Float
        val t21: Float
        val t22: Float

        when (order) {
            EulerOrder.XYZ -> {
                val ae = a * e; val af = a * f; val be = b * e; val bf = b * f

                t00 = c * e;        t01 = -c * f;       t02 = d
                t10 = af + be * d;  t11 = ae - bf * d;  t12 = - b * c
                t20 = bf - ae * d;  t21 = be + af * d;  t22 = a * c
            }
            EulerOrder.XZY -> {
                val ac = a * c; val ad = a * d; val bc = b * c; val bd = b * d

                t00 = c * e;        t01 = - f;          t02 = d * e
                t10 = ac * f + bd;  t11 = a * e;        t12 = ad * f - bc
                t20 = bc * f - ad;  t21 = b * e;        t22 = bd * f + ac
            }
            EulerOrder.YXZ -> {
                val ce = c * e; val cf = c * f; val de = d * e; val df = d * f

                t00 = ce + df * b;  t01 = de * b - cf;  t02 = a * d
                t10 = a * f;        t11 = a * e;        t12 = - b
                t20 = cf * b - de;  t21 = df + ce * b;  t22 = a * c
            }
            EulerOrder.YZX -> {
                val ac = a * c; val ad = a * d; val bc = b * c; val bd = b * d

                t00 = c * e;        t01 = bd - ac * f;  t02 = bc * f + ad
                t10 = f;            t11 = a * e;        t12 = - b * e
                t20 = - d * e;      t21 = ad * f + bc;  t22 = ac - bd * f
            }
            EulerOrder.ZXY -> {
                val ce = c * e; val cf = c * f; val de = d * e; val df = d * f

                t00 = ce - df * b;  t01 = - a * f;      t02 = de + cf * b
                t10 = cf + de * b;  t11 = a * e;        t12 = df - ce * b
                t20 = - a * d;      t21 = b;            t22 = a * c
            }
            EulerOrder.ZYX -> {
                val ae = a * e; val af = a * f; val be = b * e; val bf = b * f

                t00 = c * e;        t01 = be * d - af;  t02 = ae * d + bf
                t10 = c * f;        t11 = bf * d + ae;  t12 = af * d - be
                t20 = - d;          t21 = b * c;        t22 = a * c
            }
        }

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Float): MutableMat3f = mul(
        s, 0f, 0f,
        0f, s, 0f,
        0f, 0f, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec3f): MutableMat3f = mul(
        s.x, 0f, 0f,
        0f, s.y, 0f,
        0f, 0f, s.z
    )

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/index.htm
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Float = 0f): Boolean {
        val det = determinant()
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val t00 = m11*m22 - m12*m21
        val t01 = m02*m21 - m01*m22
        val t02 = m01*m12 - m02*m11
        val t10 = m12*m20 - m10*m22
        val t11 = m00*m22 - m02*m20
        val t12 = m02*m10 - m00*m12
        val t20 = m10*m21 - m11*m20
        val t21 = m01*m20 - m00*m21
        val t22 = m00*m11 - m01*m10

        val s = 1f / det
        m00 = t00 * s; m01 = t01 * s; m02 = t02 * s
        m10 = t10 * s; m11 = t11 * s; m12 = t12 * s
        m20 = t20 * s; m21 = t21 * s; m22 = t22 * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat3f {
        var t = m01; m01 = m10; m10 = t
        t = m02; m02 = m20; m20 = t
        t = m12; m12 = m21; m21 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec3f].
     */
    operator fun set(col: Int, that: Vec3f) {
        setColumn(col, that)
    }

    /**
     * Sets the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun set(row: Int, col: Int, that: Float) {
        when (row) {
            0 -> when (col) {
                0 -> m00 = that
                1 -> m01 = that
                2 -> m02 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                2 -> m12 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            2 -> when (col) {
                0 -> m20 = that
                1 -> m21 = that
                2 -> m22 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Sets the specified column to the given [Vec3f].
     */
    fun setColumn(col: Int, that: Vec3f): MutableMat3f {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec3f].
     */
    fun setRow(row: Int, that: Vec3f): MutableMat3f {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


open class Mat3d(
    open val m00: Double, open val m01: Double, open val m02: Double,
    open val m10: Double, open val m11: Double, open val m12: Double,
    open val m20: Double, open val m21: Double, open val m22: Double
) {

    constructor(mat: Mat3d): this(
        mat.m00, mat.m01, mat.m02,
        mat.m10, mat.m11, mat.m12,
        mat.m20, mat.m21, mat.m22
    )

    constructor(col0: Vec3d, col1: Vec3d, col2: Vec3d): this(
        col0.x, col1.x, col2.x,
        col0.y, col1.y, col2.y,
        col0.z, col1.z, col2.z
    )

    operator fun component1(): Vec3d = Vec3d(m00, m10, m20)
    operator fun component2(): Vec3d = Vec3d(m01, m11, m21)
    operator fun component3(): Vec3d = Vec3d(m02, m12, m22)

    operator fun times(that: Mat3d): MutableMat3d = mul(that, MutableMat3d())

    operator fun plus(that: Mat3d): MutableMat3d = add(that, MutableMat3d())

    operator fun minus(that: Mat3d): MutableMat3d = subtract(that, MutableMat3d())

    operator fun times(that: Vec3d): MutableVec3d = transform(that, MutableVec3d())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat3d, result: MutableMat3d): MutableMat3d = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat3d, result: MutableMat3d): MutableMat3d = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat3d, result: MutableMat3d): MutableMat3d = result.set(this).mul(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec3d] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec3d, result: MutableVec3d): MutableVec3d {
        val x = that.x * m00 + that.y * m01 + that.z * m02
        val y = that.x * m10 + that.y * m11 + that.z * m12
        val z = that.x * m20 + that.y * m21 + that.z * m22
        return result.set(x, y, z)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec3d] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec3d): MutableVec3d = transform(that, that)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3d.rotate
     */
    fun rotate(angle: AngleD, axis: Vec3d, result: MutableMat3d): MutableMat3d = result.set(this).rotate(angle, axis)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3d.rotate
     */
    fun rotate(quaternion: QuatD, result: MutableMat3d): MutableMat3d = result.set(this).rotate(quaternion)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3d.rotate
     */
    fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, result: MutableMat3d, order: EulerOrder = EulerOrder.ZYX): MutableMat3d {
        return result.set(this).rotate(eulerX, eulerY, eulerZ, order)
    }

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3d.scale
     */
    fun scale(scale: Double, result: MutableMat3d): MutableMat3d = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat3d.scale
     */
    fun scale(scale: Vec3d, result: MutableMat3d): MutableMat3d = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat3d): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat3d): MutableMat3d = result.set(this).transpose()

    fun determinant(): Double {
        return m00*m11*m22 + m01*m12*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 - m02*m11*m20
    }

    /**
     * Copies the specified column into a [Vec3d] and returns it.
     */
    operator fun get(col: Int): Vec3d = getColumn(col)

    /**
     * Returns the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun get(row: Int, col: Int): Double {
        return when (row) {
            0 -> when (col) {
                0 -> m00
                1 -> m01
                2 -> m02
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                2 -> m12
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            2 -> when (col) {
                0 -> m20
                1 -> m21
                2 -> m22
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec3d] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec3d = MutableVec3d()): MutableVec3d {
        return when (col) {
            0 -> result.set(m00, m10, m20)
            1 -> result.set(m01, m11, m21)
            2 -> result.set(m02, m12, m22)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec3d] and returns it.
     */
    fun getRow(row: Int, result: MutableVec3d = MutableVec3d()): MutableVec3d {
        return when (row) {
            0 -> result.set(m00, m01, m02)
            1 -> result.set(m10, m11, m12)
            2 -> result.set(m20, m21, m22)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Decomposes this transform matrix into its translation, rotation and scale components and returns them
     * int the provided [rotation] and [scale] vectors.
     */
    fun decompose(rotation: MutableQuatD? = null, scale: MutableVec3d? = null) {
        var sx = sqrt(m00*m00 + m10*m10 + m20*m20)
        val sy = sqrt(m01*m01 + m11*m11 + m21*m21)
        val sz = sqrt(m02*m02 + m12*m12 + m22*m22)
        if (determinant() < 0.0) {
            sx *= -1.0
        }
        scale?.set(sx, sy, sz)

        if (rotation != null) {
            val r00 = m00 / sx; val r01 = m01 / sy; val r02 = m02 / sz
            val r10 = m10 / sx; val r11 = m11 / sy; val r12 = m12 / sz
            val r20 = m20 / sx; val r21 = m21 / sy; val r22 = m22 / sz

            val trace = r00 + r11 + r22
            if (trace > 0.0) {
                val s = 0.5 / sqrt(trace + 1.0)
                rotation.set((r21 - r12) * s, (r02 - r20) * s, (r10 - r01) * s, 0.25 / s)
            } else {
                if (r00 < r11) {
                    if (r11 < r22) {
                        var s = 0.5 / sqrt(r22 - r00 - r11 + 1.0)
                        if (r10 < r01) s = -s   // ensure non-negative w
                        rotation.set((r02 + r20) * s, (r12 + r21) * s, 0.25 / s, (r10 - r01) * s)
                    } else {
                        var s = 0.5 / sqrt(r11 - r22 - r00 + 1.0)
                        if (r02 < r20) s = -s   // ensure non-negative w
                        rotation.set((r01 + r10) * s, 0.25 / s, (r21 + r12) * s, (r02 - r20) * s)
                    }
                } else {
                    if (r00 < r22) {
                        var s = 0.5 / sqrt(r22 - r00 - r11 + 1.0)
                        if (r10 < r01) s = -s   // ensure non-negative w
                        rotation.set((r02 + r20) * s, (r12 + r21) * s, 0.25 / s, (r10 - r01) * s)
                    } else {
                        var s = 0.5 / sqrt(r00 - r11 - r22 + 1.0)
                        if (r21 < r12) s = -s   // ensure non-negative w
                        rotation.set(0.25 / s, (r10 + r01) * s, (r20 + r02) * s, (r21 - r12) * s)
                    }
                }
            }
            rotation.norm()
        }
    }

    /**
     * Returns the decomposed rotation component of this transform matrix.
     *
     * @see decompose
     */
    fun getRotation(result: MutableQuatD = MutableQuatD()): MutableQuatD {
        decompose(rotation = result)
        return result
    }

    /**
     * Returns the decomposed scale component of this transform matrix.
     *
     * @see decompose
     */
    fun getScale(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        decompose(scale = result)
        return result
    }

    /**
     * Returns the euler angle representation of this matrix's rotation component. Angles are returned in degrees
     * in the given [eulersDeg] vector. Euler angle order can bes set via [order], default is ZYX.
     */
    fun getEulerAngles(eulersDeg: MutableVec3d = MutableVec3d(), order: EulerOrder = EulerOrder.ZYX): MutableVec3d {
        var sx = sqrt(m00*m00 + m10*m10 + m20*m20)
        val sy = sqrt(m01*m01 + m11*m11 + m21*m21)
        val sz = sqrt(m02*m02 + m12*m12 + m22*m22)
        if (determinant() < 0.0) {
            sx *= -1.0
        }

        val r00 = m00 / sx; val r01 = m01 / sy; val r02 = m02 / sz
        val r10 = m10 / sx; val r11 = m11 / sy; val r12 = m12 / sz
        val r20 = m20 / sx; val r21 = m21 / sy; val r22 = m22 / sz

        when (order) {
            EulerOrder.XYZ -> {
                eulersDeg.y = asin(r02.clamp(-1.0, 1.0)).toDeg()
                if (abs(r02) < 0.9999998807907104) {
                    eulersDeg.x = atan2(-r12, r22).toDeg()
                    eulersDeg.z = atan2(-r01, r00).toDeg()
                } else {
                    eulersDeg.x = atan2(r21, r11).toDeg()
                    eulersDeg.z = 0.0
                }
            }
            EulerOrder.XZY -> {
                eulersDeg.z = asin(-r01.clamp(-1.0, 1.0)).toDeg()
                if (abs(r01) < 0.9999998807907104) {
                    eulersDeg.x = atan2(r21, r11).toDeg()
                    eulersDeg.y = atan2(r02, r00).toDeg()
                } else {
                    eulersDeg.x = atan2(-r12, r22).toDeg()
                    eulersDeg.y = 0.0
                }
            }
            EulerOrder.YXZ -> {
                eulersDeg.x = asin(-r12.clamp(-1.0, 1.0)).toDeg()
                if (abs(r12) < 0.9999998807907104) {
                    eulersDeg.y = atan2(r02, r22).toDeg()
                    eulersDeg.z = atan2(r10, r11).toDeg()
                } else {
                    eulersDeg.y = atan2(-r20, r00).toDeg()
                    eulersDeg.z = 0.0
                }
            }
            EulerOrder.YZX -> {
                eulersDeg.z = asin(r10.clamp(-1.0, 1.0)).toDeg()
                if (abs(r10) < 0.9999998807907104) {
                    eulersDeg.x = atan2(-r12, r11).toDeg()
                    eulersDeg.y = atan2(-r20, r00).toDeg()
                } else {
                    eulersDeg.x = 0.0
                    eulersDeg.y = atan2(r02, r22).toDeg()
                }
            }
            EulerOrder.ZXY -> {
                eulersDeg.x = asin(r21.clamp(-1.0, 1.0)).toDeg()
                if (abs(r21) < 0.9999998807907104) {
                    eulersDeg.y = atan2(-r20, r22).toDeg()
                    eulersDeg.z = atan2(-r01, r11).toDeg()
                } else {
                    eulersDeg.y = 0.0
                    eulersDeg.z = atan2(r10, r00).toDeg()
                }
            }
            EulerOrder.ZYX -> {
                eulersDeg.y = asin(-r20.clamp(-1.0, 1.0)).toDeg()
                if (abs(r20) < 0.9999998807907104) {
                    eulersDeg.x = atan2(r21, r22).toDeg()
                    eulersDeg.z = atan2(r10, r00).toDeg()
                } else {
                    eulersDeg.x = 0.0
                    eulersDeg.z = atan2(-r01, r11).toDeg()
                }
            }
        }

        return eulersDeg
    }

    /**
     * Appends the components of this matrix to the given [Float32Buffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: Float32Buffer, withPadding: Boolean = true) {
        target.put(m00); target.put(m10); target.put(m20)
        if (withPadding) target.put(0.0)
        target.put(m01); target.put(m11); target.put(m21)
        if (withPadding) target.put(0.0)
        target.put(m02); target.put(m12); target.put(m22)
        if (withPadding) target.put(0.0)
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: MixedBuffer, withPadding: Boolean = true) {
        target.putFloat32(m00); target.putFloat32(m10); target.putFloat32(m20)
        if (withPadding) target.putFloat32(0.0)
        target.putFloat32(m01); target.putFloat32(m11); target.putFloat32(m21)
        if (withPadding) target.putFloat32(0.0)
        target.putFloat32(m02); target.putFloat32(m12); target.putFloat32(m22)
        if (withPadding) target.putFloat32(0.0)
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}, ${m02.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}, ${m12.toString(precision).padStart(width)}]\n")
        append("[${m20.toString(precision).padStart(width)}, ${m21.toString(precision).padStart(width)}, ${m22.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10, $m20), col1: ($m01, $m11, $m21), col2: ($m02, $m12, $m22) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat3d, eps: Double = FUZZY_EQ_D): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) && isFuzzyEqual(m02, that.m02, eps) &&
               isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps) && isFuzzyEqual(m12, that.m12, eps) &&
               isFuzzyEqual(m20, that.m20, eps) && isFuzzyEqual(m21, that.m21, eps) && isFuzzyEqual(m22, that.m22, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat3d) return false
        return m00 == other.m00 && m01 == other.m01 && m02 == other.m02 &&
               m10 == other.m10 && m11 == other.m11 && m12 == other.m12 &&
               m20 == other.m20 && m21 == other.m21 && m22 == other.m22
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m02.hashCode()

        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        result = 31 * result + m12.hashCode()

        result = 31 * result + m20.hashCode()
        result = 31 * result + m21.hashCode()
        result = 31 * result + m22.hashCode()

        return result
    }

    companion object {
        val IDENTITY = Mat3d(
            1.0, 0.0, 0.0,
            0.0, 1.0, 0.0,
            0.0, 0.0, 1.0
        )

        val ZERO = Mat3d(
            0.0, 0.0, 0.0,
            0.0, 0.0, 0.0,
            0.0, 0.0, 0.0
        )

        fun fromArray(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat3d {
            return MutableMat3d().set(array, offset, order)
        }

        fun rotation(angle: AngleD, axis: Vec3d): Mat3d = MutableMat3d().rotate(angle, axis)

        fun rotation(quaternion: QuatD): Mat3d = MutableMat3d().rotate(quaternion)

        fun rotation(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, order: EulerOrder = EulerOrder.ZYX): Mat3d {
            return MutableMat3d().rotate(eulerX, eulerY, eulerZ, order)
        }

        fun scale(s: Double): Mat3d = MutableMat3d().scale(s)

        fun scale(s: Vec3d): Mat3d = MutableMat3d().scale(s)

        fun composition(rotation: QuatD, scale: Vec3d): Mat3d {
            return MutableMat3d().compose(rotation, scale)
        }
    }
}

open class MutableMat3d(
    override var m00: Double, override var m01: Double, override var m02: Double,
    override var m10: Double, override var m11: Double, override var m12: Double,
    override var m20: Double, override var m21: Double, override var m22: Double
) : Mat3d(
    m00, m01, m02,
    m10, m11, m12,
    m20, m21, m22
) {

    constructor(mat: Mat3d): this(
        mat.m00, mat.m01, mat.m02,
        mat.m10, mat.m11, mat.m12,
        mat.m20, mat.m21, mat.m22
    )

    constructor(col0: Vec3d, col1: Vec3d, col2: Vec3d): this(
        col0.x, col1.x, col2.x,
        col0.y, col1.y, col2.y,
        col0.z, col1.z, col2.z
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat3d): MutableMat3d {
        m00 = that.m00; m01 = that.m01; m02 = that.m02
        m10 = that.m10; m11 = that.m11; m12 = that.m12
        m20 = that.m20; m21 = that.m21; m22 = that.m22
        return this
    }

    fun set(col0: Vec3d, col1: Vec3d, col2: Vec3d): MutableMat3d {
        m00 = col0.x; m01 = col1.x; m02 = col2.x
        m10 = col0.y; m11 = col1.y; m12 = col2.y
        m20 = col0.z; m21 = col1.z; m22 = col2.z
        return this
    }

    fun set(
        t00: Double, t01: Double, t02: Double,
        t10: Double, t11: Double, t12: Double,
        t20: Double, t21: Double, t22: Double
    ): MutableMat3d {
        m00 = t00; m01 = t01; m02 = t02
        m10 = t10; m11 = t11; m12 = t12
        m20 = t20; m21 = t21; m22 = t22
        return this
    }

    fun set(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat3d {
        var i = offset
        m00 = array[i++]; m01 = array[i++]; m02 = array[i++]
        m10 = array[i++]; m11 = array[i++]; m12 = array[i++]
        m20 = array[i++]; m21 = array[i++]; m22 = array[i]
        if (order == MatrixArrayOrder.COLUMN_MAJOR) {
            transpose()
        }
        return this
    }

    fun setIdentity() = set(IDENTITY)

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    operator fun plusAssign(that: Mat3d) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat3d) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat3d) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat3d): MutableMat3d {
        m00 += that.m00; m01 += that.m01; m02 += that.m02
        m10 += that.m10; m11 += that.m11; m12 += that.m12
        m20 += that.m20; m21 += that.m21; m22 += that.m22
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat3d): MutableMat3d {
        m00 -= that.m00; m01 -= that.m01; m02 -= that.m02
        m10 -= that.m10; m11 -= that.m11; m12 -= that.m12
        m20 -= that.m20; m21 -= that.m21; m22 -= that.m22
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat3d): MutableMat3d = mul(
        that.m00, that.m01, that.m02,
        that.m10, that.m11, that.m12,
        that.m20, that.m21, that.m22
    )

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul(
        t00: Double, t01: Double, t02: Double,
        t10: Double, t11: Double, t12: Double,
        t20: Double, t21: Double, t22: Double
    ): MutableMat3d {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20
        val r10 = m10 * t00 + m11 * t10 + m12 * t20
        val r20 = m20 * t00 + m21 * t10 + m22 * t20

        val r01 = m00 * t01 + m01 * t11 + m02 * t21
        val r11 = m10 * t01 + m11 * t11 + m12 * t21
        val r21 = m20 * t01 + m21 * t11 + m22 * t21

        val r02 = m00 * t02 + m01 * t12 + m02 * t22
        val r12 = m10 * t02 + m11 * t12 + m12 * t22
        val r22 = m20 * t02 + m21 * t12 + m22 * t22

        m00 = r00; m01 = r01; m02 = r02
        m10 = r10; m11 = r11; m12 = r12
        m20 = r20; m21 = r21; m22 = r22
        return this
    }

    /**
     * Applies the composition of the given [rotation] and [scale] to this transform matrix.
     */
    fun compose(rotation: QuatD, scale: Vec3d): MutableMat3d {
        val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w
        val x2 = x + x;  val y2 = y + y;  val z2 = z + z
        val xx = x * x2; val xy = x * y2; val xz = x * z2
        val yy = y * y2; val yz = y * z2; val zz = z * z2
        val wx = w * x2; val wy = w * y2; val wz = w * z2

        val sx = scale.x; val sy = scale.y; val sz = scale.z

        return mul(
            (1 - (yy + zz)) * sx,   (xy - wz) * sy,         (xz + wy) * sz,
            (xy + wz) * sx,         (1 - (xx + zz)) * sy,   (yz - wx) * sz,
            (xz - wy) * sx,         (yz + wx) * sy,         (1 - (xx + yy)) * sz
        )
    }

    /**
     * Inplace operation: Rotates this matrix around the given [axis] by the given [angle].
     */
    fun rotate(angle: AngleD, axis: Vec3d): MutableMat3d {
        val t00: Double
        val t01: Double
        val t02: Double
        val t10: Double
        val t11: Double
        val t12: Double
        val t20: Double
        val t21: Double
        val t22: Double

        val s = angle.sin
        val c = angle.cos
        if (axis.x > 0.0 && axis.y == 0.0 && axis.z == 0.0) {
            // positive x-axis rotation - fast version
            t00 = 1.0; t01 = 0.0; t02 = 0.0
            t10 = 0.0; t11 = c;  t12 = -s
            t20 = 0.0; t21 = s;  t22 = c

        } else if (axis.x == 0.0 && axis.y > 0.0 && axis.z == 0.0) {
            // positive y-axis rotation - fast version
            t00 = c;  t01 = 0.0; t02 = s
            t10 = 0.0; t11 = 1.0; t12 = 0.0
            t20 = -s; t21 = 0.0; t22 = c

        } else if (axis.x == 0.0 && axis.y == 0.0 && axis.z > 0.0) {
            // positive z-axis rotation - fast version
            t00 = c;  t01 = -s; t02 = 0.0
            t10 = s;  t11 = c;  t12 = 0.0
            t20 = 0.0; t21 = 0.0; t22 = 1.0

        } else {
            // general case
            val recipLen = 1.0 / axis.length()
            val x = axis.x * recipLen
            val y = axis.y * recipLen
            val z = axis.z * recipLen

            val nc = 1.0 - c
            val xy = x * y
            val yz = y * z
            val zx = z * x
            val xs = x * s
            val ys = y * s
            val zs = z * s

            t00 = x * x * nc + c
            t01 = xy * nc - zs
            t02 = zx * nc + ys

            t10 = xy * nc + zs
            t11 = y * y * nc + c
            t12 = yz * nc - xs

            t20 = zx * nc - ys
            t21 = yz * nc + xs
            t22 = z * z * nc + c
        }

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given quaternion rotation. [quaternion] is expected to be a valid
     * rotation quaternion with unit length.
     */
    fun rotate(quaternion: QuatD): MutableMat3d {
        val r = quaternion.w
        val i = quaternion.x
        val j = quaternion.y
        val k = quaternion.z

        val t00 = 1 - 2 * (j*j + k*k)
        val t01 = 2 * (i*j - k*r)
        val t02 = 2 * (i*k + j*r)

        val t10 = 2 * (i*j + k*r)
        val t11 = 1 - 2 * (i*i + k*k)
        val t12 = 2 * (j*k - i*r)

        val t20 = 2 * (i*k - j*r)
        val t21 = 2 * (j*k + i*r)
        val t22 = 1 - 2 * (i*i + j*j)

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given euler angles.
     */
    fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, order: EulerOrder = EulerOrder.ZYX): MutableMat3d {
        val a = eulerX.cos
        val b = eulerX.sin
        val c = eulerY.cos
        val d = eulerY.sin
        val e = eulerZ.cos
        val f = eulerZ.sin

        val t00: Double
        val t01: Double
        val t02: Double
        val t10: Double
        val t11: Double
        val t12: Double
        val t20: Double
        val t21: Double
        val t22: Double

        when (order) {
            EulerOrder.XYZ -> {
                val ae = a * e; val af = a * f; val be = b * e; val bf = b * f

                t00 = c * e;        t01 = -c * f;       t02 = d
                t10 = af + be * d;  t11 = ae - bf * d;  t12 = - b * c
                t20 = bf - ae * d;  t21 = be + af * d;  t22 = a * c
            }
            EulerOrder.XZY -> {
                val ac = a * c; val ad = a * d; val bc = b * c; val bd = b * d

                t00 = c * e;        t01 = - f;          t02 = d * e
                t10 = ac * f + bd;  t11 = a * e;        t12 = ad * f - bc
                t20 = bc * f - ad;  t21 = b * e;        t22 = bd * f + ac
            }
            EulerOrder.YXZ -> {
                val ce = c * e; val cf = c * f; val de = d * e; val df = d * f

                t00 = ce + df * b;  t01 = de * b - cf;  t02 = a * d
                t10 = a * f;        t11 = a * e;        t12 = - b
                t20 = cf * b - de;  t21 = df + ce * b;  t22 = a * c
            }
            EulerOrder.YZX -> {
                val ac = a * c; val ad = a * d; val bc = b * c; val bd = b * d

                t00 = c * e;        t01 = bd - ac * f;  t02 = bc * f + ad
                t10 = f;            t11 = a * e;        t12 = - b * e
                t20 = - d * e;      t21 = ad * f + bc;  t22 = ac - bd * f
            }
            EulerOrder.ZXY -> {
                val ce = c * e; val cf = c * f; val de = d * e; val df = d * f

                t00 = ce - df * b;  t01 = - a * f;      t02 = de + cf * b
                t10 = cf + de * b;  t11 = a * e;        t12 = df - ce * b
                t20 = - a * d;      t21 = b;            t22 = a * c
            }
            EulerOrder.ZYX -> {
                val ae = a * e; val af = a * f; val be = b * e; val bf = b * f

                t00 = c * e;        t01 = be * d - af;  t02 = ae * d + bf
                t10 = c * f;        t11 = bf * d + ae;  t12 = af * d - be
                t20 = - d;          t21 = b * c;        t22 = a * c
            }
        }

        return mul(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Double): MutableMat3d = mul(
        s, 0.0, 0.0,
        0.0, s, 0.0,
        0.0, 0.0, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec3d): MutableMat3d = mul(
        s.x, 0.0, 0.0,
        0.0, s.y, 0.0,
        0.0, 0.0, s.z
    )

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/threeD/index.htm
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Double = 0.0): Boolean {
        val det = determinant()
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val t00 = m11*m22 - m12*m21
        val t01 = m02*m21 - m01*m22
        val t02 = m01*m12 - m02*m11
        val t10 = m12*m20 - m10*m22
        val t11 = m00*m22 - m02*m20
        val t12 = m02*m10 - m00*m12
        val t20 = m10*m21 - m11*m20
        val t21 = m01*m20 - m00*m21
        val t22 = m00*m11 - m01*m10

        val s = 1.0 / det
        m00 = t00 * s; m01 = t01 * s; m02 = t02 * s
        m10 = t10 * s; m11 = t11 * s; m12 = t12 * s
        m20 = t20 * s; m21 = t21 * s; m22 = t22 * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat3d {
        var t = m01; m01 = m10; m10 = t
        t = m02; m02 = m20; m20 = t
        t = m12; m12 = m21; m21 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec3d].
     */
    operator fun set(col: Int, that: Vec3d) {
        setColumn(col, that)
    }

    /**
     * Sets the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun set(row: Int, col: Int, that: Double) {
        when (row) {
            0 -> when (col) {
                0 -> m00 = that
                1 -> m01 = that
                2 -> m02 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                2 -> m12 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            2 -> when (col) {
                0 -> m20 = that
                1 -> m21 = that
                2 -> m22 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
    }

    /**
     * Sets the specified column to the given [Vec3d].
     */
    fun setColumn(col: Int, that: Vec3d): MutableMat3d {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..2)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec3d].
     */
    fun setRow(row: Int, that: Vec3d): MutableMat3d {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..2)")
        }
        return this
    }
}
