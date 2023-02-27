package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.KslShaderListener
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class KslProgram(val name: String) {

    /**
     * Debug property: if true generated shader code is dumped to console
     */
    var dumpCode = false

    var isPrepared = false
        private set

    private var nextNameIdx = 1
    internal fun nextName(prefix: String): String = "${prefix}_${nextNameIdx++}"

    val commonUniformBuffer = KslUniformBuffer("CommonUniforms", this)
    val uniformBuffers = mutableListOf(commonUniformBuffer)
    val uniformSamplers = mutableMapOf<String, KslUniform<*>>()

    val dataBlocks = mutableListOf<KslDataBlock>()
    val vertexStage = KslVertexStage(this)
    val fragmentStage = KslFragmentStage(this)
    val stages = listOf(vertexStage, fragmentStage)

    val shaderListeners = mutableListOf<KslShaderListener>()

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

    private fun registerSampler(uniform: KslUniform<*>) {
        uniformSamplers[uniform.name] = uniform
        stages.forEach {
            it.globalScope.definedStates += uniform.value
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateSampler(name: String, create: () -> T): T {
        val uniform = uniformSamplers[name] ?: create().also { registerSampler(it) }
        if (uniform !is T) {
            throw IllegalStateException("Existing uniform with name \"$name\" has not the expected type")
        }
        return uniform
    }

    fun uniformFloat1(name: String) = commonUniformBuffer.uniformFloat1(name)
    fun uniformFloat2(name: String) = commonUniformBuffer.uniformFloat2(name)
    fun uniformFloat3(name: String) = commonUniformBuffer.uniformFloat3(name)
    fun uniformFloat4(name: String) = commonUniformBuffer.uniformFloat4(name)

    fun uniformFloat1Array(name: String, arraySize: Int) = commonUniformBuffer.uniformFloat1Array(name, arraySize)
    fun uniformFloat2Array(name: String, arraySize: Int) = commonUniformBuffer.uniformFloat2Array(name, arraySize)
    fun uniformFloat3Array(name: String, arraySize: Int) = commonUniformBuffer.uniformFloat3Array(name, arraySize)
    fun uniformFloat4Array(name: String, arraySize: Int) = commonUniformBuffer.uniformFloat4Array(name, arraySize)

    fun uniformInt1(name: String) = commonUniformBuffer.uniformInt1(name)
    fun uniformInt2(name: String) = commonUniformBuffer.uniformInt2(name)
    fun uniformInt3(name: String) = commonUniformBuffer.uniformInt3(name)
    fun uniformInt4(name: String) = commonUniformBuffer.uniformInt4(name)

    fun uniformInt1Array(name: String, arraySize: Int) = commonUniformBuffer.uniformInt1Array(name, arraySize)
    fun uniformInt2Array(name: String, arraySize: Int) = commonUniformBuffer.uniformInt2Array(name, arraySize)
    fun uniformInt3Array(name: String, arraySize: Int) = commonUniformBuffer.uniformInt3Array(name, arraySize)
    fun uniformInt4Array(name: String, arraySize: Int) = commonUniformBuffer.uniformInt4Array(name, arraySize)

    fun uniformMat2(name: String) = commonUniformBuffer.uniformMat2(name)
    fun uniformMat3(name: String) = commonUniformBuffer.uniformMat3(name)
    fun uniformMat4(name: String) = commonUniformBuffer.uniformMat4(name)

    fun uniformMat2Array(name: String, arraySize: Int) = commonUniformBuffer.uniformMat2Array(name, arraySize)
    fun uniformMat3Array(name: String, arraySize: Int) = commonUniformBuffer.uniformMat3Array(name, arraySize)
    fun uniformMat4Array(name: String, arraySize: Int) = commonUniformBuffer.uniformMat4Array(name, arraySize)

    fun texture1d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeColorSampler1d, false)) }
    fun texture2d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeColorSampler2d, false)) }
    fun texture3d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeColorSampler3d, false)) }
    fun textureCube(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeColorSamplerCube, false)) }

    fun depthTexture2d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeDepthSampler2d, false)) }
    fun depthTextureCube(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeDepthSamplerCube, false)) }

    // arrays of textures (this is different to array textures, like, e.g., KslTypeColorSampler2dArray)
    fun textureArray1d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler1d, arraySize, false)) }
    fun textureArray2d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler2d, arraySize, false)) }
    fun textureArray3d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSampler3d, arraySize, false)) }
    fun textureArrayCube(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeColorSamplerCube, arraySize, false)) }

    fun itexture2d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslTypeIntSampler2d, false)) }

    // arrays of depth textures (this is different to array textures, like, e.g., KslTypeDepthSampler2dArray)
    fun depthTextureArray2d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeDepthSampler2d, arraySize, false)) }
    fun depthTextureArrayCube(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslTypeDepthSamplerCube, arraySize, false)) }

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
        if (!isPrepared) {
            isPrepared = true

            stages.forEach { it.prepareGenerate() }

            // remove unused uniforms
            uniformBuffers.filter { !it.isShared }.forEach {
                it.uniforms.values.retainAll { u -> vertexStage.dependsOn(u) || fragmentStage.dependsOn(u) }
            }
            uniformBuffers.removeAll { it.uniforms.isEmpty() }

            // remove unused texture samplers
            uniformSamplers.values.retainAll { u -> vertexStage.dependsOn(u) || fragmentStage.dependsOn(u) }
        }
    }
}