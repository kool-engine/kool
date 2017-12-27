package de.fabmax.kool.scene.ui

import de.fabmax.kool.InputManager
import de.fabmax.kool.RenderContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */
open class Button(name: String, root: UiRoot) : Label(name, root) {

    val onClick: MutableList<Button.(InputManager.Pointer, RayTest, RenderContext) -> Unit> = mutableListOf()

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
                    ptrDownPos.set(rt.hitPositionLocal.x, rt.hitPositionLocal.y)
                    isPressed = true
                } else if (isPressed) {
                    // button was pressed and pointer is up, issue click event
                    isPressed = false

                    // check that pointer didn't move to much
                    ptrDownPos.x -= rt.hitPositionLocal.x
                    ptrDownPos.y -= rt.hitPositionLocal.y
                    if (ptrDownPos.length() < this@Button.dp(5f)) {
                        fireOnClick(ptr, rt, ctx)
                    }
                }
            }
        }
    }

    protected open fun fireOnClick(ptr: InputManager.Pointer, rt: RayTest, ctx: RenderContext) {
        for (i in onClick.indices) {
            onClick[i](ptr, rt, ctx)
        }
    }

    override fun setThemeProps() {
        super.setThemeProps()
        textColorHovered.setTheme(root.theme.accentColor)
    }

    override fun createThemeUi(ctx: RenderContext): ComponentUi {
        return root.theme.newButtonUi(this)
    }
}

open class ButtonUi(val button: Button, baseUi: ComponentUi) : LabelUi(button, baseUi) {

    protected var hoverAnimator = LinearAnimator(InterpolatedFloat(0f, 1f))
    protected var colorWeightStd = 1f
    protected var colorWeightHovered = 0f

    protected val hoverEnterListener: Node.(InputManager.Pointer, RayTest, RenderContext) -> Unit = { _,_,_ ->
        hoverAnimator.duration = 0.1f
        hoverAnimator.speed = 1f
    }

    protected val hoverExitListener: Node.(InputManager.Pointer, RayTest, RenderContext) -> Unit = { _,_,_ ->
        hoverAnimator.duration = 0.2f
        hoverAnimator.speed = -1f
    }

    override fun createUi(ctx: RenderContext) {
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

    override fun disposeUi(ctx: RenderContext) {
        super.disposeUi(ctx)
        button.onHoverEnter -= hoverEnterListener
        button.onHoverExit -= hoverExitListener
    }

    override fun onRender(ctx: RenderContext) {
        super.onRender(ctx)
        hoverAnimator.tick(ctx)
    }
}
