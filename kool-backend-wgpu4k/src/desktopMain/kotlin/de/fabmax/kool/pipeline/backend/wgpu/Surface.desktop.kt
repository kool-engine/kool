package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.platform.glfw.GlfwWindow
import io.ygdrasil.webgpu.*

actual class WgpuSurface(private val handler: NativeSurface, internal val glfwWindow: GlfwWindow) : AutoCloseable {
    actual val width: UInt get() = glfwWindow.physicalSize.x.toUInt()
    actual val height: UInt get() = glfwWindow.physicalSize.y.toUInt()

    val supportedFormats: Set<GPUTextureFormat> get() = handler.supportedFormats
    actual val supportedAlphaMode: Set<CompositeAlphaMode> get() = handler.supportedAlphaMode
    actual val format: GPUTextureFormat
        get() = supportedFormats.firstOrNull {
            it.name.contains("8Unorm") && !it.name.contains("Srgb")
        } ?: supportedFormats.first()

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