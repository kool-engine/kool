package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslStateType

abstract class KslStageInput<T: KslType>(val value: KslValue<T>, stateType: KslStateType) : KslExpression<T> by value {
    init {
        value.stateType = stateType
    }
}

class KslStageInputScalar<S>(value: KslValue<S>, stateType: KslStateType)
    : KslStageInput<S>(value, stateType), KslScalarExpression<S> where S: KslType, S: KslScalar

class KslStageInputVector<V, S>(value: KslValue<V>, stateType: KslStateType)
    : KslStageInput<V>(value, stateType), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
