package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time

class HelloRenderToTextureDemo : DemoScene("Hello RenderToTexture") {
    override fun Scene.setupMainScene(ctx: KoolContext) {
        // create offscreen content
        val backgroundGroup = Node().apply {
            addColorMesh {
                generate {
                    cube {
                        colored(linearSpace = false)
                    }
                }
                shader = KslUnlitShader { color { vertexColor() } }
            }
            onUpdate += { transform.rotate(Time.deltaT * 30f, Vec3f.Y_AXIS) }
        }

        // setup offscreen pass
        val off = OffscreenRenderPass2d(backgroundGroup, renderPassConfig {
            width = 512
            height = 512
            addColorTexture {
                colorFormat = TexFormat.RGBA
            }
        }).apply {
            clearColor = Color.BLACK
            camera.position.set(0f, 1f, 2f)
        }

        // create main scene and draw offscreen result on a quad
        defaultOrbitCamera()
        addOffscreenPass(off)

        addTextureMesh {
            generate {
                centeredRect {
                    size.set(2f, 2f)
                }
            }
            shader = KslUnlitShader {
                color { textureColor(off.colorTexture) }
                pipeline { cullMethod = CullMethod.NO_CULLING }
            }
        }
    }
}