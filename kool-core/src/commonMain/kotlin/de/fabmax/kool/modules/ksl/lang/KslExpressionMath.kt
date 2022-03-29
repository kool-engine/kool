package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionMath<T: KslNumericType>(
    val left: KslExpression<*>,
    val right: KslExpression<*>,
    val operator: KslMathOperator,
    override val expressionType: T)
    : KslExpression<T> {

    override fun collectStateDependencies(): Set<KslMutatedState> =
        left.collectStateDependencies() + right.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.mathExpression(this)
    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${operator.opChar} ${right.toPseudoCode()})"
}

enum class KslMathOperator(val opChar: Char) {
    Plus('+'),
    Minus('-'),
    Times('*'),
    Divide('/'),
    Remainder('%')
}

class KslExpressionMathScalar<S>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslMathOperator,
    expressionType: S)
    : KslExpressionMath<S>(left, right, operator, expressionType), KslScalarExpression<S>
        where S: KslNumericType, S: KslScalar

class KslExpressionMathVector<V, S>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslMathOperator,
    expressionType: V)
    : KslExpressionMath<V>(left, right, operator, expressionType), KslVectorExpression<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar

class KslExpressionMathMatrix<M, V>(
    left: KslExpression<*>,
    right: KslExpression<*>,
    operator: KslMathOperator,
    expressionType: M)
    : KslExpressionMath<M>(left, right, operator, expressionType), KslMatrixExpression<M, V>
        where M: KslFloatType, M: KslMatrix<V>, V: KslVector<*>


// scalar + scalar
operator fun <S> KslScalarExpression<S>.plus(right: KslScalarExpression<S>): KslExpressionMathScalar<S>
        where S: KslNumericType, S: KslScalar {
    return KslExpressionMathScalar(this, right, KslMathOperator.Plus, expressionType)
}
// vector + vector
operator fun <V, S> KslVectorExpression<V, S>.plus(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Plus, expressionType)
}
// vector + scalar
operator fun <V, S> KslVectorExpression<V, S>.plus(right: KslScalarExpression<S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Plus, expressionType)
}
// scalar + vector
operator fun <V, S> KslScalarExpression<S>.plus(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Plus, right.expressionType)
}
// matrix + matrix
operator fun <M, V> KslMatrixExpression<M, V>.plus(right: KslMatrixExpression<M, V>): KslExpressionMathMatrix<M, V>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    return KslExpressionMathMatrix(this, right, KslMathOperator.Plus, expressionType)
}


// scalar - scalar
operator fun <S> KslScalarExpression<S>.minus(right: KslScalarExpression<S>): KslExpressionMathScalar<S>
        where S: KslNumericType, S: KslScalar {
    return KslExpressionMathScalar(this, right, KslMathOperator.Minus, expressionType)
}
// vector - vector
operator fun <V, S> KslVectorExpression<V, S>.minus(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Minus, expressionType)
}
// vector - scalar
operator fun <V, S> KslVectorExpression<V, S>.minus(right: KslScalarExpression<S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Minus, expressionType)
}
// scalar - vector
operator fun <V, S> KslScalarExpression<S>.minus(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Minus, right.expressionType)
}
// matrix - matrix
operator fun <M, V> KslMatrixExpression<M, V>.minus(right: KslMatrixExpression<M, V>): KslExpressionMathMatrix<M, V>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    return KslExpressionMathMatrix(this, right, KslMathOperator.Minus, expressionType)
}


// scalar * scalar
operator fun <S> KslScalarExpression<S>.times(right: KslScalarExpression<S>): KslExpressionMathScalar<S>
        where S: KslNumericType, S: KslScalar {
    return KslExpressionMathScalar(this, right, KslMathOperator.Times, expressionType)
}
// vector * vector
operator fun <V, S> KslVectorExpression<V, S>.times(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Times, expressionType)
}
// vector * scalar
operator fun <V, S> KslVectorExpression<V, S>.times(right: KslScalarExpression<S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Times, expressionType)
}
// scalar * vector
operator fun <V, S> KslScalarExpression<S>.times(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Times, right.expressionType)
}

// matrix * vector
operator fun <M, V> KslMatrixExpression<M, V>.times(right: KslVectorExpression<V, KslTypeFloat1>): KslExpressionMathVector<V, KslTypeFloat1>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<KslTypeFloat1> {
    return KslExpressionMathVector(this, right, KslMathOperator.Times, right.expressionType)
}
// vector * matrix
operator fun <M, V> KslVectorExpression<V, KslTypeFloat1>.times(right: KslMatrixExpression<M, V>): KslExpressionMathVector<V, KslTypeFloat1>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<KslTypeFloat1> {
    return KslExpressionMathVector(this, right, KslMathOperator.Times, expressionType)
}
// matrix * scalar
operator fun <M, V> KslMatrixExpression<M, V>.times(right: KslScalarExpression<KslTypeFloat1>): KslExpressionMathMatrix<M, V>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<KslTypeFloat1> {
    return KslExpressionMathMatrix(this, right, KslMathOperator.Times, expressionType)
}
// matrix * matrix
operator fun <M, V> KslMatrixExpression<M, V>.times(right: KslMatrixExpression<M, V>): KslExpressionMathMatrix<M, V>
        where M: KslFloatType, M: KslMatrix<V>, V: KslFloatType, V: KslVector<*> {
    return KslExpressionMathMatrix(this, right, KslMathOperator.Times, expressionType)
}


// scalar / scalar
operator fun <S> KslScalarExpression<S>.div(right: KslScalarExpression<S>): KslExpressionMathScalar<S>
        where S: KslNumericType, S: KslScalar {
    return KslExpressionMathScalar(this, right, KslMathOperator.Divide, expressionType)
}
// vector / vector
operator fun <V, S> KslVectorExpression<V, S>.div(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Divide, expressionType)
}
// vector / scalar
operator fun <V, S> KslVectorExpression<V, S>.div(right: KslScalarExpression<S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Divide, expressionType)
}
// scalar / vector
operator fun <V, S> KslScalarExpression<S>.div(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Divide, right.expressionType)
}


// scalar % scalar
operator fun <S> KslScalarExpression<S>.rem(right: KslScalarExpression<S>): KslExpressionMathScalar<S>
        where S: KslNumericType, S: KslScalar {
    return KslExpressionMathScalar(this, right, KslMathOperator.Remainder, expressionType)
}
// vector % vector
operator fun <V, S> KslVectorExpression<V, S>.rem(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Remainder, expressionType)
}
// vector % scalar
operator fun <V, S> KslVectorExpression<V, S>.rem(right: KslScalarExpression<S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Remainder, expressionType)
}
// scalar % vector
operator fun <V, S> KslScalarExpression<S>.rem(right: KslVectorExpression<V, S>): KslExpressionMathVector<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {
    return KslExpressionMathVector(this, right, KslMathOperator.Remainder, right.expressionType)
}
