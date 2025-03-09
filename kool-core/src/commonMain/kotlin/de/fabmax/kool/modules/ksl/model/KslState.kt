package de.fabmax.kool.modules.ksl.model

open class KslState(val stateName: String) {

    var mutation = 0
    var isTrackingState = true

    fun depend() = KslMutatedState(this, mutation)

    fun mutate(): KslStateMutation {
        val prevMutation = mutation
        val nextMutation = if (isTrackingState) mutation + 1 else mutation
        mutation = nextMutation
        return KslStateMutation(this, prevMutation, nextMutation)
    }

    open fun toPseudoCode(): String {
        return stateName
    }
}