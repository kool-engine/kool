package de.fabmax.kool.modules.ksl.lang

class KslAssign<T: KslType>(val assignTarget: KslAssignable<T>, val assignExpression: KslExpression<T>, scopeBuilder: KslScopeBuilder)
    : KslStatement("assign", scopeBuilder)
{
    init {
        assignTarget.checkIsAssignable(scopeBuilder)

        addExpressionDependencies(assignExpression)
        addMutation(assignTarget.mutatingState!!.mutate())
    }

    override fun toPseudoCode(): String {
        return "${assignTarget.toPseudoCode()} = ${assignExpression.toPseudoCode()} // ${dependenciesAndMutationsToString()}"
    }
}