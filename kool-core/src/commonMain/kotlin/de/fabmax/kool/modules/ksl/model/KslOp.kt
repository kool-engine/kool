package de.fabmax.kool.modules.ksl.model

import de.fabmax.kool.modules.ksl.lang.KslExpression

abstract class KslOp(val opName: String, val parentScope: KslScope) {
    val usedExpressions = mutableListOf<KslExpression<*>>()
    val stateDependencies = mutableMapOf<KslState, KslMutatedState>()
    val mutations = mutableMapOf<KslState, KslStateMutation>()

    var childScopes = mutableListOf<KslScope>()

    fun areDependenciesMet(processorState: KslProcessorState): Boolean {
        return stateDependencies.values.all { processorState.hasState(it) }
    }

    fun isPreventedByStateMutation(mutation: KslStateMutation): Boolean {
        return stateDependencies[mutation.state]?.let { mutation.toMutation > it.mutation } == true
    }

    fun addDependency(dep: KslMutatedState) {
        stateDependencies[dep.state] = dep
    }

    fun addMutation(mut: KslStateMutation) {
        addDependency(KslMutatedState(mut.state, mut.fromMutation))
        mutations[mut.state] = mut
    }

    open fun toPseudoCode(): String {
        val str = StringBuilder(annotatePseudoCode(opName))
        childScopes.forEach {
            str.append("\n${it.toPseudoCode()}")
        }
        return str.toString()
    }

    protected fun dependenciesAndMutationsToString(): String {
        return "depends on: [${if (stateDependencies.isNotEmpty()) stateDependencies.values.joinToString { it.toString() } else "none"}]; " +
                "mutates: [${if (mutations.isNotEmpty()) mutations.values.joinToString { it.toString() } else "none"}]; "
    }

    protected fun annotatePseudoCode(pseudoCode: String): String {
        val annotated = StringBuilder(pseudoCode)
        while (annotated.length < 80) {
            annotated.append(" ")
        }
        annotated.append("# " + dependenciesAndMutationsToString())
        return annotated.toString()
    }

    open fun validate() {
        if (mutations.keys.any { it !in stateDependencies.keys }) {
            throw IllegalStateException("Op $opName mutates state it does not depend on: ${toPseudoCode()} // ${dependenciesAndMutationsToString()}")
        }
        mutations.values.find { mut -> stateDependencies[mut.state]?.let { it.mutation != mut.fromMutation } == true }?.let {
            throw IllegalStateException("Op $opName mutation and dependency states do not match: ${toPseudoCode()} // ${dependenciesAndMutationsToString()}")
        }
    }

    open fun updateModel() {
        childScopes.forEach { it.updateModel() }
        validate()
    }
}