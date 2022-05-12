package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MutableColor

sealed class Uniform<T>(
    var value: T,
    val name: String,

    /**
     * Size of value in bytes including padding according to Std140 layout rules.
     */
    val size: Int,

    /**
     * Number of elements in case this is an array type (1 otherwise)
     */
    val length: Int = 1
) {

    val isArray: Boolean
        get() = length > 1

    /**
     * Appends this uniform's data to the supplied buffer at its current position. Does not check for alignment, i.e.
     * the buffer position needs to be correctly set before calling this method. Also does not append any trailing
     * padding. However, for appropriate types, intermediate padding is inserted according to Std140 layout rules (e.g.
     * for most array types).
     */
    abstract fun putToBuffer(buffer: MixedBuffer)

    override fun toString(): String {
        return name
    }
}

class Uniform1f(name: String) : Uniform<Float>(0f, name, 4) {
    constructor(initValue: Float, name: String) : this(name) {
        value = initValue
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(MutableVec2f(), name, 8) {
    constructor(initValue: Vec2f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(MutableVec3f(), name, 12) {
    constructor(initValue: Vec3f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(MutableVec4f(), name, 16) {
    constructor(initValue: Vec4f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class UniformColor(name: String) : Uniform<MutableColor>(MutableColor(), name, 16) {
    constructor(initValue: Color, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform1fv(name: String, length: Int) : Uniform<FloatArray>(FloatArray(length), name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i])
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
                buffer.putUint32(0)
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform2fv(name: String, length: Int) : Uniform<Array<MutableVec2f>>(Array(length) { MutableVec2f() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform3fv(name: String, length: Int) : Uniform<Array<MutableVec3f>>(Array(length) { MutableVec3f() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform4fv(name: String, length: Int) : Uniform<Array<MutableVec4f>>(Array(length) { MutableVec4f() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
        }
    }
}

class UniformMat3f(name: String) : Uniform<Mat3f>(Mat3f(), name, 3 * 16) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (m in 0..2) {
            buffer.putFloat32(value.matrix, m * 3, 3)
            // add intermediate padding (std140 layout)
            if (m < 2) {
                buffer.putFloat32(0f)
            }
        }
    }
}

class UniformMat3fv(name: String, length: Int) : Uniform<Array<Mat3f>>(Array(length) { Mat3f() }, name, 3 * 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            for (m in 0..2) {
                buffer.putFloat32(value[i].matrix, m * 3, 3)
                // add intermediate padding (std140 layout)
                if (m < 2) {
                    buffer.putFloat32(0f)
                }
            }
        }
    }
}

class UniformMat4f(name: String) : Uniform<Mat4f>(Mat4f(), name, 4 * 16) {
    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putFloat32(value.matrix)
    }
}

class UniformMat4fv(name: String, length: Int) : Uniform<Array<Mat4f>>(Array(length) { Mat4f() }, name, 4 * 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].matrix)
        }
    }
}

class Uniform1i(name: String) : Uniform<Int>(0, name, 4) {
    constructor(initValue: Int, name: String) : this(name) {
        value = initValue
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putInt32(value)
    }
}

class Uniform2i(name: String) : Uniform<MutableVec2i>(MutableVec2i(), name, 8) {
    constructor(initValue: Vec2i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform3i(name: String) : Uniform<MutableVec3i>(MutableVec3i(), name, 16) {
    constructor(initValue: Vec3i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform4i(name: String) : Uniform<MutableVec4i>(MutableVec4i(), name, 16) {
    constructor(initValue: Vec4i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putToBuffer(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform1iv(name: String, length: Int) : Uniform<IntArray>(IntArray(length), name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i])
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
                buffer.putUint32(0)
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform2iv(name: String, length: Int) : Uniform<Array<MutableVec2i>>(Array(length) { MutableVec2i() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform3iv(name: String, length: Int) : Uniform<Array<MutableVec3i>>(Array(length) { MutableVec3i() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
            // add intermediate padding (std140 layout)
            if (i < length - 1) {
                buffer.putUint32(0)
            }
        }
    }
}

class Uniform4iv(name: String, length: Int) : Uniform<Array<MutableVec4i>>(Array(length) { MutableVec4i() }, name, 16 * length, length) {
    override fun putToBuffer(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
        }
    }
}