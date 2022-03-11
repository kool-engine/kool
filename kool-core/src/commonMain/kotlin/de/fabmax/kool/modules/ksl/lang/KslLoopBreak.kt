package de.fabmax.kool.modules.ksl.lang

class KslLoopBreak(parentScope: KslScopeBuilder) : KslStatement("break", parentScope) {
    init {
        check(parentScope.isInLoop) { "break can only be used within a loop" }
    }
}