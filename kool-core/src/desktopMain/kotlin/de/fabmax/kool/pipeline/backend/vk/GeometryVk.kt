package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

class GeometryVk(val mesh: Mesh, val backend: RenderBackendVk) : BaseReleasable(), GpuGeometry {
    val device: Device get() = backend.device

    private val createdIndexBuffer: GrowingBufferVk
    private val createdFloatBuffer: GrowingBufferVk?
    private val createdIntBuffer: GrowingBufferVk?

    val indexBuffer: VkBuffer get() = createdIndexBuffer.buffer.vkBuffer
    val floatBuffer: VkBuffer? get() = createdFloatBuffer?.buffer?.vkBuffer
    val intBuffer: VkBuffer? get() = createdIntBuffer?.buffer?.vkBuffer

    private var isNewlyCreated = true

    init {
        val geom = mesh.geometry

        val indexBufInfo = MemoryInfo(
            size = 4L * geom.numIndices,
            usage = VK_BUFFER_USAGE_INDEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
            label = "${mesh.name} index data"
        )
        createdIndexBuffer = GrowingBufferVk(backend, indexBufInfo)
        createdFloatBuffer = if (geom.byteStrideF == 0) null else {
            val floatBufInfo = MemoryInfo(
                size = geom.byteStrideF * geom.numVertices.toLong(),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                label = "${mesh.name} vertex float data"
            )
            GrowingBufferVk(backend, floatBufInfo)
        }
        createdIntBuffer = if (geom.byteStrideI == 0) null else {
            val intBufInfo = MemoryInfo(
                size = geom.byteStrideI * geom.numVertices.toLong(),
                usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                label = "${mesh.name} vertex int data"
            )
            GrowingBufferVk(backend, intBufInfo)
        }
    }

    fun checkBuffers(commandBuffer: VkCommandBuffer) {
        checkIsNotReleased()

        val geometry = mesh.geometry
        if (!geometry.isBatchUpdate && (geometry.hasChanged || isNewlyCreated)) {
            createdIndexBuffer.writeData(geometry.indices, commandBuffer)
            createdFloatBuffer?.writeData(geometry.dataF, commandBuffer)
            createdIntBuffer?.writeData(geometry.dataI, commandBuffer)
            geometry.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        super.release()
        createdIndexBuffer.buffer.release()
        createdFloatBuffer?.buffer?.release()
        createdIntBuffer?.buffer?.release()
    }
}