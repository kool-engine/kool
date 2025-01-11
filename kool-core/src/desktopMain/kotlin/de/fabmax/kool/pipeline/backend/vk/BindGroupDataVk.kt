package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.BindGroupData
import de.fabmax.kool.pipeline.Std140BufferLayout
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.pipeline.backend.wgsl.WgslLocations
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
import de.fabmax.kool.util.useRaw
import org.lwjgl.system.MemoryUtil
import org.lwjgl.util.vma.Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
import org.lwjgl.vulkan.VK10.*

class BindGroupDataVk(
    private val data: BindGroupData,
    private val gpuLayout: VkDescriptorSetLayout,
    private val locations: WgslLocations,
    private val backend: RenderBackendVk
) : BaseReleasable(), GpuBindGroupData {
    private val device: Device get() = backend.device

    var bindGroup: BindGroup? = null
        private set

    fun bind(passEncoderState: PassEncoderState, pipeline: VkPipeline, group: Int = data.layout.group) {
        val recreatedBindGroup = bindGroup == null || data.isDirty
        if (recreatedBindGroup) {
            data.isDirty = false
            bindGroup = createBindGroup()
        }

        val bg = bindGroup!!
        val frameIdx = passEncoderState.frameIndex
        for (i in bg.bufferBindings.indices) {
            val ubo = bg.bufferBindings[i]
            if (ubo.isUpdate(frameIdx, ubo.binding.version) || recreatedBindGroup) {
                ubo.binding.buffer.useRaw { raw ->
                    ubo.mappedBuffers[frameIdx].put(raw).flip()
                }
            }
        }
        passEncoderState.setBindGroup(group, this, pipeline)
    }

    private fun createBindGroup(): BindGroup = memStack {
        bindGroup?.release()

        // todo: other binding types...

        val ubos = data.bindings.filterIsInstance<BindGroupData.UniformBufferBindingData>()
        val uboBindings = ubos.map { ubo ->
            val layout = Std140BufferLayout(ubo.layout.uniforms)
            BufferBinding(ubo, layout)
        }

        val descriptorPool = backend.device.createDescriptorPool(this) {
            if (uboBindings.isNotEmpty()) {
                val poolSizes = callocVkDescriptorPoolSizeN(1) {
                    this[0].let {
                        it.descriptorCount(uboBindings.size)
                        it.type(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    }
                }
                pPoolSizes(poolSizes)
            }
            maxSets(Swapchain.MAX_FRAMES_IN_FLIGHT)
        }

        val descriptorSets = backend.device.allocateDescriptorSets(this) {
            descriptorPool(descriptorPool.handle)

            val layouts = mallocLong(Swapchain.MAX_FRAMES_IN_FLIGHT)
            repeat(Swapchain.MAX_FRAMES_IN_FLIGHT) { layouts.put(it, gpuLayout.handle) }
            pSetLayouts(layouts)
        }

        descriptorSets.forEachIndexed { i, descriptorSet ->
            if (uboBindings.isNotEmpty()) {
                val descriptorWrite = callocVkWriteDescriptorSetN(uboBindings.size) { }
                for (uboIdx in uboBindings.indices) {
                    val ubo = uboBindings[uboIdx]
                    val bufferInfo = callocVkDescriptorBufferInfoN(1) {
                        this[0].set(ubo.setEntries[i].vkBuffer.handle, 0L, ubo.layout.size.toLong())
                    }

                    descriptorWrite[uboIdx]
                        .dstSet(descriptorSet.handle)
                        .dstBinding(ubo.binding.layout.bindingIndex)
                        .dstArrayElement(0)
                        .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                        .descriptorCount(uboBindings.size)
                        .pBufferInfo(bufferInfo)
                }
                vkUpdateDescriptorSets(backend.device.vkDevice, descriptorWrite, null)
            }
        }
        return@memStack BindGroup(descriptorPool, descriptorSets, uboBindings)
    }

    inner class BindGroup(
        val descriptorPool: VkDescriptorPool,
        val descriptorSets: List<VkDescriptorSet>,
        val bufferBindings: List<BufferBinding>
    ) : BaseReleasable() {
        init {
            bufferBindings.forEach { it.releaseWith(this) }
        }

        override fun release() {
            super.release()
            backend.device.destroyDescriptorPool(descriptorPool)
        }
    }

    inner class BufferBinding(
        val binding: BindGroupData.UniformBufferBindingData,
        val layout: Std140BufferLayout
    ) : BaseReleasable() {
        val setEntries: List<Buffer> = List(Swapchain.MAX_FRAMES_IN_FLIGHT) {
            Buffer(
                backend,
                layout.size.toLong(),
                VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                VMA_MEMORY_USAGE_CPU_TO_GPU,    // todo: USAGE_AUTO + create mapped flags
                "bindGroup[${data.layout.scope}]-ubo-${binding.name}"
            )
        }

        private val bufferVersions = IntArray(setEntries.size) { -1 }
        val mappedBuffers = setEntries.map { buffer ->
            val addr = backend.memManager.mapMemory(buffer.vkBuffer)
            MemoryUtil.memByteBuffer(addr, buffer.bufferSize.toInt())
        }

        fun isUpdate(frameIndex: Int, version: Int): Boolean {
            if (bufferVersions[frameIndex] != version) {
                bufferVersions[frameIndex] = version
                return true
            }
            return false
        }

        override fun release() {
            super.release()
            setEntries.forEach {
                backend.memManager.unmapMemory(it.vkBuffer)
                it.release()
            }
        }
    }
}