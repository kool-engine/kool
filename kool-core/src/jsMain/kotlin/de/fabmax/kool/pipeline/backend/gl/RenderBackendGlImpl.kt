package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.Color
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class RenderBackendGlImpl(ctx: KoolContext, canvas: HTMLCanvasElement) :
    RenderBackendGl(KoolSystem.configJs.numSamples, GlImpl, ctx), RenderBackendJs
{
    override val deviceName = "WebGL"
    override val features: BackendFeatures

    override val glslGeneratorHints: GlslGenerator.Hints = GlslGenerator.Hints(
        glslVersionStr = "#version 300 es",
    )

    override var frameGpuTime: Duration = 0.0.seconds

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

        features = BackendFeatures(
            computeShaders = false,
            cubeMapArrays = false,
            reversedDepth = GlImpl.capabilities.hasClipControl,
            maxSamples = 4,
            readWriteStorageTextures = false,
            depthOnlyShaderColorOutput = Color.BLACK,
            maxComputeWorkGroupsPerDimension = GlImpl.capabilities.maxWorkGroupCount,
            maxComputeWorkGroupSize = GlImpl.capabilities.maxWorkGroupSize,
            maxComputeInvocationsPerWorkgroup = GlImpl.capabilities.maxWorkGroupInvocations
        )

        sceneRenderer.resolveDirect = false
        useFloatDepthBuffer = KoolSystem.configJs.forceFloatDepthBuffer
    }

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun startRenderLoop() {
        window.requestAnimationFrame { t ->
            GlobalScope.launch {
                (ctx as JsContext).renderFrame(t)
            }
        }
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override suspend fun renderFrame(ctx: KoolContext) {
        super.renderFrame(ctx)
        GlImpl.gl.finish()
    }
}
