package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslSampleDepthTexture<T: KslSamplerType<KslFloat1>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>
) : KslScalarExpression<KslFloat1> {

    override val expressionType = KslFloat1

    override fun collectStateDependencies(): Set<KslMutatedState> =
        sampler.collectStateDependencies() + coord.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.sampleDepthTexture(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sampleDepth(${coord.toPseudoCode()})"
}
