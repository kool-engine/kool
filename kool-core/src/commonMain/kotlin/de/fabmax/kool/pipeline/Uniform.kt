package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Float32Buffer

abstract class Uniform<T>(var value: T, val name: String) {
    /**
     * Size of value type in bytes
     */
    abstract val size: Int

    abstract fun putTo(buffer: Float32Buffer)
}

class Uniform1f(name: String) : Uniform<Float>(0f, name) {
    override val size: Int = 4

    constructor(initValue: Float, name: String) : this(name) {
        value = initValue
    }

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value)
    }
}

class Uniform2f(name: String) : Uniform<MutableVec2f>(MutableVec2f(), name) {
    override val size: Int = 2 * 4

    constructor(initValue: Vec2f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value.array)
    }
}

class Uniform3f(name: String) : Uniform<MutableVec3f>(MutableVec3f(), name) {
    override val size: Int = 3 * 4

    constructor(initValue: Vec3f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value.array)
    }
}

class Uniform4f(name: String) : Uniform<MutableVec4f>(MutableVec4f(), name) {
    override val size: Int = 4 * 4

    constructor(initValue: Vec4f, name: String) : this(name) {
        value.set(initValue)
    }

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value.array)
    }
}

class UniformMat3f(name: String) : Uniform<Mat3f>(Mat3f(), name) {
    override val size: Int = 9 * 4

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value.matrix)
    }
}

class UniformMat4f(name: String) : Uniform<Mat4f>(Mat4f(), name) {
    override val size: Int = 16 * 4

    override fun putTo(buffer: Float32Buffer) {
        buffer.put(value.matrix)
    }
}