package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.pipeline.*
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
    @PublishedApi
    internal fun nextName(prefix: String): String = "${prefix}_${nextNameIdx++}"

    val commonUniformBuffer = KslUniformBuffer("CommonUniforms", this, BindGroupScope.PIPELINE)
    val uniformBuffers = mutableListOf(commonUniformBuffer)
    val uniformSamplers = mutableMapOf<String, SamplerUniform>()
    val storageBuffers = mutableMapOf<String, KslStorage<*,*>>()
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
        uniformSamplers.values.forEach { stage.globalScope.definedStates += it.sampler.value }
    }

    private fun registerSampler(sampler : SamplerUniform) {
        uniformSamplers[sampler.sampler.name] = sampler
        stages.forEach {
            it.globalScope.definedStates += sampler.sampler.value
        }
    }

    @PublishedApi internal fun registerStorage(storage: KslStorage<*,*>) {
        storageBuffers[storage.name] = storage
        stages.forEach {
            it.globalScope.definedStates += storage
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateSampler(name: String, sampleType: TextureSampleType, create: () -> T): T {
        val uniform = uniformSamplers[name]?.sampler ?: create().also { registerSampler(SamplerUniform(it, sampleType)) }
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

    fun texture1d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler1d, false)) }
    fun texture2d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler2d, false)) }
    fun texture3d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler3d, false)) }
    fun textureCube(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSamplerCube, false)) }

    fun depthTexture2d(name: String) = getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSampler2d, false)) }
    fun depthTextureCube(name: String) = getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSamplerCube, false)) }

    inline fun <reified T: KslNumericType> storage1d(
        name: String,
        size: Int? = null,
        accessType: StorageAccessType = StorageAccessType.READ_WRITE
    ): KslStorage1d<KslStorage1dType<T>> {
        val type = numericTypeForT<T>()
        val storage: KslStorage<*,*> = storageBuffers[name]
            ?: KslStorage1d(name, KslStorage1dType(type), size, accessType).also { registerStorage(it) }

        check(storage is KslStorage1d<*> && storage.storageType.elemType == type) {
            "Existing storage buffer with name \"$name\" has not the expected type"
        }
        check(storage.sizeX == size) {
            "Existing storage buffer with name \"$name\" has not the expected dimension: ${storage.sizeX} != $size"
        }
        check(type != KslFloat3 && type != KslInt3 && type != KslUint3) {
            "3-dimensional storage buffer element types are not supported (use 4 dimensions instead)"
        }
        @Suppress("UNCHECKED_CAST")
        return storage as KslStorage1d<KslStorage1dType<T>>
    }

    inline fun <reified T: KslNumericType> storage2d(
        name: String,
        sizeX: Int,
        sizeY: Int? = null,
        accessType: StorageAccessType = StorageAccessType.READ_WRITE
    ): KslStorage2d<KslStorage2dType<T>> {
        val type = numericTypeForT<T>()
        val storage: KslStorage<*,*> = storageBuffers[name]
            ?: KslStorage2d(name, KslStorage2dType(type), sizeX, sizeY, accessType).also { registerStorage(it) }

        check(storage is KslStorage2d<*> && type == storage.storageType.elemType) {
            "Existing storage buffer with name \"$name\" has not the expected type"
        }
        check(storage.sizeX == sizeX && storage.sizeY == sizeY) {
            "Existing storage buffer with name \"$name\" has not the expected dimension: (${storage.sizeX}, ${storage.sizeY}) != ($sizeX, $sizeY)"
        }
        check(type != KslFloat3 && type != KslInt3 && type != KslUint3) {
            "3-dimensional storage buffer element types are not supported (use 4 dimensions instead)"
        }
        @Suppress("UNCHECKED_CAST")
        return storage as KslStorage2d<KslStorage2dType<T>>
    }

    inline fun <reified T: KslNumericType> storage3d(
        name: String,
        sizeX: Int,
        sizeY: Int,
        sizeZ: Int? = null,
        accessType: StorageAccessType = StorageAccessType.READ_WRITE
    ): KslStorage3d<KslStorage3dType<T>> {
        val type = numericTypeForT<T>()
        val storage: KslStorage<*,*> = storageBuffers[name]
            ?: KslStorage3d(name, KslStorage3dType(type), sizeX, sizeY, sizeZ, accessType).also { registerStorage(it) }

        check(storage is KslStorage3d<*> && storage.storageType.elemType == type) {
            "Existing storage buffer with name \"$name\" has not the expected type"
        }
        check(storage.sizeX == sizeX && storage.sizeY == sizeY && storage.sizeZ == sizeZ) {
            "Existing storage buffer with name \"$name\" has not the expected dimension: (${storage.sizeX}, ${storage.sizeY}, ${storage.sizeZ}) != ($sizeX, $sizeY, $sizeZ)"
        }
        check(type != KslFloat3 && type != KslInt3 && type != KslUint3) {
            "3-dimensional storage buffer element types are not supported (use 4 dimensions instead)"
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

            // filter uniforms:
            // - remove unused uniforms from non-shared buffers
            uniformBuffers.filter { ubo -> ubo.scope == BindGroupScope.PIPELINE }.forEach {
                it.uniforms.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u) } }
            }
            // - remove empty and completely unused uniform buffers
            uniformBuffers.removeAll { ubo ->
                ubo.uniforms.isEmpty() || ubo.uniforms.values.none { u -> stages.any { it.dependsOn(u) } }
            }

            // remove unused texture samplers
            uniformSamplers.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u.sampler) } }

            // remove unused storage
            storageBuffers.values.retainAll { u -> stages.any { stage -> stage.dependsOn(u) } }
        }
    }

    val UniformBinding1f.ksl: KslUniformScalar<KslFloat1> get() = uniformFloat1(bindingName)
    val UniformBinding2f.ksl: KslUniformVector<KslFloat2, KslFloat1> get() = uniformFloat2(bindingName)
    val UniformBinding3f.ksl: KslUniformVector<KslFloat3, KslFloat1> get() = uniformFloat3(bindingName)
    val UniformBinding4f.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(bindingName)
    val UniformBinding1fv.ksl: KslUniformScalarArray<KslFloat1> get() = uniformFloat1Array(bindingName, arraySize)
    val UniformBinding2fv.ksl: KslUniformVectorArray<KslFloat2, KslFloat1> get() = uniformFloat2Array(bindingName, arraySize)
    val UniformBinding3fv.ksl: KslUniformVectorArray<KslFloat3, KslFloat1> get() = uniformFloat3Array(bindingName, arraySize)
    val UniformBinding4fv.ksl: KslUniformVectorArray<KslFloat4, KslFloat1> get() = uniformFloat4Array(bindingName, arraySize)

    val UniformBindingColor.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(bindingName)
    val UniformBindingQuat.ksl: KslUniformVector<KslFloat4, KslFloat1> get() = uniformFloat4(bindingName)

    val UniformBinding1i.ksl: KslUniformScalar<KslInt1> get() = uniformInt1(bindingName)
    val UniformBinding2i.ksl: KslUniformVector<KslInt2, KslInt1> get() = uniformInt2(bindingName)
    val UniformBinding3i.ksl: KslUniformVector<KslInt3, KslInt1> get() = uniformInt3(bindingName)
    val UniformBinding4i.ksl: KslUniformVector<KslInt4, KslInt1> get() = uniformInt4(bindingName)
    val UniformBinding1iv.ksl: KslUniformScalarArray<KslInt1> get() = uniformInt1Array(bindingName, arraySize)
    val UniformBinding2iv.ksl: KslUniformVectorArray<KslInt2, KslInt1> get() = uniformInt2Array(bindingName, arraySize)
    val UniformBinding3iv.ksl: KslUniformVectorArray<KslInt3, KslInt1> get() = uniformInt3Array(bindingName, arraySize)
    val UniformBinding4iv.ksl: KslUniformVectorArray<KslInt4, KslInt1> get() = uniformInt4Array(bindingName, arraySize)

    val UniformBindingMat3f.ksl: KslUniformMatrix<KslMat3, KslFloat3> get() = uniformMat3(bindingName)
    val UniformBindingMat4f.ksl: KslUniformMatrix<KslMat4, KslFloat4> get() = uniformMat4(bindingName)
    val UniformBindingMat3fv.ksl: KslUniformMatrixArray<KslMat3, KslFloat3> get() = uniformMat3Array(bindingName, arraySize)
    val UniformBindingMat4fv.ksl: KslUniformMatrixArray<KslMat4, KslFloat4> get() = uniformMat4Array(bindingName, arraySize)

    val Texture1dBinding.ksl: KslUniform<KslColorSampler1d> get() = texture1d(bindingName)
    val Texture2dBinding.ksl: KslUniform<KslColorSampler2d> get() = texture2d(bindingName)
    val Texture3dBinding.ksl: KslUniform<KslColorSampler3d> get() = texture3d(bindingName)
    val TextureCubeBinding.ksl: KslUniform<KslColorSamplerCube> get() = textureCube(bindingName)

    data class SamplerUniform(val sampler: KslUniform<*>, val sampleType: TextureSampleType)
}