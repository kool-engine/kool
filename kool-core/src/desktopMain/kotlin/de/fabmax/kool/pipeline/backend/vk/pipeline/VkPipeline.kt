package de.fabmax.kool.pipeline.backend.vk.pipeline

import de.fabmax.kool.pipeline.PipelineBackend
import de.fabmax.kool.pipeline.PipelineBase
import de.fabmax.kool.pipeline.backend.vk.RenderBackendVk
import de.fabmax.kool.util.BaseReleasable

sealed class VkPipeline(
    private val pipeline: PipelineBase,
    protected val backend: RenderBackendVk,
): BaseReleasable(), PipelineBackend {
}