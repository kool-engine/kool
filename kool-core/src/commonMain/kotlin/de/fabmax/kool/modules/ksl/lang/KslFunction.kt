package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor

open class KslFunction<T: KslType>(val name: String, val returnType: T, val parentStage: KslShaderStage) {

    val parameters = mutableListOf<KslVar<*>>()
    val functionDependencies = mutableSetOf<KslFunction<*>>()

    private val functionScope = KslScopeBuilder(null, parentStage.globalScope, parentStage)
    private val functionRoot = FunctionRoot(this)
    val hierarchy = KslHierarchy(functionScope)

    val body = KslScopeBuilder(functionRoot, functionScope, parentStage)

    init {
        functionScope.scopeName = name
        functionScope.ops += functionRoot
        functionRoot.childScopes += body
    }

    fun body(block: KslScopeBuilder.() -> KslExpression<T>) {
        body.apply {
            `return`(block())
        }
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

    private fun <T: KslType> paramVar(name: String, type: T) =
        KslVar(name, type, false).also {
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

    fun paramUint1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramU1"), KslTypeUint1)
    fun paramUint2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU2"), KslTypeUint2)
    fun paramUint3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU3"), KslTypeUint3)
    fun paramUint4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU4"), KslTypeUint4)

    fun paramBool1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramB1"), KslTypeBool1)
    fun paramBool2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB2"), KslTypeBool2)
    fun paramBool3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB3"), KslTypeBool3)
    fun paramBool4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB4"), KslTypeBool4)

    fun paramMat2(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM2"), KslTypeMat2)
    fun paramMat3(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM3"), KslTypeMat3)
    fun paramMat4(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM4"), KslTypeMat4)

    fun paramColorTex1d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor1d"), KslTypeColorSampler1d)
    fun paramColorTex2d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor2d"), KslTypeColorSampler2d)
    fun paramColorTex3d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor3d"), KslTypeColorSampler3d)
    fun paramColorTexCube(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColorCube"), KslTypeColorSamplerCube)
    fun paramColorTex2dArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor2dArray"), KslTypeColorSampler2dArray)
    fun paramColorTexCubeArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColorCubeArray"), KslTypeColorSamplerCubeArray)

    fun paramDepthTex2d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepth2d"), KslTypeDepthSampler2d)
    fun paramDepthTexCube(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepthCube"), KslTypeDepthSamplerCube)
    fun paramDepthTex2dArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepth2dArray"), KslTypeDepthSampler2dArray)
    fun paramDepthTexCubeArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepthCubeArray"), KslTypeDepthSamplerCubeArray)

    fun paramIntTex2d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramInt2d"), KslTypeIntSampler2d)

    fun prepareGenerate() {
        hierarchy.globalScope.definedStates += parentStage.globalScope.definedStates
        KslProcessor().process(hierarchy)
    }

    fun KslScopeBuilder.`return`(returnValue: KslExpression<*>) {
        ops += KslReturn(this, returnValue)
    }

    inner class FunctionRoot(val function: KslFunction<*>) : KslOp("body", functionScope)
}

abstract class KslInvokeFunction<T: KslType>(val function: KslFunction<T>, parentScope: KslScopeBuilder, returnType: T, vararg args: KslExpression<*>) : KslExpression<T> {
    val args = listOf(*args)

    init {
        if (function.parameters.size != args.size) {
            throw IllegalArgumentException("Wrong number of parameters for invoking function ${function.name}. " +
                    "Expected: ${function.parameters.size} [${function.parameters.joinToString { it.stateName }}], provided: ${args.size} [${args.joinToString { it.toPseudoCode() }}]")
        }
        function.parameters.forEachIndexed { i, param ->
            if (args[i].expressionType != param.expressionType) {
                throw IllegalArgumentException("Wrong type of parameter ${i+1} (${param.stateName}) on invoking function ${function.name}. " +
                        "Expected: ${param.expressionType.typeName}, provided: ${args[i].expressionType.typeName} (${args[i].toPseudoCode()})")
            }
        }

        parentScope.parentFunction?.let {
            it.functionDependencies += function
        }
    }

    override val expressionType: T = returnType
    override fun collectStateDependencies() = args.flatMap { it.collectStateDependencies() }.toSet()
    override fun toPseudoCode() = "${function.name}(${args.joinToString { it.toPseudoCode() }})"
    override fun generateExpression(generator: KslGenerator) = generator.invokeFunction(this)
}

class KslInvokeFunctionScalar<S>(function: KslFunction<S>, parentScope: KslScopeBuilder, returnType: S, vararg args: KslExpression<*>)
    : KslInvokeFunction<S>(function, parentScope, returnType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslInvokeFunctionVector<V, S>(function: KslFunction<V>, parentScope: KslScopeBuilder, returnType: V, vararg args: KslExpression<*>)
    : KslInvokeFunction<V>(function, parentScope, returnType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
class KslInvokeFunctionMatrix<M, V>(function: KslFunction<M>, parentScope: KslScopeBuilder, returnType: M, vararg args: KslExpression<*>)
    : KslInvokeFunction<M>(function, parentScope, returnType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>


class KslFunctionFloat1(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeFloat1>(name, KslTypeFloat1, parentStage)
class KslFunctionFloat2(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeFloat2>(name, KslTypeFloat2, parentStage)
class KslFunctionFloat3(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeFloat3>(name, KslTypeFloat3, parentStage)
class KslFunctionFloat4(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeFloat4>(name, KslTypeFloat4, parentStage)

class KslFunctionInt1(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeInt1>(name, KslTypeInt1, parentStage)
class KslFunctionInt2(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeInt2>(name, KslTypeInt2, parentStage)
class KslFunctionInt3(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeInt3>(name, KslTypeInt3, parentStage)
class KslFunctionInt4(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeInt4>(name, KslTypeInt4, parentStage)

class KslFunctionUint1(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeUint1>(name, KslTypeUint1, parentStage)
class KslFunctionUint2(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeUint2>(name, KslTypeUint2, parentStage)
class KslFunctionUint3(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeUint3>(name, KslTypeUint3, parentStage)
class KslFunctionUint4(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeUint4>(name, KslTypeUint4, parentStage)

class KslFunctionBool1(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeBool1>(name, KslTypeBool1, parentStage)
class KslFunctionBool2(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeBool2>(name, KslTypeBool2, parentStage)
class KslFunctionBool3(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeBool3>(name, KslTypeBool3, parentStage)
class KslFunctionBool4(name: String, parentStage: KslShaderStage) : KslFunction<KslTypeBool4>(name, KslTypeBool4, parentStage)


fun KslShaderStage.functionFloat1(name: String, block: KslFunctionFloat1.() -> Unit) =
    KslFunctionFloat1(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat2(name: String, block: KslFunctionFloat2.() -> Unit) =
    KslFunctionFloat2(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat3(name: String, block: KslFunctionFloat3.() -> Unit) =
    KslFunctionFloat3(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat4(name: String, block: KslFunctionFloat4.() -> Unit) =
    KslFunctionFloat4(name, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionInt1(name: String, block: KslFunctionInt1.() -> Unit) =
    KslFunctionInt1(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt2(name: String, block: KslFunctionInt2.() -> Unit) =
    KslFunctionInt2(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt3(name: String, block: KslFunctionInt3.() -> Unit) =
    KslFunctionInt3(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt4(name: String, block: KslFunctionInt4.() -> Unit) =
    KslFunctionInt4(name, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionUint1(name: String, block: KslFunctionUint1.() -> Unit) =
    KslFunctionUint1(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint2(name: String, block: KslFunctionUint2.() -> Unit) =
    KslFunctionUint2(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint3(name: String, block: KslFunctionUint3.() -> Unit) =
    KslFunctionUint3(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint4(name: String, block: KslFunctionUint4.() -> Unit) =
    KslFunctionUint4(name, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionBool1(name: String, block: KslFunctionBool1.() -> Unit) =
    KslFunctionBool1(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool2(name: String, block: KslFunctionBool2.() -> Unit) =
    KslFunctionBool2(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool3(name: String, block: KslFunctionBool3.() -> Unit) =
    KslFunctionBool3(name, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool4(name: String, block: KslFunctionBool4.() -> Unit) =
    KslFunctionBool4(name, this).apply(block).also { createFunction(name, it) }