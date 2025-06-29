package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJs
import de.fabmax.kool.platform.JsContext
import io.ygdrasil.webgpu.*
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement

internal actual fun isRenderBackendWgpu4kSupported(): Boolean = !js("!navigator.gpu") as Boolean

internal actual suspend fun createRenderBackendWgpu4k(ctx: KoolContext): RenderBackendWgpu4k {
    ctx as JsContext
    val backend = JsRenderBackendWgpu4kWebGpu(ctx, ctx.canvas)
    backend.initContext()
    with(backend) {
        surface.configure(
            SurfaceConfiguration(
                device,
                surface.format,
                viewFormats = setOf(surface.format)
            )
        )
    }
    return backend
}

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
internal class JsRenderBackendWgpu4kWebGpu(ctx: KoolContext, canvas: HTMLCanvasElement) :
    RenderBackendWgpu4k(
        ctx,
        getSurface(canvas),
        KoolSystem.configJs.numSamples,
        { getAdapter() }
    )
{
    override fun renderFrame(ctx: KoolContext) {
        GlobalScope.launch {
            renderFrameSuspending(ctx)
        }
    }
}