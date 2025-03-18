package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.*
import kotlin.reflect.KProperty

sealed class UniformBinding<T, C, M: StructMember>(
    uniformName: String,
    initVal: C,
    shader: ShaderBase<*>
) : PipelineBinding(uniformName, shader) {

    var cache: C = initVal

    @PublishedApi
    internal val uboData: BindGroupData.UniformBufferBindingData<*>?
        get() = bindGroupData?.bindings?.getOrNull(bindingIndex) as BindGroupData.UniformBufferBindingData<*>?
    private var memberIndex = -1

    fun get(): T {
        getMember()?.let { updateCacheFromMember(it) }
        return getFromCache()
    }

    fun set(value: T) {
        setCacheTo(value)
        getMember()?.let { updateMemberFromCache(it) }
        uboData?.markDirty()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        memberIndex = -1

        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex
            memberIndex = ubo.indexOfMember(bindingName)
            getMember()?.let { updateMemberFromCache(it) }
            uboData?.markDirty()
        }
    }

    internal open fun getMember(): M? {
        @Suppress("UNCHECKED_CAST")
        return if (memberIndex >= 0) uboData?.buffer?.struct?.members?.get(memberIndex) as M? else null
    }

    internal abstract fun setCacheTo(value: T)
    internal abstract fun getFromCache(): T
    internal abstract fun updateMemberFromCache(member: M)
    internal abstract fun updateCacheFromMember(member: M)
}

sealed class UniformArrayBinding<T, C: T, M: StructArrayMember>(
    uniformName: String,
    arraySize: Int,
    shader: ShaderBase<*>,
    private val initVal: () -> C,
) : PipelineBinding(uniformName, shader) {

    protected val cache = MutableList(arraySize) { initVal() }
    val arraySize: Int get() = cache.size

    private val uboData: BindGroupData.UniformBufferBindingData<*>?
        get() = bindGroupData?.bindings?.get(bindingIndex) as BindGroupData.UniformBufferBindingData<*>?
    private var memberIndex = -1
    @Suppress("UNCHECKED_CAST")
    private val member: M?
        get() = if (memberIndex >= 0) uboData?.buffer?.struct?.members?.get(memberIndex) as M? else null

    operator fun get(index: Int): T {
        member?.let { updateCacheFromMember(it, index) }
        return cache[index]
    }

    operator fun set(index: Int, value: T) {
        setCacheTo(index, value)
        member?.let { updateMemberFromCache(it, index) }
        uboData?.markDirty()
    }

    private fun resizeCache(newSize: Int) {
        while (newSize < cache.size) {
            cache.removeAt(cache.lastIndex)
        }
        while (newSize > cache.size) {
            cache.add(initVal())
        }
    }

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        memberIndex = -1

        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformArrayBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex
            memberIndex = ubo.indexOfMember(bindingName)
            member?.let {
                resizeCache(it.arraySize)
                for (i in 0 until arraySize) {
                    updateMemberFromCache(it, i)
                }
                uboData?.markDirty()
            }

        }
    }

    protected abstract fun setCacheTo(index: Int, value: T)
    protected abstract fun updateMemberFromCache(member: M, index: Int)
    protected abstract fun updateCacheFromMember(member: M, index: Int)
}

class UniformStructBinding<S: Struct>(uniformName: String, shader: ShaderBase<*>, provider: () -> S) :
    UniformBinding<S, StructBuffer<S>, S>(uniformName, StructBuffer(1, provider()), shader)
{
    @PublishedApi
    override fun getMember(): S? {
        return uboData?.let {
            check(it.buffer.struct == cache.struct) {
                "Struct type mismatch for uniform struct $bindingName: ${it.buffer.struct.structName} != ${cache.struct.structName}"
            }
            @Suppress("UNCHECKED_CAST")
            it.buffer[0] as S
        }
    }

    inline fun set(block: S.() -> Unit) {
        get().apply(block)
        getMember()?.let {
            updateMemberFromCache(it)
            uboData?.markDirty()
        }
    }

    override fun getFromCache(): S = cache[0]
    override fun setCacheTo(value: S) = copyStruct(value, cache[0])
    override fun updateCacheFromMember(member: S) = copyStruct(member, cache[0])
    @PublishedApi
    override fun updateMemberFromCache(member: S) = copyStruct(cache[0], member)

    private fun copyStruct(src: Struct, dst: Struct) {
        val srcBuf = src.bufferAccess.structBuffer.buffer
        val srcPos = src.bufferAccess.bufferPosition
        val dstBuf = dst.bufferAccess.structBuffer.buffer
        val dstPos = dst.bufferAccess.bufferPosition
        for (i in 0 until src.structSize / 4) {
            dstBuf.setInt32(dstPos + i * 4, srcBuf.getInt32(srcPos + i * 4))
        }
    }

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        pipeline.findBindGroupItem<UniformBufferLayout<*>> { it.name == bindingName }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex
            getMember()?.let { updateMemberFromCache(it) }
            uboData?.markDirty()
        }
    }
}

class UniformBinding1f(uniformName: String, defaultVal: Float, shader: ShaderBase<*>) :
    UniformBinding<Float, Float, Struct.Float1Member>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Float = cache
    override fun setCacheTo(value: Float) { cache = value }
    override fun updateMemberFromCache(member: Struct.Float1Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float1Member) { cache = member.get() }
}

class UniformBinding2f(uniformName: String, defaultVal: Vec2f, shader: ShaderBase<*>) :
    UniformBinding<Vec2f, MutableVec2f, Struct.Float2Member>(uniformName, MutableVec2f(defaultVal), shader)
{
    override fun getFromCache(): Vec2f = cache
    override fun setCacheTo(value: Vec2f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Float2Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float2Member) { member.get(cache) }
}

class UniformBinding3f(uniformName: String, defaultVal: Vec3f, shader: ShaderBase<*>) :
    UniformBinding<Vec3f, MutableVec3f, Struct.Float3Member>(uniformName, MutableVec3f(defaultVal), shader)
{
    override fun getFromCache(): Vec3f = cache
    override fun setCacheTo(value: Vec3f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Float3Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float3Member) { member.get(cache) }
}

class UniformBinding4f(uniformName: String, defaultVal: Vec4f, shader: ShaderBase<*>) :
    UniformBinding<Vec4f, MutableVec4f, Struct.Float4Member>(uniformName, MutableVec4f(defaultVal), shader)
{
    override fun getFromCache(): Vec4f = cache
    override fun setCacheTo(value: Vec4f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Float4Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float4Member) { member.get(cache) }
}

class UniformBindingColor(uniformName: String, defaultVal: Color, shader: ShaderBase<*>) :
    UniformBinding<Color, MutableColor, Struct.Float4Member>(uniformName, MutableColor(defaultVal), shader)
{
    override fun getFromCache(): Color = cache
    override fun setCacheTo(value: Color) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Float4Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float4Member) { cache.set(member.get()) }
}

class UniformBindingQuat(uniformName: String, defaultVal: QuatF, shader: ShaderBase<*>) :
    UniformBinding<QuatF, MutableQuatF, Struct.Float4Member>(uniformName, MutableQuatF(defaultVal), shader)
{
    override fun getFromCache(): QuatF = cache
    override fun setCacheTo(value: QuatF) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Float4Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Float4Member) { cache.set(member.get()) }
}

class UniformBinding1i(uniformName: String, defaultVal: Int, shader: ShaderBase<*>) :
    UniformBinding<Int, Int, Struct.Int1Member>(uniformName, defaultVal, shader)
{
    override fun getFromCache(): Int = cache
    override fun setCacheTo(value: Int) { cache = value }
    override fun updateMemberFromCache(member: Struct.Int1Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Int1Member) { cache = member.get() }
}

class UniformBinding2i(uniformName: String, defaultVal: Vec2i, shader: ShaderBase<*>) :
    UniformBinding<Vec2i, MutableVec2i, Struct.Int2Member>(uniformName, MutableVec2i(defaultVal), shader)
{
    override fun getFromCache(): Vec2i = cache
    override fun setCacheTo(value: Vec2i) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Int2Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Int2Member) { member.get(cache) }
}

class UniformBinding3i(uniformName: String, defaultVal: Vec3i, shader: ShaderBase<*>) :
    UniformBinding<Vec3i, MutableVec3i, Struct.Int3Member>(uniformName, MutableVec3i(defaultVal), shader)
{
    override fun getFromCache(): Vec3i = cache
    override fun setCacheTo(value: Vec3i) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Int3Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Int3Member) { member.get(cache) }
}

class UniformBinding4i(uniformName: String, defaultVal: Vec4i, shader: ShaderBase<*>) :
    UniformBinding<Vec4i, MutableVec4i, Struct.Int4Member>(uniformName, MutableVec4i(defaultVal), shader)
{
    override fun getFromCache(): Vec4i = cache
    override fun setCacheTo(value: Vec4i) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Int4Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Int4Member) { member.get(cache) }
}

class UniformBindingMat2f(uniformName: String, defaultVal: Mat2f, shader: ShaderBase<*>) :
    UniformBinding<Mat2f, MutableMat2f, Struct.Mat2Member>(uniformName, MutableMat2f(defaultVal), shader)
{
    override fun getFromCache(): Mat2f = cache
    override fun setCacheTo(value: Mat2f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Mat2Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Mat2Member) { member.get(cache) }
}

class UniformBindingMat3f(uniformName: String, defaultVal: Mat3f, shader: ShaderBase<*>) :
    UniformBinding<Mat3f, MutableMat3f, Struct.Mat3Member>(uniformName, MutableMat3f(defaultVal), shader)
{
    override fun getFromCache(): Mat3f = cache
    override fun setCacheTo(value: Mat3f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Mat3Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Mat3Member) { member.get(cache) }
}

class UniformBindingMat4f(uniformName: String, defaultVal: Mat4f, shader: ShaderBase<*>) :
    UniformBinding<Mat4f, MutableMat4f, Struct.Mat4Member>(uniformName, MutableMat4f(defaultVal), shader)
{
    override fun getFromCache(): Mat4f = cache
    override fun setCacheTo(value: Mat4f) { cache.set(value) }
    override fun updateMemberFromCache(member: Struct.Mat4Member) { member.set(cache) }
    override fun updateCacheFromMember(member: Struct.Mat4Member) { member.get(cache) }
}

class UniformBinding1fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Float, Float, Struct.Float1ArrayMember>(uniformName, arraySize, shader, { 0f })
{
    override fun setCacheTo(index: Int, value: Float) { cache[index] = value }
    override fun updateMemberFromCache(member: Struct.Float1ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Float1ArrayMember, index: Int) { cache[index] = member[index] }
}

class UniformBinding2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2f, MutableVec2f, Struct.Float2ArrayMember>(uniformName, arraySize, shader, { MutableVec2f() })
{
    override fun setCacheTo(index: Int, value: Vec2f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Float2ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Float2ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBinding3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3f, MutableVec3f, Struct.Float3ArrayMember>(uniformName, arraySize, shader, { MutableVec3f() })
{
    override fun setCacheTo(index: Int, value: Vec3f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Float3ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Float3ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBinding4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4f, MutableVec4f, Struct.Float4ArrayMember>(uniformName, arraySize, shader, { MutableVec4f() })
{
    override fun setCacheTo(index: Int, value: Vec4f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Float4ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Float4ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBinding1iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Int, Int, Struct.Int1ArrayMember>(uniformName, arraySize, shader, { 0 })
{
    override fun setCacheTo(index: Int, value: Int) { cache[index] = value }
    override fun updateMemberFromCache(member: Struct.Int1ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Int1ArrayMember, index: Int) { cache[index] = member[index] }
}

class UniformBinding2iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2i, MutableVec2i, Struct.Int2ArrayMember>(uniformName, arraySize, shader, { MutableVec2i() })
{
    override fun setCacheTo(index: Int, value: Vec2i) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Int2ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Int2ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBinding3iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3i, MutableVec3i, Struct.Int3ArrayMember>(uniformName, arraySize, shader, { MutableVec3i() })
{
    override fun setCacheTo(index: Int, value: Vec3i) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Int3ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Int3ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBinding4iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4i, MutableVec4i, Struct.Int4ArrayMember>(uniformName, arraySize, shader, { MutableVec4i() })
{
    override fun setCacheTo(index: Int, value: Vec4i) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Int4ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Int4ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBindingMat2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat2f, MutableMat2f, Struct.Mat2ArrayMember>(uniformName, arraySize, shader, { MutableMat2f() })
{
    override fun setCacheTo(index: Int, value: Mat2f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Mat2ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Mat2ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBindingMat3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat3f, MutableMat3f, Struct.Mat3ArrayMember>(uniformName, arraySize, shader, { MutableMat3f() })
{
    override fun setCacheTo(index: Int, value: Mat3f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Mat3ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Mat3ArrayMember, index: Int) { member.get(index, cache[index]) }
}

class UniformBindingMat4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat4f, MutableMat4f, Struct.Mat4ArrayMember>(uniformName, arraySize, shader, { MutableMat4f() })
{
    override fun setCacheTo(index: Int, value: Mat4f) { cache[index].set(value) }
    override fun updateMemberFromCache(member: Struct.Mat4ArrayMember, index: Int) { member[index] = cache[index] }
    override fun updateCacheFromMember(member: Struct.Mat4ArrayMember, index: Int) { member.get(index, cache[index]) }
}