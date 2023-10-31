package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.MixedBuffer

sealed class Uniform<T>(
    var value: T,
    val name: String,

    /**
     * Number of elements in case this is an array type (1 otherwise)
     */
    val size: Int = 1
) {

    @Deprecated("", replaceWith = ReplaceWith("size"))
    val length: Int get() = size

    val isArray: Boolean
        get() = size > 1

    /**
     * Appends this uniform's data to the supplied buffer at its current position. Does not check for alignment, i.e.
     * the buffer position needs to be correctly set before calling this method. Trailing padding bytes are appended
     * until the specified number of bytes [len] are appended.
     *
     * For array types, [len] is also used to decide whether to add intermediate padding (Std140 layout) or not.
     */
    abstract fun putToBuffer(buffer: MixedBuffer, len: Int)

    protected fun checkLen(minRequired: Int, available: Int) {
        if (available < minRequired) {
            error("Insufficient buffer space: $minRequired > $available")
        }
    }

    protected fun putPadding(buffer: MixedBuffer, padLen: Int) {
        if (padLen > 0) {
            buffer.padding(padLen)
        }
    }

    override fun toString(): String {
        return name
    }
}

class Uniform1f(name: String) : Uniform<Float>(0f, name) {
    constructor(initValue: Float, name: String) : this(name) {
        value = initValue
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4, len)
        buffer.putFloat32(value)
        putPadding(buffer, len - 4)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(MutableVec2f(), name) {
    constructor(initValue: Vec2f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(8, len)
        value.putTo(buffer)
        putPadding(buffer, len - 8)
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(MutableVec3f(), name) {
    constructor(initValue: Vec3f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(12, len)
        value.putTo(buffer)
        putPadding(buffer, len - 12)
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(MutableVec4f(), name) {
    constructor(initValue: Vec4f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(16, len)
        value.putTo(buffer)
        putPadding(buffer, len - 16)
    }
}

class Uniform1fv(name: String, length: Int) : Uniform<FloatArray>(FloatArray(length), name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4 * size, len)
        val padLen = (len - 4 * size) / size
        for (i in 0 until size) {
            buffer.putFloat32(value[i])
            putPadding(buffer, padLen)
        }
    }
}

class Uniform2fv(name: String, length: Int) : Uniform<Array<MutableVec2f>>(Array(length) { MutableVec2f() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(8 * size, len)
        val padLen = (len - 8 * size) / size
        for (i in 0 until size) {
            value[i].putTo(buffer)
            putPadding(buffer, padLen)
        }
    }
}

class Uniform3fv(name: String, length: Int) : Uniform<Array<MutableVec3f>>(Array(length) { MutableVec3f() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(12 * size, len)
        val padLen = (len - 12 * size) / size
        for (i in 0 until size) {
            value[i].putTo(buffer)
            putPadding(buffer, padLen)
        }
    }
}

class Uniform4fv(name: String, length: Int) : Uniform<Array<MutableVec4f>>(Array(length) { MutableVec4f() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(16 * size, len)
        // Uniform4f arrays never contain padding
        for (i in 0 until size) {
            value[i].putTo(buffer)
        }
    }
}

class UniformMat3f(name: String) : Uniform<MutableMat3f>(MutableMat3f(), name) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(3 * 12, len)
        val padLen = (len - 3 * 12) / 3
        buffer.putFloat32(value.m00)
        buffer.putFloat32(value.m10)
        buffer.putFloat32(value.m20)
        putPadding(buffer, padLen)
        buffer.putFloat32(value.m01)
        buffer.putFloat32(value.m11)
        buffer.putFloat32(value.m21)
        putPadding(buffer, padLen)
        buffer.putFloat32(value.m02)
        buffer.putFloat32(value.m12)
        buffer.putFloat32(value.m22)
        putPadding(buffer, padLen)
    }
}

class UniformMat3fv(name: String, length: Int) : Uniform<Array<MutableMat3f>>(Array(length) { MutableMat3f() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(3 * 12 * size, len)
        val padLen = (len - 3 * 12 * size) / (3 * size)
        for (i in 0 until size) {
            buffer.putFloat32(value[i].m00)
            buffer.putFloat32(value[i].m10)
            buffer.putFloat32(value[i].m20)
            putPadding(buffer, padLen)
            buffer.putFloat32(value[i].m01)
            buffer.putFloat32(value[i].m11)
            buffer.putFloat32(value[i].m21)
            putPadding(buffer, padLen)
            buffer.putFloat32(value[i].m02)
            buffer.putFloat32(value[i].m12)
            buffer.putFloat32(value[i].m22)
            putPadding(buffer, padLen)
        }
    }
}

class UniformMat4f(name: String) : Uniform<MutableMat4f>(MutableMat4f(), name) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4 * 16, len)
        value.putTo(buffer)
    }
}

class UniformMat4fv(name: String, length: Int) : Uniform<Array<MutableMat4f>>(Array(length) { MutableMat4f() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4 * 16 * size, len)
        for (i in 0 until size) {
            value[i].putTo(buffer)
        }
    }
}

class Uniform1i(name: String) : Uniform<Int>(0, name) {
    constructor(initValue: Int, name: String) : this(name) {
        value = initValue
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4, len)
        buffer.putInt32(value)
        putPadding(buffer, len - 4)
    }
}

class Uniform2i(name: String) : Uniform<MutableVec2i>(MutableVec2i(), name) {
    constructor(initValue: Vec2i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(8, len)
        value.putTo(buffer)
        putPadding(buffer, len - 8)
    }
}

class Uniform3i(name: String) : Uniform<MutableVec3i>(MutableVec3i(), name) {
    constructor(initValue: Vec3i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(12, len)
        value.putTo(buffer)
        putPadding(buffer, len - 12)
    }
}

class Uniform4i(name: String) : Uniform<MutableVec4i>(MutableVec4i(), name) {
    constructor(initValue: Vec4i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(16, len)
        value.putTo(buffer)
        putPadding(buffer, len - 16)
    }
}

class Uniform1iv(name: String, length: Int) : Uniform<IntArray>(IntArray(length), name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(4 * size, len)
        val padLen = (len - 4 * size) / size
        for (i in 0 until size) {
            buffer.putInt32(value[i])
            putPadding(buffer, padLen)
        }
    }
}

class Uniform2iv(name: String, length: Int) : Uniform<Array<MutableVec2i>>(Array(length) { MutableVec2i() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(8 * size, len)
        val padLen = (len - 8 * size) / size
        for (i in 0 until size) {
            value[i].putTo(buffer)
            putPadding(buffer, padLen)
        }
    }
}

class Uniform3iv(name: String, length: Int) : Uniform<Array<MutableVec3i>>(Array(length) { MutableVec3i() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(12 * size, len)
        val padLen = (len - 12 * size) / size
        for (i in 0 until size) {
            value[i].putTo(buffer)
            putPadding(buffer, padLen)
        }
    }
}

class Uniform4iv(name: String, length: Int) : Uniform<Array<MutableVec4i>>(Array(length) { MutableVec4i() }, name, length) {
    override fun putToBuffer(buffer: MixedBuffer, len: Int) {
        checkLen(16 * size, len)
        for (i in 0 until size) {
            value[i].putTo(buffer)
        }
    }
}