package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Time

interface UiScope {
    val surface: UiSurface
    val uiNode: UiNode
    val modifier: UiModifier

    val colors: Colors get() = surface.colors
    val sizes: Sizes get() = surface.sizes

    val Int.dp: Dp get() = Dp(this.toFloat())
    val Float.dp: Dp get() = Dp(this)

    fun <T: Any?> MutableStateValue<T>.use(): T = use(surface)
    fun <T> MutableStateList<T>.use(): MutableStateList<T> = use(surface)
    fun AnimationState.progressAndUse(): Float {
        progress(Time.deltaT)
        return use(surface)
    }

    operator fun Composable.invoke() {
        compose()
    }
}

inline fun UiScope.Popup(
    screenPxX: Float,
    screenPxY: Float,
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    layout: Layout = ColumnLayout,
    block: UiScope.() -> Unit
): UiScope {
    return surface.popup().apply {
        modifier
            .margin(start = Dp.fromPx(screenPxX), top = Dp.fromPx(screenPxY))
            .width(width)
            .height(height)
            .backgroundColor(colors.background)
            .zLayer(UiSurface.LAYER_POPUP)
            .layout(layout)
        block()
    }
}

inline fun <reified T: Any> UiScope.weakRemember(provider: () -> T): T = uiNode.weakMemory.weakMemory(provider)
fun <T: Any> UiScope.weakRememberState(initialState: T): MutableStateValue<T> = uiNode.weakMemory.weakMemory { mutableStateOf(initialState) }
fun UiScope.weakRememberScrollState(): ScrollState = uiNode.weakMemory.weakMemory { ScrollState() }
fun UiScope.weakRememberListState(): LazyListState = uiNode.weakMemory.weakMemory { LazyListState() }