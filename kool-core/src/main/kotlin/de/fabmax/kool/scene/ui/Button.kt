package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
open class Button(name: String) : Label(name) {

    var onClick: List<Button.(InputManager.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()

    val textColorHovered = ThemeOrCustomProp(Color.WHITE)

    var isPressed = false
        protected set
    var isHovered = false
        protected set

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f

    protected var ptrDownPos = MutableVec2f()

    init {
        hoverAnimator.speed = 0f
        hoverAnimator.value.onUpdate = { v ->
            colorWeightHovered = v
            colorWeightStd = 1f - v
            isFgUpdateNeeded = true
        }
        onRender += { ctx -> hoverAnimator.tick(ctx) }

        onHoverEnter += { _,_,_ ->
            isHovered = true
            hoverAnimator.duration = 0.1f
            hoverAnimator.speed = 1f
        }

        onHoverExit += { _,_,_ ->
            isPressed = false
            isHovered = false
            hoverAnimator.duration = 0.2f
            hoverAnimator.speed = -1f
        }

        onHover += { ptr, rt, ctx ->
            if (ptr.isLeftButtonEvent) {
                if (ptr.isLeftButtonDown) {
                    // button is pressed, issue click event when it is released again
                    ptrDownPos.set(rt.hitPositionLocal.x, rt.hitPositionLocal.y)
                    isPressed = true
                } else if (isPressed) {
                    // button was pressed and pointer is up, issue click event
                    isPressed = false

                    // check that pointer didn't move to much
                    ptrDownPos.x -= rt.hitPositionLocal.x
                    ptrDownPos.y -= rt.hitPositionLocal.y
                    if (ptrDownPos.length() < this@Button.dp(3f)) {
                        for (i in onClick.indices) {
                            onClick[i](ptr, rt, ctx)
                        }
                    }
                }
            }
        }
    }

    override fun updateForeground(ctx: RenderContext) {
        foregroundColor.clear()
        foregroundColor.add(textColor.apply(), colorWeightStd)
        foregroundColor.add(textColorHovered.apply(), colorWeightHovered)

        super.updateForeground(ctx)
    }

    override fun applyTheme(theme: UiTheme, ctx: RenderContext) {
        super.applyTheme(theme, ctx)
        textColorHovered.setTheme(theme.accentColor)
    }
}
