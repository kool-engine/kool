package de.fabmax.kool.pipeline

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.util.*
import kotlinx.coroutines.CompletableDeferred
import kotlin.jvm.JvmInline

fun StorageBuffer(
    type: GpuType,
    size: Int,
    isResizable: Boolean = false,
    name: String = UniqueId.nextId("StorageBuffer")
) = GpuBuffer(type, BufferUsage.makeUsage(storage = true), size, isResizable, name)

class GpuBuffer(
    val type: GpuType,
    val usage: BufferUsage,
    size: Int,
    val isResizable: Boolean = false,
    val name: String = UniqueId.nextId("GpuBuffer")
): BaseReleasable() {

    /**
     * Current buffer size in number of elements of this buffer's type.
     */
    var size = size
        private set

    var gpuBuffer: GpuBufferImpl? = null
    var uploadData: Buffer? = null

    /**
     * Uploads the contents of the given float buffer into this buffer. The upload is not performed immediately but
     * is guaranteed to happen before this buffer is used by the GPU. Requires [type] to be a float type.
     */
    fun uploadData(data: Float32Buffer) {
        require(type.isFloat) {
            "Buffer type is $type (not a float type). Cannot upload from a Float32Buffer"
        }
        if (!isResizable) {
            checkUploadCapacity(data)
        }
        uploadData = data
    }

    /**
     * Uploads the contents of the given int buffer into this buffer. The upload is not performed immediately but
     * is guaranteed to happen before this buffer is used by the GPU. Requires [type] to be an int type.
     */
    fun uploadData(data: Int32Buffer) {
        require(type.isInt) {
            "Buffer type is $type (not an int type). Cannot upload from an Int32Buffer"
        }
        if (!isResizable) {
            checkUploadCapacity(data)
        }
        uploadData = data
    }

    /**
     * Uploads the contents of the given struct buffer into this buffer. The upload is not performed immediately but
     * is guaranteed to happen before this buffer is used by the GPU. Requires [type] to be the same struct as the
     * given buffer.
     */
    fun uploadData(data: StructBuffer<*>) {
        require(type is GpuType.Struct && type.struct == data.struct) {
            "Buffer type is $type but provided data buffer type is ${data.struct.type}"
        }
        if (!isResizable) {
            require(data.capacity <= size) {
                "Provided upload source buffer is too large (contains ${data.capacity} elements but this buffer's capacity is only ${size})"
            }
        }
        uploadData = data.buffer
    }

    /**
     * Downloads the contents of this buffer into the given float buffer. Requires [type] to be a float type.
     *
     * Notice that, depending on the platform, the buffer is read asynchronously and is neither guaranteed nor likely
     * to complete in the same frame. Moreover, buffer reading is an expensive operation and should be avoided on a
     * per-frame basis.
     */
    suspend fun downloadData(resultData: Float32Buffer) {
        require(type.isFloat) {
            "Buffer type is $type (not a float type). Cannot download into a Float32Buffer"
        }
        checkDownloadCapacity(resultData)
        val deferred = CompletableDeferred<Unit>()
        KoolSystem.requireContext().backend.downloadBuffer(this, deferred, resultData)
        deferred.await()
    }

    /**
     * Downloads the contents of this buffer into the given int buffer. Requires [type] to be an int type.
     *
     * Notice that, depending on the platform, the buffer is read asynchronously and is neither guaranteed nor likely
     * to complete in the same frame. Moreover, buffer reading is an expensive operation and should be avoided on a
     * per-frame basis.
     */
    suspend fun downloadData(resultData: Int32Buffer) {
        require(type.isInt) {
            "Buffer type is $type (not an int type). Cannot download into an Int32Buffer"
        }
        checkDownloadCapacity(resultData)
        val deferred = CompletableDeferred<Unit>()
        KoolSystem.requireContext().backend.downloadBuffer(this, deferred, resultData)
        deferred.await()
    }

    /**
     * Downloads the contents of this buffer into the given struct buffer. Requires [type] to be the same
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
        require(resultData.capacity >= size) {
            "Provided download buffer is too small (capacity: ${resultData.capacity} elements but this buffer's capacity is $size)"
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

    private fun checkUploadCapacity(src: Buffer) {
        val srcSize = src.limit / type.primsPerElement
        require(srcSize <= size) {
            "Provided upload buffer is too large (contains $srcSize elements but this buffer's capacity is only $size)"
        }
    }

    private fun checkDownloadCapacity(dst: Buffer) {
        val dstSize = dst.capacity / type.primsPerElement
        require(dstSize >= size) {
            "Provided download buffer is too small (capacity: $dstSize elements but this buffer's capacity is $size)"
        }
    }

    private val GpuType.primsPerElement: Int get() = when (this) {
        GpuType.Float1 -> 1
        GpuType.Float2 -> 2
        GpuType.Float3 -> 4
        GpuType.Float4 -> 4

        GpuType.Int1 -> 1
        GpuType.Int2 -> 2
        GpuType.Int3 -> 4
        GpuType.Int4 -> 4

        GpuType.Uint1 -> 1
        GpuType.Uint2 -> 2
        GpuType.Uint3 -> 4
        GpuType.Uint4 -> 4

        GpuType.Bool1 -> 1
        GpuType.Bool2 -> 2
        GpuType.Bool3 -> 4
        GpuType.Bool4 -> 4

        GpuType.Mat2 -> 8
        GpuType.Mat3 -> 12
        GpuType.Mat4 -> 16
        is GpuType.Struct -> error("unreachable")
    }

    companion object {
        /**
         * Creates a Float32Buffer for the given [type] (which is required to be a float type). The returned
         * buffer will have the required size to store [size] elements of the given type in it.
         */
        fun createFloatBufferForType(type: GpuType, size: Int): Float32Buffer {
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

        /**
         * Creates an Int32Buffer for the given [type] (which is required to be an int type). The returned
         * buffer will have the required size to store [size] elements of the given type in it.
         */
        fun createIntBufferForType(type: GpuType, size: Int): Int32Buffer {
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

/**
 * Buffer usage flags: Indicate what a buffer can be used for. Usually a buffer has only a single usage, however,
 * if multiple flags are set, a buffer can be used in multiple ways. E.g. a buffer can be bound as a storage buffer in
 * a compute shader and as a vertex buffer for a mesh. This way the compute shader can compute geometry which is then
 * rendered as a mesh.
 */
@JvmInline
value class BufferUsage(val usage: Int) {
    val isStorage: Boolean get() = usage and USAGE_STORAGE_BUFFER != 0
    val isVertex: Boolean get() = usage and USAGE_VERTEX_BUFFER != 0
    val isInstance: Boolean get() = usage and USAGE_INSTANCE_BUFFER != 0
    val isIndex: Boolean get() = usage and USAGE_INDEX_BUFFER != 0

    companion object {
        const val USAGE_STORAGE_BUFFER = 1
        const val USAGE_VERTEX_BUFFER = 2
        const val USAGE_INSTANCE_BUFFER = 4
        const val USAGE_INDEX_BUFFER = 8

        /**
         * Creates a BufferUsage with the given flags / usages set to true.
         */
        fun makeUsage(
            storage: Boolean = false,
            vertex: Boolean = false,
            instance: Boolean = false,
            index: Boolean = false
        ): BufferUsage {
            var usage = 0
            if (storage) usage = USAGE_STORAGE_BUFFER
            if (vertex) usage = usage or USAGE_VERTEX_BUFFER
            if (instance) usage = usage or USAGE_INSTANCE_BUFFER
            if (index) usage = usage or USAGE_INDEX_BUFFER
            return BufferUsage(usage)
        }
    }
}

fun GpuBuffer.checkIsStorageBuffer() {
    check(usage.isStorage) {
        "Buffer must have usage.isStorage = true in order to be used as a storage buffer"
    }
}
