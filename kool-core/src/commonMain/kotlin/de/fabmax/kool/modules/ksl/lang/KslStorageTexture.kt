package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.pipeline.TexFormat

sealed class KslStorageTexture<T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType>(
    name: String,
    val storageType: T,
    val texFormat: TexFormat
) : KslValue<T>(name, true) {
    val name: String
        get() = stateName

    internal var isRead = false
    internal var isWritten = false
}

class KslStorageTexture1d<T: KslStorageTexture1dType<R>, R: KslNumericType>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, R, KslInt1>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTexture2d<T: KslStorageTexture2dType<R>, R: KslNumericType>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, R, KslInt2>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTexture3d<T: KslStorageTexture3dType<R>, R: KslNumericType>(name: String, storage: T, texFormat: TexFormat) :
    KslStorageTexture<T, R, KslInt3>(name, storage, texFormat)
{
    override val expressionType: T get() = storageType
}

class KslStorageTextureLoad<T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorageTexture<T, R, C>,
    val coord: KslExpression<C>
) : KslExprFloat4 {
    override val expressionType = KslFloat4

    init {
        storage.isRead = true
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(storage, coord)

    override fun toPseudoCode(): String = "textureLoad(${storage.toPseudoCode()}, ${coord.toPseudoCode()})"
}

open class KslStorageTextureStore<T: KslStorageTextureType<R, C>, R: KslNumericType, C: KslIntType>(
    val storage: KslStorageTexture<T, R, C>,
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
        return annotatePseudoCode("textureStore(${storage.toPseudoCode()}, ${coord.toPseudoCode()}, ${data.toPseudoCode()})")
    }
}

class KslImageTextureLoad<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val lod: KslScalarExpression<KslInt1>?
) : KslExprFloat4 {

    override val expressionType = KslFloat4

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(sampler, coord, lod)

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.textureLoad(${coord.toPseudoCode()}, lod=${lod?.toPseudoCode() ?: "0"})"
}
