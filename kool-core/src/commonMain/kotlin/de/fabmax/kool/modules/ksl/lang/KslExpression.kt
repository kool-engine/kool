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
