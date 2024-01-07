package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.util.LongHash
import de.fabmax.kool.util.logE
import de.fabmax.kool.util.logI
import de.fabmax.kool.util.logT
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.HTMLCanvasElement

class RenderBackendWebGpu(val ctx: KoolContext, val canvas: HTMLCanvasElement) : RenderBackend, RenderBackendJs {
    override val name: String = "WebGPU Backend"
    override val apiName: String = "WebGPU"
    override val deviceName: String = "WebGPU"
    override val depthRange: DepthRange = DepthRange.ZERO_TO_ONE
    override val canBlitRenderPasses: Boolean = false
    override val isOnscreenInfiniteDepthCapable: Boolean = false // actually it can...

    lateinit var adapter: GPUAdapter
        private set
    lateinit var device: GPUDevice
        private set
    lateinit var gpuContext: GPUCanvasContext
        private set
    private var _canvasFormat: GPUTextureFormat? = null
    val canvasFormat: GPUTextureFormat
        get() = _canvasFormat!!

    private val sceneRenderer = WgpuRenderPass(this)

    private var renderSize = Vec2i(canvas.width, canvas.height)

    init {
        check(!js("!navigator.gpu") as Boolean) {
            val txt = "WebGPU not supported on this browser."
            js("alert(txt)")
            txt
        }
    }

    override suspend fun startRenderLoop() {
        adapter = checkNotNull(navigator.gpu.requestAdapter().await()) {
            val txt = "No appropriate GPUAdapter found."
            js("alert(txt)")
            txt
        }

        device = adapter.requestDevice().await()

        gpuContext = canvas.getContext("webgpu") as GPUCanvasContext
        _canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        gpuContext.configure(GPUCanvasConfiguration(device, canvasFormat))
        logI { "WebGPU context created" }

        window.requestAnimationFrame { t -> (ctx as JsContext).renderFrame(t) }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

        if (canvas.width != renderSize.x || canvas.height != renderSize.y) {
            renderSize = Vec2i(canvas.width, canvas.height)
            sceneRenderer.createRenderAttachments()
        }

//        if (ctx.disposablePipelines.isNotEmpty()) {
//            ctx.disposablePipelines.forEach {
//                shaderMgr.deleteShader(it)
//            }
//            ctx.disposablePipelines.clear()
//        }

//        doOffscreenPasses(ctx.backgroundScene, ctx)

        for (i in ctx.scenes.indices) {
            val scene = ctx.scenes[i]
            if (scene.isVisible) {
//                if (scene.framebufferCaptureMode == Scene.FramebufferCaptureMode.BeforeRender) {
//                    captureFramebuffer(scene)
//                }
//                doOffscreenPasses(scene, ctx)
                sceneRenderer.doForegroundPass(scene)
            }
        }
    }

    override fun cleanup(ctx: KoolContext) {
        // do nothing for now
    }

    override fun generateKslShader(shader: KslShader, pipeline: Pipeline): ShaderCode {
        val output = WgslGenerator().generateProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            output.dump()
        }
        return WebGpuShaderCode(
            vertexSrc = output.vertexSrc,
            vertexEntryPoint = output.vertexEntryPoint,
            fragmentSrc = output.fragmentSrc,
            fragmentEntryPoint = output.fragmentEntryPoint
        )
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        val output = WgslGenerator().generateComputeProgram(shader.program, pipeline)
        if (shader.program.dumpCode) {
            output.dump()
        }
        return WebGpuComputeShaderCode(
            computeSrc = output.computeSrc,
            computeEntryPoint = output.computeEntryPoint
        )
    }

    override fun createOffscreenPass2d(parentPass: OffscreenRenderPass2d): OffscreenPass2dImpl {
        return WebGpuOffscreenPass2d(parentPass)
    }

    override fun createOffscreenPassCube(parentPass: OffscreenRenderPassCube): OffscreenPassCubeImpl {
        return WebGpuOffscreenPassCube(parentPass)
    }

    override fun uploadTextureToGpu(tex: Texture, data: TextureData) {
        logE { "Not yet implemented: uploadTextureToGpu()" }
    }

    class WebGpuOffscreenPass2d(val parentPass: OffscreenRenderPass2d) : OffscreenPass2dImpl {
        override val isReverseDepth: Boolean = false

        override fun applySize(width: Int, height: Int) { }

        override fun release() { }

        override fun draw(ctx: KoolContext) {
            logT { "Draw 2d: ${parentPass.name}" }
        }
    }

    class WebGpuOffscreenPassCube(val parentPass: OffscreenRenderPassCube) : OffscreenPassCubeImpl {
        override val isReverseDepth: Boolean = false

        override fun applySize(width: Int, height: Int) { }

        override fun release() { }

        override fun draw(ctx: KoolContext) {
            logT { "Draw cube: ${parentPass.name}" }
        }
    }

    data class WebGpuShaderCode(
        val vertexSrc: String,
        val vertexEntryPoint: String,
        val fragmentSrc: String,
        val fragmentEntryPoint: String
    ): ShaderCode {
        override val hash = LongHash().apply {
            this += vertexSrc.hashCode().toLong() shl 32 or fragmentSrc.hashCode().toLong()
        }
    }

    data class WebGpuComputeShaderCode(val computeSrc: String, val computeEntryPoint: String): ComputeShaderCode {
        override val hash = LongHash().apply {
            this += computeSrc
        }
    }
}