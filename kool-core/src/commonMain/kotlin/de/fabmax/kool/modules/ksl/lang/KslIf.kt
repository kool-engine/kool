package de.fabmax.kool.modules.ksl.lang

class KslIf(val condition: KslExpression<KslBool1>, parentScope: KslScopeBuilder) : KslStatement("if", parentScope) {
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)
    val elseIfs = mutableListOf<Pair<KslExpression<KslBool1>, KslScopeBuilder>>()
    val elseBody = KslScopeBuilder(this, parentScope, parentScope.parentStage).apply { scopeName = "else" }

    init {
        addExpressionDependencies(condition)
        childScopes += body
        childScopes += elseBody
    }

    fun elseIf(condition: KslExpression<KslBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
        addExpressionDependencies(condition)
        val body = KslScopeBuilder(this, parentScopeBuilder, parentScopeBuilder.parentStage).apply {
            scopeName = "elseif"
            block()
        }
        elseIfs += condition to body
        // insert else if block before else
        childScopes.add(childScopes.lastIndex, body)
        return this
    }

    fun `else`(block: KslScopeBuilder.() -> Unit) {
        elseBody.apply(block)
    }

    override fun toPseudoCode(): String {
        val str = StringBuilder(annotatePseudoCode("if (${condition.toPseudoCode()})") + "\n")
        str.append(body.toPseudoCode())
        elseIfs.forEach {
            str.append(" else if (${it.first.toPseudoCode()}) ${it.second.toPseudoCode()}")
        }
        if (elseBody.isNotEmpty()) {
            str.append(" else ${elseBody.toPseudoCode()}")
        }
        return str.toString()
    }
}