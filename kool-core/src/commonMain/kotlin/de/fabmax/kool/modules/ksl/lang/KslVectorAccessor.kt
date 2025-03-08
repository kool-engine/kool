package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import kotlin.math.max

abstract class KslVectorAccessor<T: KslType>(
    val vector: KslExpression<*>,
    val components: String,
    val type: T,
    expectedLength: Int
) : KslExpression<T>, KslAssignable<T> {

    override val expressionType = type
    override val assignType = type
    override val mutatingState: KslValue<*>? get() = vector.asAssignable()

    init {
        if (components.length != expectedLength) {
            throw IllegalArgumentException("invalid swizzle length: ${components.length} != $expectedLength")
        }
        checkComponents(components, (vector.expressionType as KslVector<*>).dimens)
    }

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(vector)
    override fun generateAssignable(generator: KslGenerator) = generator.vectorSwizzleAssignable(this)
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


fun <V> KslExpression<V>.float1(component: String): KslVectorAccessorScalar<KslFloat1> where V: KslType, V: KslVector<KslFloat1> {
    return KslVectorAccessorScalar(this, component, KslFloat1)
}
fun <V> KslExpression<V>.float2(components: String): KslVectorAccessorVector<KslFloat2, KslFloat1> where V: KslType, V: KslVector<KslFloat1> {
    return KslVectorAccessorVector(this, components, KslFloat2)
}
fun <V> KslExpression<V>.float3(components: String): KslVectorAccessorVector<KslFloat3, KslFloat1> where V: KslType, V: KslVector<KslFloat1> {
    return KslVectorAccessorVector(this, components, KslFloat3)
}
fun <V> KslExpression<V>.float4(components: String): KslVectorAccessorVector<KslFloat4, KslFloat1> where V: KslType, V: KslVector<KslFloat1> {
    return KslVectorAccessorVector(this, components, KslFloat4)
}

fun <V> KslExpression<V>.int1(component: String): KslVectorAccessorScalar<KslInt1> where V: KslType, V: KslVector<KslInt1> {
    return KslVectorAccessorScalar(this, component, KslInt1)
}
fun <V> KslExpression<V>.int2(components: String): KslVectorAccessorVector<KslInt2, KslInt1> where V: KslIntType, V: KslVector<KslInt1> {
    return KslVectorAccessorVector(this, components, KslInt2)
}
fun <V> KslExpression<V>.int3(components: String): KslVectorAccessorVector<KslInt3, KslInt1> where V: KslIntType, V: KslVector<KslInt1> {
    return KslVectorAccessorVector(this, components, KslInt3)
}
fun <V> KslExpression<V>.int4(components: String): KslVectorAccessorVector<KslInt4, KslInt1> where V: KslIntType, V: KslVector<KslInt1> {
    return KslVectorAccessorVector(this, components, KslInt4)
}

fun <V> KslExpression<V>.uint1(component: String): KslVectorAccessorScalar<KslUint1> where V: KslIntType, V: KslVector<KslUint1> {
    return KslVectorAccessorScalar(this, component, KslUint1)
}
fun <V> KslExpression<V>.uint2(components: String): KslVectorAccessorVector<KslUint2, KslUint1> where V: KslIntType, V: KslVector<KslUint1> {
    return KslVectorAccessorVector(this, components, KslUint2)
}
fun <V> KslExpression<V>.uint3(components: String): KslVectorAccessorVector<KslUint3, KslUint1> where V: KslIntType, V: KslVector<KslUint1> {
    return KslVectorAccessorVector(this, components, KslUint3)
}
fun <V> KslExpression<V>.uint4(components: String): KslVectorAccessorVector<KslUint4, KslUint1> where V: KslIntType, V: KslVector<KslUint1> {
    return KslVectorAccessorVector(this, components, KslUint4)
}

fun <V> KslExpression<V>.bool1(component: String): KslVectorAccessorScalar<KslBool1> where V: KslBoolType, V: KslVector<KslBool1> {
    return KslVectorAccessorScalar(this, component, KslBool1)
}
fun <V> KslExpression<V>.bool2(components: String): KslVectorAccessorVector<KslBool2, KslBool1> where V: KslBoolType, V: KslVector<KslBool1> {
    return KslVectorAccessorVector(this, components, KslBool2)
}
fun <V> KslExpression<V>.bool3(components: String): KslVectorAccessorVector<KslBool3, KslBool1> where V: KslBoolType, V: KslVector<KslBool1> {
    return KslVectorAccessorVector(this, components, KslBool3)
}
fun <V> KslExpression<V>.bool4(components: String): KslVectorAccessorVector<KslBool4, KslBool1> where V: KslBoolType, V: KslVector<KslBool1> {
    return KslVectorAccessorVector(this, components, KslBool4)
}
