package de.fabmax.kool.math

import de.fabmax.kool.toString
import de.fabmax.kool.util.Float32Buffer
import de.fabmax.kool.util.MixedBuffer

// <template> Changes made within the template section will also affect the other type variants of this class

open class Mat2f(
    open val m00: Float, open val m01: Float,
    open val m10: Float, open val m11: Float,
) {

    constructor(mat: Mat2f) : this(
        mat.m00, mat.m01,
        mat.m10, mat.m11,
    )

    constructor(col0: Vec2f, col1: Vec2f) : this(
        col0.x, col1.x,
        col0.y, col1.y,
    )

    operator fun component1(): Vec2f = Vec2f(m00, m10)
    operator fun component2(): Vec2f = Vec2f(m01, m11)

    operator fun times(that: Mat2f): MutableMat2f = mul(that, MutableMat2f())

    operator fun plus(that: Mat2f): MutableMat2f = add(that, MutableMat2f())

    operator fun minus(that: Mat2f): MutableMat2f = subtract(that, MutableMat2f())

    operator fun times(that: Vec2f): MutableVec2f = transform(that, MutableVec2f())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat2f, result: MutableMat2f): MutableMat2f = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat2f, result: MutableMat2f): MutableMat2f = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat2f, result: MutableMat2f): MutableMat2f = result.set(this).mul(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec2f] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec2f, result: MutableVec2f): MutableVec2f {
        val x = that.x * m00 + that.y * m01
        val y = that.x * m10 + that.y * m11
        return result.set(x, y)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec2f] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec2f): MutableVec2f = transform(that, that)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2f.rotate
     */
    fun rotate(angle: AngleF, result: MutableMat2f): MutableMat2f = result.set(this).rotate(angle)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2f.scale
     */
    fun scale(scale: Float, result: MutableMat2f): MutableMat2f = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2f.scale
     */
    fun scale(scale: Vec2f, result: MutableMat2f): MutableMat2f = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat2f): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat2f): MutableMat2f = result.set(this).transpose()

    /**
     * Copies the specified column into a [Vec2f] and returns it.
     */
    operator fun get(col: Int): Vec2f = getColumn(col)

    /**
     * Returns the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun get(row: Int, col: Int): Float {
        return when (row) {
            0 -> when (col) {
                0 -> m00
                1 -> m01
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec2f] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec2f = MutableVec2f()): MutableVec2f {
        return when (col) {
            0 -> result.set(m00, m10)
            1 -> result.set(m01, m11)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec2f] and returns it.
     */
    fun getRow(row: Int, result: MutableVec2f = MutableVec2f()): MutableVec2f {
        return when (row) {
            0 -> result.set(m00, m01)
            1 -> result.set(m10, m11)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Appends the components of this matrix to the given [Float32Buffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: Float32Buffer, withPadding: Boolean = true) {
        target.put(m00)
        target.put(m10)
        if (withPadding) {
            target.put(0f)
            target.put(0f)
        }
        target.put(m01)
        target.put(m11)
        if (withPadding) {
            target.put(0f)
            target.put(0f)
        }
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: MixedBuffer, withPadding: Boolean = true) {
        target.putFloat32(m00)
        target.putFloat32(m10)
        if (withPadding) {
            target.putFloat32(0f)
            target.putFloat32(0f)
        }
        target.putFloat32(m01)
        target.putFloat32(m11)
        if (withPadding) {
            target.putFloat32(0f)
            target.putFloat32(0f)
        }
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10), col1: ($m01, $m11) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat2f, eps: Float = FUZZY_EQ_F): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) &&
                isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat2f) return false
        return m00 == other.m00 && m01 == other.m01 &&
                m10 == other.m10 && m11 == other.m11
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        return result
    }

    companion object {
        val IDENTITY = Mat2f(
            1f, 0f,
            0f, 1f
        )

        val ZERO = Mat2f(
            0f, 0f,
            0f, 0f
        )

        fun fromArray(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat2f {
            return MutableMat2f().set(array, offset, order)
        }

        fun rotation(angle: AngleF): Mat2f = MutableMat2f().rotate(angle)

        fun scale(s: Float): Mat2f = MutableMat2f().scale(s)

        fun scale(s: Vec2f): Mat2f = MutableMat2f().scale(s)

        fun composition(rotation: AngleF, scale: Vec2f): Mat2f {
            return MutableMat2f().rotate(rotation).scale(scale)
        }
    }
}

open class MutableMat2f(
    override var m00: Float, override var m01: Float,
    override var m10: Float, override var m11: Float,
) : Mat2f(
    m00, m01,
    m10, m11,
) {

    constructor(mat: Mat2f): this(
        mat.m00, mat.m01,
        mat.m10, mat.m11,
    )

    constructor(col0: Vec2f, col1: Vec2f): this(
        col0.x, col1.x,
        col0.y, col1.y,
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat2f): MutableMat2f {
        m00 = that.m00; m01 = that.m01
        m10 = that.m10; m11 = that.m11
        return this
    }

    fun set(col0: Vec2f, col1: Vec2f): MutableMat2f {
        m00 = col0.x; m01 = col1.x
        m10 = col0.y; m11 = col1.y
        return this
    }

    fun set(
        t00: Float, t01: Float,
        t10: Float, t11: Float
    ): MutableMat2f {
        m00 = t00; m01 = t01
        m10 = t10; m11 = t11
        return this
    }

    fun set(array: FloatArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat2f {
        var i = offset
        m00 = array[i++]; m01 = array[i++]
        m10 = array[i++]; m11 = array[i]
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
    operator fun plusAssign(that: Mat2f) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat2f) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat2f) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat2f): MutableMat2f {
        m00 += that.m00; m01 += that.m01
        m10 += that.m10; m11 += that.m11
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat2f): MutableMat2f {
        m00 -= that.m00; m01 -= that.m01
        m10 -= that.m10; m11 -= that.m11
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat2f): MutableMat2f = mul(
        that.m00, that.m01,
        that.m10, that.m11
    )

    private fun mul(
        t00: Float, t01: Float,
        t10: Float, t11: Float
    ): MutableMat2f {
        val r00 = m00 * t00 + m01 * t10
        val r10 = m10 * t00 + m11 * t10

        val r01 = m00 * t01 + m01 * t11
        val r11 = m10 * t01 + m11 * t11

        m00 = r00; m01 = r01
        m10 = r10; m11 = r11
        return this
    }

    /**
     * Inplace operation: Rotates this matrix by the given [angle].
     */
    fun rotate(angle: AngleF): MutableMat2f {
        val s = angle.sin
        val c = angle.cos
        return mul(
            c, -s,
            s, c
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Float): MutableMat2f = mul(
        s, 0f,
        0f, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec2f): MutableMat2f = mul(
        s.x, 0f,
        0f, s.y
    )

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Float = 0f): Boolean {
        val det = m00 * m11 - m01 * m10
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val a = m00
        val b = m01
        val c = m10
        val d = m11
        val s = 1f / det

        m00 = d * s
        m01 = -b * s
        m10 = -c * s
        m11 = a * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat2f {
        var t = m01
        m01 = m10
        m10 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec2f].
     */
    operator fun set(col: Int, that: Vec2f) {
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
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Sets the specified column to the given [Vec2f].
     */
    fun setColumn(col: Int, that: Vec2f): MutableMat2f {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y }
            1 -> { m01 = that.x; m11 = that.y }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec2f].
     */
    fun setRow(row: Int, that: Vec2f): MutableMat2f {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y }
            1 -> { m10 = that.x; m11 = that.y }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


open class Mat2d(
    open val m00: Double, open val m01: Double,
    open val m10: Double, open val m11: Double,
) {

    constructor(mat: Mat2d) : this(
        mat.m00, mat.m01,
        mat.m10, mat.m11,
    )

    constructor(col0: Vec2d, col1: Vec2d) : this(
        col0.x, col1.x,
        col0.y, col1.y,
    )

    operator fun component1(): Vec2d = Vec2d(m00, m10)
    operator fun component2(): Vec2d = Vec2d(m01, m11)

    operator fun times(that: Mat2d): MutableMat2d = mul(that, MutableMat2d())

    operator fun plus(that: Mat2d): MutableMat2d = add(that, MutableMat2d())

    operator fun minus(that: Mat2d): MutableMat2d = subtract(that, MutableMat2d())

    operator fun times(that: Vec2d): MutableVec2d = transform(that, MutableVec2d())

    /**
     * Adds the given matrix to this one and stores the result in [result].
     */
    fun add(that: Mat2d, result: MutableMat2d): MutableMat2d = result.set(this).add(that)

    /**
     * Subtracts the given matrix from this one and stores the result in [result].
     */
    fun subtract(that: Mat2d, result: MutableMat2d): MutableMat2d = result.set(this).subtract(that)

    /**
     * Multiplies this matrix with the given [that] one and stores the result in [result].
     */
    fun mul(that: Mat2d, result: MutableMat2d): MutableMat2d = result.set(this).mul(that)

    /**
     * Transforms (i.e. multiplies) the given [Vec2d] with this matrix and stores the resulting transformed vector in [result].
     */
    fun transform(that: Vec2d, result: MutableVec2d): MutableVec2d {
        val x = that.x * m00 + that.y * m01
        val y = that.x * m10 + that.y * m11
        return result.set(x, y)
    }

    /**
     * Transforms (i.e. multiplies) the given [MutableVec2d] by this matrix, changing the contents of the given
     * vector.
     */
    fun transform(that: MutableVec2d): MutableVec2d = transform(that, that)

    /**
     * Adds the given rotation transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2d.rotate
     */
    fun rotate(angle: AngleD, result: MutableMat2d): MutableMat2d = result.set(this).rotate(angle)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2d.scale
     */
    fun scale(scale: Double, result: MutableMat2d): MutableMat2d = result.set(this).scale(scale)

    /**
     * Adds the given scale transform to this matrix and stores the result in [result].
     *
     * @see MutableMat2d.scale
     */
    fun scale(scale: Vec2d, result: MutableMat2d): MutableMat2d = result.set(this).scale(scale)

    /**
     * Sets the given result matrix to this matrix and inverts it.
     *
     * @return true, if inversion succeeded, false otherwise (result matrix will contain an unchanged copy of
     *         this matrix)
     */
    fun invert(result: MutableMat2d): Boolean = result.set(this).invert()

    /**
     * Sets the given result matrix to the transpose of this matrix.
     */
    fun transpose(result: MutableMat2d): MutableMat2d = result.set(this).transpose()

    /**
     * Copies the specified column into a [Vec2d] and returns it.
     */
    operator fun get(col: Int): Vec2d = getColumn(col)

    /**
     * Returns the value at the given row / column index. Notice: Access by index is rather slow, and you should prefer
     * accessing the matrix members directly whenever possible.
     */
    operator fun get(row: Int, col: Int): Double {
        return when (row) {
            0 -> when (col) {
                0 -> m00
                1 -> m01
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            1 -> when (col) {
                0 -> m10
                1 -> m11
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Copies the specified column into the given [MutableVec2d] and returns it.
     */
    fun getColumn(col: Int, result: MutableVec2d = MutableVec2d()): MutableVec2d {
        return when (col) {
            0 -> result.set(m00, m10)
            1 -> result.set(m01, m11)
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
        }
    }

    /**
     * Copies the specified row into the given [MutableVec2d] and returns it.
     */
    fun getRow(row: Int, result: MutableVec2d = MutableVec2d()): MutableVec2d {
        return when (row) {
            0 -> result.set(m00, m01)
            1 -> result.set(m10, m11)
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Appends the components of this matrix to the given [Float32Buffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: Float32Buffer, withPadding: Boolean = true) {
        target.put(m00)
        target.put(m10)
        if (withPadding) {
            target.put(0.0)
            target.put(0.0)
        }
        target.put(m01)
        target.put(m11)
        if (withPadding) {
            target.put(0.0)
            target.put(0.0)
        }
    }

    /**
     * Appends the components of this matrix to the given [MixedBuffer]. The matrix is stored in column-major
     * order, as expected by all supported graphics APIs.
     * If [withPadding] is true (the default value), the matrix is not densely stored into the buffer but with
     * additional 4 bytes of padding after each column as expected by the common buffer layouts.
     */
    fun putTo(target: MixedBuffer, withPadding: Boolean = true) {
        target.putFloat32(m00)
        target.putFloat32(m10)
        if (withPadding) {
            target.putFloat32(0.0)
            target.putFloat32(0.0)
        }
        target.putFloat32(m01)
        target.putFloat32(m11)
        if (withPadding) {
            target.putFloat32(0.0)
            target.putFloat32(0.0)
        }
    }

    /**
     * Prints this matrix in a somewhat formatted form to the console.
     */
    fun print(precision: Int = 3, width: Int = 8) = println(toStringFormatted(precision, width))

    fun toStringFormatted(precision: Int = 3, width: Int = 8) = buildString {
        append("[${m00.toString(precision).padStart(width)}, ${m01.toString(precision).padStart(width)}]\n")
        append("[${m10.toString(precision).padStart(width)}, ${m11.toString(precision).padStart(width)}]")
    }

    override fun toString(): String {
        return "{ col0: ($m00, $m10), col1: ($m01, $m11) }"
    }

    /**
     * Checks matrix components for equality using [de.fabmax.kool.math.isFuzzyEqual], that is all components must
     * have a difference less or equal to [eps].
     */
    fun isFuzzyEqual(that: Mat2d, eps: Double = FUZZY_EQ_D): Boolean {
        return isFuzzyEqual(m00, that.m00, eps) && isFuzzyEqual(m01, that.m01, eps) &&
                isFuzzyEqual(m10, that.m10, eps) && isFuzzyEqual(m11, that.m11, eps)
    }

    /**
     * Checks matrix components for equality (using '==' operator). For better numeric stability consider using
     * [isFuzzyEqual].
     */
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Mat2d) return false
        return m00 == other.m00 && m01 == other.m01 &&
                m10 == other.m10 && m11 == other.m11
    }

    override fun hashCode(): Int {
        var result = m00.hashCode()
        result = 31 * result + m01.hashCode()
        result = 31 * result + m10.hashCode()
        result = 31 * result + m11.hashCode()
        return result
    }

    companion object {
        val IDENTITY = Mat2d(
            1.0, 0.0,
            0.0, 1.0
        )

        val ZERO = Mat2d(
            0.0, 0.0,
            0.0, 0.0
        )

        fun fromArray(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): Mat2d {
            return MutableMat2d().set(array, offset, order)
        }

        fun rotation(angle: AngleD): Mat2d = MutableMat2d().rotate(angle)

        fun scale(s: Double): Mat2d = MutableMat2d().scale(s)

        fun scale(s: Vec2d): Mat2d = MutableMat2d().scale(s)

        fun composition(rotation: AngleD, scale: Vec2d): Mat2d {
            return MutableMat2d().rotate(rotation).scale(scale)
        }
    }
}

open class MutableMat2d(
    override var m00: Double, override var m01: Double,
    override var m10: Double, override var m11: Double,
) : Mat2d(
    m00, m01,
    m10, m11,
) {

    constructor(mat: Mat2d): this(
        mat.m00, mat.m01,
        mat.m10, mat.m11,
    )

    constructor(col0: Vec2d, col1: Vec2d): this(
        col0.x, col1.x,
        col0.y, col1.y,
    )

    constructor(): this(IDENTITY)

    fun set(that: Mat2d): MutableMat2d {
        m00 = that.m00; m01 = that.m01
        m10 = that.m10; m11 = that.m11
        return this
    }

    fun set(col0: Vec2d, col1: Vec2d): MutableMat2d {
        m00 = col0.x; m01 = col1.x
        m10 = col0.y; m11 = col1.y
        return this
    }

    fun set(
        t00: Double, t01: Double,
        t10: Double, t11: Double
    ): MutableMat2d {
        m00 = t00; m01 = t01
        m10 = t10; m11 = t11
        return this
    }

    fun set(array: DoubleArray, offset: Int = 0, order: MatrixArrayOrder = MatrixArrayOrder.COLUMN_MAJOR): MutableMat2d {
        var i = offset
        m00 = array[i++]; m01 = array[i++]
        m10 = array[i++]; m11 = array[i]
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
    operator fun plusAssign(that: Mat2d) {
        add(that)
    }

    /**
     * Inplace operation: Subtracts the given matrix from this one changing the contents of this matrix to the
     * result.
     */
    operator fun minusAssign(that: Mat2d) {
        subtract(that)
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    operator fun timesAssign(that: Mat2d) {
        mul(that)
    }

    /**
     * Inplace operation: Adds the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun add(that: Mat2d): MutableMat2d {
        m00 += that.m00; m01 += that.m01
        m10 += that.m10; m11 += that.m11
        return this
    }

    /**
     * Inplace operation: Subtracts the given matrix to this one changing the contents of this matrix to the
     * result.
     */
    fun subtract(that: Mat2d): MutableMat2d {
        m00 -= that.m00; m01 -= that.m01
        m10 -= that.m10; m11 -= that.m11
        return this
    }

    /**
     * Inplace operation: Multiplies this matrix with the given one and changes the contents of this matrix to the
     * result.
     */
    fun mul(that: Mat2d): MutableMat2d = mul(
        that.m00, that.m01,
        that.m10, that.m11
    )

    private fun mul(
        t00: Double, t01: Double,
        t10: Double, t11: Double
    ): MutableMat2d {
        val r00 = m00 * t00 + m01 * t10
        val r10 = m10 * t00 + m11 * t10

        val r01 = m00 * t01 + m01 * t11
        val r11 = m10 * t01 + m11 * t11

        m00 = r00; m01 = r01
        m10 = r10; m11 = r11
        return this
    }

    /**
     * Inplace operation: Rotates this matrix by the given [angle].
     */
    fun rotate(angle: AngleD): MutableMat2d {
        val s = angle.sin
        val c = angle.cos
        return mul(
            c, -s,
            s, c
        )
    }

    /**
     * Inplace operation: Scales this matrix by the given factor.
     */
    fun scale(s: Double): MutableMat2d = mul(
        s, 0.0,
        0.0, s
    )

    /**
     * Inplace operation: Scales this matrix by the given factors.
     */
    fun scale(s: Vec2d): MutableMat2d = mul(
        s.x, 0.0,
        0.0, s.y
    )

    /**
     * Inplace operation: Inverts this matrix. Returns true if inversion was successful, false otherwise. If false
     * is returned (inversion did not succeed), the contents of the matrix remain unchanged.
     *
     * @return true, if inversion succeeded, false otherwise (matrix remains unchanged)
     */
    fun invert(eps: Double = 0.0): Boolean {
        val det = m00 * m11 - m01 * m10
        if (det.isFuzzyZero(eps)) {
            return false
        }

        val a = m00
        val b = m01
        val c = m10
        val d = m11
        val s = 1.0 / det

        m00 = d * s
        m01 = -b * s
        m10 = -c * s
        m11 = a * s
        return true
    }

    /**
     * Inplace operation: Transposes this matrix.
     */
    fun transpose(): MutableMat2d {
        var t = m01
        m01 = m10
        m10 = t
        return this
    }

    /**
     * Sets the specified column to the given [Vec2d].
     */
    operator fun set(col: Int, that: Vec2d) {
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
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            1 -> when (col) {
                0 -> m10 = that
                1 -> m11 = that
                else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
            }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
    }

    /**
     * Sets the specified column to the given [Vec2d].
     */
    fun setColumn(col: Int, that: Vec2d): MutableMat2d {
        when (col) {
            0 -> { m00 = that.x; m10 = that.y }
            1 -> { m01 = that.x; m11 = that.y }
            else -> throw IndexOutOfBoundsException("Column index $col not in bounds (0..1)")
        }
        return this
    }

    /**
     * Sets the specified row to the given [Vec2d].
     */
    fun setRow(row: Int, that: Vec2d): MutableMat2d {
        when (row) {
            0 -> { m00 = that.x; m01 = that.y }
            1 -> { m10 = that.x; m11 = that.y }
            else -> throw IndexOutOfBoundsException("Row index $row not in bounds (0..1)")
        }
        return this
    }
}
