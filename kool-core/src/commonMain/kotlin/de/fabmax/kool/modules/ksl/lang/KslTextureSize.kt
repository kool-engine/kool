package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslTextureSize<T: KslSamplerType<*>, R: KslType>(val sampler: KslExpression<T>, val lod: KslScalarExpression<KslInt1>, override val expressionType: R) : KslExpression<R> {
        override fun collectStateDependencies(): Set<KslMutatedState> = sampler.collectStateDependencies()
        override fun generateExpression(generator: KslGenerator): String = generator.textureSize(this)
        override fun toPseudoCode(): String = "textureSize(${sampler.toPseudoCode()})"
}

class KslTextureSize1d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt1>(sampler, lod, KslInt1), KslScalarExpression<KslInt1>
        where T: KslSamplerType<*>, T: KslSampler1dType
class KslTextureSize2d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt2>(sampler, lod, KslInt2), KslVectorExpression<KslInt2, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler2dType
class KslTextureSize3d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler3dType
class KslTextureSizeCube<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt2>(sampler, lod, KslInt2), KslVectorExpression<KslInt2, KslInt1>
        where T: KslSamplerType<*>, T: KslSamplerCubeType
class KslTextureSize2dArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSampler2dArrayType
class KslTextureSizeCubeArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1>)
    : KslTextureSize<T, KslInt3>(sampler, lod, KslInt3), KslVectorExpression<KslInt3, KslInt1>
        where T: KslSamplerType<*>, T: KslSamplerCubeArrayType
