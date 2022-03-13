package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor

open class KslFunction<T: KslType>(val name: String, val returnType: T, val parentStage: KslShaderStage) {

    val parameters = mutableListOf<KslVar<*>>()

    private val functionScope = KslScopeBuilder(null, parentStage.globalScope, parentStage)
    private val bodyOp = BodyOp(this)
    val hierarchy = KslHierarchy(functionScope)

    val body = KslScopeBuilder(bodyOp, functionScope, parentStage)

    init {
        functionScope.scopeName = name
        functionScope.ops += bodyOp
        bodyOp.childScopes += body
    }

    private fun <S> paramScalar(name: String, type: S) where S: KslType, S: KslScalar =
        KslVarScalar(name, type, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <V, S> paramVector(name: String, value: V) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslVarVector(name, value, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <M, V> paramMatrix(name: String, value: M) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslVarMatrix(name, value, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    fun paramFloat1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramF1"), KslTypeFloat1)
    fun paramFloat2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF2"), KslTypeFloat2)
    fun paramFloat3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF3"), KslTypeFloat3)
    fun paramFloat4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF4"), KslTypeFloat4)

    fun paramInt1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramI1"), KslTypeInt1)
    fun paramInt2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI2"), KslTypeInt2)
    fun paramInt3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI3"), KslTypeInt3)
    fun paramInt4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI4"), KslTypeInt4)

    fun paramMat2(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM2"), KslTypeMat2)
    fun paramMat3(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM3"), KslTypeMat3)
    fun paramMat4(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM4"), KslTypeMat4)

    fun prepareGenerate() {
        KslProcessor().process(hierarchy)
    }

    fun KslScopeBuilder.`return`(returnValue: KslExpression<*>) {
        ops += KslReturn(this, returnValue)
    }

    inner class BodyOp(val function: KslFunction<*>) : KslOp("body", functionScope)
}

abstract class KslInvokeFunction<T: KslType>(val function: KslFunction<T>, returnType: T, vararg args: KslExpression<*>) : KslExpression<T> {
    val args = listOf(*args)
    override val expressionType: T = returnType
    override fun collectStateDependencies() = args.flatMap { it.collectStateDependencies() }.toSet()
    override fun toPseudoCode() = "${function.name}(${args.joinToString { it.toPseudoCode() }})"
    override fun generateExpression(generator: KslGenerator) = generator.invokeFunction(this)
}

class KslInvokeFunctionScalar<S>(function: KslFunction<S>, returnType: S, vararg args: KslExpression<*>)
    : KslInvokeFunction<S>(function, returnType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslInvokeFunctionVector<V, S>(function: KslFunction<V>, returnType: V, vararg args: KslExpression<*>)
    : KslInvokeFunction<V>(function, returnType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
class KslInvokeFunctionMatrix<M, V>(function: KslFunction<M>, returnType: M, vararg args: KslExpression<*>)
    : KslInvokeFunction<M>(function, returnType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>
