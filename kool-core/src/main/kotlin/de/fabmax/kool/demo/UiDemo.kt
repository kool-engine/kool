package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.uiFont

/**
 * @author fabmax
 */

fun uiDemo(ctx: RenderContext) {

//    ctx.scene.camera = OrthographicCamera().apply {
//        clipToViewport = true
//    }

    ctx.scene.camera = PerspectiveCamera().apply {
        position.set(0f, 0f, 15f)
    }

    // Create scene contents
    ctx.scene.root = group {
        +sphericalInputTransform { +ctx.scene.camera }

        +colorMesh {
            generator = {
                translate(0f, 0f, -3f)
                cube {
                    centerOrigin()
                    colorCube()
                }
            }
        }

        +UiRoot().apply {
            //isFillViewport = true
            translate(-globalWidth /2, -globalHeight/2, 0f)
            scaleContentTo(dp(400f), ctx.screenDpi)

            for (i in 1..1) {
                +UiPanel("button $i").apply {
                    font = uiFont("Segoe UI", 32f, ctx.screenDpi)
                    layoutSpec.setOrigin(dp(150f), dp(150f), un(0f))
                    layoutSpec.setSize(dp(100f), dp(100f), un(0f))
                    panelText = "Button " + i

                    onHoverEnter = { ptr, rt, ctx ->
                        println("$panelText hover enter ${rt.hitPositionLocal}")
                    }
                    onHoverExit = { ptr, rt, ctx ->
                        println("$panelText hover exit")
                    }
                    onHover = { ptr, rt, ctx ->
                        if (ptr.isLeftButtonEvent && !ptr.isLeftButtonDown) {
                            println("$panelText clicked")
                        }
                    }
                }
            }
        }
    }
    ctx.run()
}
