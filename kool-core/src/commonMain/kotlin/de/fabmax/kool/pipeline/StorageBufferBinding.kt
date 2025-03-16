package de.fabmax.kool.pipeline

import kotlin.reflect.KProperty

class StorageBufferBinding(
    textureName: String,
    defaultBuffer: GpuBuffer?,
    shader: ShaderBase<*>
) : PipelineBinding(textureName, shader) {

    private var cache: GpuBuffer? = defaultBuffer

    init {
        defaultBuffer?.checkIsStorageBuffer()
    }

    fun get(): GpuBuffer? {
        if (isValid) {
            bindGroupData?.let {
                cache = it.getFromData()
            }
        }
        return cache
    }

    fun set(value: GpuBuffer?) {
        value?.checkIsStorageBuffer()
        cache = value
        if (isValid) {
            bindGroupData?.setInData(value)
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): GpuBuffer? = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: GpuBuffer?) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)

        pipeline.findBindGroupItem<StorageBufferLayout> { it.name == bindingName }?.let { (group, tex) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "StorageBufferBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but buffer $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = tex.bindingIndex
            pipeline.pipelineData.setInData(cache)
        }
    }

    fun BindGroupData.getFromData(): GpuBuffer? {
        return storageBuffer1dBindingData(bindingIndex).storageBuffer
    }

    fun BindGroupData.setInData(buffer: GpuBuffer?) {
        storageBuffer1dBindingData(bindingIndex).storageBuffer = buffer
    }
}
