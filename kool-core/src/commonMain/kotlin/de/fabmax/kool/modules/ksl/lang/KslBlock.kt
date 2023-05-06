package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslBlock(blockName: String, parentScope: KslScopeBuilder) : KslStatement(blockName, parentScope) {

    private val inputDependencies = mutableMapOf<BlockInput<*, *>, Set<KslMutatedState>>()
    private val outputs = mutableListOf<KslValue<*>>()

    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage).apply { scopeName = blockName }

    init {
        childScopes += body
    }

    private fun nextName(suffix: String): String = parentScopeBuilder.parentStage.program.nextName("${opName}_$suffix")

    protected fun inFloat1(name: String? = null, defaultValue: KslScalarExpression<KslTypeFloat1>? = null): ScalarInput<KslTypeFloat1> =
        ScalarInput(name ?: nextName("inF1"), KslTypeFloat1, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat2(name: String? = null, defaultValue: KslVectorExpression<KslTypeFloat2, KslTypeFloat1>? = null): VectorInput<KslTypeFloat2, KslTypeFloat1> =
        VectorInput(name ?: nextName("inF2"), KslTypeFloat2, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat3(name: String? = null, defaultValue: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>? = null): VectorInput<KslTypeFloat3, KslTypeFloat1> =
        VectorInput(name ?: nextName("inF3"), KslTypeFloat3, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat4(name: String? = null, defaultValue: KslVectorExpression<KslTypeFloat4, KslTypeFloat1>? = null): VectorInput<KslTypeFloat4, KslTypeFloat1> =
        VectorInput(name ?: nextName("inF4"), KslTypeFloat4, defaultValue).also {
            updateDependencies(it, defaultValue)
        }

    protected fun inInt1(name: String? = null, defaultValue: KslScalarExpression<KslTypeInt1>? = null): ScalarInput<KslTypeInt1> =
        ScalarInput(name ?: nextName("inI1"), KslTypeInt1, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inInt2(name: String? = null, defaultValue: KslVectorExpression<KslTypeInt2, KslTypeInt1>? = null): VectorInput<KslTypeInt2, KslTypeInt1> =
        VectorInput(name ?: nextName("inI2"), KslTypeInt2, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inInt3(name: String? = null, defaultValue: KslVectorExpression<KslTypeInt3, KslTypeInt1>? = null): VectorInput<KslTypeInt3, KslTypeInt1> =
        VectorInput(name ?: nextName("inI3"), KslTypeInt3, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inInt4(name: String? = null, defaultValue: KslVectorExpression<KslTypeInt4, KslTypeInt1>? = null): VectorInput<KslTypeInt4, KslTypeInt1> =
        VectorInput(name ?: nextName("inI4"), KslTypeInt4, defaultValue).also {
            updateDependencies(it, defaultValue)
        }

    protected fun inMat2(name: String? = null, defaultValue: KslMatrixExpression<KslTypeMat2, KslTypeFloat2>? = null): MatrixInput<KslTypeMat2, KslTypeFloat2> =
        MatrixInput(name ?: nextName("inM2"), KslTypeMat2, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inMat3(name: String? = null, defaultValue: KslMatrixExpression<KslTypeMat3, KslTypeFloat3>? = null): MatrixInput<KslTypeMat3, KslTypeFloat3> =
        MatrixInput(name ?: nextName("inM3"), KslTypeMat3, defaultValue).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inMat4(name: String? = null, defaultValue: KslMatrixExpression<KslTypeMat4, KslTypeFloat4>? = null): MatrixInput<KslTypeMat4, KslTypeFloat4> =
        MatrixInput(name ?: nextName("inM4"), KslTypeMat4, defaultValue).also {
            updateDependencies(it, defaultValue)
        }

    protected fun inFloat1Array(arraySize: Int, name: String? = null, defaultValue: KslScalarArrayExpression<KslTypeFloat1>? = null): ScalarArrayInput<KslTypeFloat1> =
        ScalarArrayInput(name ?: nextName("inArrF1"), arraySize, KslTypeFloat1).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat2Array(arraySize: Int, name: String? = null, defaultValue: KslVectorArrayExpression<KslTypeFloat2, KslTypeFloat1>? = null): VectorArrayInput<KslTypeFloat2, KslTypeFloat1> =
        VectorArrayInput(name ?: nextName("inArrF2"), arraySize, KslTypeFloat2).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat3Array(arraySize: Int, name: String? = null, defaultValue: KslVectorArrayExpression<KslTypeFloat3, KslTypeFloat1>? = null): VectorArrayInput<KslTypeFloat3, KslTypeFloat1> =
        VectorArrayInput(name ?: nextName("inArrF3"), arraySize, KslTypeFloat3).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inFloat4Array(arraySize: Int, name: String? = null, defaultValue: KslVectorArrayExpression<KslTypeFloat4, KslTypeFloat1>? = null): VectorArrayInput<KslTypeFloat4, KslTypeFloat1> =
        VectorArrayInput(name ?: nextName("inArrF4"), arraySize, KslTypeFloat4).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inMat2Array(arraySize: Int, name: String? = null, defaultValue: KslMatrixArrayExpression<KslTypeMat2, KslTypeFloat2>? = null): MatrixArrayInput<KslTypeMat2, KslTypeFloat2> =
        MatrixArrayInput(name ?: nextName("inArrMat2"), arraySize, KslTypeMat2).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inMat3Array(arraySize: Int, name: String? = null, defaultValue: KslMatrixArrayExpression<KslTypeMat3, KslTypeFloat3>? = null): MatrixArrayInput<KslTypeMat3, KslTypeFloat3> =
        MatrixArrayInput(name ?: nextName("inArrMat3"), arraySize, KslTypeMat3).also {
            updateDependencies(it, defaultValue)
        }
    protected fun inMat4Array(arraySize: Int, name: String? = null, defaultValue: KslMatrixArrayExpression<KslTypeMat4, KslTypeFloat4>? = null): MatrixArrayInput<KslTypeMat4, KslTypeFloat4> =
        MatrixArrayInput(name ?: nextName("inArrMat4"), arraySize, KslTypeMat4).also {
            updateDependencies(it, defaultValue)
        }

    protected fun outFloat1(name: String? = null): KslVarScalar<KslTypeFloat1> = parentScopeBuilder.float1Var(name = nextName(name ?: "outF1")).also { outputs += it }
    protected fun outFloat2(name: String? = null): KslVarVector<KslTypeFloat2, KslTypeFloat1> = parentScopeBuilder.float2Var(name = nextName(name ?: "outF2")).also { outputs += it }
    protected fun outFloat3(name: String? = null): KslVarVector<KslTypeFloat3, KslTypeFloat1> = parentScopeBuilder.float3Var(name = nextName(name ?: "outF3")).also { outputs += it }
    protected fun outFloat4(name: String? = null): KslVarVector<KslTypeFloat4, KslTypeFloat1> = parentScopeBuilder.float4Var(name = nextName(name ?: "outF4")).also { outputs += it }

    protected fun outInt1(name: String? = null): KslVarScalar<KslTypeInt1> = parentScopeBuilder.int1Var(name = nextName(name ?: "outI1")).also { outputs += it }
    protected fun outInt2(name: String? = null): KslVarVector<KslTypeInt2, KslTypeInt1> = parentScopeBuilder.int2Var(name = nextName(name ?: "outI2")).also { outputs += it }
    protected fun outInt3(name: String? = null): KslVarVector<KslTypeInt3, KslTypeInt1> = parentScopeBuilder.int3Var(name = nextName(name ?: "outI3")).also { outputs += it }
    protected fun outInt4(name: String? = null): KslVarVector<KslTypeInt4, KslTypeInt1> = parentScopeBuilder.int4Var(name = nextName(name ?: "outI4")).also { outputs += it }

    protected fun outMat2(name: String? = null): KslVarMatrix<KslTypeMat2, KslTypeFloat2> = parentScopeBuilder.mat2Var(name = nextName(name ?: "outM2")).also { outputs += it }
    protected fun outMat3(name: String? = null): KslVarMatrix<KslTypeMat3, KslTypeFloat3> = parentScopeBuilder.mat3Var(name = nextName(name ?: "outM3")).also { outputs += it }
    protected fun outMat4(name: String? = null): KslVarMatrix<KslTypeMat4, KslTypeFloat4> = parentScopeBuilder.mat4Var(name = nextName(name ?: "outM4")).also { outputs += it }

    private fun updateDependencies(input: BlockInput<*, *>, newExpression: KslExpression<*>?) {
        // collect dependencies of new input expression
        inputDependencies[input] = newExpression?.collectStateDependencies() ?: emptySet()

        // update dependencies of block
        dependencies.clear()
        inputDependencies.values.forEach { deps ->
            deps.forEach {
                dependencies[it.state] = it
            }
        }
    }

    override fun validate() {
        super.validate()
        inputDependencies.keys.forEach {
            if (it.input == null) {
                throw IllegalStateException("Missing input value for input ${it.name} of block $opName")
            }
        }
    }

    abstract inner class BlockInput<T: KslType, E: KslExpression<T>>(
        val name: String,
        override val expressionType: T,
        defaultValue: E?
    ) : KslExpression<T> {

        var input: E? = defaultValue
            set(value) {
                updateDependencies(this, value)
                field = value
            }

        operator fun invoke(assignExpression: E) {
            input = assignExpression
        }

        // return empty dependencies here - actual dependencies to input expression are managed by outer block statement
        override fun collectStateDependencies(): Set<KslMutatedState> = emptySet()

        override fun generateExpression(generator: KslGenerator): String {
            return input?.generateExpression(generator)
                ?: throw IllegalStateException("Missing input value for input $name of block $opName")
        }

        override fun toPseudoCode(): String {
            return input?.toPseudoCode()
                ?: throw IllegalStateException("Missing input value for input $name of block $opName")
        }
    }

    inner class ScalarInput<S>(name: String, expressionType: S, defaultValue: KslScalarExpression<S>?) :
        BlockInput<S, KslScalarExpression<S>>(name, expressionType, defaultValue), KslScalarExpression<S>
            where S : KslType, S : KslScalar

    inner class VectorInput<V, S>(name: String, expressionType: V, defaultValue: KslVectorExpression<V, S>?) :
        BlockInput<V, KslVectorExpression<V, S>>(name, expressionType, defaultValue), KslVectorExpression<V, S>
            where V : KslType, V : KslVector<S>, S : KslType, S : KslScalar

    inner class MatrixInput<M, V>(name: String, expressionType: M, defaultValue: KslMatrixExpression<M, V>?) :
        BlockInput<M, KslMatrixExpression<M, V>>(name, expressionType, defaultValue), KslMatrixExpression<M, V>
            where M : KslType, M : KslMatrix<V>, V : KslType, V : KslVector<*>

    inner class ScalarArrayInput<S>(name: String, arraySize: Int, expressionType: S) :
        BlockInput<KslTypeArray<S>, KslScalarArrayExpression<S>>(name, KslTypeArray(expressionType, arraySize), null), KslScalarArrayExpression<S>
            where S : KslType, S : KslScalar

    inner class VectorArrayInput<V, S>(name: String, arraySize: Int, expressionType: V) :
        BlockInput<KslTypeArray<V>, KslVectorArrayExpression<V, S>>(name, KslTypeArray(expressionType, arraySize), null), KslVectorArrayExpression<V, S>
            where V : KslType, V : KslVector<S>, S : KslType, S : KslScalar

    inner class MatrixArrayInput<M, V>(name: String, arraySize: Int, expressionType: M) :
        BlockInput<KslTypeArray<M>, KslMatrixArrayExpression<M, V>>(name, KslTypeArray(expressionType, arraySize), null), KslMatrixArrayExpression<M, V>
            where M : KslType, M : KslMatrix<V>, V : KslType, V : KslVector<*>

}
