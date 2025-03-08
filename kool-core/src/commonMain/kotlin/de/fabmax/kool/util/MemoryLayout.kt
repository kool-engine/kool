package de.fabmax.kool.util

import de.fabmax.kool.pipeline.GpuType

sealed interface MemoryLayout {
    fun alignmentOf(type: GpuType, isArray: Boolean): Int
    fun arrayStrideOf(type: GpuType): Int
    fun paddedSizeOf(type: GpuType): Int
    fun structSize(struct: Struct<*>, lastPosition: Int): Int

    fun offsetAndSizeOf(prevPosition: Int, type: GpuType, arraySize: Int): Pair<Int, Int> {
        require(arraySize > 0)
        val alignment = alignmentOf(type, arraySize > 1)
        val offset = alignedOffset(prevPosition, alignment)
        val size = if (arraySize == 1) paddedSizeOf(type) else arrayStrideOf(type) * arraySize
        return offset to size
    }

    fun alignedOffset(prevPosition: Int, alignment: Int): Int {
        return prevPosition + (alignment - 1) and (alignment - 1).inv()
    }

    data object TightlyPacked : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int = 4
        override fun arrayStrideOf(type: GpuType): Int = type.byteSize
        override fun paddedSizeOf(type: GpuType) = type.byteSize
        override fun structSize(struct: Struct<*>, lastPosition: Int): Int = lastPosition
    }

    data object Std140 : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int {
            return if (isArray) 16 else when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 12
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 12
                GpuType.Int4 -> 16

                GpuType.Mat2 -> 16
                GpuType.Mat3 -> 16
                GpuType.Mat4 -> 16

                is GpuType.Struct -> 16
            }
        }

        override fun arrayStrideOf(type: GpuType): Int = when (type) {
            GpuType.Mat2 -> 2 * 16
            GpuType.Mat3 -> 3 * 16
            GpuType.Mat4 -> 4 * 16
            is GpuType.Struct -> structSize(type.struct, type.byteSize)
            else -> 16
        }

        override fun paddedSizeOf(type: GpuType): Int {
            return when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 12
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 12
                GpuType.Int4 -> 16

                GpuType.Mat2 -> 2 * 16
                GpuType.Mat3 -> 3 * 16
                GpuType.Mat4 -> 4 * 16

                is GpuType.Struct -> structSize(type.struct, type.byteSize)
            }
        }

        override fun structSize(struct: Struct<*>, lastPosition: Int): Int = alignedOffset(lastPosition, 16)
    }

    data object Std430 : MemoryLayout {
        override fun alignmentOf(type: GpuType, isArray: Boolean): Int {
            return when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 12
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 12
                GpuType.Int4 -> 16

                GpuType.Mat2 -> 16
                GpuType.Mat3 -> 16
                GpuType.Mat4 -> 16

                is GpuType.Struct -> type.struct.members.maxOf { alignmentOf(it.type, it.arraySize > 1) }
            }
        }

        override fun arrayStrideOf(type: GpuType): Int = paddedSizeOf(type)

        override fun paddedSizeOf(type: GpuType): Int {
            return when (type) {
                GpuType.Float1 -> 4
                GpuType.Float2 -> 8
                GpuType.Float3 -> 12
                GpuType.Float4 -> 16

                GpuType.Int1 -> 4
                GpuType.Int2 -> 8
                GpuType.Int3 -> 12
                GpuType.Int4 -> 16

                GpuType.Mat2 -> 2 * 16
                GpuType.Mat3 -> 3 * 16
                GpuType.Mat4 -> 4 * 16

                is GpuType.Struct -> structSize(type.struct, type.byteSize)
            }
        }

        override fun structSize(struct: Struct<*>, lastPosition: Int): Int {
            val maxMemberSize = struct.members.maxOf { paddedSizeOf(it.type) }.coerceAtMost(16)
            return alignedOffset(lastPosition, maxMemberSize)
        }
    }
}