package de.fabmax.kool.modules.ksl.lang

abstract class KslExpressionBit<T: KslNumericType>(
    val left: KslExpression<*>,
    val right: KslExpression<*>,
    val operator: KslBitOperator,
    override val expressionType: T)
    : KslExpression<T> {

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(left, right)

    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${operator.opString} ${right.toPseudoCode()})"
}

enum class KslBitOperator(val opString: String) {
    And("&"),
    Or("|"),
    Xor("^"),
    Shl("<<"),
    Shr(">>")
}

class KslExpressionBitScalar<S>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslBitOperator,
    expressionType: S
) : KslExpressionBit<S>(left, right, operator, expressionType), KslScalarExpression<S>
        where S: KslIntType, S: KslScalar

class KslExpressionBitVector<V, S>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslBitOperator,
    expressionType: V
) : KslExpressionBit<V>(left, right, operator, expressionType), KslVectorExpression<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar


// scalar & scalar
infix fun <S> KslScalarExpression<S>.and(right: KslScalarExpression<S>): KslExpressionBitScalar<S>
        where S: KslIntType, S: KslScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.And, expressionType)
}
// vector & vector
infix fun <V, S> KslVectorExpression<V, S>.and(right: KslVectorExpression<V, S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.And, expressionType)
}
// vector & scalar
infix fun <V, S> KslVectorExpression<V, S>.and(right: KslScalarExpression<S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.And, expressionType)
}


// scalar | scalar
infix fun <S> KslScalarExpression<S>.or(right: KslScalarExpression<S>): KslExpressionBitScalar<S>
        where S: KslIntType, S: KslScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Or, expressionType)
}
// vector | vector
infix fun <V, S> KslVectorExpression<V, S>.or(right: KslVectorExpression<V, S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Or, expressionType)
}
// vector | scalar
infix fun <V, S> KslVectorExpression<V, S>.or(right: KslScalarExpression<S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Or, expressionType)
}


// scalar ^ scalar
infix fun <S> KslScalarExpression<S>.xor(right: KslScalarExpression<S>): KslExpressionBitScalar<S>
        where S: KslIntType, S: KslScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Xor, expressionType)
}
// vector ^ vector
infix fun <V, S> KslVectorExpression<V, S>.xor(right: KslVectorExpression<V, S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Xor, expressionType)
}
// vector ^ scalar
infix fun <V, S> KslVectorExpression<V, S>.xor(right: KslScalarExpression<S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Xor, expressionType)
}


// scalar << scalar
infix fun <S> KslScalarExpression<S>.shl(right: KslScalarExpression<S>): KslExpressionBitScalar<S>
        where S: KslIntType, S: KslScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Shl, expressionType)
}
// vector << vector
infix fun <V, S> KslVectorExpression<V, S>.shl(right: KslVectorExpression<V, S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Shl, expressionType)
}
// vector << scalar
infix fun <V, S> KslVectorExpression<V, S>.shl(right: KslScalarExpression<S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Shl, expressionType)
}


// scalar >> scalar
infix fun <S> KslScalarExpression<S>.shr(right: KslScalarExpression<S>): KslExpressionBitScalar<S>
        where S: KslIntType, S: KslScalar {
    return KslExpressionBitScalar(this, right, KslBitOperator.Shr, expressionType)
}
// vector >> vector
infix fun <V, S> KslVectorExpression<V, S>.shr(right: KslVectorExpression<V, S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Shr, expressionType)
}
// vector >> scalar
infix fun <V, S> KslVectorExpression<V, S>.shr(right: KslScalarExpression<S>): KslExpressionBitVector<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    return KslExpressionBitVector(this, right, KslBitOperator.Shr, expressionType)
}


class KslIntScalarComplement<S>(val expr: KslScalarExpression<S>) : KslScalarExpression<S>
        where S: KslIntType, S: KslScalar {
    override val expressionType = expr.expressionType
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(expr)
    override fun toPseudoCode(): String = "~(${expr.toPseudoCode()})"
}
fun <S> KslScalarExpression<S>.inv() where S: KslIntType, S: KslScalar = KslIntScalarComplement(this)

class KslIntVectorComplement<V, S>(val expr: KslVectorExpression<V, S>) : KslVectorExpression<V, S>
        where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar {
    override val expressionType = expr.expressionType
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(expr)
    override fun toPseudoCode(): String = "~(${expr.toPseudoCode()})"
}
fun <V, S> KslVectorExpression<V, S>.inv() where V: KslIntType, V: KslVector<S>, S: KslIntType, S: KslScalar = KslIntVectorComplement(this)