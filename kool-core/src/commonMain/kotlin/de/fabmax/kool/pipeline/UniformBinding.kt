package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.*
import kotlin.reflect.KProperty

sealed class UniformBinding<T, C, M: StructMember<*>>(
    uniformName: String,
    initVal: C,
    shader: ShaderBase<*>
) : PipelineBinding(uniformName, shader) {

    protected var cache: C = initVal
    private var member: M? = null

    fun get(): T {
        val uboData = getUboData()
        if (uboData != null) {
            member?.let { updateCacheFromBuffer(uboData.structBuffer, it) }
        }
        return getFromCache()
    }

    fun set(value: T) {
        setCacheTo(value)
        val uboData = getUboData()
        if (uboData != null) {
            member?.let {
                updateBufferFromCache(uboData.structBuffer, it)
                uboData.markDirty()
            }
        }
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex

            val uboData = checkNotNull(getUboData()) { "bindGroupData is null, i.e. pipeline was not yet created" }
            val memberIndex = ubo.indexOfMember(bindingName)
            @Suppress("UNCHECKED_CAST")
            member = uboData.buffer.struct.members[memberIndex] as M
            updateBufferFromCache(uboData.structBuffer, member!!)
            uboData.markDirty()
        }
    }

    private fun getUboData(): BindGroupData.UniformBufferBindingData<*>? {
        return if (bindingIndex < 0) null else bindGroupData?.uniformBufferBindingData(bindingIndex)
    }

    @Suppress("UNCHECKED_CAST")
    private val BindGroupData.UniformBufferBindingData<*>.structBuffer: StructBuffer<Struct>
        get() = buffer as StructBuffer<Struct>

    internal abstract fun setCacheTo(value: T)
    internal abstract fun getFromCache(): T
    internal abstract fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: M)
    internal abstract fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: M): Any
}

sealed class UniformArrayBinding<T, C: T, M: StructArrayMember<*>>(
    uniformName: String,
    arraySize: Int,
    shader: ShaderBase<*>,
    private val initVal: () -> C,
) : PipelineBinding(uniformName, shader) {

    protected val cache = MutableList(arraySize) { initVal() }
    val arraySize: Int get() = cache.size
    private var member: M? = null

    operator fun get(index: Int): T {
        val uboData = getUboData()
        if (uboData != null) {
            member?.let { updateCacheFromBuffer(uboData.structBuffer, it, index) }
        }
        return cache[index]
    }

    operator fun set(index: Int, value: T) {
        setCacheTo(index, value)
        val uboData = getUboData()
        if (uboData != null) {
            member?.let {
                updateBufferFromCache(uboData.structBuffer, it, index)
                uboData.markDirty()
            }
        }
    }

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformArrayBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex

            val uboData = checkNotNull(getUboData()) { "bindGroupData is null, i.e. pipeline was not yet created" }
            val memberIndex = ubo.indexOfMember(bindingName)
            @Suppress("UNCHECKED_CAST")
            member = uboData.buffer.struct.members[memberIndex] as M
            resizeCache(member!!.arraySize)
            for (i in 0 until arraySize) {
                updateBufferFromCache(uboData.structBuffer, member!!, i)
            }
            uboData.markDirty()
        }
    }

    private fun resizeCache(newSize: Int) {
        while (newSize < cache.size) {
            cache.removeAt(cache.lastIndex)
        }
        while (newSize > cache.size) {
            cache.add(initVal())
        }
    }

    private fun getUboData(): BindGroupData.UniformBufferBindingData<*>? {
        return if (bindingIndex < 0) null else bindGroupData?.uniformBufferBindingData(bindingIndex)
    }

    @Suppress("UNCHECKED_CAST")
    private val BindGroupData.UniformBufferBindingData<*>.structBuffer: StructBuffer<Struct>
        get() = buffer as StructBuffer<Struct>

    protected abstract fun setCacheTo(index: Int, value: T)
    protected abstract fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: M, index: Int)
    protected abstract fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: M, index: Int): Any
}

class UniformStructBinding<S: Struct>(
    uniformName: String,
    val struct: S,
    shader: ShaderBase<*>,
) : PipelineBinding(uniformName, shader) {

    @PublishedApi
    internal val cache = StructBuffer(struct, 1)

    inline fun set(block: MutableStructBufferView<S>.(S) -> Unit) {
        cache.set(0, block)
        val uboData = getUboData()
        if (uboData != null) {
            copy(cache, uboData.buffer)
            uboData.markDirty()
        }
    }

    inline fun get(block: StructBufferView<S>.(S) -> Unit) {
        @Suppress("UNCHECKED_CAST")
        val src = (getUboData()?.buffer as StructBuffer<S>?) ?: cache
        src.get(0, block)
    }

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.name == bindingName }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex

            val uboData = checkNotNull(getUboData()) { "bindGroupData is null, i.e. pipeline was not yet created" }
            check(uboData.buffer.struct == struct) {
                "Unexpected struct type in uboData: ${uboData.buffer.struct.name} != ${struct.name}"
            }
            copy(cache, uboData.buffer)
            uboData.markDirty()
        }
    }

    @PublishedApi
    internal fun copy(from: StructBuffer<*>, to: StructBuffer<*>) {
        to.buffer.position = 0
        to.buffer.put(from.buffer)
    }

    @PublishedApi
    internal fun getUboData(): BindGroupData.UniformBufferBindingData<*>? {
        return if (bindingIndex < 0) null else bindGroupData?.uniformBufferBindingData(bindingIndex)
    }
}

class UniformBinding1f(uniformName: String, defaultVal: Float, shader: ShaderBase<*>) :
    UniformBinding<Float, Float, Float1Member<Struct>>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Float = cache
    override fun setCacheTo(value: Float) { cache = value }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float1Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float1Member<Struct>) = buffer.get(0) { cache = get(member) }
}

class UniformBinding2f(uniformName: String, defaultVal: Vec2f, shader: ShaderBase<*>) :
    UniformBinding<Vec2f, MutableVec2f, Float2Member<Struct>>(uniformName, MutableVec2f(defaultVal), shader)
{
    override fun getFromCache(): Vec2f = cache
    override fun setCacheTo(value: Vec2f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float2Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float2Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding3f(uniformName: String, defaultVal: Vec3f, shader: ShaderBase<*>) :
    UniformBinding<Vec3f, MutableVec3f, Float3Member<Struct>>(uniformName, MutableVec3f(defaultVal), shader)
{
    override fun getFromCache(): Vec3f = cache
    override fun setCacheTo(value: Vec3f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float3Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float3Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding4f(uniformName: String, defaultVal: Vec4f, shader: ShaderBase<*>) :
    UniformBinding<Vec4f, MutableVec4f, Float4Member<Struct>>(uniformName, MutableVec4f(defaultVal), shader)
{
    override fun getFromCache(): Vec4f = cache
    override fun setCacheTo(value: Vec4f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBindingColor(uniformName: String, defaultVal: Color, shader: ShaderBase<*>) :
    UniformBinding<Color, MutableColor, Float4Member<Struct>>(uniformName, MutableColor(defaultVal), shader)
{
    override fun getFromCache(): Color = cache
    override fun setCacheTo(value: Color) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBindingQuat(uniformName: String, defaultVal: QuatF, shader: ShaderBase<*>) :
    UniformBinding<QuatF, MutableQuatF, Float4Member<Struct>>(uniformName, MutableQuatF(defaultVal), shader)
{
    override fun getFromCache(): QuatF = cache
    override fun setCacheTo(value: QuatF) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float4Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding1i(uniformName: String, defaultVal: Int, shader: ShaderBase<*>) :
    UniformBinding<Int, Int, Int1Member<Struct>>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Int = cache
    override fun setCacheTo(value: Int) { cache = value }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int1Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int1Member<Struct>) = buffer.get(0) { cache = get(member) }
}

class UniformBinding2i(uniformName: String, defaultVal: Vec2i, shader: ShaderBase<*>) :
    UniformBinding<Vec2i, MutableVec2i, Int2Member<Struct>>(uniformName, MutableVec2i(defaultVal), shader)
{
    override fun getFromCache(): Vec2i = cache
    override fun setCacheTo(value: Vec2i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int2Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int2Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding3i(uniformName: String, defaultVal: Vec3i, shader: ShaderBase<*>) :
    UniformBinding<Vec3i, MutableVec3i, Int3Member<Struct>>(uniformName, MutableVec3i(defaultVal), shader)
{
    override fun getFromCache(): Vec3i = cache
    override fun setCacheTo(value: Vec3i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int3Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int3Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding4i(uniformName: String, defaultVal: Vec4i, shader: ShaderBase<*>) :
    UniformBinding<Vec4i, MutableVec4i, Int4Member<Struct>>(uniformName, MutableVec4i(defaultVal), shader)
{
    override fun getFromCache(): Vec4i = cache
    override fun setCacheTo(value: Vec4i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int4Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int4Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat2f(uniformName: String, defaultVal: Mat2f, shader: ShaderBase<*>) :
    UniformBinding<Mat2f, MutableMat2f, Mat2Member<Struct>>(uniformName, MutableMat2f(defaultVal), shader)
{
    override fun getFromCache(): Mat2f = cache
    override fun setCacheTo(value: Mat2f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat2Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat2Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat3f(uniformName: String, defaultVal: Mat3f, shader: ShaderBase<*>) :
    UniformBinding<Mat3f, MutableMat3f, Mat3Member<Struct>>(uniformName, MutableMat3f(defaultVal), shader)
{
    override fun getFromCache(): Mat3f = cache
    override fun setCacheTo(value: Mat3f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat3Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat3Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat4f(uniformName: String, defaultVal: Mat4f, shader: ShaderBase<*>) :
    UniformBinding<Mat4f, MutableMat4f, Mat4Member<Struct>>(uniformName, MutableMat4f(defaultVal), shader)
{
    override fun getFromCache(): Mat4f = cache
    override fun setCacheTo(value: Mat4f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat4Member<Struct>) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat4Member<Struct>) = buffer.get(0) { get(member, cache) }
}

class UniformBinding1fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Float, Float, Float1ArrayMember<Struct>>(uniformName, arraySize, shader, { 0f })
{
    override fun setCacheTo(index: Int, value: Float) { cache[index] = value }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float1ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float1ArrayMember<Struct>, index: Int) = buffer.get(0) { cache[index] = get(member, index) }
}

class UniformBinding2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2f, MutableVec2f, Float2ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec2f() })
{
    override fun setCacheTo(index: Int, value: Vec2f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float2ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float2ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3f, MutableVec3f, Float3ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec3f() })
{
    override fun setCacheTo(index: Int, value: Vec3f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float3ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float3ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4f, MutableVec4f, Float4ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec4f() })
{
    override fun setCacheTo(index: Int, value: Vec4f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Float4ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Float4ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding1iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Int, Int, Int1ArrayMember<Struct>>(uniformName, arraySize, shader, { 0 })
{
    override fun setCacheTo(index: Int, value: Int) { cache[index] = value }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int1ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int1ArrayMember<Struct>, index: Int) = buffer.get(0) { cache[index] = get(member, index) }
}

class UniformBinding2iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2i, MutableVec2i, Int2ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec2i() })
{
    override fun setCacheTo(index: Int, value: Vec2i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int2ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int2ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding3iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3i, MutableVec3i, Int3ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec3i() })
{
    override fun setCacheTo(index: Int, value: Vec3i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int3ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int3ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding4iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4i, MutableVec4i, Int4ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableVec4i() })
{
    override fun setCacheTo(index: Int, value: Vec4i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Int4ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Int4ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat2f, MutableMat2f, Mat2ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableMat2f() })
{
    override fun setCacheTo(index: Int, value: Mat2f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat2ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat2ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat3f, MutableMat3f, Mat3ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableMat3f() })
{
    override fun setCacheTo(index: Int, value: Mat3f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat3ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat3ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat4f, MutableMat4f, Mat4ArrayMember<Struct>>(uniformName, arraySize, shader, { MutableMat4f() })
{
    override fun setCacheTo(index: Int, value: Mat4f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<Struct>, member: Mat4ArrayMember<Struct>, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<Struct>, member: Mat4ArrayMember<Struct>, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}