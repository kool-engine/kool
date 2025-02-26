package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.util.logE

class KslSampleDepthTexture<T: KslDepthSampler<*>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val depthRef: KslExprFloat1
) : KslExprFloat1 {

    override val expressionType = KslFloat1

    init {
        if (sampler.expressionType is KslSamplerArrayType) {
            logE { "KslSampleDepthTexture instantiated with array sampler type. You should use sampleDepthTextureArray() when sampling from depth array textures" }
        }
    }

    override fun collectSubExpressions(): List<KslExpression<*>> =
        sampler.collectSubExpressions() + coord.collectSubExpressions() + depthRef.collectSubExpressions() + this

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sampleDepth(${coord.toPseudoCode()})"
}

class KslSampleDepthTextureArray<T>(
    val sampler: KslExpression<T>,
    val arrayIndex: KslExprInt1,
    val coord: KslExpression<*>,
    val depthRef: KslExprFloat1
) : KslExprFloat1 where T: KslDepthSampler<*>, T: KslSamplerArrayType {

    override val expressionType = KslFloat1

    override fun collectSubExpressions(): List<KslExpression<*>> =
        sampler.collectSubExpressions() +
        coord.collectSubExpressions() +
        arrayIndex.collectSubExpressions() +
        depthRef.collectSubExpressions() +
        this

    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}[${arrayIndex.toPseudoCode()}].sampleDepth(${coord.toPseudoCode()})"
}
