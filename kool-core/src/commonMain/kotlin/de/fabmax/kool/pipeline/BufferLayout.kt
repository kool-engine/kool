package de.fabmax.kool.pipeline

abstract class BufferLayout(val uniforms: List<Uniform>) {
    abstract val uniformPositions: Map<String, BufferPosition>
    abstract val size: Int
}

class ProvidedBufferLayout(
    uniforms: List<Uniform>,
    override val uniformPositions: Map<String, BufferPosition>,
    override val size: Int
) : BufferLayout(uniforms) {
    init {
        check(uniformPositions.size == uniforms.size) { "Given lists of uniforms and offsets mismatch in length" }
    }
}

class Std140BufferLayout(uniforms: List<Uniform>) : BufferLayout(uniforms) {
    override val uniformPositions: Map<String, BufferPosition>
    override val size: Int

    init {
        var pos = 0
        uniformPositions = uniforms.associate { uniform ->
            val bufPos = alignPosition(pos, uniform.alignment)
            pos = bufPos + uniform.sizeBytes
            uniform.name to BufferPosition(uniform.name, bufPos, pos - bufPos, uniform.arrayStrideBytes)
        }
        size = alignPosition(pos, 16)
    }

    private fun alignPosition(pos: Int, alignment: Int): Int {
        val padding = (alignment - (pos % alignment)) % alignment
        return pos + padding
    }

    private val Uniform.alignment: Int get() = when (type) {
        GpuType.FLOAT1 -> 4
        GpuType.INT1 -> 4
        GpuType.FLOAT2 -> 8
        GpuType.INT2 -> 8
        // everything else, including all array and matrix types, has vec4 alignment / 16 bytes
        else -> 16
    }

    private val Uniform.sizeBytes: Int get() =
        if (isArray) {
            when (type) {
                GpuType.MAT2 -> 2 * 16 * arraySize
                GpuType.MAT3 -> 3 * 16 * arraySize
                GpuType.MAT4 -> 4 * 16 * arraySize
                else -> 16 * arraySize
            }
        } else {
            when (type) {
                GpuType.FLOAT1 -> 4
                GpuType.FLOAT2 -> 8
                GpuType.FLOAT3 -> 12
                GpuType.FLOAT4 -> 16

                GpuType.INT1 -> 4
                GpuType.INT2 -> 8
                GpuType.INT3 -> 12
                GpuType.INT4 -> 16

                GpuType.MAT2 -> 2 * 16
                GpuType.MAT3 -> 3 * 16
                GpuType.MAT4 -> 4 * 16
            }
        }

    private val Uniform.arrayStrideBytes: Int get() = when (type) {
        GpuType.MAT2 -> 2 * 16
        GpuType.MAT3 -> 3 * 16
        GpuType.MAT4 -> 4 * 16
        else -> 16
    }
}

data class BufferPosition(
    val uniformName: String,
    val byteIndex: Int,
    val byteLen: Int,
    val arrayStrideBytes: Int
)
