package de.fabmax.kool.modules.ksl.lang

class KslDeclareValue(val declareValue: KslValue<*>, var initExpression: KslExpression<*>?, parentScope: KslScopeBuilder) : KslStatement("declareVar", parentScope) {
    init {
        parentScope.definedStates += declareValue
        initExpression?.let { addExpressionDependencies(it) }
    }

    override fun updateModel() {
        dependencies.clear()
        initExpression?.let { addExpressionDependencies(it) }
        super.updateModel()
    }
}