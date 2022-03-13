package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.generator.KslGenerator
import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslBoolVectorExpr<T>(val boolVec: KslVectorExpression<T, KslTypeBool1>, val op: KslBoolVecOperator)
    : KslScalarExpression<KslTypeBool1> where T: KslBoolType, T: KslVector<KslTypeBool1> {

    override val expressionType = KslTypeBool1
    override fun collectStateDependencies(): Set<KslMutatedState> = boolVec.collectStateDependencies()
    override fun generateExpression(generator: KslGenerator): String = generator.boolVecExpression(this)
    override fun toPseudoCode(): String = "${op.opString}(${boolVec.toPseudoCode()})"
}

enum class KslBoolVecOperator(val opString: String) {
    Any("any"),
    All("all")
}

class KslBoolScalarExpr(val left: KslScalarExpression<KslTypeBool1>, val right: KslScalarExpression<KslTypeBool1>, val op: KslBoolScalarOperator)
    : KslScalarExpression<KslTypeBool1> {

    override val expressionType = KslTypeBool1
    override fun collectStateDependencies(): Set<KslMutatedState> =
        left.collectStateDependencies() + right.collectStateDependencies()

    override fun generateExpression(generator: KslGenerator): String = generator.boolScalarExpression(this)
    override fun toPseudoCode(): String = "(${left.toPseudoCode()} ${op.opString} ${right.toPseudoCode()})"
}

enum class KslBoolScalarOperator(val opString: String) {
    And("&&"),
    Or("||")
}

infix fun KslScalarExpression<KslTypeBool1>.and(right: KslScalarExpression<KslTypeBool1>): KslBoolScalarExpr =
    KslBoolScalarExpr(this, right, KslBoolScalarOperator.And)

infix fun KslScalarExpression<KslTypeBool1>.or(right: KslScalarExpression<KslTypeBool1>): KslBoolScalarExpr =
    KslBoolScalarExpr(this, right, KslBoolScalarOperator.Or)
