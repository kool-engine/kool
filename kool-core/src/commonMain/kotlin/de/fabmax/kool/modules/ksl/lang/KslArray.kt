package de.fabmax.kool.modules.ksl.lang

open class KslArray<T: KslType>(name: String, type: T, val arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslValue<KslTypeArray<T>>(name, isMutable) {

    override val expressionType = KslTypeArray(type)

    override fun toPseudoCode(): String {
        return "${stateName}(size=${arraySize.toPseudoCode()})"
    }
}

class KslArrayScalar<S>(name: String, type: S, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<S>(name, type, arraySize, isMutable), KslScalarArrayExpression<S> where S: KslType, S: KslScalar

class KslArrayVector<V, S>(name: String, type: V, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<V>(name, type, arraySize, isMutable), KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar

class KslArrayMatrix<M, V>(name: String, type: M, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<M>(name, type, arraySize, isMutable), KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>
