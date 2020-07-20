package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MutableColor

abstract class Uniform<T>(var value: T, val name: String) {
    /**
     * Size of value in bytes including padding.
     */
    abstract val size: Int

    /**
     * Size of value type in bytes (excluding any padding)
     */
    open val typeSize
        get() = size

    /**
     * Number of array elements (1 for non array types)
     */
    open val length = 1

    abstract fun putTo(buffer: MixedBuffer)

    override fun toString(): String {
        return name
    }
}

class Uniform1f(name: String) : Uniform<Float>(0f, name) {
    override val size: Int = 4

    constructor(initValue: Float, name: String) : this(name) {
        value = initValue
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(MutableVec2f(), name) {
    override val size: Int = 2 * 4

    constructor(initValue: Vec2f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(MutableVec3f(), name) {
    override val size: Int = 4 * 4

    constructor(initValue: Vec3f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
        // padding (std140 layout)
        buffer.putUint32(0)
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(MutableVec4f(), name) {
    override val size: Int = 4 * 4

    constructor(initValue: Vec4f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class UniformColor(name: String) : Uniform<MutableColor>(MutableColor(), name) {
    override val size: Int = 4 * 4

    constructor(initValue: Color, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform1fv(name: String, override val length: Int) : Uniform<FloatArray>(FloatArray(length), name) {
    override val size = 4 * 4 * length
    override val typeSize = 4

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i])
            // padding (std140 layout)
            buffer.putUint32(0)
            buffer.putUint32(0)
            buffer.putUint32(0)
        }
    }
}

class Uniform2fv(name: String, override val length: Int) : Uniform<Array<MutableVec2f>>(Array(length) { MutableVec2f() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
            // padding (std140 layout)
            buffer.putUint32(0)
            buffer.putUint32(0)
        }
    }
}

class Uniform3fv(name: String, override val length: Int) : Uniform<Array<MutableVec3f>>(Array(length) { MutableVec3f() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
            // padding (std140 layout)
            buffer.putUint32(0)
        }
    }
}

class Uniform4fv(name: String, override val length: Int) : Uniform<Array<MutableVec4f>>(Array(length) { MutableVec4f() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].array)
        }
    }
}

class UniformMat3f(name: String) : Uniform<Mat3f>(Mat3f(), name) {
    override val size: Int = 9 * 4

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.matrix)
    }
}

class UniformMat4f(name: String) : Uniform<Mat4f>(Mat4f(), name) {
    override val size: Int = 16 * 4

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.matrix)
    }
}

class UniformMat4fv(name: String, override val length: Int) : Uniform<Array<Mat4f>>(Array(length) { Mat4f() }, name) {
    override val size: Int = 16 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putFloat32(value[i].matrix)
        }
    }
}

class Uniform1i(name: String) : Uniform<Int>(0, name) {
    override val size: Int = 4

    constructor(initValue: Int, name: String) : this(name) {
        value = initValue
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putInt32(value)
    }
}

class Uniform2i(name: String) : Uniform<MutableVec2i>(MutableVec2i(), name) {
    override val size: Int = 2 * 4

    constructor(initValue: Vec2i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform3i(name: String) : Uniform<MutableVec3i>(MutableVec3i(), name) {
    override val size: Int = 3 * 4

    constructor(initValue: Vec3i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform4i(name: String) : Uniform<MutableVec4i>(MutableVec4i(), name) {
    override val size: Int = 4 * 4

    constructor(initValue: Vec4i, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putInt32(value.array)
    }
}

class Uniform1iv(name: String, override val length: Int) : Uniform<IntArray>(IntArray(length), name) {
    override val size = 4 * 4 * length
    override val typeSize = 4

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i])
            // padding (std140 layout)
            buffer.putInt32(0)
            buffer.putInt32(0)
            buffer.putInt32(0)
        }
    }
}

class Uniform2iv(name: String, override val length: Int) : Uniform<Array<MutableVec2i>>(Array(length) { MutableVec2i() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
            // padding (std140 layout)
            buffer.putInt32(0)
            buffer.putInt32(0)
        }
    }
}

class Uniform3iv(name: String, override val length: Int) : Uniform<Array<MutableVec3i>>(Array(length) { MutableVec3i() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
            // padding (std140 layout)
            buffer.putInt32(0)
        }
    }
}

class Uniform4iv(name: String, override val length: Int) : Uniform<Array<MutableVec4i>>(Array(length) { MutableVec4i() }, name) {
    override val size: Int = 4 * 4 * length

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until length) {
            buffer.putInt32(value[i].array)
        }
    }
}