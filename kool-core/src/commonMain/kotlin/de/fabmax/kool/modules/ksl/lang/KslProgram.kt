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

    fun interStageFloat1(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageScalar<KslTypeFloat1> {
        val varName = name ?: nextName("interStageF1")
        val input = KslVarScalar(varName, KslTypeFloat1, true)
        val output = KslVarScalar(varName, KslTypeFloat1, false)
        return KslInterStageScalar(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat2(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat2, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF2")
        val input = KslVarVector(varName, KslTypeFloat2, true)
        val output = KslVarVector(varName, KslTypeFloat2, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat3(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat3, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF3")
        val input = KslVarVector(varName, KslTypeFloat3, true)
        val output = KslVarVector(varName, KslTypeFloat3, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }
    fun interStageFloat4(interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth, name: String? = null): KslInterStageVector<KslTypeFloat4, KslTypeFloat1> {
        val varName = name ?: nextName("interStageF4")
        val input = KslVarVector(varName, KslTypeFloat4, true)
        val output = KslVarVector(varName, KslTypeFloat4, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, interpolation).also { registerInterStageVar(it) }
    }

    fun interStageInt1(name: String? = null): KslInterStageScalar<KslTypeInt1> {
        val varName = name ?: nextName("interStageI1")
        val input = KslVarScalar(varName, KslTypeInt1, true)
        val output = KslVarScalar(varName, KslTypeInt1, false)
        return KslInterStageScalar(input, output, KslShaderStageType.VertexShader, KslInterStageInterpolation.Flat).also { registerInterStageVar(it) }
    }

    fun interStageInt2(name: String? = null): KslInterStageVector<KslTypeInt2, KslTypeInt1> {
        val varName = name ?: nextName("interStageI2")
        val input = KslVarVector(varName, KslTypeInt2, true)
        val output = KslVarVector(varName, KslTypeInt2, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, KslInterStageInterpolation.Flat).also { registerInterStageVar(it) }
    }

    fun interStageInt3(name: String? = null): KslInterStageVector<KslTypeInt3, KslTypeInt1> {
        val varName = name ?: nextName("interStageI3")
        val input = KslVarVector(varName, KslTypeInt3, true)
        val output = KslVarVector(varName, KslTypeInt3, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, KslInterStageInterpolation.Flat).also { registerInterStageVar(it) }
    }

    fun interStageInt4(name: String? = null): KslInterStageVector<KslTypeInt4, KslTypeInt1> {
        val varName = name ?: nextName("interStageI4")
        val input = KslVarVector(varName, KslTypeInt4, true)
        val output = KslVarVector(varName, KslTypeInt4, false)
        return KslInterStageVector(input, output, KslShaderStageType.VertexShader, KslInterStageInterpolation.Flat).also { registerInterStageVar(it) }
    }

    fun prepareGenerate() {
        stages.forEach { it.prepareGenerate() }
    }

}