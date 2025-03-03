package de.fabmax.kool.pipeline

data class Uniform(
    val name: String,
    val type: GpuType,
    val arraySize: Int = 1
) {
    val isArray: Boolean
        get() = arraySize > 1

    init {
        check(arraySize >= 1) {
            "Uniform $name ($type) has invalid arraySize: $arraySize. Uniform arraySize has to be >= 1 (non-array-types should have arraySize = 1)"
        }
    }

    override fun toString(): String {
        return name + if (isArray) "[$arraySize]" else ""
    }

    companion object {
        fun float1(name: String) = Uniform(name, GpuType.Float1)
        fun float2(name: String) = Uniform(name, GpuType.Float2)
        fun float3(name: String) = Uniform(name, GpuType.Float3)
        fun float4(name: String) = Uniform(name, GpuType.Float4)

        fun int1(name: String) = Uniform(name, GpuType.Int1)
        fun int2(name: String) = Uniform(name, GpuType.Int2)
        fun int3(name: String) = Uniform(name, GpuType.Int3)
        fun int4(name: String) = Uniform(name, GpuType.Int4)

        fun mat2(name: String) = Uniform(name, GpuType.Mat2)
        fun mat3(name: String) = Uniform(name, GpuType.Mat3)
        fun mat4(name: String) = Uniform(name, GpuType.Mat4)

        fun float1Array(name: String, arraySize: Int) = Uniform(name, GpuType.Float1, arraySize)
        fun float2Array(name: String, arraySize: Int) = Uniform(name, GpuType.Float2, arraySize)
        fun float3Array(name: String, arraySize: Int) = Uniform(name, GpuType.Float3, arraySize)
        fun float4Array(name: String, arraySize: Int) = Uniform(name, GpuType.Float4, arraySize)

        fun int1Array(name: String, arraySize: Int) = Uniform(name, GpuType.Int1, arraySize)
        fun int2Array(name: String, arraySize: Int) = Uniform(name, GpuType.Int2, arraySize)
        fun int3Array(name: String, arraySize: Int) = Uniform(name, GpuType.Int3, arraySize)
        fun int4Array(name: String, arraySize: Int) = Uniform(name, GpuType.Int4, arraySize)

        fun mat2Array(name: String, arraySize: Int) = Uniform(name, GpuType.Mat2, arraySize)
        fun mat3Array(name: String, arraySize: Int) = Uniform(name, GpuType.Mat3, arraySize)
        fun mat4Array(name: String, arraySize: Int) = Uniform(name, GpuType.Mat4, arraySize)
    }
}
