package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.util.Color

interface ButtonScope : UiScope {
    override val modifier: ButtonModifier
}

open class ButtonModifier(surface: UiSurface) : TextModifier(surface) {
    var buttonColor: Color by property { it.colors.primaryVariant }
    var buttonHoverColor: Color? by property { it.colors.primary }
    var textHoverColor: Color? by property(null)
    var isClickFeedback: Boolean by property(true)
}

fun <T: ButtonModifier> T.isClickFeedback(value: Boolean): T { this.isClickFeedback = value; return this }

fun <T: ButtonModifier> T.colors(
    buttonColor: Color = this.buttonColor,
    textColor: Color = this.textColor,
    buttonHoverColor: Color? = this.buttonHoverColor,
    textHoverColor: Color? = this.textHoverColor
): T {
    this.buttonColor = buttonColor
    this.textColor = textColor
    this.buttonHoverColor = buttonHoverColor
    this.textHoverColor = textHoverColor
    return this
}

inline fun UiScope.Button(text: String = "", block: ButtonScope.() -> Unit): TextScope {
    val button = uiNode.createChild(ButtonNode::class, ButtonNode.factory)
    button.modifier
        .text(text)
        .colors(textColor = colors.onPrimary)
        .height(sizes.buttonHeight)
        .textAlign(AlignmentX.Center, AlignmentY.Center)
        .margin(8.dp)
        .padding(horizontal = 8.dp, vertical = 4.dp)
        .onClick(button)
        .hoverListener(button)
    button.block()
    return button
}

class ButtonNode(parent: UiNode?, surface: UiSurface) : TextNode(parent, surface), ButtonScope, Clickable, Hoverable {
    override val modifier = ButtonModifier(surface)

    private var isHovered = mutableStateOf(false)
    private val clickAnimator = AnimationState(0.3f)
    private val clickPos = MutableVec2f()

    override fun render(ctx: KoolContext) {
        var bgColor = modifier.buttonColor
        if (isHovered.use()) {
            // overwrite text color with text hover color, so TextNode uses the desired color
            modifier.textHoverColor?.let { modifier.textColor(it) }
            modifier.buttonHoverColor?.let { bgColor = it }
        }
        if (modifier.background == null) {
            // only set default button background if no custom one was configured
            modifier.background(RoundRectBackground(bgColor, 4.dp))
        }

        super.render(ctx)

        if (modifier.isClickFeedback && clickAnimator.isActive) {
            clickAnimator.progress(deltaT)
            val p = clickAnimator.use()
            surface.getUiPrimitives().localCircle(
                clickPos.x, clickPos.y,
                p * 128.dp.px,
                Color.WHITE.withAlpha(0.7f - p * 0.5f)
            )
        }
    }

    override fun onClick(ev: PointerEvent) {
        clickAnimator.start()
        clickPos.set(ev.position)
    }

    override fun onEnter(ev: PointerEvent) {
        isHovered.set(true)
    }

    override fun onExit(ev: PointerEvent) {
        isHovered.set(false)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> ButtonNode = { parent, surface -> ButtonNode(parent, surface) }
    }
}
