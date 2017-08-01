package de.fabmax.kool.demo

import de.fabmax.kool.demo.earth.earthScene
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

class Demo(ctx: RenderContext, startScene: String? = null) {

    private val dbgOverlay = debugOverlay(ctx, true)
    private val newScenes: MutableList<Scene> = mutableListOf()
    private val currentScenes: MutableList<Scene> = mutableListOf()

    init {
        ctx.scenes += demoOverlay(ctx)
        ctx.scenes += dbgOverlay
        ctx.onRender += this::onRender

        dbgOverlay.isVisible = false

        when (startScene) {
            "simpleDemo" -> newScenes.add(simpleShapesScene())
            "multiDemo" -> newScenes.addAll(multiScene())
            "pointDemo" -> newScenes.add(pointScene())
            "synthieDemo" -> newScenes.addAll(synthieScene(ctx))
            "earthDemo" -> newScenes.addAll(earthScene())
            else -> newScenes.add(simpleShapesScene())
        }

        ctx.run()
    }

    fun onRender(ctx: RenderContext) {
        if (!newScenes.isEmpty()) {
            currentScenes.forEach { s ->
                ctx.scenes -= s
                s.dispose(ctx)
            }
            currentScenes.clear()

            // new scenes have to be inserted in front, so that demo menu is rendered after it
            newScenes.forEachIndexed { i, s ->
                ctx.scenes.add(i, s)
                currentScenes.add(s)
            }
            newScenes.clear()
        }
    }

    private fun demoOverlay(ctx: RenderContext): Scene = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi({ BlankComponentUi() })
            containerUi(::BlurredComponentUi)
        }
        content.ui.setCustom(BlankComponentUi())

        val menuButton = toggleButton("menuButton") {
            layoutSpec.setOrigin(dps(10f, true), dps(-50f, true), dps(4f))
            layoutSpec.setSize(dps(40f, true), dps(40f, true), zero())
        }

        val menu = container("menu") {
            layoutSpec.setOrigin(zero(), zero(), zero())
            layoutSpec.setSize(dps(250f, true), pcs(100f, true), zero())

            // menu starts hidden
            alpha = 0f

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

                onClick += { _,_,_ ->
                    newScenes.add(simpleShapesScene())
                    menuButton.isEnabled = false
                }
            }
            +button("multiDemo") {
                layoutSpec.setOrigin(zero(), dps(-140f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Split Viewport Demo"

                onClick += { _,_,_ ->
                    newScenes.addAll(multiScene())
                    menuButton.isEnabled = false
                }
            }
            +button("pointDemo") {
                layoutSpec.setOrigin(zero(), dps(-175f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Point Cloud Demo"

                onClick += { _,_,_ ->
                    newScenes.add(pointScene())
                    menuButton.isEnabled = false
                }
            }
            +button("synthieDemo") {
                layoutSpec.setOrigin(zero(), dps(-210f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Synthie Demo"

                onClick += { _,_,_ ->
                    newScenes.addAll(synthieScene(ctx))
                    menuButton.isEnabled = false
                }
            }
            +button("earthDemo") {
                layoutSpec.setOrigin(zero(), dps(-245f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                text = "Earth Demo"

                onClick += { _,_,_ ->
                    newScenes.addAll(earthScene())
                    menuButton.isEnabled = false
                }
            }
            +toggleButton("showDbg") {
                layoutSpec.setOrigin(zero(), dps(10f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                text = "Debug Info"

                onClick += { _,_,_ -> dbgOverlay.isVisible = isEnabled }
            }
        }
        +menu

        menuButton.ui.setCustom(MenuButtonUi(menuButton, menu))
        +menuButton
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
        tb.onStateChange += { ->
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
