package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.util.Struct

open class KslVar<T: KslType>(name: String, type: T, isMutable: Boolean) :
    KslValue<T>(name, isMutable), KslAssignable<T>
{
    override val expressionType = type
    override val assignType = type
    override val mutatingState: KslValue<*> get() = this

    override fun generateAssignable(generator: KslGenerator) = generator.varAssignable(this)
}

class KslVarScalar<S>(name: String, type: S, isMutable: Boolean) :
    KslVar<S>(name, type, isMutable), KslScalarExpression<S> where S: KslType, S: KslScalar

class KslVarVector<V, S>(name: String, type: V, isMutable: Boolean) :
    KslVar<V>(name, type, isMutable), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar

class KslVarMatrix<M, V>(name: String, type: M, isMutable: Boolean) :
    KslVar<M>(name, type, isMutable), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

class KslVarStruct<S>(name: String, type: KslStruct<S>, isMutable: Boolean) :
    KslVar<KslStruct<S>>(name, type, isMutable), KslExprStruct<S> where S: Struct
