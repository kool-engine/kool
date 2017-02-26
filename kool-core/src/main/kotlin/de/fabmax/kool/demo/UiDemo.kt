package de.fabmax.kool.demo

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.scene.*
import de.fabmax.kool.scene.ui.UiPanel
import de.fabmax.kool.scene.ui.UiRoot
import de.fabmax.kool.scene.ui.pc
import de.fabmax.kool.scene.ui.un
import de.fabmax.kool.util.uiFont

/**
 * @author fabmax
 */

fun uiDemo(ctx: RenderContext) {

    ctx.scene.camera = OrthographicCamera().apply {
        clipToViewport = true
    }
//    ctx.scene.camera = PerspectiveCamera().apply {
//        position.set(0f, 0f, 200f)
//        clipNear = 1f
//        clipFar = 1000f
//    }

    // Create scene contents
    ctx.scene.root = group {
        +sphericalInputTransform { +ctx.scene.camera }

        +UiRoot().apply {
            isFillViewport = true
            //translate(-50f, -50f, 0f)
            +UiPanel("panel").apply {
                font = uiFont("Segoe UI", 32f, ctx.screenDpi)
                layoutSpec.setOrigin(pc(25f), pc(25f), un(0f))
                layoutSpec.setSize(pc(50f), pc(50f), un(0f))
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
