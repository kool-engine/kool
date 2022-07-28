package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionBit<T: KslNumericType>(
    val left: KslExpression<*>,
    val right: KslExpression<*>,
    val operator: KslBitOperator,
    override val expressionType: T)
    : KslExpression<T> {

    override fun collectStateDependencies(): Set<KslMutatedState> =
        left.collectStateDependencies() + right.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.bitExpression(this)
    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${operator.opString} ${right.toPseudoCode()})"
}

enum class KslBitOperator(val opString: String) {
    And("&"),
    Or("|"),
    Xor("^"),
    Shl("<<"),
    Shr(">>")
}

class KslExpressionBitScalar(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslBitOperator,
    expressionType: KslTypeInt1)
    : KslExpressionBit<KslTypeInt1>(left, right, operator, expressionType), KslScalarExpression<KslTypeInt1>

class KslExpressionBitVector<V>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslBitOperator,
    expressionType: V)
    : KslExpressionBit<V>(left, right, operator, expressionType), KslVectorExpression<V, KslTypeInt1>
        where V: KslIntType, V: KslVector<KslTypeInt1>


// scalar & scalar
infix fun KslScalarExpression<KslTypeInt1>.and(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.And, expressionType)
}
// vector & vector
infix fun <V> KslVectorExpression<V, KslTypeInt1>.and(right: KslVectorExpression<V, KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.And, expressionType)
}
// vector & scalar
infix fun <V> KslVectorExpression<V, KslTypeInt1>.plus(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.And, expressionType)
}


// scalar | scalar
infix fun KslScalarExpression<KslTypeInt1>.or(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Or, expressionType)
}
// vector | vector
infix fun <V> KslVectorExpression<V, KslTypeInt1>.or(right: KslVectorExpression<V, KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Or, expressionType)
}
// vector | scalar
infix fun <V> KslVectorExpression<V, KslTypeInt1>.or(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Or, expressionType)
}


// scalar ^ scalar
infix fun KslScalarExpression<KslTypeInt1>.xor(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Xor, expressionType)
}
// vector ^ vector
infix fun <V> KslVectorExpression<V, KslTypeInt1>.xor(right: KslVectorExpression<V, KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Xor, expressionType)
}
// vector ^ scalar
infix fun <V> KslVectorExpression<V, KslTypeInt1>.xor(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Xor, expressionType)
}


// scalar << scalar
infix fun KslScalarExpression<KslTypeInt1>.shl(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Shl, expressionType)
}
// vector << vector
infix fun <V> KslVectorExpression<V, KslTypeInt1>.shl(right: KslVectorExpression<V, KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Shl, expressionType)
}
// vector << scalar
infix fun <V> KslVectorExpression<V, KslTypeInt1>.shl(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Shl, expressionType)
}


// scalar << scalar
infix fun KslScalarExpression<KslTypeInt1>.shr(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Shr, expressionType)
}
// vector << vector
infix fun <V> KslVectorExpression<V, KslTypeInt1>.shr(right: KslVectorExpression<V, KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Shr, expressionType)
}
// vector << scalar
infix fun <V> KslVectorExpression<V, KslTypeInt1>.shr(right: KslScalarExpression<KslTypeInt1>): KslExpressionBitVector<V>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    return KslExpressionBitVector(this, right, KslBitOperator.Shr, expressionType)
}


class KslIntScalarComplement(val expr: KslScalarExpression<KslTypeInt1>) : KslScalarExpression<KslTypeInt1> {
    override val expressionType = expr.expressionType
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.intComplementExpression(this)
    override fun toPseudoCode(): String = "~(${expr.toPseudoCode()})"
}

operator fun KslScalarExpression<KslTypeInt1>.inv() = KslIntScalarComplement(this)

class KslIntVectorComplement<V>(val expr: KslVectorExpression<V, KslTypeInt1>) : KslVectorExpression<V, KslTypeInt1>
        where V: KslIntType, V: KslVector<KslTypeInt1> {
    override val expressionType = expr.expressionType
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.intComplementExpression(this)
    override fun toPseudoCode(): String = "~(${expr.toPseudoCode()})"
}

operator fun <V> KslVectorExpression<V, KslTypeInt1>.inv() where V: KslIntType, V: KslVector<KslTypeInt1> = KslIntVectorComplement(this)