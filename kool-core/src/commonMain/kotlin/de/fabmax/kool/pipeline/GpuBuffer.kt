package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred

class GpuBuffer(
    val type: GpuType,
    size: Int,
    val name: String = UniqueId.nextId("GpuBuffer")
): BaseReleasable() {

    var size = size
        private set

    internal var gpuBuffer: GpuBufferImpl? = null
    internal var uploadData: Buffer? = null

    fun uploadData(data: Float32Buffer) {
        require(type.isFloat) {
            "Buffer type is $type (not a float type). Cannot upload from a Float32Buffer"
        }
        uploadData = data
    }

    fun uploadData(data: Int32Buffer) {
        require(type.isInt) {
            "Buffer type is $type (not an int type). Cannot upload from an Int32Buffer"
        }
        uploadData = data
    }

    fun uploadData(data: StructBuffer<*>) {
        require(type is GpuType.Struct && type.struct == data.struct) {
            "Buffer type is $type but provided data buffer type is ${data.struct.type}"
        }
        uploadData = data.buffer
    }

    /**
     * Downloads the contents of this storage buffer into the given float buffer. Requires [type] to be a float type.
     *
     * Notice that, depending on the platform, the buffer is read asynchronously and is neither guaranteed nor likely
     * to complete in the same frame. Moreover, buffer reading is an expensive operation and should be avoided on a
     * per-frame basis.
     */
    suspend fun downloadData(resultData: Float32Buffer) {
        require(type.isFloat) {
            "Buffer type is $type (not a float type). Cannot download into a Float32Buffer"
        }
        val deferred = CompletableDeferred<Unit>()
        KoolSystem.requireContext().backend.downloadBuffer(this, deferred, resultData)
        deferred.await()
    }

    /**
     * Downloads the contents of this storage buffer into the given int buffer. Requires [type] to be an int type.
     *
     * Notice that, depending on the platform, the buffer is read asynchronously and is neither guaranteed nor likely
     * to complete in the same frame. Moreover, buffer reading is an expensive operation and should be avoided on a
     * per-frame basis.
     */
    suspend fun downloadData(resultData: Int32Buffer) {
        require(type.isInt) {
            "Buffer type is $type (not an int type). Cannot download into an Int32Buffer"
        }
        val deferred = CompletableDeferred<Unit>()
        KoolSystem.requireContext().backend.downloadBuffer(this, deferred, resultData)
        deferred.await()
    }

    /**
     * Downloads the contents of this storage buffer into the given struct buffer. Requires [type] to be the same
     * struct as the given buffer.
     *
     * Notice that, depending on the platform, the buffer is read asynchronously and is neither guaranteed nor likely
     * to complete in the same frame. Moreover, buffer reading is an expensive operation and should be avoided on a
     * per-frame basis.
     */
    suspend fun downloadData(resultData: StructBuffer<*>) {
        require(type is GpuType.Struct && type.struct == resultData.struct) {
            "Buffer type is $type but provided result buffer type is ${resultData.struct.type}"
        }
        val deferred = CompletableDeferred<Unit>()
        KoolSystem.requireContext().backend.downloadBuffer(this, deferred, resultData.buffer)
        deferred.await()
    }

    override fun release() {
        super.release()
        gpuBuffer?.release()
        gpuBuffer = null
    }

    companion object {
        fun createFloatBuffer(type: GpuType, size: Int): Float32Buffer {
            return when (type) {
                GpuType.Float1 -> Float32Buffer(size * 1)
                GpuType.Float2 -> Float32Buffer(size * 2)
                GpuType.Float3 -> Float32Buffer(size * 4)
                GpuType.Float4 -> Float32Buffer(size * 4)
                GpuType.Mat2 -> Float32Buffer(size * 4 * 2)
                GpuType.Mat3 -> Float32Buffer(size * 4 * 3)
                GpuType.Mat4 -> Float32Buffer(size * 4 * 4)
                else -> error("createFloatBuffer requires the type to be a float type but is $type")
            }
        }

        fun createIntBuffer(type: GpuType, size: Int): Int32Buffer {
            return when (type) {
                GpuType.Int1 -> Int32Buffer(size * 1)
                GpuType.Int2 -> Int32Buffer(size * 2)
                GpuType.Int3 -> Int32Buffer(size * 4)
                GpuType.Int4 -> Int32Buffer(size * 4)
                else -> error("createIntBuffer requires the type to be a int type but is $type")
            }
        }
    }
}

interface GpuBufferImpl : Releasable
