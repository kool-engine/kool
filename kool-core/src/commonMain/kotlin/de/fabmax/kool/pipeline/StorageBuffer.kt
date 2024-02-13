package de.fabmax.kool.pipeline

import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.math.Vec4i
import de.fabmax.kool.pipeline.backend.GpuBuffer
import de.fabmax.kool.util.*

abstract class StorageBuffer(
    val format: GpuType,
    val name: String,
    size: Int,
): BaseReleasable() {

    internal var gpuBuffer: GpuBuffer? = null

    internal val buffer = makeBuffer(size, format)
    internal var isDirty: Boolean = true

    private val bufferF32: Float32Buffer
        get() {
            check(buffer is Float32Buffer) { "Buffer has an int-format ($format), cannot used with float values" }
            return buffer
        }

    private val bufferI32: Int32Buffer
        get() {
            check(buffer is Int32Buffer) { "Buffer has an float-format ($format), cannot used with int values" }
            return buffer
        }

    protected fun setF1(index: Int, value: Float) {
        bufferF32[index] = value
        isDirty = true
    }

    protected fun setF2(index: Int, value: Vec2f) {
        var i = index * 2
        bufferF32[i++] = value.x
        bufferF32[i]   = value.y
        isDirty = true
    }

    protected fun setF4(index: Int, value: Vec4f) {
        var i = index * 4
        bufferF32[i++] = value.x
        bufferF32[i++] = value.y
        bufferF32[i++] = value.z
        bufferF32[i]   = value.w
        isDirty = true
    }

    protected fun setI1(index: Int, value: Int) {
        bufferI32[index] = value
        isDirty = true
    }

    protected fun setI2(index: Int, value: Vec2i) {
        var i = index * 2
        bufferI32[i++] = value.x
        bufferI32[i]   = value.y
        isDirty = true
    }

    protected fun setI4(index: Int, value: Vec4i) {
        var i = index * 4
        bufferI32[i++] = value.x
        bufferI32[i++] = value.y
        bufferI32[i++] = value.z
        bufferI32[i]   = value.w
        isDirty = true
    }

    fun writeFloats(block: (Float32Buffer) -> Unit) {
        block(bufferF32)
        isDirty = true
    }

    fun writeInts(block: (Int32Buffer) -> Unit) {
        block(bufferI32)
        isDirty = true
    }

    suspend fun readbackFloats(block: (Float32Buffer) -> Unit) {
        TODO()
    }

    suspend fun readbackInts(block: (Int32Buffer) -> Unit) {
        TODO()
    }

    override fun release() {
        super.release()
        gpuBuffer?.release()
        gpuBuffer = null
    }

    companion object {
        fun makeBuffer(size: Int, type: GpuType): Buffer = when (type) {
            GpuType.FLOAT1 -> Float32Buffer(size * 1)
            GpuType.FLOAT2 -> Float32Buffer(size * 2)
            GpuType.FLOAT4 -> Float32Buffer(size * 4)
            GpuType.INT1 -> Int32Buffer(size * 1)
            GpuType.INT2 -> Int32Buffer(size * 2)
            GpuType.INT4 -> Int32Buffer(size * 4)
            else -> error("Invalid buffer type: $type (only 1, 2, and 4 dimensional float and int types are allowed)")
        }
    }
}

class StorageBuffer1d(
    val sizeX: Int,
    type: GpuType,
    name: String = UniqueId.nextId("StorageBuffer1d")
) : StorageBuffer(type, name, sizeX) {

    operator fun set(index: Int, value: Float) = setF1(index, value)
    operator fun set(index: Int, value: Vec2f) = setF2(index, value)
    operator fun set(index: Int, value: Vec4f) = setF4(index, value)

    operator fun set(index: Int, value: Int) = setI1(index, value)
    operator fun set(index: Int, value: Vec2i) = setI2(index, value)
    operator fun set(index: Int, value: Vec4i) = setI4(index, value)
}

class StorageBuffer2d(
    val sizeX: Int,
    val sizeY: Int,
    type: GpuType,
    name: String = UniqueId.nextId("StorageBuffer2d")
) : StorageBuffer(type, name, sizeX * sizeY) {

    operator fun set(x: Int, y: Int, value: Float) = setF1(y * sizeX + x, value)
    operator fun set(x: Int, y: Int, value: Vec2f) = setF2(y * sizeX + x, value)
    operator fun set(x: Int, y: Int, value: Vec4f) = setF4(y * sizeX + x, value)

    operator fun set(x: Int, y: Int, value: Int) = setI1(y * sizeX + x, value)
    operator fun set(x: Int, y: Int, value: Vec2i) = setI2(y * sizeX + x, value)
    operator fun set(x: Int, y: Int, value: Vec4i) = setI4(y * sizeX + x, value)
}

class StorageBuffer3d(
    val sizeX: Int,
    val sizeY: Int,
    val sizeZ: Int,
    type: GpuType,
    name: String = UniqueId.nextId("StorageBuffer3d")
) : StorageBuffer(type, name, sizeX * sizeY * sizeZ) {

    operator fun set(x: Int, y: Int, z: Int, value: Float) = setF1(z * sizeX * sizeY + y * sizeX + x, value)
    operator fun set(x: Int, y: Int, z: Int, value: Vec2f) = setF2(z * sizeX * sizeY + y * sizeX + x, value)
    operator fun set(x: Int, y: Int, z: Int, value: Vec4f) = setF4(z * sizeX * sizeY + y * sizeX + x, value)

    operator fun set(x: Int, y: Int, z: Int, value: Int) = setI1(z * sizeX * sizeY + y * sizeX + x, value)
    operator fun set(x: Int, y: Int, z: Int, value: Vec2i) = setI2(z * sizeX * sizeY + y * sizeX + x, value)
    operator fun set(x: Int, y: Int, z: Int, value: Vec4i) = setI4(z * sizeX * sizeY + y * sizeX + x, value)
}
