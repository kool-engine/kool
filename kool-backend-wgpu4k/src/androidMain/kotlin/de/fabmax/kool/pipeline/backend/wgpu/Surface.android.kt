package de.fabmax.kool.pipeline.backend.wgpu

import io.ygdrasil.webgpu.*

actual class WgpuSurface(private val handler: NativeSurface, actual val width: UInt, actual val height: UInt) : AutoCloseable {

    val supportedFormats: Set<GPUTextureFormat>
        get() = handler.supportedFormats
    actual val supportedAlphaMode: Set<CompositeAlphaMode>
        get() = handler.supportedAlphaMode
    actual val format: GPUTextureFormat
        get() = supportedFormats.first()

    private var currentTexture: SurfaceTexture? = null
    private var currentTextureView: GPUTextureView? = null

    actual fun getCurrentTextureView(): GPUTextureView {
        if (currentTextureView == null) {
            currentTexture = handler.getCurrentTexture()
            currentTextureView = currentTexture?.texture?.createView()
        }

        return currentTextureView!!
    }

    actual fun present() {
        handler.present()
        currentTextureView?.close()
        currentTextureView = null
        currentTexture?.texture?.close()
        currentTexture = null
    }

    actual fun configure(surfaceConfiguration: SurfaceConfiguration) {
        handler.configure(surfaceConfiguration, width, height)
    }

    actual override fun close() = handler.close()

}