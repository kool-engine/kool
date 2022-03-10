package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class KslShaderStage(val program: KslProgram, val type: KslShaderStageType) {

    val uniforms = mutableListOf<KslUniform<*>>()
    val interStageVars = mutableListOf<KslInterStageVar<*>>()

    val globalScope = KslScopeBuilder(null, null, this)
    private val mainOp = KslOp("main", globalScope)
    val main = KslScopeBuilder(mainOp, globalScope, this)
    val hierarchy = KslHierarchy(globalScope)

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

    inline fun <reified T: KslBlock> findBlock(name: String? = null): T? {
        return main.getBlocks(name, mutableListOf()).find { it is T } as? T
    }

    open fun prepareGenerate() {
        KslProcessor().process(hierarchy)
        uniforms.removeAll { uniform -> uniform.value !in main.dependencies }
    }
}

enum class KslShaderStageType {
    VertexShader,
    FragmentShader
}

class KslVertexStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.VertexShader) {

    val attributes = mutableMapOf<String, KslVertexAttribute<*>>()

    val inVertexIndex = KslStageInputScalar(KslVar(NAME_IN_VERTEX_INDEX, KslTypeInt1, false))
    val inInstanceIndex = KslStageInputScalar(KslVar(NAME_IN_INSTANCE_INDEX, KslTypeInt1, false))
    val outPosition = KslStageOutputVector(KslVar(NAME_OUT_POSITION, KslTypeFloat4, true))

    init {
        globalScope.definedStates += inVertexIndex.value
        globalScope.definedStates += inInstanceIndex.value
        globalScope.definedStates += outPosition.value
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

    private fun <S> attribScalar(name: String, value: S, rate: KslInputRate) where S: KslType, S: KslScalar =
        getOrCreateAttribute(name) { KslVertexAttributeScalar(KslVarScalar(name, value, false), rate) }

    private fun <V, S> attribVector(name: String, value: V, rate: KslInputRate) where V: KslType, V: KslVector<S>, S: KslScalar =
        getOrCreateAttribute(name) { KslVertexAttributeVector(KslVarVector(name, value, false), rate) }

    private fun <M, V> attribMatrix(name: String, value: M, rate: KslInputRate) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        getOrCreateAttribute(name) { KslVertexAttributeMatrix(KslVarMatrix(name, value, false), rate) }

    // todo: assign attribute locations

    fun vertexAttribFloat1(name: String) = attribScalar(name, KslTypeFloat1, KslInputRate.Vertex)
    fun vertexAttribFloat2(name: String) = attribVector(name, KslTypeFloat2, KslInputRate.Vertex)
    fun vertexAttribFloat3(name: String) = attribVector(name, KslTypeFloat3, KslInputRate.Vertex)
    fun vertexAttribFloat4(name: String) = attribVector(name, KslTypeFloat4, KslInputRate.Vertex)

    fun instanceAttribFloat1(name: String) = attribScalar(name, KslTypeFloat1, KslInputRate.Instance)
    fun instanceAttribFloat2(name: String) = attribVector(name, KslTypeFloat2, KslInputRate.Instance)
    fun instanceAttribFloat3(name: String) = attribVector(name, KslTypeFloat3, KslInputRate.Instance)
    fun instanceAttribFloat4(name: String) = attribVector(name, KslTypeFloat4, KslInputRate.Instance)

    fun instanceAttribMat4(name: String) = attribMatrix(name, KslTypeMat4, KslInputRate.Instance)

    companion object {
        const val NAME_IN_VERTEX_INDEX = "inVertexIndex"
        const val NAME_IN_INSTANCE_INDEX = "inInstanceIndex"
        const val NAME_OUT_POSITION = "outPosition"
    }
}

class KslFragmentStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.FragmentShader) {

    // fixme: in OpenGL frag position is in pixels, other shading languages use normalized coordinates
    val inFragPosition = KslStageInputVector(KslVar(NAME_IN_FRAG_POSITION, KslTypeFloat4, false))
    val inIsFrontFacing = KslStageInputScalar(KslVar(NAME_IN_IS_FRONT_FACING, KslTypeBool1, false))
    val outDepth = KslStageOutputScalar(KslVar(NAME_OUT_DEPTH, KslTypeFloat1, true))
    val outColors = mutableListOf<KslStageOutputVector<KslTypeFloat4, KslTypeFloat1>>()

    init {
        globalScope.definedStates += inFragPosition.value
        globalScope.definedStates += inIsFrontFacing.value
        globalScope.definedStates += outDepth.value
    }

    fun outColor(location: Int = 0) =
        KslStageOutputVector(KslVar("${NAME_OUT_COLOR_PREFIX}${location}", KslTypeFloat4, true)).also {
            it.location = location
            globalScope.definedStates += it.value
            outColors += it
        }

    companion object {
        const val NAME_IN_FRAG_POSITION = "inFragPosition"
        const val NAME_IN_IS_FRONT_FACING = "inIsFrontFacing"
        const val NAME_OUT_DEPTH = "outDepth"
        const val NAME_OUT_COLOR_PREFIX = "outColor_"
    }
}
