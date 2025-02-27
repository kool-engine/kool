package de.fabmax.kool.modules.ksl.lang

class KslLoopContinue(parentScope: KslScopeBuilder) : KslStatement("continue", parentScope) {
    init {
        check(parentScope.isInLoop) { "continue can only be used within a loop" }
    }
}