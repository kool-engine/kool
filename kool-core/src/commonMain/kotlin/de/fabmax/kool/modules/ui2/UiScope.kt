package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Time
import kotlin.reflect.KProperty

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

    fun <T: Any> AnimatedState<T>.progressAndUse(): T {
        progress(Time.deltaT)
        return use(surface)
    }

    operator fun Composable.invoke() {
        compose()
    }

    operator fun <T: Any?> MutableStateValue<T>.getValue(thisRef: Any?, property: KProperty<*>): T = use(surface)
    operator fun <T: Any?> MutableStateValue<T>.setValue(thisRef: Any?, property: KProperty<*>, value: T) = set(value)

    operator fun <T: Any?> MutableStateList<T>.getValue(thisRef: Any?, property: KProperty<*>): MutableStateList<T> = use(surface)
}

inline fun UiScope.Popup(
    screenPxX: Float,
    screenPxY: Float,
    width: Dimension = FitContent,
    height: Dimension = FitContent,
    layout: Layout = ColumnLayout,
    scopeName: String? = null,
    block: UiScope.() -> Unit
): UiScope {
    return surface.popup(scopeName).apply {
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

inline fun <reified T: Any> UiScope.remember(provider: () -> T): T = uiNode.weakMemory.weakMemory(provider)
fun <T: Any?> UiScope.remember(initialState: T): MutableStateValue<T> = remember { mutableStateOf(initialState) }
fun <T: Any?> UiScope.remember(initialState: T, onChange: (T, T) -> Unit) = remember { mutableStateOf(initialState).onChange(onChange) }
fun UiScope.rememberScrollState(): ScrollState = remember { ScrollState() }
fun UiScope.rememberListState(): LazyListState = remember { LazyListState() }