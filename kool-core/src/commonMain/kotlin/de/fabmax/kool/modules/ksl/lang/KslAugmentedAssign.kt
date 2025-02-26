package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslAugmentedAssign<T: KslType>(
    val assignTarget: KslAssignable<T>,
    val augmentationMode: KslMathOperator,
    val assignExpression: KslExpression<T>,
    scopeBuilder: KslScopeBuilder
) : KslStatement("augmentedAssign", scopeBuilder) {
    init {
        assignTarget.checkIsAssignable(scopeBuilder)

        addExpressionDependencies(assignExpression)
        addMutation(assignTarget.mutatingState!!.mutate())
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslAugmentedAssign(assignTarget, augmentationMode, assignExpression.replaced(replaceExpressions), transformBuilder)
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("${assignTarget.toPseudoCode()} ${augmentationMode.opChar}= ${assignExpression.toPseudoCode()}")
    }
}
