package de.fabmax.kool.util

import de.fabmax.kool.math.*
import de.fabmax.kool.modules.ksl.lang.KslExprStruct
import de.fabmax.kool.modules.ksl.lang.KslExpression
import de.fabmax.kool.modules.ksl.lang.KslStruct
import de.fabmax.kool.pipeline.GpuType

abstract class Struct(val structName: String, val layout: MemoryLayout) : StructMember {
    private var _memberName: String = ""
    override val memberName: String get() = _memberName
    private var _parent: Struct? = null
    override val parent: Struct? get() = _parent
    private var _byteOffset = 0
    override val byteOffset: Int get() = _byteOffset

    private val _members = mutableListOf<StructMember>()
    val members: List<StructMember> get() = _members

    private var lastPos = 0
    val structSize: Int get() = layout.structSize(this, lastPos)

    override val type: GpuType get() = GpuType.Struct(this)

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

    val hash: LongHash by lazy {
        LongHash {
            members.forEach { this += it.type }
        }
    }
    
    protected fun addMember(member: StructMember) {
        check(members.none { it.memberName == member.memberName }) {
            "Duplicate struct member names are not allowed: ${member.memberName}"
        }
        _members += member
    }

    internal fun setupBufferAccess(access: StructBufferAccess) {
        check(_bufferAccess == null) {
            "Buffer access is already configured! A single struct instance can only view a single buffer"
        }
        _bufferAccess = access
        members.filterIsInstance<Struct>().forEach {
            it.setupBufferAccess(StructBufferAccessNested(access, it.byteOffset))
        }
    }

    internal fun setupKslAccess(access: KslExprStruct<*>) {
        _kslAccess = access
        members.filterIsInstance<Struct>().forEach {
            it.setupKslAccess(access)
        }
    }


    protected fun float1(name: String = "f1_${members.size}"): Float1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, 1)
        lastPos = offset + size
        return Float1Member(name, offset).also { addMember(it) }
    }

    protected fun float2(name: String = "f2_${members.size}"): Float2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, 1)
        lastPos = offset + size
        return Float2Member(name, offset).also { addMember(it) }
    }

    protected fun float3(name: String = "f3_${members.size}"): Float3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, 1)
        lastPos = offset + size
        return Float3Member(name, offset).also { addMember(it) }
    }

    protected fun float4(name: String = "f4_${members.size}"): Float4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, 1)
        lastPos = offset + size
        return Float4Member(name, offset).also { addMember(it) }
    }


    protected fun int1(name: String = "i1_${members.size}"): Int1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, 1)
        lastPos = offset + size
        return Int1Member(name, offset).also { addMember(it) }
    }

    protected fun int2(name: String = "i2_${members.size}"): Int2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, 1)
        lastPos = offset + size
        return Int2Member(name, offset).also { addMember(it) }
    }

    protected fun int3(name: String = "i3_${members.size}"): Int3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, 1)
        lastPos = offset + size
        return Int3Member(name, offset).also { addMember(it) }
    }

    protected fun int4(name: String = "i4_${members.size}"): Int4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, 1)
        lastPos = offset + size
        return Int4Member(name, offset).also { addMember(it) }
    }


    protected fun uint1(name: String = "u1_${members.size}"): Uint1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint1, 1)
        lastPos = offset + size
        return Uint1Member(name, offset).also { addMember(it) }
    }

    protected fun uint2(name: String = "u2_${members.size}"): Uint2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint2, 1)
        lastPos = offset + size
        return Uint2Member(name, offset).also { addMember(it) }
    }

    protected fun uint3(name: String = "u3_${members.size}"): Uint3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint3, 1)
        lastPos = offset + size
        return Uint3Member(name, offset).also { addMember(it) }
    }

    protected fun uint4(name: String = "u4_${members.size}"): Uint4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint4, 1)
        lastPos = offset + size
        return Uint4Member(name, offset).also { addMember(it) }
    }


    protected fun bool1(name: String = "b1_${members.size}"): Bool1Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool1, 1)
        lastPos = offset + size
        return Bool1Member(name, offset).also { addMember(it) }
    }

    protected fun bool2(name: String = "b2_${members.size}"): Bool2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool2, 1)
        lastPos = offset + size
        return Bool2Member(name, offset).also { addMember(it) }
    }

    protected fun bool3(name: String = "b3_${members.size}"): Bool3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool3, 1)
        lastPos = offset + size
        return Bool3Member(name, offset).also { addMember(it) }
    }

    protected fun bool4(name: String = "b4_${members.size}"): Bool4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool4, 1)
        lastPos = offset + size
        return Bool4Member(name, offset).also { addMember(it) }
    }


    protected fun mat2(name: String = "m2_${members.size}"): Mat2Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, 1)
        lastPos = offset + size
        return Mat2Member(name, offset).also { addMember(it) }
    }

    protected fun mat3(name: String = "m3_${members.size}"): Mat3Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, 1)
        lastPos = offset + size
        return Mat3Member(name, offset).also { addMember(it) }
    }

    protected fun mat4(name: String = "m4_${members.size}"): Mat4Member {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, 1)
        lastPos = offset + size
        return Mat4Member(name, offset).also { addMember(it) }
    }


    protected fun float1Array(arraySize: Int, name: String = "f1arr_${members.size}"): Float1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, arraySize)
        lastPos = offset + size
        return Float1ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun float2Array(arraySize: Int, name: String = "f2arr_${members.size}"): Float2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, arraySize)
        lastPos = offset + size
        return Float2ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun float3Array(arraySize: Int, name: String = "f3arr_${members.size}"): Float3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, arraySize)
        lastPos = offset + size
        return Float3ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun float4Array(arraySize: Int, name: String = "f4arr_${members.size}"): Float4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, arraySize)
        lastPos = offset + size
        return Float4ArrayMember(name, offset, arraySize).also { addMember(it) }
    }


    protected fun int1Array(arraySize: Int, name: String = "i1arr_${members.size}"): Int1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, arraySize)
        lastPos = offset + size
        return Int1ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun int2Array(arraySize: Int, name: String = "i2arr_${members.size}"): Int2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, arraySize)
        lastPos = offset + size
        return Int2ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun int3Array(arraySize: Int, name: String = "i3arr_${members.size}"): Int3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, arraySize)
        lastPos = offset + size
        return Int3ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun int4Array(arraySize: Int, name: String = "i4arr_${members.size}"): Int4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, arraySize)
        lastPos = offset + size
        return Int4ArrayMember(name, offset, arraySize).also { addMember(it) }
    }


    protected fun uint1Array(arraySize: Int, name: String = "u1arr_${members.size}"): Uint1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint1, arraySize)
        lastPos = offset + size
        return Uint1ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun uint2Array(arraySize: Int, name: String = "u2arr_${members.size}"): Uint2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint2, arraySize)
        lastPos = offset + size
        return Uint2ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun uint3Array(arraySize: Int, name: String = "u3arr_${members.size}"): Uint3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint3, arraySize)
        lastPos = offset + size
        return Uint3ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun uint4Array(arraySize: Int, name: String = "u4arr_${members.size}"): Uint4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint4, arraySize)
        lastPos = offset + size
        return Uint4ArrayMember(name, offset, arraySize).also { addMember(it) }
    }


    protected fun bool1Array(arraySize: Int, name: String = "b1arr_${members.size}"): Bool1ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool1, arraySize)
        lastPos = offset + size
        return Bool1ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun bool2Array(arraySize: Int, name: String = "b2arr_${members.size}"): Bool2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool2, arraySize)
        lastPos = offset + size
        return Bool2ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun bool3Array(arraySize: Int, name: String = "b3arr_${members.size}"): Bool3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool3, arraySize)
        lastPos = offset + size
        return Bool3ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun bool4Array(arraySize: Int, name: String = "b4arr_${members.size}"): Bool4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool4, arraySize)
        lastPos = offset + size
        return Bool4ArrayMember(name, offset, arraySize).also { addMember(it) }
    }


    protected fun mat2Array(arraySize: Int, name: String = "m2arr_${members.size}"): Mat2ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, arraySize)
        lastPos = offset + size
        return Mat2ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun mat3Array(arraySize: Int, name: String = "m3arr_${members.size}"): Mat3ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, arraySize)
        lastPos = offset + size
        return Mat3ArrayMember(name, offset, arraySize).also { addMember(it) }
    }

    protected fun mat4Array(arraySize: Int, name: String = "m4arr_${members.size}"): Mat4ArrayMember {
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, arraySize)
        lastPos = offset + size
        return Mat4ArrayMember(name, offset, arraySize).also { addMember(it) }
    }


    protected fun <S: Struct> struct(struct: S, name: String = "nested_${members.size}"): S {
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
        addMember(struct)
        return struct
    }

    protected fun <S: Struct> structArray(arraySize: Int, name: String = "nestedArr_${members.size}", structProvider: () -> S): NestedStructArrayMember<S> {
        val nested = structProvider()
        require(nested.layout == layout) {
            "Nested structs must have the same layout as the parent struct, but parent ${this::class} has layout $layout and nested ${nested::class} has ${nested.layout}"
        }
        val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Struct(nested), arraySize)
        lastPos = offset + size
        return NestedStructArrayMember<S>(name, offset, arraySize, structProvider).also { addMember(it) }
    }


    override fun layoutInfo(indent: String): String {
        val members = buildString {
            members.forEach { appendLine(it.layoutInfo("$indent  ")) }
        }
        return super.layoutInfo(indent) + "\n" + members.trimEnd()
    }

    fun getBufferContentString(): String {
        fun StructArrayMember.nameAndArrayType(): String = "$memberName: $type[$arraySize]".padEnd(30)
        fun StructMember.nameAndType(arrayIdx: Int = 0): String {
            val name = if (this is StructArrayMember) "$memberName[$arrayIdx]: " else "$memberName: "
            return "$name$type".padEnd(30)
        }
        return members.joinToString("\n") { member ->
            when (member) {
                is Float1Member -> "${member.nameAndType()} = ${member.get()}"
                is Float2Member -> "${member.nameAndType()} = ${member.get()}"
                is Float3Member -> "${member.nameAndType()} = ${member.get()}"
                is Float4Member -> "${member.nameAndType()} = ${member.get()}"

                is Int1Member -> "${member.nameAndType()} = ${member.get()}"
                is Int2Member -> "${member.nameAndType()} = ${member.get()}"
                is Int3Member -> "${member.nameAndType()} = ${member.get()}"
                is Int4Member -> "${member.nameAndType()} = ${member.get()}"

                is Uint1Member -> "${member.nameAndType()} = ${member.get()}"
                is Uint2Member -> "${member.nameAndType()} = ${member.get()}"
                is Uint3Member -> "${member.nameAndType()} = ${member.get()}"
                is Uint4Member -> "${member.nameAndType()} = ${member.get()}"

                is Bool1Member -> "${member.nameAndType()} = ${member.get()}"
                is Bool2Member -> "${member.nameAndType()} = ${member.get()}"
                is Bool3Member -> "${member.nameAndType()} = ${member.get()}"
                is Bool4Member -> "${member.nameAndType()} = ${member.get()}"

                is Mat2Member -> "${member.nameAndType().trim()} =\n${member.get().toStringFormatted().prependIndent("  ")}"
                is Mat3Member -> "${member.nameAndType().trim()} =\n${member.get().toStringFormatted().prependIndent("  ")}"
                is Mat4Member -> "${member.nameAndType().trim()} =\n${member.get().toStringFormatted().prependIndent("  ")}"

                is Float1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Float2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Float3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Float4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"

                is Int1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Int2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Int3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Int4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"

                is Uint1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Uint2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Uint3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Uint4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"

                is Bool1ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Bool2ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Bool3ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"
                is Bool4ArrayMember -> "${member.nameAndArrayType()} = ${(0..<member.arraySize).map { member[it] }}"

                is Mat2ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is Mat3ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is Mat4ArrayMember ->
                    (0..<member.arraySize).joinToString("\n") { i -> "${member.nameAndType(i).trim()} =\n${member[i].toStringFormatted().prependIndent("  ")}" }
                is NestedStructArrayMember<*> -> buildString {
                    repeat(member.arraySize) { i ->
                        append(member.nameAndType(i).trim()).appendLine(" {")
                        member[i].getBufferContentString().lines().forEach { appendLine("  $it") }
                        appendLine("}")
                    }
                }.trim()

                is Struct -> buildString {
                    append(member.nameAndType().trim()).appendLine(" {")
                    member.getBufferContentString().lines().forEach { appendLine("  $it") }
                    append("}")
                }
            }
        }
    }


    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || other !is Struct) return false

        if (layout != other.layout) return false
        if (members.size != other.members.size) return false
        for (i in 0 until members.size) {
            if (members[i].type != other.members[i].type) {
                return false
            }
        }
        return true
    }

    override fun hashCode(): Int {
        var result = layout.hashCode()
        members.forEach {
            result = 31 * result + it.type.hashCode()
        }
        return result
    }

    inner class Float1Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float1

        fun set(value: Float) { buffer.setFloat32(bufferPosition + byteOffset, value) }
        fun get(): Float = buffer.getFloat32(bufferPosition + byteOffset)
    }

    inner class Float2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float2

        fun set(value: Vec2f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
        }

        fun get(result: MutableVec2f = MutableVec2f()): MutableVec2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4)
            )
        }
    }

    inner class Float3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float3

        fun set(value: Vec3f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
        }

        fun get(result: MutableVec3f = MutableVec3f()): MutableVec3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0),
                buffer.getFloat32(offset +  4),
                buffer.getFloat32(offset +  8)
            )
        }
    }

    inner class Float4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float4

        fun set(value: Vec4f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        fun set(value: QuatF) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.x)
            buffer.setFloat32(offset +  4, value.y)
            buffer.setFloat32(offset +  8, value.z)
            buffer.setFloat32(offset + 12, value.w)
        }

        fun set(value: Color) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.r)
            buffer.setFloat32(offset +  4, value.g)
            buffer.setFloat32(offset +  8, value.b)
            buffer.setFloat32(offset + 12, value.a)
        }

        fun get(result: MutableVec4f = MutableVec4f()): MutableVec4f {
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
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int1

        fun set(value: Int) { buffer.setInt32(bufferPosition + byteOffset, value) }
        fun get(): Int = buffer.getInt32(bufferPosition + byteOffset)
    }

    inner class Int2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int2

        fun set(value: Vec2i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }

        fun get(result: MutableVec2i = MutableVec2i()): MutableVec2i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4)
            )
        }
    }

    inner class Int3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int3

        fun set(value: Vec3i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }

        fun get(result: MutableVec3i = MutableVec3i()): MutableVec3i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8)
            )
        }
    }

    inner class Int4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int4

        fun set(value: Vec4i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        fun get(result: MutableVec4i = MutableVec4i()): MutableVec4i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }
    }

    inner class Uint1Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint1

        fun set(value: UInt) { buffer.setUint32(bufferPosition + byteOffset, value) }
        fun get(): UInt = buffer.getUint32(bufferPosition + byteOffset)
    }

    inner class Uint2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint2

        fun set(value: Vec2i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }

        fun get(result: MutableVec2i = MutableVec2i()): MutableVec2i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4)
            )
        }
    }

    inner class Uint3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint3

        fun set(value: Vec3i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }

        fun get(result: MutableVec3i = MutableVec3i()): MutableVec3i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8)
            )
        }
    }

    inner class Uint4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint4

        fun set(value: Vec4i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        fun get(result: MutableVec4i = MutableVec4i()): MutableVec4i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8),
                buffer.getInt32(offset + 12),
            )
        }
    }

    inner class Bool1Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool1

        fun set(value: Boolean) { buffer.setInt32(bufferPosition + byteOffset, if (value) 1 else 0) }
        fun get(): Boolean = buffer.getInt32(bufferPosition + byteOffset) != 0
    }

    inner class Bool2Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool2

        fun set(value: Vec2i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
        }

        fun get(result: MutableVec2i = MutableVec2i()): MutableVec2i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4)
            )
        }
    }

    inner class Bool3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool3

        fun set(value: Vec3i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
        }

        fun get(result: MutableVec3i = MutableVec3i()): MutableVec3i {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getInt32(offset +  0),
                buffer.getInt32(offset +  4),
                buffer.getInt32(offset +  8)
            )
        }
    }

    inner class Bool4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool4

        fun set(value: Vec4i) {
            val offset = bufferPosition + byteOffset
            buffer.setInt32(offset +  0, value.x)
            buffer.setInt32(offset +  4, value.y)
            buffer.setInt32(offset +  8, value.z)
            buffer.setInt32(offset + 12, value.w)
        }

        fun get(result: MutableVec4i = MutableVec4i()): MutableVec4i {
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
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat2

        fun set(value: Mat2f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11)
        }

        fun get(result: MutableMat2f = MutableMat2f()): MutableMat2f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20)
            )
        }
    }

    inner class Mat3Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat3

        fun set(value: Mat3f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22)
        }

        fun get(result: MutableMat3f = MutableMat3f()): MutableMat3f {
            val offset = bufferPosition + byteOffset
            return result.set(
                buffer.getFloat32(offset +  0), buffer.getFloat32(offset + 16), buffer.getFloat32(offset + 32),
                buffer.getFloat32(offset +  4), buffer.getFloat32(offset + 20), buffer.getFloat32(offset + 36),
                buffer.getFloat32(offset +  8), buffer.getFloat32(offset + 24), buffer.getFloat32(offset + 40),
            )
        }
    }

    inner class Mat4Member(override val memberName: String, override val byteOffset: Int) : StructMember {
        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat4

        fun set(value: Mat4f) {
            val offset = bufferPosition + byteOffset
            buffer.setFloat32(offset +  0, value.m00); buffer.setFloat32(offset +  4, value.m10); buffer.setFloat32(offset +  8, value.m20); buffer.setFloat32(offset + 12, value.m30)
            buffer.setFloat32(offset + 16, value.m01); buffer.setFloat32(offset + 20, value.m11); buffer.setFloat32(offset + 24, value.m21); buffer.setFloat32(offset + 28, value.m31)
            buffer.setFloat32(offset + 32, value.m02); buffer.setFloat32(offset + 36, value.m12); buffer.setFloat32(offset + 40, value.m22); buffer.setFloat32(offset + 44, value.m32)
            buffer.setFloat32(offset + 48, value.m03); buffer.setFloat32(offset + 52, value.m13); buffer.setFloat32(offset + 56, value.m23); buffer.setFloat32(offset + 60, value.m33)
        }

        fun get(result: MutableMat4f = MutableMat4f()): MutableMat4f {
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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float1

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float2

        operator fun get(index: Int): Vec2f = get(index, MutableVec2f())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float3

        operator fun get(index: Int): Vec3f = get(index, MutableVec3f())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Float4

        operator fun get(index: Int): Vec4f = get(index, MutableVec4f())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int1

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int2

        operator fun get(index: Int): Vec2i = get(index, MutableVec2i())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int3

        operator fun get(index: Int): Vec3i = get(index, MutableVec3i())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Int4

        operator fun get(index: Int): Vec4i = get(index, MutableVec4i())

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

    inner class Uint1ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint1

        operator fun get(index: Int): UInt {
            require(index >= 0 && index < arraySize)
            return buffer.getUint32(bufferPosition + byteOffset + arrayStride * index)
        }

        operator fun set(index: Int, value: UInt) {
            require(index >= 0 && index < arraySize)
            buffer.setUint32(bufferPosition + byteOffset + arrayStride * index, value)
        }
    }

    inner class Uint2ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint2

        operator fun get(index: Int): Vec2i = get(index, MutableVec2i())

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

    inner class Uint3ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint3

        operator fun get(index: Int): Vec3i = get(index, MutableVec3i())

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

    inner class Uint4ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint4

        operator fun get(index: Int): Vec4i = get(index, MutableVec4i())

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

    inner class Bool1ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Uint1

        operator fun get(index: Int): Boolean {
            require(index >= 0 && index < arraySize)
            return buffer.getInt32(bufferPosition + byteOffset + arrayStride * index) != 0
        }

        operator fun set(index: Int, value: Boolean) {
            require(index >= 0 && index < arraySize)
            buffer.setInt32(bufferPosition + byteOffset + arrayStride * index, if (value) 1 else 0)
        }
    }

    inner class Bool2ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool2

        operator fun get(index: Int): Vec2i = get(index, MutableVec2i())

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

    inner class Bool3ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool3

        operator fun get(index: Int): Vec3i = get(index, MutableVec3i())

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

    inner class Bool4ArrayMember(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Bool4

        operator fun get(index: Int): Vec4i = get(index, MutableVec4i())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat2

        operator fun get(index: Int): Mat2f = get(index, MutableMat2f())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat3

        operator fun get(index: Int): Mat3f = get(index, MutableMat3f())

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
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        override val type = GpuType.Mat4

        operator fun get(index: Int): Mat4f = get(index, MutableMat4f())

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

    inner class NestedStructArrayMember<S: Struct>(
        override val memberName: String,
        override val byteOffset: Int,
        override val arraySize: Int,
        val structProvider: () -> S
    ) : StructArrayMember {

        override val parent: Struct get() = this@Struct
        private var nestedBufferAccess: StructBufferAccessNested? = null
        internal val struct = structProvider()

        override val type = GpuType.Struct(struct)

        init {
            require(struct.layout == layout) {
                "Nested struct must have the same layout as the parent struct (but nested layout is ${struct.layout} and parent layout is $layout)"
            }
        }

        operator fun get(index: Int): S {
            require(index >= 0 && index < arraySize)
            if (nestedBufferAccess == null) {
                val acc = StructBufferAccessNested(bufferAccess, byteOffset)
                nestedBufferAccess = acc
                struct.setupBufferAccess(acc)
            }
            nestedBufferAccess!!.byteOffset = byteOffset + index * arrayStride

            return struct
        }

        override fun layoutInfo(indent: String): String {
            return super.layoutInfo(indent) + "\n" + struct.layoutInfo("$indent    ").trimEnd()
        }
    }
}

sealed interface StructMember {
    val parent: Struct?
    val memberName: String
    val type: GpuType
    val byteOffset: Int

    val qualifiedName: String get() = parent?.let { "${it.qualifiedName}.$memberName" } ?: memberName

    fun layoutInfo(indent: String = ""): String {
        val name = "$memberName:".padEnd(20)
        val typeName = type.toString().padEnd(16)
        return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
    }
}

sealed interface StructArrayMember : StructMember {
    val arraySize: Int
    val arrayStride: Int get() {
        val layout = requireNotNull(parent ?: this as Struct).layout
        return layout.arrayStrideOf(type)
    }

    override fun layoutInfo(indent: String): String {
        val name = "$memberName:".padEnd(20)
        val typeName = "$type[$arraySize]".padEnd(16)
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

fun DynamicStruct(name: String, layout: MemoryLayout, block: DynamicStruct.Builder.() -> Unit): DynamicStruct {
    return DynamicStruct.Builder(name, layout).apply(block).build()
}

class DynamicStruct private constructor(builder: Builder) : Struct(builder.name, builder.layout) {
    init {
        builder.members.forEach {
            when (it.type) {
                GpuType.Float1 if (it.isArray) -> float1Array(it.arraySize, it.name)
                GpuType.Float2 if (it.isArray) -> float2Array(it.arraySize, it.name)
                GpuType.Float3 if (it.isArray) -> float3Array(it.arraySize, it.name)
                GpuType.Float4 if (it.isArray) -> float4Array(it.arraySize, it.name)

                GpuType.Int1 if (it.isArray) -> int1Array(it.arraySize, it.name)
                GpuType.Int2 if (it.isArray) -> int2Array(it.arraySize, it.name)
                GpuType.Int3 if (it.isArray) -> int3Array(it.arraySize, it.name)
                GpuType.Int4 if (it.isArray) -> int4Array(it.arraySize, it.name)

//                GpuType.Uint1 if (it.isArray) -> uint1Array(it.arraySize, it.name)
//                GpuType.Uint2 if (it.isArray) -> uint2Array(it.arraySize, it.name)
//                GpuType.Uint3 if (it.isArray) -> uint3Array(it.arraySize, it.name)
//                GpuType.Uint4 if (it.isArray) -> uint4Array(it.arraySize, it.name)

                GpuType.Mat2 if (it.isArray) -> mat2Array(it.arraySize, it.name)
                GpuType.Mat3 if (it.isArray) -> mat3Array(it.arraySize, it.name)
                GpuType.Mat4 if (it.isArray) -> mat4Array(it.arraySize, it.name)

                GpuType.Float1 -> float1(it.name)
                GpuType.Float2 -> float2(it.name)
                GpuType.Float3 -> float3(it.name)
                GpuType.Float4 -> float4(it.name)

                GpuType.Int1 -> int1(it.name)
                GpuType.Int2 -> int2(it.name)
                GpuType.Int3 -> int3(it.name)
                GpuType.Int4 -> int4(it.name)

                GpuType.Uint1 -> int1(it.name)
                GpuType.Uint2 -> int2(it.name)
                GpuType.Uint3 -> int3(it.name)
                GpuType.Uint4 -> int4(it.name)

                GpuType.Bool1 -> int1(it.name)
                GpuType.Bool2 -> int2(it.name)
                GpuType.Bool3 -> int3(it.name)
                GpuType.Bool4 -> int4(it.name)

                GpuType.Mat2 -> mat2(it.name)
                GpuType.Mat3 -> mat3(it.name)
                GpuType.Mat4 -> mat4(it.name)

                is GpuType.Struct -> error("DynamicStruct does not support nested structs")
            }
        }
    }

    class Builder(val name: String, val layout: MemoryLayout) {
        internal val members = mutableListOf<MemberBuildInfo>()

        private fun addMember(member: MemberBuildInfo): Builder {
            members.add(member)
            return this
        }

        fun float1(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Float1))
        fun float2(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Float2))
        fun float3(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Float3))
        fun float4(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Float4))

        fun int1(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Int1))
        fun int2(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Int2))
        fun int3(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Int3))
        fun int4(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Int4))

        fun mat2(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Mat2))
        fun mat3(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Mat3))
        fun mat4(name: String): Builder = addMember(MemberBuildInfo(name, GpuType.Mat4))

        fun float1Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Float1, true, arraySize))
        fun float2Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Float2, true, arraySize))
        fun float3Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Float3, true, arraySize))
        fun float4Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Float4, true, arraySize))

        fun int1Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Int1, true, arraySize))
        fun int2Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Int2, true, arraySize))
        fun int3Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Int3, true, arraySize))
        fun int4Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Int4, true, arraySize))

        fun mat2Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Mat2, true, arraySize))
        fun mat3Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Mat3, true, arraySize))
        fun mat4Array(name: String, arraySize: Int) = addMember(MemberBuildInfo(name, GpuType.Mat4, true, arraySize))

        fun build() = DynamicStruct(this)
    }
    
    internal data class MemberBuildInfo(val name: String, val type: GpuType, val isArray: Boolean = false, val arraySize: Int = 1)
}
