package de.fabmax.kool.platform.lwjgl3

import de.fabmax.kool.platform.RenderContext
import de.fabmax.kool.platform.use
import org.lwjgl.glfw.Callbacks.glfwFreeCallbacks
import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL
import org.lwjgl.system.MemoryStack
import org.lwjgl.system.MemoryUtil


/**
 * @author fabmax
 */
class Lwjgl3Context(props: InitProps) : RenderContext() {

    val window: Long

    init {
        // Setup an error callback. The default implementation will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set()

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit()) {
            throw IllegalStateException("Unable to initialize GLFW")
        }

        // Configure GLFW
        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE)
        glfwWindowHint(GLFW_SAMPLES, props.msaaSamples)

        // create window
        window = glfwCreateWindow(props.width, props.height, props.title, props.monitor, props.share)
        if (window == MemoryUtil.NULL) {
            throw RuntimeException("Failed to create the GLFW window")
        }

        // Get the resolution of the primary monitor
        val vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor())

        // Center the window
        glfwSetWindowPos(window, (vidmode.width() - props.width) / 2, (vidmode.height() - props.height) / 2)

        glfwSetFramebufferSizeCallback(window) { wnd, w, h ->
            viewportWidth = w
            viewportHeight = h
        }

        // Get the thread stack and push a new frame
        MemoryStack.stackPush().use { stack ->
            val pWidth = stack.mallocInt(1)
            val pHeight = stack.mallocInt(1)
            glfwGetFramebufferSize(window, pWidth, pHeight)
            viewportWidth = pWidth[0]
            viewportHeight = pHeight[0]
        } // the stack frame is popped automatically

    }

    override fun run() {
        // Make the OpenGL context current
        glfwMakeContextCurrent(window)
        // Enable v-sync
        glfwSwapInterval(1)
        // Make the window visible
        glfwShowWindow(window)

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        // Run the rendering loop until the user has attempted to close the window
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
        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window)
        glfwDestroyWindow(window)

        // Terminate GLFW and free the error callback
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
}

