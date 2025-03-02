package de.fabmax.kool.modules.ksl.lang

abstract class KslExpressionCompare<B: KslBoolType>(
    val left: KslExpression<*>,
    val right: KslExpression<*>,
    val operator: KslCompareOperator,
    override val expressionType: B
) : KslExpression<B> {

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(left, right)

    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${operator.opString} ${right.toPseudoCode()})"
}

enum class KslCompareOperator(val opString: String) {
    Equal("=="),
    NotEqual("!="),
    Less("<"),
    LessEqual("<="),
    Greater(">"),
    GreaterEqual(">=")
}

class KslExpressionCompareScalar(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslCompareOperator
) : KslExpressionCompare<KslBool1>(left, right, operator, KslBool1), KslScalarExpression<KslBool1>

class KslExpressionCompareVector<B>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslCompareOperator,
    expressionType: B
) : KslExpressionCompare<B>(left, right, operator, expressionType), KslVectorExpression<B, KslBool1>
        where B: KslBoolType, B: KslVector<KslBool1>


private fun <T> compareExpression(left: KslExpression<*>, right: KslExpression<*>, op: KslCompareOperator, expressionType: T):
        KslExpressionCompareVector<*> where T: KslNumericType, T: KslVector<*> {
    return when (expressionType) {
        is KslVector2<*> -> KslExpressionCompareVector(left, right, op, KslBool2)
        is KslVector3<*> -> KslExpressionCompareVector(left, right, op, KslBool3)
        is KslVector4<*> -> KslExpressionCompareVector(left, right, op, KslBool4)
        else -> throw IllegalArgumentException("Unexpected expression type: $expressionType")
    }
}


// scalar == scalar
infix fun <S> KslScalarExpression<S>.eq(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.Equal)
}
// vector == vector
infix fun <V, S> KslVectorExpression<V, S>.eq(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.Equal, expressionType)
}


// scalar != scalar
infix fun <S> KslScalarExpression<S>.ne(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.NotEqual)
}
// vector != vector
infix fun <V, S> KslVectorExpression<V, S>.ne(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.NotEqual, expressionType)
}


// scalar < scalar
infix fun <S> KslScalarExpression<S>.lt(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslNumericType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.Less)
}
// vector < vector
infix fun <V, S> KslVectorExpression<V, S>.lt(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.Less, expressionType)
}


// scalar <= scalar
infix fun <S> KslScalarExpression<S>.le(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslNumericType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.LessEqual)
}
// vector <= vector
infix fun <V, S> KslVectorExpression<V, S>.le(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.LessEqual, expressionType)
}


// scalar > scalar
infix fun <S> KslScalarExpression<S>.gt(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslNumericType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.Greater)
}
// vector > vector
infix fun <V, S> KslVectorExpression<V, S>.gt(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.Greater, expressionType)
}


// scalar > scalar
infix fun <S> KslScalarExpression<S>.ge(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslNumericType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.GreaterEqual)
}
// vector > vector
infix fun <V, S> KslVectorExpression<V, S>.ge(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.GreaterEqual, expressionType)
}
