package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.platform.GlfwWindow
import io.ygdrasil.webgpu.CompositeAlphaMode
import io.ygdrasil.webgpu.GPUTextureFormat
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.SurfaceTexture
import org.lwjgl.glfw.GLFW.glfwGetWindowSize

actual class WgpuSurface(private val handler: NativeSurface, internal val glfwWindow: GlfwWindow) : AutoCloseable {
    actual val width: UInt
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetWindowSize(glfwWindow.windowPtr, width, height)
            return width[0].toUInt()
        }
    actual val height: UInt
        get() {
            val width = IntArray(1)
            val height = IntArray(1)
            glfwGetWindowSize(glfwWindow.windowPtr, width, height)
            return height[0].toUInt()
        }

    val supportedFormats: Set<GPUTextureFormat>
        get() = handler.supportedFormats
    actual val supportedAlphaMode: Set<CompositeAlphaMode>
        get() = handler.supportedAlphaMode
    actual val format: GPUTextureFormat
        get() = supportedFormats.firstOrNull { it.name.contains("8Unorm") && !it.name.contains("Srgb") } ?: supportedFormats.first()

    actual fun getCurrentTexture(): SurfaceTexture = handler.getCurrentTexture()

    actual fun present() = handler.present()

    actual fun configure(surfaceConfiguration: SurfaceConfiguration) {
        handler.configure(surfaceConfiguration, width, height)
    }

    actual override fun close() = handler.close()

}