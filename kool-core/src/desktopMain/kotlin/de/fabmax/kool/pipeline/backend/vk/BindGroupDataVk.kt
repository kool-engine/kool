package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.BindGroupData.BindingData
import de.fabmax.kool.pipeline.backend.GpuBindGroupData
import de.fabmax.kool.util.*
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkCommandBuffer
import org.lwjgl.vulkan.VkWriteDescriptorSet

class BindGroupDataVk(
    val data: BindGroupData,
    private val gpuLayout: VkDescriptorSetLayout,
    private val backend: RenderBackendVk,
    commandBuffer: VkCommandBuffer
) : BaseReleasable(), GpuBindGroupData {
    private val device: Device get() = backend.device

    private val uboBindings = mutableListOf<UboBinding>()
    private val storageBufferBindings = mutableListOf<StorageBufferBinding>()
    private val textureBindings = mutableListOf<TextureBinding>()
    private val storageTextureBindings = mutableListOf<StorageTextureBinding>()

    val bindGroup: BindGroup

    private var prepareFrame = -1

    init {
        data.bindings.forEach { binding ->
            when (binding) {
                is BindGroupData.UniformBufferBindingData<*> -> uboBindings += UboBinding(binding, Swapchain.MAX_FRAMES_IN_FLIGHT)
                is BindGroupData.StorageBufferBindingData -> storageBufferBindings += StorageBufferBinding(binding)

                is BindGroupData.Texture1dBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_1D)
                is BindGroupData.Texture2dBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D)
                is BindGroupData.Texture3dBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_3D)
                is BindGroupData.TextureCubeBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE)
                is BindGroupData.Texture2dArrayBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D_ARRAY)
                is BindGroupData.TextureCubeArrayBindingData -> textureBindings += TextureBinding(binding, VK_IMAGE_VIEW_TYPE_CUBE_ARRAY)

                is BindGroupData.StorageTexture1dBindingData -> storageTextureBindings += StorageTextureBinding(binding, VK_IMAGE_VIEW_TYPE_1D)
                is BindGroupData.StorageTexture2dBindingData -> storageTextureBindings += StorageTextureBinding(binding, VK_IMAGE_VIEW_TYPE_2D)
                is BindGroupData.StorageTexture3dBindingData -> storageTextureBindings += StorageTextureBinding(binding, VK_IMAGE_VIEW_TYPE_3D)
            }
        }

        val poolLayout = PoolLayout(uboBindings.size, textureBindings.size, storageBufferBindings.size, storageTextureBindings.size)
        bindGroup = if (data.bindings.isEmpty()) BindGroup.emptyBindGroup else BindGroup(
            poolLayout = poolLayout,
            gpuLayout = gpuLayout,
            uboBindings = uboBindings,
            storageBufferBindings = storageBufferBindings,
            textureBindings = textureBindings,
            storageTextureBindings = storageTextureBindings,
            backend = backend
        )
    }

    fun getDescriptorSet(frameIndex: Int): VkDescriptorSet {
        return bindGroup.descriptorSets[frameIndex]
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
                logT { "$this outdated texture binding: ${tex.binding.texture?.name}, pass: ${passEncoderState.renderPass.name}" }
            }
        }
        for (i in storageTextureBindings.indices) {
            val tex = storageTextureBindings[i]
            if (tex.binding.storageTexture?.asTexture?.gpuTexture !== tex.boundImage) {
                // underlying gpu texture has changed, e.g. because render attachment of a render pass was recreated
                data.isDirty = true
                logT { "$this outdated storage texture binding: ${tex.binding.storageTexture?.asTexture?.name}, pass: ${passEncoderState.renderPass.name}" }
            }
        }

        val resetBindGroup = data.isDirty || !bindGroup.isInitialized
        if (resetBindGroup) {
            data.isDirty = false
            bindGroup.resetBindGroup(passEncoderState)
        }

        val frameIdx = passEncoderState.frameIndex
        for (i in bindGroup.uboBindings.indices) {
            val ubo = bindGroup.uboBindings[i]
            if (ubo.isUpdate(frameIdx, ubo.binding.modCount) || resetBindGroup) {
                ubo.binding.buffer.buffer.useRaw { raw -> ubo.mappedBuffers[frameIdx].put(raw).flip() }
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
        val storageTextureBindings: List<StorageTextureBinding>,
        val backend: RenderBackendVk,
    ) : BaseReleasable() {

        private var isReleasable: Boolean = true
        var isInitialized = false; private set
        var descriptorSets: List<VkDescriptorSet> = emptyList(); private set

        fun resetBindGroup(passEncoderState: PassEncoderState): Unit = memStack {
            isInitialized = true
            if (!isReleasable) {
                return
            }

            backend.descriptorPools.releaseSets(poolLayout, descriptorSets)
            descriptorSets = backend.descriptorPools.allocateSets(poolLayout, gpuLayout, this)
            val numBindings = uboBindings.size + storageBufferBindings.size + textureBindings.size + storageTextureBindings.size
            val descriptorWrite = callocVkWriteDescriptorSetN(Swapchain.MAX_FRAMES_IN_FLIGHT * numBindings) { }
            var descriptorWriteIdx = 0
            descriptorSets.forEachIndexed { setIdx, descriptorSet ->
                uboBindings.forEach { ubo ->
                    ubo.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, setIdx, this)
                }
                storageBufferBindings.forEach { storage ->
                    storage.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this, passEncoderState.commandBuffer)
                }
                textureBindings.forEach { tex ->
                    tex.setupDescriptor(descriptorWrite[descriptorWriteIdx++], descriptorSet, this)
                }
                storageTextureBindings.forEach { tex ->
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
                storageTextureBindings.forEach { it.release() }
                backend.descriptorPools.releaseSets(poolLayout, descriptorSets)
            }
        }

        companion object {
            val emptyBindGroup by lazy {
                memStack {
                    val backend = KoolSystem.requireContext().backend as RenderBackendVk
                    val emptyLayout = PoolLayout(0, 0, 0, 0)
                    val emptyPool = backend.device.createDescriptorPool(this) { maxSets(Swapchain.MAX_FRAMES_IN_FLIGHT) }
                    val emptySetLayout = backend.device.createDescriptorSetLayout { }
                    val emptySet = backend.device.allocateDescriptorSets {
                        descriptorPool(emptyPool.handle)
                        pSetLayouts(longs(emptySetLayout.handle, emptySetLayout.handle))
                    }!!
                    BindGroup(emptyLayout, emptySetLayout, emptyList(), emptyList(), emptyList(), emptyList(), backend).also { emptyBg ->
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
        val binding: BindGroupData.UniformBufferBindingData<*>,
        numSets: Int
    ) : BaseReleasable() {
        val struct = binding.buffer.struct
        val buffers: List<GpuBufferVk> = List(numSets) {
            GpuBufferVk(
                backend = backend,
                bufferInfo = MemoryInfo(
                    size = struct.structSize.toLong(),
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
                this[0].set(buffers[setIdx].vkBuffer.handle, 0L, struct.structSize.toLong())
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
            val layout = if (binding.texture is StorageTexture) {
                VK_IMAGE_LAYOUT_GENERAL
            } else {
                VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL
            }

            val imgInfo = stack.callocVkDescriptorImageInfoN(1) {
                this[0].set(sampler!!.handle, view!!.handle, layout)
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
            val samplerSettings = binding.sampler ?: tex.samplerSettings

            if (boundImage == image && boundSamplerSettings == samplerSettings) {
                return
            }
            boundImage = image
            boundSamplerSettings = samplerSettings

            view?.let { backend.device.destroyImageView(it) }
            sampler?.let { backend.device.destroySampler(it) }

            val maxAnisotropy = if (
                tex.mipMapping.isMipMapped &&
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

                mipmapMode(if (tex.mipMapping.isMipMapped) VK_SAMPLER_MIPMAP_MODE_LINEAR else VK_SAMPLER_MIPMAP_MODE_NEAREST)
                maxLod(VK_LOD_CLAMP_NONE)

                val anisotropy = maxAnisotropy.toFloat().coerceAtMost(backend.physicalDevice.maxAnisotropy)
                if (anisotropy > 1) {
                    anisotropyEnable(true)
                    maxAnisotropy(anisotropy)
                }

                compareEnable(isDepthTex)
                compareOp(compare)
            }

            val baseLevel = samplerSettings.baseMipLevel.coerceAtMost(image.imageInfo.mipLevels - 1)
            val numLevels = if (samplerSettings.numMipLevels > 0) samplerSettings.numMipLevels else image.mipLevels
            val numLevelsSafe = numLevels.coerceAtMost(image.imageInfo.mipLevels - baseLevel)

            view = backend.device.createImageView(
                image = image.vkImage,
                viewType = viewType,
                format = image.format,
                aspectMask = image.imageInfo.aspectMask,
                baseMipLevel = baseLevel,
                levelCount = numLevelsSafe,
                baseArrayLayer = 0,
                layerCount = image.arrayLayers,
                stack = stack
            )
        }

        override fun release() {
            super.release()
            view?.let { backend.device.destroyImageView(it) }
            sampler?.let { backend.device.destroySampler(it) }
        }
    }

    inner class StorageTextureBinding(
        val binding: BindGroupData.StorageTextureBindingData<*>,
        val viewType: Int
    ) : BaseReleasable() {

        var boundImage: ImageVk? = null

        private var view: VkImageView? = null

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            stack: MemoryStack
        ) {
            checkView(stack)
            val imgInfo = stack.callocVkDescriptorImageInfoN(1) {
                this[0].set(0L, view!!.handle, VK_IMAGE_LAYOUT_GENERAL)
            }
            descriptorWrite
                .dstSet(descriptorSet.handle)
                .dstBinding(binding.layout.bindingIndex)
                .dstArrayElement(0)
                .descriptorType(VK_DESCRIPTOR_TYPE_STORAGE_IMAGE)
                .descriptorCount(1)
                .pImageInfo(imgInfo)
        }

        private fun checkView(stack: MemoryStack) {
            val tex = checkNotNull(binding.storageTexture) { "Cannot create storage texture binding from null texture" }
            val image = checkNotNull(tex.asTexture.gpuTexture as ImageVk?) { "Cannot create storage texture binding from null texture" }

            if (boundImage == image) {
                return
            }
            boundImage = image

            val baseLevel = binding.mipLevel.coerceAtMost(image.imageInfo.mipLevels - 1)
            if (binding.mipLevel != baseLevel) {
                logE { "$image: binding miplevel: ${binding.mipLevel} > image mip levels: ${image.imageInfo.mipLevels}" }
            }

            view?.let { backend.device.destroyImageView(it) }
            view = backend.device.createImageView(
                image = image.vkImage,
                viewType = viewType,
                format = image.format,
                aspectMask = image.imageInfo.aspectMask,
                baseMipLevel = baseLevel,
                levelCount = 1,
                baseArrayLayer = 0,
                layerCount = image.arrayLayers,
                stack = stack
            )
        }

        override fun release() {
            super.release()
            view?.let { backend.device.destroyImageView(it) }
        }
    }

    inner class StorageBufferBinding(val binding: BindGroupData.StorageBufferBindingData) : BaseReleasable() {
        var boundBuffer: GpuBuffer? = null
        private val gpuBuffer: GpuBufferVk get() = boundBuffer!!.gpuBuffer as GpuBufferVk

        fun updateBuffer(passEncoderState: PassEncoderState) {
            boundBuffer?.uploadData?.let { upload ->
                boundBuffer?.uploadData = null
                backend.memManager.stagingBuffer(gpuBuffer.bufferSize) { stagingBuf ->
                    when (upload) {
                        is Uint8Buffer -> upload.useRaw { stagingBuf.mapped!!.put(it) }
                        is Uint16Buffer -> upload.useRaw { stagingBuf.mapped!!.asShortBuffer().put(it) }
                        is Int32Buffer -> upload.useRaw { stagingBuf.mapped!!.asIntBuffer().put(it) }
                        is Float32Buffer -> upload.useRaw { stagingBuf.mapped!!.asFloatBuffer().put(it) }
                        is MixedBuffer -> upload.useRaw { stagingBuf.mapped!!.put(it) }
                    }
                    gpuBuffer.copyFrom(stagingBuf, passEncoderState.commandBuffer)
                }
            }
        }

        fun setupDescriptor(
            descriptorWrite: VkWriteDescriptorSet,
            descriptorSet: VkDescriptorSet,
            stack: MemoryStack,
            commandBuffer: VkCommandBuffer
        ) {
            checkBuffer(commandBuffer)
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

        private fun checkBuffer(commandBuffer: VkCommandBuffer) {
            val buffer = checkNotNull(binding.storageBuffer) { "Cannot create storage buffer binding from null buffer" }
            if (boundBuffer == buffer) {
                return
            }
            boundBuffer = buffer

            var gpuBuffer = buffer.gpuBuffer as GpuBufferVk?
            if (gpuBuffer == null) {
                val name = (binding as BindingData).name
                gpuBuffer = GpuBufferVk(
                    backend = backend,
                    bufferInfo = MemoryInfo(
                        size = buffer.size * buffer.type.byteSize.toLong(),
                        usage = VK_BUFFER_USAGE_STORAGE_BUFFER_BIT or VK_BUFFER_USAGE_TRANSFER_SRC_BIT or VK_BUFFER_USAGE_TRANSFER_DST_BIT,
                        label = "bindGroup[${data.layout.scope}]-storage-${name}",
                    )
                )
                if (buffer.uploadData == null) {
                    vkCmdFillBuffer(commandBuffer, gpuBuffer.vkBuffer.handle, 0L, VK_WHOLE_SIZE, 0)
                }
                buffer.gpuBuffer = gpuBuffer
            }
        }
    }
}