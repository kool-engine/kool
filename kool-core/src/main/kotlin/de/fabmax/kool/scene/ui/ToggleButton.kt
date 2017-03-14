package de.fabmax.kool.scene.ui

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

class ToggleButton(name: String, initState: Boolean = false): Button(name) {

    private val knobAnimator = CosAnimator(InterpolatedFloat(0f, 1f))

    private val knobColor = MutableColor()
    var knobColorOn = Color.WHITE
    var knobColorOff = Color.LIGHT_GRAY
    var trackColor = Color.GRAY

    var enabled = initState
        set(value) {
            if (value != field) {
                field = value
                if (value) {
                    // animate knob from left to right
                    knobAnimator.speed = 1f
                } else {
                    // animate knob from right to left
                    knobAnimator.speed = -1f
                }
            }
        }

    init {
        textAlignment = Gravity(Alignment.START, Alignment.CENTER)

        onClick += { _,_,_ -> enabled = !enabled }
        onRender += { ctx -> knobAnimator.tick(ctx) }

        knobAnimator.speed = 0f
        knobAnimator.duration = 0.15f
        knobAnimator.value.onUpdate = { isFgUpdateNeeded = true }
    }

    override fun updateForeground(ctx: RenderContext) {
        super.updateForeground(ctx)

        val paddingR = padding.right.toUnits(width, dpi)
        val trackW = dp(24f)
        val trackH = dp(6f)
        val knobR = dp(10f)
        val x = width - paddingR - trackW - knobR
        val y = (height - trackH) / 2f

        meshBuilder.color = trackColor
        meshBuilder.rect {
            origin.set(x, y, 0f)
            width = trackW
            height = trackH
            cornerRadius = trackH / 2f
            cornerSteps = 4
        }

        val anim = knobAnimator.value.value

        knobColor.clear()
        knobColor.add(knobColorOff, 1f - anim)
        knobColor.add(knobColorOn, anim)
        meshBuilder.color = knobColor
        meshBuilder.circle {
            center.set(x + trackW * anim, y + trackH / 2f, 0f)
            radius = knobR
            steps = 30
        }
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)
        knobColorOn = theme.accentColor
    }
}
