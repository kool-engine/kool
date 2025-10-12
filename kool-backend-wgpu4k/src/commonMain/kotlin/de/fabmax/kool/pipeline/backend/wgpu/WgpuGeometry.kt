package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import io.ygdrasil.webgpu.GPUBuffer
import io.ygdrasil.webgpu.GPUBufferUsage
import io.ygdrasil.webgpu.GPUDevice

class WgpuGeometry(val mesh: Mesh, val vertexData: IndexedVertexList<*>, val backend: RenderBackendWgpu4k) : BaseReleasable(), GpuGeometry {
    private val device: GPUDevice get() = backend.device

    private val createdIndexBuffer: WgpuGrowingBuffer
    private val createdVertexBuffer: WgpuGrowingBuffer?

    val indexBuffer: GPUBuffer get() = createdIndexBuffer.buffer.buffer
    val vertexBuffer: GPUBuffer? get() = createdVertexBuffer?.buffer?.buffer

    private var updateModCount = -1

    init {
        createdIndexBuffer = WgpuGrowingBuffer(backend, "${mesh.name} index data", 4L * vertexData.numIndices, setOf(GPUBufferUsage.Index, GPUBufferUsage.CopyDst))
        createdVertexBuffer = if (vertexData.layout.structSize == 0) null else {
            WgpuGrowingBuffer(backend, "${mesh.name} vertex data", vertexData.layout.structSize * vertexData.numVertices.toLong())
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()
        if (vertexData.modCount.isDirty(updateModCount)) {
            updateModCount = vertexData.modCount.count
            createdIndexBuffer.writeData(vertexData.indices)
            createdVertexBuffer?.writeData(vertexData.vertexData.buffer)
        }
    }

    override fun doRelease() {
        createdIndexBuffer.release()
        createdVertexBuffer?.release()
    }
}