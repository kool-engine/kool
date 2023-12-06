package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.platform.RenderBackendJvm
import de.fabmax.kool.util.Viewport
import org.lwjgl.glfw.GLFW
import org.lwjgl.glfw.GLFW.glfwSetWindowShouldClose
import org.lwjgl.glfw.GLFW.glfwSwapBuffers
import org.lwjgl.opengl.GL
import org.lwjgl.opengl.GL11.glEnable
import org.lwjgl.opengl.GL20.GL_VERTEX_PROGRAM_POINT_SIZE
import org.lwjgl.opengl.GL32.GL_TEXTURE_CUBE_MAP_SEAMLESS

class RenderBackendGlImpl(ctx: KoolContext) : RenderBackendGl(GlImpl, ctx), RenderBackendJvm {
    override val glfwWindow: GlfwWindow
    override val glslVersion: String

    init {
        glfwWindow = createWindow()

        // This line is critical for LWJGL's interoperation with GLFW's OpenGL context, or any context that is managed
        // externally. LWJGL detects the context that is current in the current thread, creates the GLCapabilities
        // instance and makes the OpenGL bindings available for use.
        GL.createCapabilities()

        GlImpl.initOpenGl(this)
        glslVersion = "#version ${GlImpl.version.major}${GlImpl.version.minor}0 core"

        glEnable(GL_VERTEX_PROGRAM_POINT_SIZE)
        glEnable(GL_TEXTURE_CUBE_MAP_SEAMLESS)
        setupGl()
    }

    override fun getWindowViewport(result: Viewport) {
        result.set(0, 0, glfwWindow.framebufferWidth, glfwWindow.framebufferHeight)
    }

    override fun renderFrame(ctx: KoolContext) {
        super.renderFrame(ctx)
        glfwSwapBuffers(glfwWindow.windowPtr)
    }

    private fun createWindow(): GlfwWindow {
        // do basic GLFW configuration before we create the window
        GLFW.glfwDefaultWindowHints()
        GLFW.glfwWindowHint(GLFW.GLFW_SAMPLES, KoolSystem.configJvm.msaaSamples)

        // create window
        val glfwWindow = GlfwWindow(ctx as Lwjgl3Context)
        glfwWindow.isFullscreen = KoolSystem.configJvm.isFullscreen

        // make the OpenGL context current
        GLFW.glfwMakeContextCurrent(glfwWindow.windowPtr)

        // enable V-sync if configured
        if (KoolSystem.configJvm.isVsync) {
            GLFW.glfwSwapInterval(1)
        } else {
            GLFW.glfwSwapInterval(0)
        }

        // make the window visible
        if (KoolSystem.configJvm.showWindowOnStart) {
            glfwWindow.isVisible = true
        }
        return glfwWindow
    }

    override fun close(ctx: KoolContext) {
        glfwSetWindowShouldClose(glfwWindow.windowPtr, true)
    }

    override fun cleanup(ctx: KoolContext) {
        // for now, we leave the cleanup to the system...
    }
}
