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
import io.ygdrasil.webgpu.WGPUDevice
import kotlinx.browser.window
import kotlinx.coroutines.await
import org.w3c.dom.HTMLCanvasElement

actual suspend fun createWebGpuRenderBackend(ctx: KoolContext): RenderBackendWebGpu {
    TODO("Not yet implemented")
}

internal suspend fun createWGPURenderBackend(ctx: KoolContext, canvas: HTMLCanvasElement): JsRenderBackendWebGpu =
    JsRenderBackendWebGpu(
        RenderBackendWebGpu(
            ctx,
            getAdapter(),
            Vec2i(canvas.width, canvas.height)
        ),
        canvas
    )

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

    override suspend fun startRenderLoop() {
        backend.startRenderLoop()
        canvasContext = canvas.getContext("webgpu") as GPUCanvasContext
        val canvasFormat = navigator.gpu.getPreferredCanvasFormat()
        canvasContext.configure(
            GPUCanvasConfiguration((backend.device as Device).handler.unsafeCast<GPUDevice>(), canvasFormat)
        )

        window.requestAnimationFrame { t ->
            (backend.ctx as JsContext).renderFrame(t)
        }
    }

    override suspend fun renderFrame(ctx: KoolContext) {
        if (canvas.width != backend.renderSize.x || canvas.height != backend.renderSize.y) {
            backend.renderSize = Vec2i(canvas.width, canvas.height)
            backend.screenPass.applySize(canvas.width, canvas.height)
        }
        backend.renderFrame(ctx)
    }
}