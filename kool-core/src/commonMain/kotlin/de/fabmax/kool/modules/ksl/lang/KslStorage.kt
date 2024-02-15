package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState
import de.fabmax.kool.pipeline.StorageAccessType

sealed class KslStorage<T: KslStorageType<*, C>, C: KslIntType>(
    name: String,
    val accessType: StorageAccessType,
    val storageType: T
) : KslValue<T>(name, true) {
    val name: String
        get() = stateName
}

class KslStorage1d<T: KslStorage1dType<*>>(name: String, storage: T, val sizeX: Int?, accessType: StorageAccessType) :
    KslStorage<T, KslInt1>(name, accessType, storage)
{
    override val expressionType: T
        get() = storageType
}

class KslStorage2d<T: KslStorage2dType<*>>(name: String, storage: T, val sizeX: Int, val sizeY: Int?, accessType: StorageAccessType) :
    KslStorage<T, KslInt2>(name, accessType, storage)
{
    override val expressionType: T
        get() = storageType
}

class KslStorage3d<T: KslStorage3dType<*>>(name: String, storage: T, val sizeX: Int, val sizeY: Int, val sizeZ: Int?, accessType: StorageAccessType) :
    KslStorage<T, KslInt3>(name, accessType, storage)
{
    override val expressionType: T
        get() = storageType
}

class KslStorageRead<T: KslStorageType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslExpression<T>,
    val coord: KslExpression<C>,
    override val expressionType: R
) : KslExpression<R> {
    override fun collectStateDependencies(): Set<KslMutatedState> {
        return storage.collectStateDependencies() + coord.collectStateDependencies()
    }
    override fun generateExpression(generator: KslGenerator): String = generator.storageRead(this)
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
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("storageWrite(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${data.toPseudoCode()})")
    }
}
