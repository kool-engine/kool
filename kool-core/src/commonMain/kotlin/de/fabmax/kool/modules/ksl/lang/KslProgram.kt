package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.KslShaderListener
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.util.Struct
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

open class KslProgram(val name: String) {

    /**
     * Debug property: if true generated shader code is dumped to console
     */
    var dumpCode = false

    var optimizeExpressions = true

    var isPrepared = false
        private set

    private var nextNameIdx = 1
    @PublishedApi
    internal fun nextName(prefix: String): String = "${prefix}_${nextNameIdx++}"

    val commonUniformBuffer = KslUniformBuffer("CommonUniforms", this, BindGroupScope.PIPELINE)
    val uniformBuffers = mutableListOf(commonUniformBuffer)
    val uniformStructs = mutableMapOf<String, KslUniformStruct<*>>()
    val uniformSamplers = mutableMapOf<String, SamplerUniform>()
    val structs = mutableMapOf<String, Struct>()
    val storageBuffers = mutableMapOf<String, KslStorage<*>>()
    val storageTextures = mutableMapOf<String, KslStorageTexture<*,*,*>>()
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
        uniformStructs.values.forEach { stage.globalScope.definedStates += it.value }
        uniformSamplers.values.forEach { stage.globalScope.definedStates += it.sampler.value }
        storageBuffers.values.forEach { stage.globalScope.definedStates += it }
        storageTextures.values.forEach { stage.globalScope.definedStates += it }
    }

    @PublishedApi
    internal fun registerUniformStruct(struct: KslUniformStruct<*>) {
        uniformStructs[struct.name] = struct
        registerStruct(struct.value.expressionType.proto)
        stages.forEach {
            it.globalScope.definedStates += struct.value
        }
    }

    private fun registerSampler(sampler : SamplerUniform) {
        uniformSamplers[sampler.sampler.name] = sampler
        stages.forEach {
            it.globalScope.definedStates += sampler.sampler.value
        }
    }

    @PublishedApi
    internal fun registerStruct(struct: Struct) {
        struct.members.filterIsInstance<Struct>().forEach { registerStruct(it) }
        struct.members.filterIsInstance<Struct.NestedStructArrayMember<*>>().forEach { registerStruct(it.struct) }

        val existing = structs.getOrPut(struct.structName) { struct }
        check(struct::class == existing::class) {
            "Existing struct with name $name has type ${existing::class.simpleName} but given struct is ${struct::class.simpleName}"
        }
    }

    @PublishedApi
    internal fun registerStorage(storage: KslStorage<*>) {
        storageBuffers[storage.name] = storage
        stages.forEach {
            it.globalScope.definedStates += storage
        }
    }

    @PublishedApi
    internal fun registerStorageTexture(storageTexture: KslStorageTexture<*,*,*>) {
        storageTextures[storageTexture.name] = storageTexture
        stages.forEach {
            it.globalScope.definedStates += storageTexture
        }
    }

    private inline fun <reified T: KslUniform<*>> getOrCreateSampler(name: String, sampleType: TextureSampleType, create: () -> T): T {
        val uniform = uniformSamplers[name]?.sampler ?: create().also { registerSampler(SamplerUniform(it, sampleType)) }
        check(uniform is T) {
            "Existing uniform with name \"$name\" has not the expected type"
        }
        return uniform
    }

    @PublishedApi
    internal inline fun <reified S: Struct> getOrCreateStructUniform(
        name: String,
        scope: BindGroupScope = BindGroupScope.PIPELINE,
        noinline provider: () -> S
    ): KslUniformStruct<S> {
        val proto = provider()
        val uniform = uniformStructs[proto.structName] ?: KslUniformStruct(name, scope, provider).also { registerUniformStruct(it) }
        check(uniform.proto is S) {
            "Existing struct uniform with name \"$name\" has not the expected struct type"
        }
        @Suppress("UNCHECKED_CAST")
        return uniform as KslUniformStruct<S>
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

    inline fun <reified S: Struct> uniformStruct(name: String, scope: BindGroupScope = BindGroupScope.PIPELINE, noinline provider: () -> S): KslUniformStruct<S> =
        getOrCreateStructUniform(name, scope, provider)

    fun texture1d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler1d, false)) }
    fun texture2d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler2d, false)) }
    fun texture3d(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler3d, false)) }
    fun textureCube(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSamplerCube, false)) }
    fun texture2dArray(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSampler2dArray, false)) }
    fun textureCubeArray(name: String, sampleType: TextureSampleType = TextureSampleType.FLOAT) =
        getOrCreateSampler(name, sampleType) { KslUniform(KslVar(name, KslColorSamplerCubeArray, false)) }

    fun depthTexture2d(name: String) =
        getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSampler2d, false)) }
    fun depthTextureCube(name: String) =
        getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSamplerCube, false)) }
    fun depthTexture2dArray(name: String) =
        getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSampler2dArray, false)) }
    fun depthTextureCubeArray(name: String) =
        getOrCreateSampler(name, TextureSampleType.DEPTH) { KslUniform(KslVar(name, KslDepthSamplerCubeArray, false)) }

    fun <T: Struct> struct(provider: () -> T): KslStruct<T> {
        registerStruct(provider())
        return KslStruct<T>(provider)
    }

    fun <T: Struct> storage(
        name: String,
        structType: KslStruct<T>,
        size: Int? = null
    ): KslStructStorage<T> {
        val storage: KslStorage<*> = storageBuffers[name]
            ?: KslStructStorage(name, structType, size).also { registerStorage(it) }

        check(storage is KslStructStorage<*> && storage.storageType.elemType == structType) {
            "Existing storage buffer with name \"$name\" has not the expected type"
        }
        check(storage.size == size) {
            "Existing storage buffer with name \"$name\" has not the expected dimension: ${storage.size} != $size"
        }
        @Suppress("UNCHECKED_CAST")
        return storage as KslStructStorage<T>
    }

    inline fun <reified T: KslNumericType> storage(
        name: String,
        size: Int? = null
    ): KslPrimitiveStorage<KslPrimitiveStorageType<T>> {
        val type = numericTypeForT<T>()
        val storage: KslStorage<*> = storageBuffers[name]
            ?: KslPrimitiveStorage(name, KslPrimitiveStorageType(type), size).also { registerStorage(it) }

        check(storage is KslPrimitiveStorage<*> && storage.storageType.elemType == type) {
            "Existing storage buffer with name \"$name\" has not the expected type"
        }
        check(storage.size == size) {
            "Existing storage buffer with name \"$name\" has not the expected dimension: ${storage.size} != $size"
        }
        check(type != KslFloat3 && type != KslInt3 && type != KslUint3) {
            "3-dimensional storage buffer element types are not supported (use 4 dimensions instead)"
        }
        @Suppress("UNCHECKED_CAST")
        return storage as KslPrimitiveStorage<KslPrimitiveStorageType<T>>
    }

    inline fun <reified T: KslNumericType> storageTexture1d(
        name: String,
        texFormat: TexFormat
    ): KslStorageTexture1d<KslStorageTexture1dType<T>, T> {
        val type = numericTypeForT<T>()
        val storage: KslStorageTexture<*,*,*> = storageTextures[name]
            ?: KslStorageTexture1d(name, KslStorageTexture1dType(type), texFormat).also { registerStorageTexture(it) }

        checkStorageTexType<KslStorageTexture1d<KslStorageTexture1dType<T>, T>>(storage, type, texFormat)
        @Suppress("UNCHECKED_CAST")
        return storage as KslStorageTexture1d<KslStorageTexture1dType<T>, T>
    }

    inline fun <reified T: KslNumericType> storageTexture2d(
        name: String,
        texFormat: TexFormat
    ): KslStorageTexture2d<KslStorageTexture2dType<T>, T> {
        val type = numericTypeForT<T>()
        val storage: KslStorageTexture<*,*,*> = storageTextures[name]
            ?: KslStorageTexture2d(name, KslStorageTexture2dType(type), texFormat).also { registerStorageTexture(it) }

        checkStorageTexType<KslStorageTexture2d<KslStorageTexture2dType<T>, T>>(storage, type, texFormat)
        @Suppress("UNCHECKED_CAST")
        return storage as KslStorageTexture2d<KslStorageTexture2dType<T>, T>
    }

    inline fun <reified T: KslNumericType> storageTexture3d(
        name: String,
        texFormat: TexFormat
    ): KslStorageTexture3d<KslStorageTexture3dType<T>, T> {
        val type = numericTypeForT<T>()
        val storage: KslStorageTexture<*,*,*> = storageTextures[name]
            ?: KslStorageTexture3d(name, KslStorageTexture3dType(type), texFormat).also { registerStorageTexture(it) }

        checkStorageTexType<KslStorageTexture3d<KslStorageTexture3dType<T>, T>>(storage, type, texFormat)
        @Suppress("UNCHECKED_CAST")
        return storage as KslStorageTexture3d<KslStorageTexture3dType<T>, T>
    }

    @PublishedApi
    internal inline fun <reified T: KslStorageTexture<*,*,*>> checkStorageTexType(
        storage: KslStorageTexture<*,*,*>,
        type: KslNumericType,
        texFormat: TexFormat
    ) {
        check(storage is T && type == storage.storageType.elemType) {
            "Existing storage texture with name \"$name\" has not the expected type"
        }
        check((type is KslScalar && texFormat.channels == 1) || (type is KslVector<*> && texFormat.channels == type.dimens)) {
            "Ksl type $type does not match dimensionality of texture format $texFormat"
        }
        check(((texFormat.isI32 || texFormat.isU32) && type is KslIntType) || ((texFormat.isFloat || texFormat.isByte) && type is KslFloatType)) {
            "Ksl type $type does not match channel type of texture format $texFormat"
        }
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

    val UniformBindingMat2f.ksl: KslUniformMatrix<KslMat2, KslFloat2> get() = uniformMat2(bindingName)
    val UniformBindingMat3f.ksl: KslUniformMatrix<KslMat3, KslFloat3> get() = uniformMat3(bindingName)
    val UniformBindingMat4f.ksl: KslUniformMatrix<KslMat4, KslFloat4> get() = uniformMat4(bindingName)
    val UniformBindingMat2fv.ksl: KslUniformMatrixArray<KslMat2, KslFloat2> get() = uniformMat2Array(bindingName, arraySize)
    val UniformBindingMat3fv.ksl: KslUniformMatrixArray<KslMat3, KslFloat3> get() = uniformMat3Array(bindingName, arraySize)
    val UniformBindingMat4fv.ksl: KslUniformMatrixArray<KslMat4, KslFloat4> get() = uniformMat4Array(bindingName, arraySize)

    val Texture1dBinding.ksl: KslUniform<KslColorSampler1d> get() = texture1d(bindingName)
    val Texture2dBinding.ksl: KslUniform<KslColorSampler2d> get() = texture2d(bindingName)
    val Texture3dBinding.ksl: KslUniform<KslColorSampler3d> get() = texture3d(bindingName)
    val TextureCubeBinding.ksl: KslUniform<KslColorSamplerCube> get() = textureCube(bindingName)

    data class SamplerUniform(val sampler: KslUniform<*>, val sampleType: TextureSampleType)
}