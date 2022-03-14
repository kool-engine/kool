package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslTextureSize<T: KslTypeSampler<*>, R: KslType>(val sampler: KslExpression<T>, val lod: KslScalarExpression<KslTypeInt1>, override val expressionType: R) : KslExpression<R> {
        override fun collectStateDependencies(): Set<KslMutatedState> = sampler.collectStateDependencies()
        override fun generateExpression(generator: KslGenerator): String = generator.textureSize(this)
        override fun toPseudoCode(): String = "textureSize(${sampler.toPseudoCode()})"
}

class KslTextureSize1d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt1>(sampler, lod, KslTypeInt1), KslScalarExpression<KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSampler1d
class KslTextureSize2d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt2>(sampler, lod, KslTypeInt2), KslVectorExpression<KslTypeInt2, KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSampler2d
class KslTextureSize3d<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt3>(sampler, lod, KslTypeInt3), KslVectorExpression<KslTypeInt3, KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSampler3d
class KslTextureSizeCube<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt2>(sampler, lod, KslTypeInt2), KslVectorExpression<KslTypeInt2, KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSamplerCube
class KslTextureSize2dArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt3>(sampler, lod, KslTypeInt3), KslVectorExpression<KslTypeInt3, KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSampler2dArray
class KslTextureSizeCubeArray<T>(sampler: KslExpression<T>, lod: KslScalarExpression<KslTypeInt1>)
    : KslTextureSize<T, KslTypeInt3>(sampler, lod, KslTypeInt3), KslVectorExpression<KslTypeInt3, KslTypeInt1>
        where T: KslTypeSampler<*>, T: KslTypeSamplerCubeArray
