package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.Float32BufferImpl
import de.fabmax.kool.util.Int32BufferImpl

class WgpuGeometry(mesh: Mesh, val backend: RenderBackendWebGpu) : GpuGeometry {
    private val device: GPUDevice get() = backend.device

    val indexBuffer: GPUBuffer
    val floatBuffer: GPUBuffer
    val intBuffer: GPUBuffer?

    override var isReleased: Boolean = false

    init {
        val geom = mesh.geometry
        indexBuffer = device.createBuffer(
            GPUBufferDescriptor(
                label = "${mesh.name} index data",
                size = 4 * geom.numIndices.toLong(),
                usage = GPUBufferUsage.INDEX or GPUBufferUsage.COPY_DST
            )
        )

        floatBuffer = device.createBuffer(
            GPUBufferDescriptor(
                label = "${mesh.name} vertex float data",
                size = geom.byteStrideF.toLong() * geom.numVertices,
                usage = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
            )
        )

        intBuffer = if (geom.byteStrideI > 0) {
            device.createBuffer(
                GPUBufferDescriptor(
                    label = "${mesh.name} vertex int data",
                    size = geom.byteStrideI.toLong() * geom.numVertices,
                    usage = GPUBufferUsage.VERTEX or GPUBufferUsage.COPY_DST
                )
            )
        } else null

        device.queue.writeBuffer(
            buffer = indexBuffer,
            bufferOffset = 0L,
            data = (geom.indices as Int32BufferImpl).buffer,
            dataOffset = 0L,
            size = geom.numIndices.toLong()
        )

        device.queue.writeBuffer(
            buffer = floatBuffer,
            bufferOffset = 0L,
            data = (geom.dataF as Float32BufferImpl).buffer,
            dataOffset = 0L,
            size = geom.vertexSizeF * geom.numVertices.toLong()
        )

        intBuffer?.let {
            device.queue.writeBuffer(
                buffer = it,
                bufferOffset = 0L,
                data = (geom.dataI as Int32BufferImpl).buffer,
                dataOffset = 0L,
                size = geom.vertexSizeI * geom.numVertices.toLong()
            )
        }
    }

    override fun release() {
        indexBuffer.destroy()
        floatBuffer.destroy()
        intBuffer?.destroy()
        isReleased = true
    }

}