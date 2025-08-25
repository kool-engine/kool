package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD
import de.fabmax.kool.util.memStack
import de.fabmax.kool.util.releaseWith
import org.lwjgl.glfw.GLFW.glfwDestroyWindow
import org.lwjgl.glfw.GLFW.glfwTerminate
import org.lwjgl.glfw.GLFWVulkan
import org.lwjgl.vulkan.KHRSurface

class Surface(val backend: RenderBackendVk) : BaseReleasable() {
    var surfaceHandle = 0L
        private set

    init {
        memStack {
            val lp = mallocLong(1)
            vkCheck(GLFWVulkan.glfwCreateWindowSurface(backend.instance.vkInstance, backend.glfwWindow.windowHandle, null, lp))
            surfaceHandle = lp[0]
        }
        releaseWith(backend.instance)
        logD { "Created surface" }
    }

    override fun doRelease() {
        ReleaseQueue.enqueue {
            KHRSurface.vkDestroySurfaceKHR(backend.instance.vkInstance, surfaceHandle, null)
            logD { "Destroyed surface" }

            glfwDestroyWindow(backend.glfwWindow.windowHandle)
            glfwTerminate()
            logD { "Destroyed GLFW window" }
        }
    }
}