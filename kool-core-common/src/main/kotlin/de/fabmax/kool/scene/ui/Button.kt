package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.MutableVec2f
import de.fabmax.kool.math.RayTest
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.InterpolatedFloat
import de.fabmax.kool.util.LinearAnimator

/**
 * @author fabmax
 */
open class Button(name: String, root: UiRoot) : Label(name, root) {

    val onClick: MutableList<Button.(InputManager.Pointer, RayTest, KoolContext) -> Unit> = mutableListOf()

    val textColorHovered = ThemeOrCustomProp(Color.WHITE)

    var isPressed = false
        protected set
    var isHovered = false
        protected set

    protected var ptrDownPos = MutableVec2f()

    init {
        textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)

        onHoverEnter += { _,_,_ ->
            isHovered = true
        }

        onHoverExit += { _,_,_ ->
            isHovered = false
            isPressed = false
        }

        onHover += { ptr, rt, ctx ->
            if (ptr.isLeftButtonEvent) {
                if (ptr.isLeftButtonDown) {
                    // button is pressed, issue click event when it is released again
                    ptrDownPos.set(rt.hitPositionLocal.x - contentBounds.min.x, rt.hitPositionLocal.y - contentBounds.min.y)
                    isPressed = true

                } else if (isPressed) {
                    // button was pressed and pointer is up, issue click event
                    isPressed = false
                    // check that pointer didn't move to much
                    ptrDownPos.x -= rt.hitPositionLocal.x - contentBounds.min.x
                    ptrDownPos.y -= rt.hitPositionLocal.y - contentBounds.min.y
                    if (ptrDownPos.length() < this@Button.dp(5f)) {
                        fireOnClick(ptr, rt, ctx)
                    }
                }
            }
        }
    }

    protected open fun fireOnClick(ptr: InputManager.Pointer, rt: RayTest, ctx: KoolContext) {
        for (i in onClick.indices) {
            onClick[i](ptr, rt, ctx)
        }
    }

    override fun setThemeProps(ctx: KoolContext) {
        super.setThemeProps(ctx)
        textColorHovered.setTheme(root.theme.accentColor)
    }

    override fun createThemeUi(ctx: KoolContext): ComponentUi {
        return root.theme.newButtonUi(this)
    }
}

open class ButtonUi(val button: Button, baseUi: ComponentUi) : LabelUi(button, baseUi) {

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f

    protected val hoverEnterListener: Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit = { _, _, _ ->
        hoverAnimator.duration = 0.1f
        hoverAnimator.speed = 1f
    }

    protected val hoverExitListener: Node.(InputManager.Pointer, RayTest, KoolContext) -> Unit = { _, _, _ ->
        hoverAnimator.duration = 0.2f
        hoverAnimator.speed = -1f
    }

    override fun createUi(ctx: KoolContext) {
        super.createUi(ctx)

        hoverAnimator.speed = 0f
        hoverAnimator.value.onUpdate = { v ->
            colorWeightHovered = v
            colorWeightStd = 1f - v
            button.requestUiUpdate()
        }

        button.onHoverEnter += hoverEnterListener
        button.onHoverExit += hoverExitListener
    }

    override fun updateTextColor() {
        textColor.clear()
        textColor.add(button.textColor.apply(), colorWeightStd)
        textColor.add(button.textColorHovered.apply(), colorWeightHovered)
    }

    override fun dispose(ctx: KoolContext) {
        super.dispose(ctx)
        button.onHoverEnter -= hoverEnterListener
        button.onHoverExit -= hoverExitListener
    }

    override fun onRender(ctx: KoolContext) {
        super.onRender(ctx)
        hoverAnimator.tick(ctx)
    }
}
