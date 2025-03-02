package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslState

abstract class KslValue<T: KslType>(stateName: String, val isMutable: Boolean) : KslState(stateName), KslExpression<T> {
    override fun collectSubExpressions(): List<KslExpression<*>> = collectRecursive()
}
