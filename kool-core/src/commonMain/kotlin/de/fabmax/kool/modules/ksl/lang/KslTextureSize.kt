package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslTextureSize<T: KslSamplerType<*>, R: KslType>(
    val sampler: KslExpression<T>,
    val lod: KslScalarExpression<KslInt1>,
    override val expressionType: R
) : KslExpression<R> {
    override fun collectStateDependencies(): Set<KslMutatedState> = sampler.collectStateDependencies()
    override fun generateExpression(generator: KslGenerator): String = generator.textureSize(this)
    override fun toPseudoCode(): String = "textureSize(${sampler.toPseudoCode()})"
}

class KslTextureSize1d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt1>(sampler, lod, KslInt1), KslScalarExpression<KslInt1>
        where T: KslSamplerType<*>, T: KslSampler1dType

class KslTextureSize2d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt2>(sampler, lod, KslInt2), KslVectorExpression<KslInt2, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler2dType

class KslTextureSize3d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler3dType

class KslTextureSizeCube<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt2>(sampler, lod, KslInt2), KslVectorExpression<KslInt2, KslInt1>
        where T: KslSamplerType<*>, T: KslSamplerCubeType

class KslTextureSize2dArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler2dType, T: KslSamplerArrayType

class KslTextureSizeCubeArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>) :
    KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSamplerCubeType, T: KslSamplerArrayType

abstract class KslStorageTextureSize<T: KslStorageTextureType<*,*>, R: KslType>(
    val storageTex: KslExpression<T>,
    override val expressionType: R
) : KslExpression<R> {
    override fun collectStateDependencies(): Set<KslMutatedState> = storageTex.collectStateDependencies()
    override fun generateExpression(generator: KslGenerator): String = generator.textureSize(this)
    override fun toPseudoCode(): String = "textureSize(${storageTex.toPseudoCode()})"
}

class KslStorageTextureSize1d(storageTex: KslExprStorageTex1d) :
    KslStorageTextureSize<KslStorageTexture1dType<*>, KslInt1>(storageTex, KslInt1), KslScalarExpression<KslInt1>

class KslStorageTextureSize2d(storageTex: KslExprStorageTex2d) :
    KslStorageTextureSize<KslStorageTexture2dType<*>, KslInt2>(storageTex, KslInt2), KslVectorExpression<KslInt2, KslInt1>

class KslStorageTextureSize3d(storageTex: KslExprStorageTex3d) :
    KslStorageTextureSize<KslStorageTexture3dType<*>, KslInt3>(storageTex, KslInt3), KslVectorExpression<KslInt3, KslInt1>
