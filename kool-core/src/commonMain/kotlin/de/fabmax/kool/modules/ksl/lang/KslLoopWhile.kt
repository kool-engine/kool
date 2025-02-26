package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp


class KslLoopWhile(val whileExpression: KslScalarExpression<KslBool1>, parentScope: KslScopeBuilder) :
    KslStatement("while", parentScope), KslLoop
{
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)

    init {
        addExpressionDependencies(whileExpression)
        childScopes += body
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslLoopWhile(whileExpression.replaced(replaceExpressions), transformBuilder).also { copy ->
            copy.body.copyFrom(body)
        }
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder(annotatePseudoCode("while (${whileExpression.toPseudoCode()})") + "\n")
        str.append(body.toPseudoCode())
        return str.toString()
    }
}
