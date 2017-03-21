package de.fabmax.kool.platform.lwjgl3

import de.fabmax.kool.InputManager
import de.fabmax.kool.platform.PlatformImpl
import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.platform.use
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL20
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


/**
 * @author fabmax
 */
class Lwjgl3Context(props: InitProps) : RenderContext() {

    val window: Long

    init {
        // configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, props.msaaSamples)

        // create window
        window = glfwCreateWindow(props.width, props.height, props.title, props.monitor, props.share)
        if (window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // center the window
        val primary = PlatformImpl.primaryMonitor
        val wndPosX = primary.posX + (primary.widthPx - props.width) / 2
        val wndPosY = primary.posY + (primary.heightPx - props.height) / 2
        glfwSetWindowPos(window, wndPosX, wndPosY)
        screenDpi = primary.dpi

        // install window callbacks
        glfwSetFramebufferSizeCallback(window) { _, w, h ->
            viewportWidth = w
            viewportHeight = h
        }
        glfwSetWindowPosCallback(window) { _, x, y ->
            screenDpi = PlatformImpl.getResolutionAt(x, y)
        }

        // install mouse callbacks
        glfwSetMouseButtonCallback(window) { _, btn, act, _ ->
            inputMgr.updatePointerButtonState(InputManager.PRIMARY_POINTER, btn, act == GLFW_PRESS)
        }
        glfwSetCursorPosCallback(window) { _, x, y ->
            inputMgr.updatePointerPos(InputManager.PRIMARY_POINTER, x.toFloat(), y.toFloat())
        }
        glfwSetCursorEnterCallback(window) { _, entered ->
            inputMgr.updatePointerValid(InputManager.PRIMARY_POINTER, entered)
        }
        glfwSetScrollCallback(window) { _, _, yOff ->
            inputMgr.updatePointerScrollPos(InputManager.PRIMARY_POINTER, yOff.toFloat())
        }

        // install keyboard callbacks
        glfwSetKeyCallback(window) { _, key, _, action, mods ->
            val event = when (action) {
                GLFW_PRESS -> InputManager.KEV_EV_DOWN
                GLFW_REPEAT -> InputManager.KEV_EV_REPEATED_DOWN
                GLFW_RELEASE -> InputManager.KEV_EV_UP
                else -> -1
            }
            if (event != -1) {
                val keyCode = KEY_CODE_MAP[key] ?: key
                var keyMod = 0
                if (mods and GLFW_MOD_ALT != 0) { keyMod = keyMod or InputManager.KEV_MOD_ALT }
                if (mods and GLFW_MOD_CONTROL != 0) { keyMod = keyMod or InputManager.KEV_MOD_CTRL }
                if (mods and GLFW_MOD_SHIFT != 0) { keyMod = keyMod or InputManager.KEV_MOD_SHIFT }
                if (mods and GLFW_MOD_SUPER != 0) { keyMod = keyMod or InputManager.KEV_MOD_SUPER }
                inputMgr.keyEvent(keyCode, keyMod, event)
            }
        }
        glfwSetCharCallback(window) { _, codepoint ->
            inputMgr.charTyped(codepoint.toChar())
        }

        // get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetFramebufferSize(window, pWidth, pHeight)
            viewportWidth = pWidth[0]
            viewportHeight = pHeight[0]
        } // the stack frame is popped automatically
    }

    override fun run() {
        // make the OpenGL context current
        glfwMakeContextCurrent(window)
        // enable v-sync
        glfwSwapInterval(1)
        // make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        // This is required to be able to set gl_PointSize in vertex shaders (to get same behaviour as in GLES)
        GL11.glEnable(GL20.GL_VERTEX_PROGRAM_POINT_SIZE)

        // run the rendering loop until the user has attempted to close the window
        while (!glfwWindowShouldClose(window)) {
            // render engine content
            render()
            // swap the color buffers
            glfwSwapBuffers(window)
            // Poll for window events. The key callback above will only be invoked during this call.
            glfwPollEvents()
        }
    }

    override fun destroy() {
        // free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // terminate GLFW and free the error callback
        glfwTerminate()
        glfwSetErrorCallback(null).free()
    }

    class InitProps(init: InitProps.() -> Unit = {}) : RenderContext.InitProps() {
        var width = 800
        var height = 600
        var title = "Lwjgl3"
        var monitor = 0L
        var share = 0L

        var msaaSamples = 8

        init {
            init()
        }
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
}

