package de.fabmax.kool.app

import de.fabmax.kool.KoolContext
import de.fabmax.kool.editor.api.EditorAwareApp
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.MdColor

class App : EditorAwareApp {
    override fun startApp(ctx: KoolContext, isInEditor: Boolean): List<Scene> {
        val scene = Scene().apply {
            defaultOrbitCamera()

            colorMesh {
                generate {
                    color = MdColor.BLUE.toLinear()
                    cube {
                        centered()
                    }
                }
                shader = KslPbrShader {
                    color { vertexColor() }
                }
            }
        }
        return listOf(scene)
    }
}