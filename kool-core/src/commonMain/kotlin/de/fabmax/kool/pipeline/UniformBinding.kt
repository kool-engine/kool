package de.fabmax.kool.pipeline

import de.fabmax.kool.math.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.MixedBuffer
import de.fabmax.kool.util.MutableColor
import kotlin.reflect.KProperty

sealed class UniformBinding<T, C: T>(
    uniformName: String,
    initVal: C,
    shader: ShaderBase<*>
) : PipelineBinding(uniformName, shader) {

    private var bufferPos: BufferPosition? = null
    protected var cache: C = initVal

    fun get(): T {
        bufferPos?.let {
            getPositionedBuffer(false)?.updateCache(it)
        }
        return cache
    }

    fun set(value: T) {
        setCacheTo(value)
        getPositionedBuffer(true)?.updateBuffer()
    }

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = get()
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    override fun setup(pipeline: PipelineBase) {
        super.setup(pipeline)
        bufferPos = null

        pipeline.findBindingLayout<UniformBufferLayout> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex
            bufferPos = ubo.layout.uniformPositions[bindingName]
            getPositionedBuffer(true)?.updateBuffer()
        }
    }

    private fun getPositionedBuffer(setDirty: Boolean): MixedBuffer? {
        val pos = bufferPos ?: return null
        val groupData = bindGroupData ?: return null
        val uboData = groupData.bindings[bindingIndex] as BindGroupData.UniformBufferBindingData
        uboData.buffer.position = pos.byteIndex
        if (setDirty) {
            uboData.markDirty()
        }
        return uboData.buffer
    }

    protected abstract fun setCacheTo(value: T)
    protected abstract fun MixedBuffer.updateBuffer()
    protected abstract fun MixedBuffer.updateCache(bufferPos: BufferPosition)
}

sealed class UniformArrayBinding<T, C: T>(
    uniformName: String,
    arraySize: Int,
    shader: ShaderBase<*>,
    private val initVal: () -> C,
) : PipelineBinding(uniformName, shader) {

    private var bufferPos: BufferPosition? = null

    protected val cache = MutableList(arraySize) { initVal() }
    val arraySize: Int get() = cache.size

    operator fun get(index: Int): T {
        bufferPos?.let {
            getPositionedBuffer(index, false)?.updateCache(index, it)
        }
        return cache[index]
    }

    operator fun set(index: Int, value: T) {
        setCacheTo(index, value)
        getPositionedBuffer(index, true)?.updateBuffer(index)
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
        bufferPos = null

        pipeline.findBindingLayout<UniformBufferLayout> { it.hasUniform(bindingName) }?.let { (group, ubo) ->
            check(group.scope == BindGroupScope.PIPELINE) {
                "UniformArrayBinding only supports binding to BindGroupData of scope ${BindGroupScope.PIPELINE}, but uniform $bindingName has scope ${group.scope}."
            }
            val uniform = ubo.uniforms.first { it.name == bindingName }
            resizeCache(uniform.arraySize)
            bindGroup = group.group
            bindingIndex = ubo.bindingIndex
            bufferPos = ubo.layout.uniformPositions[bindingName]
            for (i in 0 until arraySize) {
                getPositionedBuffer(i, true)?.updateBuffer(i)
            }
        }
    }

    private fun getPositionedBuffer(index: Int, setDirty: Boolean): MixedBuffer? {
        val pos = bufferPos ?: return null
        val groupData = bindGroupData ?: return null
        val uboData = groupData.bindings[bindingIndex] as BindGroupData.UniformBufferBindingData
        uboData.buffer.position = pos.byteIndex + pos.arrayStrideBytes * index
        if (setDirty) {
            uboData.markDirty()
        }
        return uboData.buffer
    }

    protected abstract fun setCacheTo(index: Int, value: T)
    protected abstract fun MixedBuffer.updateBuffer(index: Int)
    protected abstract fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition)
}

class UniformBinding1f(uniformName: String, defaultVal: Float, shader: ShaderBase<*>) :
    UniformBinding<Float, Float>(uniformName, defaultVal, shader)
{
    override fun setCacheTo(value: Float) { cache = value }
    override fun MixedBuffer.updateBuffer() { putFloat32(cache) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) { cache = getFloat32(bufferPos.byteIndex) }
}

class UniformBinding2f(uniformName: String, defaultVal: Vec2f, shader: ShaderBase<*>) :
    UniformBinding<Vec2f, MutableVec2f>(uniformName, MutableVec2f(defaultVal), shader)
{
    override fun setCacheTo(value: Vec2f) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getFloat32(bufferPos.byteIndex), getFloat32(bufferPos.byteIndex + 4))
    }
}

class UniformBinding3f(uniformName: String, defaultVal: Vec3f, shader: ShaderBase<*>) :
    UniformBinding<Vec3f, MutableVec3f>(uniformName, MutableVec3f(defaultVal), shader)
{
    override fun setCacheTo(value: Vec3f) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getFloat32(bufferPos.byteIndex), getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 8))
    }
}

class UniformBinding4f(uniformName: String, defaultVal: Vec4f, shader: ShaderBase<*>) :
    UniformBinding<Vec4f, MutableVec4f>(uniformName, MutableVec4f(defaultVal), shader)
{
    override fun setCacheTo(value: Vec4f) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getFloat32(bufferPos.byteIndex), getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 8), getFloat32(bufferPos.byteIndex + 12))
    }
}

class UniformBindingColor(uniformName: String, defaultVal: Color, shader: ShaderBase<*>) :
    UniformBinding<Color, MutableColor>(uniformName, MutableColor(defaultVal), shader)
{
    override fun setCacheTo(value: Color) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getFloat32(bufferPos.byteIndex), getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 8), getFloat32(bufferPos.byteIndex + 12))
    }
}

class UniformBindingQuat(uniformName: String, defaultVal: QuatF, shader: ShaderBase<*>) :
    UniformBinding<QuatF, MutableQuatF>(uniformName, MutableQuatF(defaultVal), shader)
{
    override fun setCacheTo(value: QuatF) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getFloat32(bufferPos.byteIndex), getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 8), getFloat32(bufferPos.byteIndex + 12))
    }
}

class UniformBinding1i(uniformName: String, defaultVal: Int, shader: ShaderBase<*>) :
    UniformBinding<Int, Int>(uniformName, defaultVal, shader)
{
    override fun setCacheTo(value: Int) { cache = value }
    override fun MixedBuffer.updateBuffer() { putInt32(cache) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) { cache = getInt32(bufferPos.byteIndex) }
}

class UniformBinding2i(uniformName: String, defaultVal: Vec2i, shader: ShaderBase<*>) :
    UniformBinding<Vec2i, MutableVec2i>(uniformName, MutableVec2i(defaultVal), shader)
{
    override fun setCacheTo(value: Vec2i) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getInt32(bufferPos.byteIndex), getInt32(bufferPos.byteIndex + 4))
    }
}

class UniformBinding3i(uniformName: String, defaultVal: Vec3i, shader: ShaderBase<*>) :
    UniformBinding<Vec3i, MutableVec3i>(uniformName, MutableVec3i(defaultVal), shader)
{
    override fun setCacheTo(value: Vec3i) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getInt32(bufferPos.byteIndex), getInt32(bufferPos.byteIndex + 4), getInt32(bufferPos.byteIndex + 8))
    }
}

class UniformBinding4i(uniformName: String, defaultVal: Vec4i, shader: ShaderBase<*>) :
    UniformBinding<Vec4i, MutableVec4i>(uniformName, MutableVec4i(defaultVal), shader)
{
    override fun setCacheTo(value: Vec4i) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(getInt32(bufferPos.byteIndex), getInt32(bufferPos.byteIndex + 4), getInt32(bufferPos.byteIndex + 8), getInt32(bufferPos.byteIndex + 12))
    }
}

class UniformBindingMat3f(uniformName: String, defaultVal: Mat3f, shader: ShaderBase<*>) :
    UniformBinding<Mat3f, MutableMat3f>(uniformName, MutableMat3f(defaultVal), shader)
{
    override fun setCacheTo(value: Mat3f) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(
            getFloat32(bufferPos.byteIndex + 0), getFloat32(bufferPos.byteIndex + 16), getFloat32(bufferPos.byteIndex + 32),
            getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 20), getFloat32(bufferPos.byteIndex + 36),
            getFloat32(bufferPos.byteIndex + 8), getFloat32(bufferPos.byteIndex + 24), getFloat32(bufferPos.byteIndex + 40),
        )
    }
}

class UniformBindingMat4f(uniformName: String, defaultVal: Mat4f, shader: ShaderBase<*>) :
    UniformBinding<Mat4f, MutableMat4f>(uniformName, MutableMat4f(defaultVal), shader)
{
    override fun setCacheTo(value: Mat4f) { cache.set(value) }
    override fun MixedBuffer.updateBuffer() { cache.putTo(this) }
    override fun MixedBuffer.updateCache(bufferPos: BufferPosition) {
        cache.set(
            getFloat32(bufferPos.byteIndex + 0), getFloat32(bufferPos.byteIndex + 16), getFloat32(bufferPos.byteIndex + 32), getFloat32(bufferPos.byteIndex + 48),
            getFloat32(bufferPos.byteIndex + 4), getFloat32(bufferPos.byteIndex + 20), getFloat32(bufferPos.byteIndex + 36), getFloat32(bufferPos.byteIndex + 52),
            getFloat32(bufferPos.byteIndex + 8), getFloat32(bufferPos.byteIndex + 24), getFloat32(bufferPos.byteIndex + 40), getFloat32(bufferPos.byteIndex + 56),
            getFloat32(bufferPos.byteIndex + 12), getFloat32(bufferPos.byteIndex + 28), getFloat32(bufferPos.byteIndex + 44), getFloat32(bufferPos.byteIndex + 60),
        )
    }
}

class UniformBinding1fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Float, Float>(uniformName, arraySize, shader, { 0f })
{
    override fun setCacheTo(index: Int, value: Float) { cache[index] = value }
    override fun MixedBuffer.updateBuffer(index: Int) { putFloat32(cache[index]) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        cache[index] = getFloat32(bufferPos.byteIndex + bufferPos.arrayStrideBytes * index)
    }
}

class UniformBinding2fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2f, MutableVec2f>(uniformName, arraySize, shader, { MutableVec2f() })
{
    override fun setCacheTo(index: Int, value: Vec2f) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getFloat32(off), getFloat32(off + 4))
    }
}

class UniformBinding3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3f, MutableVec3f>(uniformName, arraySize, shader, { MutableVec3f() })
{
    override fun setCacheTo(index: Int, value: Vec3f) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getFloat32(off), getFloat32(off + 4), getFloat32(off + 8))
    }
}

class UniformBinding4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4f, MutableVec4f>(uniformName, arraySize, shader, { MutableVec4f() })
{
    override fun setCacheTo(index: Int, value: Vec4f) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getFloat32(off), getFloat32(off + 4), getFloat32(off + 8), getFloat32(off + 12))
    }
}

class UniformBinding1iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Int, Int>(uniformName, arraySize, shader, { 0 })
{
    override fun setCacheTo(index: Int, value: Int) { cache[index] = value }
    override fun MixedBuffer.updateBuffer(index: Int) { putInt32(cache[index]) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        cache[index] = getInt32(bufferPos.byteIndex + bufferPos.arrayStrideBytes * index)
    }
}

class UniformBinding2iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec2i, MutableVec2i>(uniformName, arraySize, shader, { MutableVec2i() })
{
    override fun setCacheTo(index: Int, value: Vec2i) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getInt32(off), getInt32(off + 4))
    }
}

class UniformBinding3iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec3i, MutableVec3i>(uniformName, arraySize, shader, { MutableVec3i() })
{
    override fun setCacheTo(index: Int, value: Vec3i) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getInt32(off), getInt32(off + 4), getInt32(off + 8))
    }
}

class UniformBinding4iv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Vec4i, MutableVec4i>(uniformName, arraySize, shader, { MutableVec4i() })
{
    override fun setCacheTo(index: Int, value: Vec4i) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(getInt32(off), getInt32(off + 4), getInt32(off + 8), getInt32(off + 12))
    }
}

class UniformBindingMat3fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat3f, MutableMat3f>(uniformName, arraySize, shader, { MutableMat3f() })
{
    override fun setCacheTo(index: Int, value: Mat3f) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(
            getFloat32(off + 0), getFloat32(off + 16), getFloat32(off + 32),
            getFloat32(off + 4), getFloat32(off + 20), getFloat32(off + 36),
            getFloat32(off + 8), getFloat32(off + 24), getFloat32(off + 40),
        )
    }
}

class UniformBindingMat4fv(uniformName: String, arraySize: Int, shader: ShaderBase<*>) :
    UniformArrayBinding<Mat4f, MutableMat4f>(uniformName, arraySize, shader, { MutableMat4f() })
{
    override fun setCacheTo(index: Int, value: Mat4f) { cache[index].set(value) }
    override fun MixedBuffer.updateBuffer(index: Int) { cache[index].putTo(this) }
    override fun MixedBuffer.updateCache(index: Int, bufferPos: BufferPosition) {
        val off = bufferPos.byteIndex + index * bufferPos.arrayStrideBytes
        cache[index].set(
            getFloat32(off + 0), getFloat32(off + 16), getFloat32(off + 32), getFloat32(off + 48),
            getFloat32(off + 4), getFloat32(off + 20), getFloat32(off + 36), getFloat32(off + 52),
            getFloat32(off + 8), getFloat32(off + 24), getFloat32(off + 40), getFloat32(off + 56),
            getFloat32(off + 12), getFloat32(off + 28), getFloat32(off + 44), getFloat32(off + 60),
        )
    }
}