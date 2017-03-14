package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.shading.ColorModel
import de.fabmax.kool.shading.LightModel
import de.fabmax.kool.shading.basicShader
import de.fabmax.kool.util.*

/**
 * @author fabmax
 */

fun uiDemo(ctx: RenderContext) {

//    ctx.scene.camera = OrthographicCamera().apply {
//        clipToViewport = true
//    }

    ctx.clearColor = color("00323F")
    ctx.scene.camera = PerspectiveCamera().apply {
        position.set(0f, 0f, 15f)
    }

    // Create scene contents
    ctx.scene.root = group {
        +sphericalInputTransform { +ctx.scene.camera }

        +transformGroup {
            onRender += { ctx ->
                setIdentity()
                translate(0f, 0f, -7f)
                rotate((ctx.time * 60).toFloat(), Vec3f.X_AXIS)
                rotate((ctx.time * 17).toFloat(), Vec3f.Y_AXIS)
            }
            +colorMesh {
                generator = {
                    scale(5f, 5f, 5f)
                    cube {
                        centerOrigin()
                        colorCube()
                    }
                }
            }
        }

        +UiRoot(300f).apply {
            theme = UiTheme.DARK

            val alphaAnimator = LinearAnimator(InterpolatedFloat(0.5f, 1f)).apply {
                speed = 1f
                duration = 1f
                value.onUpdate = { v ->
                    alpha = v
                }
            }
            onRender += { ctx ->
                alphaAnimator.tick(ctx)
            }

            translate(-globalWidth /2, -globalHeight/2, 0f)
            scaleContentTo(dps(400f))

            +ToggleButton("toggle-button").apply {
                layoutSpec.setOrigin(dps(50f), pcs(100f), uns(0f))
                layoutSpec.setSize(dps(300f), pcs(20f), uns(0f))

                text = "Toggle Button"
            }

            for (i in 1..3) {
                +Button("button $i").apply {
                    layoutSpec.setOrigin(dps(50f), pcs(-30f * i), uns(0f))
                    layoutSpec.setSize(dps(300f), pcs(20f), uns(0f))

                    //font = uiFont(Font.SYSTEM_FONT, 24f, uiDpi)
                    //textColor = Color.WHITE
                    text = "Button " + i

//                    if (i == 1) {
//                        textAlignment = Gravity(Alignment.START, Alignment.START)
//                    } else if (i == 2) {
//                        textAlignment = Gravity(Alignment.CENTER, Alignment.CENTER)
//                    } else if (i == 3) {
//                        textAlignment = Gravity(Alignment.END, Alignment.END)
//                    }

                    onClick += { _,_,_ ->
                        println("$text clicked")
                        if (i == 1) {
                            theme = UiTheme.LIGHT
                        } else if (i == 2) {
                            theme = UiTheme.DARK
                        } else if (i == 3) {
                            alphaAnimator.speed = if (alphaAnimator.progress > 0.5f) {
                                -1f
                            } else {
                                1f
                            }
                        }
                    }
                }
            }
        }
    }
    ctx.run()
}
