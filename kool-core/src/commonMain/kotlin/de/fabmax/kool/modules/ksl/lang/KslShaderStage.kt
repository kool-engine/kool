package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor

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
        main.apply(block)
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

    val attributes = mutableListOf<KslVertexAttribute<*>>()

    val inVertexIndex = KslStageInputScalar(KslVar("inVertexIndex", KslTypeInt1, false))
    val inInstanceIndex = KslStageInputScalar(KslVar("inInstanceIndex", KslTypeInt1, false))
    val outPosition = KslStageOutputVector(KslVar("outPosition", KslTypeFloat4, true))

    init {
        globalScope.definedStates += inVertexIndex.value
        globalScope.definedStates += inInstanceIndex.value
        globalScope.definedStates += outPosition.value
    }

    private fun <S> attribScalar(name: String, value: S, rate: InputRate) where S: KslType, S: KslScalar =
        KslVertexAttributeScalar(KslVarScalar(name, value, false), rate).also {
            attributes += it
            globalScope.definedStates += it.value
        }

    private fun <V, S> attribVector(name: String, value: V, rate: InputRate) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslVertexAttributeVector(KslVarVector(name, value, false), rate).also {
            attributes += it
            globalScope.definedStates += it.value
        }

    private fun <M, V> attribMatrix(name: String, value: M, rate: InputRate) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslVertexAttributeMatrix(KslVarMatrix(name, value, false), rate).also {
            attributes += it
            globalScope.definedStates += it.value
        }

    // todo: assign attribute locations

    fun vertexAttribFloat1(name: String) = attribScalar(name, KslTypeFloat1, InputRate.Vertex)
    fun vertexAttribFloat2(name: String) = attribVector(name, KslTypeFloat2, InputRate.Vertex)
    fun vertexAttribFloat3(name: String) = attribVector(name, KslTypeFloat3, InputRate.Vertex)
    fun vertexAttribFloat4(name: String) = attribVector(name, KslTypeFloat4, InputRate.Vertex)

    fun instanceAttribFloat1(name: String) = attribScalar(name, KslTypeFloat1, InputRate.Instance)
    fun instanceAttribFloat2(name: String) = attribVector(name, KslTypeFloat2, InputRate.Instance)
    fun instanceAttribFloat3(name: String) = attribVector(name, KslTypeFloat3, InputRate.Instance)
    fun instanceAttribFloat4(name: String) = attribVector(name, KslTypeFloat4, InputRate.Instance)

    fun instanceAttribMat4(name: String) = attribMatrix(name, KslTypeMat4, InputRate.Instance)
}

class KslFragmentStage(program: KslProgram) : KslShaderStage(program, KslShaderStageType.FragmentShader) {

    val inFragPosition = KslStageInputVector(KslVar("inFragPosition", KslTypeFloat4, false))
    val inIsFrontFacing = KslStageInputScalar(KslVar("inIsFrontFacing", KslTypeBool1, false))
    val outDepth = KslStageOutputScalar(KslVar("outDepth", KslTypeFloat1, true))
    val outColors = mutableListOf<KslStageOutputVector<KslTypeFloat4, KslTypeFloat1>>()

    init {
        globalScope.definedStates += inFragPosition.value
        globalScope.definedStates += inIsFrontFacing.value
        globalScope.definedStates += outDepth.value
    }

    fun outColor(location: Int = 0) =
        KslStageOutputVector(KslVar("outColor_$location", KslTypeFloat4, true)).also {
            it.location = location
            globalScope.definedStates += it.value
            outColors += it
        }
}
