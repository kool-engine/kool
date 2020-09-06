package de.fabmax.kool.scene.ui

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.animation.CosAnimator
import de.fabmax.kool.util.animation.InterpolatedFloat
import kotlin.math.min

class DrawerMenu(width: SizeSpec, title: String?, name: String, root: UiRoot) : UiContainer(name, root) {

    private val menuAnimator = CosAnimator(InterpolatedFloat(0f, 1f))
    internal lateinit var menuButton: ToggleButton

    var isOpen: Boolean
        get() = menuButton.isEnabled
        set(value) { menuButton.isEnabled = value }

    val animationPos: Float
        get() = menuAnimator.value.value

    init {
        menuAnimator.duration = 0.25f
        menuAnimator.speed = -1f
        menuAnimator.value.onUpdate = { v ->
            setScrollOffset(dp(40f) * (1f - v), 0f, 0f)
            alpha = v
        }

        root.apply {
            this@DrawerMenu.apply {
                layoutSpec.setOrigin(zero(), zero(), zero())
                layoutSpec.setSize(width, pcs(100f, true), full())

                // menu starts hidden
                alpha = 0f

                if (title != null) {
                    +label("title") {
                        layoutSpec.setOrigin(zero(), dps(-50f, true), zero())
                        layoutSpec.setSize(pcs(100f, true), dps(40f, true), full())
                        textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
                        text = title
                        textColor.setCustom(theme.accentColor)

                        // hacky: we need KoolContext to create the title font...
                        var initFont = true
                        onUpdate += { evt ->
                            if (initFont) {
                                initFont = false
                                font.setCustom(titleFont(evt.ctx))
                            }
                        }
                    }
                    +component("divider") {
                        layoutSpec.setOrigin(pcs(5f), dps(-58f, true), zero())
                        layoutSpec.setSize(pcs(90f), dps(1f, true), full())
                        val bg = SimpleComponentUi(this)
                        bg.color.setCustom(theme.accentColor)
                        ui.setCustom(bg)
                    }
                }
            }
            toggleButton("$name-menuButton") {
                menuButton = this
                ui.setCustom(MenuButtonUi(this))
                layoutSpec.setOrigin(dps(10f, true), dps(-50f, true), zero())
                layoutSpec.setSize(dps(40f, true), dps(40f, true), full())
            }
        }
    }

    private inner class MenuButtonUi(tb: ToggleButton) : ToggleButtonUi(tb, BlankComponentUi()) {

        override fun createUi(ctx: KoolContext) {
            super.createUi(ctx)
            knobAnimator.duration = 0.5f
            knobAnimator.value.onUpdate

            tb.onStateChange += {
                menuAnimator.speed = if (tb.isEnabled) 1f else -1f
            }

            mesh.shader = UiShader()
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
                    zeroTexCoords()
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
                        zeroTexCoords()
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
}
