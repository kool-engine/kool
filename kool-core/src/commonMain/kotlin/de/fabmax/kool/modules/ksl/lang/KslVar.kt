package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.util.Struct

open class KslVar<T: KslType>(name: String, type: T, isMutable: Boolean)
    : KslValue<T>(name, isMutable), KslAssignable<T> {

    override val expressionType = type
    override val assignType = type
    override val mutatingState: KslValue<*> get() = this

    override fun generateAssignable(generator: KslGenerator) = generator.varAssignable(this)
}

class KslVarScalar<S>(name: String, type: S, isMutable: Boolean)
    : KslVar<S>(name, type, isMutable), KslScalarExpression<S> where S: KslType, S: KslScalar

class KslVarVector<V, S>(name: String, type: V, isMutable: Boolean)
    : KslVar<V>(name, type, isMutable), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar

class KslVarMatrix<M, V>(name: String, type: M, isMutable: Boolean)
    : KslVar<M>(name, type, isMutable), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

class KslVarStruct<T, S>(name: String, type: T, isMutable: Boolean)
    : KslVar<T>(name, type, isMutable), KslExpression<T> where T: KslStruct<S>, S: Struct<S>
{
    init {
        // fixme: This is a nasty hack! Currently we are unable to track the mutations of individual struct fields.
        //  Instead we could only track / mutate the state of the entire struct which can easily lead to programs
        //  which cannot be generated because mutations of individual fields prohibit each other.
        //  To mitigate this, the state-tracking of struct vars is disabled but that is a terrible workaround which can
        //  also easily break. A better solution would be to introduce state mutation for individual struct fields but
        //  that doesn't fit in the current way how things work.
        isTrackingState = false
    }
}