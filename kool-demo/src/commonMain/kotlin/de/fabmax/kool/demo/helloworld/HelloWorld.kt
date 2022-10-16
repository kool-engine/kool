package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.pipeline.shading.Albedo
import de.fabmax.kool.pipeline.shading.pbrShader
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.colorMesh
import de.fabmax.kool.scene.defaultCamTransform
import de.fabmax.kool.util.Color

class HelloWorldDemo : DemoScene("Hello World") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        defaultCamTransform()

        +colorMesh {
            generate {
                cube {
                    colored()
                    centered()
                }
            }
            shader = pbrShader {
                albedoSource = Albedo.VERTEX_ALBEDO
                metallic = 0.0f
                roughness = 0.25f
            }
        }

        lighting.singleLight {
            setDirectional(Vec3f(-1f, -1f, -1f))
            setColor(Color.WHITE, 5f)
        }
    }
}