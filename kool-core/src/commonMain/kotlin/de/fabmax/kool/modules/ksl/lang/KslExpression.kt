package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

interface KslExpression<T: KslType> {
    val expressionType: T

    fun collectStateDependencies(): Set<KslMutatedState>
    fun generateExpression(generator: KslGenerator): String
    fun toPseudoCode(): String
}

interface KslScalarExpression<S> : KslExpression<S> where S: KslType, S: KslScalar
interface KslVectorExpression<V, S> : KslExpression<V> where V: KslType, V: KslVector<S>, S: KslScalar
interface KslMatrixExpression<M, V> : KslExpression<M> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

interface KslArrayExpression<T: KslType> : KslExpression<KslTypeArray<T>>
interface KslScalarArrayExpression<S> : KslExpression<KslTypeArray<S>> where S: KslType, S: KslScalar
interface KslVectorArrayExpression<V, S> : KslExpression<KslTypeArray<V>> where V: KslType, V: KslVector<S>, S: KslScalar
interface KslMatrixArrayExpression<M, V> : KslExpression<KslTypeArray<M>> where M: KslType, M: KslMatrix<V>, V: KslVector<*>
interface KslGenericArrayExpression<T: KslType> : KslExpression<KslTypeArray<T>>

typealias KslExprFloat1 = KslScalarExpression<KslTypeFloat1>
typealias KslExprFloat2 = KslVectorExpression<KslTypeFloat2, KslTypeFloat1>
typealias KslExprFloat3 = KslVectorExpression<KslTypeFloat3, KslTypeFloat1>
typealias KslExprFloat4 = KslVectorExpression<KslTypeFloat4, KslTypeFloat1>

typealias KslExprInt1 = KslScalarExpression<KslTypeInt1>
typealias KslExprInt2 = KslVectorExpression<KslTypeInt2, KslTypeInt1>
typealias KslExprInt3 = KslVectorExpression<KslTypeInt3, KslTypeInt1>
typealias KslExprInt4 = KslVectorExpression<KslTypeInt4, KslTypeInt1>

typealias KslExprBool1 = KslScalarExpression<KslTypeBool1>
typealias KslExprBool2 = KslVectorExpression<KslTypeBool2, KslTypeBool1>
typealias KslExprBool3 = KslVectorExpression<KslTypeBool3, KslTypeBool1>
typealias KslExprBool4 = KslVectorExpression<KslTypeBool4, KslTypeBool1>

typealias KslExprMat2 = KslMatrixExpression<KslTypeMat2, KslTypeFloat2>
typealias KslExprMat3 = KslMatrixExpression<KslTypeMat3, KslTypeFloat3>
typealias KslExprMat4 = KslMatrixExpression<KslTypeMat4, KslTypeFloat4>

typealias KslExprFloat1Array = KslScalarArrayExpression<KslTypeFloat1>
typealias KslExprFloat2Array = KslVectorArrayExpression<KslTypeFloat2, KslTypeFloat1>
typealias KslExprFloat3Array = KslVectorArrayExpression<KslTypeFloat3, KslTypeFloat1>
typealias KslExprFloat4Array = KslVectorArrayExpression<KslTypeFloat4, KslTypeFloat1>

typealias KslExprInt1Array = KslScalarArrayExpression<KslTypeInt1>
typealias KslExprInt2Array = KslVectorArrayExpression<KslTypeInt2, KslTypeInt1>
typealias KslExprInt3Array = KslVectorArrayExpression<KslTypeInt3, KslTypeInt1>
typealias KslExprInt4Array = KslVectorArrayExpression<KslTypeInt4, KslTypeInt1>

typealias KslExprBool1Array = KslScalarArrayExpression<KslTypeBool1>
typealias KslExprBool2Array = KslVectorArrayExpression<KslTypeBool2, KslTypeBool1>
typealias KslExprBool3Array = KslVectorArrayExpression<KslTypeBool3, KslTypeBool1>
typealias KslExprBool4Array = KslVectorArrayExpression<KslTypeBool4, KslTypeBool1>

typealias KslExprMat2Array = KslMatrixArrayExpression<KslTypeMat2, KslTypeFloat2>
typealias KslExprMat3Array = KslMatrixArrayExpression<KslTypeMat3, KslTypeFloat3>
typealias KslExprMat4Array = KslMatrixArrayExpression<KslTypeMat4, KslTypeFloat4>
