package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslReturn(parentScope: KslScopeBuilder, val returnValue: KslExpression<*>) : KslStatement("return", parentScope) {
    init {
        check(parentScope.isInFunction || parentScope.parentStage is KslComputeStage) {
            "return can only be used within a function or in a compute stage"
        }
        addExpressionDependencies(returnValue)
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslReturn(transformBuilder, returnValue.replaced(replaceExpressions))
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("return ${returnValue.toPseudoCode()}")
    }
}