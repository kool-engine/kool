package de.fabmax.kool.modules.ksl.model

import de.fabmax.kool.modules.ksl.lang.KslExpression
import de.fabmax.kool.modules.ksl.lang.KslScopeBuilder
import de.fabmax.kool.util.logE

class KslProcessor {
    private val processorState = KslProcessorState()

    fun process(scope: KslScope) {
        scope.updateModel()
        scope.transform(null, null)

//        if (hierarchy.globalScope.parentStage is KslFragmentStage) {
//            println(hierarchy.printHierarchy())
//        }
    }

    private fun KslScope.transform(parentOp: KslOp?, parentBuilder: KslScopeBuilder?) {
        processorState.enterScope(this)

        val openOps = this.ops.toMutableSet()
        val sortedOps = mutableListOf<KslOp>()
        val transformBuilder = KslScopeBuilder(parentOp ?: this.parentOp, parentBuilder, this.parentStage)
        transformBuilder.dependencies.putAll(dependencies)
        transformBuilder.mutations.putAll(mutations)
        transformBuilder.definedStates.addAll(definedStates)

//        val nonTrivialExpressions = openOps.flatMap { it.usedExpressions }.filter {
//            it !is KslValueExpression<*> &&
//            it !is KslValue<*> &&
//            it !is KslArrayAccessor<*> &&
//            it !is KslMatrixAccessor<*> &&
//            it !is KslVectorAccessor<*> &&
//            it !is KslPort<*>
//        }
//        val duplicates = nonTrivialExpressions.groupBy { it }.filter { it.value.size > 1 }
//        if (duplicates.isNotEmpty()) {
//            println("duplicate expressions: ${duplicates.keys.map { it.toPseudoCode() }}")
//            usedDuplicateExpressions = true
//        }
        val replaceExpressions = emptyMap<KslExpression<*>, KslExpression<*>>()

        while (openOps.isNotEmpty()) {
            val nextOp = selectNextOp(openOps) ?: failScope(this, openOps)
            openOps -= nextOp

            val safeOp = nextOp.transform(transformBuilder, replaceExpressions)
            sortedOps += safeOp
            safeOp.childScopes.forEach { childScope ->
                childScope.transform(safeOp, transformBuilder)
                processorState.applyScope(childScope)
            }
            processorState.applyOp(safeOp)
        }

        this.ops.clear()
        this.ops.addAll(sortedOps)

        processorState.exitScope(this)
    }

    private fun selectNextOp(ops: Set<KslOp>): KslOp? {
        return ops.find { it.areDependenciesMet(processorState) && findPreventingOp(it, ops) == null }
    }

    private fun findPreventingOp(op: KslOp, fromOps: Set<KslOp>): KslOp? {
        return fromOps.find { it != op && op.mutations.values.any { mut -> it.isPreventedByStateMutation(mut) } }
    }

    private fun failScope(scope: KslScope, remainingOps: Set<KslOp>): Nothing {
        logE { "Unable to process scope, ${remainingOps.size} ops remain:" }
        remainingOps.forEach { op ->
            val states = processorState.statesInScope.values.toSet()
            logE {
                "${op.opName} \n" +
                        "        depends on: ${op.stateDependencies.values.joinToString { it.toString() + if (it !in states) " [missing]" else "" }}\n" +
                        "        mutates:    ${op.mutations.values.joinToString { it.toString() }}\n" +
                        "        prevents:   ${findPreventingOp(op, remainingOps) ?: "none"}"
            }
        }
        logE { "Processor state:" }
        processorState.logStateE()
        throw IllegalStateException("Unable to complete scope ${scope.scopeName}")
    }
}