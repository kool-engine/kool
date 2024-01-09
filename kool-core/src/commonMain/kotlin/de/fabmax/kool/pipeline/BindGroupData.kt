package de.fabmax.kool.pipeline

import de.fabmax.kool.util.MixedBuffer

class BindGroupData(val layout: BindGroupLayout) {

    val uniformBuffers: Map<String, UniformBufferData> = layout.bindings
        .filterIsInstance<UniformBufferBinding>()
        .map { UniformBufferData(it) }
        .associateBy { it.binding.name }

    val textures1d: Map<String, Texture1dData> = layout.bindings
        .filterIsInstance<Texture1dBinding>()
        .map { Texture1dData(it) }
        .associateBy { it.binding.name }

    val textures2d: Map<String, Texture2dData> = layout.bindings
        .filterIsInstance<Texture2dBinding>()
        .map { Texture2dData(it) }
        .associateBy { it.binding.name }

    val textures3d: Map<String, Texture3dData> = layout.bindings
        .filterIsInstance<Texture3dBinding>()
        .map { Texture3dData(it) }
        .associateBy { it.binding.name }

    val texturesCube: Map<String, TextureCubeData> = layout.bindings
        .filterIsInstance<TextureCubeBinding>()
        .map { TextureCubeData(it) }
        .associateBy { it.binding.name }

    val storageTextures1d: Map<String, StorageTexture1dData> = layout.bindings
        .filterIsInstance<StorageTexture1dBinding>()
        .map { StorageTexture1dData(it) }
        .associateBy { it.binding.name }

    val storageTextures2d: Map<String, StorageTexture2dData> = layout.bindings
        .filterIsInstance<StorageTexture2dBinding>()
        .map { StorageTexture2dData(it) }
        .associateBy { it.binding.name }

    val storageTextures3d: Map<String, StorageTexture3dData> = layout.bindings
        .filterIsInstance<StorageTexture3dBinding>()
        .map { StorageTexture3dData(it) }
        .associateBy { it.binding.name }

    var isDirty = true

    inner class UniformBufferData(val binding: UniformBufferBinding) {
        val layout: Std140BufferLayout = Std140BufferLayout(binding.uniforms)
        val buffer: MixedBuffer = MixedBuffer(layout.size)

        var isBufferDirty = true
    }

    abstract inner class TextureData<T: Texture, B: TextureBinding>(val binding: B) {
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

    inner class Texture1dData(binding: Texture1dBinding) : TextureData<Texture1d, Texture1dBinding>(binding)
    inner class Texture2dData(binding: Texture2dBinding) : TextureData<Texture2d, Texture2dBinding>(binding)
    inner class Texture3dData(binding: Texture3dBinding) : TextureData<Texture3d, Texture3dBinding>(binding)
    inner class TextureCubeData(binding: TextureCubeBinding) : TextureData<TextureCube, TextureCubeBinding>(binding)

    inner class StorageTexture1dData(val binding: StorageTexture1dBinding) {
        var storageTexture: StorageTexture1d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture2dData(val binding: StorageTexture2dBinding) {
        var storageTexture: StorageTexture2d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }

    inner class StorageTexture3dData(val binding: StorageTexture3dBinding) {
        var storageTexture: StorageTexture3d? = null
            set(value) {
                field = value
                isDirty = true
            }
    }
}
