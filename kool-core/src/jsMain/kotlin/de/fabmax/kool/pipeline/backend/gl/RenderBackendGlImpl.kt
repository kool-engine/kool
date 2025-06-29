package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl = RenderBackendGlImpl(ctx as JsContext)

class RenderBackendGlImpl(ctx: JsContext) :
    RenderBackendGl(KoolSystem.configJs.numSamples, GlImpl, ctx)
{
    override val name = "WebGL"
    override val deviceName = "WebGL"
    override val features: BackendFeatures

    override val glslGeneratorHints: GlslGenerator.Hints = GlslGenerator.Hints(
        glslVersionStr = "#version 300 es",
    )

    override var frameGpuTime: Duration = 0.0.seconds

    init {
        val options = js("({})")
        options["powerPreference"] = KoolSystem.configJs.powerPreference.value
        options["antialias"] = numSamples > 1
        options["stencil"] = false

        val webGlCtx = (ctx.canvas.getContext("webgl2", options) ?: ctx.canvas.getContext("experimental-webgl2", options)) as WebGL2RenderingContext?
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

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun renderFrame(ctx: KoolContext) {
        super.renderFrame(ctx)
        GlImpl.gl.finish()
    }
}
