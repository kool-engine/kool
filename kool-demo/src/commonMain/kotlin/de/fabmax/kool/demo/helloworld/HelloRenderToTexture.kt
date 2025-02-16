package de.fabmax.kool.demo.helloworld

import de.fabmax.kool.KoolContext
import de.fabmax.kool.demo.DemoScene
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.math.Vec3f
import de.fabmax.kool.math.deg
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.*
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
        val off = OffscreenPass2d(
            drawNode = backgroundGroup,
            attachmentConfig = AttachmentConfig {
                addColor {
                    textureFormat = TexFormat.RGBA
                    samplerSettings = SamplerSettings().clamped().nearest()
                }
                // the offscreen pass will do depth testing, but the depth buffer won't be used later on.
                // in contrast, defaultDepth() would provide a depth texture which could be used by additional
                // render passes later on)
                transientDepth()
            },
            initialSize = Vec2i(512, 512),
            name = "render-to-texture",
            numSamples = 4
        ).apply {
            camera.position.set(0f, 1f, 2f)
            mirrorIfInvertedClipY()
        }

        // create main scene and draw offscreen result on a quad
        defaultOrbitCamera(yaw = 0f, pitch = 0f).apply {
            zoom = 2.0
        }
        addOffscreenPass(off)

        addTextureMesh {
            generate {
                rect {
                    size.set(2f, 2f)
                    mirrorTexCoordsY()
                }
            }
            shader = KslUnlitShader {
                color { textureColor(off.colorTexture) }
                pipeline { cullMethod = CullMethod.NO_CULLING }
            }
        }
    }
}