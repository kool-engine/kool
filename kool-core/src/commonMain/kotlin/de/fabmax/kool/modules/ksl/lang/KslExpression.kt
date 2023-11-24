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

interface KslArrayExpression<T: KslType> : KslExpression<KslArrayType<T>>
interface KslScalarArrayExpression<S> : KslExpression<KslArrayType<S>> where S: KslType, S: KslScalar
interface KslVectorArrayExpression<V, S> : KslExpression<KslArrayType<V>> where V: KslType, V: KslVector<S>, S: KslScalar
interface KslMatrixArrayExpression<M, V> : KslExpression<KslArrayType<M>> where M: KslType, M: KslMatrix<V>, V: KslVector<*>
interface KslGenericArrayExpression<T: KslType> : KslExpression<KslArrayType<T>>

typealias KslExprFloat1 = KslScalarExpression<KslFloat1>
typealias KslExprFloat2 = KslVectorExpression<KslFloat2, KslFloat1>
typealias KslExprFloat3 = KslVectorExpression<KslFloat3, KslFloat1>
typealias KslExprFloat4 = KslVectorExpression<KslFloat4, KslFloat1>

typealias KslExprInt1 = KslScalarExpression<KslInt1>
typealias KslExprInt2 = KslVectorExpression<KslInt2, KslInt1>
typealias KslExprInt3 = KslVectorExpression<KslInt3, KslInt1>
typealias KslExprInt4 = KslVectorExpression<KslInt4, KslInt1>

typealias KslExprUint1 = KslScalarExpression<KslUint1>
typealias KslExprUint2 = KslVectorExpression<KslUint2, KslUint1>
typealias KslExprUint3 = KslVectorExpression<KslUint3, KslUint1>
typealias KslExprUint4 = KslVectorExpression<KslUint4, KslUint1>

typealias KslExprBool1 = KslScalarExpression<KslBool1>
typealias KslExprBool2 = KslVectorExpression<KslBool2, KslBool1>
typealias KslExprBool3 = KslVectorExpression<KslBool3, KslBool1>
typealias KslExprBool4 = KslVectorExpression<KslBool4, KslBool1>

typealias KslExprMat2 = KslMatrixExpression<KslMat2, KslFloat2>
typealias KslExprMat3 = KslMatrixExpression<KslMat3, KslFloat3>
typealias KslExprMat4 = KslMatrixExpression<KslMat4, KslFloat4>

typealias KslExprFloat1Array = KslScalarArrayExpression<KslFloat1>
typealias KslExprFloat2Array = KslVectorArrayExpression<KslFloat2, KslFloat1>
typealias KslExprFloat3Array = KslVectorArrayExpression<KslFloat3, KslFloat1>
typealias KslExprFloat4Array = KslVectorArrayExpression<KslFloat4, KslFloat1>

typealias KslExprInt1Array = KslScalarArrayExpression<KslInt1>
typealias KslExprInt2Array = KslVectorArrayExpression<KslInt2, KslInt1>
typealias KslExprInt3Array = KslVectorArrayExpression<KslInt3, KslInt1>
typealias KslExprInt4Array = KslVectorArrayExpression<KslInt4, KslInt1>

typealias KslExprUint1Array = KslScalarArrayExpression<KslUint1>
typealias KslExprUint2Array = KslVectorArrayExpression<KslUint2, KslUint1>
typealias KslExprUint3Array = KslVectorArrayExpression<KslUint3, KslUint1>
typealias KslExprUint4Array = KslVectorArrayExpression<KslUint4, KslUint1>

typealias KslExprBool1Array = KslScalarArrayExpression<KslBool1>
typealias KslExprBool2Array = KslVectorArrayExpression<KslBool2, KslBool1>
typealias KslExprBool3Array = KslVectorArrayExpression<KslBool3, KslBool1>
typealias KslExprBool4Array = KslVectorArrayExpression<KslBool4, KslBool1>

typealias KslExprMat2Array = KslMatrixArrayExpression<KslMat2, KslFloat2>
typealias KslExprMat3Array = KslMatrixArrayExpression<KslMat3, KslFloat3>
typealias KslExprMat4Array = KslMatrixArrayExpression<KslMat4, KslFloat4>
