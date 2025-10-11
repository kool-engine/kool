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
    private val createdFloatBuffer: WgpuGrowingBuffer?
    private val createdIntBuffer: WgpuGrowingBuffer?

    val indexBuffer: GPUBuffer get() = createdIndexBuffer.buffer.buffer
    val floatBuffer: GPUBuffer? get() = createdFloatBuffer?.buffer?.buffer
    val intBuffer: GPUBuffer? get() = createdIntBuffer?.buffer?.buffer

    private var updateModCount = -1

    init {
        createdIndexBuffer = WgpuGrowingBuffer(backend, "${mesh.name} index data", 4L * vertexData.numIndices, setOf(GPUBufferUsage.Index, GPUBufferUsage.CopyDst))
        createdFloatBuffer = if (vertexData.byteStrideF == 0) null else {
            WgpuGrowingBuffer(backend, "${mesh.name} vertex float data", vertexData.byteStrideF * vertexData.numVertices.toLong())
        }
        createdIntBuffer = if (vertexData.byteStrideI == 0) null else {
            WgpuGrowingBuffer(backend, "${mesh.name} vertex int data", vertexData.byteStrideI * vertexData.numVertices.toLong())
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()
        if (vertexData.modCount.isDirty(updateModCount)) {
            updateModCount = vertexData.modCount.count
            createdIndexBuffer.writeData(vertexData.indices)
            createdFloatBuffer?.writeData(vertexData.dataF)
            createdIntBuffer?.writeData(vertexData.dataI)
        }
    }

    override fun doRelease() {
        createdIndexBuffer.release()
        createdFloatBuffer?.release()
        createdIntBuffer?.release()
    }
}