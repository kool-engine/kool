package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslSampleColorTexture<T: KslTypeSampler<KslTypeFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>)
    : KslVectorExpression<KslTypeFloat4, KslTypeFloat1> {

    override val expressionType = KslTypeFloat4

    override fun collectStateDependencies(): Set<KslMutatedState> =
        sampler.collectStateDependencies() + coord.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.sampleColorTexture(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sample(${coord.toPseudoCode()})"
}
