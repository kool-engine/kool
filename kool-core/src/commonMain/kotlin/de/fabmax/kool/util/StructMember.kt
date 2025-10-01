package de.fabmax.kool.util

import de.fabmax.kool.pipeline.GpuType

sealed class StructMember<T: Struct>(
    val name: String,
    val type: GpuType,
    val byteOffset: Int,
    val parent: T,
) {
    open fun layoutInfo(indent: String = ""): String {
        val name = "$name:".padEnd(20)
        val typeName = type.toString().padEnd(16)
        return "$indent$name$typeName 0x${byteOffset.toString(16).padStart(4, '0')}"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        other as StructMember<*>
        return type == other.type
    }

    override fun hashCode(): Int = type.hashCode()
}

sealed class StructArrayMember<T: Struct>(
    name: String,
    val arraySize: Int,
    type: GpuType,
    byteOffset: Int,
    parent: T,
) : StructMember<T>(name, type, byteOffset, parent) {
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
        other as StructArrayMember<*>
        return type == other.type && arraySize == other.arraySize
    }

    override fun hashCode(): Int = type.hashCode() * 31 + arraySize
}

class Float1Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Float1, byteOffset, parent)
class Float2Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Float2, byteOffset, parent)
class Float3Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Float3, byteOffset, parent)
class Float4Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Float4, byteOffset, parent)

class Int1Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Int1, byteOffset, parent)
class Int2Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Int2, byteOffset, parent)
class Int3Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Int3, byteOffset, parent)
class Int4Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Int4, byteOffset, parent)

class Uint1Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Uint1, byteOffset, parent)
class Uint2Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Uint2, byteOffset, parent)
class Uint3Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Uint3, byteOffset, parent)
class Uint4Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Uint4, byteOffset, parent)

class Bool1Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Bool1, byteOffset, parent)
class Bool2Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Bool2, byteOffset, parent)
class Bool3Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Bool3, byteOffset, parent)
class Bool4Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Bool4, byteOffset, parent)

class Mat2Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Mat2, byteOffset, parent)
class Mat3Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Mat3, byteOffset, parent)
class Mat4Member<T: Struct>(name: String, byteOffset: Int, parent: T) : StructMember<T>(name, GpuType.Mat4, byteOffset, parent)

class Float1ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Float1, byteOffset, parent)
class Float2ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Float2, byteOffset, parent)
class Float3ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Float3, byteOffset, parent)
class Float4ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Float4, byteOffset, parent)

class Int1ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Int1, byteOffset, parent)
class Int2ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Int2, byteOffset, parent)
class Int3ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Int3, byteOffset, parent)
class Int4ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Int4, byteOffset, parent)

class Uint1ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Uint1, byteOffset, parent)
class Uint2ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Uint2, byteOffset, parent)
class Uint3ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Uint3, byteOffset, parent)
class Uint4ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Uint4, byteOffset, parent)

class Bool1ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Bool1, byteOffset, parent)
class Bool2ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Bool2, byteOffset, parent)
class Bool3ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Bool3, byteOffset, parent)
class Bool4ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Bool4, byteOffset, parent)

class Mat2ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Mat2, byteOffset, parent)
class Mat3ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Mat3, byteOffset, parent)
class Mat4ArrayMember<T: Struct>(name: String, arraySize: Int, byteOffset: Int, parent: T) :
    StructArrayMember<T>(name, arraySize, GpuType.Mat4, byteOffset, parent)

class NestedStructMember<T: Struct, S: Struct>(
    name: String,
    byteOffset: Int,
    val struct: S,
    parent: T
) : StructMember<T>(name, struct.type, byteOffset, parent) {
    init {
        require(struct.layout == parent.layout) {
            "Nested struct must have the same layout as the parent struct (but nested layout is ${struct.layout} and parent layout is ${parent.layout})"
        }
    }

    override fun layoutInfo(indent: String): String {
        return super.layoutInfo(indent) + "\n" + struct.layoutInfo("$indent    ").trimEnd()
    }
}

class NestedStructArrayMember<T: Struct, S: Struct>(
    name: String,
    arraySize: Int,
    byteOffset: Int,
    val struct: S,
    parent: T
) : StructArrayMember<T>(name, arraySize, struct.type, byteOffset, parent) {
    init {
        require(struct.layout == parent.layout) {
            "Nested struct must have the same layout as the parent struct (but nested layout is ${struct.layout} and parent layout is ${parent.layout})"
        }
    }

    override fun layoutInfo(indent: String): String {
        return super.layoutInfo(indent) + "\n" + struct.layoutInfo("$indent    ").trimEnd()
    }
}