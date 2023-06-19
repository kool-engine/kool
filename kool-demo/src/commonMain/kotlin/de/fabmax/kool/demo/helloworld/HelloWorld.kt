package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslPbrShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addColorMesh
import de.fabmax.kool.scene.defaultOrbitCamera
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time

class HelloWorldDemo : DemoScene("Hello World") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultOrbitCamera()

        addColorMesh {
            generate {
                cube {
                    colored()
                }
            }
            shader = KslPbrShader {
                color { vertexColor() }
                metallic(0f)
                roughness(0.25f)
            }
            onUpdate {
                transform.rotate(Time.deltaT * 45f, Vec3f.X_AXIS)
            }
        }

        lighting.singleDirectionalLight {
            setup(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 5f)
        }
    }
}