package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslHierarchy
import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslProcessor

open class KslFunction<T: KslType>(val name: String, val returnType: T, val parentStage: KslShaderStage) {

    val parameters = mutableListOf<KslValue<*>>()
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

    private fun <V, S> paramVector(name: String, type: V) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslVarVector(name, type, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <M, V> paramMatrix(name: String, type: M) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslVarMatrix(name, type, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <T: KslType> paramVar(name: String, type: T) =
        KslVar(name, type, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <S> paramScalarArray(name: String, type: S, arraySize: Int) where S: KslType, S: KslScalar =
        KslArrayScalar(name, type, arraySize, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <V, S> paramVectorArray(name: String, type: V, arraySize: Int) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslArrayVector(name, type, arraySize, false).also {
            parameters += it
            functionScope.definedStates += it
        }

    private fun <M, V> paramMatrixArray(name: String, type: M, arraySize: Int) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslArrayMatrix(name, type, arraySize, false).also {
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

    fun paramFloat1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramF1"), KslTypeFloat1, arraySize)
    fun paramFloat2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF2"), KslTypeFloat2, arraySize)
    fun paramFloat3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF3"), KslTypeFloat3, arraySize)
    fun paramFloat4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF4"), KslTypeFloat4, arraySize)

    fun paramInt1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramI1"), KslTypeInt1, arraySize)
    fun paramInt2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI2"), KslTypeInt2, arraySize)
    fun paramInt3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI3"), KslTypeInt3, arraySize)
    fun paramInt4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI4"), KslTypeInt4, arraySize)

    fun paramUint1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramU1"), KslTypeUint1, arraySize)
    fun paramUint2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU2"), KslTypeUint2, arraySize)
    fun paramUint3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU3"), KslTypeUint3, arraySize)
    fun paramUint4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU4"), KslTypeUint4, arraySize)

    fun paramBool1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramB1"), KslTypeBool1, arraySize)
    fun paramBool2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB2"), KslTypeBool2, arraySize)
    fun paramBool3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB3"), KslTypeBool3, arraySize)
    fun paramBool4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB4"), KslTypeBool4, arraySize)

    fun paramMat2Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM2"), KslTypeMat2, arraySize)
    fun paramMat3Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM3"), KslTypeMat3, arraySize)
    fun paramMat4Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM4"), KslTypeMat4, arraySize)

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
class KslInvokeFunctionScalarArray<S>(function: KslFunction<KslTypeArray<S>>, parentScope: KslScopeBuilder, returnType: KslTypeArray<S>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslTypeArray<S>>(function, parentScope, returnType, *args), KslScalarArrayExpression<S> where S: KslType, S: KslScalar
class KslInvokeFunctionVectorArray<V, S>(function: KslFunction<KslTypeArray<V>>, parentScope: KslScopeBuilder, returnType: KslTypeArray<V>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslTypeArray<V>>(function, parentScope, returnType, *args), KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
class KslInvokeFunctionMatrixArray<M, V>(function: KslFunction<KslTypeArray<M>>, parentScope: KslScopeBuilder, returnType: KslTypeArray<M>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslTypeArray<M>>(function, parentScope, returnType, *args), KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>


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


class KslFunctionFloat1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeFloat1>>(name, KslTypeFloat1Array(arraySize), parentStage)
class KslFunctionFloat2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeFloat2>>(name, KslTypeFloat2Array(arraySize), parentStage)
class KslFunctionFloat3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeFloat3>>(name, KslTypeFloat3Array(arraySize), parentStage)
class KslFunctionFloat4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeFloat4>>(name, KslTypeFloat4Array(arraySize), parentStage)

class KslFunctionInt1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeInt1>>(name, KslTypeInt1Array(arraySize), parentStage)
class KslFunctionInt2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeInt2>>(name, KslTypeInt2Array(arraySize), parentStage)
class KslFunctionInt3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeInt3>>(name, KslTypeInt3Array(arraySize), parentStage)
class KslFunctionInt4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeInt4>>(name, KslTypeInt4Array(arraySize), parentStage)

class KslFunctionUint1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeUint1>>(name, KslTypeUint1Array(arraySize), parentStage)
class KslFunctionUint2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeUint2>>(name, KslTypeUint2Array(arraySize), parentStage)
class KslFunctionUint3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeUint3>>(name, KslTypeUint3Array(arraySize), parentStage)
class KslFunctionUint4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeUint4>>(name, KslTypeUint4Array(arraySize), parentStage)

class KslFunctionBool1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeBool1>>(name, KslTypeBool1Array(arraySize), parentStage)
class KslFunctionBool2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeBool2>>(name, KslTypeBool2Array(arraySize), parentStage)
class KslFunctionBool3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeBool3>>(name, KslTypeBool3Array(arraySize), parentStage)
class KslFunctionBool4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeBool4>>(name, KslTypeBool4Array(arraySize), parentStage)

class KslFunctionMat2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeMat2>>(name, KslTypeMat2Array(arraySize), parentStage)
class KslFunctionMat3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeMat3>>(name, KslTypeMat3Array(arraySize), parentStage)
class KslFunctionMat4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslTypeArray<KslTypeMat4>>(name, KslTypeMat4Array(arraySize), parentStage)


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


fun KslShaderStage.functionFloat1Array(name: String, arraySize: Int, block: KslFunctionFloat1Array.() -> Unit) =
    KslFunctionFloat1Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat2Array(name: String, arraySize: Int, block: KslFunctionFloat2Array.() -> Unit) =
    KslFunctionFloat2Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat3Array(name: String, arraySize: Int, block: KslFunctionFloat3Array.() -> Unit) =
    KslFunctionFloat3Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionFloat4Array(name: String, arraySize: Int, block: KslFunctionFloat4Array.() -> Unit) =
    KslFunctionFloat4Array(name, arraySize, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionInt1Array(name: String, arraySize: Int, block: KslFunctionInt1Array.() -> Unit) =
    KslFunctionInt1Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt2Array(name: String, arraySize: Int, block: KslFunctionInt2Array.() -> Unit) =
    KslFunctionInt2Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt3Array(name: String, arraySize: Int, block: KslFunctionInt3Array.() -> Unit) =
    KslFunctionInt3Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionInt4Array(name: String, arraySize: Int, block: KslFunctionInt4Array.() -> Unit) =
    KslFunctionInt4Array(name, arraySize, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionUint1Array(name: String, arraySize: Int, block: KslFunctionUint1Array.() -> Unit) =
    KslFunctionUint1Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint2Array(name: String, arraySize: Int, block: KslFunctionUint2Array.() -> Unit) =
    KslFunctionUint2Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint3Array(name: String, arraySize: Int, block: KslFunctionUint3Array.() -> Unit) =
    KslFunctionUint3Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionUint4Array(name: String, arraySize: Int, block: KslFunctionUint4Array.() -> Unit) =
    KslFunctionUint4Array(name, arraySize, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionBool1Array(name: String, arraySize: Int, block: KslFunctionBool1Array.() -> Unit) =
    KslFunctionBool1Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool2Array(name: String, arraySize: Int, block: KslFunctionBool2Array.() -> Unit) =
    KslFunctionBool2Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool3Array(name: String, arraySize: Int, block: KslFunctionBool3Array.() -> Unit) =
    KslFunctionBool3Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionBool4Array(name: String, arraySize: Int, block: KslFunctionBool4Array.() -> Unit) =
    KslFunctionBool4Array(name, arraySize, this).apply(block).also { createFunction(name, it) }


fun KslShaderStage.functionMat2Array(name: String, arraySize: Int, block: KslFunctionMat2Array.() -> Unit) =
    KslFunctionMat2Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionMat3Array(name: String, arraySize: Int, block: KslFunctionMat3Array.() -> Unit) =
    KslFunctionMat3Array(name, arraySize, this).apply(block).also { createFunction(name, it) }

fun KslShaderStage.functionMat4Array(name: String, arraySize: Int, block: KslFunctionMat4Array.() -> Unit) =
    KslFunctionMat4Array(name, arraySize, this).apply(block).also { createFunction(name, it) }