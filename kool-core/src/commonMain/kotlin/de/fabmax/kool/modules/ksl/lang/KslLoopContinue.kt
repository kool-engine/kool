package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslLoopContinue(parentScope: KslScopeBuilder) : KslStatement("continue", parentScope) {
    init {
        check(parentScope.isInLoop) { "continue can only be used within a loop" }
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslLoopContinue(transformBuilder)
    }
}