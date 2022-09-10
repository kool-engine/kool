package de.fabmax.kool.modules.ui2

@DslMarker
annotation class UiScopeMarker

@UiScopeMarker
interface UiScope {
    val uiCtx: UiContext
    val uiNode: UiNode
    val modifier: UiModifier

    val Int.dp: Dp
        get() = Dp(this.toFloat())

    val Float.dp: Dp
        get() = Dp(this)

    fun MutableState<*>.use() = use(uiCtx)
}
