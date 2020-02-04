package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.drawqueue.DrawCommand
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.platform.vk.Buffer
import de.fabmax.kool.platform.vk.VkSystem
import de.fabmax.kool.platform.vk.callocVkDescriptorBufferInfoN
import de.fabmax.kool.platform.vk.callocVkDescriptorImageInfoN
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.logE
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
        buffer.mapped { ubo.putTo(MixedBufferImpl(this)) }
    }
}

class SamplerDescriptor private constructor(binding: Int, private val sampler: TexSamplerWrapper, desc: Descriptor) : DescriptorObject(binding, desc) {
    private var boundTex: LoadedTexture? = null

    private val loadingTextures = mutableListOf<LoadingTex>()

    constructor(binding: Int, sampler2d: TextureSampler) : this(binding, TexSamplerWrapper(sampler2d), sampler2d)
    constructor(binding: Int, samplerCube: CubeMapSampler) : this(binding, TexSamplerWrapper(samplerCube), samplerCube)

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
                if (tex.loader != null) {
                    tex.loadingState = Texture.LoadingState.LOADING
                    val deferred = sys.ctx.assetMgr.loadTextureAsync(tex.loader)
                    loadingTextures += LoadingTex(sys, tex, deferred)
                } else {
                    tex.loadingState = Texture.LoadingState.LOADING_FAILED
                }
            }

            if (tex.loadingState == Texture.LoadingState.LOADED && boundTex != tex.loadedTexture) {
                boundTex = tex.loadedTexture
                isDescriptorSetUpdateRequired = true
            }
        }
        sampler.onUpdate(cmd)

        isValid = sampler.texture?.loadingState == Texture.LoadingState.LOADED
    }

    private class LoadingTex(val sys: VkSystem, val tex: Texture, val deferredTex: Deferred<TextureData>) {
        var isCompleted = false

        init {
            deferredTex.invokeOnCompletion { ex ->
                if (ex != null) {
                    tex.loadingState = Texture.LoadingState.LOADING_FAILED
                    logE { "Texture loading failed: $ex" }
                }
                isCompleted = true
            }
        }

        fun pollCompleted(): Boolean {
            if (isCompleted && tex.loadingState != Texture.LoadingState.LOADING_FAILED) {
                tex.loadedTexture = getLoadedTex(tex, deferredTex.getCompleted(), sys)
                tex.loadingState = Texture.LoadingState.LOADED
            }
            return isCompleted
        }
    }

    companion object {
        // todo: integrate texture manager
        private val loadedTextures = mutableMapOf<TextureData, LoadedTexture>()

        private fun getLoadedTex(tex: Texture, texData: TextureData, sys: VkSystem): LoadedTexture {
            loadedTextures.values.removeIf { it.isDestroyed }
            return loadedTextures.computeIfAbsent(texData) { k ->
                val loaded = LoadedTexture.fromTexData(sys, tex.props, k)
                sys.device.addDependingResource(loaded)
                loaded
            }
        }
    }

    private class TexSamplerWrapper private constructor(
            val mode: Int,
            val sampler2d: TextureSampler? = null,
            val samplerCube: CubeMapSampler? = null) {

        constructor(sampler2d: TextureSampler) : this(MODE_2D, sampler2d, null)
        constructor(samplerCube: CubeMapSampler) : this(MODE_CUBE, null, samplerCube)

        val texture: Texture?
            get() = if (mode == MODE_2D) { sampler2d!!.texture } else { samplerCube!!.texture }

        fun onUpdate(cmd: DrawCommand) {
            if (mode == MODE_2D) { sampler2d!!.onUpdate?.invoke(sampler2d, cmd) }
            else { samplerCube!!.onUpdate?.invoke(samplerCube, cmd) }
        }

        companion object {
            const val MODE_2D = 1
            const val MODE_CUBE = 2
        }
    }
}