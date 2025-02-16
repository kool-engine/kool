package de.fabmax.kool.modules.ksl.lang

class KslReturn(parentScope: KslScopeBuilder, val returnValue: KslExpression<*>) : KslStatement("return", parentScope) {
    init {
        check(parentScope.isInFunction || parentScope.parentStage is KslComputeStage) {
            "return can only be used within a function or in a compute stage"
        }
        addExpressionDependencies(returnValue)
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("return ${returnValue.toPseudoCode()}")
    }
}