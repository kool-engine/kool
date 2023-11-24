package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState
import kotlin.math.max

abstract class KslVectorAccessor<T: KslType>(val vector: KslExpression<*>, val components: String, val type: T, expectedLength: Int)
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


class KslVectorAccessorScalar<S>(vector: KslExpression<*>, components: String, type: S)
    : KslVectorAccessor<S>(vector, components, type, 1), KslScalarExpression<S> where S: KslType, S: KslScalar
class KslVectorAccessorVector<V, S>(vector: KslExpression<*>, components: String, type: V)
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

fun <V> KslExpression<V>.float1(component: String): KslVectorAccessorScalar<KslTypeFloat1> where V: KslType, V: KslVector<KslTypeFloat1> {
    return KslVectorAccessorScalar(this, component, KslTypeFloat1)
}
fun <V> KslExpression<V>.float2(components: String): KslVectorAccessorVector<KslTypeFloat2, KslTypeFloat1> where V: KslType, V: KslVector<KslTypeFloat1> {
    return KslVectorAccessorVector(this, components, KslTypeFloat2)
}
fun <V> KslExpression<V>.float3(components: String): KslVectorAccessorVector<KslTypeFloat3, KslTypeFloat1> where V: KslType, V: KslVector<KslTypeFloat1> {
    return KslVectorAccessorVector(this, components, KslTypeFloat3)
}
fun <V> KslExpression<V>.float4(components: String): KslVectorAccessorVector<KslTypeFloat4, KslTypeFloat1> where V: KslType, V: KslVector<KslTypeFloat1> {
    return KslVectorAccessorVector(this, components, KslTypeFloat4)
}

fun <V> KslExpression<V>.int1(component: String): KslVectorAccessorScalar<KslTypeInt1> where V: KslType, V: KslVector<KslTypeInt1> {
    return KslVectorAccessorScalar(this, component, KslTypeInt1)
}
fun <V> KslExpression<V>.int2(components: String): KslVectorAccessorVector<KslTypeInt2, KslTypeInt1> where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslVectorAccessorVector(this, components, KslTypeInt2)
}
fun <V> KslExpression<V>.int3(components: String): KslVectorAccessorVector<KslTypeInt3, KslTypeInt1> where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslVectorAccessorVector(this, components, KslTypeInt3)
}
fun <V> KslExpression<V>.int4(components: String): KslVectorAccessorVector<KslTypeInt4, KslTypeInt1> where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslVectorAccessorVector(this, components, KslTypeInt4)
}

fun <V> KslExpression<V>.uint1(component: String): KslVectorAccessorScalar<KslTypeUint1> where V: KslIntType, V: KslVector<KslTypeUint1> {
    return KslVectorAccessorScalar(this, component, KslTypeUint1)
}
fun <V> KslExpression<V>.uint2(components: String): KslVectorAccessorVector<KslTypeUint2, KslTypeUint1> where V: KslIntType, V: KslVector<KslTypeUint1> {
    return KslVectorAccessorVector(this, components, KslTypeUint2)
}
fun <V> KslExpression<V>.uint3(components: String): KslVectorAccessorVector<KslTypeUint3, KslTypeUint1> where V: KslIntType, V: KslVector<KslTypeUint1> {
    return KslVectorAccessorVector(this, components, KslTypeUint3)
}
fun <V> KslExpression<V>.uint4(components: String): KslVectorAccessorVector<KslTypeUint4, KslTypeUint1> where V: KslIntType, V: KslVector<KslTypeUint1> {
    return KslVectorAccessorVector(this, components, KslTypeUint4)
}

fun <V> KslExpression<V>.bool1(component: String): KslVectorAccessorScalar<KslTypeBool1> where V: KslBoolType, V: KslVector<KslTypeBool1> {
    return KslVectorAccessorScalar(this, component, KslTypeBool1)
}
fun <V> KslExpression<V>.bool2(components: String): KslVectorAccessorVector<KslTypeBool2, KslTypeBool1> where V: KslBoolType, V: KslVector<KslTypeBool1> {
    return KslVectorAccessorVector(this, components, KslTypeBool2)
}
fun <V> KslExpression<V>.bool3(components: String): KslVectorAccessorVector<KslTypeBool3, KslTypeBool1> where V: KslBoolType, V: KslVector<KslTypeBool1> {
    return KslVectorAccessorVector(this, components, KslTypeBool3)
}
fun <V> KslExpression<V>.bool4(components: String): KslVectorAccessorVector<KslTypeBool4, KslTypeBool1> where V: KslBoolType, V: KslVector<KslTypeBool1> {
    return KslVectorAccessorVector(this, components, KslTypeBool4)
}
