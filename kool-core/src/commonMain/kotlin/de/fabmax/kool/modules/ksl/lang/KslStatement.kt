package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslOp
import de.fabmax.kool.modules.ksl.model.KslScope

sealed class KslStatement(opName: String, parentScope: KslScope) : KslOp(opName, parentScope) {
    protected fun addExpression(expression: KslExpression<*>) {
        expression.collectStateDependencies().forEach {
            dependencies[it.state] = it
        }
    }
}