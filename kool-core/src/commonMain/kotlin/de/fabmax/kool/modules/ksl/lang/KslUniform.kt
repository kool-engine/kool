package de.fabmax.kool.modules.ksl.lang

abstract class KslUniform<T: KslType>(val value: KslValue<T>) : KslExpression<T> by value

class KslUniformScalar<S>(value: KslValue<S>) : KslUniform<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslUniformVector<V, S>(value: KslValue<V>) : KslUniform<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslUniformMatrix<M, V>(value: KslValue<M>) : KslUniform<M>(value), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>