package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionCompare<B: KslBoolType>(
    val left: KslExpression<*>,
    val right: KslExpression<*>,
    val operator: KslCompareOperator,
    override val expressionType: B)
    : KslExpression<B> {

    override fun collectStateDependencies(): Set<KslMutatedState> =
        left.collectStateDependencies() + right.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.compareExpression(this)
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
    operator: KslCompareOperator)
    : KslExpressionCompare<KslTypeBool1>(left, right, operator, KslTypeBool1), KslScalarExpression<KslTypeBool1>

class KslExpressionCompareVector<B>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslCompareOperator,
    expressionType: B)
    : KslExpressionCompare<B>(left, right, operator, expressionType), KslVectorExpression<B, KslTypeBool1>
        where B: KslBoolType, B: KslVector<KslTypeBool1>


private fun <T> compareExpression(left: KslExpression<*>, right: KslExpression<*>, op: KslCompareOperator, expressionType: T):
        KslExpressionCompareVector<*> where T: KslNumericType, T: KslVector<*> {
    return when (expressionType) {
        is KslVector2<*> -> KslExpressionCompareVector(left, right, op, KslTypeBool2)
        is KslVector3<*> -> KslExpressionCompareVector(left, right, op, KslTypeBool3)
        is KslVector4<*> -> KslExpressionCompareVector(left, right, op, KslTypeBool4)
        else -> TODO()
    }
}


// scalar == scalar
infix fun <S> KslScalarExpression<S>.`==`(right: KslScalarExpression<S>) where S: KslType, S: KslScalar = eq(right)
infix fun <S> KslScalarExpression<S>.eq(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.Equal)
}
// vector == vector
infix fun <V, S> KslVectorExpression<V, S>.`==`(right: KslVectorExpression<V, S>) where V: KslNumericType, V: KslVector<S>, S: KslScalar = eq(right)
infix fun <V, S> KslVectorExpression<V, S>.eq(right: KslVectorExpression<V, S>): KslExpressionCompareVector<*>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return compareExpression(this, right, KslCompareOperator.Equal, expressionType)
}


// scalar != scalar
infix fun <S> KslScalarExpression<S>.`!=`(right: KslScalarExpression<S>) where S: KslType, S: KslScalar = ne(right)
infix fun <S> KslScalarExpression<S>.ne(right: KslScalarExpression<S>): KslExpressionCompareScalar
        where S: KslType, S: KslScalar {
    return KslExpressionCompareScalar(this, right, KslCompareOperator.NotEqual)
}
// vector != vector
infix fun <V, S> KslVectorExpression<V, S>.`!=`(right: KslVectorExpression<V, S>) where V: KslNumericType, V: KslVector<S>, S: KslScalar = ne(right)
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
