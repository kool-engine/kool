package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.platform.GlfwWindow

interface RenderBackendJvm : RenderBackend {
    val glfwWindow: GlfwWindow
}