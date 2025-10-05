package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class GeometryVk(val mesh: Mesh, val vertexData: IndexedVertexList, val backend: RenderBackendVk) : BaseReleasable(), GpuGeometry {
    val device: Device get() = backend.device

    private val createdIndexBuffer: GrowingBufferVk
    private val createdFloatBuffer: GrowingBufferVk?
    private val createdIntBuffer: GrowingBufferVk?

    val indexBuffer: VkBuffer get() = createdIndexBuffer.buffer.vkBuffer
    val floatBuffer: VkBuffer? get() = createdFloatBuffer?.buffer?.vkBuffer
    val intBuffer: VkBuffer? get() = createdIntBuffer?.buffer?.vkBuffer

    private var updateModCount = -1

    init {
        val indexBufInfo = MemoryInfo(
            size = 4L * vertexData.numIndices,
            usage = VK_BUFFER_USAGE_INDEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
            label = "${mesh.name} index data"
        )
        createdIndexBuffer = GrowingBufferVk(backend, indexBufInfo)
        createdFloatBuffer = if (vertexData.byteStrideF == 0) null else {
            val floatBufInfo = MemoryInfo(
                size = vertexData.byteStrideF * vertexData.numVertices.toLong(),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                label = "${mesh.name} vertex float data"
            )
            GrowingBufferVk(backend, floatBufInfo)
        }
        createdIntBuffer = if (vertexData.byteStrideI == 0) null else {
            val intBufInfo = MemoryInfo(
                size = vertexData.byteStrideI * vertexData.numVertices.toLong(),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                label = "${mesh.name} vertex int data"
            )
            GrowingBufferVk(backend, intBufInfo)
        }
    }

    fun checkBuffers(commandBuffer: VkCommandBuffer) {
        checkIsNotReleased()
        if (updateModCount != vertexData.modCount) {
            updateModCount = vertexData.modCount
            createdIndexBuffer.writeData(vertexData.indices, commandBuffer)
            createdFloatBuffer?.writeData(vertexData.dataF, commandBuffer)
            createdIntBuffer?.writeData(vertexData.dataI, commandBuffer)
        }
    }

    override fun doRelease() {
        createdIndexBuffer.buffer.release()
        createdFloatBuffer?.buffer?.release()
        createdIntBuffer?.buffer?.release()
    }
}