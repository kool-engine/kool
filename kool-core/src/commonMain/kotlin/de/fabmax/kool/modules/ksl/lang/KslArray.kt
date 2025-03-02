package de.fabmax.kool.modules.ksl.lang

abstract class KslArray<T: KslType>(name: String, type: T, val arraySize: Int, isMutable: Boolean) :
    KslValue<KslArrayType<T>>(name, isMutable)
{
    override val expressionType = KslArrayType(type, arraySize)

    override fun toPseudoCode(): String {
        return "${stateName}(size=${arraySize})"
    }
}

class KslArrayScalar<S>(name: String, type: S, arraySize: Int, isMutable: Boolean)
    : KslArray<S>(name, type, arraySize, isMutable), KslScalarArrayExpression<S> where S: KslType, S: KslScalar

class KslArrayVector<V, S>(name: String, type: V, arraySize: Int, isMutable: Boolean)
    : KslArray<V>(name, type, arraySize, isMutable), KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar

class KslArrayMatrix<M, V>(name: String, type: M, arraySize: Int, isMutable: Boolean)
    : KslArray<M>(name, type, arraySize, isMutable), KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslVector<*>

class KslArrayGeneric<T: KslType>(name: String, type: T, arraySize: Int, isMutable: Boolean)
    : KslArray<T>(name, type, arraySize, isMutable), KslArrayExpression<T>
