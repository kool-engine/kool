package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.*

class VkGeometry(val mesh: Mesh, val backend: RenderBackendVk) : BaseReleasable(), GpuGeometry {
    val device: Device get() = backend.device

    private val createdIndexBuffer: GrowingBufferVk
    private val createdFloatBuffer: GrowingBufferVk
    private val createdIntBuffer: GrowingBufferVk?

    val indexBuffer: VkBuffer get() = createdIndexBuffer.buffer.vkBuffer
    val floatBuffer: VkBuffer get() = createdFloatBuffer.buffer.vkBuffer
    val intBuffer: VkBuffer? get() = createdIntBuffer?.buffer?.vkBuffer

    private var isNewlyCreated = true

    init {
        val geom = mesh.geometry

        val indexBufInfo = MemoryInfo(4L * geom.numIndices, VK_BUFFER_USAGE_INDEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT)
        val floatBufInfo = MemoryInfo(geom.byteStrideF * geom.numVertices.toLong(), VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT)
        createdIndexBuffer = GrowingBufferVk(backend, indexBufInfo, "${mesh.name} index data")
        createdFloatBuffer = GrowingBufferVk(backend, floatBufInfo, "${mesh.name} vertex float data")
        createdIntBuffer = if (geom.byteStrideI == 0) null else {
            val intBufInfo = MemoryInfo(geom.byteStrideI * geom.numVertices.toLong(), VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT)
            GrowingBufferVk(backend, intBufInfo, "${mesh.name} vertex int data")
        }
    }

    fun checkBuffers() {
        checkIsNotReleased()

        val geometry = mesh.geometry
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
    }
}