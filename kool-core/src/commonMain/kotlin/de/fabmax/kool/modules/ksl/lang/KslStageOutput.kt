package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslStateType

abstract class KslStageOutput<T: KslType>(open val value: KslVar<T>, stateType: KslStateType)
    : KslExpression<T> by value, KslAssignable<T> by value {

    var location = -1

    init {
        value.stateType = stateType
    }

    override fun toPseudoCode() = value.toPseudoCode()
}

class KslStageOutputScalar<S>(value: KslVarScalar<S>, stateType: KslStateType)
    : KslStageOutput<S>(value, stateType), KslScalarExpression<S> where S: KslType, S: KslScalar {

    override val value: KslVarScalar<S>
        get() = super.value as KslVarScalar<S>
}

class KslStageOutputVector<V, S>(value: KslVarVector<V, S>, stateType: KslStateType)
    : KslStageOutput<V>(value, stateType), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar {

    @Suppress("UNCHECKED_CAST")
    override val value: KslVarVector<V, S>
        get() = super.value as KslVarVector<V, S>
}
