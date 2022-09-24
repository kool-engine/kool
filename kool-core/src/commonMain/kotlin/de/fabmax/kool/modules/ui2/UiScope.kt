package de.fabmax.kool.modules.ui2

@DslMarker
annotation class UiScopeMarker

@UiScopeMarker
interface UiScope {
    val surface: UiSurface
    val uiNode: UiNode
    val modifier: UiModifier

    val colors: Colors get() = surface.colors
    val sizes: Sizes get() = surface.sizes
    val deltaT: Float get() = surface.deltaT

    val Int.dp: Dp get() = Dp(this.toFloat())
    val Float.dp: Dp get() = Dp(this)
    val Dp.px: Float get() = value * UiScale.measuredScale

    fun pxToDp(px: Float) = px / UiScale.measuredScale

    fun <T: Any?> MutableValueState<T>.use(): T = use(surface)
    fun <T> MutableListState<T>.use(): MutableListState<T> = use(surface)
    fun AnimationState.progressAndUse(): Float {
        progress(deltaT)
        return use(surface)
    }

    operator fun ComposableComponent.invoke() {
        compose()
    }
}

inline fun UiScope.Popup(
    screenPxX: Float,
    screenPxY: Float,
    width: Dimension = WrapContent,
    height: Dimension = WrapContent,
    layout: Layout = ColumnLayout,
    block: UiScope.() -> Unit
): UiScope {
    return surface.popup().apply {
        modifier
            .margin(start = pxToDp(screenPxX).dp, top = pxToDp(screenPxY).dp)
            .width(width)
            .height(height)
            .backgroundColor(colors.background)
            .zLayer(UiSurface.LAYER_POPUP)
            .layout(layout)
        block()
    }
}
