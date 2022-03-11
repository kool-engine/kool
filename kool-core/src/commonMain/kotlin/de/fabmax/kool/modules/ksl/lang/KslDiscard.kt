package de.fabmax.kool.modules.ksl.lang

class KslDiscard(parentScope: KslScopeBuilder) : KslStatement("break", parentScope) {
    init {
        check(parentScope.parentStage is KslFragmentStage) { "discard can only be used in fragment stage" }
    }
}