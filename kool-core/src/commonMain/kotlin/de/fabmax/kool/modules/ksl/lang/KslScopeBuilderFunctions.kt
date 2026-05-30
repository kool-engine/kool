package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.math.Vec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.Vec4f
import de.fabmax.kool.pipeline.backend.NdcYDirection
import de.fabmax.kool.util.Struct
import kotlin.jvm.JvmName

// function invocation
context(b: KslScopeBuilder)
operator fun <S> KslFunction<S>.invoke(vararg args: KslExpression<*>): KslScalarExpression<S> where S: KslType, S: KslScalar {
    return KslInvokeFunctionScalar(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <V, S> KslFunction<V>.invoke(vararg args: KslExpression<*>): KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
    return KslInvokeFunctionVector(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <M, V> KslFunction<M>.invoke(vararg args: KslExpression<*>): KslMatrixExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
    return KslInvokeFunctionMatrix(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <S> KslFunction<KslStruct<S>>.invoke(vararg args: KslExpression<*>): KslExprStruct<S> where S: Struct {
    return KslInvokeFunctionStruct(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <S> KslFunction<KslArrayType<S>>.invoke(vararg args: KslExpression<*>): KslScalarArrayExpression<S> where S: KslType, S: KslScalar {
    return KslInvokeFunctionScalarArray(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <V, S> KslFunction<KslArrayType<V>>.invoke(vararg args: KslExpression<*>): KslVectorArrayExpression<V, S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar {
    return KslInvokeFunctionVectorArray(this, b, returnType, *args)
}
context(b: KslScopeBuilder)
operator fun <M, V> KslFunction<KslArrayType<M>>.invoke(vararg args: KslExpression<*>): KslMatrixArrayExpression<M, V> where M: KslType, M: KslMatrix<V>, V: KslType, V: KslVector<*> {
    return KslInvokeFunctionMatrixArray(this, b, returnType, *args)
}
@JvmName("invokeVoid")
context(b: KslScopeBuilder)
operator fun KslFunction<KslTypeVoid>.invoke(vararg args: KslExpression<*>): KslExpression<KslTypeVoid> {
    return KslInvokeFunctionVoid(this, b, *args)
}

// builtin general functions
context(_: KslScopeBuilder)
fun <S> abs(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinAbsScalar(value)
context(_: KslScopeBuilder)
fun <V, S> abs(vec: KslVectorExpression<V, S>) where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinAbsVector(vec)

context(_: KslScopeBuilder)
fun atan2(y: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) = KslBuiltinAtan2Scalar(y, x)
context(_: KslScopeBuilder)
fun <V> atan2(y: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinAtan2Vector(y, x)

context(_: KslScopeBuilder)
fun ceil(value: KslScalarExpression<KslFloat1>) = KslBuiltinCeilScalar(value)
context(_: KslScopeBuilder)
fun <V> ceil(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinCeilVector(vec)

context(_: KslScopeBuilder)
fun <S> clamp(value: KslScalarExpression<S>, min: KslScalarExpression<S>, max: KslScalarExpression<S>)
    where S: KslNumericType, S: KslScalar = KslBuiltinClampScalar(value, min, max)
context(_: KslScopeBuilder)
fun <V, S> clamp(vec: KslVectorExpression<V, S>, min: KslVectorExpression<V, S>, max: KslVectorExpression<V, S>)
    where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinClampVector(vec, min, max)

context(_: KslScopeBuilder)
fun saturate(value: KslExprFloat1) = clamp(value, 0f.const, 1f.const)
@JvmName("saturate2f")
context(_: KslScopeBuilder)
fun saturate(value: KslExprFloat2) = clamp(value, Vec2f.ZERO.const, Vec2f.ONES.const)
@JvmName("saturate3f")
context(_: KslScopeBuilder)
fun saturate(value: KslExprFloat3) = clamp(value, Vec3f.ZERO.const, Vec3f.ONES.const)
@JvmName("saturate4f")
context(_: KslScopeBuilder)
fun saturate(value: KslExprFloat4) = clamp(value, Vec4f.ZERO.const, Vec4f.ONES.const)

context(_: KslScopeBuilder)
fun cross(a: KslVectorExpression<KslFloat3, KslFloat1>, b: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinCross(a, b)

context(_: KslScopeBuilder)
fun degrees(value: KslScalarExpression<KslFloat1>) = KslBuiltinDegreesScalar(value)
context(_: KslScopeBuilder)
fun <V> degrees(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDegreesVector(vec)

context(_: KslScopeBuilder)
fun <T: KslFloatType> distance(a: KslExpression<T>, b: KslExpression<T>) = KslBuiltinDistanceScalar(a, b)

context(_: KslScopeBuilder)
fun <V> dot(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDot(a, b)

context(_: KslScopeBuilder)
fun dpdx(value: KslScalarExpression<KslFloat1>) = KslBuiltinDpdxScalar(value)
context(_: KslScopeBuilder)
fun <V> dpdx(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDpdxVector(vec)
context(_: KslScopeBuilder)
fun dpdy(value: KslScalarExpression<KslFloat1>) = KslBuiltinDpdyScalar(value)
context(_: KslScopeBuilder)
fun <V> dpdy(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinDpdyVector(vec)

context(_: KslScopeBuilder)
fun exp(value: KslScalarExpression<KslFloat1>) = KslBuiltinExpScalar(value)
context(_: KslScopeBuilder)
fun <V> exp(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinExpVector(vec)
context(_: KslScopeBuilder)
fun exp2(value: KslScalarExpression<KslFloat1>) = KslBuiltinExpScalar(value)
context(_: KslScopeBuilder)
fun <V> exp2(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinExpVector(vec)

context(_: KslScopeBuilder)
fun <V> faceForward(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, c: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFaceForward(a, b, c)

context(_: KslScopeBuilder)
fun floor(value: KslScalarExpression<KslFloat1>) = KslBuiltinFloorScalar(value)
context(_: KslScopeBuilder)
fun <V> floor(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFloorVector(vec)

context(_: KslScopeBuilder)
fun fma(a: KslScalarExpression<KslFloat1>, b: KslScalarExpression<KslFloat1>, c: KslScalarExpression<KslFloat1>) = KslBuiltinFmaScalar(a, b, c)
context(_: KslScopeBuilder)
fun <V> fma(a: KslVectorExpression<V, KslFloat1>, b: KslVectorExpression<V, KslFloat1>, c: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFmaVector(a, b, c)

context(_: KslScopeBuilder)
fun fract(value: KslScalarExpression<KslFloat1>) = KslBuiltinFractScalar(value)
context(_: KslScopeBuilder)
fun <V> fract(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinFractVector(vec)

context(_: KslScopeBuilder)
fun inverseSqrt(value: KslScalarExpression<KslFloat1>) = KslBuiltinInverseSqrtScalar(value)
context(_: KslScopeBuilder)
fun <V> inverseSqrt(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinInverseSqrtVector(vec)

context(_: KslScopeBuilder)
fun isNan(value: KslScalarExpression<KslFloat1>) = KslBuiltinIsNanScalar(value)
context(_: KslScopeBuilder)
fun isNan(vec: KslVectorExpression<KslFloat2, KslFloat1>) = KslBuiltinIsNanVector2(vec)
context(_: KslScopeBuilder)
fun isNan(vec: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinIsNanVector3(vec)
context(_: KslScopeBuilder)
fun isNan(vec: KslVectorExpression<KslFloat4, KslFloat1>) = KslBuiltinIsNanVector4(vec)

context(_: KslScopeBuilder)
fun isInf(value: KslScalarExpression<KslFloat1>) = KslBuiltinIsNanScalar(value)
context(_: KslScopeBuilder)
fun isInf(vec: KslVectorExpression<KslFloat2, KslFloat1>) = KslBuiltinIsInfVector2(vec)
context(_: KslScopeBuilder)
fun isInf(vec: KslVectorExpression<KslFloat3, KslFloat1>) = KslBuiltinIsInfVector3(vec)
context(_: KslScopeBuilder)
fun isInf(vec: KslVectorExpression<KslFloat4, KslFloat1>) = KslBuiltinIsInfVector4(vec)

context(_: KslScopeBuilder)
fun <V> length(arg: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLength(arg)

context(_: KslScopeBuilder)
fun log(value: KslScalarExpression<KslFloat1>) = KslBuiltinLogScalar(value)
context(_: KslScopeBuilder)
fun <V> log(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLogVector(vec)
context(_: KslScopeBuilder)
fun log2(value: KslScalarExpression<KslFloat1>) = KslBuiltinLog2Scalar(value)
context(_: KslScopeBuilder)
fun <V> log2(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinLog2Vector(vec)

context(_: KslScopeBuilder)
fun <S> max(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinMaxScalar(a, b)
context(_: KslScopeBuilder)
fun <V, S> max(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinMaxVector(a, b)

context(_: KslScopeBuilder)
fun <S> min(a: KslScalarExpression<S>, b: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinMinScalar(a, b)
context(_: KslScopeBuilder)
fun <V, S> min(a: KslVectorExpression<V, S>, b: KslVectorExpression<V, S>)
    where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinMinVector(a, b)

context(_: KslScopeBuilder)
fun mix(x: KslScalarExpression<KslFloat1>, y: KslScalarExpression<KslFloat1>, a: KslScalarExpression<KslFloat1>) =
    KslBuiltinMixScalar(x, y, a)
context(_: KslScopeBuilder)
fun <V> mix(x: KslVectorExpression<V, KslFloat1>, y: KslVectorExpression<V, KslFloat1>, a: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinMixVector(x, y, a)
context(_: KslScopeBuilder)
fun <V> mix(x: KslVectorExpression<V, KslFloat1>, y: KslVectorExpression<V, KslFloat1>, a: KslScalarExpression<KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinMixVector(x, y, a)

context(_: KslScopeBuilder)
fun <V> normalize(arg: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinNormalize(arg)

context(_: KslScopeBuilder)
fun pow(value: KslScalarExpression<KslFloat1>, power: KslScalarExpression<KslFloat1>) = KslBuiltinPowScalar(value, power)
context(_: KslScopeBuilder)
fun <V> pow(vec: KslVectorExpression<V, KslFloat1>, power: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinPowVector(vec, power)

context(_: KslScopeBuilder)
fun radians(value: KslScalarExpression<KslFloat1>) = KslBuiltinRadiansScalar(value)
context(_: KslScopeBuilder)
fun <V> radians(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRadiansVector(vec)

context(_: KslScopeBuilder)
fun <V> reflect(incident: KslVectorExpression<V, KslFloat1>, normal: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinReflect(incident, normal)
context(_: KslScopeBuilder)
fun <V> refract(incident: KslVectorExpression<V, KslFloat1>, normal: KslVectorExpression<V, KslFloat1>, eta: KslScalarExpression<KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRefract(incident, normal, eta)

context(_: KslScopeBuilder)
fun round(value: KslScalarExpression<KslFloat1>) = KslBuiltinRoundScalar(value)
context(_: KslScopeBuilder)
fun <V> round(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinRoundVector(vec)

context(_: KslScopeBuilder)
fun <S> sign(value: KslScalarExpression<S>) where S: KslNumericType, S: KslScalar = KslBuiltinSignScalar(value)
context(_: KslScopeBuilder)
fun <V, S> sign(vec: KslVectorExpression<V, S>)
    where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslBuiltinSignVector(vec)

context(_: KslScopeBuilder)
fun smoothStep(low: KslScalarExpression<KslFloat1>, high: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) = KslBuiltinSmoothStepScalar(low, high, x)
context(_: KslScopeBuilder)
fun <V> smoothStep(low: KslVectorExpression<V, KslFloat1>, high: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>)
    where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinSmoothStepVector(low, high, x)

context(_: KslScopeBuilder)
fun sqrt(value: KslScalarExpression<KslFloat1>) = KslBuiltinSqrtScalar(value)
context(_: KslScopeBuilder)
fun <V> sqrt(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinSqrtVector(vec)

context(_: KslScopeBuilder)
fun step(edge: KslScalarExpression<KslFloat1>, x: KslScalarExpression<KslFloat1>) = KslBuiltinStepScalar(edge, x)
context(_: KslScopeBuilder)
fun <V> step(edge: KslVectorExpression<V, KslFloat1>, x: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinStepVector(edge, x)

context(_: KslScopeBuilder)
fun trunc(value: KslScalarExpression<KslFloat1>) = KslBuiltinTruncScalar(value)
context(_: KslScopeBuilder)
fun <V> trunc(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTruncVector(vec)

// builtin trigonometry functions
context(_: KslScopeBuilder)
fun cos(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "cos")
context(_: KslScopeBuilder)
fun sin(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "sin")
context(_: KslScopeBuilder)
fun tan(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "tan")
context(_: KslScopeBuilder)
fun cosh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "cosh")
context(_: KslScopeBuilder)
fun sinh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "sinh")
context(_: KslScopeBuilder)
fun tanh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "tanh")

context(_: KslScopeBuilder)
fun <V> cos(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "cos")
context(_: KslScopeBuilder)
fun <V> sin(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "sin")
context(_: KslScopeBuilder)
fun <V> tan(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "tan")
context(_: KslScopeBuilder)
fun <V> cosh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "cosh")
context(_: KslScopeBuilder)
fun <V> sinh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "sinh")
context(_: KslScopeBuilder)
fun <V> tanh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "tanh")

context(_: KslScopeBuilder)
fun acos(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "acos")
context(_: KslScopeBuilder)
fun asin(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "asin")
context(_: KslScopeBuilder)
fun atan(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "atan")
context(_: KslScopeBuilder)
fun acosh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "acosh")
context(_: KslScopeBuilder)
fun asinh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "asinh")
context(_: KslScopeBuilder)
fun atanh(value: KslScalarExpression<KslFloat1>) = KslBuiltinTrigonometryScalar(value, "atanh")

context(_: KslScopeBuilder)
fun <V> acos(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "acos")
context(_: KslScopeBuilder)
fun <V> asin(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "asin")
context(_: KslScopeBuilder)
fun <V> atan(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "atan")
context(_: KslScopeBuilder)
fun <V> acosh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "acosh")
context(_: KslScopeBuilder)
fun <V> asinh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "asinh")
context(_: KslScopeBuilder)
fun <V> atanh(vec: KslVectorExpression<V, KslFloat1>) where V: KslFloatType, V: KslVector<KslFloat1> = KslBuiltinTrigonometryVector(vec, "atanh")

// builtin matrix functions
context(_: KslScopeBuilder)
fun <M, V> determinant(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> = KslBuiltinDeterminant(matrix)
context(_: KslScopeBuilder)
fun <M, V> transpose(matrix: KslMatrixExpression<M, V>) where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> = KslBuiltinTranspose(matrix)

// builtin texture functions
context(_: KslScopeBuilder)
fun <T: KslColorSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, lod: KslScalarExpression<KslFloat1>? = null) = KslSampleColorTexture(this, coord, lod)
context(_: KslScopeBuilder)
fun <T: KslColorSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, ddx: KslExpression<C>, ddy: KslExpression<C>,) = KslSampleColorTextureGrad(this, coord, ddx, ddy)
context(_: KslScopeBuilder)
fun <T: KslDepthSampler<C>, C: KslFloatType> KslExpression<T>.sample(coord: KslExpression<C>, depthRef: KslExprFloat1) = KslSampleDepthTexture(this, coord, depthRef)
context(_: KslScopeBuilder)
fun <T, C: KslFloatType> KslExpression<T>.sample(arrayIndex: KslExprInt1, coord: KslExpression<C>, lod: KslExprFloat1? = null)
    where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArray(this, arrayIndex, coord, lod)
context(_: KslScopeBuilder)
fun <T, C: KslFloatType> KslExpression<T>.sample(arrayIndex: KslExprInt1, coord: KslExpression<C>, ddx: KslExpression<C>, ddy: KslExpression<C>)
    where T: KslColorSampler<C>, T: KslSamplerArrayType = KslSampleColorTextureArrayGrad(this, arrayIndex, coord, ddx, ddy)

context(_: KslScopeBuilder)
fun <T: KslColorSampler<R>, R: KslFloatType, C: KslIntType> KslExpression<T>.load(coord: KslExpression<C>, lod: KslScalarExpression<KslInt1>? = null) =
    KslImageTextureLoad(this, coord, lod, KslFloat4)
@JvmName("loadInt")
context(_: KslScopeBuilder)
fun <T: KslIntSampler<R>, R: KslFloatType, C: KslIntType> KslExpression<T>.load(coord: KslExpression<C>, lod: KslScalarExpression<KslInt1>? = null) =
    KslImageTextureLoad(this, coord, lod, KslInt4)
@JvmName("loadUint")
context(_: KslScopeBuilder)
fun <T: KslUintSampler<R>, R: KslFloatType, C: KslIntType> KslExpression<T>.load(coord: KslExpression<C>, lod: KslScalarExpression<KslInt1>? = null) =
    KslImageTextureLoad(this, coord, lod, KslUint4)

context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler1dType = KslTextureSize1d(this, lod)
context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler2dType = KslTextureSize2d(this, lod)
context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler3dType = KslTextureSize3d(this, lod)
context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSamplerCubeType = KslTextureSizeCube(this, lod)
context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler2dType, T: KslSamplerArrayType = KslTextureSize2dArray(this, lod)
context(_: KslScopeBuilder)
fun <T> KslExpression<T>.size(lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSamplerCubeType, T: KslSamplerArrayType = KslTextureSizeCubeArray(this, lod)

context(_: KslScopeBuilder)
fun <R: KslNumericType> KslStorageTexture1d<KslStorageTexture1dType<R>, R>.size(): KslExprInt1 = KslStorageTextureSize1d(this)
context(_: KslScopeBuilder)
fun <R: KslNumericType> KslStorageTexture2d<KslStorageTexture2dType<R>, R>.size(): KslExprInt2 = KslStorageTextureSize2d(this)
context(_: KslScopeBuilder)
fun <R: KslNumericType> KslStorageTexture3d<KslStorageTexture3dType<R>, R>.size(): KslExprInt3 = KslStorageTextureSize3d(this)

@Deprecated("Use sampler.sample(coord, [lod]) instead")
context(_: KslScopeBuilder)
fun <T: KslColorSampler<C>, C: KslFloatType> sampleTexture(
    sampler: KslExpression<T>,
    coord: KslExpression<C>,
    lod: KslScalarExpression<KslFloat1>? = null
) = sampler.sample(coord, lod)

@Deprecated("Use sampler.sample(coord, ddx, ddy) instead")
context(_: KslScopeBuilder)
fun <T: KslColorSampler<C>, C: KslFloatType> sampleTextureGrad(
    sampler: KslExpression<T>,
    coord: KslExpression<C>,
    ddx: KslExpression<C>,
    ddy: KslExpression<C>,
) = sampler.sample(coord, ddx, ddy)

@Deprecated("Use sampler.sample(coord, depthRef) instead")
context(_: KslScopeBuilder)
fun <T: KslDepthSampler<C>, C: KslFloatType> sampleDepthTexture(
    sampler: KslExpression<T>,
    coord: KslExpression<C>,
    depthRef: KslExprFloat1
) = sampler.sample(coord, depthRef)

@Deprecated("Use sampler.sample(arrayIndex, coord, [lod]) instead")
context(_: KslScopeBuilder)
fun <T, C: KslFloatType> sampleTextureArray(
    sampler: KslExpression<T>,
    arrayIndex: KslExprInt1,
    coord: KslExpression<C>,
    lod: KslExprFloat1? = null
) where T: KslColorSampler<C>, T: KslSamplerArrayType = sampler.sample(arrayIndex, coord, lod)

@Deprecated("Use sampler.sample(arrayIndex, coord, ddx, ddy) instead")
context(_: KslScopeBuilder)
fun <T, C: KslFloatType> sampleTextureArrayGrad(
    sampler: KslExpression<T>,
    arrayIndex: KslExprInt1,
    coord: KslExpression<C>,
    ddx: KslExpression<C>,
    ddy: KslExpression<C>,
) where T: KslColorSampler<C>, T: KslSamplerArrayType = sampler.sample(arrayIndex, coord, ddx, ddy)

/**
 * texelFetch — perform a lookup of a single texel within a texture
 * @param sampler Specifies the sampler to which the texture from which texels will be retrieved is bound.
 * @param coord Specifies the integer texture coordinates at which texture will be sampled.
 * @param lod If present, specifies the level-of-detail within the texture from which the texel will be fetched.
 */
@Deprecated("use sampler.load() instead")
context(_: KslScopeBuilder)
fun <T: KslColorSampler<R>, R: KslFloatType, C: KslIntType> texelFetch(
    sampler: KslExpression<T>,
    coord: KslExpression<C>,
    lod: KslScalarExpression<KslInt1>? = null
) = sampler.load(coord, lod)

@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSize1d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler1dType = sampler.size(lod)
@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSize2d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler2dType = sampler.size(lod)
@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSize3d(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler3dType = sampler.size(lod)
@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSizeCube(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSamplerCubeType = sampler.size(lod)
@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSize2dArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSampler2dType, T: KslSamplerArrayType = sampler.size(lod)
@Deprecated("use sampler.size() instead")
context(_: KslScopeBuilder)
fun <T> textureSizeCubeArray(sampler: KslExpression<T>, lod: KslScalarExpression<KslInt1> = 0.const)
    where T: KslSamplerType<*>, T: KslSamplerCubeType, T: KslSamplerArrayType = sampler.size(lod)

@Deprecated("use size() instead")
@JvmName("storageTexSize1d")
context(_: KslScopeBuilder)
fun <T: KslNumericType> textureSize1d(storageTex: KslStorageTexture1d<KslStorageTexture1dType<T>, T>) = storageTex.size()
@Deprecated("use size() instead")
@JvmName("storageTexSize2d")
context(_: KslScopeBuilder)
fun <T: KslNumericType> textureSize2d(storageTex: KslStorageTexture2d<KslStorageTexture2dType<T>, T>) = storageTex.size()
@Deprecated("use size() instead")
@JvmName("storageTexSize3d")
context(_: KslScopeBuilder)
fun <T: KslNumericType> textureSize3d(storageTex: KslStorageTexture3d<KslStorageTexture3dType<T>, T>) = storageTex.size()

// builtin storage functions
@JvmName("loadStruct")
context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.load(index: KslExprInt1): KslExpression<R> {
    return KslStorageRead(this, index, expressionType.elemType)
}
@JvmName("loadScalar")
context(_: KslScopeBuilder)
fun <T: KslStorageType<S>, S> KslStorage<T>.load(index: KslExprInt1): KslScalarExpression<S> where S : KslScalar, S : KslNumericType {
    return KslStorageReadScalar(this, index, expressionType.elemType)
}
@JvmName("loadVector")
context(_: KslScopeBuilder)
fun <T: KslStorageType<V>, V, S> KslStorage<T>.load(index: KslExprInt1): KslVectorExpression<V, S> where V: KslNumericType, V : KslVector<S>, S : KslScalar {
    return KslStorageReadVector(this, index, expressionType.elemType)
}
@JvmName("getStruct")
context(_: KslScopeBuilder)
operator fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.get(index: KslExprInt1): KslExpression<R> = load(index)
@JvmName("getScalar")
context(_: KslScopeBuilder)
operator fun <T: KslPrimitiveStorageType<S>, S> KslStorage<T>.get(index: KslExprInt1): KslScalarExpression<S> where S : KslScalar, S : KslNumericType = load(index)
@JvmName("getVector")
context(_: KslScopeBuilder)
operator fun <T: KslPrimitiveStorageType<V>, V, S> KslStorage<T>.get(index: KslExprInt1): KslVectorExpression<V, S> where V : KslNumericType, V : KslVector <S>, S : KslScalar = load(index)

context(b: KslScopeBuilder)
fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.store(index: KslExprInt1, data: KslExpression<R>) {
    b.ops += KslStorageWrite(this, index, data, b)
}
context(b: KslScopeBuilder)
operator fun <T: KslStorageType<R>, R: KslType> KslStorage<T>.set(index: KslExprInt1, data: KslExpression<R>) = store(index, data)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicSwap(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Swap, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicAdd(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Add, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicAnd(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.And, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicOr(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Or, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicXor(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Xor, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicMin(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Min, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicMax(
    index: KslExprInt1,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicOp(this, index, data, KslStorageAtomicOp.Op.Max, storageType.elemType)

context(_: KslScopeBuilder)
fun <T: KslStorageType<R>, R> KslStorage<T>.atomicCompareSwap(
    index: KslExprInt1,
    compare: KslExpression<R>,
    data: KslExpression<R>
): KslScalarExpression<R> where R: KslIntType, R: KslScalar =
    KslStorageAtomicCompareSwap(this, index, data, compare, storageType.elemType)

// builtin storage texture functions
context(_: KslScopeBuilder)
operator fun <T: KslStorageTextureType<R, C>, R: KslFloatType, C: KslIntType> KslStorageTexture<T, R, C>.get(coord: KslExpression<C>): KslExprFloat4 =
    KslStorageTextureLoad(this, coord)

@JvmName("getTex2d")
context(_: KslScopeBuilder)
operator fun <T: KslStorageTexture2dType<R>, R: KslFloatType> KslStorageTexture2d<T, R>.get(x: KslExprInt1, y: KslExprInt1): KslExprFloat4 =
    KslStorageTextureLoad(this, int2Value(x, y))

@JvmName("getTex3d")
context(_: KslScopeBuilder)
operator fun <T: KslStorageTexture3dType<R>, R: KslFloatType> KslStorageTexture3d<T, R>.get(x: KslExprInt1, y: KslExprInt1, z: KslExprInt1): KslExprFloat4 =
    KslStorageTextureLoad(this, int3Value(x, y, z))

context(b: KslScopeBuilder)
operator fun <T: KslStorageTextureType<R, C>, R: KslFloatType, C: KslIntType> KslStorageTexture<T, R, C>.set(
    coord: KslExpression<C>,
    data: KslExprFloat4
) {
    b.ops += KslStorageTextureStore(this, coord, data, b)
}

@JvmName("setTex2d")
context(b: KslScopeBuilder)
operator fun <T: KslStorageTexture2dType<R>, R: KslFloatType> KslStorageTexture2d<T, R>.set(
    x: KslExprInt1,
    y: KslExprInt1,
    data: KslExprFloat4
) {
    b.ops += KslStorageTextureStore(this, int2Value(x, y), data, b)
}

@JvmName("setTex3d")
context(b: KslScopeBuilder)
operator fun <T: KslStorageTexture3dType<R>, R: KslFloatType> KslStorageTexture3d<T, R>.set(
    x: KslExprInt1,
    y: KslExprInt1,
    z: KslExprInt1,
    data: KslExprFloat4
) {
    b.ops += KslStorageTextureStore(this, int3Value(x, y, z), data, b)
}

context(_: KslScopeBuilder)
fun <T: KslStorageTextureType<R, C>, R: KslFloatType, C: KslIntType> KslStorageTexture<T, R, C>.load(
    coord: KslExpression<C>
): KslExprFloat4 = KslStorageTextureLoad(this, coord)

context(b: KslScopeBuilder)
fun <T: KslStorageTextureType<R, C>, R: KslFloatType, C: KslIntType> KslStorageTexture<T, R, C>.store(
    coord: KslExpression<C>,
    data: KslExprFloat4
) {
    b.ops += KslStorageTextureStore(this, coord, data, b)
}

@JvmName("loadIntTex")
context(_: KslScopeBuilder)
fun <T: KslStorageTextureType<R, C>, R: KslIntType, C: KslIntType> KslStorageTexture<T, R, C>.load(
    coord: KslExpression<C>
): KslExprInt4 = KslStorageTextureLoadInt(this, coord)

@JvmName("storeIntTex")
context(b: KslScopeBuilder)
fun <T: KslStorageTextureType<R, C>, R: KslIntType, C: KslIntType> KslStorageTexture<T, R, C>.store(
    coord: KslExpression<C>,
    data: KslExprInt4
) {
    b.ops += KslStorageTextureStore(this, coord, data, b)
}

// higher level util functions
context(_: KslScopeBuilder)
fun KslVarVector<KslFloat2, KslFloat1>.flipUvByDeviceCoords(onClipY: NdcYDirection = NdcYDirection.TOP_TO_BOTTOM) {
    if (KoolSystem.requireContext().backend.deviceCoordinates.ndcYDirection == onClipY) {
        y set 1f.const - y
    }
}