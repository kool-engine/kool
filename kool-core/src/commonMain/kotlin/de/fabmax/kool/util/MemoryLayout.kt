package de.fabmax.kool.util

import de.fabmax.kool.pipeline.GpuType

sealed interface MemoryLayout {
    fun alignmentOf(type: GpuType, isArray: Boolean): Int
    fun arrayStrideOf(type: GpuType): Int
    fun structSize(struct: Struct, lastPosition: Int): Int
    fun sizeOf(type: GpuType): Int = type.byteSize

    fun offsetAndSizeOf(prevPosition: Int, type: GpuType, arraySize: Int): Pair<Int, Int> {
        require(arraySize > 0)
        val alignment = alignmentOf(type, arraySize > 1)
        val offset = alignedOffset(prevPosition, alignment)
        val size = if (arraySize == 1) sizeOf(type) else arrayStrideOf(type) * arraySize
        return offset to size
    }

    fun alignedOffset(prevPosition: Int, alignment: Int): Int {
        return prevPosition + (alignment - 1) and (alignment - 1).inv()
    }

    data object DontCare : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int = 0
        override fun arrayStrideOf(type: GpuType): Int = 0
        override fun structSize(struct: Struct, lastPosition: Int): Int = 0
    }

    data object TightlyPacked : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int = 4
        override fun arrayStrideOf(type: GpuType): Int = type.byteSize
        override fun structSize(struct: Struct, lastPosition: Int): Int = lastPosition
    }

    data object Std140 : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int {
            return if (isArray) 16 else when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 16
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 16
                GpuType.Int4 -> 16

                GpuType.Uint1 -> 4
                GpuType.Uint2 -> 8
                GpuType.Uint3 -> 16
                GpuType.Uint4 -> 16

                GpuType.Bool1 -> 4
                GpuType.Bool2 -> 8
                GpuType.Bool3 -> 16
                GpuType.Bool4 -> 16

                GpuType.Mat2 -> 16
                GpuType.Mat3 -> 16
                GpuType.Mat4 -> 16

                is GpuType.Struct -> 16
            }
        }

        override fun arrayStrideOf(type: GpuType): Int = when (type) {
            GpuType.Mat2 -> type.byteSize
            GpuType.Mat3 -> type.byteSize
            GpuType.Mat4 -> type.byteSize
            is GpuType.Struct -> type.byteSize
            else -> 16
        }

        override fun structSize(struct: Struct, lastPosition: Int): Int = alignedOffset(lastPosition, 16)
    }

    data object Std430 : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int {
            return when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 16
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 16
                GpuType.Int4 -> 16

                GpuType.Uint1 -> 4
                GpuType.Uint2 -> 8
                GpuType.Uint3 -> 16
                GpuType.Uint4 -> 16

                GpuType.Bool1 -> 4
                GpuType.Bool2 -> 8
                GpuType.Bool3 -> 16
                GpuType.Bool4 -> 16

                GpuType.Mat2 -> 16
                GpuType.Mat3 -> 16
                GpuType.Mat4 -> 16

                is GpuType.Struct -> type.struct.members.maxOf { alignmentOf(it.type, it is StructArrayMember) }
            }
        }

        override fun arrayStrideOf(type: GpuType): Int = when (type) {
            GpuType.Float3 -> 16
            GpuType.Int3 -> 16
            else -> sizeOf(type)
        }

        override fun structSize(struct: Struct, lastPosition: Int): Int {
            val maxMemberAlignment = struct.members.maxOf { alignmentOf(it.type, it is StructArrayMember) }
            return alignedOffset(lastPosition, maxMemberAlignment)
        }
    }
}