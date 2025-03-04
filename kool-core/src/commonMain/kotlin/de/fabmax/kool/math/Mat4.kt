package de.fabmax.kool.math

import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.toString
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MixedBuffer
import kotlin.math.*

enum class EulerOrder {
    XYZ,
    XZY,
    YXZ,
    YZX,
    ZXY,
    ZYX
}

enum class MatrixArrayOrder {
    COLUMN_MAJOR,
    ROW_MAJOR
}

fun Mat4f.toMat4d() = Mat4d(
    m00.toDouble(), m01.toDouble(), m02.toDouble(), m03.toDouble(),
    m10.toDouble(), m11.toDouble(), m12.toDouble(), m13.toDouble(),
    m20.toDouble(), m21.toDouble(), m22.toDouble(), m23.toDouble(),
    m30.toDouble(), m31.toDouble(), m32.toDouble(), m33.toDouble()
)

fun Mat4f.toMutableMat4d(result: MutableMat4d = MutableMat4d()): MutableMat4d = result.also {
    it.m00 = m00.toDouble(); it.m01 = m01.toDouble(); it.m02 = m02.toDouble(); it.m03 = m03.toDouble()
    it.m10 = m10.toDouble(); it.m11 = m11.toDouble(); it.m12 = m12.toDouble(); it.m13 = m13.toDouble()
    it.m20 = m20.toDouble(); it.m21 = m21.toDouble(); it.m22 = m22.toDouble(); it.m23 = m23.toDouble()
    it.m30 = m30.toDouble(); it.m31 = m31.toDouble(); it.m32 = m32.toDouble(); it.m33 = m33.toDouble()
}

fun MutableMat4f.set(that: Mat4d): MutableMat4f {
    m00 = that.m00.toFloat(); m01 = that.m01.toFloat(); m02 = that.m02.toFloat(); m03 = that.m03.toFloat()
    m10 = that.m10.toFloat(); m11 = that.m11.toFloat(); m12 = that.m12.toFloat(); m13 = that.m13.toFloat()
    m20 = that.m20.toFloat(); m21 = that.m21.toFloat(); m22 = that.m22.toFloat(); m23 = that.m23.toFloat()
    m30 = that.m30.toFloat(); m31 = that.m31.toFloat(); m32 = that.m32.toFloat(); m33 = that.m33.toFloat()
    return this
}

fun Mat4d.toMat4f() = Mat4f(
    m00.toFloat(), m01.toFloat(), m02.toFloat(), m03.toFloat(),
    m10.toFloat(), m11.toFloat(), m12.toFloat(), m13.toFloat(),
    m20.toFloat(), m21.toFloat(), m22.toFloat(), m23.toFloat(),
    m30.toFloat(), m31.toFloat(), m32.toFloat(), m33.toFloat()
)

fun Mat4d.toMutableMat4f(result: MutableMat4f= MutableMat4f()): MutableMat4f = result.also {
    it.m00 = m00.toFloat(); it.m01 = m01.toFloat(); it.m02 = m02.toFloat(); it.m03 = m03.toFloat()
    it.m10 = m10.toFloat(); it.m11 = m11.toFloat(); it.m12 = m12.toFloat(); it.m13 = m13.toFloat()
    it.m20 = m20.toFloat(); it.m21 = m21.toFloat(); it.m22 = m22.toFloat(); it.m23 = m23.toFloat()
    it.m30 = m30.toFloat(); it.m31 = m31.toFloat(); it.m32 = m32.toFloat(); it.m33 = m33.toFloat()
}

fun MutableMat4d.set(that: Mat4f): MutableMat4d {
    m00 = that.m00.toDouble(); m01 = that.m01.toDouble(); m02 = that.m02.toDouble(); m03 = that.m03.toDouble()
    m10 = that.m10.toDouble(); m11 = that.m11.toDouble(); m12 = that.m12.toDouble(); m13 = that.m13.toDouble()
    m20 = that.m20.toDouble(); m21 = that.m21.toDouble(); m22 = that.m22.toDouble(); m23 = that.m23.toDouble()
    m30 = that.m30.toDouble(); m31 = that.m31.toDouble(); m32 = that.m32.toDouble(); m33 = that.m33.toDouble()
    return this
}

/**
 * Transforms (i.e. multiplies) the given [Vec3d] and [w]-value with this matrix and stores the resulting transformed vector
 * in [result].
 */
fun Mat4f.transform(that: Vec3d, w: Double, result: MutableVec3d): MutableVec3d {
    val x = that.x * m00 + that.y * m01 + that.z * m02 + w * m03
    val y = that.x * m10 + that.y * m11 + that.z * m12 + w * m13
    val z = that.x * m20 + that.y * m21 + that.z * m22 + w * m23
    return result.set(x, y, z)
}

/**
 * Transforms (i.e. multiplies) the given [Vec3f] and [w]-value in place with this matrix.
 */
fun Mat4f.transform(that: MutableVec3d, w: Double = 1.0): MutableVec3d = transform(that, w, that)

/**
 * Transforms (i.e. multiplies) the given [Vec4d] with this matrix and stores the resulting transformed vector in [result].
 */
fun Mat4f.transform(that: Vec4d, result: MutableVec4d): MutableVec4d {
    val x = that.x * m00 + that.y * m01 + that.z * m02 + that.w * m03
    val y = that.x * m10 + that.y * m11 + that.z * m12 + that.w * m13
    val z = that.x * m20 + that.y * m21 + that.z * m22 + that.w * m23
    val w = that.x * m30 + that.y * m31 + that.z * m32 + that.w * m33
    return result.set(x, y, z, w)
}

/**
 * Transforms (i.e. multiplies) the given [MutableVec4d] by this matrix, changing the contents of the given
 * vector.
 */
fun Mat4f.transform(that: MutableVec4d): MutableVec4d = transform(that, that)

/**
 * Transforms (i.e. multiplies) the given [Vec3f] and [w]-value with this matrix and stores the resulting transformed vector
 * in [result].
 */
fun Mat4d.transform(that: Vec3f, w: Float, result: MutableVec3f): MutableVec3f {
    val x = that.x * m00 + that.y * m01 + that.z * m02 + w * m03
    val y = that.x * m10 + that.y * m11 + that.z * m12 + w * m13
    val z = that.x * m20 + that.y * m21 + that.z * m22 + w * m23
    return result.set(x.toFloat(), y.toFloat(), z.toFloat())
}

/**
 * Transforms (i.e. multiplies) the given [Vec3f] and [w]-value in place with this matrix.
 */
fun Mat4d.transform(that: MutableVec3f, w: Float = 1f): MutableVec3f = transform(that, w, that)

/**
 * Transforms (i.e. multiplies) the given [Vec4f] with this matrix and stores the resulting transformed vector in [result].
 */
fun Mat4d.transform(that: Vec4f, result: MutableVec4f): MutableVec4f {
    val x = that.x * m00 + that.y * m01 + that.z * m02 + that.w * m03
    val y = that.x * m10 + that.y * m11 + that.z * m12 + that.w * m13
    val z = that.x * m20 + that.y * m21 + that.z * m22 + that.w * m23
    val w = that.x * m30 + that.y * m31 + that.z * m32 + that.w * m33
    return result.set(x.toFloat(), y.toFloat(), z.toFloat(), w.toFloat())
}

/**
 * Transforms (i.e. multiplies) the given [MutableVec4f] by this matrix, changing the contents of the given
 * vector.
 */
fun Mat4d.transform(that: MutableVec4f): MutableVec4f = transform(that, that)

// <template> Changes made within the template section will also affect the other type variants of this class

open class Mat4f(
    open val m00: Float, open val m01: Float, open val m02: Float, open val m03: Float,
    open val m10: Float, open val m11: Float, open val m12: Float, open val m13: Float,
    open val m20: Float, open val m21: Float, open val m22: Float, open val m23: Float,
    open val m30: Float, open val m31: Float, open val m32: Float, open val m33: Float
) {

    constructor(mat: Mat4f): this(
        mat.m00, mat.m01, mat.m02, mat.m03,
        mat.m10, mat.m11, mat.m12, mat.m13,
        mat.m20, mat.m21, mat.m22, mat.m23,
        mat.m30, mat.m31, mat.m32, mat.m33
    )

    constructor(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f): this(
        col0.x, col1.x, col2.x, col3.x,
        col0.y, col1.y, col2.y, col3.y,
        col0.z, col1.z, col2.z, col3.z,
        col0.w, col1.w, col2.w, col3.w
    )

    operator fun component1(): Vec4f = Vec4f(m00, m10, m20, m30)
    operator fun component2(): Vec4f = Vec4f(m01, m11, m21, m31)
    operator fun component3(): Vec4f = Vec4f(m02, m12, m22, m32)
    operator fun component4(): Vec4f = Vec4f(m03, m13, m23, m33)

    operator fun times(that: Mat4f): MutableMat4f = mul(that, MutableMat4f())

    operator fun plus(that: Mat4f): MutableMat4f = add(that, MutableMat4f())

    operator fun minus(that: Mat4f): MutableMat4f = subtract(that, MutableMat4f())

    operator fun times(that: Vec4f): MutableVec4f = transform(that, MutableVec4f())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat4f, result: MutableMat4f): MutableMat4f = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat4f, result: MutableMat4f): MutableMat4f = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat4f, result: MutableMat4f): MutableMat4f {
        // We could apply the same pattern here as everywhere else and write this as
        //     result.set(this).mul(that)
        // but the explicit version used here is a good bit faster and this variant of mul()
        // is used a lot in hot code

        result.m00 = m00 * that.m00 + m01 * that.m10 + m02 * that.m20 + m03 * that.m30
        result.m10 = m10 * that.m00 + m11 * that.m10 + m12 * that.m20 + m13 * that.m30
        result.m20 = m20 * that.m00 + m21 * that.m10 + m22 * that.m20 + m23 * that.m30
        result.m30 = m30 * that.m00 + m31 * that.m10 + m32 * that.m20 + m33 * that.m30

        result.m01 = m00 * that.m01 + m01 * that.m11 + m02 * that.m21 + m03 * that.m31
        result.m11 = m10 * that.m01 + m11 * that.m11 + m12 * that.m21 + m13 * that.m31
        result.m21 = m20 * that.m01 + m21 * that.m11 + m22 * that.m21 + m23 * that.m31
        result.m31 = m30 * that.m01 + m31 * that.m11 + m32 * that.m21 + m33 * that.m31

        result.m02 = m00 * that.m02 + m01 * that.m12 + m02 * that.m22 + m03 * that.m32
        result.m12 = m10 * that.m02 + m11 * that.m12 + m12 * that.m22 + m13 * that.m32
        result.m22 = m20 * that.m02 + m21 * that.m12 + m22 * that.m22 + m23 * that.m32
        result.m32 = m30 * that.m02 + m31 * that.m12 + m32 * that.m22 + m33 * that.m32

        result.m03 = m00 * that.m03 + m01 * that.m13 + m02 * that.m23 + m03 * that.m33
        result.m13 = m10 * that.m03 + m11 * that.m13 + m12 * that.m23 + m13 * that.m33
        result.m23 = m20 * that.m03 + m21 * that.m13 + m22 * that.m23 + m23 * that.m33
        result.m33 = m30 * that.m03 + m31 * that.m13 + m32 * that.m23 + m33 * that.m33
        return result
    }

    /**
     * Multiplies the upper left 3x3 section of this matrix by the given one and stores the result in [result].
     */
    fun mulUpperLeft(that: Mat3f, result: MutableMat4f) = result.set(this).mulUpperLeft(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec4f] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec4f, result: MutableVec4f): MutableVec4f {
        val x = that.x * m00 + that.y * m01 + that.z * m02 + that.w * m03
        val y = that.x * m10 + that.y * m11 + that.z * m12 + that.w * m13
        val z = that.x * m20 + that.y * m21 + that.z * m22 + that.w * m23
        val w = that.x * m30 + that.y * m31 + that.z * m32 + that.w * m33
        return result.set(x, y, z, w)
    }

    /**
     * Transforms (i.e. multiplies) the given [Vec3f] and [w]-value with this matrix and stores the resulting transformed vector
     * in [result].
     */
    fun transform(that: Vec3f, w: Float, result: MutableVec3f): MutableVec3f {
        val x = that.x * m00 + that.y * m01 + that.z * m02 + w * m03
        val y = that.x * m10 + that.y * m11 + that.z * m12 + w * m13
        val z = that.x * m20 + that.y * m21 + that.z * m22 + w * m23
        return result.set(x, y, z)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec4f] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec4f): MutableVec4f = transform(that, that)

    /**
     * Transforms (i.e. multiplies) the given [MutableVec3f] and [w] by this matrix, changing the contents of
     * the given vector.
     */
    fun transform(that: MutableVec3f, w: Float = 1f): MutableVec3f = transform(that, w, that)

    /**
     * Adds the given [translation] to this transform matrix and stores the result in [result].
     *
     * @see MutableMat4f.translate
     */
    fun translate(translation: Vec3f, result: MutableMat4f): MutableMat4f = result.set(this).translate(translation)

    /**
     * Adds the given translation to this transform matrix and stores the result in [result].
     *
     * @see MutableMat4f.translate
     */
    fun translate(tx: Float, ty: Float, tz: Float, result: MutableMat4f): MutableMat4f {
        return result.set(this).translate(tx, ty, tz)
    }

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4f.rotate
     */
    fun rotate(angle: AngleF, axis: Vec3f, result: MutableMat4f): MutableMat4f = result.set(this).rotate(angle, axis)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4f.rotate
     */
    fun rotate(quaternion: QuatF, result: MutableMat4f): MutableMat4f = result.set(this).rotate(quaternion)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4f.rotate
     */
    fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, result: MutableMat4f, order: EulerOrder = EulerOrder.ZYX): MutableMat4f {
        return result.set(this).rotate(eulerX, eulerY, eulerZ, order)
    }

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4f.scale
     */
    fun scale(scale: Float, result: MutableMat4f): MutableMat4f = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4f.scale
     */
    fun scale(scale: Vec3f, result: MutableMat4f): MutableMat4f = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat4f): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat4f): MutableMat4f = result.set(this).transpose()

    fun determinant(): Float {
        return m03*m12*m21*m30 - m02*m13*m21*m30 - m03*m11*m22*m30 + m01*m13*m22*m30 +
               m02*m11*m23*m30 - m01*m12*m23*m30 - m03*m12*m20*m31 + m02*m13*m20*m31 +
               m03*m10*m22*m31 - m00*m13*m22*m31 - m02*m10*m23*m31 + m00*m12*m23*m31 +
               m03*m11*m20*m32 - m01*m13*m20*m32 - m03*m10*m21*m32 + m00*m13*m21*m32 +
               m01*m10*m23*m32 - m00*m11*m23*m32 - m02*m11*m20*m33 + m01*m12*m20*m33 +
               m02*m10*m21*m33 - m00*m12*m21*m33 - m01*m10*m22*m33 + m00*m11*m22*m33
    }

    /**
     * Copies the specified column into a [Vec4f] and returns it.
     */
    operator fun get(col: Int): Vec4f = getColumn(col)

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
                3 -> m03
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                2 -> m12
                3 -> m13
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            2 -> when (col) {
                0 -> m20
                1 -> m21
                2 -> m22
                3 -> m23
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            3 -> when (col) {
                0 -> m30
                1 -> m31
                2 -> m32
                3 -> m33
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec4f] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec4f = MutableVec4f()): MutableVec4f {
        return when (col) {
            0 -> result.set(m00, m10, m20, m30)
            1 -> result.set(m01, m11, m21, m31)
            2 -> result.set(m02, m12, m22, m32)
            3 -> result.set(m03, m13, m23, m33)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec4f] and returns it.
     */
    fun getRow(row: Int, result: MutableVec4f = MutableVec4f()): MutableVec4f {
        return when (row) {
            0 -> result.set(m00, m01, m02, m03)
            1 -> result.set(m10, m11, m12, m13)
            2 -> result.set(m20, m21, m22, m23)
            3 -> result.set(m30, m31, m32, m33)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Returns the upper left 3x3 section of this matrix in the given [result] matrix.
     */
    fun getUpperLeft(result: MutableMat3f): MutableMat3f {
        result.m00 = m00; result.m01 = m01; result.m02 = m02
        result.m10 = m10; result.m11 = m11; result.m12 = m12
        result.m20 = m20; result.m21 = m21; result.m22 = m22
        return result
    }

    /**
     * Decomposes this transform matrix into its translation, rotation and scale components and returns them
     * int the provided [translation], [rotation] and [scale] vectors.
     */
    fun decompose(translation: MutableVec3f? = null, rotation: MutableQuatF? = null, scale: MutableVec3f? = null) {
        translation?.set(m03, m13, m23)

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
     * Returns the decomposed translation component of this transform matrix.
     *
     * @see decompose
     */
    fun getTranslation(result: MutableVec3f = MutableVec3f()): MutableVec3f {
        decompose(translation = result)
        return result
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
     */
    fun putTo(target: Float32Buffer) {
        target.put(m00); target.put(m10); target.put(m20); target.put(m30)
        target.put(m01); target.put(m11); target.put(m21); target.put(m31)
        target.put(m02); target.put(m12); target.put(m22); target.put(m32)
        target.put(m03); target.put(m13); target.put(m23); target.put(m33)
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(m00); target.putFloat32(m10); target.putFloat32(m20); target.putFloat32(m30)
        target.putFloat32(m01); target.putFloat32(m11); target.putFloat32(m21); target.putFloat32(m31)
        target.putFloat32(m02); target.putFloat32(m12); target.putFloat32(m22); target.putFloat32(m32)
        target.putFloat32(m03); target.putFloat32(m13); target.putFloat32(m23); target.putFloat32(m33)
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}, ${m02.toString(precision).padStart(width)}, ${m03.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}, ${m12.toString(precision).padStart(width)}, ${m13.toString(precision).padStart(width)}]\n")
        append("[${m20.toString(precision).padStart(width)}, ${m21.toString(precision).padStart(width)}, ${m22.toString(precision).padStart(width)}, ${m23.toString(precision).padStart(width)}]\n")
        append("[${m30.toString(precision).padStart(width)}, ${m31.toString(precision).padStart(width)}, ${m32.toString(precision).padStart(width)}, ${m33.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10, $m20, $m30), col1: ($m01, $m11, $m21, $m31), col2: ($m02, $m12, $m22, $m32), col3: ($m03, $m13, $m23, $m33) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat4f, eps: Float = FUZZY_EQ_F): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) && isFuzzyEqual(m02, that.m02, eps) && isFuzzyEqual(m03, that.m03, eps) &&
               isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps) && isFuzzyEqual(m12, that.m12, eps) && isFuzzyEqual(m13, that.m13, eps) &&
               isFuzzyEqual(m20, that.m20, eps) && isFuzzyEqual(m21, that.m21, eps) && isFuzzyEqual(m22, that.m22, eps) && isFuzzyEqual(m23, that.m23, eps) &&
               isFuzzyEqual(m30, that.m30, eps) && isFuzzyEqual(m31, that.m31, eps) && isFuzzyEqual(m32, that.m32, eps) && isFuzzyEqual(m33, that.m33, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat4f) return false
        return m00 == other.m00 && m01 == other.m01 && m02 == other.m02 && m03 == other.m03 &&
               m10 == other.m10 && m11 == other.m11 && m12 == other.m12 && m13 == other.m13 &&
               m20 == other.m20 && m21 == other.m21 && m22 == other.m22 && m23 == other.m23 &&
               m30 == other.m30 && m31 == other.m31 && m32 == other.m32 && m33 == other.m33
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m02.hashCode()
        result = 31 * result + m03.hashCode()

        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        result = 31 * result + m12.hashCode()
        result = 31 * result + m13.hashCode()

        result = 31 * result + m20.hashCode()
        result = 31 * result + m21.hashCode()
        result = 31 * result + m22.hashCode()
        result = 31 * result + m23.hashCode()

        result = 31 * result + m30.hashCode()
        result = 31 * result + m31.hashCode()
        result = 31 * result + m32.hashCode()
        result = 31 * result + m33.hashCode()
        return result
    }

    companion object {
        val IDENTITY = Mat4f(
            1f, 0f, 0f, 0f,
            0f, 1f, 0f, 0f,
            0f, 0f, 1f, 0f,
            0f, 0f, 0f, 1f
        )

        val ZERO = Mat4f(
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f,
            0f, 0f, 0f, 0f
        )

        fun fromArray(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat4f {
            return MutableMat4f().set(array, offset, order)
        }

        fun translation(t: Vec3f): Mat4f = MutableMat4f().translate(t)

        fun translation(tx: Float, ty: Float, tz: Float): Mat4f = MutableMat4f().translate(tx, ty, tz)

        fun rotation(angle: AngleF, axis: Vec3f): Mat4f = MutableMat4f().rotate(angle, axis)

        fun rotation(quaternion: QuatF): Mat4f = MutableMat4f().rotate(quaternion)

        fun rotation(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, order: EulerOrder = EulerOrder.ZYX): Mat4f {
            return MutableMat4f().rotate(eulerX, eulerY, eulerZ, order)
        }

        fun scale(s: Float): Mat4f = MutableMat4f().scale(s)

        fun scale(s: Vec3f): Mat4f = MutableMat4f().scale(s)

        fun composition(translation: Vec3f, rotation: QuatF, scale: Vec3f = Vec3f.ONES): Mat4f {
            return MutableMat4f().compose(translation, rotation, scale)
        }

        fun lookAt(eyePosition: Vec3f, lookAt: Vec3f, up: Vec3f): Mat4f {
            return MutableMat4f().lookAt(eyePosition, lookAt, up)
        }

        fun orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float, depthRange: DepthRange, reversedDepth: Boolean): Mat4f {
            return MutableMat4f().orthographic(left, right, bottom, top, near, far, depthRange, reversedDepth)
        }

        fun perspective(fovy: AngleF, aspect: Float, near: Float, far: Float, depthRange: DepthRange): Mat4f {
            return MutableMat4f().perspective(fovy, aspect, near, far, depthRange)
        }

        fun perspectiveReversedDepthInfiniteRange(fovy: AngleF, aspect: Float, near: Float): Mat4f {
            return MutableMat4f().perspectiveReversedDepthInfiniteRange(fovy, aspect, near)
        }
    }
}

open class MutableMat4f(
    override var m00: Float, override var m01: Float, override var m02: Float, override var m03: Float,
    override var m10: Float, override var m11: Float, override var m12: Float, override var m13: Float,
    override var m20: Float, override var m21: Float, override var m22: Float, override var m23: Float,
    override var m30: Float, override var m31: Float, override var m32: Float, override var m33: Float
) : Mat4f(
    m00, m01, m02, m03,
    m10, m11, m12, m13,
    m20, m21, m22, m23,
    m30, m31, m32, m33
) {

    constructor(mat: Mat4f): this(
        mat.m00, mat.m01, mat.m02, mat.m03,
        mat.m10, mat.m11, mat.m12, mat.m13,
        mat.m20, mat.m21, mat.m22, mat.m23,
        mat.m30, mat.m31, mat.m32, mat.m33
    )

    constructor(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f): this(
        col0.x, col1.x, col2.x, col3.x,
        col0.y, col1.y, col2.y, col3.y,
        col0.z, col1.z, col2.z, col3.z,
        col0.w, col1.w, col2.w, col3.w
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat4f): MutableMat4f {
        m00 = that.m00; m01 = that.m01; m02 = that.m02; m03 = that.m03
        m10 = that.m10; m11 = that.m11; m12 = that.m12; m13 = that.m13
        m20 = that.m20; m21 = that.m21; m22 = that.m22; m23 = that.m23
        m30 = that.m30; m31 = that.m31; m32 = that.m32; m33 = that.m33
        return this
    }

    fun set(col0: Vec4f, col1: Vec4f, col2: Vec4f, col3: Vec4f): MutableMat4f {
        m00 = col0.x; m01 = col1.x; m02 = col2.x; m03 = col3.x
        m10 = col0.y; m11 = col1.y; m12 = col2.y; m13 = col3.y
        m20 = col0.z; m21 = col1.z; m22 = col2.z; m23 = col3.z
        m30 = col0.w; m31 = col1.w; m32 = col2.w; m33 = col3.w
        return this
    }

    fun set(
        t00: Float, t01: Float, t02: Float, t03: Float,
        t10: Float, t11: Float, t12: Float, t13: Float,
        t20: Float, t21: Float, t22: Float, t23: Float,
        t30: Float, t31: Float, t32: Float, t33: Float
    ): MutableMat4f {
        m00 = t00; m01 = t01; m02 = t02; m03 = t03
        m10 = t10; m11 = t11; m12 = t12; m13 = t13
        m20 = t20; m21 = t21; m22 = t22; m23 = t23
        m30 = t30; m31 = t31; m32 = t32; m33 = t33
        return this
    }

    fun set(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat4f {
        var i = offset
        m00 = array[i++]; m01 = array[i++]; m02 = array[i++]; m03 = array[i++]
        m10 = array[i++]; m11 = array[i++]; m12 = array[i++]; m13 = array[i++]
        m20 = array[i++]; m21 = array[i++]; m22 = array[i++]; m23 = array[i++]
        m30 = array[i++]; m31 = array[i++]; m32 = array[i++]; m33 = array[i]
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
    operator fun plusAssign(that: Mat4f) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat4f) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat4f) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat4f): MutableMat4f {
        m00 += that.m00; m01 += that.m01; m02 += that.m02; m03 += that.m03
        m10 += that.m10; m11 += that.m11; m12 += that.m12; m13 += that.m13
        m20 += that.m20; m21 += that.m21; m22 += that.m22; m23 += that.m23
        m30 += that.m30; m31 += that.m31; m32 += that.m32; m33 += that.m33
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat4f): MutableMat4f {
        m00 -= that.m00; m01 -= that.m01; m02 -= that.m02; m03 -= that.m03
        m10 -= that.m10; m11 -= that.m11; m12 -= that.m12; m13 -= that.m13
        m20 -= that.m20; m21 -= that.m21; m22 -= that.m22; m23 -= that.m23
        m30 -= that.m30; m31 -= that.m31; m32 -= that.m32; m33 -= that.m33
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat4f): MutableMat4f = mul(
        that.m00, that.m01, that.m02, that.m03,
        that.m10, that.m11, that.m12, that.m13,
        that.m20, that.m21, that.m22, that.m23,
        that.m30, that.m31, that.m32, that.m33
    )

    /**
     * Inplace operation: Multiplies the upper left 3x3 section of this matrix by the given one changing the contents
     * of this matrix.
     */
    fun mulUpperLeft(that: Mat3f): MutableMat4f = mul33(
        that.m00, that.m01, that.m02,
        that.m10, that.m11, that.m12,
        that.m20, that.m21, that.m22
    )

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul(
        t00: Float, t01: Float, t02: Float, t03: Float,
        t10: Float, t11: Float, t12: Float, t13: Float,
        t20: Float, t21: Float, t22: Float, t23: Float,
        t30: Float, t31: Float, t32: Float, t33: Float
    ): MutableMat4f {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20 + m03 * t30
        val r10 = m10 * t00 + m11 * t10 + m12 * t20 + m13 * t30
        val r20 = m20 * t00 + m21 * t10 + m22 * t20 + m23 * t30
        val r30 = m30 * t00 + m31 * t10 + m32 * t20 + m33 * t30

        val r01 = m00 * t01 + m01 * t11 + m02 * t21 + m03 * t31
        val r11 = m10 * t01 + m11 * t11 + m12 * t21 + m13 * t31
        val r21 = m20 * t01 + m21 * t11 + m22 * t21 + m23 * t31
        val r31 = m30 * t01 + m31 * t11 + m32 * t21 + m33 * t31

        val r02 = m00 * t02 + m01 * t12 + m02 * t22 + m03 * t32
        val r12 = m10 * t02 + m11 * t12 + m12 * t22 + m13 * t32
        val r22 = m20 * t02 + m21 * t12 + m22 * t22 + m23 * t32
        val r32 = m30 * t02 + m31 * t12 + m32 * t22 + m33 * t32

        val r03 = m00 * t03 + m01 * t13 + m02 * t23 + m03 * t33
        val r13 = m10 * t03 + m11 * t13 + m12 * t23 + m13 * t33
        val r23 = m20 * t03 + m21 * t13 + m22 * t23 + m23 * t33
        val r33 = m30 * t03 + m31 * t13 + m32 * t23 + m33 * t33

        m00 = r00; m01 = r01; m02 = r02; m03 = r03
        m10 = r10; m11 = r11; m12 = r12; m13 = r13
        m20 = r20; m21 = r21; m22 = r22; m23 = r23
        m30 = r30; m31 = r31; m32 = r32; m33 = r33
        return this
    }

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul33(
        t00: Float, t01: Float, t02: Float,
        t10: Float, t11: Float, t12: Float,
        t20: Float, t21: Float, t22: Float
    ): MutableMat4f {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20
        val r10 = m10 * t00 + m11 * t10 + m12 * t20
        val r20 = m20 * t00 + m21 * t10 + m22 * t20
        val r30 = m30 * t00 + m31 * t10 + m32 * t20

        val r01 = m00 * t01 + m01 * t11 + m02 * t21
        val r11 = m10 * t01 + m11 * t11 + m12 * t21
        val r21 = m20 * t01 + m21 * t11 + m22 * t21
        val r31 = m30 * t01 + m31 * t11 + m32 * t21

        val r02 = m00 * t02 + m01 * t12 + m02 * t22
        val r12 = m10 * t02 + m11 * t12 + m12 * t22
        val r22 = m20 * t02 + m21 * t12 + m22 * t22
        val r32 = m30 * t02 + m31 * t12 + m32 * t22

        m00 = r00; m01 = r01; m02 = r02
        m10 = r10; m11 = r11; m12 = r12
        m20 = r20; m21 = r21; m22 = r22
        m30 = r30; m31 = r31; m32 = r32
        return this
    }

    /**
     * Applies the composition of the given [translation], [rotation] and [scale] to this transform matrix.
     */
    fun compose(translation: Vec3f, rotation: QuatF, scale: Vec3f = Vec3f.ONES): MutableMat4f {
        val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w
        val x2 = x + x;  val y2 = y + y;  val z2 = z + z
        val xx = x * x2; val xy = x * y2; val xz = x * z2
        val yy = y * y2; val yz = y * z2; val zz = z * z2
        val wx = w * x2; val wy = w * y2; val wz = w * z2

        val sx = scale.x; val sy = scale.y; val sz = scale.z

        return mul(
            (1 - (yy + zz)) * sx,   (xy - wz) * sy,         (xz + wy) * sz,         translation.x,
            (xy + wz) * sx,         (1 - (xx + zz)) * sy,   (yz - wx) * sz,         translation.y,
            (xz - wy) * sx,         (yz + wx) * sy,         (1 - (xx + yy)) * sz,   translation.z,
            0f,                     0f,                     0f,                     1f
        )
    }

    /**
     * Inplace operation: Adds the given translation [t] transform to this matrix.
     */
    fun translate(t: Vec3f): MutableMat4f = translate(t.x, t.y, t.z)

    /**
     * Inplace operation: Adds the given translation [tx], [ty], [tz] transform to this matrix.
     */
    fun translate(tx: Float, ty: Float, tz: Float): MutableMat4f {
        m03 += m00 * tx + m01 * ty + m02 * tz
        m13 += m10 * tx + m11 * ty + m12 * tz
        m23 += m20 * tx + m21 * ty + m22 * tz
        return this
    }

    /**
     * Inplace operation: Rotates this matrix around the given [axis] by the given [angle].
     */
    fun rotate(angle: AngleF, axis: Vec3f): MutableMat4f {
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given quaternion rotation. [quaternion] is expected to be a valid
     * rotation quaternion with unit length.
     */
    fun rotate(quaternion: QuatF): MutableMat4f {
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given euler angles.
     */
    fun rotate(eulerX: AngleF, eulerY: AngleF, eulerZ: AngleF, order: EulerOrder = EulerOrder.ZYX): MutableMat4f {
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
                t10 = a * f;        t11 = a * e;        t12 = -b
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Float): MutableMat4f = mul33(
        s, 0f, 0f,
        0f, s, 0f,
        0f, 0f, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec3f): MutableMat4f = mul33(
        s.x, 0f, 0f,
        0f, s.y, 0f,
        0f, 0f, s.z
    )

    /**
     * Inplace operation: Applies a look-at transform to this matrix.
     */
    fun lookAt(eyePosition: Vec3f, lookAt: Vec3f, up: Vec3f): MutableMat4f {
        val z = MutableVec3f(lookAt - eyePosition)
        val fLen = z.length()
        if (fLen.isFuzzyZero()) {
            // eye position and look at are equal
            z.z = 1f
        } else {
            z *= 1f / fLen
        }

        val x = z.cross(up, MutableVec3f())
        val sLen = x.length()
        if (sLen.isFuzzyZero()) {
            // forward vector is parallel to up
            if (abs(z.dot(Vec3f.Y_AXIS)) > 0.99f) {
                z.cross(Vec3f.NEG_Z_AXIS, x)
                x.norm()
            } else {
                z.cross(Vec3f.Y_AXIS, x)
                x.norm()
            }
        } else {
            x *= 1f / sLen
        }

        val y = x.cross(z, MutableVec3f())

        return mul(
            x.x, x.y, x.z, -x.dot(eyePosition),
            y.x, y.y, y.z, -y.dot(eyePosition),
            -z.x, -z.y, -z.z, z.dot(eyePosition),
            0f, 0f, 0f, 1f
        )
    }

    /**
     * Inplace operation: Applies an orthographic projection transform to this matrix.
     */
    fun orthographic(left: Float, right: Float, bottom: Float, top: Float, near: Float, far: Float, depthRange: DepthRange, reversedDepth: Boolean): MutableMat4f {
        val farD = if (reversedDepth) near else far
        val nearD = if (reversedDepth) far else near

        if (left == right) {
            throw IllegalArgumentException("left == right")
        }
        if (bottom == top) {
            throw IllegalArgumentException("bottom == top")
        }
        if (nearD == farD) {
            throw IllegalArgumentException("near == far")
        }

        val w = 1.0f / (right - left)
        val h = 1.0f / (top - bottom)
        val d = 1.0f / (farD - nearD)

        val x = (right + left) * w
        val y = (top + bottom) * h

        val z: Float
        val zd: Float
        when (depthRange) {
            DepthRange.NEGATIVE_ONE_TO_ONE -> {
                z = (farD + nearD) * d
                zd = -2 * d
            }
            DepthRange.ZERO_TO_ONE -> {
                z = nearD * d
                zd = -1 * d
            }
        }

        return mul(
            2 * w, 0f, 0f, -x,
            0f, 2 * h, 0f, -y,
            0f, 0f, zd, -z,
            0f, 0f, 0f, 1f
        )
    }

    /**
     * Inplace operation: Applies a perspective projection transform to this matrix.
     */
    fun perspective(fovy: AngleF, aspect: Float, near: Float, far: Float, depthRange: DepthRange): MutableMat4f {
        val f = 1.0f / tan(fovy.rad / 2.0f)
        val rangeRecip = 1.0f / (near - far)

        val z: Float
        val zt: Float
        when (depthRange) {
            DepthRange.NEGATIVE_ONE_TO_ONE -> {
                z = (far + near) * rangeRecip
                zt = 2.0f * far * near * rangeRecip
            }
            DepthRange.ZERO_TO_ONE -> {
                z = far * rangeRecip
                zt = far * near * rangeRecip
            }
        }

        return mul(
            f / aspect, 0f, 0f, 0f,
            0f, f, 0f, 0f,
            0f, 0f, z, zt,
            0f, 0f, -1f, 0f
        )
    }

    /**
     * Inplace operation: Applies a perspective projection transform for reversed depth with an infinite far plane
     * to this matrix.
     */
    fun perspectiveReversedDepthInfiniteRange(fovy: AngleF, aspect: Float, near: Float): MutableMat4f {
        val f = 1.0f / tan(fovy.rad / 2.0f)
        return mul(
            f / aspect, 0f, 0f, 0f,
            0f, f, 0f, 0f,
            0f, 0f, 0f, near,
            0f, 0f, -1f, 0f
        )
    }

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Float = 0f): Boolean {
        val det = determinant()
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val r00 = m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33
        val r01 = m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33
        val r02 = m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33
        val r03 = m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23
        val r10 = m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33
        val r11 = m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33
        val r12 = m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33
        val r13 = m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23
        val r20 = m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33
        val r21 = m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33
        val r22 = m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33
        val r23 = m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23
        val r30 = m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32
        val r31 = m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32
        val r32 = m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32
        val r33 = m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22

        val s = 1f / det
        m00 = r00 * s; m01 = r01 * s; m02 = r02 * s; m03 = r03 * s
        m10 = r10 * s; m11 = r11 * s; m12 = r12 * s; m13 = r13 * s
        m20 = r20 * s; m21 = r21 * s; m22 = r22 * s; m23 = r23 * s
        m30 = r30 * s; m31 = r31 * s; m32 = r32 * s; m33 = r33 * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat4f {
        var t = m01; m01 = m10; m10 = t
            t = m02; m02 = m20; m20 = t
            t = m03; m03 = m30; m30 = t
            t = m12; m12 = m21; m21 = t
            t = m13; m13 = m31; m31 = t
            t = m23; m23 = m32; m32 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec4f].
     */
    operator fun set(col: Int, that: Vec4f) {
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
                3 -> m03 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                2 -> m12 = that
                3 -> m13 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            2 -> when (col) {
                0 -> m20 = that
                1 -> m21 = that
                2 -> m22 = that
                3 -> m23 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            3 -> when (col) {
                0 -> m30 = that
                1 -> m31 = that
                2 -> m32 = that
                3 -> m33 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Sets the specified column to the given [Vec4f].
     */
    fun setColumn(col: Int, that: Vec4f): MutableMat4f {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z; m30 = that.w }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z; m31 = that.w }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z; m32 = that.w }
            3 -> { m03 = that.x; m13 = that.y; m23 = that.z; m33 = that.w }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified column to the given [Vec3f] and [w].
     */
    fun setColumn(col: Int, that: Vec3f, w: Float): MutableMat4f {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z; m30 = w }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z; m31 = w }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z; m32 = w }
            3 -> { m03 = that.x; m13 = that.y; m23 = that.z; m33 = w }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec4f].
     */
    fun setRow(row: Int, that: Vec4f): MutableMat4f {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z; m03 = that.w }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z; m13 = that.w }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z; m23 = that.w }
            3 -> { m30 = that.x; m31 = that.y; m32 = that.z; m33 = that.w }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec3f] and [w].
     */
    fun setRow(row: Int, that: Vec3f, w: Float): MutableMat4f {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z; m03 = w }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z; m13 = w }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z; m23 = w }
            3 -> { m30 = that.x; m31 = that.y; m32 = that.z; m33 = w }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the upper left 3x3 section of this matrix to the given one.
     */
    fun setUpperLeft(that: Mat3f): MutableMat4f {
        m00 = that.m00; m01 = that.m01; m02 = that.m02
        m10 = that.m10; m11 = that.m11; m12 = that.m12
        m20 = that.m20; m21 = that.m21; m22 = that.m22
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


open class Mat4d(
    open val m00: Double, open val m01: Double, open val m02: Double, open val m03: Double,
    open val m10: Double, open val m11: Double, open val m12: Double, open val m13: Double,
    open val m20: Double, open val m21: Double, open val m22: Double, open val m23: Double,
    open val m30: Double, open val m31: Double, open val m32: Double, open val m33: Double
) {

    constructor(mat: Mat4d): this(
        mat.m00, mat.m01, mat.m02, mat.m03,
        mat.m10, mat.m11, mat.m12, mat.m13,
        mat.m20, mat.m21, mat.m22, mat.m23,
        mat.m30, mat.m31, mat.m32, mat.m33
    )

    constructor(col0: Vec4d, col1: Vec4d, col2: Vec4d, col3: Vec4d): this(
        col0.x, col1.x, col2.x, col3.x,
        col0.y, col1.y, col2.y, col3.y,
        col0.z, col1.z, col2.z, col3.z,
        col0.w, col1.w, col2.w, col3.w
    )

    operator fun component1(): Vec4d = Vec4d(m00, m10, m20, m30)
    operator fun component2(): Vec4d = Vec4d(m01, m11, m21, m31)
    operator fun component3(): Vec4d = Vec4d(m02, m12, m22, m32)
    operator fun component4(): Vec4d = Vec4d(m03, m13, m23, m33)

    operator fun times(that: Mat4d): MutableMat4d = mul(that, MutableMat4d())

    operator fun plus(that: Mat4d): MutableMat4d = add(that, MutableMat4d())

    operator fun minus(that: Mat4d): MutableMat4d = subtract(that, MutableMat4d())

    operator fun times(that: Vec4d): MutableVec4d = transform(that, MutableVec4d())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat4d, result: MutableMat4d): MutableMat4d = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat4d, result: MutableMat4d): MutableMat4d = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat4d, result: MutableMat4d): MutableMat4d {
        // We could apply the same pattern here as everywhere else and write this as
        //     result.set(this).mul(that)
        // but the explicit version used here is a good bit faster and this variant of mul()
        // is used a lot in hot code

        result.m00 = m00 * that.m00 + m01 * that.m10 + m02 * that.m20 + m03 * that.m30
        result.m10 = m10 * that.m00 + m11 * that.m10 + m12 * that.m20 + m13 * that.m30
        result.m20 = m20 * that.m00 + m21 * that.m10 + m22 * that.m20 + m23 * that.m30
        result.m30 = m30 * that.m00 + m31 * that.m10 + m32 * that.m20 + m33 * that.m30

        result.m01 = m00 * that.m01 + m01 * that.m11 + m02 * that.m21 + m03 * that.m31
        result.m11 = m10 * that.m01 + m11 * that.m11 + m12 * that.m21 + m13 * that.m31
        result.m21 = m20 * that.m01 + m21 * that.m11 + m22 * that.m21 + m23 * that.m31
        result.m31 = m30 * that.m01 + m31 * that.m11 + m32 * that.m21 + m33 * that.m31

        result.m02 = m00 * that.m02 + m01 * that.m12 + m02 * that.m22 + m03 * that.m32
        result.m12 = m10 * that.m02 + m11 * that.m12 + m12 * that.m22 + m13 * that.m32
        result.m22 = m20 * that.m02 + m21 * that.m12 + m22 * that.m22 + m23 * that.m32
        result.m32 = m30 * that.m02 + m31 * that.m12 + m32 * that.m22 + m33 * that.m32

        result.m03 = m00 * that.m03 + m01 * that.m13 + m02 * that.m23 + m03 * that.m33
        result.m13 = m10 * that.m03 + m11 * that.m13 + m12 * that.m23 + m13 * that.m33
        result.m23 = m20 * that.m03 + m21 * that.m13 + m22 * that.m23 + m23 * that.m33
        result.m33 = m30 * that.m03 + m31 * that.m13 + m32 * that.m23 + m33 * that.m33
        return result
    }

    /**
     * Multiplies the upper left 3x3 section of this matrix by the given one and stores the result in [result].
     */
    fun mulUpperLeft(that: Mat3d, result: MutableMat4d) = result.set(this).mulUpperLeft(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec4d] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec4d, result: MutableVec4d): MutableVec4d {
        val x = that.x * m00 + that.y * m01 + that.z * m02 + that.w * m03
        val y = that.x * m10 + that.y * m11 + that.z * m12 + that.w * m13
        val z = that.x * m20 + that.y * m21 + that.z * m22 + that.w * m23
        val w = that.x * m30 + that.y * m31 + that.z * m32 + that.w * m33
        return result.set(x, y, z, w)
    }

    /**
     * Transforms (i.e. multiplies) the given [Vec3d] and [w]-value with this matrix and stores the resulting transformed vector
     * in [result].
     */
    fun transform(that: Vec3d, w: Double, result: MutableVec3d): MutableVec3d {
        val x = that.x * m00 + that.y * m01 + that.z * m02 + w * m03
        val y = that.x * m10 + that.y * m11 + that.z * m12 + w * m13
        val z = that.x * m20 + that.y * m21 + that.z * m22 + w * m23
        return result.set(x, y, z)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec4d] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec4d): MutableVec4d = transform(that, that)

    /**
     * Transforms (i.e. multiplies) the given [MutableVec3d] and [w] by this matrix, changing the contents of
     * the given vector.
     */
    fun transform(that: MutableVec3d, w: Double = 1.0): MutableVec3d = transform(that, w, that)

    /**
     * Adds the given [translation] to this transform matrix and stores the result in [result].
     *
     * @see MutableMat4d.translate
     */
    fun translate(translation: Vec3d, result: MutableMat4d): MutableMat4d = result.set(this).translate(translation)

    /**
     * Adds the given translation to this transform matrix and stores the result in [result].
     *
     * @see MutableMat4d.translate
     */
    fun translate(tx: Double, ty: Double, tz: Double, result: MutableMat4d): MutableMat4d {
        return result.set(this).translate(tx, ty, tz)
    }

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4d.rotate
     */
    fun rotate(angle: AngleD, axis: Vec3d, result: MutableMat4d): MutableMat4d = result.set(this).rotate(angle, axis)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4d.rotate
     */
    fun rotate(quaternion: QuatD, result: MutableMat4d): MutableMat4d = result.set(this).rotate(quaternion)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4d.rotate
     */
    fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, result: MutableMat4d, order: EulerOrder = EulerOrder.ZYX): MutableMat4d {
        return result.set(this).rotate(eulerX, eulerY, eulerZ, order)
    }

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4d.scale
     */
    fun scale(scale: Double, result: MutableMat4d): MutableMat4d = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat4d.scale
     */
    fun scale(scale: Vec3d, result: MutableMat4d): MutableMat4d = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat4d): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat4d): MutableMat4d = result.set(this).transpose()

    fun determinant(): Double {
        return m03*m12*m21*m30 - m02*m13*m21*m30 - m03*m11*m22*m30 + m01*m13*m22*m30 +
               m02*m11*m23*m30 - m01*m12*m23*m30 - m03*m12*m20*m31 + m02*m13*m20*m31 +
               m03*m10*m22*m31 - m00*m13*m22*m31 - m02*m10*m23*m31 + m00*m12*m23*m31 +
               m03*m11*m20*m32 - m01*m13*m20*m32 - m03*m10*m21*m32 + m00*m13*m21*m32 +
               m01*m10*m23*m32 - m00*m11*m23*m32 - m02*m11*m20*m33 + m01*m12*m20*m33 +
               m02*m10*m21*m33 - m00*m12*m21*m33 - m01*m10*m22*m33 + m00*m11*m22*m33
    }

    /**
     * Copies the specified column into a [Vec4d] and returns it.
     */
    operator fun get(col: Int): Vec4d = getColumn(col)

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
                3 -> m03
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                2 -> m12
                3 -> m13
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            2 -> when (col) {
                0 -> m20
                1 -> m21
                2 -> m22
                3 -> m23
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            3 -> when (col) {
                0 -> m30
                1 -> m31
                2 -> m32
                3 -> m33
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec4d] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec4d = MutableVec4d()): MutableVec4d {
        return when (col) {
            0 -> result.set(m00, m10, m20, m30)
            1 -> result.set(m01, m11, m21, m31)
            2 -> result.set(m02, m12, m22, m32)
            3 -> result.set(m03, m13, m23, m33)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec4d] and returns it.
     */
    fun getRow(row: Int, result: MutableVec4d = MutableVec4d()): MutableVec4d {
        return when (row) {
            0 -> result.set(m00, m01, m02, m03)
            1 -> result.set(m10, m11, m12, m13)
            2 -> result.set(m20, m21, m22, m23)
            3 -> result.set(m30, m31, m32, m33)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Returns the upper left 3x3 section of this matrix in the given [result] matrix.
     */
    fun getUpperLeft(result: MutableMat3d): MutableMat3d {
        result.m00 = m00; result.m01 = m01; result.m02 = m02
        result.m10 = m10; result.m11 = m11; result.m12 = m12
        result.m20 = m20; result.m21 = m21; result.m22 = m22
        return result
    }

    /**
     * Decomposes this transform matrix into its translation, rotation and scale components and returns them
     * int the provided [translation], [rotation] and [scale] vectors.
     */
    fun decompose(translation: MutableVec3d? = null, rotation: MutableQuatD? = null, scale: MutableVec3d? = null) {
        translation?.set(m03, m13, m23)

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
     * Returns the decomposed translation component of this transform matrix.
     *
     * @see decompose
     */
    fun getTranslation(result: MutableVec3d = MutableVec3d()): MutableVec3d {
        decompose(translation = result)
        return result
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
     */
    fun putTo(target: Float32Buffer) {
        target.put(m00); target.put(m10); target.put(m20); target.put(m30)
        target.put(m01); target.put(m11); target.put(m21); target.put(m31)
        target.put(m02); target.put(m12); target.put(m22); target.put(m32)
        target.put(m03); target.put(m13); target.put(m23); target.put(m33)
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     */
    fun putTo(target: MixedBuffer) {
        target.putFloat32(m00); target.putFloat32(m10); target.putFloat32(m20); target.putFloat32(m30)
        target.putFloat32(m01); target.putFloat32(m11); target.putFloat32(m21); target.putFloat32(m31)
        target.putFloat32(m02); target.putFloat32(m12); target.putFloat32(m22); target.putFloat32(m32)
        target.putFloat32(m03); target.putFloat32(m13); target.putFloat32(m23); target.putFloat32(m33)
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}, ${m02.toString(precision).padStart(width)}, ${m03.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}, ${m12.toString(precision).padStart(width)}, ${m13.toString(precision).padStart(width)}]\n")
        append("[${m20.toString(precision).padStart(width)}, ${m21.toString(precision).padStart(width)}, ${m22.toString(precision).padStart(width)}, ${m23.toString(precision).padStart(width)}]\n")
        append("[${m30.toString(precision).padStart(width)}, ${m31.toString(precision).padStart(width)}, ${m32.toString(precision).padStart(width)}, ${m33.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10, $m20, $m30), col1: ($m01, $m11, $m21, $m31), col2: ($m02, $m12, $m22, $m32), col3: ($m03, $m13, $m23, $m33) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat4d, eps: Double = FUZZY_EQ_D): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) && isFuzzyEqual(m02, that.m02, eps) && isFuzzyEqual(m03, that.m03, eps) &&
               isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps) && isFuzzyEqual(m12, that.m12, eps) && isFuzzyEqual(m13, that.m13, eps) &&
               isFuzzyEqual(m20, that.m20, eps) && isFuzzyEqual(m21, that.m21, eps) && isFuzzyEqual(m22, that.m22, eps) && isFuzzyEqual(m23, that.m23, eps) &&
               isFuzzyEqual(m30, that.m30, eps) && isFuzzyEqual(m31, that.m31, eps) && isFuzzyEqual(m32, that.m32, eps) && isFuzzyEqual(m33, that.m33, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat4d) return false
        return m00 == other.m00 && m01 == other.m01 && m02 == other.m02 && m03 == other.m03 &&
               m10 == other.m10 && m11 == other.m11 && m12 == other.m12 && m13 == other.m13 &&
               m20 == other.m20 && m21 == other.m21 && m22 == other.m22 && m23 == other.m23 &&
               m30 == other.m30 && m31 == other.m31 && m32 == other.m32 && m33 == other.m33
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m02.hashCode()
        result = 31 * result + m03.hashCode()

        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        result = 31 * result + m12.hashCode()
        result = 31 * result + m13.hashCode()

        result = 31 * result + m20.hashCode()
        result = 31 * result + m21.hashCode()
        result = 31 * result + m22.hashCode()
        result = 31 * result + m23.hashCode()

        result = 31 * result + m30.hashCode()
        result = 31 * result + m31.hashCode()
        result = 31 * result + m32.hashCode()
        result = 31 * result + m33.hashCode()
        return result
    }

    companion object {
        val IDENTITY = Mat4d(
            1.0, 0.0, 0.0, 0.0,
            0.0, 1.0, 0.0, 0.0,
            0.0, 0.0, 1.0, 0.0,
            0.0, 0.0, 0.0, 1.0
        )

        val ZERO = Mat4d(
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0,
            0.0, 0.0, 0.0, 0.0
        )

        fun fromArray(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat4d {
            return MutableMat4d().set(array, offset, order)
        }

        fun translation(t: Vec3d): Mat4d = MutableMat4d().translate(t)

        fun translation(tx: Double, ty: Double, tz: Double): Mat4d = MutableMat4d().translate(tx, ty, tz)

        fun rotation(angle: AngleD, axis: Vec3d): Mat4d = MutableMat4d().rotate(angle, axis)

        fun rotation(quaternion: QuatD): Mat4d = MutableMat4d().rotate(quaternion)

        fun rotation(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, order: EulerOrder = EulerOrder.ZYX): Mat4d {
            return MutableMat4d().rotate(eulerX, eulerY, eulerZ, order)
        }

        fun scale(s: Double): Mat4d = MutableMat4d().scale(s)

        fun scale(s: Vec3d): Mat4d = MutableMat4d().scale(s)

        fun composition(translation: Vec3d, rotation: QuatD, scale: Vec3d = Vec3d.ONES): Mat4d {
            return MutableMat4d().compose(translation, rotation, scale)
        }

        fun lookAt(eyePosition: Vec3d, lookAt: Vec3d, up: Vec3d): Mat4d {
            return MutableMat4d().lookAt(eyePosition, lookAt, up)
        }

        fun orthographic(left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double, depthRange: DepthRange, reversedDepth: Boolean): Mat4d {
            return MutableMat4d().orthographic(left, right, bottom, top, near, far, depthRange, reversedDepth)
        }

        fun perspective(fovy: AngleD, aspect: Double, near: Double, far: Double, depthRange: DepthRange): Mat4d {
            return MutableMat4d().perspective(fovy, aspect, near, far, depthRange)
        }

        fun perspectiveReversedDepthInfiniteRange(fovy: AngleD, aspect: Double, near: Double): Mat4d {
            return MutableMat4d().perspectiveReversedDepthInfiniteRange(fovy, aspect, near)
        }
    }
}

open class MutableMat4d(
    override var m00: Double, override var m01: Double, override var m02: Double, override var m03: Double,
    override var m10: Double, override var m11: Double, override var m12: Double, override var m13: Double,
    override var m20: Double, override var m21: Double, override var m22: Double, override var m23: Double,
    override var m30: Double, override var m31: Double, override var m32: Double, override var m33: Double
) : Mat4d(
    m00, m01, m02, m03,
    m10, m11, m12, m13,
    m20, m21, m22, m23,
    m30, m31, m32, m33
) {

    constructor(mat: Mat4d): this(
        mat.m00, mat.m01, mat.m02, mat.m03,
        mat.m10, mat.m11, mat.m12, mat.m13,
        mat.m20, mat.m21, mat.m22, mat.m23,
        mat.m30, mat.m31, mat.m32, mat.m33
    )

    constructor(col0: Vec4d, col1: Vec4d, col2: Vec4d, col3: Vec4d): this(
        col0.x, col1.x, col2.x, col3.x,
        col0.y, col1.y, col2.y, col3.y,
        col0.z, col1.z, col2.z, col3.z,
        col0.w, col1.w, col2.w, col3.w
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat4d): MutableMat4d {
        m00 = that.m00; m01 = that.m01; m02 = that.m02; m03 = that.m03
        m10 = that.m10; m11 = that.m11; m12 = that.m12; m13 = that.m13
        m20 = that.m20; m21 = that.m21; m22 = that.m22; m23 = that.m23
        m30 = that.m30; m31 = that.m31; m32 = that.m32; m33 = that.m33
        return this
    }

    fun set(col0: Vec4d, col1: Vec4d, col2: Vec4d, col3: Vec4d): MutableMat4d {
        m00 = col0.x; m01 = col1.x; m02 = col2.x; m03 = col3.x
        m10 = col0.y; m11 = col1.y; m12 = col2.y; m13 = col3.y
        m20 = col0.z; m21 = col1.z; m22 = col2.z; m23 = col3.z
        m30 = col0.w; m31 = col1.w; m32 = col2.w; m33 = col3.w
        return this
    }

    fun set(
        t00: Double, t01: Double, t02: Double, t03: Double,
        t10: Double, t11: Double, t12: Double, t13: Double,
        t20: Double, t21: Double, t22: Double, t23: Double,
        t30: Double, t31: Double, t32: Double, t33: Double
    ): MutableMat4d {
        m00 = t00; m01 = t01; m02 = t02; m03 = t03
        m10 = t10; m11 = t11; m12 = t12; m13 = t13
        m20 = t20; m21 = t21; m22 = t22; m23 = t23
        m30 = t30; m31 = t31; m32 = t32; m33 = t33
        return this
    }

    fun set(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat4d {
        var i = offset
        m00 = array[i++]; m01 = array[i++]; m02 = array[i++]; m03 = array[i++]
        m10 = array[i++]; m11 = array[i++]; m12 = array[i++]; m13 = array[i++]
        m20 = array[i++]; m21 = array[i++]; m22 = array[i++]; m23 = array[i++]
        m30 = array[i++]; m31 = array[i++]; m32 = array[i++]; m33 = array[i]
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
    operator fun plusAssign(that: Mat4d) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat4d) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat4d) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat4d): MutableMat4d {
        m00 += that.m00; m01 += that.m01; m02 += that.m02; m03 += that.m03
        m10 += that.m10; m11 += that.m11; m12 += that.m12; m13 += that.m13
        m20 += that.m20; m21 += that.m21; m22 += that.m22; m23 += that.m23
        m30 += that.m30; m31 += that.m31; m32 += that.m32; m33 += that.m33
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat4d): MutableMat4d {
        m00 -= that.m00; m01 -= that.m01; m02 -= that.m02; m03 -= that.m03
        m10 -= that.m10; m11 -= that.m11; m12 -= that.m12; m13 -= that.m13
        m20 -= that.m20; m21 -= that.m21; m22 -= that.m22; m23 -= that.m23
        m30 -= that.m30; m31 -= that.m31; m32 -= that.m32; m33 -= that.m33
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat4d): MutableMat4d = mul(
        that.m00, that.m01, that.m02, that.m03,
        that.m10, that.m11, that.m12, that.m13,
        that.m20, that.m21, that.m22, that.m23,
        that.m30, that.m31, that.m32, that.m33
    )

    /**
     * Inplace operation: Multiplies the upper left 3x3 section of this matrix by the given one changing the contents
     * of this matrix.
     */
    fun mulUpperLeft(that: Mat3d): MutableMat4d = mul33(
        that.m00, that.m01, that.m02,
        that.m10, that.m11, that.m12,
        that.m20, that.m21, that.m22
    )

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul(
        t00: Double, t01: Double, t02: Double, t03: Double,
        t10: Double, t11: Double, t12: Double, t13: Double,
        t20: Double, t21: Double, t22: Double, t23: Double,
        t30: Double, t31: Double, t32: Double, t33: Double
    ): MutableMat4d {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20 + m03 * t30
        val r10 = m10 * t00 + m11 * t10 + m12 * t20 + m13 * t30
        val r20 = m20 * t00 + m21 * t10 + m22 * t20 + m23 * t30
        val r30 = m30 * t00 + m31 * t10 + m32 * t20 + m33 * t30

        val r01 = m00 * t01 + m01 * t11 + m02 * t21 + m03 * t31
        val r11 = m10 * t01 + m11 * t11 + m12 * t21 + m13 * t31
        val r21 = m20 * t01 + m21 * t11 + m22 * t21 + m23 * t31
        val r31 = m30 * t01 + m31 * t11 + m32 * t21 + m33 * t31

        val r02 = m00 * t02 + m01 * t12 + m02 * t22 + m03 * t32
        val r12 = m10 * t02 + m11 * t12 + m12 * t22 + m13 * t32
        val r22 = m20 * t02 + m21 * t12 + m22 * t22 + m23 * t32
        val r32 = m30 * t02 + m31 * t12 + m32 * t22 + m33 * t32

        val r03 = m00 * t03 + m01 * t13 + m02 * t23 + m03 * t33
        val r13 = m10 * t03 + m11 * t13 + m12 * t23 + m13 * t33
        val r23 = m20 * t03 + m21 * t13 + m22 * t23 + m23 * t33
        val r33 = m30 * t03 + m31 * t13 + m32 * t23 + m33 * t33

        m00 = r00; m01 = r01; m02 = r02; m03 = r03
        m10 = r10; m11 = r11; m12 = r12; m13 = r13
        m20 = r20; m21 = r21; m22 = r22; m23 = r23
        m30 = r30; m31 = r31; m32 = r32; m33 = r33
        return this
    }

    @Suppress("NOTHING_TO_INLINE")  // it does make a difference (~25% faster)
    private inline fun mul33(
        t00: Double, t01: Double, t02: Double,
        t10: Double, t11: Double, t12: Double,
        t20: Double, t21: Double, t22: Double
    ): MutableMat4d {
        val r00 = m00 * t00 + m01 * t10 + m02 * t20
        val r10 = m10 * t00 + m11 * t10 + m12 * t20
        val r20 = m20 * t00 + m21 * t10 + m22 * t20
        val r30 = m30 * t00 + m31 * t10 + m32 * t20

        val r01 = m00 * t01 + m01 * t11 + m02 * t21
        val r11 = m10 * t01 + m11 * t11 + m12 * t21
        val r21 = m20 * t01 + m21 * t11 + m22 * t21
        val r31 = m30 * t01 + m31 * t11 + m32 * t21

        val r02 = m00 * t02 + m01 * t12 + m02 * t22
        val r12 = m10 * t02 + m11 * t12 + m12 * t22
        val r22 = m20 * t02 + m21 * t12 + m22 * t22
        val r32 = m30 * t02 + m31 * t12 + m32 * t22

        m00 = r00; m01 = r01; m02 = r02
        m10 = r10; m11 = r11; m12 = r12
        m20 = r20; m21 = r21; m22 = r22
        m30 = r30; m31 = r31; m32 = r32
        return this
    }

    /**
     * Applies the composition of the given [translation], [rotation] and [scale] to this transform matrix.
     */
    fun compose(translation: Vec3d, rotation: QuatD, scale: Vec3d = Vec3d.ONES): MutableMat4d {
        val x = rotation.x; val y = rotation.y; val z = rotation.z; val w = rotation.w
        val x2 = x + x;  val y2 = y + y;  val z2 = z + z
        val xx = x * x2; val xy = x * y2; val xz = x * z2
        val yy = y * y2; val yz = y * z2; val zz = z * z2
        val wx = w * x2; val wy = w * y2; val wz = w * z2

        val sx = scale.x; val sy = scale.y; val sz = scale.z

        return mul(
            (1 - (yy + zz)) * sx,   (xy - wz) * sy,         (xz + wy) * sz,         translation.x,
            (xy + wz) * sx,         (1 - (xx + zz)) * sy,   (yz - wx) * sz,         translation.y,
            (xz - wy) * sx,         (yz + wx) * sy,         (1 - (xx + yy)) * sz,   translation.z,
            0.0,                     0.0,                     0.0,                     1.0
        )
    }

    /**
     * Inplace operation: Adds the given translation [t] transform to this matrix.
     */
    fun translate(t: Vec3d): MutableMat4d = translate(t.x, t.y, t.z)

    /**
     * Inplace operation: Adds the given translation [tx], [ty], [tz] transform to this matrix.
     */
    fun translate(tx: Double, ty: Double, tz: Double): MutableMat4d {
        m03 += m00 * tx + m01 * ty + m02 * tz
        m13 += m10 * tx + m11 * ty + m12 * tz
        m23 += m20 * tx + m21 * ty + m22 * tz
        return this
    }

    /**
     * Inplace operation: Rotates this matrix around the given [axis] by the given [angle].
     */
    fun rotate(angle: AngleD, axis: Vec3d): MutableMat4d {
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given quaternion rotation. [quaternion] is expected to be a valid
     * rotation quaternion with unit length.
     */
    fun rotate(quaternion: QuatD): MutableMat4d {
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Rotates this matrix by the given euler angles.
     */
    fun rotate(eulerX: AngleD, eulerY: AngleD, eulerZ: AngleD, order: EulerOrder = EulerOrder.ZYX): MutableMat4d {
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
                t10 = a * f;        t11 = a * e;        t12 = -b
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

        return mul33(
            t00, t01, t02,
            t10, t11, t12,
            t20, t21, t22
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Double): MutableMat4d = mul33(
        s, 0.0, 0.0,
        0.0, s, 0.0,
        0.0, 0.0, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec3d): MutableMat4d = mul33(
        s.x, 0.0, 0.0,
        0.0, s.y, 0.0,
        0.0, 0.0, s.z
    )

    /**
     * Inplace operation: Applies a look-at transform to this matrix.
     */
    fun lookAt(eyePosition: Vec3d, lookAt: Vec3d, up: Vec3d): MutableMat4d {
        val z = MutableVec3d(lookAt - eyePosition)
        val fLen = z.length()
        if (fLen.isFuzzyZero()) {
            // eye position and look at are equal
            z.z = 1.0
        } else {
            z *= 1.0 / fLen
        }

        val x = z.cross(up, MutableVec3d())
        val sLen = x.length()
        if (sLen.isFuzzyZero()) {
            // forward vector is parallel to up
            if (abs(z.dot(Vec3d.Y_AXIS)) > 0.9900000095367432) {
                z.cross(Vec3d.NEG_Z_AXIS, x)
                x.norm()
            } else {
                z.cross(Vec3d.Y_AXIS, x)
                x.norm()
            }
        } else {
            x *= 1.0 / sLen
        }

        val y = x.cross(z, MutableVec3d())

        return mul(
            x.x, x.y, x.z, -x.dot(eyePosition),
            y.x, y.y, y.z, -y.dot(eyePosition),
            -z.x, -z.y, -z.z, z.dot(eyePosition),
            0.0, 0.0, 0.0, 1.0
        )
    }

    /**
     * Inplace operation: Applies an orthographic projection transform to this matrix.
     */
    fun orthographic(left: Double, right: Double, bottom: Double, top: Double, near: Double, far: Double, depthRange: DepthRange, reversedDepth: Boolean): MutableMat4d {
        val farD = if (reversedDepth) near else far
        val nearD = if (reversedDepth) far else near

        if (left == right) {
            throw IllegalArgumentException("left == right")
        }
        if (bottom == top) {
            throw IllegalArgumentException("bottom == top")
        }
        if (nearD == farD) {
            throw IllegalArgumentException("near == far")
        }

        val w = 1.0 / (right - left)
        val h = 1.0 / (top - bottom)
        val d = 1.0 / (farD - nearD)

        val x = (right + left) * w
        val y = (top + bottom) * h

        val z: Double
        val zd: Double
        when (depthRange) {
            DepthRange.NEGATIVE_ONE_TO_ONE -> {
                z = (farD + nearD) * d
                zd = -2 * d
            }
            DepthRange.ZERO_TO_ONE -> {
                z = nearD * d
                zd = -1 * d
            }
        }

        return mul(
            2 * w, 0.0, 0.0, -x,
            0.0, 2 * h, 0.0, -y,
            0.0, 0.0, zd, -z,
            0.0, 0.0, 0.0, 1.0
        )
    }

    /**
     * Inplace operation: Applies a perspective projection transform to this matrix.
     */
    fun perspective(fovy: AngleD, aspect: Double, near: Double, far: Double, depthRange: DepthRange): MutableMat4d {
        val f = 1.0 / tan(fovy.rad / 2.0)
        val rangeRecip = 1.0 / (near - far)

        val z: Double
        val zt: Double
        when (depthRange) {
            DepthRange.NEGATIVE_ONE_TO_ONE -> {
                z = (far + near) * rangeRecip
                zt = 2.0 * far * near * rangeRecip
            }
            DepthRange.ZERO_TO_ONE -> {
                z = far * rangeRecip
                zt = far * near * rangeRecip
            }
        }

        return mul(
            f / aspect, 0.0, 0.0, 0.0,
            0.0, f, 0.0, 0.0,
            0.0, 0.0, z, zt,
            0.0, 0.0, -1.0, 0.0
        )
    }

    /**
     * Inplace operation: Applies a perspective projection transform for reversed depth with an infinite far plane
     * to this matrix.
     */
    fun perspectiveReversedDepthInfiniteRange(fovy: AngleD, aspect: Double, near: Double): MutableMat4d {
        val f = 1.0 / tan(fovy.rad / 2.0)
        return mul(
            f / aspect, 0.0, 0.0, 0.0,
            0.0, f, 0.0, 0.0,
            0.0, 0.0, 0.0, near,
            0.0, 0.0, -1.0, 0.0
        )
    }

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * Based on http://www.euclideanspace.com/maths/algebra/matrix/functions/inverse/fourD/index.htm
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Double = 0.0): Boolean {
        val det = determinant()
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val r00 = m12*m23*m31 - m13*m22*m31 + m13*m21*m32 - m11*m23*m32 - m12*m21*m33 + m11*m22*m33
        val r01 = m03*m22*m31 - m02*m23*m31 - m03*m21*m32 + m01*m23*m32 + m02*m21*m33 - m01*m22*m33
        val r02 = m02*m13*m31 - m03*m12*m31 + m03*m11*m32 - m01*m13*m32 - m02*m11*m33 + m01*m12*m33
        val r03 = m03*m12*m21 - m02*m13*m21 - m03*m11*m22 + m01*m13*m22 + m02*m11*m23 - m01*m12*m23
        val r10 = m13*m22*m30 - m12*m23*m30 - m13*m20*m32 + m10*m23*m32 + m12*m20*m33 - m10*m22*m33
        val r11 = m02*m23*m30 - m03*m22*m30 + m03*m20*m32 - m00*m23*m32 - m02*m20*m33 + m00*m22*m33
        val r12 = m03*m12*m30 - m02*m13*m30 - m03*m10*m32 + m00*m13*m32 + m02*m10*m33 - m00*m12*m33
        val r13 = m02*m13*m20 - m03*m12*m20 + m03*m10*m22 - m00*m13*m22 - m02*m10*m23 + m00*m12*m23
        val r20 = m11*m23*m30 - m13*m21*m30 + m13*m20*m31 - m10*m23*m31 - m11*m20*m33 + m10*m21*m33
        val r21 = m03*m21*m30 - m01*m23*m30 - m03*m20*m31 + m00*m23*m31 + m01*m20*m33 - m00*m21*m33
        val r22 = m01*m13*m30 - m03*m11*m30 + m03*m10*m31 - m00*m13*m31 - m01*m10*m33 + m00*m11*m33
        val r23 = m03*m11*m20 - m01*m13*m20 - m03*m10*m21 + m00*m13*m21 + m01*m10*m23 - m00*m11*m23
        val r30 = m12*m21*m30 - m11*m22*m30 - m12*m20*m31 + m10*m22*m31 + m11*m20*m32 - m10*m21*m32
        val r31 = m01*m22*m30 - m02*m21*m30 + m02*m20*m31 - m00*m22*m31 - m01*m20*m32 + m00*m21*m32
        val r32 = m02*m11*m30 - m01*m12*m30 - m02*m10*m31 + m00*m12*m31 + m01*m10*m32 - m00*m11*m32
        val r33 = m01*m12*m20 - m02*m11*m20 + m02*m10*m21 - m00*m12*m21 - m01*m10*m22 + m00*m11*m22

        val s = 1.0 / det
        m00 = r00 * s; m01 = r01 * s; m02 = r02 * s; m03 = r03 * s
        m10 = r10 * s; m11 = r11 * s; m12 = r12 * s; m13 = r13 * s
        m20 = r20 * s; m21 = r21 * s; m22 = r22 * s; m23 = r23 * s
        m30 = r30 * s; m31 = r31 * s; m32 = r32 * s; m33 = r33 * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat4d {
        var t = m01; m01 = m10; m10 = t
            t = m02; m02 = m20; m20 = t
            t = m03; m03 = m30; m30 = t
            t = m12; m12 = m21; m21 = t
            t = m13; m13 = m31; m31 = t
            t = m23; m23 = m32; m32 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec4d].
     */
    operator fun set(col: Int, that: Vec4d) {
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
                3 -> m03 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                2 -> m12 = that
                3 -> m13 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            2 -> when (col) {
                0 -> m20 = that
                1 -> m21 = that
                2 -> m22 = that
                3 -> m23 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            3 -> when (col) {
                0 -> m30 = that
                1 -> m31 = that
                2 -> m32 = that
                3 -> m33 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
    }

    /**
     * Sets the specified column to the given [Vec4d].
     */
    fun setColumn(col: Int, that: Vec4d): MutableMat4d {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z; m30 = that.w }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z; m31 = that.w }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z; m32 = that.w }
            3 -> { m03 = that.x; m13 = that.y; m23 = that.z; m33 = that.w }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified column to the given [Vec3d] and [w].
     */
    fun setColumn(col: Int, that: Vec3d, w: Double): MutableMat4d {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y; m20 = that.z; m30 = w }
            1 -> { m01 = that.x; m11 = that.y; m21 = that.z; m31 = w }
            2 -> { m02 = that.x; m12 = that.y; m22 = that.z; m32 = w }
            3 -> { m03 = that.x; m13 = that.y; m23 = that.z; m33 = w }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec4d].
     */
    fun setRow(row: Int, that: Vec4d): MutableMat4d {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z; m03 = that.w }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z; m13 = that.w }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z; m23 = that.w }
            3 -> { m30 = that.x; m31 = that.y; m32 = that.z; m33 = that.w }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec3d] and [w].
     */
    fun setRow(row: Int, that: Vec3d, w: Double): MutableMat4d {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y; m02 = that.z; m03 = w }
            1 -> { m10 = that.x; m11 = that.y; m12 = that.z; m13 = w }
            2 -> { m20 = that.x; m21 = that.y; m22 = that.z; m23 = w }
            3 -> { m30 = that.x; m31 = that.y; m32 = that.z; m33 = w }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..3)")
        }
        return this
    }

    /**
     * Sets the upper left 3x3 section of this matrix to the given one.
     */
    fun setUpperLeft(that: Mat3d): MutableMat4d {
        m00 = that.m00; m01 = that.m01; m02 = that.m02
        m10 = that.m10; m11 = that.m11; m12 = that.m12
        m20 = that.m20; m21 = that.m21; m22 = that.m22
        return this
    }
}
