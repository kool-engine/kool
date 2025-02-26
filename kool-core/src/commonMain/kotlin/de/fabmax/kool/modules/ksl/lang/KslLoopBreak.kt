package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslLoopBreak(parentScope: KslScopeBuilder) : KslStatement("break", parentScope) {
    init {
        check(parentScope.isInLoop) { "break can only be used within a loop" }
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslLoopBreak(transformBuilder)
    }
}