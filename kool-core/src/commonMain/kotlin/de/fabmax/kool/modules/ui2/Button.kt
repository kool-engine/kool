package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

interface ButtonScope : UiScope {
    override val modifier: ButtonModifier
    val isHovered: Boolean
}

open class ButtonModifier(surface: UiSurface) : TextModifier(surface) {
    var buttonColor: Color by property { it.colors.secondaryVariant }
    var buttonHoverColor: Color by property { it.colors.secondary }
    var textHoverColor: Color by property{ it.colors.onSecondary }
    var isClickFeedback: Boolean by property(true)
}

fun <T: ButtonModifier> T.isClickFeedback(value: Boolean): T { this.isClickFeedback = value; return this }

fun <T: ButtonModifier> T.colors(
    buttonColor: Color = this.buttonColor,
    textColor: Color = this.textColor,
    buttonHoverColor: Color = this.buttonHoverColor,
    textHoverColor: Color = this.textHoverColor
): T {
    this.buttonColor = buttonColor
    this.textColor = textColor
    this.buttonHoverColor = buttonHoverColor
    this.textHoverColor = textHoverColor
    return this
}

inline fun UiScope.Button(
    text: String = "",
    scopeName: String? = null,
    block: ButtonScope.() -> Unit
): TextScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val button = uiNode.createChild(scopeName, ButtonNode::class, ButtonNode.factory)
    button.modifier
        .text(text)
        .colors(textColor = colors.onSecondary)
        .textAlign(AlignmentX.Center, AlignmentY.Center)
        .padding(horizontal = sizes.gap, vertical = sizes.smallGap)
        .onClick(button)
        .hoverListener(button)
    button.block()
    return button
}

class ButtonNode(parent: UiNode?, surface: UiSurface) : TextNode(parent, surface), ButtonScope, Clickable, Hoverable {
    override val modifier = ButtonModifier(surface)
    override val isHovered: Boolean get() = isHoveredState.value

    private var isHoveredState = mutableStateOf(false)
    private val clickAnimator = AnimatedFloat(0.3f)
    private val clickPos = MutableVec2f()

    override fun render(ctx: KoolContext) {
        var bgColor = modifier.buttonColor
        if (isHoveredState.use()) {
            // overwrite text color with text hover color, so TextNode uses the desired color
            modifier.textColor(modifier.textHoverColor)
            bgColor = modifier.buttonHoverColor
        }
        if (modifier.background == null) {
            // only set default button background if no custom one was configured
            modifier.background(RoundRectBackground(bgColor, sizes.smallGap))
        }

        super.render(ctx)

        if (modifier.isClickFeedback) {
            val p = clickAnimator.use()
            if (clickAnimator.isActive) {
                clickAnimator.progress(Time.deltaT)
                getUiPrimitives().localCircle(
                    clickPos.x, clickPos.y,
                    p * 128.dp.px,
                    Color.WHITE.withAlpha(0.7f - p * 0.5f)
                )
            }
        }
    }

    override fun onClick(ev: PointerEvent) {
        clickAnimator.start()
        clickPos.set(ev.position)
    }

    override fun onEnter(ev: PointerEvent) {
        isHoveredState.set(true)
    }

    override fun onExit(ev: PointerEvent) {
        isHoveredState.set(false)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ButtonNode = { parent, surface -> ButtonNode(parent, surface) }
    }
}
