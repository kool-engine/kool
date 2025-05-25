package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.SurfaceTexture

actual class WgpuSurface(private val handler: NativeSurface, actual val width: UInt, actual val height: UInt) : AutoCloseable {

    val supportedFormats: Set<GPUTextureFormat>
        get() = handler.supportedFormats
    actual val supportedAlphaMode: Set<CompositeAlphaMode>
        get() = handler.supportedAlphaMode
    actual val format: GPUTextureFormat
        get() = supportedFormats.first()

    actual fun getCurrentTexture(): SurfaceTexture = handler.getCurrentTexture()

    actual fun present() = handler.present()

    actual fun configure(surfaceConfiguration: SurfaceConfiguration) {
        handler.configure(surfaceConfiguration, width, height)
    }

    actual override fun close() = handler.close()

}