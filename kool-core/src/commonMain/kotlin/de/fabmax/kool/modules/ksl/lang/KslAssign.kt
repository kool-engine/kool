package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslAssign<T: KslType>(val assignTarget: KslAssignable<T>, val assignExpression: KslExpression<T>, scopeBuilder: KslScopeBuilder) :
    KslStatement("assign", scopeBuilder)
{
    init {
        assignTarget.checkIsAssignable(scopeBuilder)

        addExpressionDependencies(assignExpression)
        addMutation(assignTarget.mutatingState!!.mutate())
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslAssign(assignTarget, assignExpression.replaced(replaceExpressions), transformBuilder)
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("${assignTarget.toPseudoCode()} = ${assignExpression.toPseudoCode()}")
    }
}