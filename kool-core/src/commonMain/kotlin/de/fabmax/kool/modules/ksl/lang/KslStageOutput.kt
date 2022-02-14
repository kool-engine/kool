package de.fabmax.kool.modules.ksl.lang

abstract class KslStageOutput<T: KslType>(val value: KslVar<T>)
    : KslExpression<T> by value, KslAssignable<T> by value {

    var location = -1

    override fun toPseudoCode() = value.toPseudoCode()
}

class KslStageOutputScalar<S>(value: KslVar<S>)
    : KslStageOutput<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslStageOutputVector<V, S>(value: KslVar<V>)
    : KslStageOutput<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
