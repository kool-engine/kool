package de.fabmax.kool.modules.ksl.lang

sealed class KslStorage<T: KslStorageType<*>>(
    name: String,
    val storageType: T
) : KslValue<T>(name, true) {
    val name: String
        get() = stateName

    internal var isRead = false
    internal var isWritten = false
    internal var isAccessedAtomically = false
}

class KslStructStorage(name: String, type: KslStruct, val sizeX: Int?) :
    KslStorage<KslStructStorageType>(name, KslStructStorageType(type))
{
    override val expressionType: KslStructStorageType get() = storageType
}

class KslPrimitiveStorage<T: KslPrimitiveStorageType<*>>(name: String, storage: T, val size: Int?) :
    KslStorage<T>(name, storage)
{
    override val expressionType: T get() = storageType
}

class KslStorageRead<T: KslStorageType<R>, R: KslNumericType>(
    val storage: KslStorage<T>,
    val index: KslExprInt1,
    override val expressionType: R
) : KslExpression<R> {

    init {
        storage.isRead = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(storage, index)

    override fun toPseudoCode(): String = "storageRead(${storage.toPseudoCode()}, ${index.toPseudoCode()})"
}

open class KslStorageWrite<T: KslStorageType<R>, R: KslNumericType>(
    val storage: KslStorage<T>,
    val index: KslExprInt1,
    val data: KslExpression<R>,
    scopeBuilder: KslScopeBuilder
) : KslStatement("store", scopeBuilder) {

    init {
        addExpressionDependencies(storage)
        addExpressionDependencies(index)
        addExpressionDependencies(data)
        addMutation(storage.mutate())

        storage.isWritten = true
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("storageWrite(${storage.toPseudoCode()}, ${index.toPseudoCode()}, ${data.toPseudoCode()})")
    }
}
