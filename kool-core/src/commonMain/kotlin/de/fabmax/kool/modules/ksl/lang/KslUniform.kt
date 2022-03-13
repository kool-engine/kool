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

class KslUniformVectorArray<V, S>(value: KslArrayVector<V, S>, arraySize: Int) : KslUniform<KslTypeArray<V>>(value, arraySize) where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
    @Suppress("UNCHECKED_CAST")
    override val value: KslArrayVector<V, S>
        get() = super.value as KslArrayVector<V, S>
}

class KslUniformMatrixArray<M, V>(value: KslArrayMatrix<M, V>, arraySize: Int) : KslUniform<KslTypeArray<M>>(value, arraySize) where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
    @Suppress("UNCHECKED_CAST")
    override val value: KslArrayMatrix<M, V>
        get() = super.value as KslArrayMatrix<M, V>
}