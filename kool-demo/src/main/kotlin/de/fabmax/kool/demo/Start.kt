package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

class Demo(ctx: RenderContext) {

    private val dbgOverlay = debugOverlay(ctx)
    private var newScene: Scene? = simpleShapesScene()
    private var currentScene: Scene? = null

    init {
        ctx.scenes += demoOverlay(ctx)
        ctx.scenes += dbgOverlay
        ctx.onRender += this::onRender

        dbgOverlay.isVisible = false

        ctx.run()
    }

    fun onRender(ctx: RenderContext) {
        if (newScene != null) {
            if (currentScene != null) {
                ctx.scenes -= currentScene!!
                currentScene!!.dispose(ctx)
            }
            // new scenes has to be inserted as first element, so overlays are rendered after it
            ctx.scenes.add(0, newScene!!)
            currentScene = newScene
            newScene = null
        }
    }

    private fun demoOverlay(ctx: RenderContext): Scene = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi({ BlankComponentUi() })
            containerUi(::BlurredComponentUi)
        }
        content.ui.setCustom(BlankComponentUi())

        val menu = container("menu") {
            layoutSpec.setOrigin(zero(), zero(), zero())
            layoutSpec.setSize(dps(250f, true), pcs(100f, true), zero())

            +label("title") {
                layoutSpec.setOrigin(zero(), dps(-50f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(40f, true), zero())
                textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                text = "Demos"
                textColor.setCustom(theme.accentColor)
                font.setCustom(theme.titleFont(dpi))
            }
            +component("divider") {
                layoutSpec.setOrigin(pcs(5f), dps(-60f, true), zero())
                layoutSpec.setSize(pcs(90f), dps(1f, true), zero())
                val bg = SimpleComponentUi(this)
                bg.color.setCustom(theme.accentColor)
                ui.setCustom(bg)
            }

            +button("simpleDemo") {
                layoutSpec.setOrigin(zero(), dps(-105f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Simple Demo"

                onClick += { _,_,_ -> newScene = simpleShapesScene() }
            }
            +button("uiDemo") {
                layoutSpec.setOrigin(zero(), dps(-140f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "UI Demo"

                onClick += { _,_,_ -> newScene = uiDemoScene() }
            }
            +button("pointDemo") {
                layoutSpec.setOrigin(zero(), dps(-175f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Point Cloud Demo"

                onClick += { _,_,_ -> newScene = pointScene() }
            }
            +toggleButton("showDbg") {
                layoutSpec.setOrigin(zero(), dps(10f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                text = "Debug Info"

                onClick += { _,_,_ -> dbgOverlay.isVisible = isEnabled }
            }
        }
        +menu

        +toggleButton("menuButton") {
            layoutSpec.setOrigin(dps(10f, true), dps(-50f, true), dps(4f))
            layoutSpec.setSize(dps(40f, true), dps(40f, true), zero())

            ui.setCustom(MenuButtonUi(this, menu))
        }
    }
}

class MenuButtonUi(tb: ToggleButton, val menu: UiContainer) : ToggleButtonUi(tb, BlankComponentUi()) {

    private val bgColor = MutableColor()
    private val menuAnimator = CosAnimator(InterpolatedFloat(0f, 1f))

    override fun createUi(ctx: RenderContext) {
        super.createUi(ctx)
        knobAnimator.duration = 0.5f
        knobAnimator.value.onUpdate

        menuAnimator.duration = 0.25f
        menuAnimator.speed = -1f
        menuAnimator.value.onUpdate = { v ->
            menu.setIdentity()
            menu.translate(menu.posInParent.x + tb.dp(-40f) * (1f - v), menu.posInParent.y, menu.posInParent.z)
            menu.alpha = v
        }
        tb.onClick += { _,_,_ ->
            menuAnimator.speed = if (tb.isEnabled) 1f else -1f
        }
    }

    override fun onRender(ctx: RenderContext) {
        super.onRender(ctx)
        menuAnimator.tick(ctx)
    }

    override fun updateUi(ctx: RenderContext) {
        val hw = tb.width * 0.5f
        val hh = tb.height * 0.18f
        val hx = -hw / 2f
        val ph = tb.dp(2.5f)

        bgColor.set(tb.root.theme.backgroundColor)
        bgColor.a = 0.7f
        updateTextColor()

        tb.setupBuilder(meshBuilder)
        meshBuilder.apply {
//            color = bgColor
//            circle {
//                radius = Math.min(tb.width, tb.height) / 2f
//                center.set(tb.width / 2f, tb.height / 2f, 0f)
//                steps = 30
//            }

            val tx = knobAnimator.value.value * -hw * 0.1f
            val w = hw - knobAnimator.value.value * hw * 0.4f

            color = textColor
            translate(tb.width / 2f, tb.height / 2f, 0f)
            rotate(180f - knobAnimator.value.value * 180f, Vec3f.Z_AXIS)

            withTransform {
                translate(tx, hh, 0f)
                rotate(knobAnimator.value.value * 45f, Vec3f.Z_AXIS)
                rect {
                    origin.set(hx, -ph / 2f, 0f)
                    size.set(w, ph)
                }
            }
            rect {
                origin.set(hx, -ph / 2f, 0f)
                size.set(hw, ph)
            }
            withTransform {
                translate(tx, -hh, 0f)
                rotate(knobAnimator.value.value * -45f, Vec3f.Z_AXIS)
                rect {
                    origin.set(hx, -ph / 2f, 0f)
                    size.set(w, ph)
                }
            }
        }
    }
}
