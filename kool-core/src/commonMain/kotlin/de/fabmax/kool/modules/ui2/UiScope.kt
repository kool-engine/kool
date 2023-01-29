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
    fun AnimationState.progressAndUse(): Float {
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

inline fun <reified T: Any> UiScope.remember(provider: () -> T): T = uiNode.weakMemory.weakMemory(provider)
fun <T: Any> UiScope.remember(initialState: T): MutableStateValue<T> = uiNode.weakMemory.weakMemory { mutableStateOf(initialState) }
fun UiScope.rememberScrollState(): ScrollState = uiNode.weakMemory.weakMemory { ScrollState() }
fun UiScope.rememberListState(): LazyListState = uiNode.weakMemory.weakMemory { LazyListState() }
fun UiScope.clearMemory() = uiNode.weakMemory.clear()

@Deprecated("replaced by remember()", replaceWith = ReplaceWith("remember(provider)"))
inline fun <reified T: Any> UiScope.weakRemember(provider: () -> T): T = remember(provider)
@Deprecated("replaced by remember()", replaceWith = ReplaceWith("remember(initialState)"))
fun <T: Any> UiScope.weakRememberState(initialState: T): MutableStateValue<T> = remember(initialState)
@Deprecated("replaced by rememberScrollState()", replaceWith = ReplaceWith("rememberScrollState()"))
fun UiScope.weakRememberScrollState(): ScrollState = rememberScrollState()
@Deprecated("replaced by rememberListState()", replaceWith = ReplaceWith("rememberListState()"))
fun UiScope.weakRememberListState(): LazyListState = rememberListState()
