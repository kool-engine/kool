package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.geometry.IndexedVertexList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class GeometryVk(val mesh: Mesh<*>, val vertexData: IndexedVertexList<*>, val backend: RenderBackendVk) : BaseReleasable(), GpuGeometry {
    val device: Device get() = backend.device

    private val createdIndexBuffer: GrowingBufferVk
    private val createdVertexBuffer: GrowingBufferVk?

    val indexBuffer: VkBuffer get() = createdIndexBuffer.buffer.vkBuffer
    val vertexBuffer: VkBuffer? get() = createdVertexBuffer?.buffer?.vkBuffer

    private var updateModCount = -1

    init {
        val indexBufInfo = MemoryInfo(
            size = 4L * vertexData.numIndices,
            usage = VK_BUFFER_USAGE_INDEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
            label = "${mesh.name} index data"
        )
        createdIndexBuffer = GrowingBufferVk(backend, indexBufInfo)
        createdVertexBuffer = if (vertexData.layout.structSize == 0) null else {
            val floatBufInfo = MemoryInfo(
                size = vertexData.layout.structSize * vertexData.numVertices.toLong(),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                label = "${mesh.name} vertex data"
            )
            GrowingBufferVk(backend, floatBufInfo)
        }
    }

    fun checkBuffers(commandBuffer: VkCommandBuffer) {
        checkIsNotReleased()
        if (vertexData.modCount.isDirty(updateModCount)) {
            updateModCount = vertexData.modCount.count
            createdIndexBuffer.writeData(vertexData.indices, commandBuffer)
            createdVertexBuffer?.writeData(vertexData.vertexData.buffer, commandBuffer)
        }
    }

    override fun doRelease() {
        createdIndexBuffer.buffer.release()
        createdVertexBuffer?.buffer?.release()
    }
}