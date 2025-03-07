package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.lang.KslExpression
import de.fabmax.kool.modules.ksl.lang.KslStruct
import de.fabmax.kool.pipeline.GpuType

abstract class Struct<T: Struct<T>>(val structName: String, val layout: BufferLayout) : StructMember {
    private var _memberName: String = ""
    override val memberName: String get() = _memberName
    private var _parent: Struct<*>? = null
    override val parent: Struct<*>? get() = _parent
    private var _byteOffset = 0
    override val byteOffset: Int get() = _byteOffset

    val members = mutableListOf<StructMember>()

    private var lastPos = 0
    val structSize: Int get() = layout.structSize(lastPos)

    override val type: GpuType get() = GpuType.Struct(structName, structSize)
    override val arraySize = 1

    private var _bufferAccess: StructBufferAccess? = null
    val bufferAccess: StructBufferAccess get() = checkNotNull(_bufferAccess) {
        "Buffer access only works if Struct is used in an StructBuffer context"
    }
    private val buffer: MixedBuffer get() = bufferAccess.structBuffer.buffer
    private val bufferPosition: Int get() = bufferAccess.bufferPosition

    private var _kslAccess: KslExpression<KslStruct<*>>? = null
    internal val kslAccess: KslExpression<KslStruct<*>> get() = checkNotNull(_kslAccess) {
        "ksl access only works if Struct is used in an ksl context"
    }

    internal fun setupBufferAccess(access: StructBufferAccess) {
        _bufferAccess = access
        members.filterIsInstance<Struct<*>>().forEach {
            it.setupBufferAccess(StructBufferAccessNested(access, it.byteOffset))
        }
    }

    internal fun setupKslAccess(access: KslExpression<KslStruct<*>>) {
        _kslAccess = access
        members.filterIsInstance<Struct<*>>().forEach {
            it.setupKslAccess(access)
        }
    }

    override fun layoutInfo(indent: String): String {
        val members = buildString {
            members.forEach { appendLine(it.layoutInfo("$indent  ")) }
        }
        return super.layoutInfo(indent) + "\n" + members.trimEnd()
    }

    fun getBufferContentString(): String {
        fun StructMember.nameAndArrayType(): String = "$memberName: $type[$arraySize]".padEnd(30)
        fun StructMember.nameAndType(arrayIdx: Int = 0): String {
            val name = if (arraySize == 1) "$memberName: " else "$memberName[$arrayIdx]: "
            return "$name$type".padEnd(30)
        }
        return members.joinToString("\n") { member ->
            when (member) {
                is Struct<*>.Float1Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Float2Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Float3Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Float4Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Int1Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Int2Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Int3Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Int4Member -> "${member.nameAndType()} = ${member()}"
                is Struct<*>.Mat2Member -> "${member.nameAndType().trim()} =\n${member().toStringFormatted().prependIndent("  ")}"
                is Struct<*>.Mat3Member -> "${member.nameAndType().trim()} =\n${member().toStringFormatted().prependIndent("  ")}"
                is Struct<*>.Mat4Member -> "${member.nameAndType().trim()} =\n${member().toStringFormatted().prependIndent("  ")}"

                is Struct<*>.Float1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Float2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Float3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Float4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Int1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Int2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Int3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Int4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Struct<*>.Mat2ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.Mat3ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.Mat4ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is Struct<*>.NestedStructArrayMember<*> -> buildString {
                    repeat(member.arraySize) { i ->
                        append(member.nameAndType(i).trim()).appendLine(" {")
                        member[i].getBufferContentString().lines().forEach { appendLine("  $it") }
                        appendLine("}")
                    }
                }.trim()

                is Struct<*> -> buildString {
                    append(member.nameAndType().trim()).appendLine(" {")
                    member.getBufferContentString().lines().forEach { appendLine("  $it") }
                    append("}")
                }
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


    protected fun <S: Struct<S>> struct(struct: S, name: String = "nested_${members.size}"): S {
        require(struct.layout == layout) {
            "Nested structs must have the same layout as the parent struct, but parent ${this::class} has layout $layout and nested ${struct::class} has ${struct.layout}"
        }
        require(struct.parent == null) {
            "Given nested struct already has a parent"
        }
        struct._memberName = name
        struct._parent = this
        val (offset, size) = layout.offsetAndSizeOf(lastPos, struct.type, 1)
        struct._byteOffset = offset
        lastPos = offset + size
        members.add(struct)
        return struct
    }

    protected fun <S: Struct<S>> structArray(arraySize: Int, name: String = "nestedArr_${members.size}", structProvider: () -> S): NestedStructArrayMember<S> {
        val nested = structProvider()
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Struct(nested.memberName, nested.structSize), arraySize)
        lastPos = offset + size
        return NestedStructArrayMember<S>(name, offset, arraySize, structProvider).also { members.add(it) }
    }


    inner class Float1Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float1
        override val arraySize = 1

        operator fun invoke(): Float = buffer.getFloat32(bufferPosition + byteOffset)
        operator fun invoke(value: Float) { buffer.setFloat32(bufferPosition + byteOffset, value) }
    }

    inner class Float2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float2
        override val arraySize = 1

        operator fun invoke(): Vec2f = get(MutableVec2f())
        operator fun invoke(value: Vec2f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
        }

        fun get(result: MutableVec2f): MutableVec2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4)
            )
        }
    }

    inner class Float3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float3
        override val arraySize = 1

        operator fun invoke(): Vec3f = get(MutableVec3f())
        operator fun invoke(value: Vec3f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
        }

        fun get(result: MutableVec3f): MutableVec3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8)
            )
        }
    }

    inner class Float4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float4
        override val arraySize = 1

        operator fun invoke(): Vec4f = get(MutableVec4f())
        operator fun invoke(value: Vec4f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        fun get(result: MutableVec4f): MutableVec4f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
                buffer.getFloat32(offset + 12),
            )
        }
    }

    inner class Int1Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int1
        override val arraySize = 1

        operator fun invoke(): Int = buffer.getInt32(bufferPosition + byteOffset)
        operator fun invoke(value: Int) { buffer.setInt32(bufferPosition + byteOffset, value) }
    }

    inner class Int2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int2
        override val arraySize = 1

        operator fun invoke(): Vec2i = get(MutableVec2i())
        operator fun invoke(value: Vec2i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }

        fun get(result: MutableVec2i): MutableVec2i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4)
            )
        }
    }

    inner class Int3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int3
        override val arraySize = 1

        operator fun invoke(): Vec3i = get(MutableVec3i())
        operator fun invoke(value: Vec3i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }

        fun get(result: MutableVec3i): MutableVec3i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8)
            )
        }
    }

    inner class Int4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int4
        override val arraySize = 1

        operator fun invoke(): Vec4i = get(MutableVec4i())
        operator fun invoke(value: Vec4i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        fun get(result: MutableVec4i): MutableVec4i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }
    }

    inner class Mat2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat2
        override val arraySize = 1

        operator fun invoke(): Mat2f = get(MutableMat2f())
        operator fun invoke(value: Mat2f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }

        fun get(result: MutableMat2f): MutableMat2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }
    }

    inner class Mat3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat3
        override val arraySize = 1

        operator fun invoke(): Mat3f = get(MutableMat3f())
        operator fun invoke(value: Mat3f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }

        fun get(result: MutableMat3f): MutableMat3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }
    }

    inner class Mat4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat4
        override val arraySize = 1

        operator fun invoke(): Mat4f = get(MutableMat4f())
        operator fun invoke(value: Mat4f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }

        fun get(result: MutableMat4f): MutableMat4f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }
    }

    inner class Float1ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float1
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): Float {
            require(index >= 0 && index < arraySize)
            return buffer.getFloat32(bufferPosition + byteOffset + arrayStride * index)
        }

        operator fun set(index: Int, value: Float) {
            require(index >= 0 && index < arraySize)
            buffer.setFloat32(bufferPosition + byteOffset + arrayStride * index, value)
        }
    }

    inner class Float2ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float2
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec2f = get(index, MutableVec2f())

        fun get(index: Int, result: MutableVec2f): MutableVec2f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
            )
        }

        operator fun set(index: Int, value: Vec2f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
        }
    }

    inner class Float3ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float4
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec3f = get(index, MutableVec3f())

        fun get(index: Int, result: MutableVec3f): MutableVec3f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
            )
        }

        operator fun set(index: Int, value: Vec3f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
        }
    }

    inner class Float4ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Float4
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec4f = get(index, MutableVec4f())

        fun get(index: Int, result: MutableVec4f): MutableVec4f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8),
                buffer.getFloat32(offset + 12),
            )
        }

        operator fun set(index: Int, value: Vec4f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }
    }

    inner class Int1ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int1
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): Int {
            require(index >= 0 && index < arraySize)
            return buffer.getInt32(bufferPosition + byteOffset + arrayStride * index)
        }

        operator fun set(index: Int, value: Int) {
            require(index >= 0 && index < arraySize)
            buffer.setInt32(bufferPosition + byteOffset + arrayStride * index, value)
        }
    }

    inner class Int2ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int2
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec2i = get(index, MutableVec2i())

        fun get(index: Int, result: MutableVec2i): MutableVec2i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
            )
        }

        operator fun set(index: Int, value: Vec2i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }
    }

    inner class Int3ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int4
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec3i = get(index, MutableVec3i())

        fun get(index: Int, result: MutableVec3i): MutableVec3i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
            )
        }

        operator fun set(index: Int, value: Vec3i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }
    }

    inner class Int4ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Int4
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableVec4i = get(index, MutableVec4i())

        fun get(index: Int, result: MutableVec4i): MutableVec4i {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }

        operator fun set(index: Int, value: Vec4i) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }
    }

    inner class Mat2ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat2
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableMat2f = get(index, MutableMat2f())

        fun get(index: Int, result: MutableMat2f): MutableMat2f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }

        operator fun set(index: Int, value: Mat2f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }
    }

    inner class Mat3ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat3
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableMat3f = get(index, MutableMat3f())

        fun get(index: Int, result: MutableMat3f): MutableMat3f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }

        operator fun set(index: Int, value: Mat3f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }
    }

    inner class Mat4ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        override val type = GpuType.Mat4
        val arrayStride = layout.arrayStrideOf(type)

        operator fun get(index: Int): MutableMat4f = get(index, MutableMat4f())

        fun get(index: Int, result: MutableMat4f): MutableMat4f {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32), buffer.getFloat32(offset + 48),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36), buffer.getFloat32(offset + 52),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40), buffer.getFloat32(offset + 56),
                buffer.getFloat32(offset + 12), buffer.getFloat32(offset + 28), buffer.getFloat32(offset + 44), buffer.getFloat32(offset + 60),
            )
        }

        operator fun set(index: Int, value: Mat4f) {
            require(index >= 0 && index < arraySize)
            val offset = bufferPosition + byteOffset + arrayStride * index
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }
    }

    inner class NestedStructArrayMember<S: Struct<S>>(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
        structProvider: () -> S
    ) : StructMember {

        override val parent: Struct<T> get() = this@Struct
        private var nestedBufferAccess: StructBufferAccessNested? = null
        internal val accessor = structProvider()

        override val type = GpuType.Struct(accessor.memberName, accessor.structSize)
        val arrayStride = layout.arrayStrideOf(type)

        init {
            require(accessor.layout == layout) {
                "Nested struct must have the same layout as the parent struct (but nested layout is ${accessor.layout} and parent layout is $layout)"
            }
        }

        operator fun get(index: Int): S {
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
            return super.layoutInfo(indent) + "\n" + accessor.layoutInfo("$indent    ").trimEnd()
        }
    }
}

sealed interface StructMember {
    val parent: Struct<*>?
    val memberName: String
    val type: GpuType
    val arraySize: Int
    val byteOffset: Int

    val qualifiedName: String get() = parent?.let { "${it.qualifiedName}.$memberName" } ?: memberName

    fun layoutInfo(indent: String): String {
        val name = "$memberName:".padEnd(20)
        val typeName = if (arraySize == 1) type.toString().padEnd(16) else "$type[$arraySize]".padEnd(16)
        return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
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
