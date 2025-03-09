package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.stats.PipelineInfo
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.checkIsNotReleased
import de.fabmax.kool.util.memStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer

sealed class PipelineVk(
    private val pipeline: PipelineBase,
    protected val backend: RenderBackendVk,
): BaseReleasable(), PipelineBackend {

    private val pipelineInfo = PipelineInfo(pipeline)

    protected val device: Device get() = backend.device

    private val bindGroupLayouts: List<VkDescriptorSetLayout> = createBindGroupLayouts(pipeline)
    val pipelineLayout: VkPipelineLayout = createPipelineLayout()

    private fun createBindGroupLayouts(pipeline: PipelineBase): List<VkDescriptorSetLayout> = memStack {
        val layouts = if (this@PipelineVk is ComputePipelineVk) {
            listOf(pipeline.bindGroupLayouts.pipelineScope)
        } else {
            pipeline.bindGroupLayouts.asList
        }

        layouts.map { group ->
            val bindings = callocVkDescriptorSetLayoutBindingN(group.bindings.size) {
                group.bindings.forEachIndexed { i, binding ->
                    this[i].apply {
                        binding(binding.bindingIndex)
                        descriptorType(binding.type.intType())
                        stageFlags(binding.stages.fold(0) { acc, stage -> acc or stage.bitValue() })
                        descriptorCount(1)
                    }
                }
            }
            device.createDescriptorSetLayout(this) {
                pBindings(bindings)
            }
        }
    }

    private fun createPipelineLayout(): VkPipelineLayout = memStack {
        device.createPipelineLayout(this) {
            if (bindGroupLayouts.isNotEmpty()) {
                val ptrs = mallocLong(bindGroupLayouts.size)
                for (i in bindGroupLayouts.indices) {
                    ptrs.put(i, bindGroupLayouts[i].handle)
                }
                this.pSetLayouts(ptrs)
            }
        }
    }

    protected fun BindGroupData.checkBindings(): Boolean {
        if (Time.frameCount == checkFrame) return isCheckOk
        checkFrame = Time.frameCount
        isCheckOk = true

        for (i in bindings.indices) {
            val binding = bindings[i]
            when (binding) {
                is BindGroupData.StorageBufferBindingData -> {
                    isCheckOk = isCheckOk && binding.storageBuffer != null
                }
                is BindGroupData.TextureBindingData<*> -> {
                    val tex = binding.texture
                    if (tex == null || !tex.checkLoadingState()) {
                        isCheckOk = false
                    }
                }
                else -> { }
            }
        }
        return isCheckOk
    }

    private fun <T: ImageData> Texture<T>.checkLoadingState(): Boolean {
        checkIsNotReleased()
        uploadData?.let { backend.textureLoader.loadTexture(this) }
        return isLoaded
    }

    protected fun BindGroupData.getOrCreateVkData(commandBuffer: VkCommandBuffer): BindGroupDataVk {
        if (gpuData == null) {
            val group = if (this@PipelineVk is ComputePipelineVk) 0 else layout.group
            gpuData = BindGroupDataVk(this, bindGroupLayouts[group], backend, commandBuffer)
        }
        return gpuData as BindGroupDataVk
    }

    override fun release() {
        super.release()
        if (!pipeline.isReleased) {
            pipeline.release()
        }
        bindGroupLayouts.forEach { backend.device.destroyDescriptorSetLayout(it) }
        backend.device.destroyPipelineLayout(pipelineLayout)
        pipelineInfo.deleted()
    }

    private fun BindingType.intType() = when (this) {
        BindingType.UNIFORM_BUFFER -> VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER
        BindingType.STORAGE_BUFFER_1D -> VK_DESCRIPTOR_TYPE_STORAGE_BUFFER
        BindingType.STORAGE_BUFFER_2D -> VK_DESCRIPTOR_TYPE_STORAGE_BUFFER
        BindingType.STORAGE_BUFFER_3D -> VK_DESCRIPTOR_TYPE_STORAGE_BUFFER
        BindingType.TEXTURE_1D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_2D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_3D -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_CUBE -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_2D_ARRAY -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.TEXTURE_CUBE_ARRAY -> VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER
        BindingType.STORAGE_TEXTURE_1D -> VK_DESCRIPTOR_TYPE_STORAGE_IMAGE
        BindingType.STORAGE_TEXTURE_2D -> VK_DESCRIPTOR_TYPE_STORAGE_IMAGE
        BindingType.STORAGE_TEXTURE_3D -> VK_DESCRIPTOR_TYPE_STORAGE_IMAGE
    }
}