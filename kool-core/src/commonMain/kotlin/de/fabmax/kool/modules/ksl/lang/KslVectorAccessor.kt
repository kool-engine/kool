package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState
import kotlin.math.max

abstract class KslVectorAccessor<T: KslType>(val vector: KslVectorExpression<*,*>, val components: String, val type: T, expectedLength: Int)
    : KslExpression<T>, KslAssignable<T> {

    override val expressionType = type
    override val assignType = type

    override val mutatingState: KslValue<*>?
        get() = vector as? KslValue<*> ?: (vector as? KslAssignable<*>)?.mutatingState

    init {
        if (components.length != expectedLength) {
            throw IllegalArgumentException("invalid swizzle length: ${components.length} != $expectedLength")
        }
        checkComponents(components, (vector.expressionType as KslVector<*>).dimens)
    }

    override fun collectStateDependencies(): Set<KslMutatedState> = vector.collectStateDependencies()
    override fun generateAssignable(generator: KslGenerator) = generator.vectorSwizzleAssignable(this)
    override fun generateExpression(generator: KslGenerator) = generator.vectorSwizzleExpression(this)
    override fun toPseudoCode() = "${vector.toPseudoCode()}.$components"

    private fun checkComponents(components: String, inputDim: Int) {
        if (!components.matches(Regex("[xyzwrgba]+"))) {
            throw IllegalArgumentException("invalid swizzle components requested: $components (supported: xyzw, rgba)")
        }
        var reqDim = 1
        components.forEach { c ->
            reqDim = when (c) {
                'y' -> max(reqDim, 2)
                'z' -> max(reqDim, 3)
                'w' -> max(reqDim, 4)
                'g' -> max(reqDim, 2)
                'b' -> max(reqDim, 3)
                'a' -> max(reqDim, 4)
                else -> reqDim
            }
        }
        if (reqDim > inputDim) {
            throw IllegalArgumentException("requested swizzle requires $reqDim dimensions but input expression has only $inputDim")
        }
    }
}


class KslVectorAccessorScalar<S>(vector: KslVectorExpression<*,*>, components: String, type: S)
    : KslVectorAccessor<S>(vector, components, type, 1), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslVectorAccessorVector<V, S>(vector: KslVectorExpression<*,*>, components: String, type: V)
    : KslVectorAccessor<V>(vector, components, type, type.dimens), KslVectorExpression<V, S> where V: KslType, V: KslVector<S>, S: KslScalar


@Suppress("UNCHECKED_CAST")
private fun <S> scalarTypeOf(expr: KslVectorExpression<*, S>): S where S: KslType, S: KslScalar {
    return when (expr.expressionType) {
        is KslFloatType -> KslTypeFloat1 as S
        is KslIntType -> KslTypeInt1 as S
        is KslBoolType -> KslTypeBool1 as S
        else -> throw IllegalStateException("invalid vector expression type: ${expr.expressionType}")
    }
}

val <V, S> KslVectorExpression<V, S>.x: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "x", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.y: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "y", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.z: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "z", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.w: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "w", scalarTypeOf(this))

val <V, S> KslVectorExpression<V, S>.r: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "r", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.g: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "g", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.b: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "b", scalarTypeOf(this))
val <V, S> KslVectorExpression<V, S>.a: KslVectorAccessorScalar<S> where V: KslType, V: KslVector<S>, S: KslType, S: KslScalar
    get() = KslVectorAccessorScalar(this, "a", scalarTypeOf(this))

val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.xyz get() = float3("xyz")
val KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.rgb get() = float3("rgb")

fun <V> KslVectorExpression<V, *>.float2(components: String): KslVectorAccessorVector<KslTypeFloat2, KslTypeFloat1> where V: KslFloatType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeFloat2)
}
fun <V> KslVectorExpression<V, *>.int2(components: String): KslVectorAccessorVector<KslTypeInt2, KslTypeInt1> where V: KslIntType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeInt2)
}
fun <V> KslVectorExpression<V, *>.bool2(components: String): KslVectorAccessorVector<KslTypeBool2, KslTypeBool1> where V: KslBoolType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeBool2)
}

fun <V> KslVectorExpression<V, *>.float3(components: String): KslVectorAccessorVector<KslTypeFloat3, KslTypeFloat1> where V: KslFloatType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeFloat3)
}
fun <V> KslVectorExpression<V, *>.int3(components: String): KslVectorAccessorVector<KslTypeInt3, KslTypeInt1> where V: KslIntType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeInt3)
}
fun <V> KslVectorExpression<V, *>.bool3(components: String): KslVectorAccessorVector<KslTypeBool3, KslTypeBool1> where V: KslBoolType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeBool3)
}

fun <V> KslVectorExpression<V, *>.float4(components: String): KslVectorAccessorVector<KslTypeFloat4, KslTypeFloat1> where V: KslFloatType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeFloat4)
}
fun <V> KslVectorExpression<V, *>.int4(components: String): KslVectorAccessorVector<KslTypeInt4, KslTypeInt1> where V: KslIntType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeInt4)
}
fun <V> KslVectorExpression<V, *>.bool4(components: String): KslVectorAccessorVector<KslTypeBool4, KslTypeBool1> where V: KslBoolType, V: KslVector<*> {
    return KslVectorAccessorVector(this, components, KslTypeBool4)
}
