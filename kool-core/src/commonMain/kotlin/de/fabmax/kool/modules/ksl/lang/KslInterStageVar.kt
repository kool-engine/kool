package de.fabmax.kool.modules.ksl.lang

abstract class KslInterStageVar<T: KslType>(
    open val input: KslVar<T>,
    open val output: KslValue<T>,
    val outputStage: KslShaderStageType,
    val interpolation: KslInterStageInterpolation)

enum class KslInterStageInterpolation {
    Smooth,
    Flat,
    NoPerspective
}

class KslInterStageScalar<S>(input: KslVarScalar<S>, output: KslVarScalar<S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation)
    : KslInterStageVar<S>(input, output, outputStage, interpolation) where S: KslType, S: KslScalar {

    override val input: KslVarScalar<S>
        get() = super.input as KslVarScalar<S>

    override val output: KslVarScalar<S>
        get() = super.output as KslVarScalar<S>
}

class KslInterStageVector<V, S>(input: KslVarVector<V, S>, output: KslVarVector<V, S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation)
    : KslInterStageVar<V>(input, output, outputStage, interpolation) where V: KslType, V: KslVector<S>, S: KslScalar {

    @Suppress("UNCHECKED_CAST")
    override val input: KslVarVector<V, S>
        get() = super.input as KslVarVector<V, S>

    @Suppress("UNCHECKED_CAST")
    override val output: KslVarVector<V, S>
        get() = super.output as KslVarVector<V, S>
}
