package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.util.Struct

open class KslFunction<T: KslType>(val name: String, val returnType: T, val parentStage: KslShaderStage) {

    val parameters = mutableListOf<KslValue<*>>()
    val functionDependencies = mutableSetOf<KslFunction<*>>()

    val functionRoot = FunctionRoot(this)
    val body: KslScopeBuilder get() = functionRoot.body

    init {
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
            body.definedStates += it
        }

    private fun <V, S> paramVector(name: String, type: V) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslVarVector(name, type, false).also {
            parameters += it
            body.definedStates += it
        }

    private fun <M, V> paramMatrix(name: String, type: M) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslVarMatrix(name, type, false).also {
            parameters += it
            body.definedStates += it
        }

    private fun <T: KslType> paramVar(name: String, type: T) =
        KslVar(name, type, false).also {
            parameters += it
            body.definedStates += it
        }

    private fun <S> paramScalarArray(name: String, type: S, arraySize: Int) where S: KslType, S: KslScalar =
        KslArrayScalar(name, type, arraySize, false).also {
            parameters += it
            body.definedStates += it
        }

    private fun <V, S> paramVectorArray(name: String, type: V, arraySize: Int) where V: KslType, V: KslVector<S>, S: KslScalar =
        KslArrayVector(name, type, arraySize, false).also {
            parameters += it
            body.definedStates += it
        }

    private fun <M, V> paramMatrixArray(name: String, type: M, arraySize: Int) where M: KslType, M: KslMatrix<V>, V: KslVector<*> =
        KslArrayMatrix(name, type, arraySize, false).also {
            parameters += it
            body.definedStates += it
        }

    fun paramFloat1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramF1"), KslFloat1)
    fun paramFloat2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF2"), KslFloat2)
    fun paramFloat3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF3"), KslFloat3)
    fun paramFloat4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramF4"), KslFloat4)

    fun paramInt1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramI1"), KslInt1)
    fun paramInt2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI2"), KslInt2)
    fun paramInt3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI3"), KslInt3)
    fun paramInt4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramI4"), KslInt4)

    fun paramUint1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramU1"), KslUint1)
    fun paramUint2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU2"), KslUint2)
    fun paramUint3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU3"), KslUint3)
    fun paramUint4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramU4"), KslUint4)

    fun paramBool1(name: String? = null) = paramScalar(name ?: parentStage.program.nextName("paramB1"), KslBool1)
    fun paramBool2(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB2"), KslBool2)
    fun paramBool3(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB3"), KslBool3)
    fun paramBool4(name: String? = null) = paramVector(name ?: parentStage.program.nextName("paramB4"), KslBool4)

    fun paramMat2(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM2"), KslMat2)
    fun paramMat3(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM3"), KslMat3)
    fun paramMat4(name: String? = null) = paramMatrix(name ?: parentStage.program.nextName("paramM4"), KslMat4)

    fun <S> paramStruct(type: KslStruct<S>, name: String = parentStage.program.nextName("paramStr")) where S: Struct =
        KslVarStruct(name, type, false).also {
            parameters += it
            body.definedStates += it
        }

    fun paramColorTex1d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor1d"), KslColorSampler1d)
    fun paramColorTex2d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor2d"), KslColorSampler2d)
    fun paramColorTex3d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor3d"), KslColorSampler3d)
    fun paramColorTexCube(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColorCube"), KslColorSamplerCube)
    fun paramColorTex2dArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColor2dArray"), KslColorSampler2dArray)
    fun paramColorTexCubeArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramColorCubeArray"), KslColorSamplerCubeArray)

    fun paramDepthTex2d(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepth2d"), KslDepthSampler2d)
    fun paramDepthTexCube(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepthCube"), KslDepthSamplerCube)
    fun paramDepthTex2dArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepth2dArray"), KslDepthSampler2dArray)
    fun paramDepthTexCubeArray(name: String? = null) = paramVar(name ?: parentStage.program.nextName("paramDepthCubeArray"), KslDepthSamplerCubeArray)

    fun paramFloat1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramF1"), KslFloat1, arraySize)
    fun paramFloat2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF2"), KslFloat2, arraySize)
    fun paramFloat3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF3"), KslFloat3, arraySize)
    fun paramFloat4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramF4"), KslFloat4, arraySize)

    fun paramInt1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramI1"), KslInt1, arraySize)
    fun paramInt2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI2"), KslInt2, arraySize)
    fun paramInt3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI3"), KslInt3, arraySize)
    fun paramInt4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramI4"), KslInt4, arraySize)

    fun paramUint1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramU1"), KslUint1, arraySize)
    fun paramUint2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU2"), KslUint2, arraySize)
    fun paramUint3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU3"), KslUint3, arraySize)
    fun paramUint4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramU4"), KslUint4, arraySize)

    fun paramBool1Array(arraySize: Int, name: String? = null) = paramScalarArray(name ?: parentStage.program.nextName("paramB1"), KslBool1, arraySize)
    fun paramBool2Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB2"), KslBool2, arraySize)
    fun paramBool3Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB3"), KslBool3, arraySize)
    fun paramBool4Array(arraySize: Int, name: String? = null) = paramVectorArray(name ?: parentStage.program.nextName("paramB4"), KslBool4, arraySize)

    fun paramMat2Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM2"), KslMat2, arraySize)
    fun paramMat3Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM3"), KslMat3, arraySize)
    fun paramMat4Array(arraySize: Int, name: String? = null) = paramMatrixArray(name ?: parentStage.program.nextName("paramM4"), KslMat4, arraySize)

    fun KslScopeBuilder.`return`(returnValue: KslExpression<*>) {
        ops += KslReturn(this, returnValue)
    }

    inner class FunctionRoot(val function: KslFunction<*>) : KslOp(function.name, parentStage.globalScope) {
        val body = KslScopeBuilder(this, parentStage.globalScope, parentStage)
    }
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
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(*args.toTypedArray())
    override fun toPseudoCode() = "${function.name}(${args.joinToString { it.toPseudoCode() }})"
}

class KslInvokeFunctionScalar<S>(function: KslFunction<S>, parentScope: KslScopeBuilder, returnType: S, vararg args: KslExpression<*>)
    : KslInvokeFunction<S>(function, parentScope, returnType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslInvokeFunctionVector<V, S>(function: KslFunction<V>, parentScope: KslScopeBuilder, returnType: V, vararg args: KslExpression<*>)
    : KslInvokeFunction<V>(function, parentScope, returnType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
class KslInvokeFunctionMatrix<M, V>(function: KslFunction<M>, parentScope: KslScopeBuilder, returnType: M, vararg args: KslExpression<*>)
    : KslInvokeFunction<M>(function, parentScope, returnType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>
class KslInvokeFunctionStruct<S>(function: KslFunction<KslStruct<S>>, parentScope: KslScopeBuilder, returnType: KslStruct<S>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslStruct<S>>(function, parentScope, returnType, *args), KslExprStruct<S> where S: Struct
class KslInvokeFunctionScalarArray<S>(function: KslFunction<KslArrayType<S>>, parentScope: KslScopeBuilder, returnType: KslArrayType<S>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslArrayType<S>>(function, parentScope, returnType, *args), KslScalarArrayExpression<S> where S: KslType, S: KslScalar
class KslInvokeFunctionVectorArray<V, S>(function: KslFunction<KslArrayType<V>>, parentScope: KslScopeBuilder, returnType: KslArrayType<V>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslArrayType<V>>(function, parentScope, returnType, *args), KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
class KslInvokeFunctionMatrixArray<M, V>(function: KslFunction<KslArrayType<M>>, parentScope: KslScopeBuilder, returnType: KslArrayType<M>, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslArrayType<M>>(function, parentScope, returnType, *args), KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>
class KslInvokeFunctionVoid(function: KslFunction<KslTypeVoid>, parentScope: KslScopeBuilder, vararg args: KslExpression<*>)
    : KslInvokeFunction<KslTypeVoid>(function, parentScope, KslTypeVoid, *args), KslExpression<KslTypeVoid>

class KslFunctionFloat1(name: String, parentStage: KslShaderStage) : KslFunction<KslFloat1>(name, KslFloat1, parentStage)
class KslFunctionFloat2(name: String, parentStage: KslShaderStage) : KslFunction<KslFloat2>(name, KslFloat2, parentStage)
class KslFunctionFloat3(name: String, parentStage: KslShaderStage) : KslFunction<KslFloat3>(name, KslFloat3, parentStage)
class KslFunctionFloat4(name: String, parentStage: KslShaderStage) : KslFunction<KslFloat4>(name, KslFloat4, parentStage)

class KslFunctionInt1(name: String, parentStage: KslShaderStage) : KslFunction<KslInt1>(name, KslInt1, parentStage)
class KslFunctionInt2(name: String, parentStage: KslShaderStage) : KslFunction<KslInt2>(name, KslInt2, parentStage)
class KslFunctionInt3(name: String, parentStage: KslShaderStage) : KslFunction<KslInt3>(name, KslInt3, parentStage)
class KslFunctionInt4(name: String, parentStage: KslShaderStage) : KslFunction<KslInt4>(name, KslInt4, parentStage)

class KslFunctionUint1(name: String, parentStage: KslShaderStage) : KslFunction<KslUint1>(name, KslUint1, parentStage)
class KslFunctionUint2(name: String, parentStage: KslShaderStage) : KslFunction<KslUint2>(name, KslUint2, parentStage)
class KslFunctionUint3(name: String, parentStage: KslShaderStage) : KslFunction<KslUint3>(name, KslUint3, parentStage)
class KslFunctionUint4(name: String, parentStage: KslShaderStage) : KslFunction<KslUint4>(name, KslUint4, parentStage)

class KslFunctionBool1(name: String, parentStage: KslShaderStage) : KslFunction<KslBool1>(name, KslBool1, parentStage)
class KslFunctionBool2(name: String, parentStage: KslShaderStage) : KslFunction<KslBool2>(name, KslBool2, parentStage)
class KslFunctionBool3(name: String, parentStage: KslShaderStage) : KslFunction<KslBool3>(name, KslBool3, parentStage)
class KslFunctionBool4(name: String, parentStage: KslShaderStage) : KslFunction<KslBool4>(name, KslBool4, parentStage)

class KslFunctionStruct<S: Struct>(name: String, parentStage: KslShaderStage, struct: KslStruct<S>) : KslFunction<KslStruct<S>>(name, struct, parentStage)

class KslFunctionFloat1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslFloat1>>(name, KslFloat1Array(arraySize), parentStage)
class KslFunctionFloat2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslFloat2>>(name, KslFloat2Array(arraySize), parentStage)
class KslFunctionFloat3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslFloat3>>(name, KslFloat3Array(arraySize), parentStage)
class KslFunctionFloat4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslFloat4>>(name, KslFloat4Array(arraySize), parentStage)

class KslFunctionInt1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslInt1>>(name, KslInt1Array(arraySize), parentStage)
class KslFunctionInt2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslInt2>>(name, KslInt2Array(arraySize), parentStage)
class KslFunctionInt3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslInt3>>(name, KslInt3Array(arraySize), parentStage)
class KslFunctionInt4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslInt4>>(name, KslInt4Array(arraySize), parentStage)

class KslFunctionUint1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslUint1>>(name, KslUint1Array(arraySize), parentStage)
class KslFunctionUint2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslUint2>>(name, KslUint2Array(arraySize), parentStage)
class KslFunctionUint3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslUint3>>(name, KslUint3Array(arraySize), parentStage)
class KslFunctionUint4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslUint4>>(name, KslUint4Array(arraySize), parentStage)

class KslFunctionBool1Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslBool1>>(name, KslBool1Array(arraySize), parentStage)
class KslFunctionBool2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslBool2>>(name, KslBool2Array(arraySize), parentStage)
class KslFunctionBool3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslBool3>>(name, KslBool3Array(arraySize), parentStage)
class KslFunctionBool4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslBool4>>(name, KslBool4Array(arraySize), parentStage)

class KslFunctionMat2Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslMat2>>(name, KslMat2Array(arraySize), parentStage)
class KslFunctionMat3Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslMat3>>(name, KslMat3Array(arraySize), parentStage)
class KslFunctionMat4Array(name: String, arraySize: Int, parentStage: KslShaderStage) : KslFunction<KslArrayType<KslMat4>>(name, KslMat4Array(arraySize), parentStage)


fun KslShaderStage.functionFloat1(name: String, block: KslFunctionFloat1.() -> Unit) =
    KslFunctionFloat1(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat2(name: String, block: KslFunctionFloat2.() -> Unit) =
    KslFunctionFloat2(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat3(name: String, block: KslFunctionFloat3.() -> Unit) =
    KslFunctionFloat3(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat4(name: String, block: KslFunctionFloat4.() -> Unit) =
    KslFunctionFloat4(name, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionInt1(name: String, block: KslFunctionInt1.() -> Unit) =
    KslFunctionInt1(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt2(name: String, block: KslFunctionInt2.() -> Unit) =
    KslFunctionInt2(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt3(name: String, block: KslFunctionInt3.() -> Unit) =
    KslFunctionInt3(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt4(name: String, block: KslFunctionInt4.() -> Unit) =
    KslFunctionInt4(name, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionUint1(name: String, block: KslFunctionUint1.() -> Unit) =
    KslFunctionUint1(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint2(name: String, block: KslFunctionUint2.() -> Unit) =
    KslFunctionUint2(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint3(name: String, block: KslFunctionUint3.() -> Unit) =
    KslFunctionUint3(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint4(name: String, block: KslFunctionUint4.() -> Unit) =
    KslFunctionUint4(name, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionBool1(name: String, block: KslFunctionBool1.() -> Unit) =
    KslFunctionBool1(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool2(name: String, block: KslFunctionBool2.() -> Unit) =
    KslFunctionBool2(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool3(name: String, block: KslFunctionBool3.() -> Unit) =
    KslFunctionBool3(name, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool4(name: String, block: KslFunctionBool4.() -> Unit) =
    KslFunctionBool4(name, this).apply(block).also { addFunction(name, it) }


fun <S: Struct> KslShaderStage.functionStruct(name: String, struct: KslStruct<S>, block: KslFunctionStruct<S>.() -> Unit) =
    KslFunctionStruct(name, this, struct).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionFloat1Array(name: String, arraySize: Int, block: KslFunctionFloat1Array.() -> Unit) =
    KslFunctionFloat1Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat2Array(name: String, arraySize: Int, block: KslFunctionFloat2Array.() -> Unit) =
    KslFunctionFloat2Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat3Array(name: String, arraySize: Int, block: KslFunctionFloat3Array.() -> Unit) =
    KslFunctionFloat3Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionFloat4Array(name: String, arraySize: Int, block: KslFunctionFloat4Array.() -> Unit) =
    KslFunctionFloat4Array(name, arraySize, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionInt1Array(name: String, arraySize: Int, block: KslFunctionInt1Array.() -> Unit) =
    KslFunctionInt1Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt2Array(name: String, arraySize: Int, block: KslFunctionInt2Array.() -> Unit) =
    KslFunctionInt2Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt3Array(name: String, arraySize: Int, block: KslFunctionInt3Array.() -> Unit) =
    KslFunctionInt3Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionInt4Array(name: String, arraySize: Int, block: KslFunctionInt4Array.() -> Unit) =
    KslFunctionInt4Array(name, arraySize, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionUint1Array(name: String, arraySize: Int, block: KslFunctionUint1Array.() -> Unit) =
    KslFunctionUint1Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint2Array(name: String, arraySize: Int, block: KslFunctionUint2Array.() -> Unit) =
    KslFunctionUint2Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint3Array(name: String, arraySize: Int, block: KslFunctionUint3Array.() -> Unit) =
    KslFunctionUint3Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionUint4Array(name: String, arraySize: Int, block: KslFunctionUint4Array.() -> Unit) =
    KslFunctionUint4Array(name, arraySize, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionBool1Array(name: String, arraySize: Int, block: KslFunctionBool1Array.() -> Unit) =
    KslFunctionBool1Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool2Array(name: String, arraySize: Int, block: KslFunctionBool2Array.() -> Unit) =
    KslFunctionBool2Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool3Array(name: String, arraySize: Int, block: KslFunctionBool3Array.() -> Unit) =
    KslFunctionBool3Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionBool4Array(name: String, arraySize: Int, block: KslFunctionBool4Array.() -> Unit) =
    KslFunctionBool4Array(name, arraySize, this).apply(block).also { addFunction(name, it) }


fun KslShaderStage.functionMat2Array(name: String, arraySize: Int, block: KslFunctionMat2Array.() -> Unit) =
    KslFunctionMat2Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionMat3Array(name: String, arraySize: Int, block: KslFunctionMat3Array.() -> Unit) =
    KslFunctionMat3Array(name, arraySize, this).apply(block).also { addFunction(name, it) }

fun KslShaderStage.functionMat4Array(name: String, arraySize: Int, block: KslFunctionMat4Array.() -> Unit) =
    KslFunctionMat4Array(name, arraySize, this).apply(block).also { addFunction(name, it) }