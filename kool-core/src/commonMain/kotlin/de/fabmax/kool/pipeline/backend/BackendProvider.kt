package de.fabmax.kool.pipeline.backend

import de.fabmax.kool.KoolContext

interface BackendProvider {
    val displayName: String

    suspend fun createBackend(ctx: KoolContext): Result<RenderBackend>
}