package de.fabmax.kool.modules.ksl.lang

import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class KslProgram(val name: String) {

    var dumpCode = false

    private var nextNameIdx = 1
    internal fun nextName(prefix: String): String = "${prefix}_${nextNameIdx++}"

    val uniforms = mutableMapOf<String, KslUniform<*>>()
    val uniformBuffers = mutableListOf<KslUniformBuffer>()

    val vertexStage = KslVertexStage(this)
    val fragmentStage = KslFragmentStage(this)
    val stages = listOf(vertexStage, fragmentStage)

    fun vertexStage(block: KslVertexStage.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        vertexStage.apply(block)
    }

    fun fragmentStage(block: KslFragmentStage.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        fragmentStage.apply(block)
    }

    private fun registerUniform(uniform: KslUniform<*>) {
        uniforms[uniform.name] = uniform
        stages.forEach {
            it.uniforms += uniform
            it.globalScope.definedStates += uniform.value
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateUniform(name: String, create: () -> T): T {
        val uniform = uniforms[name] ?: create().also { registerUniform(it) }
        if (uniform !is T) {
            throw IllegalStateException("Existing uniform with name \"$name\" has not the expected type")
        }
        return uniform
    }

    fun uniformFloat1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslTypeFloat1, false)) }
    fun uniformFloat2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat2, false)) }
    fun uniformFloat3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat3, false)) }
    fun uniformFloat4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeFloat4, false)) }

    fun uniformFloat1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslTypeFloat1, arraySize, false)) }
    fun uniformFloat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat2, arraySize, false)) }
    fun uniformFloat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat3, arraySize, false)) }
    fun uniformFloat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeFloat4, arraySize, false)) }

    fun uniformInt1(name: String) = getOrCreateUniform(name) { KslUniformScalar(KslVarScalar(name, KslTypeInt1, false)) }
    fun uniformInt2(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt2, false)) }
    fun uniformInt3(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt3, false)) }
    fun uniformInt4(name: String) = getOrCreateUniform(name) { KslUniformVector(KslVarVector(name, KslTypeInt4, false)) }

    fun uniformInt1Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformScalarArray(KslArrayScalar(name, KslTypeInt1, arraySize, false)) }
    fun uniformInt2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt2, arraySize, false)) }
    fun uniformInt3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt3, arraySize, false)) }
    fun uniformInt4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformVectorArray(KslArrayVector(name, KslTypeInt4, arraySize, false)) }

    fun uniformMat2(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat2, false)) }
    fun uniformMat3(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat3, false)) }
    fun uniformMat4(name: String) = getOrCreateUniform(name) { KslUniformMatrix(KslVarMatrix(name, KslTypeMat4, false)) }

    fun uniformMat2Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat2, arraySize, false)) }
    fun uniformMat3Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat3, arraySize, false)) }
    fun uniformMat4Array(name: String, arraySize: Int) =
        getOrCreateUniform(name) { KslUniformMatrixArray(KslArrayMatrix(name, KslTypeMat4, arraySize, false)) }

    fun texture1d(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeColorSampler1d, false)) }
    fun texture2d(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeColorSampler2d, false)) }
    fun texture3d(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeColorSampler3d, false)) }
    fun textureCube(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeColorSamplerCube, false)) }

    fun depthTexture2d(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeDepthSampler2d, false)) }
    fun depthTextureCube(name: String) = getOrCreateUniform(name) { KslUniform(KslVar(name, KslTypeDepthSamplerCube, false)) }

    // arrays of textures (this is different to array textures, like, e.g., KslTypeColorSampler2dArray)
    fun textureArray1d(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler1d, arraySize, false)) }
    fun textureArray2d(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler2d, arraySize, false)) }
    fun textureArray3d(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler3d, arraySize, false)) }
    fun textureArrayCube(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSamplerCube, arraySize, false)) }

    // arrays of depth textures (this is different to array textures, like, e.g., KslTypeDepthSampler2dArray)
    fun depthTextureArray2d(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeDepthSampler2d, arraySize, false)) }
    fun depthTextureArrayCube(name: String, arraySize: Int) = getOrCreateUniform(name) { KslUniformArray(KslArrayGeneric(name, KslTypeDepthSamplerCube, arraySize, false)) }

    private fun registerInterStageVar(interStageVar: KslInterStageVar<*>) {
        stages.forEach { it.interStageVars += interStageVar }
        vertexStage.globalScope.definedStates += interStageVar.input
        fragmentStage.globalScope.definedStates += interStageVar.output
    }

    private fun <S> interStageScalar(type: S, interpolation: KslInterStageInterpolation, name: String):
            KslInterStageScalar<S> where S: KslType, S: KslScalar {
        val input = KslVarScalar(name, type, true)
        val output = KslVarScalar(name, type, false)
        return KslInterStageScalar(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    private fun <V, S> interStageVector(type: V, interpolation: KslInterStageInterpolation, name: String):
            KslInterStageVector<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        val input = KslVarVector(name, type, true)
        val output = KslVarVector(name, type, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    fun interStageFloat1(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageScalar(KslTypeFloat1, interpolation, name ?: nextName("interStageF1"))
    fun interStageFloat2(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslTypeFloat2, interpolation, name ?: nextName("interStageF2"))
    fun interStageFloat3(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslTypeFloat3, interpolation, name ?: nextName("interStageF3"))
    fun interStageFloat4(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslTypeFloat4, interpolation, name ?: nextName("interStageF4"))

    fun interStageInt1(name: String? = null) = interStageScalar(KslTypeInt1, KslInterStageInterpolation.Flat, name ?: nextName("interStageI1"))
    fun interStageInt2(name: String? = null) = interStageVector(KslTypeInt2, KslInterStageInterpolation.Flat, name ?: nextName("interStageI2"))
    fun interStageInt3(name: String? = null) = interStageVector(KslTypeInt3, KslInterStageInterpolation.Flat, name ?: nextName("interStageI3"))
    fun interStageInt4(name: String? = null) = interStageVector(KslTypeInt4, KslInterStageInterpolation.Flat, name ?: nextName("interStageI4"))

    private fun <S> interStageScalarArray(type: S, arraySize: Int, interpolation: KslInterStageInterpolation, name: String):
            KslInterStageScalarArray<S> where S: KslType, S: KslScalar {
        val input = KslArrayScalar(name, type, arraySize, true)
        val output = KslArrayScalar(name, type, arraySize, false)
        return KslInterStageScalarArray(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    private fun <V, S> interStageVectorArray(type: V, arraySize: Int, interpolation: KslInterStageInterpolation, name: String):
            KslInterStageVectorArray<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        val input = KslArrayVector(name, type, arraySize, true)
        val output = KslArrayVector(name, type, arraySize, false)
        return KslInterStageVectorArray(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    fun interStageFloat1Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageScalarArray(KslTypeFloat1, arraySize, interpolation, name ?: nextName("interStageF1Array"))
    fun interStageFloat2Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslTypeFloat2, arraySize, interpolation, name ?: nextName("interStageF2Array"))
    fun interStageFloat3Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslTypeFloat3, arraySize, interpolation, name ?: nextName("interStageF3Array"))
    fun interStageFloat4Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslTypeFloat4, arraySize, interpolation, name ?: nextName("interStageF4Array"))

    fun interStageInt1Array(arraySize: Int, name: String? = null) =
        interStageScalarArray(KslTypeInt1, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI1Array"))
    fun interStageInt2Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslTypeInt2, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI2Array"))
    fun interStageInt3Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslTypeInt3, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI3Array"))
    fun interStageInt4Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslTypeInt4, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI4Array"))

    fun prepareGenerate() {
        stages.forEach { it.prepareGenerate() }
    }

}