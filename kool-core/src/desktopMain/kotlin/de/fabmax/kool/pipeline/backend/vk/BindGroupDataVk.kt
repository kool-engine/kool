package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.BindGroupData.BindingData
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
    private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
    private val textureBindings = mutableListOf<TextureBinding>()

    val bindGroup: BindGroup

    private var prepareFrame = -1

    init {
        data.bindings.forEach { binding ->
            when (binding) {
                is BindGroupData.UniformBufferBindingData -> uboBindings += UboBinding(binding, Swapchain.MAX_FRAMES_IN_FLIGHT)

                is BindGroupData.Texture1dBindingData -> textureBindings += Texture1dBinding(binding)
                is BindGroupData.Texture2dBindingData -> textureBindings += Texture2dBinding(binding)
                is BindGroupData.Texture3dBindingData -> textureBindings += Texture3dBinding(binding)
                is BindGroupData.TextureCubeBindingData -> textureBindings += TextureCubeBinding(binding)
                is BindGroupData.Texture2dArrayBindingData -> textureBindings += Texture2dArrayBinding(binding)
                is BindGroupData.TextureCubeArrayBindingData -> textureBindings += TextureCubeArrayBinding(binding)

                is BindGroupData.StorageBuffer1dBindingData -> storageBufferBindings += StorageBufferBinding(binding)
                is BindGroupData.StorageBuffer2dBindingData -> storageBufferBindings += StorageBufferBinding(binding)
                is BindGroupData.StorageBuffer3dBindingData -> storageBufferBindings += StorageBufferBinding(binding)
            }
        }

        val poolLayout = PoolLayout(uboBindings.size, textureBindings.size, storageBufferBindings.size)
        bindGroup = if (data.bindings.isEmpty()) BindGroup.emptyBindGroup else BindGroup(
            poolLayout = poolLayout,
            gpuLayout = gpuLayout,
            uboBindings = uboBindings,
            storageBufferBindings = storageBufferBindings,
            textureBindings = textureBindings,
            backend = backend
        )
    }

    fun updateBuffers(passEncoderState: PassEncoderState) {
        for (i in storageBufferBindings.indices) {
            storageBufferBindings[i].updateBuffer(passEncoderState)
        }
    }

    fun prepareBind(passEncoderState: PassEncoderState) {
        if (prepareFrame == Time.frameCount) return
        prepareFrame = Time.frameCount

        for (i in textureBindings.indices) {
            val tex = textureBindings[i]
            if (tex.binding.texture?.gpuTexture !== tex.boundImage) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
                logT { "$this outdated texture binding: ${tex.binding.texture?.name}, ${passEncoderState.renderPass.name}" }
            }
        }

        val resetBindGroup = data.isDirty || !bindGroup.isInitialized
        if (resetBindGroup) {
            data.isDirty = false
            bindGroup.resetBindGroup()
        }

        val bg = bindGroup
        val frameIdx = passEncoderState.frameIndex
        for (i in bg.uboBindings.indices) {
            val ubo = bg.uboBindings[i]
            if (ubo.isUpdate(frameIdx, ubo.binding.modCount) || resetBindGroup) {
                ubo.binding.buffer.useRaw { raw -> ubo.mappedBuffers[frameIdx].put(raw).flip() }
            }
        }
    }

    override fun release() {
        val alreadyReleased = isReleased
        super.release()
        if (!alreadyReleased) {
            bindGroup.release()
        }
    }

    class BindGroup(
        val poolLayout: PoolLayout,
        val gpuLayout: VkDescriptorSetLayout,
        val uboBindings: List<UboBinding>,
        val storageBufferBindings: List<StorageBufferBinding>,
        val textureBindings: List<TextureBinding>,
        val backend: RenderBackendVk,
    ) : BaseReleasable() {

        var isInitialized = false; private set
        private var isReleasable: Boolean = true
        private var descriptorSets: List<VkDescriptorSet> = emptyList()

        fun resetBindGroup(): Unit = memStack {
            isInitialized = true
            if (!isReleasable) {
                return
            }

            backend.descriptorPools.releaseSets(poolLayout, descriptorSets)
            descriptorSets = backend.descriptorPools.allocateSets(poolLayout, gpuLayout, this)
            val numBindings = uboBindings.size + storageBufferBindings.size + textureBindings.size
            val descriptorWrite = callocVkWriteDescriptorSetN(Swapchain.MAX_FRAMES_IN_FLIGHT * numBindings) { }
            var descriptorWriteIdx = 0
            descriptorSets.forEachIndexed { setIdx, descriptorSet ->
                uboBindings.forEach { ubo ->
                    ubo.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, setIdx, this)
                }
                storageBufferBindings.forEach { storage ->
                    storage.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this)
                }
                textureBindings.forEach { tex ->
                    tex.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this)
                }
            }
            vkUpdateDescriptorSets(backend.device.vkDevice, descriptorWrite, null)
        }

        override fun release() {
            if (isReleasable) {
                super.release()
                uboBindings.forEach { it.release() }
                storageBufferBindings.forEach { it.release() }
                textureBindings.forEach { it.release() }
                backend.descriptorPools.releaseSets(poolLayout, descriptorSets)
            }
        }

        fun getDescriptorSet(frameIndex: Int): VkDescriptorSet {
            return descriptorSets[frameIndex]
        }

        companion object {
            val emptyBindGroup by lazy {
                memStack {
                    val backend = KoolSystem.requireContext().backend as RenderBackendVk
                    val emptyLayout = PoolLayout(0, 0, 0)
                    val emptyPool = backend.device.createDescriptorPool(this) { maxSets(Swapchain.MAX_FRAMES_IN_FLIGHT) }
                    val emptySetLayout = backend.device.createDescriptorSetLayout { }
                    val emptySet = backend.device.allocateDescriptorSets {
                        descriptorPool(emptyPool.handle)
                        pSetLayouts(longs(emptySetLayout.handle, emptySetLayout.handle))
                    }!!
                    BindGroup(emptyLayout, emptySetLayout, emptyList(), emptyList(), emptyList(), backend).also { emptyBg ->
                        emptyBg.descriptorSets = emptySet
                        emptyBg.isReleasable = false
                        backend.device.onRelease {
                            emptyBg.isReleasable = true
                            emptyBg.release()
                            backend.device.destroyDescriptorSetLayout(emptySetLayout)
                            backend.device.destroyDescriptorPool(emptyPool)
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

    private fun Texture1dBinding(binding: BindGroupData.Texture1dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_1D)
    }

    private fun Texture2dBinding(binding: BindGroupData.Texture2dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D)
    }

    private fun Texture3dBinding(binding: BindGroupData.Texture3dBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_3D)
    }

    private fun TextureCubeBinding(binding: BindGroupData.TextureCubeBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE)
    }

    private fun Texture2dArrayBinding(binding: BindGroupData.Texture2dArrayBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D_ARRAY)
    }

    private fun TextureCubeArrayBinding(binding: BindGroupData.TextureCubeArrayBindingData): TextureBinding {
        return TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE_ARRAY)
    }

    inner class TextureBinding(
        val binding: BindGroupData.TextureBindingData<*>,
        val viewType: Int
    ) : BaseReleasable() {

        var boundImage: ImageVk? = null

        private var view: VkImageView? = null
        private var sampler: VkSampler? = null
        private var boundSamplerSettings: SamplerSettings? = null

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            stack: MemoryStack
        ) {
            checkViewAndSampler(stack)
            val imgInfo = stack.callocVkDescriptorImageInfoN(1) {
                this[0].set(
                    sampler!!.handle,
                    view!!.handle,
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

        private fun checkViewAndSampler(stack: MemoryStack) {
            val tex = checkNotNull(binding.texture) { "Cannot create texture binding from null texture" }
            val image = checkNotNull(tex.gpuTexture as ImageVk?) { "Cannot create texture binding from null texture" }
            val samplerSettings = binding.sampler ?: tex.props.defaultSamplerSettings

            if (boundImage == image && boundSamplerSettings == samplerSettings) {
                return
            }
            boundImage = image
            boundSamplerSettings = samplerSettings

            view?.let { backend.device.destroyImageView(it) }
            sampler?.let { backend.device.destroySampler(it) }

            val maxAnisotropy = if (tex.props.generateMipMaps &&
                samplerSettings.minFilter == FilterMethod.LINEAR &&
                samplerSettings.magFilter == FilterMethod.LINEAR
            ) samplerSettings.maxAnisotropy else 1

            val isDepthTex = binding.layout.sampleType == TextureSampleType.DEPTH
            val isUnfilterable = binding.layout.sampleType == TextureSampleType.UNFILTERABLE_FLOAT
            val compare = if (isDepthTex) samplerSettings.compareOp.vk else VK_COMPARE_OP_ALWAYS

            sampler = backend.device.createSampler {
                magFilter(if (isUnfilterable) VK_FILTER_NEAREST else samplerSettings.magFilter.vk)
                minFilter(if (isUnfilterable) VK_FILTER_NEAREST else samplerSettings.minFilter.vk)
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

            view = backend.device.createImageView(
                image = image.vkImage,
                viewType = viewType,
                format = image.format,
                aspectMask = image.imageInfo.aspectMask,
                levelCount = if (samplerSettings.numMipLevels > 0) samplerSettings.numMipLevels else image.mipLevels,
                layerCount = image.arrayLayers,
                baseMipLevel = samplerSettings.baseMipLevel,
                baseArrayLayer = 0,
                stack = stack
            )
        }

        override fun release() {
            super.release()
            view?.let { backend.device.destroyImageView(it) }
            sampler?.let { backend.device.destroySampler(it) }
        }
    }

    private fun StorageBufferBinding(
        binding: BindGroupData.StorageBufferBindingData<*>
    ): StorageBufferBinding {
        val name = (binding as BindingData).name
        val storage = checkNotNull(binding.storageBuffer)
        var gpuBuffer = storage.gpuBuffer as BufferVk?
        if (gpuBuffer == null) {
            gpuBuffer = BufferVk(
                backend = backend,
                bufferInfo = MemoryInfo(
                    size = storage.buffer.limit * 4L,
                    usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_SRC_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                    label = "bindGroup[${data.layout.scope}]-storage-${name}",
                )
            )
            storage.gpuBuffer = gpuBuffer
        }
        return StorageBufferBinding(binding, storage, gpuBuffer)
    }

    inner class StorageBufferBinding(
        val binding: BindGroupData.StorageBufferBindingData<*>,
        val storageBuffer: StorageBuffer,
        val gpuBuffer: BufferVk
    ) : BaseReleasable() {

        fun updateBuffer(passEncoderState: PassEncoderState) {
            if (binding.getAndClearDirtyFlag()) {
                backend.memManager.stagingBuffer(gpuBuffer.bufferSize) { stagingBuf ->
                    when (storageBuffer.buffer) {
                        is Int32Buffer -> {
                            storageBuffer.buffer.useRaw { stagingBuf.mapped!!.asIntBuffer().put(it) }
                        }
                        is Float32Buffer -> {
                            storageBuffer.buffer.useRaw { stagingBuf.mapped!!.asFloatBuffer().put(it) }
                        }
                    }
                    gpuBuffer.copyFrom(stagingBuf, passEncoderState.commandBuffer)
                }
            }
        }

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            stack: MemoryStack
        ) {
            val bufferInfo = stack.callocVkDescriptorBufferInfoN(1) {
                this[0].set(gpuBuffer.vkBuffer.handle, 0L, gpuBuffer.bufferSize)
            }
            descriptorWrite
                .dstSet(descriptorSet.handle)
                .dstBinding((binding as BindingData).layout.bindingIndex)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_STORAGE_BUFFER)
                .descriptorCount(1)
                .pBufferInfo(bufferInfo)
        }
    }
}