package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.model.KslTransformer
import de.fabmax.kool.pipeline.ShaderStage
import de.fabmax.kool.util.Struct
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class KslShaderStage(val program: KslProgram, val type: KslShaderStageType) {

    val interStageVars = mutableListOf<KslInterStageVar<*>>()
    val globalVars = mutableMapOf<String, KslVar<*>>()
    val functions = mutableMapOf<String, KslFunction<*>>()
    val globalScope = KslScopeBuilder(null, null, this)

    private val mainFunc = KslFunction<KslTypeVoid>("main", KslTypeVoid, this)
    val main: KslScopeBuilder get() = mainFunc.body

    var generatorExpressions = emptyMap<KslExpression<*>, KslExpression<*>>()
        private set

    init {
        globalScope.scopeName = "global"
        globalScope.ops += mainFunc.functionRoot
    }

    fun main(block: KslScopeBuilder.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        main.apply(block)
    }

    fun addFunction(name: String, function: KslFunction<*>) {
        if (name in functions.keys) {
            error("Function with name $name is already defined")
        }
        functions[name] = function
        globalScope.ops += function.functionRoot
    }

    inline fun <reified T: KslFunction<*>> getOrCreateFunction(name: String, createFunc: () -> T): T {
        return (functions[name] ?: createFunc().also { addFunction(name, it) }) as T
    }

    inline fun <reified T: KslBlock> findBlock(name: String? = null): T? {
        return main.getBlocks(name, mutableListOf()).find { it is T } as? T
    }

    fun dependsOn(uniform: KslUniform<*>): Boolean {
        return uniform.value in main.dependencies || functions.values.any { f -> uniform.value in f.body.dependencies }
    }

    fun dependsOn(storage: KslStorage<*>): Boolean {
        return storage in main.dependencies || functions.values.any { f -> storage in f.body.dependencies }
    }

    fun dependsOn(storage: KslStorageTexture<*, *, *>): Boolean {
        return storage in main.dependencies || functions.values.any { f -> storage in f.body.dependencies }
    }

    fun getUsedSamplers(): List<KslUniform<*>> {
        return program.uniformSamplers.values.map { it.sampler }.filter { dependsOn(it) }
    }

    fun getUsedStructs(): List<Struct> {
        return program.structs.values.toList()  // todo: .filter { dependsOn(it) }
    }

    fun getUsedStorage(): List<KslStorage<*>> {
        return program.storageBuffers.values.filter { dependsOn(it) }
    }

    fun getUsedStorageTextures(): List<KslStorageTexture<*, *, *>> {
        return program.storageTextures.values.filter { dependsOn(it) }
    }

    fun getUsedUbos(): List<KslUniformBuffer> {
        return program.uniformBuffers.filter { ubo ->
            ubo.uniforms.values.any { dependsOn(it) }
        }
    }

    fun getUsedUboStructs(): List<KslUniformStruct<*>> {
        return program.uniformStructs.values.filter { ubo -> dependsOn(ubo) }
    }

    fun prepareGenerate() {
        generatorExpressions = KslTransformer.transform(this, program.optimizeExpressions).generatorExpressions
    }

    private fun <S> getOrCreateGlobalScalar(name: String, type: S): KslVarScalar<S> where S : KslType, S : KslScalar {
        val kslVar = globalVars.getOrPut(name) {
            KslVarScalar(name, type, true).also { kslVar -> globalScope.definedStates += kslVar }
        }
        check(kslVar.expressionType == type) { "Existing global var with name \"$name\" has not the expected type" }
        @Suppress("UNCHECKED_CAST")
        return kslVar as KslVarScalar<S>
    }

    private fun <V, S> getOrCreateGlobalVector(name: String, type: V): KslVarVector<V, S> where V : KslType, V : KslVector<S>, S : KslScalar {
        val kslVar = globalVars.getOrPut(name) {
            KslVarVector(name, type, true).also { kslVar -> globalScope.definedStates += kslVar }
        }
        check(kslVar.expressionType == type) { "Existing global var with name \"$name\" has not the expected type" }
        @Suppress("UNCHECKED_CAST")
        return kslVar as KslVarVector<V, S>
    }

    private fun <M, V> getOrCreateGlobalMatrix(name: String, type: M): KslVarMatrix<M, V> where M : KslType, M : KslMatrix<V>, V : KslVector<*> {
        val kslVar = globalVars.getOrPut(name) {
            KslVarMatrix(name, type, true).also { kslVar -> globalScope.definedStates += kslVar }
        }
        check(kslVar.expressionType == type) { "Existing global var with name \"$name\" has not the expected type" }
        @Suppress("UNCHECKED_CAST")
        return kslVar as KslVarMatrix<M, V>
    }

    private fun <T> getOrCreateGlobalStruct(name: String, type: KslStruct<T>): KslVarStruct<T> where T : Struct {
        val kslVar = globalVars.getOrPut(name) {
            KslVarStruct(name, type, true).also { kslVar -> globalScope.definedStates += kslVar }
        }
        check(kslVar.expressionType == type) { "Existing global var with name \"$name\" has not the expected type" }
        @Suppress("UNCHECKED_CAST")
        return kslVar as KslVarStruct<T>
    }

    fun globalFloat1(name: String) = getOrCreateGlobalScalar(name, KslFloat1)
    fun globalFloat2(name: String) = getOrCreateGlobalVector(name, KslFloat2)
    fun globalFloat3(name: String) = getOrCreateGlobalVector(name, KslFloat3)
    fun globalFloat4(name: String) = getOrCreateGlobalVector(name, KslFloat4)

    fun globalInt1(name: String) = getOrCreateGlobalScalar(name, KslInt1)
    fun globalInt2(name: String) = getOrCreateGlobalVector(name, KslInt2)
    fun globalInt3(name: String) = getOrCreateGlobalVector(name, KslInt3)
    fun globalInt4(name: String) = getOrCreateGlobalVector(name, KslInt4)

    fun globalUint1(name: String) = getOrCreateGlobalScalar(name, KslUint1)
    fun globalUint2(name: String) = getOrCreateGlobalVector(name, KslUint2)
    fun globalUint3(name: String) = getOrCreateGlobalVector(name, KslUint3)
    fun globalUint4(name: String) = getOrCreateGlobalVector(name, KslUint4)

    fun globalBool1(name: String) = getOrCreateGlobalScalar(name, KslBool1)
    fun globalBool2(name: String) = getOrCreateGlobalVector(name, KslBool2)
    fun globalBool3(name: String) = getOrCreateGlobalVector(name, KslBool3)
    fun globalBool4(name: String) = getOrCreateGlobalVector(name, KslBool4)

    fun globalMat2(name: String) = getOrCreateGlobalMatrix(name, KslMat2)
    fun globalMat3(name: String) = getOrCreateGlobalMatrix(name, KslMat3)
    fun globalMat4(name: String) = getOrCreateGlobalMatrix(name, KslMat4)

    fun <T: Struct> globalStruct(name: String, struct: KslStruct<T>) = getOrCreateGlobalStruct(name, struct)
}

enum class KslShaderStageType(val pipelineStageType: ShaderStage) {
    VertexShader(ShaderStage.VERTEX_SHADER),
    FragmentShader(ShaderStage.FRAGMENT_SHADER),
    ComputeShader(ShaderStage.COMPUTE_SHADER)
}

class KslVertexStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.VertexShader) {

    val attributes = mutableMapOf<String, KslVertexAttribute<*>>()

    val inVertexIndex = KslStageInputScalar(KslVarScalar(NAME_IN_VERTEX_INDEX, KslUint1, false))
    val inInstanceIndex = KslStageInputScalar(KslVarScalar(NAME_IN_INSTANCE_INDEX, KslUint1, false))
    val outPosition = KslStageOutputVector(KslVarVector(NAME_OUT_POSITION, KslFloat4, true))
    val outPointSize = KslStageOutputScalar(KslVarScalar(NAME_OUT_POINT_SIZE, KslFloat1, true))

    val isUsingVertexIndex: Boolean get() = inVertexIndex.value in main.dependencies
    val isUsingInstanceIndex: Boolean get() = inInstanceIndex.value in main.dependencies
    val isSettingPosition: Boolean get() = outPosition.value in main.dependencies
    val isSettingPointSize: Boolean get() = outPointSize.value in main.dependencies

    init {
        globalScope.definedStates += inVertexIndex.value
        globalScope.definedStates += inInstanceIndex.value
        globalScope.definedStates += outPosition.value
        globalScope.definedStates += outPointSize.value
    }

    private inline fun <reified T: KslVertexAttribute<*>> getOrCreateAttribute(name: String, create: () -> T): T {
        val attribute = attributes[name] ?: create().also {
            attributes[name] = it
            globalScope.definedStates += it.value
        }
        if (attribute !is T) {
            throw IllegalStateException("Existing attribute with name \"$name\" has not the expected type")
        }
        return attribute
    }

    private fun <S> attribScalar(name: String, type: S, rate: KslInputRate) where S: KslType, S: KslScalar =
        getOrCreateAttribute(name) { KslVertexAttributeScalar(KslVarScalar(name, type, false), rate) }

    private fun <V, S> attribVector(name: String, type: V, rate: KslInputRate) where V: KslType, V: KslVector<S>, S: KslScalar =
        getOrCreateAttribute(name) { KslVertexAttributeVector(KslVarVector(name, type, false), rate) }

    private fun <M, V> attribMatrix(name: String, type: M, rate: KslInputRate) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        getOrCreateAttribute(name) { KslVertexAttributeMatrix(KslVarMatrix(name, type, false), rate) }

    fun vertexAttribFloat1(name: String) = attribScalar(name, KslFloat1, KslInputRate.Vertex)
    fun vertexAttribFloat2(name: String) = attribVector(name, KslFloat2, KslInputRate.Vertex)
    fun vertexAttribFloat3(name: String) = attribVector(name, KslFloat3, KslInputRate.Vertex)
    fun vertexAttribFloat4(name: String) = attribVector(name, KslFloat4, KslInputRate.Vertex)

    fun vertexAttribInt1(name: String) = attribScalar(name, KslInt1, KslInputRate.Vertex)
    fun vertexAttribInt2(name: String) = attribVector(name, KslInt2, KslInputRate.Vertex)
    fun vertexAttribInt3(name: String) = attribVector(name, KslInt3, KslInputRate.Vertex)
    fun vertexAttribInt4(name: String) = attribVector(name, KslInt4, KslInputRate.Vertex)

    fun instanceAttribFloat1(name: String) = attribScalar(name, KslFloat1, KslInputRate.Instance)
    fun instanceAttribFloat2(name: String) = attribVector(name, KslFloat2, KslInputRate.Instance)
    fun instanceAttribFloat3(name: String) = attribVector(name, KslFloat3, KslInputRate.Instance)
    fun instanceAttribFloat4(name: String) = attribVector(name, KslFloat4, KslInputRate.Instance)

    fun instanceAttribMat3(name: String) = attribMatrix(name, KslMat3, KslInputRate.Instance)
    fun instanceAttribMat4(name: String) = attribMatrix(name, KslMat4, KslInputRate.Instance)

    companion object {
        const val NAME_IN_VERTEX_INDEX = "inVertexIndex"
        const val NAME_IN_INSTANCE_INDEX = "inInstanceIndex"
        const val NAME_OUT_POSITION = "outPosition"
        const val NAME_OUT_POINT_SIZE = "outPointSize"
    }
}

@Suppress("DEPRECATION")
class KslFragmentStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.FragmentShader) {

    @Deprecated("Avoid using inFragPosition as it behaves differently on different backends (OpenGL vs. WebGPU")
    val inFragPosition = KslStageInputVector(KslVarVector(NAME_IN_FRAG_POSITION, KslFloat4, false))
    val inIsFrontFacing = KslStageInputScalar(KslVarScalar(NAME_IN_IS_FRONT_FACING, KslBool1, false))
    val outDepth = KslStageOutputScalar(KslVarScalar(NAME_OUT_DEPTH, KslFloat1, true))
    val outColors = mutableListOf<KslStageOutputVector<KslFloat4, KslFloat1>>()

    val isUsingFragPosition: Boolean get() = inFragPosition.value in main.dependencies
    val isUsingIsFrontFacing: Boolean get() = inIsFrontFacing.value in main.dependencies
    val isSettingFragDepth: Boolean get() = outDepth.value in main.dependencies

    init {
        globalScope.definedStates += inFragPosition.value
        globalScope.definedStates += inIsFrontFacing.value
        globalScope.definedStates += outDepth.value
    }

    fun colorOutput(location: Int = 0): KslStageOutputVector<KslFloat4, KslFloat1> {
        val name = "${NAME_OUT_COLOR_PREFIX}${location}"
        return outColors.find { it.value.stateName == name }
            ?: KslStageOutputVector(KslVarVector(name, KslFloat4, true)).also {
                it.location = location
                globalScope.definedStates += it.value
                outColors += it
            }
    }

    fun KslScopeBuilder.colorOutput(rgb: KslVectorExpression<KslFloat3, KslFloat1>, a: KslScalarExpression<KslFloat1> = 1f.const, location: Int = 0) {
        check (parentStage is KslFragmentStage) { "colorOutput is only available in fragment stage" }
        val outColor = parentStage.colorOutput(location)
        outColor.value set float4Value(rgb, a)
    }

    fun KslScopeBuilder.colorOutput(value: KslVectorExpression<KslFloat4, KslFloat1>, location: Int = 0) {
        check (parentStage is KslFragmentStage) { "colorOutput is only available in fragment stage" }
        parentStage.colorOutput(location) set value
    }

    companion object {
        const val NAME_IN_FRAG_POSITION = "inFragPosition"
        const val NAME_IN_IS_FRONT_FACING = "inIsFrontFacing"
        const val NAME_OUT_DEPTH = "outDepth"
        const val NAME_OUT_COLOR_PREFIX = "outColor_"
    }
}

class KslComputeStage(program: KslProgram, val workGroupSize: Vec3i) : KslShaderStage(program, KslShaderStageType.ComputeShader) {

    val inGlobalInvocationId = KslStageInputVector(KslVarVector(NAME_IN_GLOBAL_INVOCATION_ID, KslUint3, false))
    val inLocalInvocationId = KslStageInputVector(KslVarVector(NAME_IN_LOCAL_INVOCATION_ID, KslUint3, false))
    val inWorkGroupId = KslStageInputVector(KslVarVector(NAME_IN_WORK_GROUP_ID, KslUint3, false))
    val inNumWorkGroups = KslStageInputVector(KslVarVector(NAME_IN_NUM_WORK_GROUPS, KslUint3, false))
    val inWorkGroupSize = KslStageInputVector(KslVarVector(NAME_IN_WORK_GROUP_SIZE, KslUint3, false))

    val isUsingGlobalInvocationId: Boolean get() = inGlobalInvocationId.value in main.dependencies
    val isUsingLocalInvocationId: Boolean get() = inLocalInvocationId.value in main.dependencies
    val isUsingWorkGroupId: Boolean get() = inWorkGroupId.value in main.dependencies
    val isUsingNumWorkGroups: Boolean get() = inNumWorkGroups.value in main.dependencies
    val isUsingWorkGroupSize: Boolean get() = inWorkGroupSize.value in main.dependencies

    init {
        globalScope.definedStates += inGlobalInvocationId.value
        globalScope.definedStates += inLocalInvocationId.value
        globalScope.definedStates += inWorkGroupId.value
        globalScope.definedStates += inNumWorkGroups.value
        globalScope.definedStates += inWorkGroupSize.value
    }

    fun KslScopeBuilder.`return`() {
        check (parentStage is KslComputeStage) { "return is only available in compute stage" }
        ops += KslReturn(this, KslValueVoid)
    }

    companion object {
        const val NAME_IN_GLOBAL_INVOCATION_ID = "inGlobalInvocationId"
        const val NAME_IN_LOCAL_INVOCATION_ID = "inLocalInvocationId"
        const val NAME_IN_WORK_GROUP_ID = "inWorkGroupId"
        const val NAME_IN_NUM_WORK_GROUPS = "inNumWorkGroups"
        const val NAME_IN_WORK_GROUP_SIZE = "inWorkGroupSize"
    }
}
