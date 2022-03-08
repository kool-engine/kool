package de.fabmax.kool.modules.ksl.model

class KslStateMutation(val state: KslState, val fromMutation: Int, val toMutation: Int) {

    override fun toString(): String {
        return "${state.stateName}@$fromMutation->$toMutation"
    }

}