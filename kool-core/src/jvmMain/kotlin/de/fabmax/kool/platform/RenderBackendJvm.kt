package de.fabmax.kool.platform

import de.fabmax.kool.pipeline.backend.RenderBackend

interface RenderBackendJvm : RenderBackend {
    val glfwWindow: GlfwWindow
}