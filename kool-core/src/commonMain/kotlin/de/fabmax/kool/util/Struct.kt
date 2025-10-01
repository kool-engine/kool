package de.fabmax.kool.util

import de.fabmax.kool.pipeline.GpuType
import kotlin.jvm.JvmStatic

abstract class Struct(val name: String, val layout: MemoryLayout) {
    private val _members = mutableListOf<StructMember<*>>()
    val members: List<StructMember<*>> get() = _members

    private var lastPos = 0
    val structSize: Int get() = layout.structSize(this, lastPos)

    val type: GpuType get() = GpuType.Struct(this)

    val hash: LongHash by lazy {
        LongHash {
            this += layout.hashCode()
            members.forEach { this += it.hashCode() }
        }
    }
    
    private fun addMember(member: StructMember<*>) {
        check(members.none { it.name == member.name }) {
            "Duplicate struct member names are not allowed: ${member.name}"
        }
        _members += member
    }

    companion object {
        @JvmStatic
        protected fun <T: Struct> T.float1(name: String = "f1_${members.size}"): Float1Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, 1)
            lastPos = offset + size
            return Float1Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float2(name: String = "f2_${members.size}"): Float2Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, 1)
            lastPos = offset + size
            return Float2Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float3(name: String = "f3_${members.size}"): Float3Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, 1)
            lastPos = offset + size
            return Float3Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float4(name: String = "f4_${members.size}"): Float4Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, 1)
            lastPos = offset + size
            return Float4Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int1(name: String = "i1_${members.size}"): Int1Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, 1)
            lastPos = offset + size
            return Int1Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int2(name: String = "i2_${members.size}"): Int2Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, 1)
            lastPos = offset + size
            return Int2Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int3(name: String = "i3_${members.size}"): Int3Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, 1)
            lastPos = offset + size
            return Int3Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int4(name: String = "i4_${members.size}"): Int4Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, 1)
            lastPos = offset + size
            return Int4Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint1(name: String = "u1_${members.size}"): Uint1Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint1, 1)
            lastPos = offset + size
            return Uint1Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint2(name: String = "u2_${members.size}"): Uint2Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint2, 1)
            lastPos = offset + size
            return Uint2Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint3(name: String = "u3_${members.size}"): Uint3Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint3, 1)
            lastPos = offset + size
            return Uint3Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint4(name: String = "u4_${members.size}"): Uint4Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint4, 1)
            lastPos = offset + size
            return Uint4Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool1(name: String = "b1_${members.size}"): Bool1Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool1, 1)
            lastPos = offset + size
            return Bool1Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool2(name: String = "b2_${members.size}"): Bool2Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool2, 1)
            lastPos = offset + size
            return Bool2Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool3(name: String = "b3_${members.size}"): Bool3Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool3, 1)
            lastPos = offset + size
            return Bool3Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool4(name: String = "b4_${members.size}"): Bool4Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool4, 1)
            lastPos = offset + size
            return Bool4Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat2(name: String = "m2_${members.size}"): Mat2Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, 1)
            lastPos = offset + size
            return Mat2Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat3(name: String = "m3_${members.size}"): Mat3Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, 1)
            lastPos = offset + size
            return Mat3Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat4(name: String = "m4_${members.size}"): Mat4Member<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, 1)
            lastPos = offset + size
            return Mat4Member(name, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float1Array(arraySize: Int, name: String = "f1arr_${members.size}"): Float1ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float1, arraySize)
            lastPos = offset + size
            return Float1ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float2Array(arraySize: Int, name: String = "f2arr_${members.size}"): Float2ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float2, arraySize)
            lastPos = offset + size
            return Float2ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float3Array(arraySize: Int, name: String = "f3arr_${members.size}"): Float3ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float3, arraySize)
            lastPos = offset + size
            return Float3ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.float4Array(arraySize: Int, name: String = "f4arr_${members.size}"): Float4ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Float4, arraySize)
            lastPos = offset + size
            return Float4ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int1Array(arraySize: Int, name: String = "i1arr_${members.size}"): Int1ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int1, arraySize)
            lastPos = offset + size
            return Int1ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int2Array(arraySize: Int, name: String = "i2arr_${members.size}"): Int2ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int2, arraySize)
            lastPos = offset + size
            return Int2ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int3Array(arraySize: Int, name: String = "i3arr_${members.size}"): Int3ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int3, arraySize)
            lastPos = offset + size
            return Int3ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.int4Array(arraySize: Int, name: String = "i4arr_${members.size}"): Int4ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Int4, arraySize)
            lastPos = offset + size
            return Int4ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint1Array(arraySize: Int, name: String = "u1arr_${members.size}"): Uint1ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint1, arraySize)
            lastPos = offset + size
            return Uint1ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint2Array(arraySize: Int, name: String = "u2arr_${members.size}"): Uint2ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint2, arraySize)
            lastPos = offset + size
            return Uint2ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint3Array(arraySize: Int, name: String = "u3arr_${members.size}"): Uint3ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint3, arraySize)
            lastPos = offset + size
            return Uint3ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.uint4Array(arraySize: Int, name: String = "u4arr_${members.size}"): Uint4ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Uint4, arraySize)
            lastPos = offset + size
            return Uint4ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool1Array(arraySize: Int, name: String = "b1arr_${members.size}"): Bool1ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool1, arraySize)
            lastPos = offset + size
            return Bool1ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool2Array(arraySize: Int, name: String = "b2arr_${members.size}"): Bool2ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool2, arraySize)
            lastPos = offset + size
            return Bool2ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool3Array(arraySize: Int, name: String = "b3arr_${members.size}"): Bool3ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool3, arraySize)
            lastPos = offset + size
            return Bool3ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.bool4Array(arraySize: Int, name: String = "b4arr_${members.size}"): Bool4ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Bool4, arraySize)
            lastPos = offset + size
            return Bool4ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat2Array(arraySize: Int, name: String = "m2arr_${members.size}"): Mat2ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat2, arraySize)
            lastPos = offset + size
            return Mat2ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat3Array(arraySize: Int, name: String = "m3arr_${members.size}"): Mat3ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat3, arraySize)
            lastPos = offset + size
            return Mat3ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct> T.mat4Array(arraySize: Int, name: String = "m4arr_${members.size}"): Mat4ArrayMember<T> {
            val (offset, size) = layout.offsetAndSizeOf(lastPos, GpuType.Mat4, arraySize)
            lastPos = offset + size
            return Mat4ArrayMember(name, arraySize, offset, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct, S: Struct> T.struct(struct: S, name: String = "nested_${struct.name}_${members.size}"): NestedStructMember<T, S> {
            require(struct.layout == layout) {
                "Nested structs must have the same layout as the parent struct, but parent ${this::class} has layout $layout and nested ${struct::class} has ${struct.layout}"
            }
            val (offset, size) = layout.offsetAndSizeOf(lastPos, struct.type, 1)
            lastPos = offset + size
            return NestedStructMember(name, offset, struct, this).also { addMember(it) }
        }

        @JvmStatic
        protected fun <T: Struct, S: Struct> T.structArray(struct: S, arraySize: Int, name: String = "nestedArr_${members.size}"): NestedStructArrayMember<T, S> {
            require(struct.layout == layout) {
                "Nested structs must have the same layout as the parent struct, but parent ${this::class} has layout $layout and nested ${struct::class} has ${struct.layout}"
            }
            val (offset, size) = layout.offsetAndSizeOf(lastPos, struct.type, arraySize)
            lastPos = offset + size
            return NestedStructArrayMember(name, arraySize, offset, struct, this).also { addMember(it) }
        }
    }

    fun layoutInfo(indent: String): String {
        val members = buildString {
            members.forEach { appendLine(it.layoutInfo("$indent  ")) }
        }
        return members.trimEnd()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as Struct
        return hash.hash == other.hash.hash
    }

    override fun hashCode(): Int = hash.hashCode()
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

                GpuType.Uint1 if (it.isArray) -> uint1Array(it.arraySize, it.name)
                GpuType.Uint2 if (it.isArray) -> uint2Array(it.arraySize, it.name)
                GpuType.Uint3 if (it.isArray) -> uint3Array(it.arraySize, it.name)
                GpuType.Uint4 if (it.isArray) -> uint4Array(it.arraySize, it.name)

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
