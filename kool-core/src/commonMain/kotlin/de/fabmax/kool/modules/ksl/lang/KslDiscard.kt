package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

class KslDiscard(parentScope: KslScopeBuilder) : KslStatement("break", parentScope) {
    init {
        check(parentScope.parentStage is KslFragmentStage) { "discard can only be used in fragment stage" }
    }

    override fun copyWithTransformedExpressions(transformBuilder: KslScopeBuilder, replaceExpressions: Map<KslExpression<*>, KslExpression<*>>): KslOp {
        return KslDiscard(transformBuilder)
    }
}