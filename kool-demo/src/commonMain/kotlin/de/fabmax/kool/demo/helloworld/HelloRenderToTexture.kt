package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.CullMethod
import de.fabmax.kool.pipeline.OffscreenRenderPass
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.scene.*
import de.fabmax.kool.util.Color
import de.fabmax.kool.util.Time
import de.fabmax.kool.util.releaseWith

class HelloRenderToTexture : DemoScene("Hello RenderToTexture") {
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
            onUpdate += { transform.rotate(30f.deg * Time.deltaT, Vec3f.Y_AXIS) }
        }
        backgroundGroup.releaseWith(this)

        // setup offscreen pass
        val off = OffscreenRenderPass2d(
            backgroundGroup,
            OffscreenRenderPass.colorAttachmentDefaultDepth(TexFormat.RGBA),
            Vec2i(512, 512),
            name = "render-to-texture"
        ).apply {
            clearColor = Color.BLACK
            camera.position.set(0f, 1f, 2f)
        }

        // create main scene and draw offscreen result on a quad
        defaultOrbitCamera()
        addOffscreenPass(off)

        addTextureMesh {
            generate {
                rect {
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