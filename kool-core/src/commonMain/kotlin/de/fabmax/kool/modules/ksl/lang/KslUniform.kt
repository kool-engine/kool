package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.pipeline.BindGroupScope
import de.fabmax.kool.util.Struct

open class KslUniform<T: KslType>(val value: KslValue<T>, val arraySize: Int = -1) : KslExpression<T> by value {
    val name: String
        get() = value.stateName
}

class KslUniformScalar<S>(value: KslValue<S>) : KslUniform<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslUniformVector<V, S>(value: KslValue<V>) : KslUniform<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslUniformMatrix<M, V>(value: KslValue<M>) : KslUniform<M>(value), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

class KslUniformStruct<S: Struct>(name: String, val scope: BindGroupScope, val provider: () -> S) :
    KslUniform<KslStruct<S>>(KslVarStruct(name, KslStruct(provider), false))
{
    val proto: S get() = value.expressionType.proto
}

class KslUniformScalarArray<S>(value: KslArrayScalar<S>) :
    KslUniform<KslArrayType<S>>(value, value.arraySize), KslScalarArrayExpression<S>
        where S: KslType, S: KslScalar

class KslUniformVectorArray<V, S>(value: KslArrayVector<V, S>) :
    KslUniform<KslArrayType<V>>(value, value.arraySize), KslVectorArrayExpression<V, S>
        where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar

class KslUniformMatrixArray<M, V>(value: KslArrayMatrix<M, V>) :
    KslUniform<KslArrayType<M>>(value, value.arraySize), KslMatrixArrayExpression<M, V>
        where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>

class KslUniformArray<T: KslType>(value: KslArrayGeneric<T>) :
    KslUniform<KslArrayType<T>>(value, value.arraySize), KslGenericArrayExpression<T>