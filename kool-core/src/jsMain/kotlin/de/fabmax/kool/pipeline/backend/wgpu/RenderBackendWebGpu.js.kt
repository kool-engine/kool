package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.platform.JsContext
import io.ygdrasil.webgpu.navigator
import io.ygdrasil.webgpu.Adapter
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.WGPUAdapter
import io.ygdrasil.webgpu.WGPURequestAdapterOptions
import io.ygdrasil.webgpu.createJsObject
import io.ygdrasil.webgpu.getCanvasSurface
import io.ygdrasil.webgpu.wait
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

internal suspend fun createWGPURenderBackend(ctx: KoolContext, canvas: HTMLCanvasElement): JsRenderBackendWebGpu =
    JsRenderBackendWebGpu(
        WgpuRenderBackend(
            ctx,
            getAdapter(),
            getSurface(canvas),
            KoolSystem.configJs.numSamples,
        )
    )

private fun getSurface(canvas: HTMLCanvasElement): WgpuSurface {
    val canvasSurface = canvas.unsafeCast<io.ygdrasil.webgpu.HTMLCanvasElement>().getCanvasSurface()
    return WgpuSurface(canvasSurface)
}

private suspend fun getAdapter(): Adapter {
    val adapterDescriptor = createJsObject<WGPURequestAdapterOptions>().apply {
        powerPreference = KoolSystem.configJs.powerPreference
    }
    val selectedAdapter: WGPUAdapter? = navigator.gpu?.requestAdapter(adapterDescriptor)?.wait()
            ?: navigator.gpu?.requestAdapter()?.wait()
    checkNotNull(selectedAdapter) { "No appropriate GPUAdapter found." }

    val adapter = Adapter(selectedAdapter)
    return adapter
}

internal class JsRenderBackendWebGpu(private val backend: WgpuRenderBackend) :
    RenderBackend by backend, RenderBackendJs {

    @OptIn(DelicateCoroutinesApi::class)
    override suspend fun startRenderLoop() {
        backend.initContext()

        backend.surface.configure(
            SurfaceConfiguration(
                backend.device,
                backend.surface.format,
                viewFormats = setOf(backend.surface.format)
            )
        )

        window.requestAnimationFrame { t ->
            GlobalScope.launch {
                (backend.ctx as JsContext).renderFrame(t)
            }
        }
    }

    companion object {
        fun isSupported(): Boolean {
            return !js("!navigator.gpu") as Boolean
        }
    }
}