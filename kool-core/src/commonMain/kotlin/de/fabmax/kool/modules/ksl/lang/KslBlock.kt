package de.fabmax.kool.modules.ksl.lang

import kotlin.reflect.KProperty

abstract class KslBlock(blockName: String, parentScope: KslScopeBuilder) : KslStatement(blockName, parentScope) {

    private val inputVars = mutableListOf<KslVar<*>>()
    private val inputVarValues = mutableMapOf<KslVar<*>, KslExpression<*>>()
    private val outputs = mutableListOf<KslValue<*>>()

    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage).apply { scopeName = blockName }

    init {
        childScopes += body
    }

    private fun addInputVar(inVar: KslVar<*>, initExpr: KslExpression<*>?) {
        inputVars += inVar
        initExpr?.let { inputVarValues[inVar] = it }
    }

    protected fun inFloat1(defaultValue: KslScalarExpression<KslTypeFloat1>? = null, name: String? = null): ScalarInput<KslTypeFloat1> =
        ScalarInput(body.floatVar(name).also { addInputVar(it, defaultValue) })
    protected fun inFloat2(defaultValue: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat2, KslTypeFloat1> =
        VectorInput(body.float2Var(name).also { addInputVar(it, defaultValue) })
    protected fun inFloat3(defaultValue: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat3, KslTypeFloat1> =
        VectorInput(body.float3Var(name).also { addInputVar(it, defaultValue) })
    protected fun inFloat4(defaultValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>? = null, name: String? = null): VectorInput<KslTypeFloat4, KslTypeFloat1> =
        VectorInput(body.float4Var(name).also { addInputVar(it, defaultValue) })

    protected fun inInt1(defaultValue: KslScalarExpression<KslTypeInt1>? = null, name: String? = null): ScalarInput<KslTypeInt1> =
        ScalarInput(body.intVar(name).also { addInputVar(it, defaultValue) })
    protected fun inInt2(defaultValue: KslVectorExpression<KslTypeInt2, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt2, KslTypeInt1> =
        VectorInput(body.int2Var(name).also { addInputVar(it, defaultValue) })
    protected fun inInt3(defaultValue: KslVectorExpression<KslTypeInt3, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt3, KslTypeInt1> =
        VectorInput(body.int3Var(name).also { addInputVar(it, defaultValue) })
    protected fun inInt4(defaultValue: KslVectorExpression<KslTypeInt4, KslTypeInt1>? = null, name: String? = null): VectorInput<KslTypeInt4, KslTypeInt1> =
        VectorInput(body.int4Var(name).also { addInputVar(it, defaultValue) })

    protected fun inMat2(defaultValue: KslMatrixExpression<KslTypeMat2, KslTypeFloat2>? = null, name: String? = null): MatrixInput<KslTypeMat2, KslTypeFloat2> =
        MatrixInput(body.mat2Var(name).also { addInputVar(it, defaultValue) })
    protected fun inMat3(defaultValue: KslMatrixExpression<KslTypeMat3, KslTypeFloat3>? = null, name: String? = null): MatrixInput<KslTypeMat3, KslTypeFloat3> =
        MatrixInput(body.mat3Var(name).also { addInputVar(it, defaultValue) })
    protected fun inMat4(defaultValue: KslMatrixExpression<KslTypeMat4, KslTypeFloat4>? = null, name: String? = null): MatrixInput<KslTypeMat4, KslTypeFloat4> =
        MatrixInput(body.mat4Var(name).also { addInputVar(it, defaultValue) })

    protected fun outFloat1(name: String? = null): KslVarScalar<KslTypeFloat1> = parentScopeBuilder.floatVar(name).also { outputs += it }
    protected fun outFloat2(name: String? = null): KslVarVector<KslTypeFloat2, KslTypeFloat1> = parentScopeBuilder.float2Var(name).also { outputs += it }
    protected fun outFloat3(name: String? = null): KslVarVector<KslTypeFloat3, KslTypeFloat1> = parentScopeBuilder.float3Var(name).also { outputs += it }
    protected fun outFloat4(name: String? = null): KslVarVector<KslTypeFloat4, KslTypeFloat1> = parentScopeBuilder.float4Var(name).also { outputs += it }

    protected fun outInt1(name: String? = null): KslVarScalar<KslTypeInt1> = parentScopeBuilder.intVar(name).also { outputs += it }
    protected fun outInt2(name: String? = null): KslVarVector<KslTypeInt2, KslTypeInt1> = parentScopeBuilder.int2Var(name).also { outputs += it }
    protected fun outInt3(name: String? = null): KslVarVector<KslTypeInt3, KslTypeInt1> = parentScopeBuilder.int3Var(name).also { outputs += it }
    protected fun outInt4(name: String? = null): KslVarVector<KslTypeInt4, KslTypeInt1> = parentScopeBuilder.int4Var(name).also { outputs += it }

    protected fun outMat2(name: String? = null): KslVarMatrix<KslTypeMat2, KslTypeFloat2> = parentScopeBuilder.mat2Var(name).also { outputs += it }
    protected fun outMat3(name: String? = null): KslVarMatrix<KslTypeMat3, KslTypeFloat3> = parentScopeBuilder.mat3Var(name).also { outputs += it }
    protected fun outMat4(name: String? = null): KslVarMatrix<KslTypeMat4, KslTypeFloat4> = parentScopeBuilder.mat4Var(name).also { outputs += it }

    override fun validate() {
        super.validate()
        inputVars.forEach {
            if (inputVarValues[it] == null) {
                throw IllegalStateException("Missing input value for input ${it.stateName} of block $opName")
            }
        }
    }

    override fun updateModel() {
        dependencies.clear()
        mutations.clear()
        inputVarValues.forEach { (inVar, expr) ->
            body.initExpressions[inVar] = expr
            addExpressionDependencies(expr)
        }
        super.updateModel()
    }

    override fun toPseudoCode() = body.toPseudoCode()

    protected inner class ScalarInput<S>(val kslVar: KslVarScalar<S>) where S: KslType, S: KslScalar {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslScalarExpression<S> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslScalarExpression<S>) {
            inputVarValues[kslVar] = value
        }
    }

    protected inner class VectorInput<V, S>(val kslVar: KslVarVector<V, S>) where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslVectorExpression<V, S> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslVectorExpression<V, S>) {
            inputVarValues[kslVar] = value
        }
    }

    protected inner class MatrixInput<M, V>(val kslVar: KslVarMatrix<M, V>) where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
        operator fun getValue(thisRef: Any?, property: KProperty<*>): KslMatrixExpression<M, V> {
            return kslVar
        }

        operator fun setValue(thisRef: Any?, property: KProperty<*>, value: KslMatrixExpression<M, V>) {
            inputVarValues[kslVar] = value
        }
    }
}
