package de.fabmax.kool.pipeline.backend

interface RenderBackendJs : RenderBackend {
    suspend fun startRenderLoop()
}