package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp

sealed class KslStatement(opName: String, val parentScopeBuilder: KslScopeBuilder) : KslOp(opName, parentScopeBuilder) {
    protected fun addExpressionDependencies(expression: KslExpression<*>) {
        expression.collectStateDependencies().forEach {
            dependencies[it.state] = it
        }
    }
}