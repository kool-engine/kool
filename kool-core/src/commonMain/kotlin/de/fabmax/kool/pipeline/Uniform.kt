package de.fabmax.kool.pipeline

data class Uniform(
    val name: String,
    val type: GpuType,
    val arraySize: Int = 1
) {
    val isArray: Boolean
        get() = arraySize > 1

    override fun toString(): String {
        return name + if (isArray) "[$arraySize]" else ""
    }

    companion object {
        fun float1(name: String) = Uniform(name, GpuType.FLOAT1)
        fun float2(name: String) = Uniform(name, GpuType.FLOAT2)
        fun float3(name: String) = Uniform(name, GpuType.FLOAT3)
        fun float4(name: String) = Uniform(name, GpuType.FLOAT4)

        fun int1(name: String) = Uniform(name, GpuType.INT1)
        fun int2(name: String) = Uniform(name, GpuType.INT2)
        fun int3(name: String) = Uniform(name, GpuType.INT3)
        fun int4(name: String) = Uniform(name, GpuType.INT4)

        fun mat2(name: String) = Uniform(name, GpuType.MAT2)
        fun mat3(name: String) = Uniform(name, GpuType.MAT3)
        fun mat4(name: String) = Uniform(name, GpuType.MAT4)

        fun float1Array(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT1, arraySize)
        fun float2Array(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT2, arraySize)
        fun float3Array(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT3, arraySize)
        fun float4Array(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT4, arraySize)

        fun int1Array(name: String, arraySize: Int) = Uniform(name, GpuType.INT1, arraySize)
        fun int2Array(name: String, arraySize: Int) = Uniform(name, GpuType.INT2, arraySize)
        fun int3Array(name: String, arraySize: Int) = Uniform(name, GpuType.INT3, arraySize)
        fun int4Array(name: String, arraySize: Int) = Uniform(name, GpuType.INT4, arraySize)

        fun mat2Array(name: String, arraySize: Int) = Uniform(name, GpuType.MAT2, arraySize)
        fun mat3Array(name: String, arraySize: Int) = Uniform(name, GpuType.MAT3, arraySize)
        fun mat4Array(name: String, arraySize: Int) = Uniform(name, GpuType.MAT4, arraySize)
    }
}
