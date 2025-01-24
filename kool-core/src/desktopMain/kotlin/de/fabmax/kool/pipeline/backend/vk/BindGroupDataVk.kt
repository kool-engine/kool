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
    val data: BindGroupData,
    private val gpuLayout: VkDescriptorSetLayout,
    private val backend: RenderBackendVk
) : BaseReleasable(), GpuBindGroupData {
    private val device: Device get() = backend.device

    private val uboBindings = mutableListOf<UboBinding>()
    //private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
    private val textureBindings = mutableListOf<TextureBinding>()

    var bindGroup: BindGroup? = null
        private set

    private var prepareFrame = -1

    fun prepareBind(passEncoderState: PassEncoderState) {
        if (prepareFrame == Time.frameCount) return
        prepareFrame = Time.frameCount

        for (i in textureBindings.indices) {
            val tex = textureBindings[i]
            if (tex.binding.texture?.gpuTexture !== tex.image) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
                logT { "$this outdated texture binding: ${tex.binding.texture?.name}, ${passEncoderState.renderPass.name}" }
            }
        }

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
    }

    private fun createBindGroup(): BindGroup = memStack {
        if (data.bindings.isEmpty()) {
            return@memStack BindGroup.emptyBindGroup
        }
        bindGroup?.release()
        uboBindings.clear()
        textureBindings.clear()

        val numFrames = Swapchain.MAX_FRAMES_IN_FLIGHT

        data.bindings.forEach { binding ->
            when (binding) {
                is BindGroupData.UniformBufferBindingData -> uboBindings += UboBinding(binding, numFrames)

                is BindGroupData.Texture1dBindingData -> textureBindings += Texture1dBinding(binding)
                is BindGroupData.Texture2dBindingData -> textureBindings += Texture2dBinding(binding)
                is BindGroupData.Texture3dBindingData -> textureBindings += Texture3dBinding(binding)
                is BindGroupData.TextureCubeBindingData -> textureBindings += TextureCubeBinding(binding)
                is BindGroupData.Texture2dArrayBindingData -> textureBindings += Texture2dArrayBinding(binding)
                is BindGroupData.TextureCubeArrayBindingData -> textureBindings += TextureCubeArrayBinding(binding)

                is BindGroupData.StorageBuffer1dBindingData -> TODO()
                is BindGroupData.StorageBuffer2dBindingData -> TODO()
                is BindGroupData.StorageBuffer3dBindingData -> TODO()
            }
        }

        val nPoolSizes = uboBindings.size.coerceAtMost(1) + textureBindings.size.coerceAtMost(1)
        val descriptorPool = backend.device.createDescriptorPool(this) {
            val poolSizes = callocVkDescriptorPoolSizeN(nPoolSizes) {
                var iPoolSize = 0
                if (uboBindings.isNotEmpty()) {
                    this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER, uboBindings.size * numFrames)
                }
                if (textureBindings.isNotEmpty()) {
                    this[iPoolSize++].set(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER, textureBindings.size * numFrames)
                }
            }
            pPoolSizes(poolSizes)
            maxSets(numFrames)
        }

        val descriptorSets = backend.device.allocateDescriptorSets(this) {
            val layouts = mallocLong(numFrames)
            repeat(numFrames) { layouts.put(it, gpuLayout.handle) }
            pSetLayouts(layouts)
            descriptorPool(descriptorPool.handle)
        }

        val descriptorWrite = callocVkWriteDescriptorSetN(numFrames * (uboBindings.size + textureBindings.size)) { }
        var descriptorWriteIdx = 0
        descriptorSets.forEachIndexed { setIdx, descriptorSet ->
            uboBindings.forEach { ubo ->
                ubo.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, setIdx, this)
            }
            textureBindings.forEach { tex ->
                tex.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this)
            }
        }
        vkUpdateDescriptorSets(backend.device.vkDevice, descriptorWrite, null)

        return@memStack BindGroup(descriptorPool, descriptorSets, uboBindings, textureBindings, backend)
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            bindGroup?.release()
        }
    }

    class BindGroup(
        val descriptorPool: VkDescriptorPool,
        val descriptorSets: List<VkDescriptorSet>,
        val uboBindings: List<UboBinding>,
        val textureBindings: List<TextureBinding>,
        val backend: RenderBackendVk,
    ) : BaseReleasable() {
        private var isReleasable: Boolean = true

        override fun release() {
            if (isReleasable) {
                super.release()
                uboBindings.forEach { it.release() }
                textureBindings.forEach { it.release() }
                backend.device.destroyDescriptorPool(descriptorPool)
            }
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
                    BindGroup(emptyPool, emptySet, emptyList(), emptyList(), backend).also { emptyBg ->
                        emptyBg.isReleasable = false
                        backend.device.onRelease {
                            emptyBg.isReleasable = true
                            emptyBg.release()
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
        val buffers: List<BufferVk> = List(numSets) {
            BufferVk(
                backend = backend,
                bufferInfo = MemoryInfo(
                    size = layout.size.toLong(),
                    usage = VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT,
                    label = "bindGroup[${data.layout.scope}]-ubo-${binding.name}",
                    createMapped = true
                ),
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

    private fun MemoryStack.Texture1dBinding(binding: BindGroupData.Texture1dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_1D)
    }

    private fun MemoryStack.Texture2dBinding(binding: BindGroupData.Texture2dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D)
    }

    private fun MemoryStack.Texture3dBinding(binding: BindGroupData.Texture3dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_3D)
    }

    private fun MemoryStack.TextureCubeBinding(binding: BindGroupData.TextureCubeBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE)
    }

    private fun MemoryStack.Texture2dArrayBinding(binding: BindGroupData.Texture2dArrayBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D_ARRAY)
    }

    private fun MemoryStack.TextureCubeArrayBinding(binding: BindGroupData.TextureCubeArrayBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE_ARRAY)
    }

    private fun MemoryStack.TextureBinding(
        binding: BindGroupData.TextureBindingData<*>,
        viewType: Int,
    ): TextureBinding {
        val tex = checkNotNull(binding.texture) { "Cannot create texture binding from null texture" }
        val image = checkNotNull(tex.gpuTexture as ImageVk?) { "Cannot create texture binding from null texture" }
        val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings

        val maxAnisotropy = if (tex.props.generateMipMaps &&
            samplerSettings.minFilter == FilterMethod.LINEAR &&
            samplerSettings.magFilter == FilterMethod.LINEAR
        ) samplerSettings.maxAnisotropy else 1

        val isDepthTex = binding.layout.sampleType == TextureSampleType.DEPTH
        val isUnfilterable = binding.layout.sampleType == TextureSampleType.UNFILTERABLE_FLOAT
        val compare = if (isDepthTex) samplerSettings.compareOp.vk else VK_COMPARE_OP_ALWAYS

        val sampler = backend.device.createSampler {
            magFilter(samplerSettings.magFilter.vk)
            minFilter(samplerSettings.minFilter.vk)
            addressModeU(samplerSettings.addressModeU.vk)
            addressModeV(samplerSettings.addressModeV.vk)
            addressModeW(samplerSettings.addressModeW.vk)

            mipmapMode(if (tex.props.generateMipMaps) VK_SAMPLER_MIPMAP_MODE_LINEAR else VK_SAMPLER_MIPMAP_MODE_NEAREST)
            maxLod(VK_LOD_CLAMP_NONE)

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
            aspectMask = if (isDepthTex || isUnfilterable) VK_IMAGE_ASPECT_DEPTH_BIT else VK_IMAGE_ASPECT_COLOR_BIT,
            levelCount = if (samplerSettings.numMipLevels > 0) samplerSettings.numMipLevels else image.mipLevels,
            layerCount = image.arrayLayers,
            baseMipLevel = samplerSettings.baseMipLevel,
            baseArrayLayer = 0,
            stack = this
        )
        return TextureBinding(binding, image, view, sampler)
    }

    inner class TextureBinding(
        val binding: BindGroupData.TextureBindingData<*>,
        val image: ImageVk,
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