package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

sealed class KslStatement(opName: String, val parentScopeBuilder: KslScopeBuilder) : KslOp(opName, parentScopeBuilder) {
    protected fun addExpressionDependencies(expression: KslExpression<*>) {
        val subExpressions = expression.collectSubExpressions()
        usedExpressions.addAll(subExpressions)
        subExpressions.distinct().filterIsInstance<KslValue<*>>().forEach {
            addDependency(it.depend())
        }
    }
}

class KslInlineCode(val code: String, parentScope: KslScopeBuilder) : KslStatement("inlineCode", parentScope) {
    override fun toPseudoCode(): String {
        return code
    }
}