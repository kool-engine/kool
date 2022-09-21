package de.fabmax.kool.modules.ui2

import de.fabmax.kool.math.MutableVec2f

class MutableTooltipState(val delay: Double = 1.5) : MutableValueState<Boolean>(false), Hoverable {
    private var enterTime = 0.0
    val pointerPos = MutableVec2f()

    override fun onEnter(ev: PointerEvent) {
        enterTime = ev.ctx.time
    }

    override fun onHover(ev: PointerEvent) {
        if (ev.ctx.time - enterTime > delay) {
            pointerPos.set(ev.pointer.x.toFloat(), ev.pointer.y.toFloat())
            set(true)
        }
    }

    override fun onExit(ev: PointerEvent) {
        set(false)
    }
}

fun UiScope.Tooltip(tooltipState: MutableTooltipState, text: String, yOffset: Dp = (-30).dp, target: UiScope? = this) =
    Tooltip(tooltipState, target) {
        modifier
            .margin(top = pxToDp(tooltipState.pointerPos.y).dp + yOffset)
            .layout(CellLayout)
            .background(UiRenderer { node ->
                node.apply {
                    node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                        .localRoundRect(0f, 0f, widthPx, heightPx, heightPx * 0.5f, colors.background)
                    node.getUiPrimitives(UiSurface.LAYER_BACKGROUND)
                        .localRoundRectBorder(0f, 0f, widthPx, heightPx, heightPx * 0.5f, 2.dp.px, colors.accentVariant.withAlpha(0.5f))
                }
            })
        Text(text) {
            modifier
                .alignY(AlignmentY.Center)
                .padding(horizontal = sizes.largeGap, vertical = sizes.smallGap)
        }
    }

inline fun UiScope.Tooltip(tooltipState: MutableTooltipState, target: UiScope? = this, block: UiScope.() -> Unit) {
    target?.modifier?.hoverListener(tooltipState)
    if (tooltipState.use()) {
        Popup(tooltipState.pointerPos.x, tooltipState.pointerPos.y, block = block)
    }
}