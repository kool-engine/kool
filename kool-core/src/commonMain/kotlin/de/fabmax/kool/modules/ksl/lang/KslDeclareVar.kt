package de.fabmax.kool.modules.ksl.lang

import de.fabmax.kool.modules.ksl.model.KslMutatedState

class KslDeclareVar(val declareVar: KslVar<*>, initExpression: KslExpression<*>?, parentScope: KslScopeBuilder) : KslStatement("declareVar", parentScope) {

    var initExpression = initExpression
        private set

    init {
        parentScope.definedStates += declareVar
        initExpression?.let { addExpressionDependencies(it) }
        addMutation(declareVar.mutatingState.mutate())
    }

    override fun toPseudoCode(): String {
        return "declare(${declareVar.stateName}) = ${initExpression?.toPseudoCode()} // ${dependenciesAndMutationsToString()}"
    }

    fun changeInitExpression(newInitExpression: KslExpression<*>?) {
        dependencies.clear()
        val mut = mutations[declareVar]!!
        addDependency(KslMutatedState(mut.state, mut.fromMutation))
        initExpression = newInitExpression
        initExpression?.let { addExpressionDependencies(it) }
    }
}