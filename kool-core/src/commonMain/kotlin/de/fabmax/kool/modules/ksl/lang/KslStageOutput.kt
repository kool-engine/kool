package de.fabmax.kool.modules.ksl.lang

abstract class KslStageOutput<T: KslType>(open val value: KslVar<T>)
    : KslExpression<T> by value, KslAssignable<T> by value {

    var location = -1

    override fun toPseudoCode() = value.toPseudoCode()
}

class KslStageOutputScalar<S>(value: KslVarScalar<S>)
    : KslStageOutput<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar {

    override val value: KslVarScalar<S>
        get() = super.value as KslVarScalar<S>
}

class KslStageOutputVector<V, S>(value: KslVarVector<V, S>)
    : KslStageOutput<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar {

    @Suppress("UNCHECKED_CAST")
    override val value: KslVarVector<V, S>
        get() = super.value as KslVarVector<V, S>
}
