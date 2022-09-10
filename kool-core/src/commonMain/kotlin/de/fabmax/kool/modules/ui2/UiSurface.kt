package de.fabmax.kool.modules.ui2

import de.fabmax.kool.scene.Group

class UiSurface(
    name: String = "uiSurface",
    private val block: BoxScope.() -> Unit
) : Group(name) {

    private val uiCtx = UiContext(this)

    init {

//        +colorMesh {
//            generate {
//                color = Color.RED
//                rect {
//                    origin.set(100f, -100f, 0f)
//                    size.set(500f, 500f)
//                }
//            }
//            shader = KslUnlitShader { color { vertexColor() } }
//        }

        onUpdate += {
            uiCtx.updateUi(it, block)
        }
    }
}
