package de.fabmax.kool.platform.glfw

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.input.PlatformInput
import de.fabmax.kool.platform.ClientApi
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.WindowSubsystem
import de.fabmax.kool.util.logD
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.system.MemoryUtil

object GlfwWindowSubsystem : WindowSubsystem {
    private val _monitors: MutableList<MonitorSpec> = mutableListOf()
    val monitors: List<MonitorSpec> get() = _monitors
    var primaryMonitor: MonitorSpec? = null; private set

    var primaryWindow: GlfwWindow? = null
        private set

    override val isCloseRequested: Boolean
        get() = primaryWindow?.let { glfwWindowShouldClose(it.windowHandle) } ?: false

    override val input: PlatformInput
        get() = requireNotNull(primaryWindow?.input)

    override fun queryRequiredVkExtensions(): List<String> {
        val glfwExtensions = checkNotNull(GLFWVulkan.glfwGetRequiredInstanceExtensions()) {
            "glfwGetRequiredInstanceExtensions failed to find the platform surface extensions."
        }
        return buildList {
            for (i in 0 until glfwExtensions.limit()) {
                add(MemoryUtil.memASCII(glfwExtensions[i]))
            }
        }
    }

    override fun createWindow(clientApi: ClientApi, ctx: Lwjgl3Context): GlfwWindow {
        if (clientApi == ClientApi.UNMANAGED) {
            // tell GLFW to not initialize default OpenGL API before we create the window
            check(GLFWVulkan.glfwVulkanSupported()) { "Cannot find a compatible Vulkan installable client driver (ICD)" }
            glfwWindowHint(GLFW_CLIENT_API, GLFW_NO_API)
        } else {
            // do basic GLFW configuration before we create the window
            glfwDefaultWindowHints()
            glfwWindowHint(GLFW_SAMPLES, KoolSystem.configJvm.numSamples)
        }

        val window = GlfwWindow(clientApi, ctx)
        if (primaryWindow == null) {
            primaryWindow = window
        } else {
            error("Multi-window operation is still not implemented")
        }

        if (clientApi == ClientApi.OPEN_GL) {
            glfwMakeContextCurrent(window.windowHandle)
            glfwSwapInterval(if (KoolSystem.configJvm.isVsync) 1 else 0)
        }
        return window
    }

    override fun onEarlyInit() {
        // on macOS, AWT clashes with GLFW, because GLFW also has to run on the first thread enabling AWT
        // headless-mode somewhat mitigates this problem.
        // This has to happen before *any* AWT class (BufferedImage, etc.) is loaded.
        val osName = System.getProperty("os.name", "unknown").lowercase()
        if ("mac os" in osName || "darwin" in osName || "osx" in osName) {
            logD { "Detected macOS. Enabling AWT headless mode to mitigate AWT / GLFW compatibility issues" }
            System.setProperty("java.awt.headless", "true")
        }

        GLFWErrorCallback.createPrint(System.err).set()
        check(glfwInit()) { "Unable to initialize GLFW" }

        val primMonId = glfwGetPrimaryMonitor()
        val mons = glfwGetMonitors()!!
        var primMon = MonitorSpec(mons[0])
        for (i in 0 until mons.limit()) {
            val spec = MonitorSpec(mons[i])
            _monitors += spec
            if (mons[i] == primMonId) {
                primMon = spec
            }
        }
        primaryMonitor = primMon
    }

    override fun onBackendCreated(ctx: Lwjgl3Context) {
        (input as GlfwInput).onContextCreated(ctx)
    }


    fun getMonitorSpecAt(x: Int, y: Int): MonitorSpec {
        var nearestMon: MonitorSpec? = null
        var dist = Double.MAX_VALUE
        for (i in monitors.indices) {
            val d = monitors[i].distance(x, y)
            if (d < dist) {
                dist = d
                nearestMon = monitors[i]
            }
        }
        return nearestMon!!
    }

    fun getResolutionAt(x: Int, y: Int): Float {
        return getMonitorSpecAt(x, y).dpi
    }
}

