package de.fabmax.kool.modules.ksl.lang

class KslIf(val condition: KslExpression<KslTypeBool1>, parentScope: KslScopeBuilder) : KslStatement("if", parentScope) {
    val body = KslScopeBuilder(this, parentScope, parentScope.parentStage)
    val elseIfs = mutableListOf<Pair<KslExpression<KslTypeBool1>, KslScopeBuilder>>()
    val elseBody = KslScopeBuilder(this, parentScope, parentScope.parentStage).apply { scopeName = "else" }

    private val parentBuilder = parentScope

    init {
        addExpression(condition)
        childScopes += body
        childScopes += elseBody
    }

    fun `else if`(condition: KslExpression<KslTypeBool1>, block: KslScopeBuilder.() -> Unit): KslIf {
        addExpression(condition)
        val body = KslScopeBuilder(this, parentBuilder, parentBuilder.parentStage).apply {
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
        val str = StringBuilder("if (${condition.toPseudoCode()}) ${body.toPseudoCode()}")
        elseIfs.forEach {
            str.append(" else if (${it.first.toPseudoCode()}) ${it.second.toPseudoCode()}")
        }
        if (elseBody.isNotEmpty()) {
            str.append(" else ${elseBody.toPseudoCode()}")
        }
        return str.toString()
    }
}