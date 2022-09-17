package de.fabmax.kool.modules.ui2

@DslMarker
annotation class UiScopeMarker

@UiScopeMarker
interface UiScope {
    val surface: UiSurface
    val uiNode: UiNode
    val modifier: UiModifier

    val colors: Colors get() = surface.colors
    val deltaT: Float get() = surface.deltaT

    val Int.dp: Dp get() = Dp(this.toFloat())
    val Float.dp: Dp get() = Dp(this)
    val Dp.px: Float get() = value * surface.measuredScale

    fun pxToDp(px: Float) = px / surface.measuredScale

    fun <T: Any?> MutableValueState<T>.use(): T = use(surface)
    fun <T> MutableListState<T>.use(): MutableListState<T> = use(surface)
    fun AnimationState.progressAndUse(): Float {
        progress(deltaT)
        return use(surface)
    }
}
