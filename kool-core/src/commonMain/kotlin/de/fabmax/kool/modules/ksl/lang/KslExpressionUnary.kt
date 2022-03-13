package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslNumericScalarUnaryMinus<S>(val expr: KslScalarExpression<S>) : KslScalarExpression<S>
        where S: KslNumericType, S: KslScalar {

    override val expressionType = expr.expressionType
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.numericUnaryMinusExpression(this)
    override fun toPseudoCode(): String = "-(${expr.toPseudoCode()})"
}

operator fun <S> KslScalarExpression<S>.unaryMinus() where S: KslNumericType, S: KslScalar = KslNumericScalarUnaryMinus(this)

class KslNumericVectorUnaryMinus<V, S>(val expr: KslVectorExpression<V, S>) : KslVectorExpression<V, S>
        where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar {

    override val expressionType = expr.expressionType
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.numericUnaryMinusExpression(this)
    override fun toPseudoCode(): String = "-(${expr.toPseudoCode()})"
}

operator fun <V, S> KslVectorExpression<V, S>.unaryMinus() where V: KslNumericType, V: KslVector<S>, S: KslNumericType, S: KslScalar = KslNumericVectorUnaryMinus(this)

class KslBoolNotExpr(val expr: KslScalarExpression<KslTypeBool1>) : KslScalarExpression<KslTypeBool1> {
    override val expressionType = KslTypeBool1
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.boolNotExpression(this)
    override fun toPseudoCode(): String = "!(${expr.toPseudoCode()})"
}

operator fun KslScalarExpression<KslTypeBool1>.not() = KslBoolNotExpr(this)