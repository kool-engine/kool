package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.backend.GpuInstances
import de.fabmax.kool.scene.Mesh
import de.fabmax.kool.scene.MeshInstanceList
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.checkIsNotReleased
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_TRANSFER_DST_BIT
import org.lwjgl.vulkan.VK10.VK_BUFFER_USAGE_VERTEX_BUFFER_BIT

class InstancesVk(val instances: MeshInstanceList, val backend: RenderBackendVk, mesh: Mesh) : BaseReleasable(), GpuInstances {
    private val device: Device get() = backend.device

    private val createdInstanceBuffer: GrowingBufferVk? = if (instances.instanceSizeF == 0) null else {
        val memInfo = MemoryInfo(instances.strideBytesF * instances.maxInstances.toLong(), VK_BUFFER_USAGE_VERTEX_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT)
        GrowingBufferVk(backend, memInfo, "${mesh.name} instance data")
    }
    val instanceBuffer: VkBuffer? get() = createdInstanceBuffer?.buffer?.vkBuffer

    private var isNewlyCreated = true

    fun checkBuffers() {
        checkIsNotReleased()

        if (instances.hasChanged || isNewlyCreated) {
            createdInstanceBuffer?.writeData(instances.dataF)
            instances.hasChanged = false
        }
        isNewlyCreated = false
    }

    override fun release() {
        createdInstanceBuffer?.buffer?.release()
        super.release()
    }
}