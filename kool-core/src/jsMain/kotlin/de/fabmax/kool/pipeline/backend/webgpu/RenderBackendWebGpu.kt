package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.modules.ksl.KslComputeShader
import de.fabmax.kool.modules.ksl.KslShader
import de.fabmax.kool.pipeline.*
import de.fabmax.kool.pipeline.backend.DepthRange
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.stats.BackendStats
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.scene.Scene
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

    private lateinit var adapter: GPUAdapter
    private lateinit var device: GPUDevice
    private lateinit var gpuContext: GPUCanvasContext

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
        val canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        gpuContext.configure(GPUCanvasConfiguration(device, canvasFormat))
        logI { "WebGPU context created" }

        window.requestAnimationFrame { t -> (ctx as JsContext).renderFrame(t) }
    }

    override fun renderFrame(ctx: KoolContext) {
        BackendStats.resetPerFrameCounts()

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
                doForegroundPass(scene)
            }
        }
    }

    fun doForegroundPass(scene: Scene) {
        val clearColor = scene.mainRenderPass.clearColor ?: return

        val encoder = device.createCommandEncoder()
        val pass = encoder.beginRenderPass(GPURenderPassDescriptor(arrayOf(
            GPURenderPassColorAttachment(
                view = gpuContext.getCurrentTexture().createView(),
                clearValue = GPUColorDict(clearColor.r.toDouble(), clearColor.g.toDouble(), clearColor.b.toDouble(), clearColor.a.toDouble()),
                loadOp = GPULoadOp.clear,
                storeOp = GPUStoreOp.store
            )
        )))

        pass.end()
        device.queue.submit(arrayOf(encoder.finish()))
    }

    override fun cleanup(ctx: KoolContext) {
        // do nothing for now
    }

    override fun generateKslShader(shader: KslShader, pipeline: Pipeline): ShaderCode {
        logE { "Not yet implemented: WebGpuShaderCode()" }
        return WebGpuShaderCode()
    }

    override fun generateKslComputeShader(shader: KslComputeShader, pipeline: ComputePipeline): ComputeShaderCode {
        logE { "Not yet implemented: WebGpuComputeShaderCode()" }
        return WebGpuComputeShaderCode()
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

    class WebGpuShaderCode: ShaderCode {
        override val hash: LongHash = LongHash()
    }

    class WebGpuComputeShaderCode: ComputeShaderCode {
        override val hash: LongHash = LongHash()
    }
}