package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.BufferedTextureData
import de.fabmax.kool.TextureData
import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.ImageTextureData
import de.fabmax.kool.platform.vk.Buffer
import de.fabmax.kool.platform.vk.VkSystem
import de.fabmax.kool.platform.vk.callocVkDescriptorBufferInfoN
import de.fabmax.kool.platform.vk.callocVkDescriptorImageInfoN
import de.fabmax.kool.util.Float32BufferImpl
import kotlinx.coroutines.Deferred
import org.lwjgl.system.MemoryStack
import org.lwjgl.vulkan.VK10
import org.lwjgl.vulkan.VkWriteDescriptorSet

abstract class DescriptorObject(val binding: Int, val descriptor: Descriptor) {
    var isValid = true
    var isDescriptorSetUpdateRequired = true

    abstract fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long)

    abstract fun update(cmd: DrawCommand, sys: VkSystem)
}

class UboDescriptor(binding: Int, val ubo: UniformBuffer, val buffer: Buffer) : DescriptorObject(binding, ubo) {
    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
        stack.apply {
            val buffereInfo = callocVkDescriptorBufferInfoN(1) {
                buffer(buffer.vkBuffer)
                offset(0L)
                range(ubo.size.toLong())
            }
            vkWriteDescriptorSet
                    .sType(VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                    .dstSet(dstSet)
                    .dstBinding(binding)
                    .dstArrayElement(0)
                    .descriptorType(VK10.VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1)
                    .pBufferInfo(buffereInfo)
        }
    }

    override fun update(cmd: DrawCommand, sys: VkSystem) {
        ubo.onUpdate?.invoke(ubo, cmd)
        buffer.mappedFloats { ubo.putTo(Float32BufferImpl(this)) }
    }
}

class SamplerDescriptor(binding: Int, val sampler: TextureSampler) : DescriptorObject(binding, sampler) {
    private var boundTex: LoadedTexture? = null

    private val loadingTextures = mutableListOf<LoadingTex>()

    init {
        isValid = false
    }

    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
        stack.apply {
            val vkTex = sampler.texture?.loadedTexture

            val imageInfo = callocVkDescriptorImageInfoN(1) {
                imageLayout(VK10.VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                imageView(vkTex?.textureImageView?.vkImageView ?: 0L)
                sampler(vkTex?.sampler ?: 0L)
            }
            vkWriteDescriptorSet
                    .sType(VK10.VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                    .dstSet(dstSet)
                    .dstBinding(binding)
                    .dstArrayElement(0)
                    .descriptorType(VK10.VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .descriptorCount(1)
                    .pImageInfo(imageInfo)
        }
    }

    override fun update(cmd: DrawCommand, sys: VkSystem) {
        if (loadingTextures.isNotEmpty()) {
            val iterator = loadingTextures.iterator()
            for (loading in iterator) {
                if (loading.pollCompleted()) {
                    iterator.remove()
                    isDescriptorSetUpdateRequired = true
                }
            }
        }

        sampler.texture?.let { tex ->
            if (tex.loadingState == Texture.LoadingState.NOT_LOADED) {
                tex.loadingState = Texture.LoadingState.LOADING
                val deferred = sys.ctx.assetMgr.loadTextureAsync(tex.loader)
                loadingTextures += LoadingTex(sys, tex, deferred)
            }

            if (tex.loadingState == Texture.LoadingState.LOADED && boundTex != tex.loadedTexture) {
                boundTex = tex.loadedTexture
            }
        }
        sampler.onUpdate?.invoke(sampler, cmd)

        isValid = sampler.texture?.loadingState == Texture.LoadingState.LOADED
    }

    private class LoadingTex(val sys: VkSystem, val tex: Texture, val deferredTex: Deferred<TextureData>) {
        var isCompleted = false

        init {
            deferredTex.invokeOnCompletion { ex ->
                if (ex != null) {
                    tex.loadingState = Texture.LoadingState.LOADING_FAILED
                }
                isCompleted = true
            }
        }

        fun pollCompleted(): Boolean {
            if (isCompleted && tex.loadingState != Texture.LoadingState.LOADING_FAILED) {
                val texData = deferredTex.getCompleted()
                val loadedTex = when (texData) {
                    is ImageTextureData -> LoadedTexture.fromImageTextureData(sys, texData)
                    is BufferedTextureData -> LoadedTexture.fromBufferedTextureData(sys, texData)
                    else -> throw IllegalArgumentException("Unsupported texture format")
                }
                sys.device.addDependingResource(loadedTex)
                tex.loadedTexture = loadedTex
                tex.loadingState = Texture.LoadingState.LOADED
            }
            return isCompleted
        }
    }
}