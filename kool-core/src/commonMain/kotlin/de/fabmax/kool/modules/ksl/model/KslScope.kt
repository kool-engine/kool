package de.fabmax.kool.modules.ksl.model

import kotlin.math.max
import kotlin.math.min

open class KslScope(val parentOp: KslOp?) {

    val dependencies = mutableMapOf<KslState, KslMutatedState>()
    val mutations = mutableMapOf<KslState, KslStateMutation>()

    val definedStates = mutableSetOf<KslState>()
    val ops = mutableListOf<KslOp>()

    var scopeName = parentOp?.opName ?: "unnamed"

    fun isEmpty(): Boolean = ops.isEmpty()
    fun isNotEmpty(): Boolean = ops.isNotEmpty()

    fun dependsOn(state: KslState): Boolean = dependencies.containsKey(state)
    fun mutates(state: KslState): Boolean = mutations.containsKey(state)

    fun updateModel() {
        ops.forEach { it.updateModel() }
        updateDependenciesAndMutations()
    }

    private fun updateDependenciesAndMutations() {
        val startStates = mutableMapOf<KslState, Int>()
        val endStates = mutableMapOf<KslState, Int>()

        parentOp?.let { parent ->
            parent.dependencies.values.forEach { startStates[it.state] = it.mutation }
            parent.mutations.values.forEach { endStates[it.state] = it.toMutation }
        }

        ops.forEach { op ->
            op.dependencies.values.filter { it.state !in definedStates }.forEach { extDep ->
                val start = min(startStates.getOrElse(extDep.state) { extDep.mutation }, extDep.mutation)
                startStates[extDep.state] = start
            }
            op.mutations.values.filter { it.state !in definedStates }.forEach { extMut ->
                val end = max(endStates.getOrElse(extMut.state) { extMut.toMutation }, extMut.toMutation)
                endStates[extMut.state] = end
            }
        }

        dependencies.clear()
        mutations.clear()
        startStates.forEach { (state, startMutation) ->
            val endMutation = endStates[state] ?: startMutation
            dependencies[state] = KslMutatedState(state, startMutation)
            if (startMutation != endMutation) {
                mutations[state] = KslStateMutation(state, startMutation, endMutation)
            }
            parentOp?.let { parent ->
                parent.dependencies[state] = KslMutatedState(state, startMutation)
                if (startMutation != endMutation) {
                    parent.mutations[state] = KslStateMutation(state, startMutation, endMutation)
                }
            }
        }
    }

    fun toPseudoCode(): String {
        val str = StringBuilder("{   # scope: $scopeName\n")
        str.appendLine("    # depends on:")
        dependencies.values.forEach {
            str.appendLine("    #   $it")
        }
        str.appendLine("    # mutates:")
        mutations.values.forEach {
            str.appendLine("    #   $it")
        }
        str.appendLine()

        definedStates.forEach { state ->
            str.appendLine("    def ${state.toPseudoCode()}")
        }
        if (definedStates.isNotEmpty()) {
            str.appendLine()
        }
        ops.forEach { op ->
            str.appendLine(op.toPseudoCode().prependIndent("    "))
        }
        str.append("}")
        return str.toString()
    }

}