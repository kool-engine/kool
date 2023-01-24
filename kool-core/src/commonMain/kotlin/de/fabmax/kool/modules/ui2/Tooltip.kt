package de.fabmax.kool.modules.ui2

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
            pointerX.set(ev.pointer.x.toFloat())
            pointerY.set(ev.pointer.y.toFloat())
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
    tooltipState: TooltipState = remember { TooltipState() }
) = Tooltip(tooltipState, target) {
    modifier
        .margin(top = Dp.fromPx(tooltipState.pointerY.use()) + yOffset)
        .layout(CellLayout)
        .background(UiRenderer { node ->
            node.apply {
                node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                    .localRoundRect(0f, 0f, widthPx, heightPx, heightPx * 0.5f, colors.backgroundVariant)
                node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                    .localRoundRectBorder(0f, 0f, widthPx, heightPx, heightPx * 0.5f, sizes.borderWidth.px, colors.primaryVariantAlpha(0.5f))
            }
        })
    Text(text) {
        modifier
            .alignY(AlignmentY.Center)
            .padding(horizontal = sizes.largeGap, vertical = sizes.smallGap)
    }
}

inline fun UiScope.Tooltip(tooltipState: TooltipState, target: UiScope? = this, block: UiScope.() -> Unit) {
    target?.modifier?.hoverListener(tooltipState)
    if (tooltipState.use()) {
        Popup(tooltipState.pointerX.use(), tooltipState.pointerY.use(), block = block)
    }
}