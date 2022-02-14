package de.fabmax.kool.modules.ksl.model

class KslMutatedState(val state: KslState, val mutation: Int) {

    override fun toString(): String {
        return "${state.stateName}@$mutation"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as KslMutatedState

        if (state != other.state) return false
        if (mutation != other.mutation) return false

        return true
    }

    override fun hashCode(): Int {
        var result = state.hashCode()
        result = 31 * result + mutation
        return result
    }
}