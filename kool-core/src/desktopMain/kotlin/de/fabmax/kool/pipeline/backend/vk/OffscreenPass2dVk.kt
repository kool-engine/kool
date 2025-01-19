package de.fabmax.kool.pipeline.backend.vk

import de.fabmax.kool.pipeline.FrameCopy
import de.fabmax.kool.pipeline.OffscreenPass2dImpl
import de.fabmax.kool.pipeline.OffscreenRenderPass2d

class OffscreenPass2dVk(
    val parentPass: OffscreenRenderPass2d,
    numSamples: Int,
    backend: RenderBackendVk
) : RenderPassVk(0, numSamples, backend), OffscreenPass2dImpl {

    override val colorTargetFormats: List<Int> = parentPass.colorTextures.map { it.props.format.vk }

    override fun beginRenderPass(passEncoderState: RenderPassEncoderState, forceLoad: Boolean): VkRenderPass {
        TODO("Not yet implemented")
    }

    override fun copy(
        frameCopy: FrameCopy,
        encoder: RenderPassEncoderState
    ) {
        TODO("Not yet implemented")
    }

    override fun applySize(width: Int, height: Int) {
        TODO("Not yet implemented")
    }
}