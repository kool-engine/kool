package de.fabmax.kool.pipeline

import de.fabmax.kool.util.MixedBuffer

class BindGroupData(val layout: BindGroupLayout) {

    val bindings: List<BindingData> = layout.bindings.map {
        when (it) {
            is UniformBufferBinding -> UniformBufferData(it)
            is Texture1dBinding -> TextureBinding1dData(it)
            is Texture2dBinding -> TextureBinding2dData(it)
            is Texture3dBinding -> TextureBinding3dData(it)
            is TextureCubeBinding -> TextureBindingCubeData(it)
            is StorageTexture1dBinding -> StorageTexture1dData(it)
            is StorageTexture2dBinding -> StorageTexture2dData(it)
            is StorageTexture3dBinding -> StorageTexture3dData(it)
        }
    }
    var isDirty = true

    fun uniformBufferData(binding: Int) = bindings[binding] as UniformBufferData

    sealed interface BindingData

    inner class UniformBufferData(val binding: UniformBufferBinding) : BindingData {
        val buffer: MixedBuffer = MixedBuffer(binding.layout.size)
        var isBufferDirty = true

        fun getAndClearDirtyFlag(): Boolean {
            val isDirty = isBufferDirty
            isBufferDirty = false
            return isDirty
        }
    }

    abstract inner class TextureBindingData<T: Texture, B: TextureBinding>(val binding: B) {
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

    inner class TextureBinding1dData(binding: Texture1dBinding) : TextureBindingData<Texture1d, Texture1dBinding>(binding), BindingData
    inner class TextureBinding2dData(binding: Texture2dBinding) : TextureBindingData<Texture2d, Texture2dBinding>(binding), BindingData
    inner class TextureBinding3dData(binding: Texture3dBinding) : TextureBindingData<Texture3d, Texture3dBinding>(binding), BindingData
    inner class TextureBindingCubeData(binding: TextureCubeBinding) : TextureBindingData<TextureCube, TextureCubeBinding>(binding), BindingData

    inner class StorageTexture1dData(val binding: StorageTexture1dBinding) : BindingData {
        var storageTexture: StorageTexture1d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture2dData(val binding: StorageTexture2dBinding) : BindingData {
        var storageTexture: StorageTexture2d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture3dData(val binding: StorageTexture3dBinding) : BindingData {
        var storageTexture: StorageTexture3d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }
}
