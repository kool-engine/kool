package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.CosAnimator
import de.fabmax.kool.util.InterpolatedFloat
import de.fabmax.kool.util.MutableColor

/**
 * @author fabmax
 */

open class ToggleButton(name: String, root: UiRoot, initState: Boolean = false): Button(name, root) {

    val onStateChange: MutableList<ToggleButton.() -> Unit> = mutableListOf()

    var knobColorOn = Color.WHITE
    var knobColorOff = Color.LIGHT_GRAY
    var trackColor = Color.GRAY

    var isEnabled = initState
        set(value) {
            if (value != field) {
                field = value
                fireStateChanged()
            }
        }

    init {
        textAlignment = Gravity(Alignment.START, Alignment.CENTER)
    }

    protected fun fireStateChanged() {
        for (i in onStateChange.indices) {
            onStateChange[i]()
        }
    }

    override fun fireOnClick(ptr: InputManager.Pointer, rt: RayTest, ctx: KoolContext) {
        isEnabled = !isEnabled
        super.fireOnClick(ptr, rt, ctx)
    }

    override fun setThemeProps(ctx: KoolContext) {
        super.setThemeProps(ctx)
        knobColorOn = root.theme.accentColor
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newToggleButtonUi(this)
    }
}

open class ToggleButtonUi(val tb: ToggleButton, baseUi: ComponentUi) : ButtonUi(tb, baseUi) {

    protected val knobAnimator = CosAnimator(InterpolatedFloat(0f, 1f))
    protected val knobColor = MutableColor()

    protected val stateChangedListener: ToggleButton.() -> Unit = {
        if (isEnabled) {
            // animate knob from left to right
            knobAnimator.speed = 1f
        } else {
            // animate knob from right to left
            knobAnimator.speed = -1f
        }
    }

    override fun createUi(ctx: KoolContext) {
        super.createUi(ctx)

        knobAnimator.speed = 0f
        knobAnimator.duration = 0.15f
        knobAnimator.value.value = if (tb.isEnabled) { 1f } else { 0f }
        knobAnimator.value.onUpdate = { tb.requestUiUpdate() }

        tb.onStateChange += stateChangedListener
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        tb.onStateChange -= stateChangedListener
    }

    override fun updateUi(ctx: KoolContext) {
        super.updateUi(ctx)

        val paddingR = tb.padding.right.toUnits(tb.width, tb.dpi)
        val trackW = tb.dp(24f)
        val trackH = tb.dp(6f)
        val knobR = tb.dp(10f)
        val x = tb.width - paddingR - trackW - knobR
        val y = (tb.height - trackH) / 2f

        meshBuilder.color = tb.trackColor
        meshBuilder.rect {
            origin.set(x, y, tb.dp(4f))
            size.set(trackW, trackH)
            cornerRadius = trackH / 2f
            cornerSteps = 4
        }

        val anim = knobAnimator.value.value

        knobColor.clear()
        knobColor.add(tb.knobColorOff, 1f - anim)
        knobColor.add(tb.knobColorOn, anim)
        meshBuilder.color = knobColor
        meshBuilder.circle {
            center.set(x + trackW * anim, y + trackH / 2f, tb.dp(6f))
            radius = knobR
            steps = 30
        }
    }

    override fun onRender(ctx: KoolContext) {
        super.onRender(ctx)
        knobAnimator.tick(ctx)
    }
}
