package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import kotlin.math.abs

interface ToggleScope : UiScope {
    override val modifier: ToggleModifier
}

open class ToggleModifier(surface: UiSurface) : UiModifier(surface) {
    var toggleState: Boolean by property(false)
    var onToggle: ((Boolean) -> Unit)? by property(null)

    // default color value are overridden by theme colors
    var foregroundColor: Color by property { it.colors.secondary }
    var backgroundColor: Color by property { it.colors.secondaryVariant.withAlpha(0.5f) }
    var borderColor: Color by property { it.colors.secondaryVariant }
}

fun <T: ToggleModifier> T.toggleState(value: Boolean): T { toggleState = value; return this }
fun <T: ToggleModifier> T.onToggle(block: ((Boolean) -> Unit)?): T { onToggle = block; return this }

fun <T: ToggleModifier> T.colors(
    foregroundColor: Color = this.foregroundColor,
    backgroundColor: Color = this.backgroundColor,
    borderColor: Color = this.borderColor
): T {
    this.foregroundColor = foregroundColor
    this.backgroundColor = backgroundColor
    this.borderColor = borderColor
    return this
}

inline fun UiScope.Checkbox(state: Boolean? = null, block: ToggleScope.() -> Unit): ToggleScope {
    val checkbox = uiNode.createChild(CheckboxNode::class, CheckboxNode.factory)
    state?.let { checkbox.modifier.toggleState(it) }
    checkbox.block()
    return checkbox
}

inline fun UiScope.RadioButton(state: Boolean? = null, block: ToggleScope.() -> Unit): ToggleScope {
    val radioButton = uiNode.createChild(RadioButtonNode::class, RadioButtonNode.factory)
    state?.let { radioButton.modifier.toggleState(it) }
    radioButton.block()
    return radioButton
}

inline fun UiScope.Switch(state: Boolean? = null, block: ToggleScope.() -> Unit): ToggleScope {
    val switch = uiNode.createChild(SwitchNode::class, SwitchNode.factory)
    state?.let { switch.modifier.toggleState(it) }
    switch.block()
    return switch
}

abstract class ToggleNode(
    parent: UiNode?,
    surface: UiSurface,
    protected val buttonWidth: Dp,
    protected val buttonHeight: Dp
) : UiNode(parent, surface), ToggleScope, Clickable {

    override val modifier = ToggleModifier(surface)
    protected val toggleAnimator = AnimationState(0.1f)

    protected fun animationPos(): Float {
        val ax = if (toggleAnimator.isActive) toggleAnimator.progressAndUse() else 1f
        return if (modifier.toggleState) ax else 1f - ax
    }

    protected fun center() = MutableVec2f( paddingStartPx + innerWidthPx * 0.5f, paddingTopPx + innerHeightPx * 0.5f)

    protected open fun isOnClickTarget(ev: PointerEvent): Boolean {
        val d = center().subtract(ev.position)
        return pxToDp(abs(d.x)) < buttonWidth.value * 0.5f && pxToDp(abs(d.y)) < buttonHeight.value * 0.5f
    }

    override fun measureContentSize(ctx: KoolContext) {
        val measuredWidth = buttonWidth.px + paddingStartPx + paddingEndPx
        val measuredHeight = buttonHeight.px + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)
    }

    override fun resetDefaults() {
        super.resetDefaults()
        modifier
            .onClick(this)
            .margin(8.dp)
            .padding(bottom = 1.dp)
    }

    override fun onClick(ev: PointerEvent) {
        if (isOnClickTarget(ev)) {
            toggleAnimator.start()
            modifier.onToggle?.invoke(!modifier.toggleState)
        } else {
            ev.reject()
        }
    }
}

class CheckboxNode(parent: UiNode?, surface: UiSurface)
    : ToggleNode(parent, surface, surface.sizes.checkboxHeight, surface.sizes.checkboxHeight)
{
    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val r = buttonHeight.px * 0.5f
        val c = center()
        val draw = surface.getUiPrimitives()
        draw.localRoundRect(c.x - r, c.y - r, r * 2f, r * 2f, 4.dp.px, modifier.backgroundColor)
        draw.localRoundRectBorder(c.x - r, c.y - r, r * 2f, r * 2f, 4.dp.px, 2.dp.px, modifier.borderColor)

        val p = animationPos()
        if (p > 0f) {
            surface.getPlainBuilder().configured(modifier.foregroundColor) {
                translate(c.x, c.y, 0f)
                val sz = buttonWidth.px * 0.8f * p
                rotate(45f, Vec3f.Z_AXIS)
                rect {
                    size.set(sz, sz * 0.2f)
                    origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
                }
                rotate(90f, Vec3f.Z_AXIS)
                rect {
                    size.set(sz, sz * 0.2f)
                    origin.set(size.x * -0.5f, size.y * -0.5f, 0f)
                }
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> CheckboxNode = { parent, surface -> CheckboxNode(parent, surface) }
    }
}

class RadioButtonNode(parent: UiNode?, surface: UiSurface)
    : ToggleNode(parent, surface, surface.sizes.radioButtonHeight, surface.sizes.radioButtonHeight)
{

    override fun isOnClickTarget(ev: PointerEvent): Boolean =
        pxToDp(ev.position.distance(center())) < buttonWidth.px * 0.5f

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val r = buttonHeight.px * 0.5f
        val c = center()
        val draw = surface.getUiPrimitives()

        draw.localCircle(c.x, c.y, r, modifier.backgroundColor)
        draw.localCircleBorder(c.x, c.y, r, 2.dp.px, modifier.borderColor)

        val p = animationPos()
        if (p > 0f) {
            draw.localCircle(c.x, c.y, (r - 4.dp.px) * p, modifier.foregroundColor)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> RadioButtonNode = { parent, surface -> RadioButtonNode(parent, surface) }
    }
}

class SwitchNode(parent: UiNode?, surface: UiSurface)
    : ToggleNode(parent, surface, Dp(surface.sizes.switchHeight.value * 2f), surface.sizes.switchHeight)
{

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val c = center()
        val w = buttonWidth.px
        val h = buttonHeight.px
        val tH = buttonHeight.px * 0.75f
        val tW = buttonWidth.px - (h - tH) * 0.5f
        val draw = surface.getUiPrimitives()

        draw.localRoundRect(c.x - tW * 0.5f, c.y - tH * 0.5f, tW, tH, tH * 0.5f, modifier.backgroundColor)

        val extent = w - h
        val knobX = c.x - extent * 0.5f + extent * animationPos()
        draw.localCircle(knobX, c.y, h * 0.5f, modifier.foregroundColor)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> SwitchNode = { parent, surface -> SwitchNode(parent, surface) }
    }
}