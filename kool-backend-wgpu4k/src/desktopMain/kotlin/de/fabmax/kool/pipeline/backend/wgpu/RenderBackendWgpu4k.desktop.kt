package de.fabmax.kool.pipeline.backend.wgpu

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import darwin.CAMetalLayer
import darwin.NSWindow
import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.OsInfo
import ffi.LibraryLoader
import ffi.NativeAddress
import io.ygdrasil.webgpu.GPUAdapter
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.WGPU
import io.ygdrasil.webgpu.WGPU.Companion.createInstance
import kotlinx.coroutines.runBlocking
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandDisplay
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandWindow
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Window
import org.rococoa.ID
import org.rococoa.Rococoa
import java.lang.foreign.MemorySegment

internal actual fun isRenderBackendWgpu4kSupported(): Boolean = true

internal actual suspend fun createRenderBackendWgpu4k(ctx: KoolContext): RenderBackendWgpu4k {
    LibraryLoader.load()
    // Disable context creation, WGPU will manage that
    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)

    val glfwWindow = GlfwWindow(ctx as Lwjgl3Context)
    glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen

    // make the window visible
    if (KoolSystem.configJvm.showWindowOnStart) {
        glfwWindow.isVisible = true
    }

    val wgpu = createInstance() ?: error("fail to wgpu instance")
    val nativeSurface = wgpu.getNativeSurface(glfwWindow)
    val surface = WgpuSurface(nativeSurface, glfwWindow)

    val adapter = wgpu.requestAdapter(nativeSurface)
        ?: error("fail to get adapter")

    // Get supported format and opacity on surface
    nativeSurface.computeSurfaceCapabilities(adapter)

    val backend = DesktopRenderBackendWgpu4kWebGpu(
        ctx,
        surface,
        KoolSystem.configJvm.numSamples,
        { adapter }
    )
    backend.initContext()
    return backend
}

internal class DesktopRenderBackendWgpu4kWebGpu(
    ctx: KoolContext,
    surface: WgpuSurface,
    numSamples: Int,
    adapterProvider: suspend () -> GPUAdapter
) : RenderBackendWgpu4k(ctx, surface, numSamples, adapterProvider), RenderBackendJvm {
    override val glfwWindow: GlfwWindow
        get() = surface.glfwWindow

    override fun renderFrame(ctx: KoolContext) {
        runBlocking { renderFrameSuspending(ctx) }
    }
}

private fun WGPU.getNativeSurface(window: GlfwWindow): NativeSurface = when (OsInfo.os) {
    OsInfo.OS.LINUX -> when {
        glfwGetWaylandWindow(window.windowPtr) == 0L -> {
            println("running on X11")
            val display = glfwGetX11Display().toNativeAddress()
            val x11_window = glfwGetX11Window(window.windowPtr).toULong()
            getSurfaceFromX11Window(display, x11_window) ?: error("fail to get surface on Linux")
        }

        else -> {
            println("running on Wayland")
            val display = glfwGetWaylandDisplay().toNativeAddress()
            val wayland_window = glfwGetWaylandWindow(window.windowPtr).toNativeAddress()
            getSurfaceFromWaylandWindow(display, wayland_window)
        }
    }

    OsInfo.OS.WINDOWS -> {
        val hwnd = glfwGetWin32Window(window.windowPtr).toNativeAddress()
        val hinstance = Kernel32.INSTANCE.GetModuleHandle(null).pointer.toNativeAddress()
        getSurfaceFromWindows(hinstance, hwnd) ?: error("fail to get surface on Windows")
    }

    OsInfo.OS.MACOS_X -> {
        val nsWindowPtr = glfwGetCocoaWindow(window.windowPtr)
        val nswindow = Rococoa.wrap(ID.fromLong(nsWindowPtr), NSWindow::class.java)
        nswindow.contentView()?.setWantsLayer(true)
        val layer = CAMetalLayer.layer()
        nswindow.contentView()?.setLayer(layer.id().toLong().toPointer())
        getSurfaceFromMetalLayer(layer.id().toLong().toNativeAddress())
    }

    OsInfo.OS.UNKNOWN -> error("unsupported OS: ${OsInfo.os}")
} ?: error("fail to get surface")

private fun Long.toPointer(): Pointer = Pointer(this)

fun Pointer.toNativeAddress() = let { MemorySegment.ofAddress(Pointer.nativeValue(this)) }
    .let { NativeAddress(it) }

fun Long.toNativeAddress() = let { MemorySegment.ofAddress(it) }
    .let { NativeAddress(it) }