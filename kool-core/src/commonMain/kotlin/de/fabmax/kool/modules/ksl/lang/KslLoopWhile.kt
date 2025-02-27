package de.fabmax.kool.modules.ksl.lang


class KslLoopWhile(val whileExpression: KslScalarExpression<KslBool1>, parentScope: KslScopeBuilder) :
    KslStatement("while", parentScope), KslLoop
{
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)

    init {
        addExpressionDependencies(whileExpression)
        childScopes += body
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder(annotatePseudoCode("while (${whileExpression.toPseudoCode()})") + "\n")
        str.append(body.toPseudoCode())
        return str.toString()
    }
}
