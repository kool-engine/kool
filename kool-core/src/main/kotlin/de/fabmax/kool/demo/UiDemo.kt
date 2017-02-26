package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.*
import de.fabmax.kool.util.uiFont

/**
 * @author fabmax
 */

fun uiDemo(ctx: RenderContext) {

    ctx.scene.camera = OrthographicCamera().apply {
        clipToViewport = true
    }

//    ctx.scene.camera = PerspectiveCamera().apply {
//        position.set(0f, 0f, 15f)
//    }

    // Create scene contents
    ctx.scene.root = group {
        +sphericalInputTransform { +ctx.scene.camera }

        +colorMesh {
            generator = {
                cube {
                    colorCube()
                    centerOrigin()
                }
            }
        }

        +UiRoot().apply {
            isFillViewport = true
            //translate(-globalWidth /2, -globalHeight/2, 0f)
            //scaleContentTo(mm(100f), 96f)

            +UiPanel("panel").apply {
                font = uiFont("Segoe UI", 32f, ctx.screenDpi)
                layoutSpec.setOrigin(pc(25f), pc(0f), un(0f))
                layoutSpec.setSize(pc(50f), pc(100f), un(0f))
            }
        }

//        +textureMesh {
//            generator = {
//                rect {
//                    origin.set(-25f, 0f, 0f)
//                    width = 10.24f * 10
//                    height = 0.64f * 10
//                }
//            }
//            (shader as BasicShader).texture = font.texture
//        }

//        +textMesh(font) {
//            generator = {
//                color = Color.BLACK
//                translate(rpx(10f), rpx(100f), 0f)
//                text(font) {
//                    text = "Hello World! ${ctx.viewportWidth} x ${ctx.viewportHeight}"
//                }
//            }
//        }
    }
    ctx.run()
}

class FullPixelRound(val scale: Float) {
    private val scaleRecip = 1f / scale

    companion object {
        fun mm(screenDpi: Float): FullPixelRound {
            return FullPixelRound(screenDpi / 25.4f)
        }
    }

    operator fun invoke(units: Float): Float {
        return Math.round(units * scale) * scaleRecip
    }
}
