package de.fabmax.kool.pipeline.backend

interface RenderBackendJs {
    suspend fun startRenderLoop()
}