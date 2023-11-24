package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslTexelFetch<T: KslSamplerType<KslFloat4>>(
    val sampler: KslExpression<T>,
    val coord: KslExpression<*>,
    val lod: KslScalarExpression<KslInt1>?
) : KslVectorExpression<KslFloat4, KslFloat1> {

    override val expressionType = KslFloat4

    override fun collectStateDependencies(): Set<KslMutatedState> {
        val deps = sampler.collectStateDependencies() + coord.collectStateDependencies()
        return lod?.let { deps + it.collectStateDependencies() } ?: deps
    }

    override fun generateExpression(generator: KslGenerator): String = generator.texelFetch(this)
    override fun toPseudoCode(): String = "${sampler.toPseudoCode()}.texelFetch(${coord.toPseudoCode()}, lod=${lod?.toPseudoCode() ?: "0"})"
}
