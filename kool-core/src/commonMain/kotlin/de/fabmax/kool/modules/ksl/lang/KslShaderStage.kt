package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.math.Vec3i
import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor
import de.fabmax.kool.pipeline.ShaderStage
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class KslShaderStage(val program: KslProgram, val type: KslShaderStageType) {

    val interStageVars = mutableListOf<KslInterStageVar<*>>()

    val globalScope = KslScopeBuilder(null, null, this)
    private val mainOp = KslOp("main", globalScope)
    val main = KslScopeBuilder(mainOp, globalScope, this)
    val hierarchy = KslHierarchy(globalScope)

    val functions = mutableMapOf<String, KslFunction<*>>()

    init {
        globalScope.scopeName = "global"
        globalScope.ops += mainOp
        mainOp.childScopes += main
    }

    fun main(block: KslScopeBuilder.() -> Unit) {
        contract {
            callsInPlace(block, InvocationKind.EXACTLY_ONCE)
        }
        main.apply(block)
    }

    fun createFunction(name: String, function: KslFunction<*>) {
        if (name in functions.keys) {
            throw IllegalStateException("Function with name $name is already defined")
        }
        functions[name] = function
    }

    inline fun <reified T: KslFunction<*>> getOrCreateFunction(name: String, createFunc: () -> T): T {
        return functions.getOrPut(name, createFunc) as T
    }

    inline fun <reified T: KslBlock> findBlock(name: String? = null): T? {
        return main.getBlocks(name, mutableListOf()).find { it is T } as? T
    }

    fun dependsOn(uniform: KslUniform<*>): Boolean {
        return uniform.value in main.dependencies || functions.values.any { f -> uniform.value in f.body.dependencies }
    }

    fun dependsOn(storage: KslStorage<*, *>): Boolean {
        return storage in main.dependencies || functions.values.any { f -> storage in f.body.dependencies }
    }

    fun getUsedSamplers(): List<KslUniform<*>> {
        return program.uniformSamplers.values.filter { dependsOn(it) }
    }

    fun getUsedStorage(): List<KslStorage<*, *>> {
        return program.uniformStorage.values.filter { dependsOn(it) }
    }

    fun getUsedUbos(): List<KslUniformBuffer> {
        return program.uniformBuffers.filter { ubo ->
            ubo.uniforms.values.any { dependsOn(it) }
        }
    }

    open fun prepareGenerate() {
        KslProcessor().process(hierarchy)
        functions.values.forEach { it.prepareGenerate() }
    }
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

class KslFragmentStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.FragmentShader) {

    // fixme: in OpenGL frag position is in pixels, other shading languages use normalized coordinates
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
        outColor.value.rgb set rgb
        outColor.value.a set a
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

    init {
        globalScope.definedStates += inGlobalInvocationId.value
        globalScope.definedStates += inLocalInvocationId.value
        globalScope.definedStates += inWorkGroupId.value
        globalScope.definedStates += inNumWorkGroups.value
        globalScope.definedStates += inWorkGroupSize.value
    }

    companion object {
        const val NAME_IN_GLOBAL_INVOCATION_ID = "inGlobalInvocationId"
        const val NAME_IN_LOCAL_INVOCATION_ID = "inLocalInvocationId"
        const val NAME_IN_WORK_GROUP_ID = "inWorkGroupId"
        const val NAME_IN_NUM_WORK_GROUPS = "inNumWorkGroups"
        const val NAME_IN_WORK_GROUP_SIZE = "inWorkGroupSize"
    }
}
