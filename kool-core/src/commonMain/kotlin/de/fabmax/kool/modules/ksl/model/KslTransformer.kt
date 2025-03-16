package de.fabmax.kool.modules.ksl.model

import de.fabmax.kool.modules.ksl.lang.*
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logT
import de.fabmax.kool.util.logW

class KslTransformer private constructor(val stage: KslShaderStage) {
    private val transformerState = KslTransformerState()

    private val transformedExpressions = mutableMapOf<KslExpression<*>, KslExpression<*>>()

    private fun transform(optimizeExpressions: Boolean) {
        try {
            stage.globalScope.updateModel()
            sortFunctions(stage)
            if (optimizeExpressions) {
                transformExpressions(stage.globalScope)
            }
            stage.globalScope.transform(null, null)
        } catch (e: Exception) {
            logE { stage.globalScope.toPseudoCode() }
            throw RuntimeException("Failed transforming ksl scope (${stage.program.name}:${stage.type.pipelineStageType})", e)
        }
    }

    private fun sortFunctions(stage: KslShaderStage) {
        val functionOps = stage.globalScope.ops.filterIsInstance<KslFunction<*>.FunctionRoot>()
        val closed = mutableSetOf<KslFunction<*>>()
        val open = functionOps.map { it.function }.toMutableSet()
        stage.globalScope.ops -= functionOps

        while (open.isNotEmpty()) {
            val next = open.find { it.functionDependencies.all { dep -> dep in closed } }
            if (next == null) {
                logE {
                    buildString {
                        appendLine("Unable to sort functions. Circular dependencies?")
                        appendLine("  Done:")
                        closed.forEach { appendLine("    ${it.name}") }
                        appendLine("  Open:")
                        open.forEach { f ->
                            appendLine("    ${f.name}, dependencies: ${f.functionDependencies.map { it.name }}")
                        }
                    }
                }
                error("Unable to sort functions, circular dependencies?")
            }
            open -= next
            closed += next
            stage.globalScope.ops += next.functionRoot
        }
    }

    private fun transformExpressions(rootScope: KslScope) {
        val expressionUsers = mutableMapOf<KslExpression<*>, MutableList<KslOp>>()

        fun KslScope.traverse() {
            ops.forEach { op ->
                op.usedExpressions
                    .filter { it.isCacheable() }
                    .filterMultiUsages()
                    .forEach { expressionUsers.getOrPut(it) { mutableListOf() }.add(op) }
                op.childScopes.forEach { it.traverse() }
            }
        }
        rootScope.traverse()
        expressionUsers.keys.removeAll { expressionUsers[it]!!.size == 1 }

        expressionUsers.forEach { (expr, ops) ->
            val commonScope = ops.findGreatestCommonScope() as KslScopeBuilder?
            if (commonScope != null) {
                commonScope.injectExpression(expr)?.let { replacement ->
                    logT { "inject cached expression: ${replacement.toPseudoCode()} = ${expr.toPseudoCode()} (used by ${ops.size} ops in ${stage.program.name})" }
                    transformedExpressions[expr] = replacement
                    val dependency = replacement.depend()
                    ops.distinct().forEach {
                        it.addDependency(dependency)
                        it.parentScope.addDependencyUpTo(dependency, commonScope)
                    }
                }
            } else {
                logW { "null scope for expression ${expr.toPseudoCode()}" }
            }
        }
    }

    private fun List<KslExpression<*>>.filterMultiUsages(): List<KslExpression<*>> {
        // check for expressions occurring multiple times, remove multiple occurrences caused by parent expressions
        // occurring multiple times
        val multis = groupBy { it }.filter { (_, occs) -> occs.size > 1 }
        val multiSubTrees = multis.keys.associateWith { it.collectSubExpressions().toSet() - it }
        val multiWhitelist = mutableSetOf<KslExpression<*>>()
        multis.forEach { (expr, occs) ->
            val parentExpressionsContainingExpr = multiSubTrees.keys.filter { multiSubTrees[it]!!.contains(expr) }
            val expectedFreq = parentExpressionsContainingExpr.sumOf { multis[it]!!.size }
            if (occs.size <= expectedFreq) {
                multiWhitelist += expr
            }
        }
        // make whitelisted expressions distinct
        return (this - multiWhitelist) + multiWhitelist
    }

    @Suppress("UNCHECKED_CAST")
    private fun KslScopeBuilder.injectExpression(expr: KslExpression<*>): KslVar<*>? {
        val replacement: KslVar<*>? = when (expr.expressionType) {
            KslBool1 -> bool1Var(KslInjectedExpression(expr as KslExprBool1))
            KslBool2 -> bool2Var(KslInjectedExpression(expr as KslExprBool2))
            KslBool3 -> bool3Var(KslInjectedExpression(expr as KslExprBool3))
            KslBool4 -> bool4Var(KslInjectedExpression(expr as KslExprBool4))
            KslFloat1 -> float1Var(KslInjectedExpression(expr as KslExprFloat1))
            KslFloat2 -> float2Var(KslInjectedExpression(expr as KslExprFloat2))
            KslFloat3 -> float3Var(KslInjectedExpression(expr as KslExprFloat3))
            KslFloat4 -> float4Var(KslInjectedExpression(expr as KslExprFloat4))
            KslInt1 -> int1Var(KslInjectedExpression(expr as KslExprInt1))
            KslInt2 -> int2Var(KslInjectedExpression(expr as KslExprInt2))
            KslInt3 -> int3Var(KslInjectedExpression(expr as KslExprInt3))
            KslInt4 -> int4Var(KslInjectedExpression(expr as KslExprInt4))
            KslUint1 -> uint1Var(KslInjectedExpression(expr as KslExprUint1))
            KslUint2 -> uint2Var(KslInjectedExpression(expr as KslExprUint2))
            KslUint3 -> uint3Var(KslInjectedExpression(expr as KslExprUint3))
            KslUint4 -> uint4Var(KslInjectedExpression(expr as KslExprUint4))
            KslMat2 -> mat2Var(KslInjectedExpression(expr as KslExprMat2))
            KslMat3 -> mat3Var(KslInjectedExpression(expr as KslExprMat3))
            KslMat4 -> mat4Var(KslInjectedExpression(expr as KslExprMat4))
            else -> null
        }
        if (replacement == null) {
            logW { "Failed injecting expression of type ${expr.expressionType}: ${expr.toPseudoCode()}" }
            return null
        }
        return replacement
    }

    private fun List<KslOp>.findGreatestCommonScope(): KslScope? {
        var greatest = first().parentScope
        for (i in 1 until size) {
            if (get(i).parentScope.isAncestorOf(greatest)) {
                greatest = get(i).parentScope
            }
        }
        return greatest
    }

    private fun KslScope.addDependencyUpTo(dep: KslMutatedState, term: KslScope) {
        if (this == term) {
            return
        }
        addDependency(dep)
        parentScope?.addDependencyUpTo(dep, term)
    }

    private fun KslExpression<*>.isCacheable(): Boolean {
        return this !is KslValueExpression<*> &&
               this !is KslValue<*> &&
               this !is KslVectorAccessor<*> &&
               this !is KslArrayAccessor<*> &&
               this !is KslMatrixAccessor<*> &&
               this !is KslVertexAttribute<*> &&
               this !is KslStageInput<*> &&
               this !is KslStageOutput<*> &&
               this !is KslUniform<*> &&
               this !is KslBlock.BlockInput<*, *> &&
               this !is KslNumericVectorUnaryMinus<*, *> &&
               this !is KslNumericScalarUnaryMinus<*> //&&
               //this !is KslStructMemberExpression<*>
    }

    private fun KslScope.transform(parentOp: KslOp?, parentBuilder: KslScopeBuilder?) {
        transformerState.enterScope(this)

        val openOps = this.ops.toMutableSet()
        val sortedOps = mutableListOf<KslOp>()
        val transformBuilder = KslScopeBuilder(parentOp ?: this.parentOp, parentBuilder, this.parentStage)
        transformBuilder.dependencies.putAll(dependencies)
        transformBuilder.mutations.putAll(mutations)
        transformBuilder.definedStates.addAll(definedStates)

        while (openOps.isNotEmpty()) {
            val nextOp = selectNextOp(openOps) ?: failScope(this, openOps)
            openOps -= nextOp

            sortedOps += nextOp
            nextOp.childScopes.forEach { childScope ->
                childScope.transform(nextOp, transformBuilder)
                transformerState.applyScope(childScope)
            }
            transformerState.applyOp(nextOp)
        }

        this.ops.clear()
        this.ops.addAll(sortedOps)

        transformerState.exitScope(this)
    }

    private fun selectNextOp(ops: Set<KslOp>): KslOp? {
        return ops.find { it.areDependenciesMet(transformerState) && findPreventingOp(it, ops) == null }
    }

    private fun findPreventingOp(op: KslOp, fromOps: Set<KslOp>): KslOp? {
        return fromOps.find { it != op && op.mutations.values.any { mut -> it.isPreventedByStateMutation(mut) } }
    }

    private fun failScope(scope: KslScope, remainingOps: Set<KslOp>): Nothing {
        logE { "Unable to process scope, ${remainingOps.size} ops remain:" }
        remainingOps.forEach { op ->
            val states = transformerState.statesInScope.values.toSet()
            logE {
                "${op.opName} \n" +
                        "        depends on: ${op.stateDependencies.values.joinToString { it.toString() + if (it !in states) " [missing]" else "" }}\n" +
                        "        mutates:    ${op.mutations.values.joinToString { it.toString() }}\n" +
                        "        prevents:   ${findPreventingOp(op, remainingOps) ?: "none"}"
            }
        }
        logE { "Processor state:" }
        transformerState.logStateE()
        throw IllegalStateException("Unable to complete scope ${scope.scopeName}")
    }

    companion object {
        fun transform(shaderStage: KslShaderStage, optimizeExpressions: Boolean): TransformOutput {
            val transformer = KslTransformer(shaderStage)
            transformer.transform(optimizeExpressions)
            return TransformOutput(transformer.transformedExpressions)
        }
    }
}

class TransformOutput(val generatorExpressions: Map<KslExpression<*>, KslExpression<*>>)
