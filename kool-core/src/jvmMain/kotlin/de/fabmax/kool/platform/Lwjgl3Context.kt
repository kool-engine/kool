package de.fabmax.kool.platform

import de.fabmax.kool.DesktopImpl
import de.fabmax.kool.InputManager
import de.fabmax.kool.KoolContext
import de.fabmax.kool.platform.gl.GlRenderBackend
import de.fabmax.kool.platform.vk.VkRenderBackend
import org.lwjgl.glfw.GLFW.*
import java.awt.Desktop
import java.net.URI
import java.util.*
import java.util.concurrent.CompletableFuture

/**
 * @author fabmax
 */
class Lwjgl3Context(props: InitProps) : KoolContext() {
    override val assetMgr = JvmAssetManager(props, this)

    internal val renderBackend: RenderBackend

    override val shaderGenerator
        get() = renderBackend.shaderGenerator

    override val windowWidth: Int
        get() = renderBackend.windowWidth
    override val windowHeight: Int
        get() = renderBackend.windowHeight
    override var viewport: Viewport
        get() = renderBackend.windowViewport
        set(_) {}

    private val mainThreadRunnables = mutableListOf<GpuThreadRunnable>()

    private object SysInfo : ArrayList<String>() {
        fun set(api: String, dev: String) {
            clear()
            add(System.getProperty("java.vm.name") + " " + System.getProperty("java.version"))
            add(api)
            add(dev)
            add("")
            update()
        }

        fun update() {
            val rt = Runtime.getRuntime()
            val freeMem = rt.freeMemory()
            val totalMem = rt.totalMemory()
            set(3, "Heap: ${(totalMem - freeMem) / 1024 / 1024} / ${totalMem / 1024 / 1024} MB")
        }
    }

    init {
        renderBackend = if (props.renderBackend == RenderBackendMode.VULKAN) {
            VkRenderBackend(props, this)
        } else {
            GlRenderBackend(props, this)
        }

        projCorrectionMatrix.set(renderBackend.projCorrectionMatrix)
        depthBiasMatrix.set(renderBackend.depthBiasMatrix)

        SysInfo.set(renderBackend.apiName, renderBackend.deviceName)

        setupInput(renderBackend.glfwWindowHandle)

        screenDpi = DesktopImpl.primaryMonitor.dpi
    }

    override fun openUrl(url: String)  = Desktop.getDesktop().browse(URI(url))

    override fun run() {
        var prevTime = System.nanoTime()
        while (!glfwWindowShouldClose(renderBackend.glfwWindowHandle)) {
            SysInfo.update()
            glfwPollEvents()

            synchronized(mainThreadRunnables) {
                if (mainThreadRunnables.isNotEmpty()) {
                    for (r in mainThreadRunnables) {
                        r.r()
                        r.future.complete(null)
                    }
                    mainThreadRunnables.clear()
                }
            }

            // determine time delta
            val time = System.nanoTime()
            val dt = (time - prevTime) / 1e9
            prevTime = time

            // setup draw queues for all scenes / render passes
            render(dt)

            // execute draw queues
            engineStats.resetPerFrameCounts()
            renderBackend.drawFrame(this)
        }
        destroy()
    }

    override fun destroy() {
        renderBackend.destroy(this)
    }

    override fun getSysInfos(): List<String> = SysInfo

    // fixme: use Coroutine stuff instead
    fun runOnMainThread(action: () -> Unit): CompletableFuture<Void> {
        synchronized(mainThreadRunnables) {
            val r = GpuThreadRunnable(action)
            mainThreadRunnables += r
            return r.future
        }
    }

    private fun setupInput(window: Long) {
        // install window callbacks
        glfwSetWindowPosCallback(window) { _, x, y ->
            screenDpi = getResolutionAt(x, y)
        }

        // install mouse callbacks
        glfwSetMouseButtonCallback(window) { _, btn, act, _ ->
            inputMgr.handleMouseButtonState(btn, act == GLFW_PRESS)
        }
        glfwSetCursorPosCallback(window) { _, x, y ->
            inputMgr.handleMouseMove(x.toFloat(), y.toFloat())
        }
        glfwSetCursorEnterCallback(window) { _, entered ->
            if (!entered) {
                inputMgr.handleMouseExit()
            }
        }
        glfwSetScrollCallback(window) { _, _, yOff ->
            inputMgr.handleMouseScroll(yOff.toFloat())
        }

        // install keyboard callbacks
        glfwSetKeyCallback(window) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> InputManager.KEY_EV_DOWN
                GLFW_REPEAT -> InputManager.KEY_EV_DOWN or InputManager.KEY_EV_REPEATED
                GLFW_RELEASE -> InputManager.KEY_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = KEY_CODE_MAP[key] ?: key
                var keyMod = 0
                if (mods and GLFW_MOD_ALT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_ALT
                }
                if (mods and GLFW_MOD_CONTROL != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_CTRL
                }
                if (mods and GLFW_MOD_SHIFT != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SHIFT
                }
                if (mods and GLFW_MOD_SUPER != 0) {
                    keyMod = keyMod or InputManager.KEY_MOD_SUPER
                }
                inputMgr.keyEvent(keyCode, keyMod, event)
            }
        }
        glfwSetCharCallback(window) { _, codepoint ->
            inputMgr.charTyped(codepoint.toChar())
        }
    }

    private class GpuThreadRunnable(val r: () -> Unit) {
        val future = CompletableFuture<Void>()
    }

    companion object {
        val KEY_CODE_MAP: Map<Int, Int> = mutableMapOf(
                GLFW_KEY_LEFT_CONTROL to InputManager.KEY_CTRL_LEFT,
                GLFW_KEY_RIGHT_CONTROL to InputManager.KEY_CTRL_RIGHT,
                GLFW_KEY_LEFT_SHIFT to InputManager.KEY_SHIFT_LEFT,
                GLFW_KEY_RIGHT_SHIFT to InputManager.KEY_SHIFT_RIGHT,
                GLFW_KEY_LEFT_ALT to InputManager.KEY_ALT_LEFT,
                GLFW_KEY_RIGHT_ALT to InputManager.KEY_ALT_RIGHT,
                GLFW_KEY_LEFT_SUPER to InputManager.KEY_SUPER_LEFT,
                GLFW_KEY_RIGHT_SUPER to InputManager.KEY_SUPER_RIGHT,
                GLFW_KEY_ESCAPE to InputManager.KEY_ESC,
                GLFW_KEY_MENU to InputManager.KEY_MENU,
                GLFW_KEY_ENTER to InputManager.KEY_ENTER,
                GLFW_KEY_KP_ENTER to InputManager.KEY_NP_ENTER,
                GLFW_KEY_KP_DIVIDE to InputManager.KEY_NP_DIV,
                GLFW_KEY_KP_MULTIPLY to InputManager.KEY_NP_MUL,
                GLFW_KEY_KP_ADD to InputManager.KEY_NP_PLUS,
                GLFW_KEY_KP_SUBTRACT to InputManager.KEY_NP_MINUS,
                GLFW_KEY_BACKSPACE to InputManager.KEY_BACKSPACE,
                GLFW_KEY_TAB to InputManager.KEY_TAB,
                GLFW_KEY_DELETE to InputManager.KEY_DEL,
                GLFW_KEY_INSERT to InputManager.KEY_INSERT,
                GLFW_KEY_HOME to InputManager.KEY_HOME,
                GLFW_KEY_END to InputManager.KEY_END,
                GLFW_KEY_PAGE_UP to InputManager.KEY_PAGE_UP,
                GLFW_KEY_PAGE_DOWN to InputManager.KEY_PAGE_DOWN,
                GLFW_KEY_LEFT to InputManager.KEY_CURSOR_LEFT,
                GLFW_KEY_RIGHT to InputManager.KEY_CURSOR_RIGHT,
                GLFW_KEY_UP to InputManager.KEY_CURSOR_UP,
                GLFW_KEY_DOWN to InputManager.KEY_CURSOR_DOWN,
                GLFW_KEY_F1 to InputManager.KEY_F1,
                GLFW_KEY_F2 to InputManager.KEY_F2,
                GLFW_KEY_F3 to InputManager.KEY_F3,
                GLFW_KEY_F4 to InputManager.KEY_F4,
                GLFW_KEY_F5 to InputManager.KEY_F5,
                GLFW_KEY_F6 to InputManager.KEY_F6,
                GLFW_KEY_F7 to InputManager.KEY_F7,
                GLFW_KEY_F8 to InputManager.KEY_F8,
                GLFW_KEY_F9 to InputManager.KEY_F9,
                GLFW_KEY_F10 to InputManager.KEY_F10,
                GLFW_KEY_F11 to InputManager.KEY_F11,
                GLFW_KEY_F12 to InputManager.KEY_F12
        )
    }

    class InitProps(init: InitProps.() -> Unit = {}) {
        var width = 1200
        var height = 800
        var title = "Kool"
        var monitor = 0L
        var share = 0L

        var renderBackend = RenderBackendMode.OPEN_GL

        var msaaSamples = 8

        var assetsBaseDir = "./assets"

        val extraFonts = mutableListOf<String>()

        init {
            init()
        }
    }

    enum class RenderBackendMode {
        VULKAN,
        OPEN_GL
    }
}

