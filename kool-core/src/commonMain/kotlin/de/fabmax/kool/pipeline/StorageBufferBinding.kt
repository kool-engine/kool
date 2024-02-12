package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

sealed class StorageBufferBinding<T: StorageBuffer?>(
    textureName: String,
    defaultBuffer: T,
    shader: ShaderBase<*>
) : PipelineBinding(textureName, shader) {

    private var cache: T = defaultBuffer

    fun get(): T {
        if (isValid) {
            bindGroupData?.let {
                cache = it.getFromData()
            }
        }
        return cache
    }

    fun set(value: T) {
        cache = value
        if (isValid) {
            bindGroupData?.setInData(value)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)

        pipeline.findBindingLayout<StorageBufferLayout> { it.name == bindingName }?.let { (group, tex) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "StorageBufferBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but buffer $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = tex.bindingIndex
            pipeline.pipelineData.setInData(cache)
        }
    }

    protected abstract fun BindGroupData.getFromData(): T
    protected abstract fun BindGroupData.setInData(buffer: T)
}

class StorageBuffer1dBinding(
    textureName: String,
    defaultTexture: StorageBuffer1d?,
    shader: ShaderBase<*>
) : StorageBufferBinding<StorageBuffer1d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageBuffer1d? {
        return storageTexture1dBindingData(bindingIndex).storageBuffer
    }

    override fun BindGroupData.setInData(buffer: StorageBuffer1d?) {
        storageTexture1dBindingData(bindingIndex).storageBuffer = buffer
    }
}

class StorageBuffer2dBinding(
    textureName: String,
    defaultTexture: StorageBuffer2d?,
    shader: ShaderBase<*>
) : StorageBufferBinding<StorageBuffer2d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageBuffer2d? {
        return storageTexture2dBindingData(bindingIndex).storageBuffer
    }

    override fun BindGroupData.setInData(buffer: StorageBuffer2d?) {
        storageTexture2dBindingData(bindingIndex).storageBuffer = buffer
    }
}

class StorageBuffer3dBinding(
    textureName: String,
    defaultTexture: StorageBuffer3d?,
    shader: ShaderBase<*>
) : StorageBufferBinding<StorageBuffer3d?>(textureName, defaultTexture, shader) {
    override fun BindGroupData.getFromData(): StorageBuffer3d? {
        return storageTexture3dBindingData(bindingIndex).storageBuffer
    }

    override fun BindGroupData.setInData(buffer: StorageBuffer3d?) {
        storageTexture3dBindingData(bindingIndex).storageBuffer = buffer
    }
}
