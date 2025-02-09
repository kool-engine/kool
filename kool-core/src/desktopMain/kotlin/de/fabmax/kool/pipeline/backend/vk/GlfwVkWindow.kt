package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolSystem
import de.fabmax.kool.configJvm
import de.fabmax.kool.platform.GlfwWindow
import de.fabmax.kool.platform.Lwjgl3Context
import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.vulkan.KHRSurface

class GlfwVkWindow(val backend: RenderBackendVk, ctx: Lwjgl3Context) : GlfwWindow(ctx) {

    val onResize = mutableListOf<OnWindowResizeListener>()

    lateinit var surface: Surface
        private set

    init {
        // make the window visible
        if (KoolSystem.configJvm.showWindowOnStart) {
            isVisible = true
        }
    }

    override fun onFramebufferSizeChanged(width: Int, height: Int) {
        super.onFramebufferSizeChanged(width, height)
        for (listener in onResize) {
            listener.onResize(width, height)
        }
    }

    fun createSurface() {
        surface = Surface()
    }

    fun interface OnWindowResizeListener {
        fun onResize(newWidth: Int, newHeight: Int)
    }

    inner class Surface : BaseReleasable() {
        var surfaceHandle = 0L
            private set

        init {
            memStack {
                val lp = mallocLong(1)
                vkCheck(GLFWVulkan.glfwCreateWindowSurface(backend.instance.vkInstance, windowPtr, null, lp))
                surfaceHandle = lp[0]
            }
            releaseWith(backend.instance)
            logD { "Created surface" }
        }

        override fun release() {
            super.release()
            ReleaseQueue.enqueue {
                KHRSurface.vkDestroySurfaceKHR(backend.instance.vkInstance, surfaceHandle, null)
                logD { "Destroyed surface" }

                glfwDestroyWindow(windowPtr)
                glfwTerminate()
                logD { "Destroyed GLFW window" }
            }
        }
    }
}