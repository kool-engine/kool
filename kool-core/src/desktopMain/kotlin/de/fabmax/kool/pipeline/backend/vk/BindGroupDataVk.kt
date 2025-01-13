package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.BindGroupData
import de.fabmax.kool.pipeline.FilterMethod
import de.fabmax.kool.pipeline.Std140BufferLayout
import de.fabmax.kool.pipeline.TextureSampleType
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkWriteDescriptorSet

class BindGroupDataVk(
    private val data: BindGroupData,
    private val gpuLayout: VkDescriptorSetLayout,
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
        for (i in bg.uboBindings.indices) {
            val ubo = bg.uboBindings[i]
            if (ubo.isUpdate(frameIdx, ubo.binding.modCount) || recreatedBindGroup) {
                ubo.binding.buffer.useRaw { raw -> ubo.mappedBuffers[frameIdx].put(raw).flip() }
            }
        }
        passEncoderState.setBindGroup(group, this, pipeline)
    }

    private fun createBindGroup(): BindGroup = memStack {
        if (data.bindings.isEmpty()) {
            return@memStack BindGroup.emptyBindGroup
        }
        bindGroup?.release()

        val numSets = Swapchain.MAX_FRAMES_IN_FLIGHT

        val ubos = data.bindings.filterIsInstance<BindGroupData.UniformBufferBindingData>()
        val textures2d = data.bindings.filterIsInstance<BindGroupData.Texture2dBindingData>()

        val uboBindings = ubos.map { ubo -> UboBinding(ubo, numSets) }
        val texture2dBindings = textures2d.map { tex -> Texture2dBinding(tex) }

        val nPoolSizes = ubos.size.coerceAtMost(1) + textures2d.size.coerceAtMost(1)
        val descriptorPool = backend.device.createDescriptorPool(this) {
            val poolSizes = callocVkDescriptorPoolSizeN(nPoolSizes) {
                var iPoolSize = 0
                if (ubos.isNotEmpty()) {
                    this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, ubos.size)
                }
                if (textures2d.isNotEmpty()) {
                    this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, textures2d.size)
                }
            }
            pPoolSizes(poolSizes)
            maxSets(numSets)
        }

        val descriptorSets = backend.device.allocateDescriptorSets(this) {
            val layouts = mallocLong(numSets)
            repeat(numSets) { layouts.put(it, gpuLayout.handle) }
            pSetLayouts(layouts)
            descriptorPool(descriptorPool.handle)
        }

        val descriptorWrite = callocVkWriteDescriptorSetN(numSets * (uboBindings.size + textures2d.size)) { }
        var descriptorWriteIdx = 0
        uboBindings.forEach { ubo ->
            descriptorSets.forEachIndexed { setIdx, descriptorSet ->
                ubo.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, setIdx, this)
            }
        }
        texture2dBindings.forEach { tex ->
            descriptorSets.forEach { descriptorSet ->
                tex.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this)
            }
        }
        vkUpdateDescriptorSets(backend.device.vkDevice, descriptorWrite, null)

        return@memStack BindGroup(descriptorPool, descriptorSets, uboBindings, texture2dBindings, backend)
    }

    override fun release() {
        checkIsNotReleased()
        super.release()
        bindGroup?.release()
    }

    class BindGroup(
        val descriptorPool: VkDescriptorPool,
        val descriptorSets: List<VkDescriptorSet>,
        val uboBindings: List<UboBinding>,
        val textureBindings: List<TextureBinding>,
        val backend: RenderBackendVk,
    ) : BaseReleasable() {

        init {
            uboBindings.forEach { it.releaseWith(this) }
            textureBindings.forEach { it.releaseWith(this) }
        }

        override fun release() {
            super.release()
            backend.device.destroyDescriptorPool(descriptorPool)
        }

        fun getDescriptorSet(frameIndex: Int): VkDescriptorSet {
            return if (frameIndex < descriptorSets.size) {
                descriptorSets[frameIndex]
            } else {
                descriptorSets[0]
            }
        }

        companion object {
            val emptyBindGroup by lazy {
                memStack {
                    val backend = KoolSystem.requireContext().backend as RenderBackendVk
                    val emptyPool = backend.device.createDescriptorPool(this) { maxSets(1) }
                    val emptySetLayout = backend.device.createDescriptorSetLayout { }
                    val emptySet = backend.device.allocateDescriptorSets {
                        descriptorPool(emptyPool.handle)
                        pSetLayouts(longs(emptySetLayout.handle))
                    }
                    BindGroup(emptyPool, emptySet, emptyList(), emptyList(), backend).also {
                        it.releaseWith(backend.device)
                        it.onRelease {
                            backend.device.destroyDescriptorSetLayout(emptySetLayout)
                        }
                    }
                }
            }
        }
    }

    inner class UboBinding(
        val binding: BindGroupData.UniformBufferBindingData,
        numSets: Int
    ) : BaseReleasable() {
        val layout: Std140BufferLayout = Std140BufferLayout(binding.layout.uniforms)
        val buffers: List<Buffer> = List(numSets) {
            Buffer(
                backend,
                MemoryInfo(layout.size.toLong(), VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT, createMapped = true),
                "bindGroup[${data.layout.scope}]-ubo-${binding.name}"
            )
        }

        private val modCounts = IntArray(buffers.size) { -1 }
        val mappedBuffers = buffers.map { buffer ->
            checkNotNull(buffer.vkBuffer.mapped) { "UBO buffer was not created as mapped buffer" }
        }

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            setIdx: Int,
            stack: MemoryStack
        ) {
            val bufferInfo = stack.callocVkDescriptorBufferInfoN(1) {
                this[0].set(buffers[setIdx].vkBuffer.handle, 0L, layout.size.toLong())
            }
            descriptorWrite
                .dstSet(descriptorSet.handle)
                .dstBinding(binding.layout.bindingIndex)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                .descriptorCount(1)
                .pBufferInfo(bufferInfo)
        }

        fun isUpdate(frameIndex: Int, modCount: Int): Boolean {
            if (modCounts[frameIndex] != modCount) {
                modCounts[frameIndex] = modCount
                return true
            }
            return false
        }

        override fun release() {
            super.release()
            buffers.forEach { it.release() }
        }
    }

    private fun MemoryStack.Texture2dBinding(binding: BindGroupData.Texture2dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D)
    }

    private fun MemoryStack.TextureBinding(
        binding: BindGroupData.TextureBindingData<*>,
        viewType: Int,
    ): TextureBinding {
        val tex = checkNotNull(binding.texture) { "Cannot create texture binding from null texture" }
        val loadedTex = checkNotNull(tex.gpuTexture as LoadedTextureVk?) { "Cannot create texture binding from null texture" }
        val image = loadedTex.image
        val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings

        val maxAnisotropy = if (tex.props.generateMipMaps &&
            samplerSettings.minFilter == FilterMethod.LINEAR &&
            samplerSettings.magFilter == FilterMethod.LINEAR
        ) samplerSettings.maxAnisotropy else 1

        val isDepthTex = binding.layout.sampleType == TextureSampleType.DEPTH
        val compare = if (isDepthTex) samplerSettings.compareOp.vk else VK_COMPARE_OP_ALWAYS

        val sampler = backend.device.createSampler {
            magFilter(samplerSettings.magFilter.vk)
            minFilter(samplerSettings.minFilter.vk)
            addressModeU(samplerSettings.addressModeU.vk)
            addressModeV(samplerSettings.addressModeV.vk)
            addressModeW(samplerSettings.addressModeW.vk)

            mipmapMode(if (tex.props.generateMipMaps) VK_SAMPLER_MIPMAP_MODE_LINEAR else VK_SAMPLER_MIPMAP_MODE_NEAREST)

            val anisotropy = maxAnisotropy.toFloat().coerceAtMost(backend.physicalDevice.maxAnisotropy)
            if (anisotropy > 1) {
                anisotropyEnable(true)
                maxAnisotropy(anisotropy)
            }

            compareEnable(isDepthTex)
            compareOp(compare)
        }

        val view = backend.device.createImageView(
            image = image.vkImage,
            viewType = viewType,
            format = image.format,
            aspectMask = if (isDepthTex) VK_IMAGE_ASPECT_DEPTH_BIT else VK_IMAGE_ASPECT_COLOR_BIT,
            levelCount = if (samplerSettings.numMipLevels > 0) samplerSettings.numMipLevels else image.mipLevels,
            layerCount = image.arrayLayers,
            baseMipLevel = samplerSettings.baseMipLevel,
            baseArrayLayer = 0,
            stack = this
        )
        return TextureBinding(binding, view, sampler)
    }

    inner class TextureBinding(
        val binding: BindGroupData.TextureBindingData<*>,
        val view: VkImageView,
        val sampler: VkSampler
    ) : BaseReleasable() {

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            stack: MemoryStack
        ) {
            val imgInfo = stack.callocVkDescriptorImageInfoN(1) {
                this[0].set(
                    sampler.handle,
                    view.handle,
                    VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
                )
            }
            descriptorWrite
                .dstSet(descriptorSet.handle)
                .dstBinding(binding.layout.bindingIndex)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                .descriptorCount(1)
                .pImageInfo(imgInfo)
        }

        override fun release() {
            super.release()
            backend.device.destroyImageView(view)
            backend.device.destroySampler(sampler)
        }
    }
}