package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

class StorageBufferBinding(
    textureName: String,
    defaultBuffer: StorageBuffer?,
    shader: ShaderBase<*>
) : PipelineBinding(textureName, shader) {

    private var cache: StorageBuffer? = defaultBuffer

    fun get(): StorageBuffer? {
        if (isValid) {
            bindGroupData?.let {
                cache = it.getFromData()
            }
        }
        return cache
    }

    fun set(value: StorageBuffer?) {
        cache = value
        if (isValid) {
            bindGroupData?.setInData(value)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): StorageBuffer? = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: StorageBuffer?) = set(value)

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

    fun BindGroupData.getFromData(): StorageBuffer? {
        return storageBuffer1dBindingData(bindingIndex).storageBuffer
    }

    fun BindGroupData.setInData(buffer: StorageBuffer?) {
        storageBuffer1dBindingData(bindingIndex).storageBuffer = buffer
    }
}
