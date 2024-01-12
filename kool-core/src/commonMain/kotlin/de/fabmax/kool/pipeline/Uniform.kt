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
}

fun uniform1f(name: String) = Uniform(name, GpuType.FLOAT1)
fun uniform2f(name: String) = Uniform(name, GpuType.FLOAT2)
fun uniform3f(name: String) = Uniform(name, GpuType.FLOAT3)
fun uniform4f(name: String) = Uniform(name, GpuType.FLOAT4)

fun uniform1i(name: String) = Uniform(name, GpuType.INT1)
fun uniform2i(name: String) = Uniform(name, GpuType.INT2)
fun uniform3i(name: String) = Uniform(name, GpuType.INT3)
fun uniform4i(name: String) = Uniform(name, GpuType.INT4)

fun uniformMat2(name: String) = Uniform(name, GpuType.MAT2)
fun uniformMat3(name: String) = Uniform(name, GpuType.MAT3)
fun uniformMat4(name: String) = Uniform(name, GpuType.MAT4)

fun uniform1fv(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT1, arraySize)
fun uniform2fv(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT2, arraySize)
fun uniform3fv(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT3, arraySize)
fun uniform4fv(name: String, arraySize: Int) = Uniform(name, GpuType.FLOAT4, arraySize)

fun uniform1iv(name: String, arraySize: Int) = Uniform(name, GpuType.INT1, arraySize)
fun uniform2iv(name: String, arraySize: Int) = Uniform(name, GpuType.INT2, arraySize)
fun uniform3iv(name: String, arraySize: Int) = Uniform(name, GpuType.INT3, arraySize)
fun uniform4iv(name: String, arraySize: Int) = Uniform(name, GpuType.INT4, arraySize)

fun uniformMat2v(name: String, arraySize: Int) = Uniform(name, GpuType.MAT2, arraySize)
fun uniformMat3v(name: String, arraySize: Int) = Uniform(name, GpuType.MAT3, arraySize)
fun uniformMat4v(name: String, arraySize: Int) = Uniform(name, GpuType.MAT4, arraySize)