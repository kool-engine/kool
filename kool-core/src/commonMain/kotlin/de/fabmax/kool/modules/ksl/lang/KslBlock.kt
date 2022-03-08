package de.fabmax.kool.modules.ksl.lang

import kotlin.reflect.KProperty

abstract class KslBlock(blockName: String, parentScope: KslScopeBuilder) : KslStatement(blockName, parentScope) {

    private val inputs = mutableListOf<BlockInput>()
    private val outputs = mutableListOf<KslValue<*>>()

    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage).apply { scopeName = blockName }

    init {
        childScopes += body
    }

    protected fun inFloat1(defaultValue: KslScalarExpression<KslTypeFloat1>? = null, name: String? = null): ScalarInput<KslTypeFloat1> =
        ScalarInput(body.floatVar(defaultValue, name)).also { inputs += it }
    protected fun inFloat2(defaultValue: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat2, KslTypeFloat1> =
        VectorInput(body.float2Var(defaultValue, name)).also { inputs += it }
    protected fun inFloat3(defaultValue: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat3, KslTypeFloat1> =
        VectorInput(body.float3Var(defaultValue, name)).also { inputs += it }
    protected fun inFloat4(defaultValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat4, KslTypeFloat1> =
        VectorInput(body.float4Var(defaultValue, name)).also { inputs += it }

    protected fun inInt1(defaultValue: KslScalarExpression<KslTypeInt1>? = null, name: String? = null): ScalarInput<KslTypeInt1> =
        ScalarInput(body.intVar(defaultValue, name)).also { inputs += it }
    protected fun inInt2(defaultValue: KslVectorExpression<KslTypeInt2, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt2, KslTypeInt1> =
        VectorInput(body.int2Var(defaultValue, name)).also { inputs += it }
    protected fun inInt3(defaultValue: KslVectorExpression<KslTypeInt3, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt3, KslTypeInt1> =
        VectorInput(body.int3Var(defaultValue, name)).also { inputs += it }
    protected fun inInt4(defaultValue: KslVectorExpression<KslTypeInt4, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt4, KslTypeInt1> =
        VectorInput(body.int4Var(defaultValue, name)).also { inputs += it }

    protected fun inMat2(defaultValue: KslMatrixExpression<KslTypeMat2, KslTypeFloat2>? = null, name: String? = null): MatrixInput<KslTypeMat2, KslTypeFloat2> =
        MatrixInput(body.mat2Var(defaultValue, name)).also { inputs += it }
    protected fun inMat3(defaultValue: KslMatrixExpression<KslTypeMat3, KslTypeFloat3>? = null, name: String? = null): MatrixInput<KslTypeMat3, KslTypeFloat3> =
        MatrixInput(body.mat3Var(defaultValue, name)).also { inputs += it }
    protected fun inMat4(defaultValue: KslMatrixExpression<KslTypeMat4, KslTypeFloat4>? = null, name: String? = null): MatrixInput<KslTypeMat4, KslTypeFloat4> =
        MatrixInput(body.mat4Var(defaultValue, name)).also { inputs += it }

    protected fun outFloat1(name: String? = null): KslVarScalar<KslTypeFloat1> = parentScopeBuilder.floatVar(name = name).also { outputs += it }
    protected fun outFloat2(name: String? = null): KslVarVector<KslTypeFloat2, KslTypeFloat1> = parentScopeBuilder.float2Var(name = name).also { outputs += it }
    protected fun outFloat3(name: String? = null): KslVarVector<KslTypeFloat3, KslTypeFloat1> = parentScopeBuilder.float3Var(name = name).also { outputs += it }
    protected fun outFloat4(name: String? = null): KslVarVector<KslTypeFloat4, KslTypeFloat1> = parentScopeBuilder.float4Var(name = name).also { outputs += it }

    protected fun outInt1(name: String? = null): KslVarScalar<KslTypeInt1> = parentScopeBuilder.intVar(name = name).also { outputs += it }
    protected fun outInt2(name: String? = null): KslVarVector<KslTypeInt2, KslTypeInt1> = parentScopeBuilder.int2Var(name = name).also { outputs += it }
    protected fun outInt3(name: String? = null): KslVarVector<KslTypeInt3, KslTypeInt1> = parentScopeBuilder.int3Var(name = name).also { outputs += it }
    protected fun outInt4(name: String? = null): KslVarVector<KslTypeInt4, KslTypeInt1> = parentScopeBuilder.int4Var(name = name).also { outputs += it }

    protected fun outMat2(name: String? = null): KslVarMatrix<KslTypeMat2, KslTypeFloat2> = parentScopeBuilder.mat2Var(name = name).also { outputs += it }
    protected fun outMat3(name: String? = null): KslVarMatrix<KslTypeMat3, KslTypeFloat3> = parentScopeBuilder.mat3Var(name = name).also { outputs += it }
    protected fun outMat4(name: String? = null): KslVarMatrix<KslTypeMat4, KslTypeFloat4> = parentScopeBuilder.mat4Var(name = name).also { outputs += it }

    override fun validate() {
        super.validate()
        inputs.forEach {
            if (it.declareOp.initExpression == null) {
                throw IllegalStateException("Missing input value for input ${it.declareOp.declareValue.stateName} of block $opName")
            }
        }
    }

    override fun toPseudoCode() = body.toPseudoCode()

    protected abstract class BlockInput {
        abstract val declareOp: KslDeclareValue
    }

    protected inner class ScalarInput<S>(val kslVar: KslVarScalar<S>) : BlockInput() where S: KslType, S: KslScalar {
        override val declareOp = body.ops.first { it is KslDeclareValue && it.declareValue === kslVar } as KslDeclareValue

        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslScalarExpression<S> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslScalarExpression<S>) {
            declareOp.initExpression = value
        }
    }

    protected inner class VectorInput<V, S>(val kslVar: KslVarVector<V, S>) : BlockInput() where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        override val declareOp = body.ops.first { it is KslDeclareValue && it.declareValue === kslVar } as KslDeclareValue

        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslVectorExpression<V, S> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslVectorExpression<V, S>) {
            declareOp.initExpression = value
        }
    }

    protected inner class MatrixInput<M, V>(val kslVar: KslVarMatrix<M, V>) : BlockInput() where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
        override val declareOp = body.ops.first { it is KslDeclareValue && it.declareValue === kslVar } as KslDeclareValue

        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslMatrixExpression<M, V> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslMatrixExpression<M, V>) {
            declareOp.initExpression = value
        }
    }
}
