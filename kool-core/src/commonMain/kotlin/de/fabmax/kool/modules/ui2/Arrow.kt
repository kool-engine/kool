package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.min

interface ArrowScope : UiScope {
    override val modifier: ArrowModifier
    val isHovered: Boolean

    companion object {
        const val ROTATION_RIGHT = 0f
        const val ROTATION_DOWN = 90f
        const val ROTATION_LEFT = 180f
        const val ROTATION_UP = 270f
    }
}

open class ArrowModifier(surface: UiSurface) : UiModifier(surface) {
    var rotation: Float by property { 0f }
    var arrowColor: Color by property { it.colors.primaryVariant }
    var arrowHoverColor: Color by property { it.colors.primary }
}

fun <T: ArrowModifier> T.rotation(rotation: Float): T { this.rotation = rotation; return this }
fun <T: ArrowModifier> T.colors(
    arrowColor: Color = this.arrowColor,
    arrowHoverColor: Color = this.arrowHoverColor
): T {
    this.arrowColor = arrowColor
    this.arrowHoverColor = arrowHoverColor
    return this
}

inline fun UiScope.Arrow(
    rotation: Float = ArrowScope.ROTATION_RIGHT,
    scopeName: String? = null,
    isHoverable: Boolean = true,
    block: ArrowScope.() -> Unit
): ArrowScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val arrow = uiNode.createChild(scopeName, ArrowNode::class, ArrowNode.factory)
    arrow.modifier.rotation(rotation)
    if (isHoverable) {
        arrow.modifier.hoverListener(arrow)
    }
    arrow.block()
    return arrow
}

class ArrowNode(parent: UiNode?, surface: UiSurface) : UiNode(parent, surface), ArrowScope, Hoverable {
    override val modifier = ArrowModifier(surface)
    override val isHovered: Boolean get() = isHoveredState.value

    private var isHoveredState = mutableStateOf(false)
    private val rotationAnimator = AnimatedFloat(0.1f)
    private var isFirst = true
    private var prevRotation = 0f

    override fun measureContentSize(ctx: KoolContext) {
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else sizes.gap.px + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else sizes.gap.px + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)

        if (isFirst) {
            prevRotation = modifier.rotation
            isFirst = false
        } else if (prevRotation != modifier.rotation && !rotationAnimator.isActive) {
            rotationAnimator.start()
        }
    }

    override fun render(ctx: KoolContext) {
        super.render(ctx)

        val p = rotationAnimator.progressAndUse()
        val rot = modifier.rotation * p + prevRotation * (1f - p)
        val color = if (isHoveredState.use()) modifier.arrowHoverColor else modifier.arrowColor
        getPlainBuilder().configured(color) {
            arrow(widthPx * 0.5f, heightPx * 0.5f, min(innerWidthPx, innerHeightPx), rot)
        }

        if (!rotationAnimator.isActive) {
            prevRotation = modifier.rotation
        }
    }

    override fun onEnter(ev: PointerEvent) {
        isHoveredState.set(true)
    }

    override fun onExit(ev: PointerEvent) {
        isHoveredState.set(false)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ArrowNode = { parent, surface -> ArrowNode(parent, surface) }
    }
}