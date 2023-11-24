package de.fabmax.kool.modules.ksl.model

open class KslOp(val opName: String, val parentScope: KslScope) {
    val dependencies = mutableMapOf<KslState, KslMutatedState>()
    val mutations = mutableMapOf<KslState, KslStateMutation>()

    var childScopes = mutableListOf<KslScope>()

    fun areDependenciesMet(processorState: KslProcessorState): Boolean {
        return dependencies.values.all { processorState.hasState(it) }
    }

    fun isPreventedByStateMutation(mutation: KslStateMutation): Boolean {
        return dependencies[mutation.state]?.let { mutation.toMutation > it.mutation } ?: false
    }

    fun addDependency(dep: KslMutatedState) {
        dependencies[dep.state] = dep
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
        return "depends on: [${if (dependencies.isNotEmpty()) dependencies.values.joinToString { it.toString() } else "none"}]; " +
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
        if (mutations.keys.any { it !in dependencies.keys }) {
            throw IllegalStateException("Op $opName mutates state it does not depend on: ${toPseudoCode()} // ${dependenciesAndMutationsToString()}")
        }
        mutations.values.find { mut -> dependencies[mut.state]?.let { it.mutation != mut.fromMutation } == true }?.let {
            throw IllegalStateException("Op $opName mutation and dependency states do not match: ${toPseudoCode()} // ${dependenciesAndMutationsToString()}")
        }
    }

    open fun updateModel() {
        childScopes.forEach { it.updateModel() }
        validate()
    }
}