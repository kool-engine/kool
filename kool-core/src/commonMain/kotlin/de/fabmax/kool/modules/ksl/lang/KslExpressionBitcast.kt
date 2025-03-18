package de.fabmax.kool.modules.ksl.lang

import kotlin.jvm.JvmName

sealed class KslExpressionBitcast<T : KslType>(val value: KslExpression<*>, type: T) : KslExpression<T> {
    override val expressionType = type
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(value)
    override fun toPseudoCode() = "bitcast<$expressionType>(${value.toPseudoCode()})"
}

class KslExpressionBitcastScalar<T>(value: KslExpression<*>, type: T) : KslExpressionBitcast<T>(value, type), KslScalarExpression<T>
    where T : KslType, T : KslScalar
class KslExpressionBitcastVec<T, S>(value: KslExpression<*>, type: T) : KslExpressionBitcast<T>(value, type), KslVectorExpression<T, S>
    where T : KslType, T : KslVector<S>, S : KslScalar

fun KslExprFloat1.toIntBits() = KslExpressionBitcastScalar(this, KslInt1)
fun KslExprFloat1.toUintBits() = KslExpressionBitcastScalar(this, KslUint1)
@JvmName("intBitsToFloat")
fun KslExprInt1.toFloatBits() = KslExpressionBitcastScalar(this, KslFloat1)
@JvmName("uintBitsToFloat")
fun KslExprUint1.toFloatBits() = KslExpressionBitcastScalar(this, KslFloat1)

@JvmName("vec2fToVec2i")
fun KslExprFloat2.toIntBits() = KslExpressionBitcastVec(this, KslInt2)
@JvmName("vec2fToVec2u")
fun KslExprFloat2.toUintBits() = KslExpressionBitcastVec(this, KslUint2)
@JvmName("vec2iToVec2f")
fun KslExprInt2.toFloatBits() = KslExpressionBitcastVec(this, KslFloat2)
@JvmName("vec2uToVec2f")
fun KslExprUint2.toFloatBits() = KslExpressionBitcastVec(this, KslFloat2)

@JvmName("vec3fToVec3i")
fun KslExprFloat3.toIntBits() = KslExpressionBitcastVec(this, KslInt3)
@JvmName("vec3fToVec3u")
fun KslExprFloat3.toUintBits() = KslExpressionBitcastVec(this, KslUint3)
@JvmName("vec3iToVec3f")
fun KslExprInt3.toFloatBits() = KslExpressionBitcastVec(this, KslFloat3)
@JvmName("vec3uToVec3f")
fun KslExprUint3.toFloatBits() = KslExpressionBitcastVec(this, KslFloat3)

@JvmName("vec4fToVec4i")
fun KslExprFloat4.toIntBits() = KslExpressionBitcastVec(this, KslInt4)
@JvmName("vec4fToVec4u")
fun KslExprFloat4.toUintBits() = KslExpressionBitcastVec(this, KslUint4)
@JvmName("vec4iToVec4f")
fun KslExprInt4.toFloatBits() = KslExpressionBitcastVec(this, KslFloat4)
@JvmName("vec4uToVec4f")
fun KslExprUint4.toFloatBits() = KslExpressionBitcastVec(this, KslFloat4)
