package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.FrameData
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configWasm
import de.fabmax.kool.pipeline.backend.BackendFeatures
import de.fabmax.kool.platform.WasmContext
import de.fabmax.kool.util.Color
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

actual fun createRenderBackendGl(ctx: KoolContext): RenderBackendGl = RenderBackendGlImpl(ctx as WasmContext)

class RenderBackendGlImpl(ctx: WasmContext) :
    RenderBackendGl(KoolSystem.configWasm.numSamples, GlImpl, ctx)
{
    override val name = "WebGL"
    override val deviceName = "WebGL"
    override val features: BackendFeatures

    override val glslGeneratorHints: GlslGenerator.Hints = GlslGenerator.Hints(
        glslVersionStr = "#version 300 es",
    )

    override var frameGpuTime: Duration = 0.0.seconds

    override val isAsyncRendering: Boolean = false

    init {
        val options = WebGlContextOptions(KoolSystem.configWasm.powerPreference.value, numSamples > 1)
        val canvas = ctx.window.canvas
        val webGlCtx = (canvas.getContext("webgl2", options) ?: canvas.getContext("experimental-webgl2", options)) as WebGL2RenderingContext?
        check(webGlCtx != null) { "Unable to initialize WebGL2 context. Your browser may not support it." }
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
        useFloatDepthBuffer = KoolSystem.configWasm.forceFloatDepthBuffer
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }

    override fun renderFrame(frameData: FrameData, ctx: KoolContext) {
        super.renderFrame(frameData, ctx)
        GlImpl.gl.finish()
    }
}

private fun WebGlContextOptions(powerPreference: String, antialias: Boolean): JsAny = js("({ powerPreference: powerPreference, antialias: antialias, stencil: false })")
