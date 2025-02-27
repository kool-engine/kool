package de.fabmax.kool.modules.ksl.lang

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

    override fun toPseudoCode(): String {
        return annotatePseudoCode("${assignTarget.toPseudoCode()} ${augmentationMode.opChar}= ${assignExpression.toPseudoCode()}")
    }
}
