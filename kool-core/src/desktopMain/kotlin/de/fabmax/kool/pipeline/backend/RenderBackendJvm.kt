package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.platform.glfw.GlfwWindow

interface RenderBackendJvm : RenderBackend {
    val window: GlfwWindow
}