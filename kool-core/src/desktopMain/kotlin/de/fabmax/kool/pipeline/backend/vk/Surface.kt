package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.util.BaseReleasable
import de.fabmax.kool.util.logD

class Surface(val backend: RenderBackendVk) : BaseReleasable() {
    val surfaceHandle = backend.window.createVulkanSurface(backend.instance.vkInstance)

    init {
        logD { "Created surface" }
    }

    override fun doRelease() {
        ReleaseQueue.enqueue {
            backend.window.destroyVulkanSurface(surfaceHandle, backend.instance.vkInstance)
        }
    }
}