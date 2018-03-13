package de.fabmax.kool.demo

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.earth.earthScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.CosAnimator
import de.fabmax.kool.util.InterpolatedFloat
import de.fabmax.kool.util.debugOverlay
import kotlin.math.min

/**
 * @author fabmax
 */

class Demo(ctx: KoolContext, startScene: String? = null) {

    private val dbgOverlay = debugOverlay(ctx, true)
    private val newScenes = mutableListOf<Scene>()
    private val currentScenes = mutableListOf<Scene>()

    private val defaultScene = DemoEntry("Simple Demo") { add(simpleShapesScene(it)) }
    private val demos = mutableMapOf(
            "simpleDemo" to defaultScene,
            "multiDemo" to DemoEntry("Split Viewport Demo") { addAll(multiScene(it)) },
            "pointDemo" to DemoEntry("Point Cloud Demo") { add(pointScene()) },
            "synthieDemo" to DemoEntry("Synthie Demo") { addAll(synthieScene(it)) },
            "earthDemo" to DemoEntry("Earth Demo") { addAll(earthScene(it)) },
            "modelDemo" to DemoEntry("Model Demo") { add(modelScene(it)) },
            "treeDemo" to DemoEntry("Tree Demo") { addAll(treeScene(it)) }
    )

    init {
        ctx.scenes += demoOverlay(ctx)
        ctx.scenes += dbgOverlay
        ctx.onRender += this::onRender

        //dbgOverlay.isVisible = false

        (demos[startScene] ?: defaultScene).loadScene(newScenes, ctx)

        ctx.run()
    }

    private fun onRender(ctx: KoolContext) {
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

    private fun demoOverlay(ctx: KoolContext): Scene = uiScene(ctx.screenDpi) {
        theme = theme(UiTheme.DARK) {
            componentUi { BlankComponentUi() }
            containerUi(::BlurredComponentUi)
        }
        content.ui.setCustom(BlankComponentUi())

        val menuButton = toggleButton("menuButton") {
            layoutSpec.setOrigin(dps(10f, true), dps(-50f, true), zero())
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
                font.setCustom(titleFont(ctx))
            }
            +component("divider") {
                layoutSpec.setOrigin(pcs(5f), dps(-60f, true), zero())
                layoutSpec.setSize(pcs(90f), dps(1f, true), zero())
                val bg = SimpleComponentUi(this)
                bg.color.setCustom(theme.accentColor)
                ui.setCustom(bg)
            }

            var y = -105f
            for (demo in demos) {
                +button(demo.key) {
                    layoutSpec.setOrigin(zero(), dps(y, true), zero())
                    layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                    textAlignment = Gravity(Alignment.START, Alignment.CENTER)
                    text = demo.value.label
                    y -= 35f

                    onClick += { _,_,_ ->
                        demo.value.loadScene.invoke(newScenes, ctx)
                        menuButton.isEnabled = false
                    }
                }
            }

            +toggleButton("showDbg") {
                layoutSpec.setOrigin(zero(), dps(10f, true), zero())
                layoutSpec.setSize(pcs(100f, true), dps(30f, true), zero())
                text = "Debug Info"
                isEnabled = dbgOverlay.isVisible

                onClick += { _,_,_ -> dbgOverlay.isVisible = isEnabled }
            }
        }
        +menu

        menuButton.ui.setCustom(MenuButtonUi(menuButton, menu))
        +menuButton
    }

    private class DemoEntry(val label: String, val loadScene: MutableList<Scene>.(KoolContext) -> Unit)
}

class MenuButtonUi(tb: ToggleButton, private val menu: UiContainer) : ToggleButtonUi(tb, BlankComponentUi()) {

    private val menuAnimator = CosAnimator(InterpolatedFloat(0f, 1f))

    override fun createUi(ctx: KoolContext) {
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
        tb.onStateChange += {
            menuAnimator.speed = if (tb.isEnabled) 1f else -1f
        }

        mesh.shader = basicShader {
            colorModel = ColorModel.VERTEX_COLOR
            lightModel = tb.root.shaderLightModel
        }
    }

    override fun onRender(ctx: KoolContext) {
        super.onRender(ctx)
        menuAnimator.tick(ctx)
    }

    override fun updateUi(ctx: KoolContext) {
        val hw = tb.width * 0.5f
        val hh = tb.height * 0.18f
        val hx = -hw / 2f
        val ph = tb.dp(2.5f)

        updateTextColor()

        tb.setupBuilder(meshBuilder)
        meshBuilder.apply {
            // add a completely translucent circle as background to get a larger click target
            color = Color(0f, 0f, 0f, 0f)
            circle {
                radius = min(tb.width, tb.height) / 2f
                center.set(tb.width / 2f, tb.height / 2f, -4f)
                steps = 30
            }

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
