package de.fabmax.kool.modules.ksl.model

import de.fabmax.kool.util.logE

class KslTransformerState {
    private val stack = mutableListOf<ScopeState>()

    val statesInScope: Map<KslState, KslMutatedState>
        get() = if (stack.isEmpty()) emptyMap() else stack.last()

    val stackTrace: String
        get() = stack.joinToString(" -> ") { it.scope.scopeName }

    fun hasState(mutatedState: KslMutatedState) = statesInScope[mutatedState.state] == mutatedState

    fun enterScope(scope: KslScope) {
        stack += ScopeState(scope, statesInScope)
    }

    fun exitScope(scope: KslScope) {
        if (stack.last().scope != scope) {
            throw IllegalStateException("Unable to leave scope ${scope.scopeName}: Not on top of stack, current stack: $stackTrace")
        }
        stack.removeLast()
    }

    fun applyOp(op: KslOp) = stack.last().applyOp(op)

    fun applyScope(scope: KslScope) = stack.last().applyScope(scope)

    fun logStateE() {
        logE { "stack: $stackTrace" }
        logE { "current state:" }
        statesInScope.values.forEach {
            logE { "  $it" }
        }
    }

    private inner class ScopeState(
        val scope: KslScope,
        initialState: Map<KslState, KslMutatedState>,
        val statesInScope: MutableMap<KslState, KslMutatedState> = mutableMapOf()
    ) : Map<KslState, KslMutatedState> by statesInScope {

        init {
            statesInScope += initialState
            scope.definedStates.forEach { statesInScope[it] = KslMutatedState(it, 0) }
        }

        fun applyScope(scope: KslScope) {
            scope.mutations.values.forEach {
                if (!statesInScope.containsKey(it.state)) {
                    throw IllegalStateException("Unable to apply scope ${scope.scopeName}: State ${it.state.stateName} not in scope. Current stack: $stackTrace")
                }
                statesInScope[it.state] = KslMutatedState(it.state, it.toMutation)
            }
        }

        fun applyOp(op: KslOp) {
            op.mutations.values.forEach {
                if (!statesInScope.containsKey(it.state)) {
                    throw IllegalStateException("Unable to apply op ${op.opName}: State ${it.state.stateName} not in scope. Current stack: $stackTrace")
                }
                statesInScope[it.state] = KslMutatedState(it.state, it.toMutation)
            }
        }
    }
}