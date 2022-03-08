package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

abstract class KslExpressionCast<T: KslType>(val value: KslExpression<*>, type: T) : KslExpression<T> {
    override val expressionType = type

    override fun collectStateDependencies(): Set<KslMutatedState> = value.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator) = generator.castExpression(this)
    override fun toPseudoCode() = "cast<${expressionType.typeName}>(${value.toPseudoCode()})"
}

class KslExpressionCastScalar<S>(value: KslExpression<*>, type: S)
    : KslExpressionCast<S>(value, type), KslScalarExpression<S> where S: KslScalar, S: KslType
class KslExpressionCastVector<V, S>(value: KslExpression<*>, type: V)
    : KslExpressionCast<V>(value, type), KslVectorExpression<V, S> where V: KslVector<S>, V: KslType, S: KslScalar

fun KslScalarExpression<KslTypeFloat1>.toInt1() = KslExpressionCastScalar(this, KslTypeInt1)
fun KslScalarExpression<KslTypeInt1>.toFloat1() = KslExpressionCastScalar(this, KslTypeFloat1)

fun KslVectorExpression<KslTypeFloat2, KslTypeFloat1>.toInt2() = KslExpressionCastVector(this, KslTypeInt2)
fun KslVectorExpression<KslTypeInt2, KslTypeInt1>.toFloat2() = KslExpressionCastVector(this, KslTypeFloat2)

fun KslVectorExpression<KslTypeFloat3, KslTypeFloat1>.toInt3() = KslExpressionCastVector(this, KslTypeInt3)
fun KslVectorExpression<KslTypeInt3, KslTypeInt1>.toFloat3() = KslExpressionCastVector(this, KslTypeFloat3)

fun KslVectorExpression<KslTypeFloat4, KslTypeFloat1>.toInt4() = KslExpressionCastVector(this, KslTypeInt4)
fun KslVectorExpression<KslTypeInt4, KslTypeInt1>.toFloat4() = KslExpressionCastVector(this, KslTypeFloat4)
