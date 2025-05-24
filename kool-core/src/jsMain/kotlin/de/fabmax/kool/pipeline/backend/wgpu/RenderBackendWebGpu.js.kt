package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.math.Vec2i
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.pipeline.backend.webgpu.GPUCanvasConfiguration
import de.fabmax.kool.pipeline.backend.webgpu.GPUCanvasContext
import de.fabmax.kool.pipeline.backend.webgpu.GPUDevice
import de.fabmax.kool.pipeline.backend.webgpu.GPURequestAdapterOptions
import de.fabmax.kool.platform.JsContext
import de.fabmax.kool.platform.navigator
import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.WGPUAdapter
import io.ygdrasil.webgpu.getCanvasSurface
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

actual suspend fun createWebGpuRenderBackend(ctx: KoolContext): RenderBackendWebGpu {
    TODO("Not yet implemented")
}

internal suspend fun createWGPURenderBackend(ctx: KoolContext, canvas: HTMLCanvasElement): JsRenderBackendWebGpu =
    JsRenderBackendWebGpu(
        RenderBackendWebGpu(
            ctx,
            getAdapter(),
            getSurface(canvas),
            KoolSystem.configJs.numSamples,
        ),
        canvas
    )

private fun getSurface(canvas: HTMLCanvasElement): WgpuSurface {
    val canvasSurface = canvas.unsafeCast<io.ygdrasil.webgpu.HTMLCanvasElement>().getCanvasSurface()
    return WgpuSurface(canvasSurface)
}

private suspend fun getAdapter(): Adapter {
    val selectedAdapter =
        navigator.gpu.requestAdapter(GPURequestAdapterOptions(powerPreference = KoolSystem.configJs.powerPreference))
            .await()
            ?: navigator.gpu.requestAdapter().await()
    checkNotNull(selectedAdapter) { "No appropriate GPUAdapter found." }

    val adapter = Adapter(selectedAdapter.unsafeCast<WGPUAdapter>())
    return adapter
}

internal class JsRenderBackendWebGpu(private val backend: RenderBackendWebGpu, val canvas: HTMLCanvasElement) :
    RenderBackend by backend, RenderBackendJs {

    lateinit var canvasContext: GPUCanvasContext
        private set

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun startRenderLoop() {
        backend.startRenderLoop()
        canvasContext = canvas.getContext("webgpu") as GPUCanvasContext
        val canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        canvasContext.configure(
            GPUCanvasConfiguration((backend.device as Device).handler.unsafeCast<GPUDevice>(), canvasFormat)
        )

        window.requestAnimationFrame { t ->
            GlobalScope.launch {
                (backend.ctx as JsContext).renderFrame(t)
            }
        }
    }

    override suspend fun renderFrame(ctx: KoolContext) {
        backend.renderFrame(ctx)
    }
}