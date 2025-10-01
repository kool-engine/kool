package de.fabmax.kool.util

import de.fabmax.kool.pipeline.GpuType

sealed class StructMember(
    val name: String,
    val type: GpuType,
    val byteOffset: Int,
    val parent: Struct,
) {
    open fun layoutInfo(indent: String = ""): String {
        val name = "$name:".padEnd(20)
        val typeName = type.toString().padEnd(16)
        return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructMember
        return type == other.type
    }

    override fun hashCode(): Int = type.hashCode()
}

sealed class StructArrayMember(
    name: String,
    val arraySize: Int,
    type: GpuType,
    byteOffset: Int,
    parent: Struct,
) : StructMember(name, type, byteOffset, parent) {
    val arrayStride: Int get() {
        return parent.layout.arrayStrideOf(type)
    }

    override fun layoutInfo(indent: String): String {
        val name = "$name:".padEnd(20)
        val typeName = "$type[$arraySize]".padEnd(16)
        return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructArrayMember
        return type == other.type && arraySize == other.arraySize
    }

    override fun hashCode(): Int = type.hashCode() * 31 + arraySize
}

class Float1Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Float1, byteOffset, parent)
class Float2Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Float2, byteOffset, parent)
class Float3Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Float3, byteOffset, parent)
class Float4Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Float4, byteOffset, parent)

class Int1Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Int1, byteOffset, parent)
class Int2Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Int2, byteOffset, parent)
class Int3Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Int3, byteOffset, parent)
class Int4Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Int4, byteOffset, parent)

class Uint1Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Uint1, byteOffset, parent)
class Uint2Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Uint2, byteOffset, parent)
class Uint3Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Uint3, byteOffset, parent)
class Uint4Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Uint4, byteOffset, parent)

class Bool1Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Bool1, byteOffset, parent)
class Bool2Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Bool2, byteOffset, parent)
class Bool3Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Bool3, byteOffset, parent)
class Bool4Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Bool4, byteOffset, parent)

class Mat2Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Mat2, byteOffset, parent)
class Mat3Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Mat3, byteOffset, parent)
class Mat4Member(name: String, byteOffset: Int, parent: Struct) : StructMember(name, GpuType.Mat4, byteOffset, parent)

class Float1ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Float1, byteOffset, parent)
class Float2ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Float2, byteOffset, parent)
class Float3ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Float3, byteOffset, parent)
class Float4ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Float4, byteOffset, parent)

class Int1ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Int1, byteOffset, parent)
class Int2ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Int2, byteOffset, parent)
class Int3ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Int3, byteOffset, parent)
class Int4ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Int4, byteOffset, parent)

class Uint1ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Uint1, byteOffset, parent)
class Uint2ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Uint2, byteOffset, parent)
class Uint3ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Uint3, byteOffset, parent)
class Uint4ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Uint4, byteOffset, parent)

class Bool1ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Bool1, byteOffset, parent)
class Bool2ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Bool2, byteOffset, parent)
class Bool3ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Bool3, byteOffset, parent)
class Bool4ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Bool4, byteOffset, parent)

class Mat2ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Mat2, byteOffset, parent)
class Mat3ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Mat3, byteOffset, parent)
class Mat4ArrayMember(name: String, arraySize: Int, byteOffset: Int, parent: Struct) :
    StructArrayMember(name, arraySize, GpuType.Mat4, byteOffset, parent)

class NestedStructMember<S: Struct>(
    name: String,
    byteOffset: Int,
    val struct: S,
    parent: Struct
) : StructMember(name, struct.type, byteOffset, parent) {
    init {
        require(struct.layout == parent.layout) {
            "Nested struct must have the same layout as the parent struct (but nested layout is ${struct.layout} and parent layout is ${parent.layout})"
        }
    }

    override fun layoutInfo(indent: String): String {
        return super.layoutInfo(indent) + "\n" + struct.layoutInfo("$indent    ").trimEnd()
    }
}

class NestedStructArrayMember<S: Struct>(
    name: String,
    arraySize: Int,
    byteOffset: Int,
    val struct: S,
    parent: Struct
) : StructArrayMember(name, arraySize, struct.type, byteOffset, parent) {
    init {
        require(struct.layout == parent.layout) {
            "Nested struct must have the same layout as the parent struct (but nested layout is ${struct.layout} and parent layout is ${parent.layout})"
        }
    }

    override fun layoutInfo(indent: String): String {
        return super.layoutInfo(indent) + "\n" + struct.layoutInfo("$indent    ").trimEnd()
    }
}