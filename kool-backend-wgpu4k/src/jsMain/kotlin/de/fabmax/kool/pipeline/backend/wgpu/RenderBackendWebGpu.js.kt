package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.pipeline.backend.RenderBackendJs
import de.fabmax.kool.platform.JsContext
import io.ygdrasil.webgpu.*
import kotlinx.browser.window
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

internal fun createWGPURenderBackend(ctx: JsContext): JsRenderBackendWebGpu =
    JsRenderBackendWebGpu(ctx, ctx.canvas)

private fun getSurface(canvas: HTMLCanvasElement): WgpuSurface {
    val canvasSurface = canvas.unsafeCast<io.ygdrasil.webgpu.HTMLCanvasElement>().getCanvasSurface()
    return WgpuSurface(canvasSurface)
}

private suspend fun getAdapter(): Adapter {
    val adapterDescriptor = createJsObject<WGPURequestAdapterOptions>().apply {
        powerPreference = KoolSystem.configJs.powerPreference.value
    }
    val selectedAdapter: WGPUAdapter? = navigator.gpu?.requestAdapter(adapterDescriptor)?.wait()
            ?: navigator.gpu?.requestAdapter()?.wait()
    checkNotNull(selectedAdapter) { "No appropriate GPUAdapter found." }

    val adapter = Adapter(selectedAdapter)
    return adapter
}

@OptIn(DelicateCoroutinesApi::class)
internal class JsRenderBackendWebGpu(ctx: KoolContext, canvas: HTMLCanvasElement) :
    WgpuRenderBackend(
        ctx,
        getSurface(canvas),
        KoolSystem.configJs.numSamples,
        { getAdapter() }
    ),
    RenderBackendJs
{
    override suspend fun startRenderLoop() {
        initContext()
        surface.configure(
            SurfaceConfiguration(
                device,
                surface.format,
                viewFormats = setOf(surface.format)
            )
        )

        window.requestAnimationFrame { t -> (ctx as JsContext).renderFrame(t) }
    }

    override fun renderFrame(ctx: KoolContext) {
        GlobalScope.launch {
            renderFrameSuspending(ctx)
        }
    }

    companion object {
        fun isSupported(): Boolean {
            return !js("!navigator.gpu") as Boolean
        }
    }
}