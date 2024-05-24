package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslSampleColorTexture<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val lod: KslScalarExpression<KslFloat1>?
) : KslVectorExpression<KslFloat4, KslFloat1> {

    override val expressionType = KslFloat4

    override fun collectStateDependencies(): Set<KslMutatedState> {
        val deps = sampler.collectStateDependencies() + coord.collectStateDependencies()
        return lod?.let { deps + it.collectStateDependencies() } ?: deps
    }

    override fun generateExpression(generator: KslGenerator): String = generator.sampleColorTexture(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sample(${coord.toPseudoCode()}, lod=${lod?.toPseudoCode() ?: "0"})"
}

class KslSampleColorTextureGrad<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val ddx: KslExpression<*>,
    val ddy: KslExpression<*>,
) : KslVectorExpression<KslFloat4, KslFloat1> {

    override val expressionType = KslFloat4

    override fun collectStateDependencies(): Set<KslMutatedState> {
        return sampler.collectStateDependencies() +
                coord.collectStateDependencies() +
                ddx.collectStateDependencies() +
                ddy.collectStateDependencies()
    }

    override fun generateExpression(generator: KslGenerator): String = generator.sampleColorTextureGrad(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.sampleGrad(${coord.toPseudoCode()}, ${ddx.toPseudoCode()}, ${ddy.toPseudoCode()})"
}
