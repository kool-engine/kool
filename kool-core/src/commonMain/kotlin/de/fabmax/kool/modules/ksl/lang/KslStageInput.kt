package de.fabmax.kool.modules.ksl.lang

abstract class KslStageInput<T: KslType>(val value: KslValue<T>) : KslExpression<T> by value

class KslStageInputScalar<S>(value: KslValue<S>)
    : KslStageInput<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar

class KslStageInputVector<V, S>(value: KslValue<V>)
    : KslStageInput<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
