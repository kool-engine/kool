package de.fabmax.kool.pipeline.backend.wgpu

import com.sun.jna.Pointer
import com.sun.jna.platform.win32.Kernel32
import darwin.CAMetalLayer
import darwin.NSWindow
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.pipeline.backend.RenderBackendJvm
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.OsInfo
import ffi.LibraryLoader
import ffi.NativeAddress
import io.ygdrasil.webgpu.NativeSurface
import io.ygdrasil.webgpu.SurfaceConfiguration
import io.ygdrasil.webgpu.WGPU
import io.ygdrasil.webgpu.WGPU.Companion.createInstance
import org.lwjgl.glfw.GLFW.GLFW_CLIENT_API
import org.lwjgl.glfw.GLFW.GLFW_NO_API
import org.lwjgl.glfw.GLFW.glfwCreateWindow
import org.lwjgl.glfw.GLFW.glfwWindowHint
import org.lwjgl.glfw.GLFWNativeCocoa.glfwGetCocoaWindow
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandDisplay
import org.lwjgl.glfw.GLFWNativeWayland.glfwGetWaylandWindow
import org.lwjgl.glfw.GLFWNativeWin32.glfwGetWin32Window
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Display
import org.lwjgl.glfw.GLFWNativeX11.glfwGetX11Window
import org.lwjgl.system.MemoryUtil.NULL
import org.rococoa.ID
import org.rococoa.Rococoa
import java.lang.foreign.MemorySegment

internal suspend fun createWGPURenderBackend(ctx: Lwjgl3Context): DesktopRenderBackendWebGpu {
    LibraryLoader.load()
    // Disable context creation, WGPU will manage that
    glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)

    val glfwWindow = GlfwWindow(ctx)
    glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen

    val wgpu = createInstance() ?: error("fail to wgpu instance")
    val nativeSurface = wgpu.getNativeSurface(glfwWindow)
    val surface = WgpuSurface(nativeSurface, glfwWindow)

    val adapter = wgpu.requestAdapter(nativeSurface)
        ?: error("fail to get adapter")

    return DesktopRenderBackendWebGpu(
        WgpuRenderBackend(
            ctx,
            adapter,
            surface,
            KoolSystem.configJvm.numSamples,
        )
    ).also { it.initContext() }
}

internal class DesktopRenderBackendWebGpu(private val backend: WgpuRenderBackend) :
    RenderBackend by backend, RenderBackendJvm {

    override val glfwWindow: GlfwWindow
        get() = backend.surface.glfwWindow


    internal suspend fun initContext() {
        backend.initContext()

        backend.surface.configure(
            SurfaceConfiguration(
                backend.device,
                backend.surface.format,
                viewFormats = setOf(backend.surface.format)
            )
        )

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