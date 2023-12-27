package de.fabmax.kool.pipeline.backend

interface JsRenderBackend {
    suspend fun startRenderLoop()
}