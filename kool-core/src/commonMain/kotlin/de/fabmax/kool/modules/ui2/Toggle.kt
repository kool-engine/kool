package de.fabmax.kool.modules.ui2

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.util.Color
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.abs
import kotlin.math.floor

interface CheckboxScope : UiScope {
    override val modifier: CheckboxModifier
}

interface RadioButtonScope : UiScope {
    override val modifier: RadioButtonModifier
}

interface SwitchScope : UiScope {
    override val modifier: SwitchModifier
}

open class ToggleModifier(surface: UiSurface) : UiModifier(surface) {
    var toggleState: Boolean by property(false)
    var onToggle: ((Boolean) -> Unit)? by property(null)
}

open class CheckboxModifier(surface: UiSurface) : ToggleModifier(surface) {
    var checkboxSize: Dp by property { it.sizes.checkboxSize }

    var borderColor: Color by property { it.colors.secondaryVariant }
    var backgroundColor: Color by property { it.colors.secondaryAlpha(0.5f) }
    var fillColor: Color by property { it.colors.primary }
    var checkMarkColor: Color by property { it.colors.onPrimary }
}

open class RadioButtonModifier(surface: UiSurface) : ToggleModifier(surface) {
    var radioButtonSize: Dp by property { it.sizes.radioButtonSize }

    var borderColorOn: Color by property { it.colors.primaryVariant }
    var borderColorOff: Color by property { it.colors.secondaryVariant }
    var backgroundColorOn: Color by property { it.colors.primaryAlpha(0.5f) }
    var backgroundColorOff: Color by property { it.colors.secondaryAlpha(0.3f) }
    var knobColor: Color by property { it.colors.primary }
}

open class SwitchModifier(surface: UiSurface) : ToggleModifier(surface) {
    var switchWidth: Dp by property { it.sizes.switchSize * 2f }
    var switchHeight: Dp by property { it.sizes.switchSize }

    var knobColorOn: Color by property { it.colors.primary }
    var knobColorOff: Color by property { it.colors.primary }
    var trackColorOn: Color by property { it.colors.secondary }
    var trackColorOff: Color by property { it.colors.secondaryVariant }
}


fun <T: ToggleModifier> T.toggleState(value: Boolean): T { toggleState = value; return this }
fun <T: ToggleModifier> T.onToggle(block: ((Boolean) -> Unit)?): T { onToggle = block; return this }

fun <T: CheckboxModifier> T.checkboxSize(size: Dp): T { checkboxSize = size; return this }
fun <T: CheckboxModifier> T.colors(
    borderColor: Color = this.borderColor,
    backgroundColor: Color = this.backgroundColor,
    fillColor: Color = this.fillColor,
    checkMarkColor: Color = this.checkMarkColor
): T {
    this.borderColor = borderColor
    this.backgroundColor = backgroundColor
    this.fillColor = fillColor
    this.checkMarkColor = checkMarkColor
    return this
}

fun <T: RadioButtonModifier> T.radioButtonSize(size: Dp): T { radioButtonSize = size; return this }
fun <T: RadioButtonModifier> T.colors(
    borderColorOn: Color = this.borderColorOn,
    borderColorOff: Color = this.borderColorOff,
    backgroundColorOn: Color = this.backgroundColorOn,
    backgroundColorOff: Color = this.backgroundColorOff,
    knobColor: Color = this.knobColor
): T {
    this.borderColorOff = borderColorOff
    this.borderColorOn = borderColorOn
    this.backgroundColorOn = backgroundColorOn
    this.backgroundColorOff = backgroundColorOff
    this.knobColor = knobColor
    return this
}

fun <T: SwitchModifier> T.switchSize(width: Dp = this.switchWidth, height: Dp = this.switchHeight): T {
    switchWidth = width
    switchHeight = height
    return this
}
fun <T: SwitchModifier> T.colors(
    knobColorOn: Color = this.knobColorOn,
    knobColorOff: Color = this.knobColorOff,
    trackColorOn: Color = this.trackColorOn,
    trackColorOff: Color = this.trackColorOff
): T {
    this.knobColorOn = knobColorOn
    this.knobColorOff = knobColorOff
    this.trackColorOn = trackColorOn
    this.trackColorOff = trackColorOff
    return this
}

inline fun UiScope.Checkbox(
    state: Boolean? = null,
    scopeName: String? = null,
    block: CheckboxScope.() -> Unit
): CheckboxScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val checkbox = uiNode.createChild(scopeName, CheckboxNode::class, CheckboxNode.factory)
    state?.let { checkbox.modifier.toggleState(it) }
    checkbox.block()
    return checkbox
}

inline fun UiScope.RadioButton(
    state: Boolean? = null,
    scopeName: String? = null,
    block: RadioButtonScope.() -> Unit
): RadioButtonScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val radioButton = uiNode.createChild(scopeName, RadioButtonNode::class, RadioButtonNode.factory)
    state?.let { radioButton.modifier.toggleState(it) }
    radioButton.block()
    return radioButton
}

inline fun UiScope.Switch(
    state: Boolean? = null,
    scopeName: String? = null,
    block: SwitchScope.() -> Unit
): SwitchScope {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }

    val switch = uiNode.createChild(scopeName, SwitchNode::class, SwitchNode.factory)
    state?.let { switch.modifier.toggleState(it) }
    switch.block()
    return switch
}

abstract class ToggleNode(
    parent: UiNode?,
    surface: UiSurface
) : UiNode(parent, surface), Clickable {

    abstract override val modifier: ToggleModifier
    abstract val buttonWidth: Dp
    abstract val buttonHeight: Dp

    private val toggleAnimator = AnimatedFloat(0.1f)
    private var isFirst = true
    private var prevState = false

    protected fun animationPos(): Float {
        val ax = if (toggleAnimator.isActive) toggleAnimator.progressAndUse() else 1f
        return if (modifier.toggleState) ax else 1f - ax
    }

    protected fun center() = MutableVec2f( paddingStartPx + innerWidthPx * 0.5f, paddingTopPx + innerHeightPx * 0.5f)

    protected open fun isOnClickTarget(ev: PointerEvent): Boolean {
        val d = center().subtract(ev.position)
        return Dp.fromPx(abs(d.x)) < buttonWidth * 0.5f && Dp.fromPx(abs(d.y)) < buttonHeight * 0.5f
    }

    override fun measureContentSize(ctx: KoolContext) {
        val modWidth = modifier.width
        val modHeight = modifier.height
        val measuredWidth = if (modWidth is Dp) modWidth.px else buttonWidth.px + paddingStartPx + paddingEndPx
        val measuredHeight = if (modHeight is Dp) modHeight.px else buttonHeight.px + paddingTopPx + paddingBottomPx
        setContentSize(measuredWidth, measuredHeight)

        if (isFirst) {
            isFirst = false
        } else if (prevState != modifier.toggleState) {
            toggleAnimator.start()
        }
        prevState = modifier.toggleState
    }

    override fun applyDefaults() {
        super.applyDefaults()
        modifier.onClick(this)
    }

    override fun onClick(ev: PointerEvent) {
        if (isOnClickTarget(ev)) {
            modifier.onToggle?.invoke(!modifier.toggleState)
        } else {
            ev.reject()
        }
    }
}

class CheckboxNode(parent: UiNode?, surface: UiSurface) : ToggleNode(parent, surface), CheckboxScope {
    override val modifier = CheckboxModifier(surface)
    override val buttonWidth: Dp get() = modifier.checkboxSize
    override val buttonHeight: Dp get() = modifier.checkboxSize

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val r = floor(buttonHeight.px * 0.5f)
        val c = center()
        val draw = getUiPrimitives()
        val p = animationPos()

        val bgColor = when (p) {
            0f -> modifier.backgroundColor
            1f -> modifier.fillColor
            else -> modifier.backgroundColor.mix(modifier.fillColor, p)
        }
        draw.localRoundRect(c.x - r, c.y - r, r * 2f, r * 2f, 4.dp.px, bgColor)

        if (p < 1f) {
            draw.localRoundRectBorder(c.x - r, c.y - r, r * 2f, r * 2f, 4.dp.px, sizes.borderWidth.px * 1.5f, modifier.borderColor)
        }

        if (p > 0f) {
            getPlainBuilder().configured(modifier.checkMarkColor) {
                val sz = buttonWidth.px * 0.8f * p
                //scale(0.9f)
                translate(c.x, c.y - sz * 0.2f, 0f)
                rotate(45f.deg, Vec3f.Z_AXIS)
                rect {
                    isCenteredOrigin = false
                    size.set(sz * 0.5f, sz * 0.2f)
                    origin.set(sz * -0.15f, sz * 0.35f, 0f)
                }
                rect {
                    isCenteredOrigin = false
                    size.set(sz * 0.2f, sz * 0.7f)
                    origin.set(sz * 0.15f, sz * -0.35f, 0f)
                }
            }
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> CheckboxNode = { parent, surface -> CheckboxNode(parent, surface) }
    }
}

class RadioButtonNode(parent: UiNode?, surface: UiSurface) : ToggleNode(parent, surface), RadioButtonScope {
    override val modifier = RadioButtonModifier(surface)
    override val buttonWidth: Dp get() = modifier.radioButtonSize
    override val buttonHeight: Dp get() = modifier.radioButtonSize

    override fun isOnClickTarget(ev: PointerEvent): Boolean =
        Dp.fromPx(ev.position.distance(center())) < modifier.radioButtonSize * 0.5f

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val r = floor(buttonHeight.px * 0.5f)
        val c = center()
        val draw = getUiPrimitives()

        val p = animationPos()
        if (p > 0f) {
            val bgColor: Color
            val borderColor: Color
            if (p < 1f) {
                bgColor = modifier.backgroundColorOff.mix(modifier.backgroundColorOn, p)
                borderColor = modifier.borderColorOff.mix(modifier.borderColorOn, p)
            } else {
                bgColor = modifier.backgroundColorOn
                borderColor = modifier.borderColorOn
            }

            draw.localCircle(c.x, c.y, r, bgColor)
            draw.localCircleBorder(c.x, c.y, r, sizes.borderWidth.px * 1.5f, borderColor)
            draw.localCircle(c.x, c.y, (r - sizes.borderWidth.px * 3f) * p, modifier.knobColor)

        } else {
            draw.localCircle(c.x, c.y, r, modifier.backgroundColorOff)
            draw.localCircleBorder(c.x, c.y, r, sizes.borderWidth.px * 1.5f, modifier.borderColorOff)
        }
    }

    companion object {
        val factory: (UiNode, UiSurface) -> RadioButtonNode = { parent, surface -> RadioButtonNode(parent, surface) }
    }
}

class SwitchNode(parent: UiNode?, surface: UiSurface) : ToggleNode(parent, surface), SwitchScope {
    override val modifier = SwitchModifier(surface)
    override val buttonWidth: Dp get() = modifier.switchWidth
    override val buttonHeight: Dp get() = modifier.switchHeight

    override fun render(ctx: KoolContext) {
        super.render(ctx)
        val c = center()
        val w = buttonWidth.px
        val h = buttonHeight.px
        val tH = buttonHeight.px * 0.75f
        val tW = buttonWidth.px - (h - tH) * 0.5f
        val draw = getUiPrimitives()
        val p = animationPos()

        val trackColor = when (p) {
            0f -> modifier.trackColorOff
            1f -> modifier.trackColorOn
            else -> modifier.trackColorOff.mix(modifier.trackColorOn, p)
        }
        val knobColor = when (p) {
            0f -> modifier.knobColorOff
            1f -> modifier.knobColorOn
            else -> modifier.knobColorOff.mix(modifier.knobColorOn, p)
        }

        draw.localRoundRect(c.x - tW * 0.5f, c.y - tH * 0.5f, tW, tH, tH * 0.5f, trackColor)

        val extent = w - h
        val knobX = c.x - extent * 0.5f + extent * animationPos()
        draw.localCircle(knobX, c.y, floor(h * 0.5f), knobColor)
    }

    companion object {
        val factory: (UiNode, UiSurface) -> SwitchNode = { parent, surface -> SwitchNode(parent, surface) }
    }
}