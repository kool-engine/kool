package de.fabmax.kool.pipeline.backend.wgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.platform.Lwjgl3Context

object Wgpu4kBackendProvider : BackendProvider {
    override val displayName: String = "wgpu4k"

    override fun createBackend(ctx: KoolContext): Result<RenderBackend> {
        return Result.success(createWGPURenderBackend(ctx as Lwjgl3Context))
    }
}