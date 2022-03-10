package de.fabmax.kool.modules.ksl.lang

open class KslUniform<T: KslType>(open val value: KslValue<T>, val arraySize: Int = -1) : KslExpression<T> by value {
    val name: String
        get() = value.stateName
}

class KslUniformScalar<S>(value: KslValue<S>) : KslUniform<S>(value), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslUniformVector<V, S>(value: KslValue<V>) : KslUniform<V>(value), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar
class KslUniformMatrix<M, V>(value: KslValue<M>) : KslUniform<M>(value), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

class KslUniformScalarArray<S>(value: KslArrayScalar<S>, arraySize: Int) : KslUniform<KslTypeArray<S>>(value, arraySize) where S: KslType, S: KslScalar {
    override val value: KslArrayScalar<S>
        get() = super.value as KslArrayScalar<S>
}

class KslUniformVectorArray<V, S>(value: KslArrayVector<V>, arraySize: Int) : KslUniform<KslTypeArray<V>>(value, arraySize) where V: KslType, V: KslVector<S>, S: KslScalar {
    override val value: KslArrayVector<V>
        get() = super.value as KslArrayVector<V>
}

class KslUniformMatrixArray<M, V>(value: KslArrayMatrix<M>, arraySize: Int) : KslUniform<KslTypeArray<M>>(value, arraySize) where M: KslType, M: KslMatrix<V>, V: KslVector<*> {
    override val value: KslArrayMatrix<M>
        get() = super.value as KslArrayMatrix<M>
}