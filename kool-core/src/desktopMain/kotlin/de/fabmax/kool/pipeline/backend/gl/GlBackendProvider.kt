package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.BackendProvider

object GlBackendProvider : BackendProvider {
    override val displayName: String = "OpenGL"

    override fun createBackend(ctx: KoolContext): Result<RenderBackendGlImpl> {
        return Result.success(RenderBackendGlImpl(ctx))
    }
}