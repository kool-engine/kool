package de.fabmax.kool.modules.ksl.lang

abstract class KslInterStageVar<T: KslType>(val input: KslVar<T>, val output: KslValue<T>, val outputStage: KslShaderStageType, val interpolation: KslInterStageInterpolation)

enum class KslInterStageInterpolation {
    Smooth,
    Flat,
    NoPerspective
}

class KslInterStageScalar<S>(input: KslVar<S>, output: KslValue<S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation)
    : KslInterStageVar<S>(input, output, outputStage, interpolation) where S: KslType, S: KslScalar

class KslInterStageVector<V, S>(input: KslVar<V>, output: KslValue<V>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation)
    : KslInterStageVar<V>(input, output, outputStage, interpolation) where V: KslType, V: KslVector<S>, S: KslScalar
