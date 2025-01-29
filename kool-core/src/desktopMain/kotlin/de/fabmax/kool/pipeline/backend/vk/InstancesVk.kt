package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT
import org.lwjgl.vulkan.VkCommandBuffer

class InstancesVk(val instances: MeshInstanceList, val backend: RenderBackendVk, mesh: Mesh) : BaseReleasable(), GpuInstances {
    private val device: Device get() = backend.device

    private val createdInstanceBuffer: GrowingBufferVk? = if (instances.instanceSizeF == 0) null else {
        val memInfo = MemoryInfo(
            size = instances.strideBytesF * instances.maxInstances.toLong(),
            usage = VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
            label = "${mesh.name} instance data"
        )
        GrowingBufferVk(backend, memInfo)
    }
    val instanceBuffer: VkBuffer? get() = createdInstanceBuffer?.buffer?.vkBuffer

    private var isNewlyCreated = true

    fun checkBuffers(commandBuffer: VkCommandBuffer) {
        checkIsNotReleased()

        if (instances.hasChanged || isNewlyCreated) {
            createdInstanceBuffer?.writeData(instances.dataF, commandBuffer)
            instances.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        createdInstanceBuffer?.buffer?.release()
        super.release()
    }
}