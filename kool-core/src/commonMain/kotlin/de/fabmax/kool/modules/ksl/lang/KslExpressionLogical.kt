package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslBoolVectorExpr<T>(val boolVec: KslVectorExpression<T, KslBool1>, val op: KslBoolVecOperator) :
    KslScalarExpression<KslBool1> where T: KslBoolType, T: KslVector<KslBool1>
{

    override val expressionType = KslBool1
    override fun collectStateDependencies(): Set<KslMutatedState> = boolVec.collectStateDependencies()
    override fun generateExpression(generator: KslGenerator): String = generator.boolVecExpression(this)
    override fun toPseudoCode(): String = "${op.opString}(${boolVec.toPseudoCode()})"
}

enum class KslBoolVecOperator(val opString: String) {
    Any("any"),
    All("all")
}

class KslBoolScalarExpr(val left: KslScalarExpression<KslBool1>, val right: KslScalarExpression<KslBool1>, val op: KslBoolScalarOperator) :
    KslScalarExpression<KslBool1>
{

    override val expressionType = KslBool1
    override fun collectStateDependencies(): Set<KslMutatedState> =
        left.collectStateDependencies() + right.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.boolScalarExpression(this)
    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${op.opString} ${right.toPseudoCode()})"
}

enum class KslBoolScalarOperator(val opString: String) {
    And("&&"),
    Or("||")
}

infix fun KslScalarExpression<KslBool1>.and(right: KslScalarExpression<KslBool1>): KslBoolScalarExpr =
    KslBoolScalarExpr(this, right, KslBoolScalarOperator.And)

infix fun KslScalarExpression<KslBool1>.or(right: KslScalarExpression<KslBool1>): KslBoolScalarExpr =
    KslBoolScalarExpr(this, right, KslBoolScalarOperator.Or)


class KslBoolNotExpr(val expr: KslScalarExpression<KslBool1>) : KslScalarExpression<KslBool1> {
    override val expressionType = KslBool1
    override fun collectStateDependencies(): Set<KslMutatedState> = expr.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.boolNotExpression(this)
    override fun toPseudoCode(): String = "!(${expr.toPseudoCode()})"
}

operator fun KslScalarExpression<KslBool1>.not() = KslBoolNotExpr(this)
