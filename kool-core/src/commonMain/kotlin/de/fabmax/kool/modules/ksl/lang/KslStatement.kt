package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

sealed class KslStatement(opName: String, val parentScopeBuilder: KslScopeBuilder) : KslOp(opName, parentScopeBuilder) {
    protected fun addExpressionDependencies(expression: KslExpression<*>) {
        expression.collectStateDependencies().forEach {
            dependencies[it.state] = it
        }
    }
}

class KslRawGLSL(private val raw: String, parentScope: KslScopeBuilder) : KslStatement("raw", parentScope) {
    override fun toPseudoCode(): String {
        return raw
    }
}