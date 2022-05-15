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

class KslBuiltinAbsScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslNumericType, S: KslScalar {
    override val name = "abs"
    override fun generateExpression(generator: KslGenerator) = generator.builtinAbs(this)
}

class KslBuiltinAbsVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "abs"
    override fun generateExpression(generator: KslGenerator) = generator.builtinAbs(this)
}

class KslBuiltinAtan2Scalar(y: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(y.expressionType, y, x) {
    override val name = "atan2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinAtan2(this)
}

class KslBuiltinAtan2Vector<V>(y: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(y.expressionType, y, x) where V: KslNumericType, V: KslVector<KslTypeFloat1> {
    override val name = "atan2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinAtan2(this)
}

class KslBuiltinCeilScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "ceil"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCeil(this)
}

class KslBuiltinCeilVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "ceil"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCeil(this)
}

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

class KslBuiltinCross(vec1: KslExprFloat3, vec2: KslExprFloat3)
    : KslBuiltinFunctionVector<KslTypeFloat3, KslTypeFloat1>(KslTypeFloat3, vec1, vec2) {
    override val name = "cross"
    override fun generateExpression(generator: KslGenerator) = generator.builtinCross(this)
}

class KslBuiltinDegreesScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "degrees"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDegrees(this)
}

class KslBuiltinDegreesVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "degrees"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDegrees(this)
}

class KslBuiltinDistanceScalar<T: KslFloatType>(a: KslExpression<T>, b: KslExpression<T>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, a, b) {
    override val name = "distance"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDistance(this)
}

class KslBuiltinDot<V>(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, a, b) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "dot"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDot(this)
}

class KslBuiltinExpScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "exp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinExp(this)
}

class KslBuiltinExpVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "exp"
    override fun generateExpression(generator: KslGenerator) = generator.builtinExp(this)
}

class KslBuiltinExp2Scalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "exp2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinExp2(this)
}

class KslBuiltinExp2Vector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "exp2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinExp2(this)
}

class KslBuiltinFaceForward<V>(n: KslVectorExpression<V, KslTypeFloat1>, i: KslVectorExpression<V, KslTypeFloat1>, nRef: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(n.expressionType, n, i, nRef) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "faceForward"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFaceForward(this)
}

class KslBuiltinFloorScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "floor"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFloor(this)
}

class KslBuiltinFloorVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "floor"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFloor(this)
}

class KslBuiltinFmaScalar(a: KslExprFloat1, b: KslExprFloat1, c: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(a.expressionType, a, b, c) {
    override val name = "fma"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFma(this)
}

class KslBuiltinFmaVector<V>(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>, c: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(a.expressionType, a, b, c) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "fma"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFma(this)
}

class KslBuiltinFractScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "fract"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFract(this)
}

class KslBuiltinFractVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "fract"
    override fun generateExpression(generator: KslGenerator) = generator.builtinFract(this)
}

class KslBuiltinInverseSqrtScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "inverseSqrt"
    override fun generateExpression(generator: KslGenerator) = generator.builtinInverseSqrt(this)
}

class KslBuiltinInverseSqrtVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "inverseSqrt"
    override fun generateExpression(generator: KslGenerator) = generator.builtinInverseSqrt(this)
}

class KslBuiltinLength<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "length"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLength(this)
}

class KslBuiltinLogScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "log"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLog(this)
}

class KslBuiltinLogVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "log"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLog(this)
}

class KslBuiltinLog2Scalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "log2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLog2(this)
}

class KslBuiltinLog2Vector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "log2"
    override fun generateExpression(generator: KslGenerator) = generator.builtinLog2(this)
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

class KslBuiltinMixScalar(x: KslExprFloat1, y: KslExprFloat1, a: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(x.expressionType, x, y, a) {
    override val name = "mix"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMix(this)
}

class KslBuiltinMixVector<V>(x: KslVectorExpression<V, KslTypeFloat1>, y: KslVectorExpression<V, KslTypeFloat1>, a: KslExpression<*>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(x.expressionType, x, y, a) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "mix"
    override fun generateExpression(generator: KslGenerator) = generator.builtinMix(this)
}

class KslBuiltinNormalize<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "normalize"
    override fun generateExpression(generator: KslGenerator) = generator.builtinNormalize(this)
}

class KslBuiltinPowScalar(value: KslExprFloat1, power: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, value, power) {
    override val name = "pow"
    override fun generateExpression(generator: KslGenerator) = generator.builtinPow(this)
}

class KslBuiltinPowVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>, power: KslExpression<*>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec, power) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "pow"
    override fun generateExpression(generator: KslGenerator) = generator.builtinPow(this)
}

class KslBuiltinRadiansScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "radians"
    override fun generateExpression(generator: KslGenerator) = generator.builtinRadians(this)
}

class KslBuiltinRadiansVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "radians"
    override fun generateExpression(generator: KslGenerator) = generator.builtinRadians(this)
}

class KslBuiltinReflect<V>(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(a.expressionType, a, b) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "reflect"
    override fun generateExpression(generator: KslGenerator) = generator.builtinReflect(this)
}

class KslBuiltinRefract<V>(a: KslVectorExpression<V, KslTypeFloat1>, b: KslVectorExpression<V, KslTypeFloat1>, i: KslExprFloat1)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(a.expressionType, a, b, i) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "refract"
    override fun generateExpression(generator: KslGenerator) = generator.builtinRefract(this)
}

class KslBuiltinRoundScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "round"
    override fun generateExpression(generator: KslGenerator) = generator.builtinRound(this)
}

class KslBuiltinRoundVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "round"
    override fun generateExpression(generator: KslGenerator) = generator.builtinRound(this)
}

class KslBuiltinSignScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslNumericType, S: KslScalar {
    override val name = "sign"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSign(this)
}

class KslBuiltinSignVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "sign"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSign(this)
}

class KslBuiltinSmoothStepScalar(low: KslExprFloat1, high: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(x.expressionType, low, high, x) {
    override val name = "smoothStep"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSmoothStep(this)
}

class KslBuiltinSmoothStepVector<V>(low: KslVectorExpression<V, KslTypeFloat1>, high: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(x.expressionType, low, high, x) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "smoothStep"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSmoothStep(this)
}

class KslBuiltinSqrtScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "sqrt"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSqrt(this)
}

class KslBuiltinSqrtVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "sqrt"
    override fun generateExpression(generator: KslGenerator) = generator.builtinSqrt(this)
}

class KslBuiltinStepScalar(edge: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(edge.expressionType, edge, x) {
    override val name = "step"
    override fun generateExpression(generator: KslGenerator) = generator.builtinStep(this)
}

class KslBuiltinStepVector<V>(edge: KslVectorExpression<V, KslTypeFloat1>, x: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(edge.expressionType, edge, x) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "step"
    override fun generateExpression(generator: KslGenerator) = generator.builtinStep(this)
}

class KslBuiltinTrigonometryScalar(value: KslExprFloat1, override val name: String)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override fun generateExpression(generator: KslGenerator) = generator.builtinTrigonometry(this)
}

class KslBuiltinTrigonometryVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>, override val name: String)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override fun generateExpression(generator: KslGenerator) = generator.builtinTrigonometry(this)
}

class KslBuiltinTruncScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(value.expressionType, value) {
    override val name = "trunc"
    override fun generateExpression(generator: KslGenerator) = generator.builtinTrunc(this)
}

class KslBuiltinTruncVector<V>(vec: KslVectorExpression<V, KslTypeFloat1>)
    : KslBuiltinFunctionVector<V, KslTypeFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslTypeFloat1> {
    override val name = "trunc"
    override fun generateExpression(generator: KslGenerator) = generator.builtinTrunc(this)
}

class KslBuiltinDeterminant<M, V>(matrix: KslMatrixExpression<M, V>)
    : KslBuiltinFunctionScalar<KslTypeFloat1>(KslTypeFloat1, matrix) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    override val name = "determinant"
    override fun generateExpression(generator: KslGenerator) = generator.builtinDeterminant(this)
}

class KslBuiltinTranspose<M, V>(matrix: KslMatrixExpression<M, V>)
    : KslBuiltinFunctionMatrix<M, V>(matrix.expressionType, matrix) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    override val name = "transpose"
    override fun generateExpression(generator: KslGenerator) = generator.builtinTranspose(this)
}
