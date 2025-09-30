package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.BufferUsage
import de.fabmax.kool.pipeline.GpuBuffer

class StructBuffer<T: Struct>(val capacity: Int, val struct: T) {
    @PublishedApi
    internal val defaultView: MutableView = MutableView(0)

    val strideBytes: Int = struct.structSize
    // todo: restrict visibility?
    val buffer = MixedBuffer(capacity * strideBytes)

    @PublishedApi
    internal var position = 0
    val size: Int get() = position

    init {
        require(struct.layout != MemoryLayout.DontCare) {
            "StructBuffer requires the memory layout of the struct to be other than MemoryLayout.DontCare"
        }
    }

    fun clear() {
        position = 0
        buffer.clear()
    }

    inline fun get(index: Int, block: StructBufferView.(T) -> Unit) {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        defaultView.bytePosition = index * strideBytes
        defaultView.block(struct)
    }

    inline fun set(index: Int, block: MutableStructBufferView.(T) -> Unit) {
        require(index >= 0 && index < capacity) { "Out-of-bounds index: $index, capacity: $capacity" }
        defaultView.bytePosition = index * strideBytes
        defaultView.block(struct)
    }

    inline fun put(block: MutableStructBufferView.(T) -> Unit): Int {
        check(position < capacity) { "StructBuffer capacity exceeded, capacity: $capacity" }
        val index = position++
        defaultView.bytePosition = index * strideBytes
        defaultView.block(struct)
        return index
    }

    // todo: move index into buffer view?
    fun view(index: Int): StructBufferView = MutableView(index * strideBytes)
    fun mutableView(index: Int): MutableStructBufferView = MutableView(index * strideBytes)

    @PublishedApi
    internal inner class MutableView(var bytePosition: Int) : MutableStructBufferView {
        override fun get(member: Struct.Float1Member): Float = buffer.getFloat32(bytePosition + member.byteOffset)

        override fun get(member: Struct.Float2Member, result: MutableVec2f): Vec2f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
            )
        }

        override fun get(member: Struct.Float3Member, result: MutableVec3f): Vec3f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
            )
        }

        override fun get(member: Struct.Float4Member, result: MutableVec4f): Vec4f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
                buffer.getFloat32(offset + 12),
            )
        }

        override fun get(member: Struct.Float4Member, result: MutableQuatF): QuatF {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
                buffer.getFloat32(offset + 12),
            )
        }

        override fun get(member: Struct.Float4Member, result: MutableColor): Color {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
                buffer.getFloat32(offset + 12),
            )
        }

        override fun get(member: Struct.Int1Member): Int = buffer.getInt32(bytePosition + member.byteOffset)

        override fun get(member: Struct.Int2Member, result: MutableVec2i): Vec2i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Int3Member, result: MutableVec3i): Vec3i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Int4Member, result: MutableVec4i): Vec4i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Uint1Member): UInt = buffer.getUint32(bytePosition + member.byteOffset)

        override fun get(member: Struct.Uint2Member, result: MutableVec2i): Vec2i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Uint3Member, result: MutableVec3i): Vec3i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Uint4Member, result: MutableVec4i): Vec4i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Bool1Member): Boolean = buffer.getInt32(bytePosition + member.byteOffset) != 0

        override fun get(member: Struct.Bool2Member, result: MutableVec2i): Vec2i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Bool3Member, result: MutableVec3i): Vec3i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Bool4Member, result: MutableVec4i): Vec4i {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Float1ArrayMember, index: Int): Float {
            require(index >= 0 && index < member.arraySize)
            return buffer.getFloat32(bytePosition + member.byteOffset + member.arrayStride * index)
        }

        override fun get(member: Struct.Float2ArrayMember, index: Int, result: MutableVec2f): Vec2f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
            )
        }

        override fun get(member: Struct.Float3ArrayMember, index: Int, result: MutableVec3f): Vec3f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
            )
        }

        override fun get(member: Struct.Float4ArrayMember, index: Int, result: MutableVec4f): Vec4f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset + 0),
                buffer.getFloat32(offset + 4),
                buffer.getFloat32(offset + 8),
                buffer.getFloat32(offset + 12),
            )
        }

        override fun get(member: Struct.Int1ArrayMember, index: Int): Int {
            require(index >= 0 && index < member.arraySize)
            return buffer.getInt32(bytePosition + member.byteOffset + member.arrayStride * index)
        }

        override fun get(member: Struct.Int2ArrayMember, index: Int, result: MutableVec2i): Vec2i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Int3ArrayMember, index: Int, result: MutableVec3i): Vec3i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Int4ArrayMember, index: Int, result: MutableVec4i): Vec4i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Uint1ArrayMember, index: Int): UInt {
            require(index >= 0 && index < member.arraySize)
            return buffer.getUint32(bytePosition + member.byteOffset + member.arrayStride * index)
        }

        override fun get(member: Struct.Uint2ArrayMember, index: Int, result: MutableVec2i): Vec2i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Uint3ArrayMember, index: Int, result: MutableVec3i): Vec3i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Uint4ArrayMember, index: Int, result: MutableVec4i): Vec4i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Bool1ArrayMember, index: Int): Boolean {
            require(index >= 0 && index < member.arraySize)
            return buffer.getInt32(bytePosition + member.byteOffset + member.arrayStride * index) != 0
        }

        override fun get(member: Struct.Bool2ArrayMember, index: Int, result: MutableVec2i): Vec2i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
            )
        }

        override fun get(member: Struct.Bool3ArrayMember, index: Int, result: MutableVec3i): Vec3i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
            )
        }

        override fun get(member: Struct.Bool4ArrayMember, index: Int, result: MutableVec4i): Vec4i {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getInt32(offset + 0),
                buffer.getInt32(offset + 4),
                buffer.getInt32(offset + 8),
                buffer.getInt32(offset + 12),
            )
        }

        override fun get(member: Struct.Mat2Member, result: MutableMat2f): Mat2f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }

        override fun get(member: Struct.Mat3Member, result: MutableMat3f): Mat3f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }

        override fun get(member: Struct.Mat4Member, result: MutableMat4f): Mat4f {
            val offset = bytePosition + member.byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }

        override fun get(member: Struct.Mat2ArrayMember, index: Int, result: MutableMat2f): Mat2f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }

        override fun get(member: Struct.Mat3ArrayMember, index: Int, result: MutableMat3f): Mat3f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }

        override fun get(member: Struct.Mat4ArrayMember, index: Int, result: MutableMat4f): Mat4f {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }

        override fun <S: Struct> get(member: Struct.NestedStructMember<S>, block: StructBufferView.(S) -> Unit) {
            MutableView(bytePosition + member.byteOffset).block(member.struct)
        }

        override fun <S: Struct> get(member: Struct.NestedStructArrayMember<S>, index: Int, block: StructBufferView.(S) -> Unit) {
            require(index >= 0 && index < member.arraySize)
            MutableView(bytePosition + member.byteOffset + member.arrayStride * index).block(member.struct)
        }

        override fun set(member: Struct.Float1Member, value: Float) {
            buffer.setFloat32(bytePosition + member.byteOffset, value)
        }

        override fun set(member: Struct.Float2Member, value: Vec2f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
        }

        override fun set(member: Struct.Float3Member, value: Vec3f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
            buffer.setFloat32(offset + 8, value.z)
        }

        override fun set(member: Struct.Float4Member, value: Vec4f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
            buffer.setFloat32(offset + 8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        override fun set(member: Struct.Float4Member, value: QuatF) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
            buffer.setFloat32(offset + 8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        override fun set(member: Struct.Float4Member, value: Color) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset + 0, value.r)
            buffer.setFloat32(offset + 4, value.g)
            buffer.setFloat32(offset + 8, value.b)
            buffer.setFloat32(offset + 12, value.a)
        }

        override fun set(member: Struct.Int1Member, value: Int) {
            buffer.setInt32(bytePosition + member.byteOffset, value)
        }

        override fun set(member: Struct.Int2Member, value: Vec2i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Int3Member, value: Vec3i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
        }

        override fun set(member: Struct.Int4Member, value: Vec4i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
            buffer.setInt32(offset + 12, value.y)
        }

        override fun set(member: Struct.Uint1Member, value: UInt) {
            buffer.setUint32(bytePosition + member.byteOffset, value)
        }

        override fun set(member: Struct.Uint2Member, value: Vec2i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Uint3Member, value: Vec3i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
        }

        override fun set(member: Struct.Uint4Member, value: Vec4i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
            buffer.setInt32(offset + 12, value.y)
        }

        override fun set(member: Struct.Bool1Member, value: Boolean) {
            buffer.setInt32(bytePosition + member.byteOffset, if (value) 1 else 0)
        }

        override fun set(member: Struct.Bool2Member, value: Vec2i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Bool3Member, value: Vec3i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
        }

        override fun set(member: Struct.Bool4Member, value: Vec4i) {
            val offset = bytePosition + member.byteOffset
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.y)
            buffer.setInt32(offset + 12, value.y)
        }

        override fun set(member: Struct.Float1ArrayMember, index: Int, value: Float) {
            require(index >= 0 && index < member.arraySize)
            buffer.setFloat32(bytePosition + member.byteOffset + member.arrayStride * index, value)
        }

        override fun set(member: Struct.Float2ArrayMember, index: Int, value: Vec2f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
        }

        override fun set(member: Struct.Float3ArrayMember, index: Int, value: Vec3f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
            buffer.setFloat32(offset + 8, value.z)
        }

        override fun set(member: Struct.Float4ArrayMember, index: Int, value: Vec4f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset + 0, value.x)
            buffer.setFloat32(offset + 4, value.y)
            buffer.setFloat32(offset + 8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        override fun set(member: Struct.Int1ArrayMember, index: Int, value: Int) {
            require(index >= 0 && index < member.arraySize)
            buffer.setInt32(bytePosition + member.byteOffset + member.arrayStride * index, value)
        }

        override fun set(member: Struct.Int2ArrayMember, index: Int, value: Vec2i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Int3ArrayMember, index: Int, value: Vec3i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
        }

        override fun set(member: Struct.Int4ArrayMember, index: Int, value: Vec4i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        override fun set(member: Struct.Uint1ArrayMember, index: Int, value: UInt) {
            require(index >= 0 && index < member.arraySize)
            buffer.setUint32(bytePosition + member.byteOffset + member.arrayStride * index, value)
        }

        override fun set(member: Struct.Uint2ArrayMember, index: Int, value: Vec2i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Uint3ArrayMember, index: Int, value: Vec3i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
        }

        override fun set(member: Struct.Uint4ArrayMember, index: Int, value: Vec4i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        override fun set(member: Struct.Bool1ArrayMember, index: Int, value: Boolean) {
            require(index >= 0 && index < member.arraySize)
            buffer.setInt32(bytePosition + member.byteOffset + member.arrayStride * index, if (value) 1 else 0)
        }

        override fun set(member: Struct.Bool2ArrayMember, index: Int, value: Vec2i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
        }

        override fun set(member: Struct.Bool3ArrayMember, index: Int, value: Vec3i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
        }

        override fun set(member: Struct.Bool4ArrayMember, index: Int, value: Vec4i) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setInt32(offset + 0, value.x)
            buffer.setInt32(offset + 4, value.y)
            buffer.setInt32(offset + 8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        override fun set(member: Struct.Mat2Member, value: Mat2f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }

        override fun set(member: Struct.Mat3Member, value: Mat3f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }

        override fun set(member: Struct.Mat4Member, value: Mat4f) {
            val offset = bytePosition + member.byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }

        override fun set(member: Struct.Mat2ArrayMember, index: Int, value: Mat2f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }

        override fun set(member: Struct.Mat3ArrayMember, index: Int, value: Mat3f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }

        override fun set(member: Struct.Mat4ArrayMember, index: Int, value: Mat4f) {
            require(index >= 0 && index < member.arraySize)
            val offset = bytePosition + member.byteOffset + member.arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }

        override fun <S : Struct> set(member: Struct.NestedStructMember<S>, block: MutableStructBufferView.(S) -> Unit) {
            MutableView(bytePosition + member.byteOffset).block(member.struct)
        }

        override fun <S : Struct> set(member: Struct.NestedStructArrayMember<S>, index: Int, block: MutableStructBufferView.(S) -> Unit) {
            require(index >= 0 && index < member.arraySize)
            MutableView(bytePosition + member.byteOffset + member.arrayStride * index).block(member.struct)
        }
    }
}

interface StructBufferView {
    fun get(member: Struct.Float1Member): Float
    fun get(member: Struct.Float2Member, result: MutableVec2f = MutableVec2f()): Vec2f
    fun get(member: Struct.Float3Member, result: MutableVec3f = MutableVec3f()): Vec3f
    fun get(member: Struct.Float4Member, result: MutableVec4f = MutableVec4f()): Vec4f
    fun get(member: Struct.Float4Member, result: MutableQuatF): QuatF
    fun get(member: Struct.Float4Member, result: MutableColor): Color

    fun get(member: Struct.Int1Member): Int
    fun get(member: Struct.Int2Member, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Int3Member, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Int4Member, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Uint1Member): UInt
    fun get(member: Struct.Uint2Member, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Uint3Member, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Uint4Member, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Bool1Member): Boolean
    fun get(member: Struct.Bool2Member, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Bool3Member, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Bool4Member, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Float1ArrayMember, index: Int): Float
    fun get(member: Struct.Float2ArrayMember, index: Int, result: MutableVec2f = MutableVec2f()): Vec2f
    fun get(member: Struct.Float3ArrayMember, index: Int, result: MutableVec3f = MutableVec3f()): Vec3f
    fun get(member: Struct.Float4ArrayMember, index: Int, result: MutableVec4f = MutableVec4f()): Vec4f

    fun get(member: Struct.Int1ArrayMember, index: Int): Int
    fun get(member: Struct.Int2ArrayMember, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Int3ArrayMember, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Int4ArrayMember, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Uint1ArrayMember, index: Int): UInt
    fun get(member: Struct.Uint2ArrayMember, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Uint3ArrayMember, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Uint4ArrayMember, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Bool1ArrayMember, index: Int): Boolean
    fun get(member: Struct.Bool2ArrayMember, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Struct.Bool3ArrayMember, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Struct.Bool4ArrayMember, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Struct.Mat2Member, result: MutableMat2f = MutableMat2f()): Mat2f
    fun get(member: Struct.Mat3Member, result: MutableMat3f = MutableMat3f()): Mat3f
    fun get(member: Struct.Mat4Member, result: MutableMat4f = MutableMat4f()): Mat4f

    fun get(member: Struct.Mat2ArrayMember, index: Int, result: MutableMat2f = MutableMat2f()): Mat2f
    fun get(member: Struct.Mat3ArrayMember, index: Int, result: MutableMat3f = MutableMat3f()): Mat3f
    fun get(member: Struct.Mat4ArrayMember, index: Int, result: MutableMat4f = MutableMat4f()): Mat4f

    fun <S: Struct> get(member: Struct.NestedStructMember<S>, block: StructBufferView.(S) -> Unit)
    fun <S: Struct> get(member: Struct.NestedStructArrayMember<S>, index: Int, block: StructBufferView.(S) -> Unit)
}

interface MutableStructBufferView : StructBufferView {
    fun set(member: Struct.Float1Member, value: Float)
    fun set(member: Struct.Float2Member, value: Vec2f)
    fun set(member: Struct.Float3Member, value: Vec3f)
    fun set(member: Struct.Float4Member, value: Vec4f)
    fun set(member: Struct.Float4Member, value: QuatF)
    fun set(member: Struct.Float4Member, value: Color)

    fun set(member: Struct.Int1Member, value: Int)
    fun set(member: Struct.Int2Member, value: Vec2i)
    fun set(member: Struct.Int3Member, value: Vec3i)
    fun set(member: Struct.Int4Member, value: Vec4i)

    fun set(member: Struct.Uint1Member, value: UInt)
    fun set(member: Struct.Uint2Member, value: Vec2i)
    fun set(member: Struct.Uint3Member, value: Vec3i)
    fun set(member: Struct.Uint4Member, value: Vec4i)

    fun set(member: Struct.Bool1Member, value: Boolean)
    fun set(member: Struct.Bool2Member, value: Vec2i)
    fun set(member: Struct.Bool3Member, value: Vec3i)
    fun set(member: Struct.Bool4Member, value: Vec4i)

    fun set(member: Struct.Float1ArrayMember, index: Int, value: Float)
    fun set(member: Struct.Float2ArrayMember, index: Int, value: Vec2f)
    fun set(member: Struct.Float3ArrayMember, index: Int, value: Vec3f)
    fun set(member: Struct.Float4ArrayMember, index: Int, value: Vec4f)

    fun set(member: Struct.Int1ArrayMember, index: Int, value: Int)
    fun set(member: Struct.Int2ArrayMember, index: Int, value: Vec2i)
    fun set(member: Struct.Int3ArrayMember, index: Int, value: Vec3i)
    fun set(member: Struct.Int4ArrayMember, index: Int, value: Vec4i)

    fun set(member: Struct.Uint1ArrayMember, index: Int, value: UInt)
    fun set(member: Struct.Uint2ArrayMember, index: Int, value: Vec2i)
    fun set(member: Struct.Uint3ArrayMember, index: Int, value: Vec3i)
    fun set(member: Struct.Uint4ArrayMember, index: Int, value: Vec4i)

    fun set(member: Struct.Bool1ArrayMember, index: Int, value: Boolean)
    fun set(member: Struct.Bool2ArrayMember, index: Int, value: Vec2i)
    fun set(member: Struct.Bool3ArrayMember, index: Int, value: Vec3i)
    fun set(member: Struct.Bool4ArrayMember, index: Int, value: Vec4i)

    fun set(member: Struct.Mat2Member, value: Mat2f)
    fun set(member: Struct.Mat3Member, value: Mat3f)
    fun set(member: Struct.Mat4Member, value: Mat4f)

    fun set(member: Struct.Mat2ArrayMember, index: Int, value: Mat2f)
    fun set(member: Struct.Mat3ArrayMember, index: Int, value: Mat3f)
    fun set(member: Struct.Mat4ArrayMember, index: Int, value: Mat4f)

    fun <S: Struct> set(member: Struct.NestedStructMember<S>, block: MutableStructBufferView.(S) -> Unit)
    fun <S: Struct> set(member: Struct.NestedStructArrayMember<S>, index: Int, block: MutableStructBufferView.(S) -> Unit)
}

fun StructBuffer<*>.asStorageBuffer(): GpuBuffer = asGpuBuffer(BufferUsage.makeUsage(storage = true))

fun StructBuffer<*>.asGpuBuffer(usage: BufferUsage): GpuBuffer {
    val buffer = GpuBuffer(struct.type, usage, capacity)
    buffer.uploadData(this)
    return buffer
}
