package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.*
import kotlin.reflect.KProperty

sealed class UniformBinding<T, C, M: StructMember>(
    uniformName: String,
    initVal: C,
    shader: ShaderBase<*>
) : PipelineBinding(uniformName, shader) {

    protected var cache: C = initVal
    private var member: M? = null

    fun get(): T {
        val uboData = getUboData()
        if (uboData != null) {
            member?.let { updateCacheFromBuffer(uboData.buffer, it) }
        }
        return getFromCache()
    }

    fun set(value: T) {
        setCacheTo(value)
        val uboData = getUboData()
        if (uboData != null) {
            member?.let {
                updateBufferFromCache(uboData.buffer, it)
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
            updateBufferFromCache(uboData.buffer, member!!)
            uboData.markDirty()
        }
    }

    private fun getUboData(): BindGroupData.UniformBufferBindingData<*>? {
        return if (bindingIndex < 0) null else bindGroupData?.uniformBufferBindingData(bindingIndex)
    }

    internal abstract fun setCacheTo(value: T)
    internal abstract fun getFromCache(): T
    internal abstract fun updateBufferFromCache(buffer: StructBuffer<*>, member: M)
    internal abstract fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: M)
}

sealed class UniformArrayBinding<T, C: T, M: StructArrayMember>(
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
            member?.let { updateCacheFromBuffer(uboData.buffer, it, index) }
        }
        return cache[index]
    }

    operator fun set(index: Int, value: T) {
        setCacheTo(index, value)
        val uboData = getUboData()
        if (uboData != null) {
            member?.let {
                updateBufferFromCache(uboData.buffer, it, index)
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
                updateBufferFromCache(uboData.buffer, member!!, i)
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

    protected abstract fun setCacheTo(index: Int, value: T)
    protected abstract fun updateBufferFromCache(buffer: StructBuffer<*>, member: M, index: Int)
    protected abstract fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: M, index: Int)
}

class UniformStructBinding<S: Struct>(
    uniformName: String,
    val struct: S,
    shader: ShaderBase<*>,
) : PipelineBinding(uniformName, shader) {

    @PublishedApi
    internal val cache = StructBuffer(struct, 1)

    inline fun set(block: MutableStructBufferView.(S) -> Unit) {
        cache.set(0, block)
        val uboData = getUboData()
        if (uboData != null) {
            copy(cache, uboData.buffer)
            uboData.markDirty()
        }
    }

    inline fun get(block: StructBufferView.(S) -> Unit) {
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
    UniformBinding<Float, Float, Struct.Float1Member>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Float = cache
    override fun setCacheTo(value: Float) { cache = value }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float1Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float1Member) = buffer.get(0) { cache = get(member) }
}

class UniformBinding2f(uniformName: String, defaultVal: Vec2f, shader: ShaderBase<*>) :
    UniformBinding<Vec2f, MutableVec2f, Struct.Float2Member>(uniformName, MutableVec2f(defaultVal), shader)
{
    override fun getFromCache(): Vec2f = cache
    override fun setCacheTo(value: Vec2f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float2Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float2Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding3f(uniformName: String, defaultVal: Vec3f, shader: ShaderBase<*>) :
    UniformBinding<Vec3f, MutableVec3f, Struct.Float3Member>(uniformName, MutableVec3f(defaultVal), shader)
{
    override fun getFromCache(): Vec3f = cache
    override fun setCacheTo(value: Vec3f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float3Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float3Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding4f(uniformName: String, defaultVal: Vec4f, shader: ShaderBase<*>) :
    UniformBinding<Vec4f, MutableVec4f, Struct.Float4Member>(uniformName, MutableVec4f(defaultVal), shader)
{
    override fun getFromCache(): Vec4f = cache
    override fun setCacheTo(value: Vec4f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.get(0) { get(member, cache) }
}

class UniformBindingColor(uniformName: String, defaultVal: Color, shader: ShaderBase<*>) :
    UniformBinding<Color, MutableColor, Struct.Float4Member>(uniformName, MutableColor(defaultVal), shader)
{
    override fun getFromCache(): Color = cache
    override fun setCacheTo(value: Color) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.get(0) { get(member, cache) }
}

class UniformBindingQuat(uniformName: String, defaultVal: QuatF, shader: ShaderBase<*>) :
    UniformBinding<QuatF, MutableQuatF, Struct.Float4Member>(uniformName, MutableQuatF(defaultVal), shader)
{
    override fun getFromCache(): QuatF = cache
    override fun setCacheTo(value: QuatF) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float4Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding1i(uniformName: String, defaultVal: Int, shader: ShaderBase<*>) :
    UniformBinding<Int, Int, Struct.Int1Member>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Int = cache
    override fun setCacheTo(value: Int) { cache = value }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int1Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int1Member) = buffer.get(0) { cache = get(member) }
}

class UniformBinding2i(uniformName: String, defaultVal: Vec2i, shader: ShaderBase<*>) :
    UniformBinding<Vec2i, MutableVec2i, Struct.Int2Member>(uniformName, MutableVec2i(defaultVal), shader)
{
    override fun getFromCache(): Vec2i = cache
    override fun setCacheTo(value: Vec2i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int2Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int2Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding3i(uniformName: String, defaultVal: Vec3i, shader: ShaderBase<*>) :
    UniformBinding<Vec3i, MutableVec3i, Struct.Int3Member>(uniformName, MutableVec3i(defaultVal), shader)
{
    override fun getFromCache(): Vec3i = cache
    override fun setCacheTo(value: Vec3i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int3Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int3Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding4i(uniformName: String, defaultVal: Vec4i, shader: ShaderBase<*>) :
    UniformBinding<Vec4i, MutableVec4i, Struct.Int4Member>(uniformName, MutableVec4i(defaultVal), shader)
{
    override fun getFromCache(): Vec4i = cache
    override fun setCacheTo(value: Vec4i) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int4Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int4Member) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat2f(uniformName: String, defaultVal: Mat2f, shader: ShaderBase<*>) :
    UniformBinding<Mat2f, MutableMat2f, Struct.Mat2Member>(uniformName, MutableMat2f(defaultVal), shader)
{
    override fun getFromCache(): Mat2f = cache
    override fun setCacheTo(value: Mat2f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat2Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat2Member) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat3f(uniformName: String, defaultVal: Mat3f, shader: ShaderBase<*>) :
    UniformBinding<Mat3f, MutableMat3f, Struct.Mat3Member>(uniformName, MutableMat3f(defaultVal), shader)
{
    override fun getFromCache(): Mat3f = cache
    override fun setCacheTo(value: Mat3f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat3Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat3Member) = buffer.get(0) { get(member, cache) }
}

class UniformBindingMat4f(uniformName: String, defaultVal: Mat4f, shader: ShaderBase<*>) :
    UniformBinding<Mat4f, MutableMat4f, Struct.Mat4Member>(uniformName, MutableMat4f(defaultVal), shader)
{
    override fun getFromCache(): Mat4f = cache
    override fun setCacheTo(value: Mat4f) { cache.set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat4Member) = buffer.set(0) { set(member, cache) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat4Member) = buffer.get(0) { get(member, cache) }
}

class UniformBinding1fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Float, Float, Struct.Float1ArrayMember>(uniformName, arraySize, shader, { 0f })
{
    override fun setCacheTo(index: Int, value: Float) { cache[index] = value }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float1ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float1ArrayMember, index: Int) = buffer.get(0) { cache[index] = get(member, index) }
}

class UniformBinding2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2f, MutableVec2f, Struct.Float2ArrayMember>(uniformName, arraySize, shader, { MutableVec2f() })
{
    override fun setCacheTo(index: Int, value: Vec2f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float2ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float2ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3f, MutableVec3f, Struct.Float3ArrayMember>(uniformName, arraySize, shader, { MutableVec3f() })
{
    override fun setCacheTo(index: Int, value: Vec3f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float3ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float3ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4f, MutableVec4f, Struct.Float4ArrayMember>(uniformName, arraySize, shader, { MutableVec4f() })
{
    override fun setCacheTo(index: Int, value: Vec4f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Float4ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Float4ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding1iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Int, Int, Struct.Int1ArrayMember>(uniformName, arraySize, shader, { 0 })
{
    override fun setCacheTo(index: Int, value: Int) { cache[index] = value }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int1ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int1ArrayMember, index: Int) = buffer.get(0) { cache[index] = get(member, index) }
}

class UniformBinding2iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2i, MutableVec2i, Struct.Int2ArrayMember>(uniformName, arraySize, shader, { MutableVec2i() })
{
    override fun setCacheTo(index: Int, value: Vec2i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int2ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int2ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding3iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3i, MutableVec3i, Struct.Int3ArrayMember>(uniformName, arraySize, shader, { MutableVec3i() })
{
    override fun setCacheTo(index: Int, value: Vec3i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int3ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int3ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBinding4iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4i, MutableVec4i, Struct.Int4ArrayMember>(uniformName, arraySize, shader, { MutableVec4i() })
{
    override fun setCacheTo(index: Int, value: Vec4i) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Int4ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Int4ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat2f, MutableMat2f, Struct.Mat2ArrayMember>(uniformName, arraySize, shader, { MutableMat2f() })
{
    override fun setCacheTo(index: Int, value: Mat2f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat2ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat2ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat3f, MutableMat3f, Struct.Mat3ArrayMember>(uniformName, arraySize, shader, { MutableMat3f() })
{
    override fun setCacheTo(index: Int, value: Mat3f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat3ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat3ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}

class UniformBindingMat4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat4f, MutableMat4f, Struct.Mat4ArrayMember>(uniformName, arraySize, shader, { MutableMat4f() })
{
    override fun setCacheTo(index: Int, value: Mat4f) { cache[index].set(value) }
    override fun updateBufferFromCache(buffer: StructBuffer<*>, member: Struct.Mat4ArrayMember, index: Int) = buffer.set(0) { set(member, index, cache[index]) }
    override fun updateCacheFromBuffer(buffer: StructBuffer<*>, member: Struct.Mat4ArrayMember, index: Int) = buffer.get(0) { get(member, index, cache[index]) }
}