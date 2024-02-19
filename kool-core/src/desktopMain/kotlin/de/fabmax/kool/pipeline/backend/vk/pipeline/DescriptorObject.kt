package de.fabmax.kool.pipeline.backend.vk.pipeline

import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.vk.LoadedTextureVk
import de.fabmax.kool.pipeline.backend.vk.VkSystem
import de.fabmax.kool.pipeline.backend.vk.callocVkDescriptorBufferInfoN
import de.fabmax.kool.pipeline.backend.vk.callocVkDescriptorImageInfoN
import de.fabmax.kool.util.MixedBufferImpl
import de.fabmax.kool.util.logE
import kotlinx.coroutines.Deferred
import org.lwjgl.system.MemoryStack
import org.lwjgl.util.vma.Vma
import org.lwjgl.vulkan.VK10.*
import org.lwjgl.vulkan.VkWriteDescriptorSet

abstract class DescriptorObject(val binding: Int, val descriptor: BindingLayout) {
    var isValid = true
    var isDescriptorSetUpdateRequired = true

    abstract fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long, cmd: DrawCommand)

    abstract fun update(cmd: DrawCommand, sys: VkSystem)

    open fun destroy(graphicsPipeline: GraphicsPipeline) { }
}

class UboDescriptor(binding: Int, graphicsPipeline: GraphicsPipeline, private val ubo: UniformBufferLayout) : DescriptorObject(binding, ubo) {
    private val buffer: de.fabmax.kool.pipeline.backend.vk.Buffer

    init {
        val usage = VK_BUFFER_USAGE_UNIFORM_BUFFER_BIT
        val allocUsage = Vma.VMA_MEMORY_USAGE_CPU_TO_GPU
        buffer = de.fabmax.kool.pipeline.backend.vk.Buffer(
            graphicsPipeline.sys,
            ubo.layout.size.toLong(),
            usage,
            allocUsage
        ).also {
            graphicsPipeline.addDependingResource(it)
        }
    }

    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long, cmd: DrawCommand) {
        stack.apply {
            val buffereInfo = callocVkDescriptorBufferInfoN(1) {
                buffer(buffer.vkBuffer)
                offset(0L)
                range(ubo.layout.size.toLong())
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
        val hostBuffer = cmd.pipeline!!.pipelineData.uniformBufferBindingData(binding).buffer as MixedBufferImpl
        hostBuffer.useRaw { host ->
            buffer.mapped { put(host) }
        }
    }

    override fun destroy(graphicsPipeline: GraphicsPipeline) {
        graphicsPipeline.removeDependingResource(buffer)
        buffer.destroy()
    }
}

class SamplerDescriptor private constructor(binding: Int, private val sampler: TexSamplerWrapper, desc: BindingLayout) : DescriptorObject(binding, desc) {
    private var boundTex = mutableListOf<LoadedTextureVk>()

    constructor(binding: Int, sampler1d: Texture1dLayout) : this(binding, TexSamplerWrapper(binding, sampler1d), sampler1d)
    constructor(binding: Int, sampler2d: Texture2dLayout) : this(binding, TexSamplerWrapper(binding, sampler2d), sampler2d)
    constructor(binding: Int, sampler3d: Texture3dLayout) : this(binding, TexSamplerWrapper(binding, sampler3d), sampler3d)
    constructor(binding: Int, samplerCube: TextureCubeLayout) : this(binding, TexSamplerWrapper(binding, samplerCube), samplerCube)

    init {
        isValid = false
    }

    override fun setDescriptorSet(stack: MemoryStack, vkWriteDescriptorSet: VkWriteDescriptorSet, dstSet: Long, cmd: DrawCommand) {
        stack.apply {
            val textures = sampler.getTextures(cmd.pipeline!!.pipelineData)
            val imageInfo = callocVkDescriptorImageInfoN(sampler.arraySize) {
                for (i in 0 until sampler.arraySize) {
                    this[i].apply {
                        val vkTex = textures[i]?.gpuTexture as LoadedTextureVk?
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

        val textures = sampler.getTextures(cmd.pipeline!!.pipelineData)

        var allValid = true
        if (boundTex.size != sampler.arraySize) {
            boundTex.clear()
        }
        for (i in 0 until sampler.arraySize) {
            val tex = textures[i]
            if (tex == null) {
                allValid = false
            } else {
                if (tex.loadingState == Texture.LoadingState.NOT_LOADED) {
                    when (tex.loader) {
                        is AsyncTextureLoader -> {
                            val deferredData = tex.loader.loadTextureDataAsync()
                            loadingTextures += LoadingTex(sys, tex, deferredData)
                        }
                        is SyncTextureLoader -> {
                            val data = tex.loader.loadTextureDataSync()
                            tex.gpuTexture = getLoadedTex(tex, data, sys)
                            tex.loadingState = Texture.LoadingState.LOADED
                        }
                        is BufferedTextureLoader -> {
                            tex.gpuTexture = getLoadedTex(tex, tex.loader.data, sys)
                            tex.loadingState = Texture.LoadingState.LOADED
                        }
                        else -> {
                            // loader is null
                        }
                    }
                }

                val bound = if (i < boundTex.size) boundTex[i] else null
                if (tex.loadingState == Texture.LoadingState.LOADED && bound != tex.gpuTexture) {
                    when {
                        i < boundTex.size -> {
                            boundTex[i] = tex.gpuTexture as LoadedTextureVk
                        }
                        i == boundTex.size -> {
                            boundTex.add(tex.gpuTexture as LoadedTextureVk)
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
                tex.gpuTexture = getLoadedTex(tex, deferredTex.getCompleted(), sys)
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
        val bindingIndex: Int,
        val mode: Int,
        val sampler1d: Texture1dLayout? = null,
        val sampler2d: Texture2dLayout? = null,
        val sampler3d: Texture3dLayout? = null,
        val samplerCube: TextureCubeLayout? = null
    ) {

        val arraySize = 1

        constructor(bindingIndex: Int, sampler1d: Texture1dLayout) : this(bindingIndex, MODE_1D, sampler1d = sampler1d)
        constructor(bindingIndex: Int, sampler2d: Texture2dLayout) : this(bindingIndex, MODE_2D, sampler2d = sampler2d)
        constructor(bindingIndex: Int, sampler3d: Texture3dLayout) : this(bindingIndex, MODE_3D, sampler3d = sampler3d)
        constructor(bindingIndex: Int, samplerCube: TextureCubeLayout) : this(bindingIndex, MODE_CUBE, samplerCube = samplerCube)

//        val textures: Array<out Texture?>
//            get() = when (mode) {
//                MODE_1D -> sampler1d!!.textures
//                MODE_2D -> sampler2d!!.textures
//                MODE_3D -> sampler3d!!.textures
//                MODE_CUBE -> samplerCube!!.textures
//                else -> throw IllegalStateException("Invalid mode: $mode")
//            }

        fun getTextures(data: BindGroupData): List<Texture?> {
            return listOf((data.bindings[bindingIndex] as BindGroupData.TextureBindingData<*>).texture)
        }

        companion object {
            const val MODE_1D = 1
            const val MODE_2D = 2
            const val MODE_3D = 3
            const val MODE_CUBE = 4
        }
    }
}
