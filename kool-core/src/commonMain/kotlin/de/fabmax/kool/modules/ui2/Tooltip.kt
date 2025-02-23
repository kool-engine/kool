package de.fabmax.kool.modules.ui2

import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time

class TooltipState(val delay: Double = 1.0) : MutableStateValue<Boolean>(false), Hoverable {
    private var enterTime = 0.0
    val pointerX = mutableStateOf(0f)
    val pointerY = mutableStateOf(0f)

    override fun onEnter(ev: PointerEvent) {
        enterTime = Time.gameTime
    }

    override fun onHover(ev: PointerEvent) {
        if (Time.gameTime - enterTime > delay) {
            pointerX.set(ev.pointer.pos.x)
            pointerY.set(ev.pointer.pos.y)
            set(true)
        }
    }

    override fun onExit(ev: PointerEvent) {
        set(false)
    }
}

fun UiScope.Tooltip(
    text: String,
    yOffset: Dp = (-30).dp,
    target: UiScope? = this,
    tooltipState: TooltipState = remember { TooltipState() },
    backgroundColor: Color = colors.backgroundVariant,
    borderColor: Color? = colors.primaryVariantAlpha(0.5f),
    scopeName: String? = null
) = Tooltip(tooltipState, target, scopeName) {
    modifier
        .margin(top = Dp.fromPx(tooltipState.pointerY.use()) + yOffset)
        .layout(CellLayout)
        .background(UiRenderer { node ->
            node.apply {
                getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                    .localRoundRect(0f, 0f, widthPx, heightPx, heightPx * 0.5f, backgroundColor)
                borderColor?.let {
                    getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                        .localRoundRectBorder(0f, 0f, widthPx, heightPx, heightPx * 0.5f, sizes.borderWidth.px, it)
                }
            }
        })
    Text(text) {
        modifier
            .alignY(AlignmentY.Center)
            .padding(horizontal = sizes.largeGap, vertical = sizes.smallGap)
    }
}

inline fun UiScope.Tooltip(
    tooltipState: TooltipState,
    target: UiScope? = this,
    scopeName: String? = null,
    block: UiScope.() -> Unit
) {
    //contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }

    target?.modifier?.hoverListener(tooltipState)
    if (tooltipState.use()) {
        Popup(tooltipState.pointerX.use(), tooltipState.pointerY.use(), scopeName = scopeName, block = block)
    }
}