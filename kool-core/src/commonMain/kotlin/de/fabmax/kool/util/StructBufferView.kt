package de.fabmax.kool.util

import de.fabmax.kool.math.*

interface StructBufferView<S: Struct> {
    var index: Int

    fun get(member: Float1Member<S>): Float
    fun get(member: Float2Member<S>, result: MutableVec2f = MutableVec2f()): Vec2f
    fun get(member: Float3Member<S>, result: MutableVec3f = MutableVec3f()): Vec3f
    fun get(member: Float4Member<S>, result: MutableVec4f = MutableVec4f()): Vec4f
    fun get(member: Float4Member<S>, result: MutableQuatF): QuatF
    fun get(member: Float4Member<S>, result: MutableColor): Color

    fun get(member: Int1Member<S>): Int
    fun get(member: Int2Member<S>, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Int3Member<S>, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Int4Member<S>, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Uint1Member<S>): UInt
    fun get(member: Uint2Member<S>, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Uint3Member<S>, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Uint4Member<S>, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Bool1Member<S>): Boolean
    fun get(member: Bool2Member<S>, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Bool3Member<S>, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Bool4Member<S>, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Float1ArrayMember<S>, index: Int): Float
    fun get(member: Float2ArrayMember<S>, index: Int, result: MutableVec2f = MutableVec2f()): Vec2f
    fun get(member: Float3ArrayMember<S>, index: Int, result: MutableVec3f = MutableVec3f()): Vec3f
    fun get(member: Float4ArrayMember<S>, index: Int, result: MutableVec4f = MutableVec4f()): Vec4f

    fun get(member: Int1ArrayMember<S>, index: Int): Int
    fun get(member: Int2ArrayMember<S>, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Int3ArrayMember<S>, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Int4ArrayMember<S>, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Uint1ArrayMember<S>, index: Int): UInt
    fun get(member: Uint2ArrayMember<S>, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Uint3ArrayMember<S>, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Uint4ArrayMember<S>, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Bool1ArrayMember<S>, index: Int): Boolean
    fun get(member: Bool2ArrayMember<S>, index: Int, result: MutableVec2i = MutableVec2i()): Vec2i
    fun get(member: Bool3ArrayMember<S>, index: Int, result: MutableVec3i = MutableVec3i()): Vec3i
    fun get(member: Bool4ArrayMember<S>, index: Int, result: MutableVec4i = MutableVec4i()): Vec4i

    fun get(member: Mat2Member<S>, result: MutableMat2f = MutableMat2f()): Mat2f
    fun get(member: Mat3Member<S>, result: MutableMat3f = MutableMat3f()): Mat3f
    fun get(member: Mat4Member<S>, result: MutableMat4f = MutableMat4f()): Mat4f

    fun get(member: Mat2ArrayMember<S>, index: Int, result: MutableMat2f = MutableMat2f()): Mat2f
    fun get(member: Mat3ArrayMember<S>, index: Int, result: MutableMat3f = MutableMat3f()): Mat3f
    fun get(member: Mat4ArrayMember<S>, index: Int, result: MutableMat4f = MutableMat4f()): Mat4f

    fun <S: Struct, N: Struct> get(member: NestedStructMember<S, N>, block: StructBufferView<N>.(N) -> Unit)
    fun <S: Struct, N: Struct> get(member: NestedStructArrayMember<S, N>, index: Int, block: StructBufferView<N>.(N) -> Unit)
}

class MutableStructBufferView<T: Struct>(val buffer: MixedBuffer, val offset: Int, val strideBytes: Int, val size: Int) : StructBufferView<T> {
    override var index: Int = 0
        set(value) {
            require(value >= 0 && value < size) { "Out-of-bounds index: $value, capacity: $size" }
            field = value
        }

    private val bytePosition: Int
        get() = offset + index * strideBytes

    override fun get(member: Float1Member<T>): Float = buffer.getFloat32(bytePosition + member.byteOffset)

    override fun get(member: Float2Member<T>, result: MutableVec2f): Vec2f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
        )
    }

    override fun get(member: Float3Member<T>, result: MutableVec3f): Vec3f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
        )
    }

    override fun get(member: Float4Member<T>, result: MutableVec4f): Vec4f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
            buffer.getFloat32(offset + 12),
        )
    }

    override fun get(member: Float4Member<T>, result: MutableQuatF): QuatF {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
            buffer.getFloat32(offset + 12),
        )
    }

    override fun get(member: Float4Member<T>, result: MutableColor): Color {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
            buffer.getFloat32(offset + 12),
        )
    }

    override fun get(member: Int1Member<T>): Int = buffer.getInt32(bytePosition + member.byteOffset)

    override fun get(member: Int2Member<T>, result: MutableVec2i): Vec2i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Int3Member<T>, result: MutableVec3i): Vec3i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Int4Member<T>, result: MutableVec4i): Vec4i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Uint1Member<T>): UInt = buffer.getUint32(bytePosition + member.byteOffset)

    override fun get(member: Uint2Member<T>, result: MutableVec2i): Vec2i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Uint3Member<T>, result: MutableVec3i): Vec3i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Uint4Member<T>, result: MutableVec4i): Vec4i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Bool1Member<T>): Boolean = buffer.getInt32(bytePosition + member.byteOffset) != 0

    override fun get(member: Bool2Member<T>, result: MutableVec2i): Vec2i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Bool3Member<T>, result: MutableVec3i): Vec3i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Bool4Member<T>, result: MutableVec4i): Vec4i {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Float1ArrayMember<T>, index: Int): Float {
        require(index >= 0 && index < member.arraySize)
        return buffer.getFloat32(bytePosition + member.byteOffset + member.arrayStride * index)
    }

    override fun get(member: Float2ArrayMember<T>, index: Int, result: MutableVec2f): Vec2f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
        )
    }

    override fun get(member: Float3ArrayMember<T>, index: Int, result: MutableVec3f): Vec3f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
        )
    }

    override fun get(member: Float4ArrayMember<T>, index: Int, result: MutableVec4f): Vec4f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset + 0),
            buffer.getFloat32(offset + 4),
            buffer.getFloat32(offset + 8),
            buffer.getFloat32(offset + 12),
        )
    }

    override fun get(member: Int1ArrayMember<T>, index: Int): Int {
        require(index >= 0 && index < member.arraySize)
        return buffer.getInt32(bytePosition + member.byteOffset + member.arrayStride * index)
    }

    override fun get(member: Int2ArrayMember<T>, index: Int, result: MutableVec2i): Vec2i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Int3ArrayMember<T>, index: Int, result: MutableVec3i): Vec3i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Int4ArrayMember<T>, index: Int, result: MutableVec4i): Vec4i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Uint1ArrayMember<T>, index: Int): UInt {
        require(index >= 0 && index < member.arraySize)
        return buffer.getUint32(bytePosition + member.byteOffset + member.arrayStride * index)
    }

    override fun get(member: Uint2ArrayMember<T>, index: Int, result: MutableVec2i): Vec2i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Uint3ArrayMember<T>, index: Int, result: MutableVec3i): Vec3i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Uint4ArrayMember<T>, index: Int, result: MutableVec4i): Vec4i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Bool1ArrayMember<T>, index: Int): Boolean {
        require(index >= 0 && index < member.arraySize)
        return buffer.getInt32(bytePosition + member.byteOffset + member.arrayStride * index) != 0
    }

    override fun get(member: Bool2ArrayMember<T>, index: Int, result: MutableVec2i): Vec2i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
        )
    }

    override fun get(member: Bool3ArrayMember<T>, index: Int, result: MutableVec3i): Vec3i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
        )
    }

    override fun get(member: Bool4ArrayMember<T>, index: Int, result: MutableVec4i): Vec4i {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getInt32(offset + 0),
            buffer.getInt32(offset + 4),
            buffer.getInt32(offset + 8),
            buffer.getInt32(offset + 12),
        )
    }

    override fun get(member: Mat2Member<T>, result: MutableMat2f): Mat2f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
        )
    }

    override fun get(member: Mat3Member<T>, result: MutableMat3f): Mat3f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
            buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
        )
    }

    override fun get(member: Mat4Member<T>, result: MutableMat4f): Mat4f {
        val offset = bytePosition + member.byteOffset
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
            buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
            buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
        )
    }

    override fun get(member: Mat2ArrayMember<T>, index: Int, result: MutableMat2f): Mat2f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
        )
    }

    override fun get(member: Mat3ArrayMember<T>, index: Int, result: MutableMat3f): Mat3f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
            buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
        )
    }

    override fun get(member: Mat4ArrayMember<T>, index: Int, result: MutableMat4f): Mat4f {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        return result.set(
            buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
            buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
            buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
            buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
        )
    }

    override fun <S: Struct, N: Struct> get(member: NestedStructMember<S, N>, block: StructBufferView<N>.(N) -> Unit) {
        MutableStructBufferView<N>(buffer, bytePosition + member.byteOffset, 0, 1).block(member.struct)
    }

    override fun <S: Struct, N: Struct> get(member: NestedStructArrayMember<S, N>, index: Int, block: StructBufferView<N>.(N) -> Unit) {
        require(index >= 0 && index < member.arraySize)
        val view = MutableStructBufferView<N>(buffer, bytePosition + member.byteOffset, member.arrayStride, member.arraySize)
        view.index = index
        view.block(member.struct)
    }

    fun set(member: Float1Member<T>, value: Float) {
        buffer.setFloat32(bytePosition + member.byteOffset, value)
    }

    fun set(member: Float2Member<T>, value: Vec2f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
    }

    fun set(member: Float2Member<T>, x: Float, y: Float) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, x)
        buffer.setFloat32(offset + 4, y)
    }

    fun set(member: Float3Member<T>, value: Vec3f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
        buffer.setFloat32(offset + 8, value.z)
    }

    fun set(member: Float3Member<T>, x: Float, y: Float, z: Float) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, x)
        buffer.setFloat32(offset + 4, y)
        buffer.setFloat32(offset + 8, z)
    }

    fun set(member: Float4Member<T>, value: Vec4f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
        buffer.setFloat32(offset + 8, value.z)
        buffer.setFloat32(offset + 12, value.w)
    }

    fun set(member: Float4Member<T>, x: Float, y: Float, z: Float, w: Float) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, x)
        buffer.setFloat32(offset + 4, y)
        buffer.setFloat32(offset + 8, z)
        buffer.setFloat32(offset + 12, w)
    }

    fun set(member: Float4Member<T>, value: QuatF) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
        buffer.setFloat32(offset + 8, value.z)
        buffer.setFloat32(offset + 12, value.w)
    }

    fun set(member: Float4Member<T>, value: Color) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset + 0, value.r)
        buffer.setFloat32(offset + 4, value.g)
        buffer.setFloat32(offset + 8, value.b)
        buffer.setFloat32(offset + 12, value.a)
    }

    fun set(member: Int1Member<T>, value: Int) {
        buffer.setInt32(bytePosition + member.byteOffset, value)
    }

    fun set(member: Int2Member<T>, value: Vec2i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Int2Member<T>, x: Int, y: Int) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, x)
        buffer.setInt32(offset + 4, y)
    }

    fun set(member: Int3Member<T>, value: Vec3i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Int3Member<T>, x: Int, y: Int, z: Int) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, x)
        buffer.setInt32(offset + 4, y)
        buffer.setInt32(offset + 8, z)
    }

    fun set(member: Int4Member<T>, value: Vec4i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Int4Member<T>, x: Int, y: Int, z: Int, w: Int) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, x)
        buffer.setInt32(offset + 4, y)
        buffer.setInt32(offset + 8, z)
        buffer.setInt32(offset + 12, w)
    }

    fun set(member: Uint1Member<T>, value: UInt) {
        buffer.setUint32(bytePosition + member.byteOffset, value)
    }

    fun set(member: Uint2Member<T>, value: Vec2i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Uint2Member<T>, x: UInt, y: UInt) {
        val offset = bytePosition + member.byteOffset
        buffer.setUint32(offset + 0, x)
        buffer.setUint32(offset + 4, y)
    }

    fun set(member: Uint3Member<T>, value: Vec3i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Uint3Member<T>, x: UInt, y: UInt, z: UInt) {
        val offset = bytePosition + member.byteOffset
        buffer.setUint32(offset + 0, x)
        buffer.setUint32(offset + 4, y)
        buffer.setUint32(offset + 8, z)
    }

    fun set(member: Uint4Member<T>, value: Vec4i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Uint4Member<T>, x: UInt, y: UInt, z: UInt, w: UInt) {
        val offset = bytePosition + member.byteOffset
        buffer.setUint32(offset + 0, x)
        buffer.setUint32(offset + 4, y)
        buffer.setUint32(offset + 8, z)
        buffer.setUint32(offset + 12, w)
    }

    fun set(member: Bool1Member<T>, value: Boolean) {
        buffer.setInt32(bytePosition + member.byteOffset, if (value) 1 else 0)
    }

    fun set(member: Bool2Member<T>, value: Vec2i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Bool3Member<T>, value: Vec3i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Bool4Member<T>, value: Vec4i) {
        val offset = bytePosition + member.byteOffset
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Float1ArrayMember<T>, index: Int, value: Float) {
        require(index >= 0 && index < member.arraySize)
        buffer.setFloat32(bytePosition + member.byteOffset + member.arrayStride * index, value)
    }

    fun set(member: Float2ArrayMember<T>, index: Int, value: Vec2f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
    }

    fun set(member: Float3ArrayMember<T>, index: Int, value: Vec3f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
        buffer.setFloat32(offset + 8, value.z)
    }

    fun set(member: Float4ArrayMember<T>, index: Int, value: Vec4f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset + 0, value.x)
        buffer.setFloat32(offset + 4, value.y)
        buffer.setFloat32(offset + 8, value.z)
        buffer.setFloat32(offset + 12, value.w)
    }

    fun set(member: Int1ArrayMember<T>, index: Int, value: Int) {
        require(index >= 0 && index < member.arraySize)
        buffer.setInt32(bytePosition + member.byteOffset + member.arrayStride * index, value)
    }

    fun set(member: Int2ArrayMember<T>, index: Int, value: Vec2i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Int3ArrayMember<T>, index: Int, value: Vec3i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Int4ArrayMember<T>, index: Int, value: Vec4i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Uint1ArrayMember<T>, index: Int, value: UInt) {
        require(index >= 0 && index < member.arraySize)
        buffer.setUint32(bytePosition + member.byteOffset + member.arrayStride * index, value)
    }

    fun set(member: Uint2ArrayMember<T>, index: Int, value: Vec2i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Uint3ArrayMember<T>, index: Int, value: Vec3i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Uint4ArrayMember<T>, index: Int, value: Vec4i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Bool1ArrayMember<T>, index: Int, value: Boolean) {
        require(index >= 0 && index < member.arraySize)
        buffer.setInt32(bytePosition + member.byteOffset + member.arrayStride * index, if (value) 1 else 0)
    }

    fun set(member: Bool2ArrayMember<T>, index: Int, value: Vec2i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
    }

    fun set(member: Bool3ArrayMember<T>, index: Int, value: Vec3i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
    }

    fun set(member: Bool4ArrayMember<T>, index: Int, value: Vec4i) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setInt32(offset + 0, value.x)
        buffer.setInt32(offset + 4, value.y)
        buffer.setInt32(offset + 8, value.z)
        buffer.setInt32(offset + 12, value.w)
    }

    fun set(member: Mat2Member<T>, value: Mat2f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
    }

    fun set(member: Mat3Member<T>, value: Mat3f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
        buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
    }

    fun set(member: Mat4Member<T>, value: Mat4f) {
        val offset = bytePosition + member.byteOffset
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
        buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
        buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
    }

    fun set(member: Mat2ArrayMember<T>, index: Int, value: Mat2f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
    }

    fun set(member: Mat3ArrayMember<T>, index: Int, value: Mat3f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
        buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
    }

    fun set(member: Mat4ArrayMember<T>, index: Int, value: Mat4f) {
        require(index >= 0 && index < member.arraySize)
        val offset = bytePosition + member.byteOffset + member.arrayStride * index
        buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
        buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
        buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
        buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
    }

    fun <S: Struct, N: Struct> set(member: NestedStructMember<S, N>, block: MutableStructBufferView<N>.(N) -> Unit) {
        MutableStructBufferView<N>(buffer, bytePosition + member.byteOffset, 0, 1).block(member.struct)
    }

    fun <S : Struct, N: Struct> set(member: NestedStructArrayMember<S, N>, index: Int, block: MutableStructBufferView<N>.(N) -> Unit) {
        require(index >= 0 && index < member.arraySize)
        val view = MutableStructBufferView<N>(buffer, bytePosition + member.byteOffset, member.arrayStride, member.arraySize)
        view.index = index
        view.block(member.struct)
    }
}