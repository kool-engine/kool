package de.fabmax.kool.modules.ksl.lang

sealed class KslStorage<T: KslStorageType<*, C>, C: KslIntType>(
    name: String,
    val storageType: T
) : KslValue<T>(name, true) {
    val name: String
        get() = stateName

    internal var isRead = false
    internal var isWritten = false
    internal var isAccessedAtomically = false
}

class KslStorage1d<T: KslStorage1dType<*>>(name: String, storage: T, val sizeX: Int?) :
    KslStorage<T, KslInt1>(name, storage)
{
    override val expressionType: T get() = storageType
}

class KslStorage2d<T: KslStorage2dType<*>>(name: String, storage: T, val sizeX: Int, val sizeY: Int?) :
    KslStorage<T, KslInt2>(name, storage)
{
    override val expressionType: T get() = storageType
}

class KslStorage3d<T: KslStorage3dType<*>>(name: String, storage: T, val sizeX: Int, val sizeY: Int, val sizeZ: Int?) :
    KslStorage<T, KslInt3>(name, storage)
{
    override val expressionType: T get() = storageType
}

class KslStorageRead<T: KslStorageType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorage<T, *>,
    val coord: KslExpression<C>,
    override val expressionType: R
) : KslExpression<R> {

    init {
        storage.isRead = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(storage, coord)

    override fun toPseudoCode(): String = "storageRead(${storage.toPseudoCode()}, ${coord.toPseudoCode()})"
}

open class KslStorageWrite<T: KslStorageType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorage<T, *>,
    val coord: KslExpression<C>,
    val data: KslExpression<R>,
    scopeBuilder: KslScopeBuilder
) : KslStatement("store", scopeBuilder) {

    init {
        addExpressionDependencies(storage)
        addExpressionDependencies(coord)
        addExpressionDependencies(data)
        addMutation(storage.mutate())

        storage.isWritten = true
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("storageWrite(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${data.toPseudoCode()})")
    }
}
