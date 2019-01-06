package de.fabmax.kool.demo

import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*

/**
 * @author fabmax
 */

fun uiDemoScene(): Scene = scene("UI Demo") {
    +sphericalInputTransform { +camera }

    +transformGroup {
        onPreRender += { ctx ->
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

    +embeddedUi(10f, 10f, dps(400f)) {
        content.uiDemoContent(this)
    }
}

fun UiContainer.uiDemoContent(uiRoot: UiRoot) {
    translate(-uiRoot.globalWidth /2, -uiRoot.globalHeight/2, 0f)

    +ToggleButton("toggle-button", root).apply {
        layoutSpec.setOrigin(pcs(15f), pcs(-25f), zero())
        layoutSpec.setSize(pcs(70f), pcs(15f), full())

        text = "Toggle Button"
    }

    +Label("label", root).apply {
        layoutSpec.setOrigin(pcs(15f), pcs(-45f), zero())
        layoutSpec.setSize(pcs(20f), pcs(15f), full())

        text = "Slider"
    }

    +Slider("slider", 0.4f, 1f, 1f, root).apply {
        layoutSpec.setOrigin(pcs(35f), pcs(-45f), zero())
        layoutSpec.setSize(pcs(50f), pcs(15f), full())
        padding.left = uns(0f)

        onValueChanged += { value ->
            root.content.alpha = value
        }
    }

    +TextField("text-field", root).apply {
        layoutSpec.setOrigin(pcs(15f), pcs(-65f), zero())
        layoutSpec.setSize(pcs(70f), pcs(15f), full())
    }

    +Button("toggle-theme", root).apply {
        layoutSpec.setOrigin(pcs(15f), pcs(-85f), zero())
        layoutSpec.setSize(pcs(70f), pcs(15f), full())
        text = "Toggle Theme"

        onClick += { _,_,_ ->
            if (uiRoot.theme == UiTheme.DARK) {
                uiRoot.theme = UiTheme.LIGHT
            } else {
                uiRoot.theme = UiTheme.DARK
            }
        }
    }
}
