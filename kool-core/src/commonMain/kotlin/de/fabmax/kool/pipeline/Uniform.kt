package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.MixedBuffer

abstract class Uniform<T>(var value: T, val name: String) {
    /**
     * Size of value type in bytes
     */
    abstract val size: Int

    abstract fun putTo(buffer: MixedBuffer)
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

class Uniform1fv(name: String, val n: Int) : Uniform<FloatArray>(FloatArray(n), name) {
    override val size: Int = 4 * n

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until n) {
            buffer.putFloat32(value[i])
        }
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

class Uniform2fv(name: String, val n: Int) : Uniform<Array<MutableVec2f>>(Array(n) { MutableVec2f() }, name) {
    override val size: Int = 2 * 4 * n

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until n) {
            buffer.putFloat32(value[i].array)
        }
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(MutableVec3f(), name) {
    override val size: Int = 3 * 4

    constructor(initValue: Vec3f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putFloat32(value.array)
    }
}

class Uniform3fv(name: String, val n: Int) : Uniform<Array<MutableVec3f>>(Array(n) { MutableVec3f() }, name) {
    override val size: Int = 3 * 4 * n

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until n) {
            buffer.putFloat32(value[i].array)
        }
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

class Uniform4fv(name: String, val n: Int) : Uniform<Array<MutableVec4f>>(Array(n) { MutableVec4f() }, name) {
    override val size: Int = 4 * 4 * n

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until n) {
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

class Uniform1i(name: String) : Uniform<Int>(0, name) {
    override val size: Int = 4

    constructor(initValue: Int, name: String) : this(name) {
        value = initValue
    }

    override fun putTo(buffer: MixedBuffer) {
        buffer.putUint32(value)
    }
}

class Uniform1iv(name: String, val n: Int) : Uniform<IntArray>(IntArray(n), name) {
    override val size: Int = 4 * n

    override fun putTo(buffer: MixedBuffer) {
        for (i in 0 until n) {
            buffer.putUint32(value[i])
        }
    }
}