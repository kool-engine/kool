package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.modules.ksl.lang.xy
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import de.fabmax.kool.util.Viewport
import org.w3c.dom.HTMLCanvasElement

class RenderBackendGlImpl(ctx: KoolContext, canvas: HTMLCanvasElement) : RenderBackendGl(GlImpl, ctx) {
    override val deviceName = "WebGL"

    init {
        val options = js("""
            {
              antialias: true,
              stencil: false,
            }
        """)
        val webGlCtx = (canvas.getContext("webgl2", options) ?: canvas.getContext("experimental-webgl2", options)) as WebGL2RenderingContext?
        check(webGlCtx != null) {
            val txt = "Unable to initialize WebGL2 context. Your browser may not support it."
            js("alert(txt)")
            txt
        }
        GlImpl.initWebGl(webGlCtx)
        setupGl()
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, ctx.windowWidth, ctx.windowHeight)
    }

    override fun close(ctx: KoolContext) {
        // nothing to do here
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun blitFrameBuffers(src: OffscreenRenderPass2d, dst: RenderPass, mipLevel: Int) {
        if (dst is ScreenRenderPass && numSamples > 1) {
            // on WebGL blitting frame-buffers does not work if target frame-buffer is multi-sampled
            //  -> use a non-multi-sampled texture frame-buffer as conversion helper and then render the texture
            //     using a shader. This means a lot of overhead, but apparently is the only thing we can do.
            val ctx = KoolSystem.requireContext()
            blitTempFrameBuffer.blitRenderPass = src
            blitTempFrameBuffer.setSize(src.width, src.height, ctx)
            blitTempFrameBuffer.impl.draw(ctx)

            blitScene.mainRenderPass.update(ctx)
            blitScene.mainRenderPass.collectDrawCommands(ctx)
            queueRenderer.renderView(blitScene.mainRenderPass.screenView)
        } else {
            super.blitFrameBuffers(src, dst, mipLevel)
        }
    }

    private val blitTempFrameBuffer: OffscreenRenderPass2d by lazy {
        OffscreenRenderPass2d(Node(), renderPassConfig {
            colorTargetTexture(TexFormat.RGBA)
        })
    }

    private val blitScene: Scene by lazy {
        Scene().apply {
            addTextureMesh {
                generate {
                    centeredRect {
                        size.set(2f, 2f)
                    }
                }
                shader = KslUnlitShader {
                    color { textureColor(blitTempFrameBuffer.colorTexture, gamma = 1f) }
                    modelCustomizer = {
                        vertexStage {
                            main {
                                outPosition set float4Value(vertexAttribFloat3(Attribute.POSITIONS).xy, 0f, 1f)
                            }
                        }
                    }
                }
            }
        }
    }
}
