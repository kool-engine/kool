package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator

abstract class KslBuiltinFunction<T: KslType>(override val expressionType: T, vararg args: KslExpression<*>) : KslExpression<T> {
    val args = listOf(*args)
    abstract val name: String
    override fun collectStateDependencies() = args.flatMap { it.collectStateDependencies() }.toSet()
    override fun toPseudoCode() = "${name}(${args.joinToString { it.toPseudoCode() }})"
}

abstract class KslBuiltinFunctionScalar<S>(expressionType: S, vararg args: KslExpression<*>)
    : KslBuiltinFunction<S>(expressionType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
abstract class KslBuiltinFunctionVector<V, S>(expressionType: V, vararg args: KslExpression<*>)
    : KslBuiltinFunction<V>(expressionType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
abstract class KslBuiltinFunctionMatrix<M, V>(expressionType: M, vararg args: KslExpression<*>)
    : KslBuiltinFunction<M>(expressionType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>

class KslBuiltinClampScalar<S>(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value, min, max) where S: KslNumericType, S: KslScalar {
    override val name = "clamp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinClamp(this)
}

class KslBuiltinClampVector<V, S>(vec: KslVectorExpression<V, S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec, min, max) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "clamp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinClamp(this)
}

class KslBuiltinDot<V, S>(vec1: KslVectorExpression<V, S>, vec2: KslVectorExpression<V, S>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, vec1, vec2) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
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

class KslBuiltinReflect<V, S>(vec1: KslVectorExpression<V, S>, vec2: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec1.expressionType, vec1, vec2) where V: KslFloatType, V: KslVector<S>, S: KslFloatType, S: KslScalar {
    override val name = "reflect"
    override fun generateExpression(generator: KslGenerator) = generator.builtinReflect(this)
}
