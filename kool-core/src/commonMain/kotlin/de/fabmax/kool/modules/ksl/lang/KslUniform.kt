package de.fabmax.kool.modules.ksl.lang

open class KslUniform<T: KslType>(val value: KslValue<T>, val arraySize: Int = -1) : KslExpression<T> by value {
    val name: String
        get() = value.stateName
}

class KslUniformScalar<S>(value: KslValue<S>) : KslUniform<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslUniformVector<V, S>(value: KslValue<V>) : KslUniform<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslUniformMatrix<M, V>(value: KslValue<M>) : KslUniform<M>(value), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>