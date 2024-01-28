package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.*

class WgpuGeometry(val mesh: Mesh, val backend: RenderBackendWebGpu) : BaseReleasable(), GpuGeometry {
    private val device: GPUDevice get() = backend.device

    private val createdIndexBuffer: CreatedBuffer
    private val createdFloatBuffer: CreatedBuffer
    private val createdIntBuffer: CreatedBuffer?
    private val createdInstanceBuffer: CreatedBuffer?

    val indexBuffer: GPUBuffer get() = createdIndexBuffer.buffer.buffer
    val floatBuffer: GPUBuffer get() = createdFloatBuffer.buffer.buffer
    val intBuffer: GPUBuffer? get() = createdIntBuffer?.buffer?.buffer
    val instanceBuffer: GPUBuffer? get() = createdInstanceBuffer?.buffer?.buffer

    private var isNewlyCreated =  true

    init {
        val geom = mesh.geometry
        createdIndexBuffer = CreatedBuffer("${mesh.name} index data", 4 * geom.numIndices, GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST)
        createdFloatBuffer = CreatedBuffer("${mesh.name} vertex float data", geom.byteStrideF * geom.numVertices)
        createdIntBuffer = if (geom.byteStrideI == 0) null else {
            CreatedBuffer("${mesh.name} vertex int data", geom.byteStrideI * geom.numVertices)
        }
        createdInstanceBuffer = mesh.instances?.let {
            CreatedBuffer("${mesh.name} instance data", it.strideBytesF * it.maxInstances)
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()

        val instances = mesh.instances
        val geometry = mesh.geometry

        if (instances != null && createdInstanceBuffer != null && (instances.hasChanged || isNewlyCreated)) {
            createdInstanceBuffer.writeData(instances.dataF)
            instances.hasChanged = false
        }

        if (!geometry.isBatchUpdate && (geometry.hasChanged || isNewlyCreated)) {
            createdIndexBuffer.writeData(geometry.indices)
            createdFloatBuffer.writeData(geometry.dataF)
            createdIntBuffer?.writeData(geometry.dataI)
            geometry.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        super.release()
        createdIndexBuffer.buffer.release()
        createdFloatBuffer.buffer.release()
        createdIntBuffer?.buffer?.release()
        createdInstanceBuffer?.buffer?.release()
    }

    private inner class CreatedBuffer(val label: String, var size: Int, val usage: Int = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST) {
        var buffer: WgpuBufferResource = makeBuffer()

        fun writeData(data: Float32Buffer) {
            checkSize(data.limit * 4)
            device.queue.writeBuffer(
                buffer = buffer.buffer,
                bufferOffset = 0L,
                data = (data as Float32BufferImpl).buffer,
                dataOffset = 0L,
                size = data.limit.toLong()
            )
        }

        fun writeData(data: Int32Buffer) {
            checkSize(data.limit * 4)
            device.queue.writeBuffer(
                buffer = buffer.buffer,
                bufferOffset = 0L,
                data = (data as Int32BufferImpl).buffer,
                dataOffset = 0L,
                size = data.limit.toLong()
            )
        }

        private fun checkSize(required: Int) {
            if (required > size) {
                buffer.release()
                size = required
                buffer = makeBuffer()
            }
        }

        private fun makeBuffer() = backend.createBuffer(
            GPUBufferDescriptor(
                label = label,
                size = size.toLong(),
                usage = usage
            ),
            "mesh: ${mesh.name}"
        )
    }
}