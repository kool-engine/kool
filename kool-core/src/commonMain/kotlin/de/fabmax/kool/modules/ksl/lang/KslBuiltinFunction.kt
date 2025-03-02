package de.fabmax.kool.modules.ksl.lang

sealed class KslBuiltinFunction<T: KslType>(returnType: T, vararg args: KslExpression<*>) : KslExpression<T> {
    val args = listOf(*args)
    abstract val name: String
    override val expressionType: T = returnType
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(*args.toTypedArray())
    override fun toPseudoCode() = "${name}(${args.joinToString { it.toPseudoCode() }})"
}

sealed class KslBuiltinFunctionScalar<S>(returnType: S, vararg args: KslExpression<*>)
    : KslBuiltinFunction<S>(returnType, *args), KslScalarExpression<S> where S: KslType, S: KslScalar
sealed class KslBuiltinFunctionVector<V, S>(returnType: V, vararg args: KslExpression<*>)
    : KslBuiltinFunction<V>(returnType, *args), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
sealed class KslBuiltinFunctionMatrix<M, V>(returnType: M, vararg args: KslExpression<*>)
    : KslBuiltinFunction<M>(returnType, *args), KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*>

class KslBuiltinAbsScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslNumericType, S: KslScalar {
    override val name = "abs"
}

class KslBuiltinAbsVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "abs"
}

class KslBuiltinAtan2Scalar(y: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(y.expressionType, y, x) {
    override val name = "atan2"
}

class KslBuiltinAtan2Vector<V>(y: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(y.expressionType, y, x) where V: KslNumericType, V: KslVector<KslFloat1> {
    override val name = "atan2"
}

class KslBuiltinCeilScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "ceil"
}

class KslBuiltinCeilVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "ceil"
}

class KslBuiltinClampScalar<S>(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value, min, max) where S: KslNumericType, S: KslScalar {
    override val name = "clamp"
}

class KslBuiltinClampVector<V, S>(vec: KslVectorExpression<V, S>, min: KslVectorExpression<V, S>, max: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec, min, max) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "clamp"
}

class KslBuiltinCross(vec1: KslExprFloat3, vec2: KslExprFloat3)
    : KslBuiltinFunctionVector<KslFloat3, KslFloat1>(KslFloat3, vec1, vec2) {
    override val name = "cross"
}

class KslBuiltinDegreesScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "degrees"
}

class KslBuiltinDegreesVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "degrees"
}

class KslBuiltinDistanceScalar<T: KslFloatType>(a: KslExpression<T>, b: KslExpression<T>)
    : KslBuiltinFunctionScalar<KslFloat1>(KslFloat1, a, b) {
    override val name = "distance"
}

class KslBuiltinDot<V>(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionScalar<KslFloat1>(KslFloat1, a, b) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "dot"
}

class KslBuiltinDpdxScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "dpdx"
}

class KslBuiltinDpdxVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "dpdx"
}

class KslBuiltinDpdyScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "dpdy"
}

class KslBuiltinDpdyVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "dpdy"
}

class KslBuiltinExpScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "exp"
}

class KslBuiltinExpVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "exp"
}

class KslBuiltinExp2Scalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "exp2"
}

class KslBuiltinExp2Vector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "exp2"
}

class KslBuiltinFaceForward<V>(n: KslVectorExpression<V, KslFloat1>, i: KslVectorExpression<V, KslFloat1>, nRef: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(n.expressionType, n, i, nRef) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "faceForward"
}

class KslBuiltinFloorScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "floor"
}

class KslBuiltinFloorVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "floor"
}

class KslBuiltinFmaScalar(a: KslExprFloat1, b: KslExprFloat1, c: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(a.expressionType, a, b, c) {
    override val name = "fma"
}

class KslBuiltinFmaVector<V>(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, c: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(a.expressionType, a, b, c) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "fma"
}

class KslBuiltinFractScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "fract"
}

class KslBuiltinFractVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "fract"
}

class KslBuiltinInverseSqrtScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "inverseSqrt"
}

class KslBuiltinInverseSqrtVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "inverseSqrt"
}

class KslBuiltinIsInfScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslBool1>(KslBool1, value) {
    override val name = "isInf"
}

abstract class KslBuiltinIsInfVector<V, B>(vec: KslVectorExpression<V, KslFloat1>, returnType: B)
    : KslBuiltinFunctionVector<B, KslBool1>(returnType, vec) where V: KslFloatType, V: KslVector<KslFloat1>, B: KslBoolType, B: KslVector<KslBool1> {
    override val name = "isInf"
}
class KslBuiltinIsInfVector2(vec: KslVectorExpression<KslFloat2, KslFloat1>)
    : KslBuiltinIsInfVector<KslFloat2, KslBool2>(vec, KslBool2)
class KslBuiltinIsInfVector3(vec: KslVectorExpression<KslFloat3, KslFloat1>)
    : KslBuiltinIsInfVector<KslFloat3, KslBool3>(vec, KslBool3)
class KslBuiltinIsInfVector4(vec: KslVectorExpression<KslFloat4, KslFloat1>)
    : KslBuiltinIsInfVector<KslFloat4, KslBool4>(vec, KslBool4)

class KslBuiltinIsNanScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslBool1>(KslBool1, value) {
    override val name = "isNan"
}

abstract class KslBuiltinIsNanVector<V, B>(vec: KslVectorExpression<V, KslFloat1>, returnType: B)
    : KslBuiltinFunctionVector<B, KslBool1>(returnType, vec) where V: KslFloatType, V: KslVector<KslFloat1>, B: KslBoolType, B: KslVector<KslBool1> {
    override val name = "isNan"
}
class KslBuiltinIsNanVector2(vec: KslVectorExpression<KslFloat2, KslFloat1>)
    : KslBuiltinIsNanVector<KslFloat2, KslBool2>(vec, KslBool2)
class KslBuiltinIsNanVector3(vec: KslVectorExpression<KslFloat3, KslFloat1>)
    : KslBuiltinIsNanVector<KslFloat3, KslBool3>(vec, KslBool3)
class KslBuiltinIsNanVector4(vec: KslVectorExpression<KslFloat4, KslFloat1>)
    : KslBuiltinIsNanVector<KslFloat4, KslBool4>(vec, KslBool4)

class KslBuiltinLength<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionScalar<KslFloat1>(KslFloat1, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "length"
}

class KslBuiltinLogScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "log"
}

class KslBuiltinLogVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "log"
}

class KslBuiltinLog2Scalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "log2"
}

class KslBuiltinLog2Vector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "log2"
}

class KslBuiltinMaxScalar<S>(a: KslScalarExpression<S>, b: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(a.expressionType, a, b) where S: KslNumericType, S: KslScalar {
    override val name = "max"
}

class KslBuiltinMaxVector<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(a.expressionType, a, b) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "max"
}

class KslBuiltinMinScalar<S>(a: KslScalarExpression<S>, b: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(a.expressionType, a, b) where S: KslNumericType, S: KslScalar {
    override val name = "min"
}

class KslBuiltinMinVector<V, S>(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(a.expressionType, a, b) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "min"
}

class KslBuiltinMixScalar(x: KslExprFloat1, y: KslExprFloat1, a: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(x.expressionType, x, y, a) {
    override val name = "mix"
}

class KslBuiltinMixVector<V>(x: KslVectorExpression<V, KslFloat1>, y: KslVectorExpression<V, KslFloat1>, a: KslExpression<*>)
    : KslBuiltinFunctionVector<V, KslFloat1>(x.expressionType, x, y, a) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "mix"
}

class KslBuiltinNormalize<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "normalize"
}

class KslBuiltinPowScalar(value: KslExprFloat1, power: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(KslFloat1, value, power) {
    override val name = "pow"
}

class KslBuiltinPowVector<V>(vec: KslVectorExpression<V, KslFloat1>, power: KslExpression<*>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec, power) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "pow"
}

class KslBuiltinRadiansScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "radians"
}

class KslBuiltinRadiansVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "radians"
}

class KslBuiltinReflect<V>(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(a.expressionType, a, b) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "reflect"
}

class KslBuiltinRefract<V>(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, i: KslExprFloat1)
    : KslBuiltinFunctionVector<V, KslFloat1>(a.expressionType, a, b, i) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "refract"
}

class KslBuiltinRoundScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "round"
}

class KslBuiltinRoundVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "round"
}

class KslBuiltinSignScalar<S>(value: KslScalarExpression<S>)
    : KslBuiltinFunctionScalar<S>(value.expressionType, value) where S: KslNumericType, S: KslScalar {
    override val name = "sign"
}

class KslBuiltinSignVector<V, S>(vec: KslVectorExpression<V, S>)
    : KslBuiltinFunctionVector<V, S>(vec.expressionType, vec) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    override val name = "sign"
}

class KslBuiltinSmoothStepScalar(low: KslExprFloat1, high: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(x.expressionType, low, high, x) {
    override val name = "smoothStep"
}

class KslBuiltinSmoothStepVector<V>(low: KslVectorExpression<V, KslFloat1>, high: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(x.expressionType, low, high, x) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "smoothStep"
}

class KslBuiltinSqrtScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "sqrt"
}

class KslBuiltinSqrtVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "sqrt"
}

class KslBuiltinStepScalar(edge: KslExprFloat1, x: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(edge.expressionType, edge, x) {
    override val name = "step"
}

class KslBuiltinStepVector<V>(edge: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(edge.expressionType, edge, x) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "step"
}

class KslBuiltinTrigonometryScalar(value: KslExprFloat1, override val name: String)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value)

class KslBuiltinTrigonometryVector<V>(vec: KslVectorExpression<V, KslFloat1>, override val name: String)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1>

class KslBuiltinTruncScalar(value: KslExprFloat1)
    : KslBuiltinFunctionScalar<KslFloat1>(value.expressionType, value) {
    override val name = "trunc"
}

class KslBuiltinTruncVector<V>(vec: KslVectorExpression<V, KslFloat1>)
    : KslBuiltinFunctionVector<V, KslFloat1>(vec.expressionType, vec) where V: KslFloatType, V: KslVector<KslFloat1> {
    override val name = "trunc"
}

class KslBuiltinDeterminant<M, V>(matrix: KslMatrixExpression<M, V>)
    : KslBuiltinFunctionScalar<KslFloat1>(KslFloat1, matrix) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    override val name = "determinant"
}

class KslBuiltinTranspose<M, V>(matrix: KslMatrixExpression<M, V>)
    : KslBuiltinFunctionMatrix<M, V>(matrix.expressionType, matrix) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    override val name = "transpose"
}
