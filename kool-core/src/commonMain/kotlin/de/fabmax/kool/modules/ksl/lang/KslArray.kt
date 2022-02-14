package de.fabmax.kool.modules.ksl.lang

open class KslArray<T: KslType>(name: String, type: T, val arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslValue<KslTypeArray<T>>(name, isMutable) {

    override val expressionType = KslTypeArray(type)

    override fun toPseudoCode(): String {
        return "${stateName}(size=${arraySize.toPseudoCode()})"
    }
}

class KslArrayScalar<S>(name: String, type: S, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<S>(name, type, arraySize, isMutable) where S: KslType, S: KslScalar

class KslArrayVector<V>(name: String, type: V, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<V>(name, type, arraySize, isMutable) where V: KslType, V: KslVector<*>

class KslArrayMatrix<M>(name: String, type: M, arraySize: KslExpression<KslTypeInt1>, isMutable: Boolean)
    : KslArray<M>(name, type, arraySize, isMutable) where M: KslType, M: KslMatrix<*>
