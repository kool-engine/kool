package de.fabmax.kool.pipeline

import de.fabmax.kool.pipeline.backend.GpuBuffer
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.MixedBuffer

class BindGroupData(val layout: BindGroupLayout) : BaseReleasable() {

    val bindings: List<BindingData> = layout.bindings.map {
        when (it) {
            is UniformBufferLayout -> UniformBufferBindingData(it)
            is Texture1dLayout -> Texture1dBindingData(it)
            is Texture2dLayout -> Texture2dBindingData(it)
            is Texture3dLayout -> Texture3dBindingData(it)
            is TextureCubeLayout -> TextureCubeBindingData(it)
            is StorageTexture1dLayout -> StorageTexture1dBindingData(it)
            is StorageTexture2dLayout -> StorageTexture2dBindingData(it)
            is StorageTexture3dLayout -> StorageTexture3dBindingData(it)
        }
    }
    var isDirty = true

    fun uniformBufferBindingData(bindingIndex: Int) = bindings[bindingIndex] as UniformBufferBindingData
    fun texture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture1dBindingData
    fun texture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture2dBindingData
    fun texture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as Texture3dBindingData
    fun textureCubeBindingData(bindingIndex: Int) = bindings[bindingIndex] as TextureCubeBindingData
    fun storageTexture1dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture1dBindingData
    fun storageTexture2dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture2dBindingData
    fun storageTexture3dBindingData(bindingIndex: Int) = bindings[bindingIndex] as StorageTexture3dBindingData

    override fun release() {
        super.release()
        bindings.filterIsInstance<UniformBufferBindingData>().forEach { it.gpuBuffer?.release() }
    }

    sealed interface BindingData

    inner class UniformBufferBindingData(val binding: UniformBufferLayout) : BindingData {
        var isBufferDirty = true
        val buffer: MixedBuffer = MixedBuffer(binding.layout.size)
        var gpuBuffer: GpuBuffer? = null
            internal set

        fun getAndClearDirtyFlag(): Boolean {
            val isDirty = isBufferDirty
            isBufferDirty = false
            return isDirty
        }
    }

    abstract inner class TextureBindingData<T: Texture, B: TextureLayout>(val binding: B) {
        private val _textures = MutableList<T?>(binding.arraySize) { null }
        val textures: List<T?> get() = _textures

        var texture: T?
            get() = _textures[0]
            set(value) {
                _textures[0] = value
                isDirty = false
            }

        var sampler: SamplerSettings? = null
            set(value) {
                field = value
                isDirty = true
            }

        operator fun set(i: Int, texture: T?) {
            _textures[i] = texture
            isDirty = true
        }
    }

    inner class Texture1dBindingData(binding: Texture1dLayout) : TextureBindingData<Texture1d, Texture1dLayout>(binding), BindingData
    inner class Texture2dBindingData(binding: Texture2dLayout) : TextureBindingData<Texture2d, Texture2dLayout>(binding), BindingData
    inner class Texture3dBindingData(binding: Texture3dLayout) : TextureBindingData<Texture3d, Texture3dLayout>(binding), BindingData
    inner class TextureCubeBindingData(binding: TextureCubeLayout) : TextureBindingData<TextureCube, TextureCubeLayout>(binding), BindingData

    inner class StorageTexture1dBindingData(val binding: StorageTexture1dLayout) : BindingData {
        var storageTexture: StorageTexture1d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture2dBindingData(val binding: StorageTexture2dLayout) : BindingData {
        var storageTexture: StorageTexture2d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture3dBindingData(val binding: StorageTexture3dLayout) : BindingData {
        var storageTexture: StorageTexture3d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }
}
