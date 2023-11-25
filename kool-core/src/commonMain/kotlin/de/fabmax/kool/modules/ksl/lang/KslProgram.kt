package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslShader
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
    val uniformStorage = mutableMapOf<String, KslStorage<*,*>>()
    val dataBlocks = mutableListOf<KslDataBlock>()

    var vertexStage: KslVertexStage? = null
        private set
    var fragmentStage: KslFragmentStage? = null
        private set
    var computeStage: KslComputeStage? = null
        private set
    private val _stages = mutableListOf<KslShaderStage>()
    val stages: List<KslShaderStage> get() = _stages

    val shaderListeners = mutableListOf<KslShaderListener>()

    fun vertexStage(block: KslVertexStage.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val stage = vertexStage ?: KslVertexStage(this).also {
            initGlobalScope(it)
            vertexStage = it
            _stages += it
        }
        stage.apply(block)
    }

    fun fragmentStage(block: KslFragmentStage.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val stage = fragmentStage ?: KslFragmentStage(this).also {
            initGlobalScope(it)
            fragmentStage = it
            _stages += it
        }
        stage.apply(block)
    }

    fun computeStage(workGroupSizeX: Int = 1, workGroupSizeY: Int = 1, workGroupSizeZ: Int = 1, block: KslComputeStage.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        val stage = computeStage
            ?: KslComputeStage(this, Vec3i(workGroupSizeX, workGroupSizeY, workGroupSizeZ)).also {
                initGlobalScope(it)
                computeStage = it
                _stages += it
            }
        stage.apply(block)
    }

    private fun initGlobalScope(stage: KslShaderStage) {
        uniformBuffers.forEach { ubo ->
            ubo.uniforms.values.forEach { stage.globalScope.definedStates += it.value }
        }
        uniformSamplers.values.forEach { stage.globalScope.definedStates += it.value }
    }

    private fun registerSampler(uniform: KslUniform<*>) {
        uniformSamplers[uniform.name] = uniform
        stages.forEach {
            it.globalScope.definedStates += uniform.value
        }
    }

    @PublishedApi internal fun registerStorage(storage: KslStorage<*,*>) {
        uniformStorage[storage.name] = storage
        stages.forEach {
            it.globalScope.definedStates += storage
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateSampler(name: String, create: () -> T): T {
        val uniform = uniformSamplers[name] ?: create().also { registerSampler(it) }
        check(uniform is T) {
            "Existing uniform with name \"$name\" has not the expected type"
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

    fun texture1d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslColorSampler1d, false)) }
    fun texture2d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslColorSampler2d, false)) }
    fun texture3d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslColorSampler3d, false)) }
    fun textureCube(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslColorSamplerCube, false)) }

    fun depthTexture2d(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslDepthSampler2D, false)) }
    fun depthTextureCube(name: String) = getOrCreateSampler(name) { KslUniform(KslVar(name, KslDepthSamplerCube, false)) }

    // arrays of textures (this is different to array textures, like, e.g., KslTypeColorSampler2dArray)
    fun textureArray1d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslColorSampler1d, arraySize, false)) }
    fun textureArray2d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslColorSampler2d, arraySize, false)) }
    fun textureArray3d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslColorSampler3d, arraySize, false)) }
    fun textureArrayCube(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslColorSamplerCube, arraySize, false)) }

    // arrays of depth textures (this is different to array textures, like, e.g., KslTypeDepthSampler2dArray)
    fun depthTextureArray2d(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslDepthSampler2D, arraySize, false)) }
    fun depthTextureArrayCube(name: String, arraySize: Int) = getOrCreateSampler(name) { KslUniformArray(KslArrayGeneric(name, KslDepthSamplerCube, arraySize, false)) }

    inline fun <reified T: KslNumericType> storage1d(name: String): KslStorage1d<KslStorage1dType<T>> {
        val storage: KslStorage<*,*> = uniformStorage[name] ?: (
                if (KslFloat1 is T) KslStorage1d(name, KslStorage1dType<KslFloat1>(KslFloat1))
                else if (KslFloat2 is T) KslStorage1d(name, KslStorage1dType<KslFloat2>(KslFloat2))
                else if (KslFloat3 is T) KslStorage1d(name, KslStorage1dType<KslFloat3>(KslFloat3))
                else if (KslFloat4 is T) KslStorage1d(name, KslStorage1dType<KslFloat4>(KslFloat4))

                else if (KslInt1 is T)   KslStorage1d(name, KslStorage1dType<KslInt1>(KslInt1))
                else if (KslInt2 is T)   KslStorage1d(name, KslStorage1dType<KslInt2>(KslInt2))
                else if (KslInt3 is T)   KslStorage1d(name, KslStorage1dType<KslInt3>(KslInt3))
                else if (KslInt4 is T)   KslStorage1d(name, KslStorage1dType<KslInt4>(KslInt4))

                else if (KslUint1 is T)  KslStorage1d(name, KslStorage1dType<KslUint1>(KslUint1))
                else if (KslUint2 is T)  KslStorage1d(name, KslStorage1dType<KslUint2>(KslUint2))
                else if (KslUint3 is T)  KslStorage1d(name, KslStorage1dType<KslUint3>(KslUint3))
                else if (KslUint4 is T)  KslStorage1d(name, KslStorage1dType<KslUint4>(KslUint4))

                else throw IllegalArgumentException("Unsupported storage type")
                ).also { registerStorage(it) }

        check(storage is KslStorage1d<*> && storage.storageType.elemType is T) {
            "Existing uniform with name \"$name\" has not the expected type"
        }

        @Suppress("UNCHECKED_CAST")
        return storage as KslStorage1d<KslStorage1dType<T>>
    }

    inline fun <reified T: KslNumericType> storage2d(name: String): KslStorage2d<KslStorage2dType<T>> {
        val storage: KslStorage<*,*> = uniformStorage[name] ?: (
                if (KslFloat1 is T) KslStorage2d(name, KslStorage2dType<KslFloat1>(KslFloat1))
                else if (KslFloat2 is T) KslStorage2d(name, KslStorage2dType<KslFloat2>(KslFloat2))
                else if (KslFloat3 is T) KslStorage2d(name, KslStorage2dType<KslFloat3>(KslFloat3))
                else if (KslFloat4 is T) KslStorage2d(name, KslStorage2dType<KslFloat4>(KslFloat4))

                else if (KslInt1 is T)   KslStorage2d(name, KslStorage2dType<KslInt1>(KslInt1))
                else if (KslInt2 is T)   KslStorage2d(name, KslStorage2dType<KslInt2>(KslInt2))
                else if (KslInt3 is T)   KslStorage2d(name, KslStorage2dType<KslInt3>(KslInt3))
                else if (KslInt4 is T)   KslStorage2d(name, KslStorage2dType<KslInt4>(KslInt4))

                else if (KslUint1 is T)  KslStorage2d(name, KslStorage2dType<KslUint1>(KslUint1))
                else if (KslUint2 is T)  KslStorage2d(name, KslStorage2dType<KslUint2>(KslUint2))
                else if (KslUint3 is T)  KslStorage2d(name, KslStorage2dType<KslUint3>(KslUint3))
                else if (KslUint4 is T)  KslStorage2d(name, KslStorage2dType<KslUint4>(KslUint4))

                else throw IllegalArgumentException("Unsupported storage type")
                ).also { registerStorage(it) }

        check(storage is KslStorage2d<*> && storage.storageType.elemType is T) {
            "Existing uniform with name \"$name\" has not the expected type"
        }

        @Suppress("UNCHECKED_CAST")
        return storage as KslStorage2d<KslStorage2dType<T>>
    }

    inline fun <reified T: KslNumericType> storage3d(name: String): KslStorage3d<KslStorage3dType<T>> {
        val storage: KslStorage<*,*> = uniformStorage[name] ?: (
                if (KslFloat1 is T) KslStorage3d(name, KslStorage3dType<KslFloat1>(KslFloat1))
                else if (KslFloat2 is T) KslStorage3d(name, KslStorage3dType<KslFloat2>(KslFloat2))
                else if (KslFloat3 is T) KslStorage3d(name, KslStorage3dType<KslFloat3>(KslFloat3))
                else if (KslFloat4 is T) KslStorage3d(name, KslStorage3dType<KslFloat4>(KslFloat4))

                else if (KslInt1 is T)   KslStorage3d(name, KslStorage3dType<KslInt1>(KslInt1))
                else if (KslInt2 is T)   KslStorage3d(name, KslStorage3dType<KslInt2>(KslInt2))
                else if (KslInt3 is T)   KslStorage3d(name, KslStorage3dType<KslInt3>(KslInt3))
                else if (KslInt4 is T)   KslStorage3d(name, KslStorage3dType<KslInt4>(KslInt4))

                else if (KslUint1 is T)  KslStorage3d(name, KslStorage3dType<KslUint1>(KslUint1))
                else if (KslUint2 is T)  KslStorage3d(name, KslStorage3dType<KslUint2>(KslUint2))
                else if (KslUint3 is T)  KslStorage3d(name, KslStorage3dType<KslUint3>(KslUint3))
                else if (KslUint4 is T)  KslStorage3d(name, KslStorage3dType<KslUint4>(KslUint4))

                else throw IllegalArgumentException("Unsupported storage type")
                ).also { registerStorage(it) }

        check(storage is KslStorage3d<*> && storage.storageType.elemType is T) {
            "Existing uniform with name \"$name\" has not the expected type"
        }

        @Suppress("UNCHECKED_CAST")
        return storage as KslStorage3d<KslStorage3dType<T>>
    }

    private fun registerInterStageVar(interStageVar: KslInterStageVar<*>) {
        // make sure vertex and fragment stage are created
        vertexStage {  }
        fragmentStage {  }

        stages.forEach {
            it.interStageVars += interStageVar
            if (it.type == KslShaderStageType.VertexShader) {
                it.globalScope.definedStates += interStageVar.input
            } else if (it.type == KslShaderStageType.FragmentShader) {
                it.globalScope.definedStates += interStageVar.output
            }
        }
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
        interStageScalar(KslFloat1, interpolation, name ?: nextName("interStageF1"))
    fun interStageFloat2(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslFloat2, interpolation, name ?: nextName("interStageF2"))
    fun interStageFloat3(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslFloat3, interpolation, name ?: nextName("interStageF3"))
    fun interStageFloat4(name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVector(KslFloat4, interpolation, name ?: nextName("interStageF4"))

    fun interStageInt1(name: String? = null) = interStageScalar(KslInt1, KslInterStageInterpolation.Flat, name ?: nextName("interStageI1"))
    fun interStageInt2(name: String? = null) = interStageVector(KslInt2, KslInterStageInterpolation.Flat, name ?: nextName("interStageI2"))
    fun interStageInt3(name: String? = null) = interStageVector(KslInt3, KslInterStageInterpolation.Flat, name ?: nextName("interStageI3"))
    fun interStageInt4(name: String? = null) = interStageVector(KslInt4, KslInterStageInterpolation.Flat, name ?: nextName("interStageI4"))

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
        interStageScalarArray(KslFloat1, arraySize, interpolation, name ?: nextName("interStageF1Array"))
    fun interStageFloat2Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslFloat2, arraySize, interpolation, name ?: nextName("interStageF2Array"))
    fun interStageFloat3Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslFloat3, arraySize, interpolation, name ?: nextName("interStageF3Array"))
    fun interStageFloat4Array(arraySize: Int, name: String? = null, interpolation: KslInterStageInterpolation = KslInterStageInterpolation.Smooth) =
        interStageVectorArray(KslFloat4, arraySize, interpolation, name ?: nextName("interStageF4Array"))

    fun interStageInt1Array(arraySize: Int, name: String? = null) =
        interStageScalarArray(KslInt1, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI1Array"))
    fun interStageInt2Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslInt2, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI2Array"))
    fun interStageInt3Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslInt3, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI3Array"))
    fun interStageInt4Array(arraySize: Int, name: String? = null) =
        interStageVectorArray(KslInt4, arraySize, KslInterStageInterpolation.Flat, name ?: nextName("interStageI4Array"))

    fun prepareGenerate() {
        if (!isPrepared) {
            isPrepared = true

            stages.forEach { it.prepareGenerate() }

            // remove unused uniforms
            uniformBuffers.filter { !it.isShared }.forEach {
                it.uniforms.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u) } }
            }
            uniformBuffers.removeAll { it.uniforms.isEmpty() }

            // remove unused texture samplers
            uniformSamplers.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u) } }

            // remove unused storage
            uniformStorage.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u) } }
        }
    }

    val KslShader.UniformInput1f.ksl: KslUniformScalar<KslFloat1> get() = uniformFloat1(uniformName)
    val KslShader.UniformInput2f.ksl: KslUniformVector<KslFloat2, KslFloat1> get() = uniformFloat2(uniformName)
    val KslShader.UniformInput3f.ksl: KslUniformVector<KslFloat3, KslFloat1> get() = uniformFloat3(uniformName)
    val KslShader.UniformInput4f.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(uniformName)
    val KslShader.UniformInput1fv.ksl: KslUniformScalarArray<KslFloat1> get() = uniformFloat1Array(uniformName, arraySize)
    val KslShader.UniformInput2fv.ksl: KslUniformVectorArray<KslFloat2, KslFloat1> get() = uniformFloat2Array(uniformName, arraySize)
    val KslShader.UniformInput3fv.ksl: KslUniformVectorArray<KslFloat3, KslFloat1> get() = uniformFloat3Array(uniformName, arraySize)
    val KslShader.UniformInput4fv.ksl: KslUniformVectorArray<KslFloat4, KslFloat1> get() = uniformFloat4Array(uniformName, arraySize)

    val KslShader.UniformInputColor.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(uniformName)
    val KslShader.UniformInputQuat.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(uniformName)

    val KslShader.UniformInput1i.ksl: KslUniformScalar<KslInt1> get() = uniformInt1(uniformName)
    val KslShader.UniformInput2i.ksl: KslUniformVector<KslInt2, KslInt1> get() = uniformInt2(uniformName)
    val KslShader.UniformInput3i.ksl: KslUniformVector<KslInt3, KslInt1> get() = uniformInt3(uniformName)
    val KslShader.UniformInput4i.ksl: KslUniformVector<KslInt4, KslInt1> get() = uniformInt4(uniformName)
    val KslShader.UniformInput1iv.ksl: KslUniformScalarArray<KslInt1> get() = uniformInt1Array(uniformName, arraySize)
    val KslShader.UniformInput2iv.ksl: KslUniformVectorArray<KslInt2, KslInt1> get() = uniformInt2Array(uniformName, arraySize)
    val KslShader.UniformInput3iv.ksl: KslUniformVectorArray<KslInt3, KslInt1> get() = uniformInt3Array(uniformName, arraySize)
    val KslShader.UniformInput4iv.ksl: KslUniformVectorArray<KslInt4, KslInt1> get() = uniformInt4Array(uniformName, arraySize)

    val KslShader.UniformInputMat3f.ksl: KslUniformMatrix<KslMat3, KslFloat3> get() = uniformMat3(uniformName)
    val KslShader.UniformInputMat4f.ksl: KslUniformMatrix<KslMat4, KslFloat4> get() = uniformMat4(uniformName)
    val KslShader.UniformInputMat3fv.ksl: KslUniformMatrixArray<KslMat3, KslFloat3> get() = uniformMat3Array(uniformName, arraySize)
    val KslShader.UniformInputMat4fv.ksl: KslUniformMatrixArray<KslMat4, KslFloat4> get() = uniformMat4Array(uniformName, arraySize)

    val KslShader.UniformInputTexture1d.ksl: KslUniform<KslColorSampler1d> get() = texture1d(uniformName)
    val KslShader.UniformInputTexture2d.ksl: KslUniform<KslColorSampler2d> get() = texture2d(uniformName)
    val KslShader.UniformInputTexture3d.ksl: KslUniform<KslColorSampler3d> get() = texture3d(uniformName)
    val KslShader.UniformInputTextureCube.ksl: KslUniform<KslColorSamplerCube> get() = textureCube(uniformName)

    val KslShader.UniformInputTextureArray1d.ksl: KslUniformArray<KslColorSampler1d> get() = textureArray1d(uniformName, arrSize)
    val KslShader.UniformInputTextureArray2d.ksl: KslUniformArray<KslColorSampler2d> get() = textureArray2d(uniformName, arrSize)
    val KslShader.UniformInputTextureArray3d.ksl: KslUniformArray<KslColorSampler3d> get() = textureArray3d(uniformName, arrSize)
    val KslShader.UniformInputTextureArrayCube.ksl: KslUniformArray<KslColorSamplerCube> get() = textureArrayCube(uniformName, arrSize)
}