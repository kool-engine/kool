package de.fabmax.kool.math

// <template> Changes made within the template section will also affect the other type variants of this class

class Mat4fStack(val stackSize: Int = DEFAULT_STACK_SIZE) : MutableMat4f() {
    companion object {
        const val DEFAULT_STACK_SIZE = 32
    }

    private var stackIndex = 0
    private val stack = FloatArray(16 * stackSize)

    fun push(): Mat4fStack {
        if (stackIndex >= stackSize) {
            throw IndexOutOfBoundsException("Matrix stack overflow")
        }
        val offset = stackIndex * 16
        stack[offset +  0] = m00; stack[offset +  1] = m01; stack[offset +  2] = m02; stack[offset +  3] = m03
        stack[offset +  4] = m10; stack[offset +  5] = m11; stack[offset +  6] = m12; stack[offset +  7] = m13
        stack[offset +  8] = m20; stack[offset +  9] = m21; stack[offset + 10] = m22; stack[offset + 11] = m23
        stack[offset + 12] = m30; stack[offset + 13] = m31; stack[offset + 14] = m32; stack[offset + 15] = m33
        stackIndex++
        return this
    }

    fun pop(): Mat4fStack {
        if (stackIndex <= 0) {
            throw IndexOutOfBoundsException("Matrix stack underflow")
        }
        stackIndex--
        val offset = stackIndex * 16
        m00 = stack[offset +  0]; m01 = stack[offset +  1]; m02 = stack[offset +  2]; m03 = stack[offset +  3]
        m10 = stack[offset +  4]; m11 = stack[offset +  5]; m12 = stack[offset +  6]; m13 = stack[offset +  7]
        m20 = stack[offset +  8]; m21 = stack[offset +  9]; m22 = stack[offset + 10]; m23 = stack[offset + 11]
        m30 = stack[offset + 12]; m31 = stack[offset + 13]; m32 = stack[offset + 14]; m33 = stack[offset + 15]
        return this
    }

    fun reset(): Mat4fStack {
        stackIndex = 0
        setIdentity()
        return this
    }
}

// </template> End of template section, DO NOT EDIT BELOW THIS!


class Mat4dStack(val stackSize: Int = DEFAULT_STACK_SIZE) : MutableMat4d() {
    companion object {
        const val DEFAULT_STACK_SIZE = 32
    }

    private var stackIndex = 0
    private val stack = DoubleArray(16 * stackSize)

    fun push(): Mat4dStack {
        if (stackIndex >= stackSize) {
            throw IndexOutOfBoundsException("Matrix stack overflow")
        }
        val offset = stackIndex * 16
        stack[offset +  0] = m00; stack[offset +  1] = m01; stack[offset +  2] = m02; stack[offset +  3] = m03
        stack[offset +  4] = m10; stack[offset +  5] = m11; stack[offset +  6] = m12; stack[offset +  7] = m13
        stack[offset +  8] = m20; stack[offset +  9] = m21; stack[offset + 10] = m22; stack[offset + 11] = m23
        stack[offset + 12] = m30; stack[offset + 13] = m31; stack[offset + 14] = m32; stack[offset + 15] = m33
        stackIndex++
        return this
    }

    fun pop(): Mat4dStack {
        if (stackIndex <= 0) {
            throw IndexOutOfBoundsException("Matrix stack underflow")
        }
        stackIndex--
        val offset = stackIndex * 16
        m00 = stack[offset +  0]; m01 = stack[offset +  1]; m02 = stack[offset +  2]; m03 = stack[offset +  3]
        m10 = stack[offset +  4]; m11 = stack[offset +  5]; m12 = stack[offset +  6]; m13 = stack[offset +  7]
        m20 = stack[offset +  8]; m21 = stack[offset +  9]; m22 = stack[offset + 10]; m23 = stack[offset + 11]
        m30 = stack[offset + 12]; m31 = stack[offset + 13]; m32 = stack[offset + 14]; m33 = stack[offset + 15]
        return this
    }

    fun reset(): Mat4dStack {
        stackIndex = 0
        setIdentity()
        return this
    }
}
