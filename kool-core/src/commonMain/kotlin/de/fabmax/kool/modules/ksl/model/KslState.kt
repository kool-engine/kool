package de.fabmax.kool.modules.ksl.model

open class KslState(val stateName: String) {

    var mutation = 0

    fun depend() = KslMutatedState(this, mutation)
    fun mutate() = KslStateMutation(this, mutation, ++mutation)

    open fun toPseudoCode(): String {
        return stateName
    }
}