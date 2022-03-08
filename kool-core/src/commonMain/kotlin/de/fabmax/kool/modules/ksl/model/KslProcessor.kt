package de.fabmax.kool.modules.ksl.model

import de.fabmax.kool.util.logE

class KslProcessor {
    private val processorState = KslProcessorState()

    fun process(hierarchy: KslHierarchy) {
        hierarchy.globalScope.updateModel()
        processScope(hierarchy.globalScope)
    }

    private fun processScope(scope: KslScope) {
        processorState.enterScope(scope)
        val openOps = mutableSetOf<KslOp>().also { it += scope.ops }
        scope.ops.clear()

        while (openOps.isNotEmpty()) {
            val nextOp = selectNextOp(openOps) ?: failScope(scope, openOps)
            openOps -= nextOp

            // reinsert op in correct / safe order
            scope.ops += nextOp
            nextOp.childScopes.forEach { childScope ->
                processScope(childScope)
                processorState.applyScope(childScope)
            }
            processorState.applyOp(nextOp)
        }

        processorState.exitScope(scope)
    }

    private fun selectNextOp(ops: Set<KslOp>): KslOp? {
        val candidates = ops.filter { it.areDependenciesMet(processorState) }
        for (c in candidates) {
            if (findPreventingOp(c, ops) == null) {
                return c
            }
        }
        return null
    }

    private fun findPreventingOp(op: KslOp, fromOps: Set<KslOp>): KslOp? {
        return fromOps.find { it != op && op.mutations.values.any { mut -> it.isPreventedByStateMutation(mut) } }
    }

    private fun failScope(scope: KslScope, remainingOps: Set<KslOp>): Nothing {
        logE { "Unable to process scope, ${remainingOps.size} ops remain:" }
        remainingOps.forEach {
            logE {
                "${it.opName} \n" +
                        "        depends on: ${it.dependencies.values.joinToString { it.toString() }}\n" +
                        "        mutates:    ${it.mutations.values.joinToString { it.toString() }}\n" +
                        "        prevents:   ${findPreventingOp(it, remainingOps) ?: "none"}"
            }
        }
        logE { "Processor state:" }
        processorState.logStateE()
        throw IllegalStateException("Unable to complete scope ${scope.scopeName}")
    }
}