package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.modules.ksl.KslUnlitShader
import de.fabmax.kool.pipeline.FullscreenShaderUtil.fullscreenQuadVertexStage
import de.fabmax.kool.pipeline.FullscreenShaderUtil.generateFullscreenQuad
import de.fabmax.kool.pipeline.OffscreenRenderPass2d
import de.fabmax.kool.pipeline.TexFormat
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.renderPassConfig
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Node
import de.fabmax.kool.scene.Scene
import de.fabmax.kool.scene.addTextureMesh
import kotlinx.browser.window
import org.w3c.dom.HTMLCanvasElement

class RenderBackendGlImpl(ctx: KoolContext, canvas: HTMLCanvasElement) :
    RenderBackendGl(KoolSystem.configJs.numSamples, GlImpl, ctx), RenderBackendJs
{
    override val deviceName = "WebGL"

    override val glslGeneratorHints: GlslGenerator.Hints = GlslGenerator.Hints(
        glslVersionStr = "#version 300 es",
    )

    init {
        val options = js("({})")
        options["powerPreference"] = KoolSystem.configJs.powerPreference
        options["antialias"] = numSamples > 1
        options["stencil"] = false

        val webGlCtx = (canvas.getContext("webgl2", options) ?: canvas.getContext("experimental-webgl2", options)) as WebGL2RenderingContext?
        check(webGlCtx != null) {
            val txt = "Unable to initialize WebGL2 context. Your browser may not support it."
            js("alert(txt)")
            txt
        }
        GlImpl.initWebGl(webGlCtx)
        setupGl()

        sceneRenderer.resolveDirect = false
    }

    override suspend fun startRenderLoop() {
        window.requestAnimationFrame { t -> (ctx as JsContext).renderFrame(t) }
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun renderFrame(ctx: KoolContext) {
        super.renderFrame(ctx)
        GlImpl.gl.finish()
    }

    private val blitTempFrameBuffer: OffscreenRenderPass2d by lazy {
        OffscreenRenderPass2d(Node(), renderPassConfig {
            colorTargetTexture(TexFormat.RGBA)
            name = "blitTempFrameBuffer"
        }).apply {
            clearColor = null
            clearDepth = false
        }
    }

    private val blitScene: Scene by lazy {
        Scene().apply {
            addTextureMesh {
                generateFullscreenQuad()
                shader = KslUnlitShader {
                    color { textureColor(blitTempFrameBuffer.colorTexture, gamma = 1f) }
                    modelCustomizer = { fullscreenQuadVertexStage(null) }
                }
            }
        }
    }
}
