package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

sealed class StorageTextureBinding<T: StorageTexture?>(
    textureName: String,
    defaultTexture: T,
    defaultMipLevel: Int,
    shader: ShaderBase<*>
) : PipelineBinding(textureName, shader) {

    private var cache: T = defaultTexture
    private var cachedMipLevel: Int = defaultMipLevel

    fun get(): T {
        if (isValid) {
            bindGroupData?.let {
                cache = it.getFromData()
            }
        }
        return cache
    }

    fun set(value: T, mipLevel: Int = cachedMipLevel) {
        cache = value
        cachedMipLevel = mipLevel
        if (isValid) {
            bindGroupData?.setInData(value, mipLevel)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<StorageTextureLayout> { it.name == bindingName }?.let { (group, tex) ->
            bindGroup = group.group
            bindingIndex = tex.bindingIndex
            pipeline.pipelineData.setInData(cache, cachedMipLevel)
        }
    }

    protected abstract fun BindGroupData.getFromData(): T
    protected abstract fun BindGroupData.setInData(storageTexture: T, mipLevel: Int)
}

class StorageTexture1dBinding(
    textureName: String,
    defaultTexture: StorageTexture1d?,
    defaultMipLevel: Int,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture1d?>(textureName, defaultTexture, defaultMipLevel, shader) {
    override fun BindGroupData.getFromData(): StorageTexture1d? {
        return storageTexture1dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(storageTexture: StorageTexture1d?, mipLevel: Int) {
        val binding = storageTexture1dBindingData(bindingIndex)
        binding.storageTexture = storageTexture
        binding.mipLevel = mipLevel
    }
}

class StorageTexture2dBinding(
    textureName: String,
    defaultTexture: StorageTexture2d?,
    defaultMipLevel: Int,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture2d?>(textureName, defaultTexture, defaultMipLevel, shader) {
    override fun BindGroupData.getFromData(): StorageTexture2d? {
        return storageTexture2dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(storageTexture: StorageTexture2d?, mipLevel: Int) {
        val binding = storageTexture2dBindingData(bindingIndex)
        binding.storageTexture = storageTexture
        binding.mipLevel = mipLevel
    }
}

class StorageTexture3dBinding(
    textureName: String,
    defaultTexture: StorageTexture3d?,
    defaultMipLevel: Int,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture3d?>(textureName, defaultTexture, defaultMipLevel, shader) {
    override fun BindGroupData.getFromData(): StorageTexture3d? {
        return storageTexture3dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(storageTexture: StorageTexture3d?, mipLevel: Int) {
        val binding = storageTexture3dBindingData(bindingIndex)
        binding.storageTexture = storageTexture
        binding.mipLevel = mipLevel
    }
}
