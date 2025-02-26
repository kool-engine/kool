package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp


class KslLoopDoWhile(val whileExpression: KslScalarExpression<KslBool1>, parentScope: KslScopeBuilder) :
    KslStatement("do-while", parentScope), KslLoop
{
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)

    init {
        addExpressionDependencies(whileExpression)
        childScopes += body
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslLoopDoWhile(whileExpression.replaced(replaceExpressions), transformBuilder).also { copy ->
            copy.body.copyFrom(body)
        }
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder("do\n")
        str.appendLine(body.toPseudoCode())
        str.append(annotatePseudoCode("do-while (${whileExpression.toPseudoCode()})"))
        return str.toString()
    }
}
