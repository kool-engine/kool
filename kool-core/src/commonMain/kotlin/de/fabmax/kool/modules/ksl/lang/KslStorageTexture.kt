package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState
import de.fabmax.kool.pipeline.TexFormat

sealed class KslStorageTexture<T: KslStorageTextureType<*, C>, C: KslIntType>(
    name: String,
    val storageType: T,
    val texFormat: TexFormat
) : KslValue<T>(name, true) {
    val name: String
        get() = stateName

    internal var isRead = false
    internal var isWritten = false
}

class KslStorageTexture1d<T: KslStorageTexture1dType<*>>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, KslInt1>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTexture2d<T: KslStorageTexture2dType<*>>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, KslInt2>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTexture3d<T: KslStorageTexture3dType<*>>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, KslInt3>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTextureRead<T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorageTexture<T, *>,
    val coord: KslExpression<C>
) : KslExprFloat4 {
    override val expressionType = KslFloat4

    init {
        storage.isRead = true
    }

    override fun collectStateDependencies(): Set<KslMutatedState> {
        return storage.collectStateDependencies() + coord.collectStateDependencies()
    }
    override fun generateExpression(generator: KslGenerator): String = generator.storageTextureRead(this)
    override fun toPseudoCode(): String = "storageTextureRead(${storage.toPseudoCode()}, ${coord.toPseudoCode()})"
}

open class KslStorageTextureWrite<T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorageTexture<T, *>,
    val coord: KslExpression<C>,
    val data: KslExprFloat4,
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
        return annotatePseudoCode("storageTextureWrite(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${data.toPseudoCode()})")
    }
}
