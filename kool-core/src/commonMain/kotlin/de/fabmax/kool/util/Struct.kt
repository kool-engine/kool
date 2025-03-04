package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.pipeline.GpuType

abstract class Struct<T: Struct<T>>(val layout: BufferLayout) {
    val members = mutableListOf<StructMember>()

    private var lastPos = 0
    val structSize: Int get() = layout.structSize(lastPos)

    private var _bufferAccess: StructBufferAccess? = null
    val bufferAccess: StructBufferAccess get() = checkNotNull(_bufferAccess) {
        "Buffer access only works if StructDefinition is used in an StructBuffer context"
    }
    private val buffer: MixedBuffer get() = bufferAccess.structBuffer.buffer
    private val bufferPosition: Int get() = bufferAccess.bufferPosition

    internal fun setupBufferAccess(access: StructBufferAccess) {
        _bufferAccess = access
    }

    fun getLayoutString(indent: String = "") = buildString {
        members.forEach { appendLine(it.layoutInfo(indent)) }
    }

    fun getBufferContentString(): String {
        fun StructMember.nameAndArrayType(): String = "$name: $type[$arraySize]".padEnd(30)
        fun StructMember.nameAndType(arrayIdx: Int = 0): String {
            val name = if (arraySize == 1) "$name: " else "$name[$arrayIdx]: "
            return "$name$type".padEnd(30)
        }
        return members.joinToString("\n") { member ->
            when (member) {
                is Struct<*>.Float1Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Float2Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Float3Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Float4Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Int1Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Int2Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Int3Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Int4Member -> "${member.nameAndType()} = ${member.buf}"
                is Struct<*>.Mat2Member -> "${member.nameAndType().trim()} =\n${member.buf.toStringFormatted().prependIndent("  ")}"
                is Struct<*>.Mat3Member -> "${member.nameAndType().trim()} =\n${member.buf.toStringFormatted().prependIndent("  ")}"
                is Struct<*>.Mat4Member -> "${member.nameAndType().trim()} =\n${member.buf.toStringFormatted().prependIndent("  ")}"
                is Struct<*>.NestedStructMember<*> -> buildString {
                    append(member.nameAndType().trim()).appendLine(" {")
                    member.buf.getBufferContentString().lines().forEach { appendLine("  $it") }
                    append("}")
                }
                is Struct<*>.Float1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Float2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Float3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Float4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Int1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Int2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Int3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Int4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member.bufGet(it) }}"
                is Struct<*>.Mat2ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member.bufGet(i).toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.Mat3ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member.bufGet(i).toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.Mat4ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member.bufGet(i).toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.NestedStructArrayMember<*> -> buildString {
                    repeat(member.arraySize) { i ->
                        append(member.nameAndType(i).trim()).appendLine(" {")
                        member.buf(i).getBufferContentString().lines().forEach { appendLine("  $it") }
                        appendLine("}")
                    }
                }.trim()
            }
        }
    }


    protected fun float1(name: String = "f1_${members.size}"): Float1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, 1)
        lastPos = offset + size
        return Float1Member(name, offset).also { members.add(it) }
    }

    protected fun float2(name: String = "f2_${members.size}"): Float2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, 1)
        lastPos = offset + size
        return Float2Member(name, offset).also { members.add(it) }
    }

    protected fun float3(name: String = "f3_${members.size}"): Float3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, 1)
        lastPos = offset + size
        return Float3Member(name, offset).also { members.add(it) }
    }

    protected fun float4(name: String = "f4_${members.size}"): Float4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, 1)
        lastPos = offset + size
        return Float4Member(name, offset).also { members.add(it) }
    }


    protected fun int1(name: String = "i1_${members.size}"): Int1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, 1)
        lastPos = offset + size
        return Int1Member(name, offset).also { members.add(it) }
    }

    protected fun int2(name: String = "i2_${members.size}"): Int2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, 1)
        lastPos = offset + size
        return Int2Member(name, offset).also { members.add(it) }
    }

    protected fun int3(name: String = "i3_${members.size}"): Int3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, 1)
        lastPos = offset + size
        return Int3Member(name, offset).also { members.add(it) }
    }

    protected fun int4(name: String = "i4_${members.size}"): Int4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, 1)
        lastPos = offset + size
        return Int4Member(name, offset).also { members.add(it) }
    }


    protected fun mat2(name: String = "m2_${members.size}"): Mat2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, 1)
        lastPos = offset + size
        return Mat2Member(name, offset).also { members.add(it) }
    }

    protected fun mat3(name: String = "m3_${members.size}"): Mat3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, 1)
        lastPos = offset + size
        return Mat3Member(name, offset).also { members.add(it) }
    }

    protected fun mat4(name: String = "m4_${members.size}"): Mat4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, 1)
        lastPos = offset + size
        return Mat4Member(name, offset).also { members.add(it) }
    }


    protected fun <S: Struct<S>> struct(name: String = "nested_${members.size}", structProvider: () -> S): NestedStructMember<S> {
        val nested = structProvider()
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Struct(name, nested.structSize), 1)
        lastPos = offset + size
        return NestedStructMember<S>(name, offset, structProvider).also { members.add(it) }
    }


    protected fun float1Array(arraySize: Int, name: String = "f1arr_${members.size}"): Float1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, arraySize)
        lastPos = offset + size
        return Float1ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun float2Array(arraySize: Int, name: String = "f2arr_${members.size}"): Float2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, arraySize)
        lastPos = offset + size
        return Float2ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun float3Array(arraySize: Int, name: String = "f3arr_${members.size}"): Float3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, arraySize)
        lastPos = offset + size
        return Float3ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun float4Array(arraySize: Int, name: String = "f4arr_${members.size}"): Float4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, arraySize)
        lastPos = offset + size
        return Float4ArrayMember(name, offset, arraySize).also { members.add(it) }
    }


    protected fun int1Array(arraySize: Int, name: String = "i1arr_${members.size}"): Int1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, arraySize)
        lastPos = offset + size
        return Int1ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun int2Array(arraySize: Int, name: String = "i2arr_${members.size}"): Int2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, arraySize)
        lastPos = offset + size
        return Int2ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun int3Array(arraySize: Int, name: String = "i3arr_${members.size}"): Int3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, arraySize)
        lastPos = offset + size
        return Int3ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun int4Array(arraySize: Int, name: String = "i4arr_${members.size}"): Int4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, arraySize)
        lastPos = offset + size
        return Int4ArrayMember(name, offset, arraySize).also { members.add(it) }
    }


    protected fun mat2Array(arraySize: Int, name: String = "m2arr_${members.size}"): Mat2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, arraySize)
        lastPos = offset + size
        return Mat2ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun mat3Array(arraySize: Int, name: String = "m3arr_${members.size}"): Mat3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, arraySize)
        lastPos = offset + size
        return Mat3ArrayMember(name, offset, arraySize).also { members.add(it) }
    }

    protected fun mat4Array(arraySize: Int, name: String = "m4arr_${members.size}"): Mat4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, arraySize)
        lastPos = offset + size
        return Mat4ArrayMember(name, offset, arraySize).also { members.add(it) }
    }


    protected fun <S: Struct<S>> structArray(arraySize: Int, name: String = "nestedArr_${members.size}", structProvider: () -> S): NestedStructArrayMember<S> {
        val nested = structProvider()
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Struct(name, nested.structSize), arraySize)
        lastPos = offset + size
        return NestedStructArrayMember<S>(name, offset, arraySize, structProvider).also { members.add(it) }
    }


    sealed interface StructMember {
        val name: String
        val type: GpuType
        val arraySize: Int
        val byteOffset: Int

        fun layoutInfo(indent: String): String {
            val name = "$name:".padEnd(20)
            val typeName = if (arraySize == 1) type.toString().padEnd(16) else "$type[$arraySize]".padEnd(16)
            return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
        }
    }

    inner class Float1Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float1
        override val arraySize = 1

        var buf: Float
            get() = buffer.getFloat32(bufferPosition + byteOffset)
            set(value) { buffer.setFloat32(bufferPosition + byteOffset, value) }
    }

    inner class Float2Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float2
        override val arraySize = 1

        var buf: Vec2f
            get() = bufGet(MutableVec2f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
            }

        fun bufGet(result: MutableVec2f): MutableVec2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4)
            )
        }
    }

    inner class Float3Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float3
        override val arraySize = 1

        var buf: Vec3f
            get() = bufGet(MutableVec3f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
                buffer.setFloat32(offset +  8, value.z)
            }

        fun bufGet(result: MutableVec3f): MutableVec3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8)
            )
        }
    }

    inner class Float4Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Float4
        override val arraySize = 1

        var buf: Vec4f
            get() = bufGet(MutableVec4f())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.x)
                buffer.setFloat32(offset +  4, value.y)
                buffer.setFloat32(offset +  8, value.z)
                buffer.setFloat32(offset + 12, value.w)
            }

        fun bufGet(result: MutableVec4f): MutableVec4f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
                buffer.getFloat32(offset + 12),
            )
        }
    }

    inner class Int1Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Int1
        override val arraySize = 1

        var buf: Int
            get() = buffer.getInt32(bufferPosition + byteOffset)
            set(value) { buffer.setInt32(bufferPosition + byteOffset, value) }
    }

    inner class Int2Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Int2
        override val arraySize = 1

        var buf: Vec2i
            get() = bufGet(MutableVec2i())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setInt32(offset +  0, value.x)
                buffer.setInt32(offset +  4, value.y)
            }

        fun bufGet(result: MutableVec2i): MutableVec2i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4)
            )
        }
    }

    inner class Int3Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Int3
        override val arraySize = 1

        var buf: Vec3i
            get() = bufGet(MutableVec3i())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setInt32(offset +  0, value.x)
                buffer.setInt32(offset +  4, value.y)
                buffer.setInt32(offset +  8, value.z)
            }

        fun bufGet(result: MutableVec3i): MutableVec3i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8)
            )
        }
    }

    inner class Int4Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Int4
        override val arraySize = 1

        var buf: Vec4i
            get() = bufGet(MutableVec4i())
            set(value) {
                val offset = bufferPosition + byteOffset
                buffer.setInt32(offset +  0, value.x)
                buffer.setInt32(offset +  4, value.y)
                buffer.setInt32(offset +  8, value.z)
                buffer.setInt32(offset + 12, value.w)
            }

        fun bufGet(result: MutableVec4i): MutableVec4i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }
    }

    inner class Mat2Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Mat2
        override val arraySize = 1

        var buf: Mat2f
            get() = bufGet(MutableMat2f())
            set(value) {
                value.putTo(buffer)
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
                buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
            }

        fun bufGet(result: MutableMat2f): MutableMat2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }
    }

    inner class Mat3Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Mat3
        override val arraySize = 1

        var buf: Mat3f
            get() = bufGet(MutableMat3f())
            set(value) {
                value.putTo(buffer)
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
                buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
                buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
            }

        fun bufGet(result: MutableMat3f): MutableMat3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }
    }

    inner class Mat4Member(override val name: String, override val byteOffset: Int) : StructMember {
        override val type = GpuType.Mat4
        override val arraySize = 1

        var buf: Mat4f
            get() = bufGet(MutableMat4f())
            set(value) {
                value.putTo(buffer)
                val offset = bufferPosition + byteOffset
                buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
                buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
                buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
                buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
            }

        fun bufGet(result: MutableMat4f): MutableMat4f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }
    }

    inner class NestedStructMember<S: Struct<S>>(
        override val name: String,
        override val byteOffset: Int,
        structProvider: () -> S
    ) : StructMember {

        private var nestedBufferAccess: StructBufferAccess? = null
        internal val accessor = structProvider()

        override val type = GpuType.Struct(name, accessor.structSize)
        override val arraySize = 1

        val buf: S
            get() {
                if (nestedBufferAccess == null) {
                    val acc = StructBufferAccessNested(bufferAccess, byteOffset)
                    nestedBufferAccess = acc
                    accessor.setupBufferAccess(acc)
                }
                return accessor
            }

        init {
            require(accessor.layout == layout) {
                "Nested struct must have the same layout as the parent struct (but nested layout is ${accessor.layout} and parent layout is $layout)"
            }
        }

        override fun layoutInfo(indent: String): String {
            return super.layoutInfo(indent) + "\n" + accessor.getLayoutString("$indent    ").trimEnd()
        }
    }

    inner class Float1ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Float1
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int): Float {
            require(index >= 0 && index < arraySize)
            return buffer.getFloat32(bufferPosition + byteOffset + arrayStride * index)
        }

        fun bufSet(index: Int, value: Float) {
            require(index >= 0 && index < arraySize)
            buffer.setFloat32(bufferPosition + byteOffset + arrayStride * index, value)
        }
    }

    inner class Float2ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Float2
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec2f = MutableVec2f()): MutableVec2f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
            )
        }

        fun bufSet(index: Int, value: Vec2f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
        }
    }

    inner class Float3ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Float4
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec3f = MutableVec3f()): MutableVec3f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
            )
        }

        fun bufSet(index: Int, value: Vec3f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
        }
    }

    inner class Float4ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Float4
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec4f = MutableVec4f()): MutableVec4f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
                buffer.getFloat32(offset + 12),
            )
        }

        fun bufSet(index: Int, value: Vec4f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }
    }

    inner class Int1ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Int1
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int): Int {
            require(index >= 0 && index < arraySize)
            return buffer.getInt32(bufferPosition + byteOffset + arrayStride * index)
        }

        fun bufSet(index: Int, value: Int) {
            require(index >= 0 && index < arraySize)
            buffer.setInt32(bufferPosition + byteOffset + arrayStride * index, value)
        }
    }

    inner class Int2ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Int2
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec2i = MutableVec2i()): MutableVec2i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
            )
        }

        fun bufSet(index: Int, value: Vec2i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }
    }

    inner class Int3ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Int4
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec3i = MutableVec3i()): MutableVec3i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
            )
        }

        fun bufSet(index: Int, value: Vec3i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }
    }

    inner class Int4ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Int4
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableVec4i = MutableVec4i()): MutableVec4i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }

        fun bufSet(index: Int, value: Vec4i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }
    }

    inner class Mat2ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Mat2
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableMat2f = MutableMat2f()): MutableMat2f {
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }

        fun bufSet(index: Int, value: Mat2f) {
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }
    }

    inner class Mat3ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Mat3
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableMat3f = MutableMat3f()): MutableMat3f {
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }

        fun bufSet(index: Int, value: Mat3f) {
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }
    }

    inner class Mat4ArrayMember(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val type = GpuType.Mat4
        val arrayStride = layout.arrayStrideOf(type)

        fun bufGet(index: Int, result: MutableMat4f = MutableMat4f()): MutableMat4f {
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }

        fun bufSet(index: Int, value: Mat4f) {
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }
    }

    inner class NestedStructArrayMember<S: Struct<S>>(
        override val name: String,
        override val byteOffset: Int,
        override val arraySize: Int,
        structProvider: () -> S
    ) : StructMember {

        private var nestedBufferAccess: StructBufferAccessNested? = null
        internal val accessor = structProvider()

        override val type = GpuType.Struct(name, accessor.structSize)
        val arrayStride = layout.arrayStrideOf(type)

        init {
            require(accessor.layout == layout) {
                "Nested struct must have the same layout as the parent struct (but nested layout is ${accessor.layout} and parent layout is $layout)"
            }
        }

        fun buf(index: Int): S {
            require(index >= 0 && index < arraySize)
            if (nestedBufferAccess == null) {
                val acc = StructBufferAccessNested(bufferAccess, byteOffset)
                nestedBufferAccess = acc
                accessor.setupBufferAccess(acc)
            }
            nestedBufferAccess!!.byteOffset = byteOffset + index * arrayStride

            return accessor
        }

        override fun layoutInfo(indent: String): String {
            return super.layoutInfo(indent) + "\n" + accessor.getLayoutString("$indent    ").trimEnd()
        }
    }
}

interface StructBufferAccess {
    val structBuffer: StructBuffer<*>
    val bufferPosition: Int
}

class StructBufferAccessIndexed(override val structBuffer: StructBuffer<*>, var index: Int = 0) : StructBufferAccess {
    override val bufferPosition: Int get() = index * structBuffer.strideBytes
}

class StructBufferAccessNested(val parent: StructBufferAccess, var byteOffset: Int) : StructBufferAccess {
    override val structBuffer: StructBuffer<*> get() = parent.structBuffer
    override val bufferPosition: Int get() = parent.bufferPosition + byteOffset
}

