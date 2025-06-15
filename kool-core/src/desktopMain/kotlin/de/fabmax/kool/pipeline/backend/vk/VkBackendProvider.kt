package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.platform.Lwjgl3Context

object VkBackendProvider : BackendProvider {
    override val displayName: String = "Vulkan"

    override fun createBackend(ctx: KoolContext): Result<RenderBackend> {
        return try {
            Result.success(RenderBackendVk(ctx as Lwjgl3Context))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}