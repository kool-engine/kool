package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslSampleDepthTexture<T: KslTypeSampler<KslTypeFloat1>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>)
    : KslScalarExpression<KslTypeFloat1> {

    override val expressionType = KslTypeFloat1

    override fun collectStateDependencies(): Set<KslMutatedState> =
        sampler.collectStateDependencies() + coord.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.sampleDepthTexture(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sampleDepth(${coord.toPseudoCode()})"
}
