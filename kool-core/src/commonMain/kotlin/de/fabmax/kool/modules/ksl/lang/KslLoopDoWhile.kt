package de.fabmax.kool.modules.ksl.lang


class KslLoopDoWhile(val whileExpression: KslScalarExpression<KslBool1>, parentScope: KslScopeBuilder) :
    KslStatement("do-while", parentScope), KslLoop
{
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)

    init {
        addExpressionDependencies(whileExpression)
        childScopes += body
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder("do\n")
        str.appendLine(body.toPseudoCode())
        str.append(annotatePseudoCode("do-while (${whileExpression.toPseudoCode()})"))
        return str.toString()
    }
}
