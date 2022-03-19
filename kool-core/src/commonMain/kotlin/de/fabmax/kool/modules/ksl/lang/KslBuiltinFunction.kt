package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator

abstract class KslBuiltinFunction<T: KslType>(returnType: T, vararg args: KslExpression<*>) : KslExpression<T> {
    val args = listOf(*args)
    abstract val name: String
    override val expressionType: T = returnType
    override fun collectStateDependencies() = args.flatMap { it.collectStateDependencies() }.toSet()
    override fun toPseudoCode() = "${name}(${args.joinToString { it.toPseudoCode() }})"
}

abstract class KslBuiltinFunctionScalar<S>(returnType: S, vararg args: KslExpression<*>)
    : KslBuiltinFunction<S>(returnType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
abstract class KslBuiltinFunctionVector<V, S>(returnType: V, vararg args: KslExpression<*>)
    : KslBuiltinFunction<V>(returnType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
abstract class KslBuiltinFunctionMatrix<M, V>(returnType: M, vararg args: KslExpression<*>)
    : KslBuiltinFunction<M>(returnType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>

class KslBuiltinClampScalar<S>(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value, min, max) where S: KslNumericType, S: KslScalar {
    override val name = "clamp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinClamp(this)
}

class KslBuiltinClampVector<V, S>(vec: KslVectorExpression<V, S>, min: KslVectorExpression<V, S>, max: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec, min, max) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "clamp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinClamp(this)
}

class KslBuiltinCosScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslFloatType, S: KslScalar {
    override val name = "cos"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCos(this)
}

class KslBuiltinCosVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "cos"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCos(this)
}

class KslBuiltinCross(vec1: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>, vec2: KslVectorExpression<KslTypeFloat3, KslTypeFloat1>)
    : KslBuiltinFunctionVector<KslTypeFloat3, KslTypeFloat1>(KslTypeFloat3, vec1, vec2) {
    override val name = "cross"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCross(this)
}

class KslBuiltinDot<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, a, b) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "dot"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDot(this)
}

class KslBuiltinLength<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, vec) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "length"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLength(this)
}

class KslBuiltinMaxScalar<S>(a: KslScalarExpression<S>, b: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(a.expressionType, a, b) where S: KslNumericType, S: KslScalar {
    override val name = "max"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMax(this)
}

class KslBuiltinMaxVector<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(a.expressionType, a, b) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "max"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMax(this)
}

class KslBuiltinMinScalar<S>(a: KslScalarExpression<S>, b: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(a.expressionType, a, b) where S: KslNumericType, S: KslScalar {
    override val name = "min"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMin(this)
}

class KslBuiltinMinVector<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(a.expressionType, a, b) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "min"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMin(this)
}

class KslBuiltinMixScalar(x: KslScalarExpression<KslTypeFloat1>, y: KslScalarExpression<KslTypeFloat1>, a: KslScalarExpression<KslTypeFloat1>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(x.expressionType, x, y, a) {
    override val name = "mix"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMix(this)
}

class KslBuiltinMixVector<V, S>(x: KslVectorExpression<V, S>, y: KslVectorExpression<V, S>, a: KslExpression<*>)
    : KslBuiltinFunctionVector<V, S>(x.expressionType, x, y, a) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "mix"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMix(this)
}

class KslBuiltinNormalize<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "normalize"
    override fun generateExpression(generator: KslGenerator) = generator.builtinNormalize(this)
}

class KslBuiltinPowScalar(value: KslScalarExpression<KslTypeFloat1>, power: KslScalarExpression<KslTypeFloat1>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, value, power) {
    override val name = "pow"
    override fun generateExpression(generator: KslGenerator) = generator.builtinPow(this)
}

class KslBuiltinPowVector<V, S>(vec: KslVectorExpression<V, S>, power: KslExpression<*>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec, power) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "pow"
    override fun generateExpression(generator: KslGenerator) = generator.builtinPow(this)
}

class KslBuiltinReflect<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(a.expressionType, a, b) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "reflect"
    override fun generateExpression(generator: KslGenerator) = generator.builtinReflect(this)
}

class KslBuiltinSinScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslFloatType, S: KslScalar {
    override val name = "sin"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSin(this)
}

class KslBuiltinSinVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "sin"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSin(this)
}

class KslBuiltinSmoothStepScalar<S>(low: KslScalarExpression<S>, high: KslScalarExpression<S>, x: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(x.expressionType, low, high, x) where S: KslFloatType, S: KslScalar {
    override val name = "smoothStep"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSmoothStep(this)
}

class KslBuiltinSmoothStepVector<V, S>(low: KslVectorExpression<V, S>, high: KslVectorExpression<V, S>, x: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(x.expressionType, low, high, x) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "smoothStep"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSmoothStep(this)
}
