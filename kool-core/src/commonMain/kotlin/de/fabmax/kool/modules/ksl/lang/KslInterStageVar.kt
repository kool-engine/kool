package de.fabmax.kool.modules.ksl.lang

abstract class KslInterStageVar<T: KslType>(
    open val input: KslValue<T>,
    open val output: KslValue<T>,
    val outputStage: KslShaderStageType,
    val interpolation: KslInterStageInterpolation
)

enum class KslInterStageInterpolation {
    Smooth,
    Flat,
    NoPerspective
}

class KslInterStageScalar<S>(input: KslVarScalar<S>, output: KslVarScalar<S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation) :
    KslInterStageVar<S>(input, output, outputStage, interpolation) where S: KslType, S: KslScalar
{

    override val input: KslVarScalar<S>
        get() = super.input as KslVarScalar<S>

    override val output: KslVarScalar<S>
        get() = super.output as KslVarScalar<S>
}

class KslInterStageVector<V, S>(input: KslVarVector<V, S>, output: KslVarVector<V, S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation)
    : KslInterStageVar<V>(input, output, outputStage, interpolation) where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {

    @Suppress("UNCHECKED_CAST")
    override val input: KslVarVector<V, S>
        get() = super.input as KslVarVector<V, S>

    @Suppress("UNCHECKED_CAST")
    override val output: KslVarVector<V, S>
        get() = super.output as KslVarVector<V, S>
}

class KslInterStageScalarArray<S>(input: KslArrayScalar<S>, output: KslArrayScalar<S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation) :
    KslInterStageVar<KslArrayType<S>>(input, output, outputStage, interpolation) where S: KslType, S: KslScalar
{

    override val input: KslArrayScalar<S>
        get() = super.input as KslArrayScalar<S>

    override val output: KslArrayScalar<S>
        get() = super.output as KslArrayScalar<S>
}

class KslInterStageVectorArray<V, S>(input: KslArrayVector<V, S>, output: KslArrayVector<V, S>, outputStage: KslShaderStageType, interpolation: KslInterStageInterpolation) :
    KslInterStageVar<KslArrayType<V>>(input, output, outputStage, interpolation) where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
{

    @Suppress("UNCHECKED_CAST")
    override val input: KslArrayVector<V, S>
        get() = super.input as KslArrayVector<V, S>

    @Suppress("UNCHECKED_CAST")
    override val output: KslArrayVector<V, S>
        get() = super.output as KslArrayVector<V, S>
}
