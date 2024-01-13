package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

sealed class StorageTextureBinding<T: Texture?>(
    textureName: String,
    defaultTexture: T,
    val shader: ShaderBase<*>
) : PipelineBinding(textureName) {

    private var cache: T = defaultTexture

    fun get(): T {
        if (isValid) {
            shader.createdPipeline?.let {
                cache = it.bindGroupData[bindGroup].getFromData()
            }
        }
        return cache
    }

    fun set(value: T) {
        cache = value
        if (isValid) {
            shader.createdPipeline?.let {
                it.bindGroupData[bindGroup].setInData(value)
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)

        pipeline.bindGroupLayouts.find { group ->
            group.bindings.any { b -> b is StorageTextureLayout && b.name == bindingName }
        }?.let { group ->
            val storageTex = group.bindings.first { b -> b.name == bindingName } as StorageTextureLayout
            bindGroup = group.group
            bindingIndex = storageTex.bindingIndex
            pipeline.bindGroupData[bindGroup].setInData(cache)
        }
    }

    protected abstract fun BindGroupData.getFromData(): T
    protected abstract fun BindGroupData.setInData(texture: T)
}

class StorageTexture1dBinding(
    textureName: String,
    defaultTexture: StorageTexture1d?,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture1d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageTexture1d? {
        return storageTexture1dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(texture: StorageTexture1d?) {
        storageTexture1dBindingData(bindingIndex).storageTexture = texture
    }
}

class StorageTexture2dBinding(
    textureName: String,
    defaultTexture: StorageTexture2d?,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture2d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageTexture2d? {
        return storageTexture2dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(texture: StorageTexture2d?) {
        storageTexture2dBindingData(bindingIndex).storageTexture = texture
    }
}

class StorageTexture3dBinding(
    textureName: String,
    defaultTexture: StorageTexture3d?,
    shader: ShaderBase<*>
) : StorageTextureBinding<StorageTexture3d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageTexture3d? {
        return storageTexture3dBindingData(bindingIndex).storageTexture
    }

    override fun BindGroupData.setInData(texture: StorageTexture3d?) {
        storageTexture3dBindingData(bindingIndex).storageTexture = texture
    }
}
