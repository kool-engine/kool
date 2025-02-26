package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslLoopFor<S>(
    val loopVar: KslVarScalar<S>,
    val whileExpression: KslScalarExpression<KslBool1>,
    val incExpr: KslScalarExpression<S>,
    parentScope: KslScopeBuilder
) : KslStatement("for", parentScope), KslLoop where S: KslNumericType, S: KslScalar {

    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)

    init {
        addExpressionDependencies(whileExpression)
        addExpressionDependencies(incExpr)
        childScopes += body
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslLoopFor(loopVar, whileExpression.replaced(replaceExpressions), incExpr.replaced(replaceExpressions), transformBuilder).also { copy ->
            copy.body.copyFrom(body)
        }
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder(annotatePseudoCode("for (${loopVar.toPseudoCode()}; ${whileExpression.toPseudoCode()}; ${loopVar.stateName} += ${incExpr.toPseudoCode()})") + "\n")
        str.append(body.toPseudoCode())
        return str.toString()
    }
}
