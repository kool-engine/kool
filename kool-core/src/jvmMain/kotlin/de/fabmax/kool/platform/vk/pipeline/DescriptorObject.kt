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

    constructor(binding: Int, sampler1d: TextureSampler1d) : this(binding, TexSamplerWrapper(sampler1d), sampler1d)
    constructor(binding: Int, sampler2d: TextureSampler2d) : this(binding, TexSamplerWrapper(sampler2d), sampler2d)
    constructor(binding: Int, sampler3d: TextureSampler3d) : this(binding, TexSamplerWrapper(sampler3d), sampler3d)
    constructor(binding: Int, samplerCube: TextureSamplerCube) : this(binding, TexSamplerWrapper(samplerCube), samplerCube)

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
                // do not create LoadedTextureVk yet: we are in the wrong thread
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
        private val loadingTextures = mutableListOf<LoadingTex>()
        private val loadedTextures = mutableMapOf<TextureData, LoadedTextureVk>()

        private fun getLoadedTex(tex: Texture, texData: TextureData, sys: VkSystem): LoadedTextureVk {
            return synchronized(loadedTextures) {
                loadedTextures.values.removeIf { it.isDestroyed }
                loadedTextures.computeIfAbsent(texData) { k ->
                    val loaded = LoadedTextureVk.fromTexData(sys, tex.props, k)
                    sys.device.addDependingResource(loaded)
                    loaded
                }
            }
        }
    }

    private class TexSamplerWrapper private constructor(
            val mode: Int,
            val sampler1d: TextureSampler1d? = null,
            val sampler2d: TextureSampler2d? = null,
            val sampler3d: TextureSampler3d? = null,
            val samplerCube: TextureSamplerCube? = null,
            val arraySize: Int) {

        constructor(sampler1d: TextureSampler1d) : this(MODE_1D, sampler1d = sampler1d, arraySize = sampler1d.arraySize)
        constructor(sampler2d: TextureSampler2d) : this(MODE_2D, sampler2d = sampler2d, arraySize = sampler2d.arraySize)
        constructor(sampler3d: TextureSampler3d) : this(MODE_3D, sampler3d = sampler3d, arraySize = sampler3d.arraySize)
        constructor(samplerCube: TextureSamplerCube) : this(MODE_CUBE, samplerCube = samplerCube, arraySize = samplerCube.arraySize)

        val textures: Array<out Texture?>
            get() = when (mode) {
                MODE_1D -> sampler1d!!.textures
                MODE_2D -> sampler2d!!.textures
                MODE_3D -> sampler3d!!.textures
                MODE_CUBE -> samplerCube!!.textures
                else -> throw IllegalStateException("Invalid mode: $mode")
            }

        fun onUpdate(cmd: DrawCommand) {
            when (mode) {
                MODE_1D -> sampler1d!!.onUpdate?.invoke(sampler1d, cmd)
                MODE_2D -> sampler2d!!.onUpdate?.invoke(sampler2d, cmd)
                MODE_3D -> sampler3d!!.onUpdate?.invoke(sampler3d, cmd)
                MODE_CUBE -> samplerCube!!.onUpdate?.invoke(samplerCube, cmd)
                else -> throw IllegalStateException("Invalid mode: $mode")
            }
        }

        companion object {
            const val MODE_1D = 1
            const val MODE_2D = 2
            const val MODE_3D = 3
            const val MODE_CUBE = 4
        }
    }
}
