package de.fabmax.kool.platform.vk.pipeline

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.drawqueue.DrawCommand
import de.fabmax.kool.platform.vk.*
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Deferred
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkWriteDescriptorSet

abstract class DescriptorObject(val binding: Int, val descriptor: Descriptor) {
    var isValid = true
    var isDescriptorSetUpdateRequired = true

    abstract fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long)

    abstract fun update(cmd: DrawCommand, sys: VkSystem)

    open fun destroy(graphicsPipeline: GraphicsPipeline) { }
}

class UboDescriptor(binding: Int, graphicsPipeline: GraphicsPipeline, private val ubo: UniformBuffer) : DescriptorObject(binding, ubo) {
    private val buffer: Buffer

    init {
        val usage = VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT
        val allocUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
        buffer = Buffer(graphicsPipeline.sys, ubo.size.toLong(), usage, allocUsage).also {
            graphicsPipeline.addDependingResource(it)
        }
    }

    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
        stack.apply {
            val buffereInfo = callocVkDescriptorBufferInfoN(1) {
                buffer(buffer.vkBuffer)
                offset(0L)
                range(ubo.size.toLong())
            }
            vkWriteDescriptorSet
                    .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                    .dstSet(dstSet)
                    .dstBinding(binding)
                    .dstArrayElement(0)
                    .descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER)
                    .descriptorCount(1)
                    .pBufferInfo(buffereInfo)
        }
    }

    override fun update(cmd: DrawCommand, sys: VkSystem) {
        ubo.onUpdate?.invoke(ubo, cmd)
        buffer.mapped { ubo.putTo(MixedBufferImpl(this)) }
    }

    override fun destroy(graphicsPipeline: GraphicsPipeline) {
        graphicsPipeline.removeDependingResource(buffer)
        buffer.destroy()
    }
}

class SamplerDescriptor private constructor(binding: Int, private val sampler: TexSamplerWrapper, desc: Descriptor) : DescriptorObject(binding, desc) {
    private var boundTex = mutableListOf<LoadedTextureVk>()

    private val loadingTextures = mutableListOf<LoadingTex>()

    constructor(binding: Int, sampler2d: TextureSampler) : this(binding, TexSamplerWrapper(sampler2d), sampler2d)
    constructor(binding: Int, samplerCube: CubeMapSampler) : this(binding, TexSamplerWrapper(samplerCube), samplerCube)

    init {
        isValid = false
    }

    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long) {
        stack.apply {

            val imageInfo = callocVkDescriptorImageInfoN(sampler.arraySize) {
                for (i in 0 until sampler.arraySize) {
                    this[i].apply {
                        val vkTex = sampler.textures[i]?.loadedTexture as LoadedTextureVk?
                        imageLayout(VK_IMAGE_LAYOUT_SHADER_READ_ONLY_OPTIMAL)
                        imageView(vkTex?.textureImageView?.vkImageView ?: 0L)
                        sampler(vkTex?.sampler ?: 0L)
                    }
                }
            }
            vkWriteDescriptorSet
                    .sType(VK_STRUCTURE_TYPE_WRITE_DESCRIPTOR_SET)
                    .dstSet(dstSet)
                    .dstBinding(binding)
                    .dstArrayElement(0)
                    .descriptorType(VK_DESCRIPTOR_TYPE_COMBINED_IMAGE_SAMPLER)
                    .descriptorCount(sampler.arraySize)
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

        var allValid = true
        if (boundTex.size != sampler.arraySize) {
            boundTex.clear()
        }
        for (i in 0 until sampler.arraySize) {
            val tex = sampler.textures[i]
            if (tex == null) {
                allValid = false
            } else {
                if (tex.loadingState == Texture.LoadingState.NOT_LOADED) {
                    if (tex.loader != null) {
                        tex.loadingState = Texture.LoadingState.LOADING
                        val deferred = sys.ctx.assetMgr.loadTextureAsync(tex.loader)
                        loadingTextures += LoadingTex(sys, tex, deferred)
                    } else {
                        tex.loadingState = Texture.LoadingState.LOADING_FAILED
                    }
                }

                val bound = if (i < boundTex.size) boundTex[i] else null
                if (tex.loadingState == Texture.LoadingState.LOADED && bound != tex.loadedTexture) {
                    when {
                        i < boundTex.size -> {
                            boundTex[i] = tex.loadedTexture as LoadedTextureVk
                        }
                        i == boundTex.size -> {
                            boundTex.add(tex.loadedTexture as LoadedTextureVk)
                        }
                        else -> {
                            throw IllegalStateException()
                        }
                    }
                    isDescriptorSetUpdateRequired = true
                }

                allValid = allValid && tex.loadingState == Texture.LoadingState.LOADED
            }
        }
        sampler.onUpdate(cmd)

        isValid = allValid
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
        private val loadedTextures = mutableMapOf<TextureData, LoadedTextureVk>()

        private fun getLoadedTex(tex: Texture, texData: TextureData, sys: VkSystem): LoadedTextureVk {
            loadedTextures.values.removeIf { it.isDestroyed }
            return loadedTextures.computeIfAbsent(texData) { k ->
                val loaded = LoadedTextureVk.fromTexData(sys, tex.props, k)
                sys.device.addDependingResource(loaded)
                loaded
            }
        }
    }

    private class TexSamplerWrapper private constructor(
            val mode: Int,
            val sampler2d: TextureSampler? = null,
            val samplerCube: CubeMapSampler? = null,
            val arraySize: Int) {

        constructor(sampler2d: TextureSampler) : this(MODE_2D, sampler2d, null, sampler2d.arraySize)
        constructor(samplerCube: CubeMapSampler) : this(MODE_CUBE, null, samplerCube, samplerCube.arraySize)

        val textures: Array<out Texture?>
            get() = if (mode == MODE_2D) { sampler2d!!.textures } else { samplerCube!!.textures }

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
