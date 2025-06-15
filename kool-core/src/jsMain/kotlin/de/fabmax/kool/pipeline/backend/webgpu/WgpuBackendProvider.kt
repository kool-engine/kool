package de.fabmax.kool.pipeline.backend.webgpu

import de.fabmax.kool.KoolContext
import de.fabmax.kool.pipeline.backend.BackendProvider
import de.fabmax.kool.pipeline.backend.RenderBackend
import de.fabmax.kool.platform.JsContext

object WgpuBackendProvider : BackendProvider {
    override val displayName: String = "WebGPU"

    override fun createBackend(ctx: KoolContext): Result<RenderBackend> {
        return if (RenderBackendWebGpu.isSupported()) {
            Result.success(RenderBackendWebGpu(ctx as JsContext))
        } else {
            Result.failure(IllegalStateException("WebGPU is not supported by this browser"))
        }
    }
}