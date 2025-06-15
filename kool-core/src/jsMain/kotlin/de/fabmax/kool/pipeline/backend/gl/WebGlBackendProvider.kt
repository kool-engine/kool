package de.fabmax.kool.pipeline.backend.gl

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.platform.JsContext

object WebGlBackendProvider : BackendProvider {
    override val displayName: String = "WebGL 2"

    override fun createBackend(ctx: KoolContext): Result<RenderBackendGlImpl> {
        return Result.success(RenderBackendGlImpl(ctx as JsContext))
    }
}