package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.navigator
import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.Device
import io.ygdrasil.webgpu.GPUCanvasToneMappingMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.HTMLCanvasElement
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.SurfaceTexture
import io.ygdrasil.webgpu.SurfaceTextureStatus
import io.ygdrasil.webgpu.Texture
import io.ygdrasil.webgpu.WGPUCanvasConfiguration
import io.ygdrasil.webgpu.WGPUCanvasContext
import io.ygdrasil.webgpu.WGPUCanvasToneMapping
import io.ygdrasil.webgpu.asJsNumber
import io.ygdrasil.webgpu.asJsString
import io.ygdrasil.webgpu.asUInt
import io.ygdrasil.webgpu.castAs
import io.ygdrasil.webgpu.createJsObject
import io.ygdrasil.webgpu.mapJsArray
import io.ygdrasil.webgpu.toFlagInt

actual class WgpuSurface(private val handler: WGPUCanvasContext) : AutoCloseable {
    actual val width: UInt
        get() = handler.canvas.castAs<HTMLCanvasElement>().width.asUInt()
    actual val height: UInt
        get() = handler.canvas.castAs<HTMLCanvasElement>().height.asUInt()

    actual val supportedAlphaMode: Set<CompositeAlphaMode> =
        setOf(CompositeAlphaMode.Opaque, CompositeAlphaMode.Premultiplied)

    actual val format: GPUTextureFormat
        get() = navigator.gpu?.getPreferredCanvasFormat()
            ?.let { GPUTextureFormat.of(it) ?: error("Unsupported surface format: $it") }
            ?: error("WebGPU not supported")

    actual fun getCurrentTexture(): SurfaceTexture {
        return handler.getCurrentTexture()
            .let { Texture(it, canBeDestroy = false)}
            .let { SurfaceTexture(it, SurfaceTextureStatus.success) }
    }

    actual fun present() { /* does not exists on Web */ }

    actual fun configure(surfaceConfiguration: SurfaceConfiguration) {
        handler.configure(map(surfaceConfiguration))
    }

    actual override fun close() { /* does not exists on Web */ }
}

private fun map(input: SurfaceConfiguration) = createJsObject<WGPUCanvasConfiguration>().apply {
    device = (input.device as Device).handler
    format = input.format.value
    usage = input.usage.toFlagInt().asJsNumber()
    viewFormats = input.viewFormats.mapJsArray { it.value.asJsString().castAs() }
    colorSpace = input.colorSpace.value.asJsString().castAs()
    toneMapping = createJsObject<WGPUCanvasToneMapping>().apply {
        // GPUCanvasToneMappingMode.Standard is the default value on specification, should we allow to use extends for HDR ?
        mode = GPUCanvasToneMappingMode.Standard.value.asJsString().castAs()
    }
    alphaMode = input.alphaMode.value.asJsString().castAs()
}