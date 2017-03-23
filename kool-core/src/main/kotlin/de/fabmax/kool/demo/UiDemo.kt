package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.Vec3f
import de.fabmax.kool.util.color

/**
 * @author fabmax
 */

fun uiDemo(ctx: RenderContext) {
    ctx.scenes += uiScene()
//    ctx.scene.camera = OrthographicCamera().apply {
//        clipToViewport = true
//    }

    ctx.clearColor = color("00323F")
//    ctx.scene.camera = PerspectiveCamera().apply {
//        position.set(0f, 0f, 15f)
//    }
    ctx.run()
}

fun uiScene(): Scene = scene("UI Demo") {
    +sphericalInputTransform { +camera }

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

        content.apply {
            translate(-globalWidth /2, -globalHeight/2, 0f)
            scaleContentTo(dps(400f))

            +ToggleButton("toggle-button", root).apply {
                layoutSpec.setOrigin(dps(50f), pcs(-15f), uns(0f))
                layoutSpec.setSize(dps(300f), pcs(15f), uns(0f))

                text = "Toggle Button"
            }

            +Label("label", root).apply {
                layoutSpec.setOrigin(dps(50f), pcs(-35f), uns(0f))
                layoutSpec.setSize(dps(80f), pcs(15f), uns(0f))

                text = "Slider"
            }

            +Slider("slider", 0.4f, 1f, 1f, root).apply {
                layoutSpec.setOrigin(dps(130f), pcs(-35f), uns(0f))
                layoutSpec.setSize(dps(220f), pcs(15f), uns(0f))
                padding.left = uns(0f)

                onValueChanged += { value ->
                    root.content.alpha = value
                }
            }

            +TextField("text-field", root).apply {
                layoutSpec.setOrigin(dps(50f), pcs(-55f), uns(0f))
                layoutSpec.setSize(dps(300f), pcs(15f), uns(0f))
                text = "a"
            }

            +Button("toggle-theme", root).apply {
                layoutSpec.setOrigin(dps(50f), pcs(-75f), uns(0f))
                layoutSpec.setSize(dps(300f), pcs(15f), uns(0f))
                text = "Toggle Theme"

                onClick += { _,_,_ ->
                    if (theme == UiTheme.DARK) {
                        theme = UiTheme.LIGHT
                    } else {
                        theme = UiTheme.DARK
                    }
                }
            }
        }
    }
}
