package de.fabmax.kool.modules.ksl.lang

class KslReturn(parentScope: KslScopeBuilder, val returnValue: KslExpression<*>) : KslStatement("return", parentScope) {
    init {
        check(parentScope.isInFunction) { "return can only be used within a function" }
        addExpressionDependencies(returnValue)
    }

    override fun toPseudoCode(): String {
        return annotatePseudoCode("return ${returnValue.toPseudoCode()}")
    }
}