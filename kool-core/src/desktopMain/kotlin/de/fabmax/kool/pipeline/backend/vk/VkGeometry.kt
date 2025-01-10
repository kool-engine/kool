package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuGeometry
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_INDEX_BUFFER_BIT
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT

class VkGeometry(val mesh: Mesh, val backend: RenderBackendVk) : BaseReleasable(), GpuGeometry {
    val device: Device get() = backend.device

    private val createdIndexBuffer: VertexBuffer
    private val createdFloatBuffer: VertexBuffer
    private val createdIntBuffer: VertexBuffer?

    val indexBuffer: VkBuffer get() = createdIndexBuffer.buffer.vkBuffer
    val floatBuffer: VkBuffer get() = createdFloatBuffer.buffer.vkBuffer
    val intBuffer: VkBuffer? get() = createdIntBuffer?.buffer?.vkBuffer

    private var isNewlyCreated = true

    init {
        val geom = mesh.geometry
        createdIndexBuffer = VertexBuffer(backend, "${mesh.name} index data", 4L * geom.numIndices, VK_BUFFER_USAGE_INDEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT)
        createdFloatBuffer = VertexBuffer(backend, "${mesh.name} vertex float data", geom.byteStrideF * geom.numVertices.toLong())
        createdIntBuffer = if (geom.byteStrideI == 0) null else {
            VertexBuffer(backend, "${mesh.name} vertex int data", geom.byteStrideI * geom.numVertices.toLong())
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