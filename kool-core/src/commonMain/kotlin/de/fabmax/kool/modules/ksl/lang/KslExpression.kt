package de.fabmax.kool.modules.ksl.lang

interface KslExpression<T: KslType> {
    val expressionType: T

    fun collectSubExpressions(): List<KslExpression<*>>
    fun toPseudoCode(): String

    fun collectRecursive(vararg exprs: KslExpression<*>?): List<KslExpression<*>> {
        return exprs.filterNotNull().flatMap { it.collectSubExpressions() } + this
    }
}

class KslInjectedExpression<T: KslType>(val expr: KslExpression<T>): KslExpression<T> by expr

interface KslScalarExpression<S> : KslExpression<S> where S: KslType, S: KslScalar
interface KslVectorExpression<V, S> : KslExpression<V> where V: KslType, V: KslVector<S>, S: KslScalar
interface KslMatrixExpression<M, V> : KslExpression<M> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

interface KslArrayExpression<T: KslType> : KslExpression<KslArrayType<T>>
interface KslScalarArrayExpression<S> : KslExpression<KslArrayType<S>> where S: KslType, S: KslScalar
interface KslVectorArrayExpression<V, S> : KslExpression<KslArrayType<V>> where V: KslType, V: KslVector<S>, S: KslScalar
interface KslMatrixArrayExpression<M, V> : KslExpression<KslArrayType<M>> where M: KslType, M: KslMatrix<V>, V: KslVector<*>
interface KslGenericArrayExpression<T: KslType> : KslExpression<KslArrayType<T>>

typealias KslExprStruct<S> = KslExpression<KslStruct<S>>

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

typealias KslVarFloat1 = KslVarScalar<KslFloat1>
typealias KslVarFloat2 = KslVarVector<KslFloat2, KslFloat1>
typealias KslVarFloat3 = KslVarVector<KslFloat3, KslFloat1>
typealias KslVarFloat4 = KslVarVector<KslFloat4, KslFloat1>

typealias KslVarInt1 = KslVarScalar<KslInt1>
typealias KslVarInt2 = KslVarVector<KslInt2, KslInt1>
typealias KslVarInt3 = KslVarVector<KslInt3, KslInt1>
typealias KslVarInt4 = KslVarVector<KslInt4, KslInt1>

typealias KslVarUint1 = KslVarScalar<KslUint1>
typealias KslVarUint2 = KslVarVector<KslUint2, KslUint1>
typealias KslVarUint3 = KslVarVector<KslUint3, KslUint1>
typealias KslVarUint4 = KslVarVector<KslUint4, KslUint1>

typealias KslVarBool1 = KslVarScalar<KslBool1>
typealias KslVarBool2 = KslVarVector<KslBool2, KslBool1>
typealias KslVarBool3 = KslVarVector<KslBool3, KslBool1>
typealias KslVarBool4 = KslVarVector<KslBool4, KslBool1>

typealias KslVarMat2 = KslVarMatrix<KslMat2, KslFloat2>
typealias KslVarMat3 = KslVarMatrix<KslMat3, KslFloat3>
typealias KslVarMat4 = KslVarMatrix<KslMat4, KslFloat4>