package de.fabmax.kool.modules.ksl.lang

abstract class KslExpressionCast<T: KslType>(val value: KslExpression<*>, type: T) : KslExpression<T> {
    override val expressionType = type

    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive(value)

    override fun toPseudoCode() = "cast<${expressionType.typeName}>(${value.toPseudoCode()})"
}

class KslExpressionCastScalar<S>(value: KslExpression<*>, type: S)
    : KslExpressionCast<S>(value, type), KslScalarExpression<S> where S: KslScalar, S: KslType
class KslExpressionCastVector<V, S>(value: KslExpression<*>, type: V)
    : KslExpressionCast<V>(value, type), KslVectorExpression<V, S> where V: KslVector<S>, V: KslType, S: KslScalar
