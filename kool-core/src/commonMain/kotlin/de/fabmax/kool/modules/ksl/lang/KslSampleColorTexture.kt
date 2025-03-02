package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.util.logE

class KslSampleColorTexture<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val lod: KslExprFloat1?
) : KslExprFloat4 {

    override val expressionType = KslFloat4

    init {
        if (sampler.expressionType is KslSamplerArrayType) {
            logE { "KslSampleColorTexture instantiated with array sampler type. You should use sampleTextureArray() when sampling from array textures" }
        }
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(sampler, coord, lod)

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sample(${coord.toPseudoCode()}, lod=${lod?.toPseudoCode() ?: "0"})"
}

class KslSampleColorTextureGrad<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val ddx: KslExpression<*>,
    val ddy: KslExpression<*>,
) : KslExprFloat4 {

    override val expressionType = KslFloat4

    init {
        if (sampler.expressionType is KslSamplerArrayType) {
            logE { "KslSampleColorTextureGrad instantiated with array sampler type. You should use sampleTextureArrayGrad() when sampling from array textures" }
        }
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(sampler, coord, ddx, ddy)

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sampleGrad(${coord.toPseudoCode()}, ${ddx.toPseudoCode()}, ${ddy.toPseudoCode()})"
}

class KslSampleColorTextureArray<T>(
    val sampler: KslExpression<T>,
    val arrayIndex: KslExprInt1,
    val coord: KslExpression<*>,
    val lod: KslExprFloat1?
) : KslExprFloat4 where T: KslColorSampler<*>, T: KslSamplerArrayType {

    override val expressionType = KslFloat4

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(sampler, arrayIndex, coord, lod)

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}[${arrayIndex.toPseudoCode()}].sample(${coord.toPseudoCode()}, lod=${lod?.toPseudoCode() ?: "0"})"
}

class KslSampleColorTextureArrayGrad<T>(
    val sampler: KslExpression<T>,
    val arrayIndex: KslExprInt1,
    val coord: KslExpression<*>,
    val ddx: KslExpression<*>,
    val ddy: KslExpression<*>,
) : KslExprFloat4 where T: KslColorSampler<*>, T: KslSamplerArrayType {

    override val expressionType = KslFloat4

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(sampler, arrayIndex, coord, ddx, ddy)

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}[${arrayIndex.toPseudoCode()}].sampleGrad(${coord.toPseudoCode()}, ${ddx.toPseudoCode()}, ${ddy.toPseudoCode()})"
}